package com.my.fomo.websocket.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HantuApprovalKeyClient {

    private final HantuProperties properties;

    @SuppressWarnings("unchecked")
    public String fetchApprovalKey() {
        RestClient restClient = RestClient.create();
        Map<String, String> body = Map.of(
                "grant_type", "client_credentials",
                "appkey", properties.getAppKey(),
                "secretkey", properties.getAppSecret()
        );
        Map<String, String> response = restClient.post()
                .uri(properties.getBaseUrl() + "/oauth2/Approval")
                .header("content-type", "application/json")
                .body(body)
                .retrieve()
                .body(Map.class);

        if (response == null || !response.containsKey("approval_key")) {
            throw new IllegalStateException("Failed to obtain approval_key from Korea Investment API");
        }
        log.info("Approval key obtained successfully");
        return response.get("approval_key");
    }
}
