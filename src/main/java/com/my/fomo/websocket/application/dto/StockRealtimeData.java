package com.my.fomo.websocket.application.dto;

public sealed interface StockRealtimeData permits HokaMessage, PurchaseMessage {
    String getSymbol();
    String getType();
}
