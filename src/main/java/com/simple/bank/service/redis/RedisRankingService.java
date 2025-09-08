package com.simple.bank.service.redis;

import com.simple.bank.api.request.RankingScoreIncreaseRequest;
import com.simple.bank.api.request.RankingScoreUpdateRequest;
import com.simple.bank.dto.RankingDTO;

import java.util.List;
import java.util.Map;

public interface RedisRankingService {
    RankingDTO addOrUpdateScore(RankingScoreUpdateRequest rankingScoreUpdateRequest);
    RankingDTO incrementScore(RankingScoreIncreaseRequest rankingScoreIncreaseRequest);
    List<Map<String, Object>> getTopRank(int topN);
    List<Map<String, Object>> getRankAroundUser(String clientId, int around);
    Long getTotalClients();
    void clearRanking();
    RankingDTO getClientScore(String clientId);
    RankingDTO getClientRank(String clientId);
}
