package com.example.intelligentscheduling.entity;/*
 *
 * @Param
 */

import lombok.Data;

/**
 * 排班表
 */
@Data
public class Scheduling {

    private String type;

    //门店id
    private Long shopId;

    //规则值
    private String value;
}
