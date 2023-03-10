package com.example.intelligentscheduling.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //用户名
    private String name;

    //手机号
    private String phone;

    //状态 0:禁用，1:正常
    private Integer status;

    private String password;
}
