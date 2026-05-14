package com.poe2wiki.controller;

import com.poe2wiki.common.ApiResult;
import com.poe2wiki.dto.DpsRequest;
import com.poe2wiki.dto.DpsResponse;
import com.poe2wiki.service.CalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/calculator")
@RequiredArgsConstructor
public class CalculatorController {

    private final CalculatorService calculatorService;

    @PostMapping("/dps")
    public ApiResult<DpsResponse> calculateDps(@RequestBody DpsRequest req) {
        return ApiResult.success(calculatorService.calculate(req));
    }

    @PostMapping("/compare")
    public ApiResult<Map<String, DpsResponse>> compareDps(@RequestBody Map<String, DpsRequest> body) {
        return ApiResult.success(calculatorService.compare(
                body.get("buildA"), body.get("buildB")));
    }
}
