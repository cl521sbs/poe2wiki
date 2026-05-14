package com.poe2wiki.controller;

import com.poe2wiki.common.ApiResult;
import com.poe2wiki.common.PageResult;
import com.poe2wiki.entity.Guide;
import com.poe2wiki.service.GuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guides")
@RequiredArgsConstructor
public class GuideController {

    private final GuideService guideService;

    @GetMapping
    public ApiResult<PageResult<Guide>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        return ApiResult.success(guideService.list(page, size, category, status, keyword));
    }

    @GetMapping("/{id}")
    public ApiResult<Guide> getById(@PathVariable Long id) {
        return ApiResult.success(guideService.getById(id));
    }

    @PostMapping
    public ApiResult<Guide> create(@RequestBody Guide guide) {
        return ApiResult.success(guideService.create(guide));
    }

    @PutMapping("/{id}")
    public ApiResult<Guide> update(@PathVariable Long id, @RequestBody Guide guide) {
        guide.setId(id);
        return ApiResult.success(guideService.update(guide));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        guideService.delete(id);
        return ApiResult.success();
    }
}
