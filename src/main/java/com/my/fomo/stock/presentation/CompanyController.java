package com.my.fomo.stock.presentation;

import com.my.fomo.global.response.ApiResponse;
import com.my.fomo.stock.application.CompanyService;
import com.my.fomo.stock.application.dto.CompanyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> getCompanies(
            @RequestParam(required = false) String search) {
        List<CompanyResponse> companies = StringUtils.hasText(search)
                ? companyService.searchCompanies(search)
                : companyService.getCompanies();
        return ResponseEntity.ok(ApiResponse.ok(companies));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> searchCompanies(
            @RequestParam(required = false) String search) {
        List<CompanyResponse> companies = StringUtils.hasText(search)
                ? companyService.searchCompanies(search)
                : companyService.getCompanies();
        return ResponseEntity.ok(ApiResponse.ok(companies));
    }
}
