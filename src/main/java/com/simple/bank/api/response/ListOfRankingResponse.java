package com.simple.bank.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.simple.bank.dto.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@JsonPropertyOrder({"response", "listOfRankings"})
@Setter @Getter
public class ListOfRankingResponse {
    private Response response;
    private List<Map<String, Object>> listOfRankings;

    public ListOfRankingResponse(List<Map<String, Object>> listOfRankings, String message){
        this.response = new Response(message);
        this.listOfRankings = listOfRankings;
    }

    public ListOfRankingResponse(List<Map<String, Object>> listOfRankings){
        this.response = new Response();
        this.listOfRankings = listOfRankings;
    }

}
