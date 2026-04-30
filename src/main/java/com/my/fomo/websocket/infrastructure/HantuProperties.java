package com.my.fomo.websocket.infrastructure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "hantu")
public class HantuProperties {
    private String appKey;
    private String appSecret;
    private String baseUrl;
    private String wsUrl = "ws://ops.koreainvestment.com:31000";
}
