package com.my.fomo.websocket.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.fomo.websocket.application.dto.HokaMessage;
import com.my.fomo.websocket.application.dto.PurchaseMessage;
import com.my.fomo.websocket.application.dto.StockRealtimeData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class HantuMessageParser {

    private static final String HOKA_TR_ID = "HDFSASP0";
    private static final String PURCHASE_TR_ID = "HDFSCNT0";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Optional<StockRealtimeData> parse(String message) {
        try {
            if (message.startsWith("{") || message.startsWith("[")) {
                return parseJson(message);
            } else if (message.contains("|") && message.contains("^")) {
                return parsePipe(message);
            }
        } catch (Exception e) {
            log.warn("Failed to parse message: {}", e.getMessage());
        }
        return Optional.empty();
    }

    private Optional<StockRealtimeData> parseJson(String message) throws Exception {
        JsonNode root = objectMapper.readTree(message);
        String trId = root.path("header").path("tr_id").asText();
        String output = root.path("body").path("output").asText();
        if (output.isBlank()) return Optional.empty();
        return parseData(trId, output);
    }

    private Optional<StockRealtimeData> parsePipe(String message) {
        // format: status|tr_id|count|data
        String[] parts = message.split("\\|", 4);
        if (parts.length < 4) return Optional.empty();
        return parseData(parts[1], parts[3]);
    }

    private Optional<StockRealtimeData> parseData(String trId, String data) {
        String[] fields = data.split("\\^");
        return switch (trId) {
            case HOKA_TR_ID -> parseHoka(fields);
            case PURCHASE_TR_ID -> parsePurchase(fields);
            default -> Optional.empty();
        };
    }

    // recv[1] = 종목코드, recv[3]=현지일자, recv[4]=현지시간, recv[5]=한국일자, recv[6]=한국시간
    // recv[7]=매수총잔량, recv[8]=매도총잔량, recv[11]=매수호가, recv[12]=매도호가
    // recv[13]=매수잔량, recv[14]=매도잔량
    private Optional<StockRealtimeData> parseHoka(String[] f) {
        if (f.length < 17) return Optional.empty();
        return Optional.of(HokaMessage.builder()
                .symbol(f[1])
                .localDate(f[3])
                .localTime(f[4])
                .koreaDate(f[5])
                .koreaTime(f[6])
                .totalBidQty(f[7])
                .totalAskQty(f[8])
                .bidPrice(f[11])
                .askPrice(f[12])
                .bidQty(f[13])
                .askQty(f[14])
                .build());
    }

    // f[1]=종목코드, f[4]=현지일자, f[5]=현지시간, f[6]=한국일자, f[7]=한국시간
    // f[8]=시가, f[9]=고가, f[10]=저가, f[11]=현재가, f[12]=대비구분
    // f[13]=전일대비, f[14]=등락율, f[15]=매수호가, f[16]=매도호가
    // f[19]=체결량, f[20]=거래량, f[24]=체결강도
    private Optional<StockRealtimeData> parsePurchase(String[] f) {
        if (f.length < 26) return Optional.empty();
        return Optional.of(PurchaseMessage.builder()
                .symbol(f[1])
                .localDate(f[4])
                .localTime(f[5])
                .koreaDate(f[6])
                .koreaTime(f[7])
                .openPrice(f[8])
                .highPrice(f[9])
                .lowPrice(f[10])
                .currentPrice(f[11])
                .changeType(f[12])
                .changeAmount(f[13])
                .changeRate(f[14])
                .bidPrice(f[15])
                .askPrice(f[16])
                .tradeQty(f[19])
                .volume(f[20])
                .tradeStrength(f[24])
                .build());
    }
}
