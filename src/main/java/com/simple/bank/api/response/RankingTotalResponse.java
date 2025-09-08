package com.simple.bank.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.simple.bank.dto.RankingDTO;
import com.simple.bank.dto.Response;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({"response", "rank"})
@Setter @Getter
public class RankingTotalResponse {
    private Response response;
    private Long total;

    public RankingTotalResponse(Long total, String message){
        this.response = new Response(message);
        this.total = total;
    }

    public RankingTotalResponse(Long total){
        this.response = new Response();
        this.total = total;
    }

    public RankingTotalResponse(){
        this.response = new Response();
        this.total = 0L;
    }

}
