package com.example.intelligentscheduling.entity;/*
 *
 * @Param
 */

import lombok.Data;

/**
 * 员工爱好表
 */
@Data
public class Preference {
    private String type;

    //员工id
    private Long staffId;

    //偏好值
    private String value;
}
