package com.my.fomo.websocket.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class PurchaseMessage implements StockRealtimeData {

    private final String type = "purchase";
    private final String symbol;
    private final String localDate;
    private final String localTime;
    private final String koreaDate;
    private final String koreaTime;
    private final String openPrice;
    private final String highPrice;
    private final String lowPrice;
    private final String currentPrice;
    private final String changeType;
    private final String changeAmount;
    private final String changeRate;
    private final String bidPrice;
    private final String askPrice;
    private final String tradeQty;
    private final String volume;
    private final String tradeStrength;
}
