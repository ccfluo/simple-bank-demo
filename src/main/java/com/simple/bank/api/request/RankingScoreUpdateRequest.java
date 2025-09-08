package com.simple.bank.api.request;

import com.simple.bank.dto.OperContext;
import lombok.Data;

@Data
public class RankingScoreUpdateRequest {
    private OperContext operContext;
    private String clientId;
    private double score;
}