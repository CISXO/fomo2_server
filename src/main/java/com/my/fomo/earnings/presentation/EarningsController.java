package com.my.fomo.earnings.presentation;

import com.my.fomo.chart.application.ChartService;
import com.my.fomo.chart.application.dto.DailyChartRequest;
import com.my.fomo.chart.application.dto.MinutesChartRequest;
import com.my.fomo.earnings.application.EarningsService;
import com.my.fomo.earnings.application.dto.EarningsResponse;
import com.my.fomo.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/earnings")
@RequiredArgsConstructor
public class EarningsController {

    private final ChartService chartService;
    private final EarningsService earningsService;

    @GetMapping("/hantu")
    public ResponseEntity<Map<String, Object>> getDailyChart(
            @RequestParam(name = "AUTH", defaultValue = "") String auth,
            @RequestParam(name = "SYMB") String symb,
            @RequestParam(name = "GUBN", defaultValue = "0") String gubn,
            @RequestParam(name = "EXCD", defaultValue = "") String excd,
            @RequestParam(name = "BYMD", defaultValue = "") String bymd,
            @RequestParam(name = "MODP", defaultValue = "") String modp) {

        DailyChartRequest req = new DailyChartRequest();
        req.setAuth(auth);
        req.setSymb(symb);
        req.setGubn(gubn);
        req.setExcd(excd);
        req.setBymd(bymd);
        req.setModp(modp);

        return ResponseEntity.ok(chartService.getDailyChart(req));
    }

    @GetMapping("/hantu/minutesChart")
    public ResponseEntity<Map<String, Object>> getMinutesChart(
            @RequestParam(name = "AUTH", defaultValue = "") String auth,
            @RequestParam(name = "SYMB") String symb,
            @RequestParam(name = "GUBN", defaultValue = "0") String gubn,
            @RequestParam(name = "EXCD", defaultValue = "") String excd,
            @RequestParam(name = "NMIN", defaultValue = "1") String nmin,
            @RequestParam(name = "PINC", defaultValue = "1") String pinc,
            @RequestParam(name = "NEXT", defaultValue = "") String next,
            @RequestParam(name = "NREC", defaultValue = "100") String nrec,
            @RequestParam(name = "FILL", defaultValue = "") String fill,
            @RequestParam(name = "KEYB", defaultValue = "") String keyb) {

        MinutesChartRequest req = new MinutesChartRequest();
        req.setAuth(auth);
        req.setSymb(symb);
        req.setGubn(gubn);
        req.setExcd(excd);
        req.setNmin(nmin);
        req.setPinc(pinc);
        req.setNext(next);
        req.setNrec(nrec);
        req.setFill(fill);
        req.setKeyb(keyb);

        return ResponseEntity.ok(chartService.getMinutesChart(req));
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<ApiResponse<EarningsResponse>> getEarningsBySymbol(
            @PathVariable String symbol) {
        return ResponseEntity.ok(ApiResponse.ok(earningsService.getEarningsBySymbol(symbol)));
    }
}
