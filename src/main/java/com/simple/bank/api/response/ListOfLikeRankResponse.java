package com.simple.bank.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.simple.bank.dto.LikeRankDTO;
import com.simple.bank.dto.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@JsonPropertyOrder({"response", "contentId", "userId"})
@Setter @Getter
public class ListOfLikeRankResponse {
    private Response response;
    private int topN;
    private long timestamp;
    private List<LikeRankDTO> rankList;

    public ListOfLikeRankResponse(int topN, List<LikeRankDTO> rankList, String message){
        this.topN = topN;
        this.rankList = rankList;
        this.timestamp = System.currentTimeMillis();
        this.response = new Response(message);
    }

    public ListOfLikeRankResponse(int topN, List<LikeRankDTO> rankList){
        this.topN = topN;
        this.rankList = rankList;
        this.timestamp = System.currentTimeMillis();
        this.response = new Response();
    }


}
