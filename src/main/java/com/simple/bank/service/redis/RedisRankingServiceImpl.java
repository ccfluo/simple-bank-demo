package com.simple.bank.service.redis;

import com.simple.bank.api.request.RankingScoreIncreaseRequest;
import com.simple.bank.api.request.RankingScoreUpdateRequest;
import com.simple.bank.dto.RankingDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@DependsOn("redisTemplate")
public class RedisRankingServiceImpl implements RedisRankingService {
    private static final String RANKING_KEY = "purchase_ranking";

//    @Autowired
//    private RedisTemplate redisTemplate;
    private final RedisTemplate redisTemplate;

    public RedisRankingServiceImpl(@Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 添加或更新用户分数
     * @return 该用户的排名
     */
    public RankingDTO addOrUpdateScore(RankingScoreUpdateRequest rankingScoreUpdateRequest) {
        // 添加或更新分数，zAdd返回值为1表示新添加，0表示更新
        String clientId = rankingScoreUpdateRequest.getClientId();
        double score = rankingScoreUpdateRequest.getScore();
        redisTemplate.opsForZSet().add(RANKING_KEY, clientId, score);
        RankingDTO rankingDTO = new RankingDTO();
        // 返回用户排名(从0开始)，zRank是升序排名，zReverseRank是降序排名
        Long rank = redisTemplate.opsForZSet().reverseRank(RANKING_KEY, clientId);
        rankingDTO.setRank(rank);
        rankingDTO.setScore(score);
        rankingDTO.setClientId(clientId);

        return rankingDTO;
    }

    /**
     * 增加用户分数
     * @return 更新后的分数
     */
    public RankingDTO incrementScore(RankingScoreIncreaseRequest rankingScoreIncreaseRequest) {
        String clientId = rankingScoreIncreaseRequest.getClientId();
        double increment = rankingScoreIncreaseRequest.getIncrementScore();
        Double score = redisTemplate.opsForZSet().incrementScore(RANKING_KEY, clientId, increment);
        RankingDTO rankingDTO = new RankingDTO();
        rankingDTO.setScore(score == null ? 0.0:score);
        rankingDTO.setClientId(clientId);
        return rankingDTO;
    }

    /**
     * 获取用户的分数
     * @param clientId 用户ID
     * @return 分数，如果用户不存在返回0
     */
    public RankingDTO getClientScore(String clientId) {
        Double score = redisTemplate.opsForZSet().score(RANKING_KEY, clientId);
        RankingDTO rankingDTO = new RankingDTO();
        rankingDTO.setScore(score == null ? 0.0: score);
        rankingDTO.setClientId(clientId);
        return rankingDTO;
    }

    /**
     * 获取用户的排名
     * @param clientId 用户ID
     * @return 排名(从1开始)，如果用户不存在返回-1
     */
    public RankingDTO getClientRank(String clientId) {
        Long rank = redisTemplate.opsForZSet().reverseRank(RANKING_KEY, clientId);
        RankingDTO rankingDTO = new RankingDTO();
        rankingDTO.setRank(rank == null ? -1: rank + 1);
        rankingDTO.setClientId(clientId);
        return rankingDTO;
    }

    /**
     * 获取排行榜前N名
     * @param topN 前N名
     * @return 排行榜列表，包含用户ID、分数和排名
     */
    public List<Map<String, Object>> getTopRank(int topN) {
        // 获取前N名，按分数降序排列
        Set<TypedTuple<Object>> entries = redisTemplate.opsForZSet()
                .reverseRangeWithScores(RANKING_KEY, 0, topN - 1);

        if (entries == null || entries.isEmpty()) {
            return Collections.emptyList();
        }

        // 转换为包含排名信息的列表
        List<Map<String, Object>> result = new ArrayList<>();
        int rank = 1;
        for (TypedTuple<Object> entry : entries) {
            Map<String, Object> item = new HashMap<>();
            item.put("clientId", entry.getValue().toString());
            item.put("score", entry.getScore());
            item.put("rank", rank++);
            result.add(item);
        }

        return result;
    }

    /**
     * 获取用户附近的排名（用户前后各n名）
     * @param clientId 用户ID
     * @param around 前后各多少名
     * @return 包含用户附近排名的列表
     */
    public List<Map<String, Object>> getRankAroundUser(String clientId, int around) {
        Long userRank = redisTemplate.opsForZSet().reverseRank(RANKING_KEY, clientId);
        if (userRank == null) {
            return Collections.emptyList();
        }

        // 计算查询范围
        long start = Math.max(0, userRank - around);
        long end = userRank + around;

        // 获取范围内的排名数据
        Set<TypedTuple<Object>> entries = redisTemplate.opsForZSet()
                .reverseRangeWithScores(RANKING_KEY, start, end);

        if (entries == null || entries.isEmpty()) {
            return Collections.emptyList();
        }

        // 转换为包含排名信息的列表
        return entries.stream().map(entry -> {
            Map<String, Object> item = new HashMap<>();
            item.put("clientId", entry.getValue().toString());
            item.put("score", entry.getScore());
            item.put("rank", redisTemplate.opsForZSet().reverseRank(RANKING_KEY, entry.getValue()) + 1);
            item.put("isCurrentUser", entry.getValue().toString().equals(clientId));
            return item;
        }).collect(Collectors.toList());
    }

    /**
     * 获取排行榜总人数
     * @return 总人数
     */
    public Long getTotalClients() {
        Long total = redisTemplate.opsForZSet().size(RANKING_KEY);
        return (total == null? 0L: total);
    }

    /**
     * 清除排行榜数据
     */
    public void clearRanking() {
        redisTemplate.delete(RANKING_KEY);
    }
}
