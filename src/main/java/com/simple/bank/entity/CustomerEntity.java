package com.simple.bank.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity {
    private Long customerId;
    private String name;
    private String email;
    private String mobile;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
