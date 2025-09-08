package com.simple.bank.service.redis;

import com.simple.bank.api.response.ListOfLikedUserResponse;
import com.simple.bank.dto.LikeRankDTO;
import java.util.List;

public interface LikeService {
    boolean like(String contentId, String userId);
    boolean cancelLike(String contentId, String userId);
    long getLikeCount(String contentId);
    boolean isLiked(String contentId, String userId);
//    ListOfUserLikedResponse getLikedUsers(String contentId, int page, int size);
    ListOfLikedUserResponse getLikedUsers(String contentId);
    List<LikeRankDTO> getLikeRank(int topN);
}
