package com.my.fomo.chart.infrastructure;

import com.my.fomo.websocket.infrastructure.HantuProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KisTokenClient {

    private final HantuProperties properties;

    @SuppressWarnings("unchecked")
    public Map<String, Object> fetchAccessToken() {
        Map<String, String> body = Map.of(
                "grant_type", "client_credentials",
                "appkey", properties.getAppKey(),
                "appsecret", properties.getAppSecret()
        );
        Map<String, Object> response = RestClient.create().post()
                .uri(properties.getBaseUrl() + "/oauth2/tokenP")
                .header("Content-Type", "application/json")
                .body(body)
                .retrieve()
                .body(Map.class);

        if (response == null || !response.containsKey("access_token")) {
            throw new IllegalStateException("KIS access_token 발급 실패");
        }
        log.info("KIS period access token 발급 완료");
        return response;
    }
}
