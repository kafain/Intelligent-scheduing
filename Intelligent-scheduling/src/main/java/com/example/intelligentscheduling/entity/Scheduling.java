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
    //门店id
    private int shopId;

    //员工id
    private int staffId;

    //起始工作时间
    private int startTime;
}
