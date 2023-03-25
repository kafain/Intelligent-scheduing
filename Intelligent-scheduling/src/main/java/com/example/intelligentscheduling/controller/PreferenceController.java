package com.example.intelligentscheduling.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.intelligentscheduling.common.R;
import com.example.intelligentscheduling.dto.PreferenceDto;
import com.example.intelligentscheduling.dto.StaffDto;
import com.example.intelligentscheduling.entity.Preference;
import com.example.intelligentscheduling.entity.Staff;
import com.example.intelligentscheduling.service.PreferenceService;
import com.example.intelligentscheduling.service.StaffService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/preference")
public class PreferenceController {

    @Autowired
    private PreferenceService preferenceService;

    @Autowired
    private StaffService staffService;

    /**
     *  员工偏好信息分页查询
     * @param: page
     * @param: pageSize
     * @param: name
     * @return R<Page>
     **/
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //构造分页构造器
        Page<Preference> pageInfo = new Page(page, pageSize);
        Page<PreferenceDto> preferenceDtoPage = new Page<>(page,pageSize);

        //1.根据姓名模糊查询出所有符合的员工id
        //构造条件构造器
        LambdaQueryWrapper<Staff> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件,根据名字模糊查询
        queryWrapper.like(StringUtils.isNotEmpty(name),Staff::getName,name);
        //执行查询
        List<Staff> staffs = staffService.list(queryWrapper);


        List<Long> staffIds = staffs.stream().map((item) -> {
            return item.getId();
        }).collect(Collectors.toList());

        //2.根据staffIds分页查询员工偏好
        //构造条件构造器
        LambdaQueryWrapper<Preference> queryWrapper1 = new LambdaQueryWrapper<>();
        //添加过滤条件，符合条件的员工id
        queryWrapper1.in(!staffIds.isEmpty(),Preference::getStaffId,staffIds);
        //执行分页查询
        preferenceService.page(pageInfo,queryWrapper1);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,preferenceDtoPage,"records");

        //查询staffName
        List<Preference> records = pageInfo.getRecords();
        List<PreferenceDto> preferenceDtos = records.stream().map((item) -> {
            PreferenceDto preferenceDto = new PreferenceDto();
            BeanUtils.copyProperties(item, preferenceDto);//对象拷贝
            Long staffId = item.getStaffId();//得到员工id
            queryWrapper.eq(Staff::getId, staffId);
            Staff staff = staffService.getOne(queryWrapper);
            preferenceDto.setStaffName(staff.getName());//给偏好类型赋予员工姓名显示在页面上
            return preferenceDto;
        }).collect(Collectors.toList());

        preferenceDtoPage.setRecords(preferenceDtos);
        return R.success(preferenceDtoPage);
    }

    /**
     *  根据id修改员工偏好信息
     * @return R<String>
     **/
    @PutMapping
    public R<String> update(@RequestBody Preference preference){
        boolean loop = preferenceService.updateById(preference);
        if(!loop){
            return R.error("员工偏好信息修改失败");
        }
        return R.success("员工偏好信息修改成功");
    }

    /**
     *  根据id查询员工偏好信息
     * @param: id
     * @return R<Preference>
     **/
    @GetMapping("/{id}")
    public R<Preference> getById(@PathVariable Long id){
        LambdaQueryWrapper<Preference> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Preference::getStaffId,id);
        Preference preference = preferenceService.getOne(queryWrapper);
        if(preference == null) {
            return R.error("没有查询到该员工的偏好信息");
        }
        return R.success(preference);
    }

    /**
     *  根据id删除员工偏好信息
     * @param: id
     * @return R<String>
     **/
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id){
        LambdaQueryWrapper<Preference> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Preference::getStaffId,id);
        boolean loop = preferenceService.remove(queryWrapper);

        if(!loop){
            return R.error("删除员工偏好失败");
        }
        return R.success("删除员工成功");
    }

    /**
     *  新增员工偏好信息
     * @param: request
     * @param: preferenceDto{name,type,value}
     * @return R<String>
     **/
    @PostMapping
    public R<String> save(@RequestBody PreferenceDto preferenceDto){

        //根据员工姓名查询员工表中是否有该员工，若无则不能添加
        String staffName = preferenceDto.getStaffName();
        LambdaQueryWrapper<Staff> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Staff::getName,staffName);
        Staff one = staffService.getOne(queryWrapper);
        if(one == null){
            return R.error("该员工不存在，不能添加偏好信息");
        }

        //若存在则将type,staffId,value保存到preference表中
        preferenceDto.setStaffId(one.getId());
        boolean loop = preferenceService.save(preferenceDto);

        if(!loop){
            return R.error("新增员工偏好信息失败");
        }
        return R.success("新增员工偏好成功");
    }
}
