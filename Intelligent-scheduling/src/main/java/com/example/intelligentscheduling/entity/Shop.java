package com.example.intelligentscheduling.entity;/*
 *
 * @Param
 */

import lombok.Data;

/**
 * 门店表
 */
@Data
public class Shop {
    private Long id;

    private String name;

    private String address;

    private Double size;
}
