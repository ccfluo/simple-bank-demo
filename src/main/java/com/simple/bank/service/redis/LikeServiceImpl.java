package com.simple.bank.service.redis;

import com.simple.bank.api.response.ListOfLikedUserResponse;
import com.simple.bank.dto.LikeRankDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LikeServiceImpl implements LikeService {

    // Redis键前缀定义
    private static final String LIKE_STATUS_PREFIX = "like:status:"; // like status: Hash key prefix + contentId
    private static final String LIKE_COUNT_PREFIX = "like:count:";   // like count: String prefix + contentId
    private static final String LIKE_USER_SET_PREFIX = "like:users:";// liked user Set prefix + contentId
    private static final String LIKE_RANK_KEY = "like:rank";         // liked rank Zset

    private final RedisTemplate<String, Object> redisTemplate;
    private final ZSetOperations<String, Object> zSetOperations;

    // 构造器注入RedisTemplate
    public LikeServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.zSetOperations = redisTemplate.opsForZSet();
    }

    /**
     * 用户点赞
     * @param contentId 内容ID（如文章ID、评论ID等）
     * @param userId 用户ID
     * @return 点赞结果
     */
    public boolean like(String contentId, String userId) {
        // 1. build Redis key
        String statusKey = LIKE_STATUS_PREFIX + contentId;
        String countKey = LIKE_COUNT_PREFIX + contentId;
        String userSetKey = LIKE_USER_SET_PREFIX + contentId;

        // 2. check if user liked
        Object status = redisTemplate.opsForHash().get(statusKey, userId);
        if (status != null && "1".equals(status.toString())) {
            return false; // already liked, return false
        }

        // 3. update redis to like（多命令原子性保证）
        redisTemplate.executePipelined(new SessionCallback< Object>() {
            @Override
            public Object execute(RedisOperations operations) {
                //update redis Hash key - "like:status:{contentId}" | field - userId | value - 1
                operations.opsForHash().put(statusKey, userId, "1");
                //update redis String key - "like:count:{countentId}" | value++
                operations.opsForValue().increment(countKey, 1);
                //update redis Set key - ""like:users:{contentId}" | add member : userId
                operations.opsForSet().add(userSetKey, userId);
                operations.expire(statusKey, 7, TimeUnit.DAYS);
                operations.expire(countKey, 7, TimeUnit.DAYS);
                operations.expire(userSetKey, 7, TimeUnit.DAYS);
                return null;
            }
        });

        // 4. update like rank Zset key - "like:rank" | member - contendId | score++
        zSetOperations.incrementScore(LIKE_RANK_KEY, contentId, 1);

        return true;
    }
    /**
     * 用户取消点赞
     * @param contentId 内容ID
     * @param userId 用户ID
     * @return 取消点赞结果
     */
    public boolean cancelLike(String contentId, String userId) {
        // 1. build Redis key
        String statusKey = LIKE_STATUS_PREFIX + contentId;
        String countKey = LIKE_COUNT_PREFIX + contentId;
        String userSetKey = LIKE_USER_SET_PREFIX + contentId;

        // 2. check if user liked
        Object status = redisTemplate.opsForHash().get(statusKey, userId);
        if (status == null || "0".equals(status.toString())) {
            return false; // didn't like, return false
        }

        // 3. cancel the like（多命令原子性保证）
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) {
                //update redis Hash key - "like:status:{contentId}" | field - userId | value - 0
                operations.opsForHash().put(statusKey, userId, "0");
                //update redis String key - "like:count:{countentId}" | value--
                operations.opsForValue().increment(countKey, -1);
                //update redis Set key -  "like:users:{contentId}" | remove member : userId
                operations.opsForSet().remove(userSetKey, userId);
                return null;
            }
        });

        // 4. update like rank Zset key - "like:rank" | member - contendId | score--
        zSetOperations.incrementScore(LIKE_RANK_KEY, contentId, -1);

        return true;
    }

    /**
     * 获取内容的点赞数
     * @param contentId 内容ID
     * @return 点赞数
     */
    public long getLikeCount(String contentId) {
        String countKey = LIKE_COUNT_PREFIX + contentId;
        Object count = redisTemplate.opsForValue().get(countKey);
        return count != null ? Long.parseLong(count.toString()) : 0;
    }

    /**
     * 检查用户是否点赞
     * @param contentId 内容ID
     * @param userId 用户ID
     * @return 是否点赞
     */
    public boolean isLiked(String contentId, String userId) {
        String statusKey = LIKE_STATUS_PREFIX + contentId;
        Object status = redisTemplate.opsForHash().get(statusKey, userId);
        return status != null && "1".equals(status.toString());
    }

    /**
     * 获取点赞的用户列表（分页） TODO: won't support pagination for SET. will return all liked users.
     * @param contentId 内容ID
//     * @param page 页码（从0开始）
//     * @param size 每页大小
     * @return 用户ID列表
     */
//    public ListOfUserLikedResponse getLikedUsers(String contentId, int page, int size) {
    public ListOfLikedUserResponse getLikedUsers(String contentId) {
        String userSetKey = LIKE_USER_SET_PREFIX + contentId;

//        long start = (long) page * size;
//        long end = start + size - 1;

//        List<String> result = new ArrayList<>(size);
        List<String> result = new ArrayList<>();

        ScanOptions options = ScanOptions.scanOptions()
                .match("*")
                .count(200)
                .build();

//        long index = 0;
//        boolean hasMore = false;

        try (Cursor<Object> cursor = redisTemplate.opsForSet().scan(userSetKey, options)) {
            while (cursor.hasNext()) {
//                if (result.size() >= size) {
//                    hasMore = true;
//                    break;
//                }
                String userId = cursor.next().toString();

//                if (index >= start && index <= end) {
                    result.add(userId);
//                }
//                index++;
            }
        }

//        PageInfo pageInfo = new PageInfo();
//        pageInfo.setPage(page);
//        pageInfo.setSize(size);
//        pageInfo.setTotal(getLikeCount(contentId));
//        pageInfo.setHasMore(hasMore);
//        return(new ListOfUserLikedResponse(result,contentId,pageInfo));
        return(new ListOfLikedUserResponse(result,contentId));
    }


    /**
     * 获取点赞排行榜（按点赞数降序）
     * @param topN 前N名
     * @return 内容ID及对应的点赞数
     */
    public List<LikeRankDTO> getLikeRank(int topN) {
        // inquire topN liked
        Set<ZSetOperations.TypedTuple<Object>> tuples = zSetOperations
                .reverseRangeWithScores(LIKE_RANK_KEY, 0, topN - 1);

        if (tuples == null || tuples.isEmpty()) {
            return Collections.emptyList();
        }

        List<LikeRankDTO> rankList = new ArrayList<>();
        for (ZSetOperations.TypedTuple<Object> tuple : tuples) {
            LikeRankDTO likeRankDTO = new LikeRankDTO();
            likeRankDTO.setContentId(tuple.getValue().toString());
            likeRankDTO.setLikeCount(tuple.getScore());
            rankList.add(likeRankDTO);
        }

        return rankList;
    }

}
