package com.my.fomo.websocket.application;

import com.my.fomo.websocket.application.dto.StockRealtimeData;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockBroadcastService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcast(StockRealtimeData data) {
        messagingTemplate.convertAndSend("/topic/stock/" + data.getSymbol(), data);
    }
}
