package com.simple.bank.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.simple.bank.dto.RankingDTO;
import com.simple.bank.dto.Response;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({"response", "rank"})
@Setter @Getter
public class RankingResponse {
    private Response response;
    private RankingDTO ranking;

    public RankingResponse(RankingDTO rankingDTO, String message){
        this.response = new Response(message);
        this.ranking = rankingDTO;
    }

    public RankingResponse(RankingDTO rankingDTO){
        this.response = new Response();
        this.ranking = rankingDTO;
    }


}
