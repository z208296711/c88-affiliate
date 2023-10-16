package com.c88.affiliate.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionDTO {

    private String id;

    private String serial_no;

    private String username;

    private String type;

    private BigDecimal amount;



}
