package com.my.fomo.chart.presentation;

import com.my.fomo.chart.application.ChartService;
import com.my.fomo.chart.application.KisTokenService;
import com.my.fomo.chart.application.dto.DailyChartRequest;
import com.my.fomo.chart.application.dto.MinutesChartRequest;
import com.my.fomo.global.response.ApiResponse;
import com.my.fomo.websocket.infrastructure.HantuApprovalKeyClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chart")
@RequiredArgsConstructor
public class ChartController {

    private final ChartService chartService;
    private final KisTokenService kisTokenService;
    private final HantuApprovalKeyClient approvalKeyClient;

    /**
     * GET /api/chart/minutes
     * 해외주식 분봉 차트 (한투 HHDFS76950200)
     * 필수: SYMB / 선택: EXCD, NMIN(기본5), PINC(기본1), NREC(기본100), KEYB(다음 페이지 커서)
     */
    @GetMapping("/minutes")
    public ResponseEntity<Map<String, Object>> getMinutesChart(
            @RequestParam(name = "AUTH", defaultValue = "") String auth,
            @RequestParam(name = "SYMB") String symb,
            @RequestParam(name = "GUBN", defaultValue = "0") String gubn,
            @RequestParam(name = "EXCD", required = false, defaultValue = "") String excd,
            @RequestParam(name = "NMIN", defaultValue = "5") String nmin,
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

    /**
     * GET /api/chart/daily
     * 해외주식 일봉 차트 (한투 HHDFS76240000)
     * 필수: SYMB / 선택: EXCD, BYMD(기준일자 YYYYMMDD), MODP
     */
    @GetMapping("/daily")
    public ResponseEntity<Map<String, Object>> getDailyChart(
            @RequestParam(name = "AUTH", defaultValue = "") String auth,
            @RequestParam(name = "SYMB") String symb,
            @RequestParam(name = "GUBN", defaultValue = "0") String gubn,
            @RequestParam(name = "EXCD", required = false, defaultValue = "") String excd,
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

    /**
     * GET /api/chart/token
     * KIS REST API access_token 갱신 (기간계 토큰)
     */
    @GetMapping("/token")
    public ResponseEntity<ApiResponse<String>> refreshToken() {
        String token = kisTokenService.refreshAndGetToken();
        return ResponseEntity.ok(ApiResponse.ok("KIS 토큰 갱신 완료", token));
    }

    /**
     * GET /api/chart/realtime-token
     * KIS WebSocket approval_key 발급 (실시간 토큰)
     */
    @GetMapping("/realtime-token")
    public ResponseEntity<ApiResponse<String>> getRealtimeToken() {
        String approvalKey = approvalKeyClient.fetchApprovalKey();
        return ResponseEntity.ok(ApiResponse.ok(approvalKey));
    }
}
