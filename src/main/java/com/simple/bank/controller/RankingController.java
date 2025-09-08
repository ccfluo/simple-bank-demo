package com.simple.bank.controller;


import com.simple.bank.api.request.RankingScoreIncreaseRequest;
import com.simple.bank.api.request.RankingScoreUpdateRequest;
import com.simple.bank.api.response.ListOfRankingResponse;
import com.simple.bank.api.response.RankingResponse;
import com.simple.bank.api.response.RankingTotalResponse;
import com.simple.bank.dto.RankingDTO;
import com.simple.bank.dto.Response;
import com.simple.bank.service.redis.RedisRankingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final RedisRankingService rankingService;
    public RankingController(RedisRankingService rankingService) {
        this.rankingService = rankingService;
    }

    /**
     * 添加或更新用户分数
     */
    @PostMapping("/score")
    public ResponseEntity<RankingResponse> updateScore(@RequestBody RankingScoreUpdateRequest rankingScoreUpdateRequest) {
        RankingDTO rankingDTO = rankingService.addOrUpdateScore(rankingScoreUpdateRequest);
        return ResponseEntity.ok(new RankingResponse(rankingDTO));
    }

    /**
     * 增加用户分数
     */
    @PostMapping("/score/increment")
    public ResponseEntity<RankingResponse> incrementScore(@RequestBody RankingScoreIncreaseRequest rankingScoreIncreaseRequest) {
        RankingDTO rankingDTO = rankingService.incrementScore(rankingScoreIncreaseRequest);
        return ResponseEntity.ok(new RankingResponse(rankingDTO));
    }

    /**
     * 获取用户分数
     */
    @GetMapping("/score/{clientId}")
    public ResponseEntity<RankingResponse> getUserScore(@PathVariable String clientId) {
        RankingDTO rankingDTO = rankingService.getClientScore(clientId);
        return ResponseEntity.ok(new RankingResponse(rankingDTO));
    }

    /**
     * 获取用户排名
     */
    @GetMapping("/rank/{clientId}")
    public ResponseEntity<RankingResponse> getUserRank(@PathVariable String clientId) {
        RankingDTO rankingDTO = rankingService.getClientRank(clientId);
        return ResponseEntity.ok(new RankingResponse(rankingDTO));
    }

    /**
     * 获取排行榜前N名
     */
    @GetMapping("/top/{topN}")
    public ResponseEntity<ListOfRankingResponse> getTopRank(@PathVariable int topN) {
        List<Map<String, Object>> listOfRanking = rankingService.getTopRank(topN);
        return ResponseEntity.ok(new ListOfRankingResponse(listOfRanking));
    }

    /**
     * 获取用户附近的排名
     */
    @GetMapping("/around/{clientId}")
    public ResponseEntity<ListOfRankingResponse> getRankAroundUser(
            @PathVariable String clientId,
            @RequestParam(defaultValue = "5") int around) {
        List<Map<String, Object>> listOfRanking = rankingService.getRankAroundUser(clientId, around);
        return ResponseEntity.ok(new ListOfRankingResponse(listOfRanking));
    }

    /**
     * 获取总人数
     */
    @GetMapping("/total")
    public ResponseEntity<RankingTotalResponse> getTotalUsers() {
        Long total = rankingService.getTotalClients();
        return ResponseEntity.ok(new RankingTotalResponse(total));
    }

    /**
     * 清空排行榜
     */
    @DeleteMapping
    public ResponseEntity<Response> clearRanking() {
        rankingService.clearRanking();
        return ResponseEntity.ok(new Response("Ranking is successfully cleaned up"));
    }
}
