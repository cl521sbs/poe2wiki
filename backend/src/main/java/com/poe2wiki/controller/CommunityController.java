package com.poe2wiki.controller;

import com.poe2wiki.common.ApiResult;
import com.poe2wiki.entity.Comment;
import com.poe2wiki.entity.Favorite;
import com.poe2wiki.service.CommentService;
import com.poe2wiki.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommentService commentService;
    private final FavoriteService favoriteService;

    // ==================== Comments ====================

    @GetMapping("/comments")
    public ApiResult<List<Comment>> listComments(@RequestParam Long guideId) {
        return ApiResult.success(commentService.listByGuideId(guideId));
    }

    @PostMapping("/comments")
    public ApiResult<Comment> createComment(@RequestBody Comment comment) {
        return ApiResult.success(commentService.create(comment));
    }

    @DeleteMapping("/comments/{id}")
    public ApiResult<Void> deleteComment(@PathVariable Long id) {
        commentService.delete(id);
        return ApiResult.success();
    }

    // ==================== Favorites ====================

    @PostMapping("/favorites/{guideId}")
    public ApiResult<Void> addFavorite(@PathVariable Long guideId, @RequestParam Long userId) {
        favoriteService.add(userId, guideId);
        return ApiResult.success();
    }

    @DeleteMapping("/favorites/{guideId}")
    public ApiResult<Void> removeFavorite(@PathVariable Long guideId, @RequestParam Long userId) {
        favoriteService.remove(userId, guideId);
        return ApiResult.success();
    }

    @GetMapping("/my-favorites")
    public ApiResult<List<Favorite>> myFavorites(@RequestParam Long userId) {
        return ApiResult.success(favoriteService.listByUser(userId));
    }
}
