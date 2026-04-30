package com.my.fomo.websocket.presentation;

import com.my.fomo.stock.domain.StockRepository;
import com.my.fomo.websocket.domain.SubscriptionRegistry;
import com.my.fomo.websocket.infrastructure.HantuWebSocketClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockSubscribeEventListener {

    private static final String STOCK_TOPIC_PREFIX = "/topic/stock/";

    private final SubscriptionRegistry registry;
    private final HantuWebSocketClient hantuClient;
    private final StockRepository stockRepository;

    @EventListener
    public void onSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headers.getDestination();
        if (destination == null || !destination.startsWith(STOCK_TOPIC_PREFIX)) return;

        String symbol = destination.substring(STOCK_TOPIC_PREFIX.length()).toUpperCase();
        String sessionId = headers.getSessionId();
        String subscriptionId = headers.getSubscriptionId();

        String excd = stockRepository.findBySymbol(symbol)
                .map(s -> s.getExcd())
                .orElse("NAS");

        boolean isFirst = registry.subscribe(sessionId, subscriptionId, symbol, excd);
        if (isFirst) {
            hantuClient.subscribe(symbol, excd);
            log.info("First subscriber for {} — subscribed on Korea Investment", symbol);
        }
    }

    @EventListener
    public void onUnsubscribe(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();
        String subscriptionId = headers.getSubscriptionId();

        registry.unsubscribe(sessionId, subscriptionId).ifPresent(symbol -> {
            if (!registry.hasSubscribers(symbol)) {
                registry.getExcd(symbol).ifPresent(excd -> hantuClient.unsubscribe(symbol, excd));
                log.info("No more subscribers for {} — unsubscribed from Korea Investment", symbol);
            }
        });
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        Set<String> orphaned = registry.disconnect(event.getSessionId());
        orphaned.forEach(symbol -> {
            String excd = stockRepository.findBySymbol(symbol)
                    .map(s -> s.getExcd())
                    .orElse("NAS");
            hantuClient.unsubscribe(symbol, excd);
            log.info("Session disconnected — unsubscribed {} from Korea Investment", symbol);
        });
    }
}
