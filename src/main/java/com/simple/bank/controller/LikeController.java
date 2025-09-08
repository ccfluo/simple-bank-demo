package com.simple.bank.controller;

import com.simple.bank.api.response.LikeStatusResponse;
import com.simple.bank.api.response.ListOfLikeRankResponse;
import com.simple.bank.api.response.ListOfLikedUserResponse;
import com.simple.bank.dto.LikeRankDTO;
import com.simple.bank.dto.Response;
import com.simple.bank.service.redis.LikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    // 构造器注入LikeService
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    /**
     * like
     *
     * @param contentId
     * @param userId
     * @return like result
     */
    @PostMapping("/{contentId}/like")
    public ResponseEntity<Response> like(
            @PathVariable String contentId,
            @RequestParam String userId) {

        boolean success = likeService.like(contentId, userId);
        if (success) {
            return ResponseEntity.ok(new Response("Like Successful"));
        } else {
            return ResponseEntity.badRequest().body(new Response("You've already liked this"));
        }
    }

    /**
     * cancel like
     *
     * @param contentId
     * @param userId
     * @return cancel like result
     */
    @PostMapping("/{contentId}/cancel")
    public ResponseEntity<Response> cancelLike(
            @PathVariable String contentId,
            @RequestParam String userId) {

        boolean success = likeService.cancelLike(contentId, userId);
        if (success) {
            return ResponseEntity.ok(new Response("Unlike Successful"));
        } else {
            return ResponseEntity.badRequest().body(new Response("Not liked yet, cannot cancel"));
        }
    }

    /**
     * get total like for a given contendId
     */
    @GetMapping("/count/{contentId}")
    public ResponseEntity<Map<String, Object>> getLikeCount(
            @PathVariable String contentId) {

        long count = likeService.getLikeCount(contentId);
        return ResponseEntity.ok(Map.of(
                "contentId", contentId,
                "likeCount", count,
                "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * check if user liked a content
     */
    @GetMapping("/status")
    public ResponseEntity<LikeStatusResponse> checkLikeStatus(
            @RequestParam String contentId,
            @RequestParam String userId) {

        boolean isLiked = likeService.isLiked(contentId, userId);
        return ResponseEntity.ok(new LikeStatusResponse(contentId, userId, isLiked, isLiked ? "User liked" : "User unlike"));
    }

    /**
     * get user list for a content
     * TODO: LIKE_USER_SET_PREFIX is designed as SET, which won't support pagination.
     * need migrate to zset to support pagination
     */

    @GetMapping("/users/{contentId}")
    public ResponseEntity<ListOfLikedUserResponse> getLikedUsers(@PathVariable String contentId) {
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {

        ListOfLikedUserResponse listOfLikedUserResponse = likeService.getLikedUsers(contentId);
        return ResponseEntity.ok(listOfLikedUserResponse);

    }

    /**
     * get like rank for different contents
     */
    @GetMapping("/rank")
    public ResponseEntity<ListOfLikeRankResponse> getLikeRanking(
            @RequestParam(defaultValue = "10") int topN) {
            List<LikeRankDTO> rankList = likeService.getLikeRank(topN);
        return ResponseEntity.ok(new ListOfLikeRankResponse(topN, rankList));
    }
}
