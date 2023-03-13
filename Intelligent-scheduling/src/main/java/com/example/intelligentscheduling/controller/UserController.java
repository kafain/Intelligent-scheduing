package com.example.intelligentscheduling.controller;/*
 *
 * @Param
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.intelligentscheduling.common.R;
import com.example.intelligentscheduling.entity.User;
import com.example.intelligentscheduling.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /**
     *  管理员登录
     * @param: request
     * @param: user
     * @return R<User>
     **/
    @PostMapping("/login")
    public R<User> login(HttpServletRequest request, @RequestBody User user){
        //1、将页面提交的密码password进行md5加密处理
        String password = user.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据用户名查询数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,user.getUsername());
        User admin = userService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        if(admin == null){
            return R.error("登录失败");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if(!admin.getPassword().equals(password)){
            return R.error("登录失败");
        }


        //6、登录成功，将管理员id存入Session并返回登录成功结果
        request.getSession().setAttribute("user",admin.getId());
        return R.success(admin);
    }

    /**
     *  管理员退出登录
     * @param: request
     * @return R<String>
     **/
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }

    /**
     *  新增管理员
     * @param: request
     * @param: employee
     * @return R<String>
     **/
    @PostMapping
    public R<String> save(@RequestBody User user){
        log.info("新增管理员，管理员信息:{}",user.toString());
        //得到前端传来的密码，并进行md5加密处理
        String password = user.getPassword();
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));

        boolean loop = userService.save(user);
        if(!loop){
            return R.error("新增管理员失败");
        }

        return R.success("新增管理员成功");
    }
}
