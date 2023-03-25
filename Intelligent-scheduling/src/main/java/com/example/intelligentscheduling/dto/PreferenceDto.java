package com.example.intelligentscheduling.dto;

import com.example.intelligentscheduling.entity.Preference;
import lombok.Data;

@Data
public class PreferenceDto extends Preference {
    //员工姓名
    private String staffName;
}
