package com.simple.bank.dto;

import lombok.Data;

@Data
public class PageInfo {
    private int size;  //requested size
    private long page; //requested page no#
    private long total; //total records
    private boolean hasMore;

}
