package com.example.intelligentscheduling.entity;/*
 *
 * @Param
 */

import lombok.Data;

/**
 * 员工表
 */
@Data
public class Staff {
    private Long id;

    //名字
    private String name;

    //邮件地址
    private String email;

    //职位
    private String position;

    //门店id
    private Long shopId;
}
