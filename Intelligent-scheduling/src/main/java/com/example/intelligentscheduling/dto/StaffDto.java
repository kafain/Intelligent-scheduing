package com.example.intelligentscheduling.dto;

import com.example.intelligentscheduling.entity.Staff;
import lombok.Data;

@Data
public class StaffDto extends Staff {
    //门店名称
    private String shopName;

    //偏好类型
    private String type;

    //偏好值
    private String value;

}
