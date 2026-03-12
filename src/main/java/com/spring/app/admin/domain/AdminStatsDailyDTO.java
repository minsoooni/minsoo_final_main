package com.spring.app.admin.domain;

import lombok.Data;

@Data
public class AdminStatsDailyDTO {
    private String date;
    private int count;
}