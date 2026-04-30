package com.my.fomo.websocket.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class HokaMessage implements StockRealtimeData {

    private final String type = "hoka";
    private final String symbol;
    private final String localDate;
    private final String localTime;
    private final String koreaDate;
    private final String koreaTime;
    private final String totalBidQty;
    private final String totalAskQty;
    private final String bidPrice;
    private final String askPrice;
    private final String bidQty;
    private final String askQty;
}
