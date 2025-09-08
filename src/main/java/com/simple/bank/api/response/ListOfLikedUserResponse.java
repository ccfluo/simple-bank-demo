package com.simple.bank.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.simple.bank.dto.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@JsonPropertyOrder({"response", "contentId", "userId"})
@Setter @Getter
public class ListOfLikedUserResponse {
    private Response response;
//    private PageInfo pageInfo;
    private List<String> users;
    private String contentId;

    public ListOfLikedUserResponse(List<String> listOfUsers, String contentId, String message){
        this.users = listOfUsers;
        this.contentId = contentId;
//        this.pageInfo = pageInfo;
        this.response = new Response(message);
    }

    public ListOfLikedUserResponse(List<String> listOfUsers, String contentId){
        this.users = listOfUsers;
        this.contentId = contentId;
//        this.pageInfo = pageInfo;
        this.response = new Response();
    }


}
