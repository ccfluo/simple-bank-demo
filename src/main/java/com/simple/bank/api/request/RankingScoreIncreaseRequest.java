package com.simple.bank.api.request;

import com.simple.bank.dto.OperContext;
import lombok.Data;

@Data
public class RankingScoreIncreaseRequest {
    private OperContext operContext;
    private String clientId;
    private double incrementScore;
}