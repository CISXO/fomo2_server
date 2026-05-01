package com.my.fomo.chart.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyChartRequest {

    private String auth = "";
    private String symb;
    private String gubn = "0";
    private String excd;
    private String bymd = "";
    private String modp = "";
}
