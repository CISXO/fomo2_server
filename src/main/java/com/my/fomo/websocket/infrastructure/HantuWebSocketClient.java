package com.my.fomo.websocket.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.fomo.websocket.application.StockBroadcastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class HantuWebSocketClient extends TextWebSocketHandler {

    private final HantuProperties properties;
    private final HantuApprovalKeyClient approvalKeyClient;
    private final HantuMessageParser messageParser;
    private final StockBroadcastService broadcastService;

    private volatile WebSocketSession session;
    private volatile String approvalKey;

    // symbol → excd: currently desired subscriptions (re-sent on reconnect)
    private final ConcurrentHashMap<String, String> activeSubscriptions = new ConcurrentHashMap<>();

    private final AtomicInteger retryCount = new AtomicInteger(0);
    private static final int MAX_RETRY = 20;
    private static final long MAX_DELAY_MS = 60_000L;

    private static final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "hantu-ws-reconnect");
                t.setDaemon(true);
                return t;
            });

    private final ObjectMapper objectMapper = new ObjectMapper();

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        scheduler.submit(this::connect);
    }

    private void connect() {
        int attempt = retryCount.get();
        if (attempt >= MAX_RETRY) {
            log.error("Korea Investment WebSocket: max retry exceeded");
            return;
        }
        try {
            approvalKey = approvalKeyClient.fetchApprovalKey();
            new StandardWebSocketClient()
                    .execute(this, properties.getWsUrl())
                    .whenComplete((s, ex) -> {
                        if (ex != null) {
                            log.error("WebSocket connection failed: {}", ex.getMessage());
                            scheduleReconnect();
                        }
                    });
        } catch (Exception e) {
            log.error("Korea Investment WebSocket connect error: {}", e.getMessage());
            scheduleReconnect();
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        this.session = session;
        retryCount.set(0);
        log.info("Connected to Korea Investment WebSocket");
        activeSubscriptions.forEach((symbol, excd) -> sendMessage(symbol, excd, true));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        if ("PINGPONG".equals(payload)) {
            try {
                session.sendMessage(new TextMessage("PINGPONG"));
            } catch (IOException e) {
                log.warn("Failed to send PINGPONG: {}", e.getMessage());
            }
            return;
        }
        messageParser.parse(payload).ifPresent(broadcastService::broadcast);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        this.session = null;
        log.warn("Korea Investment WebSocket closed: {}", status);
        if (!CloseStatus.NORMAL.equalsCode(status)) {
            scheduleReconnect();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable ex) {
        log.error("Korea Investment WebSocket transport error: {}", ex.getMessage());
        this.session = null;
        scheduleReconnect();
    }

    public void subscribe(String symbol, String excd) {
        activeSubscriptions.put(symbol, excd);
        sendMessage(symbol, excd, true);
    }

    public void unsubscribe(String symbol, String excd) {
        activeSubscriptions.remove(symbol);
        sendMessage(symbol, excd, false);
    }

    private void sendMessage(String symbol, String excd, boolean subscribe) {
        WebSocketSession s = this.session;
        if (s == null || !s.isOpen() || approvalKey == null) return;
        try {
            Map<String, Object> msg = Map.of(
                    "header", Map.of(
                            "approval_key", approvalKey,
                            "custtype", "P",
                            "tr_type", subscribe ? "1" : "2",
                            "content-type", "utf-8"
                    ),
                    "body", Map.of(
                            "input", Map.of(
                                    "tr_id", "HDFSASP0",
                                    "tr_key", toTrKey(symbol, excd)
                            )
                    )
            );
            s.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
            log.debug("{} {} ({})", subscribe ? "Subscribed" : "Unsubscribed", symbol, excd);
        } catch (IOException e) {
            log.error("Failed to send subscription message for {}: {}", symbol, e.getMessage());
        }
    }

    private String toTrKey(String symbol, String excd) {
        String prefix = switch (excd.toUpperCase()) {
            case "NAS" -> "DNAS";
            case "NYS" -> "DNYS";
            case "AMS" -> "DAMS";
            default -> "DNAS";
        };
        return prefix + symbol;
    }

    private void scheduleReconnect() {
        int attempt = retryCount.incrementAndGet();
        long delay = Math.min(2000L * (1L << Math.min(attempt, 10)), MAX_DELAY_MS);
        log.info("Reconnecting in {}s (attempt {})", delay / 1000, attempt);
        scheduler.schedule(this::connect, delay, TimeUnit.MILLISECONDS);
    }
}
