package com.simple.bank.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.simple.bank.dto.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@JsonPropertyOrder({"response", "contentId", "userId"})
@Setter @Getter
public class LikeStatusResponse {
    private Response response;
    private String contentId;
    private String userId;
    private boolean isLiked;

    public LikeStatusResponse(String contentId, String userId, boolean isLiked, String message){
        this.contentId = contentId;
        this.userId = userId;
        this.isLiked = isLiked;
        this.response = new Response(message);
    }

    public LikeStatusResponse(String contentId, String userId, boolean isLiked){
        this.contentId = contentId;
        this.userId = userId;
        this.isLiked = isLiked;
        this.response = new Response();
    }

}
