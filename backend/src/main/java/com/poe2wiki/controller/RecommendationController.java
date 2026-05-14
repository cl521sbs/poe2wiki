package com.poe2wiki.controller;

import com.poe2wiki.common.ApiResult;
import com.poe2wiki.common.PageResult;
import com.poe2wiki.entity.BuildRecommendation;
import com.poe2wiki.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService service;

    @GetMapping
    public ApiResult<PageResult<BuildRecommendation>> list(
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String stage,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResult.success(service.list(className, stage, page, size));
    }

    @GetMapping("/{id}")
    public ApiResult<BuildRecommendation> getById(@PathVariable Long id) {
        return ApiResult.success(service.getById(id));
    }
}
