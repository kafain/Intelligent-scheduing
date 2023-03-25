package com.example.intelligentscheduling.controller;/*
 *
 * @Param
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.intelligentscheduling.common.R;
import com.example.intelligentscheduling.dto.StaffDto;
import com.example.intelligentscheduling.entity.Preference;
import com.example.intelligentscheduling.entity.Staff;
import com.example.intelligentscheduling.entity.User;
import com.example.intelligentscheduling.service.PreferenceService;
import com.example.intelligentscheduling.service.StaffService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/staff")
public class StaffController {
    @Autowired
    private StaffService staffService;
    @Autowired
    private PreferenceService preferenceService;

    /**
     *  添加员工
     * @param: staff{id,name,emai,position,shopName}
     * @return R<String>
     **/
    @PostMapping
    public R<String> save(@RequestBody StaffDto staffDto){

        //根据门店名称得到门店id
        Long shopId = staffDto.getShopId();
        staffDto.setShopId(shopId);
        boolean loop = staffService.save(staffDto);

        if(!loop){
            return R.error("新增员工失败");
        }
        return R.success("新增员工成功");
    }

    /**
     *  员工信息分页查询，展示员工的偏好信息
     * @param: page
     * @param: pageSize
     * @param: name
     * @return R<Page>
     **/
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //构造分页构造器
        Page<Staff> pageInfo = new Page(page, pageSize);
        Page<StaffDto> staffDtoPage = new Page(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Staff> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件,根据名字模糊查询
        queryWrapper.like(StringUtils.isNotEmpty(name),Staff::getName,name);
        //执行查询
        staffService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,staffDtoPage,"records");

        //查询shopName
        List<Staff> records = pageInfo.getRecords();
        List<StaffDto> staffDtos = records.stream().map((item) -> {
            StaffDto staffDto = new StaffDto();
            BeanUtils.copyProperties(item, staffDto);//对象拷贝
            Long staffId = item.getId();//得到员工id
            LambdaQueryWrapper<Preference> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Preference::getStaffId, staffId);
            Preference preference = preferenceService.getOne(queryWrapper1);
            staffDto.setType(preference.getType());//给员工赋予偏好类型
            staffDto.setValue(preference.getValue());//给员工赋予偏好值
            return staffDto;
        }).collect(Collectors.toList());
        
        staffDtoPage.setRecords(staffDtos);
        return R.success(staffDtoPage);
    }

    /**
     *  根据id修改员工信息
     * @return R<String>
     **/
    @PutMapping
    public R<String> update(@RequestBody Staff staff){
        boolean loop = staffService.updateById(staff);
        if(!loop){
            return R.error("员工信息修改失败");
        }
        return R.success("员工信息修改成功");
    }

    /**
     *  根据id查询员工信息
     * @param: id
     * @return R<Staff>
     **/
    @GetMapping("/{id}")
    public R<Staff> getById(@PathVariable Long id){
        Staff staff = staffService.getById(id);
        if(staff == null) {
            return R.error("没有查询到该员工");
        }
        return R.success(staff);
    }

    /**
     *  根据id删除员工，同时删除员工偏好信息
     * @param: id
     * @return R<String>
     **/
    @DeleteMapping("/{id}")
    @Transactional
    public R<String> delete(@PathVariable Long id){

        boolean loop = staffService.removeById(id);
        LambdaQueryWrapper<Preference> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Preference::getStaffId,id);
        preferenceService.remove(queryWrapper);

        if(!loop){
            return R.error("删除员工失败");
        }
        return R.success("删除员工成功");
    }

}
