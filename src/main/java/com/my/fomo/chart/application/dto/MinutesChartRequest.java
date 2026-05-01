package com.my.fomo.chart.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MinutesChartRequest {

    private String auth = "";
    private String symb;
    private String gubn = "0";
    private String excd;
    private String nmin = "5";
    private String pinc = "1";
    private String next = "";
    private String nrec = "100";
    private String fill = "";
    private String keyb = "";
}
