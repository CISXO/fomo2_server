package com.my.fomo.chart.infrastructure;

import com.my.fomo.chart.application.KisTokenService;
import com.my.fomo.chart.application.dto.DailyChartRequest;
import com.my.fomo.chart.application.dto.MinutesChartRequest;
import com.my.fomo.websocket.infrastructure.HantuProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KisChartClient {

    private final HantuProperties properties;
    private final KisTokenService tokenService;

    @SuppressWarnings("unchecked")
    public Map<String, Object> fetchMinutesChart(MinutesChartRequest req) {
        String accessToken = tokenService.getAccessToken();

        URI uri = UriComponentsBuilder
                .fromUriString(properties.getBaseUrl() + "/uapi/overseas-price/v1/quotations/inquire-time-itemchartprice")
                .queryParam("AUTH", req.getAuth())
                .queryParam("EXCD", req.getExcd())
                .queryParam("SYMB", req.getSymb())
                .queryParam("GUBN", req.getGubn())
                .queryParam("NMIN", req.getNmin())
                .queryParam("PINC", req.getPinc())
                .queryParam("NEXT", req.getNext())
                .queryParam("NREC", req.getNrec())
                .queryParam("FILL", req.getFill())
                .queryParam("KEYB", req.getKeyb())
                .build().encode().toUri();

        return RestClient.create().get()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .header("content-type", "application/json; charset=utf-8")
                .header("appKey", properties.getAppKey())
                .header("appSecret", properties.getAppSecret())
                .header("tr_id", "HHDFS76950200")
                .header("custtype", "P")
                .exchange((req2, resp) -> resp.bodyTo(Map.class));
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> fetchDailyChart(DailyChartRequest req) {
        String accessToken = tokenService.getAccessToken();

        URI uri = UriComponentsBuilder
                .fromUriString(properties.getBaseUrl() + "/uapi/overseas-price/v1/quotations/dailyprice")
                .queryParam("AUTH", req.getAuth())
                .queryParam("EXCD", req.getExcd())
                .queryParam("SYMB", req.getSymb())
                .queryParam("GUBN", req.getGubn())
                .queryParam("BYMD", req.getBymd())
                .queryParam("MODP", req.getModp())
                .build().encode().toUri();

        return RestClient.create().get()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .header("content-type", "application/json")
                .header("appKey", properties.getAppKey())
                .header("appSecret", properties.getAppSecret())
                .header("tr_id", "HHDFS76240000")
                .exchange((req2, resp) -> resp.bodyTo(Map.class));
    }
}
