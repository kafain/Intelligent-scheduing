package com.example.intelligentscheduling.controller;/*
 *
 * @Param
 */

import com.example.intelligentscheduling.common.R;
import com.example.intelligentscheduling.entity.Shop;
import com.example.intelligentscheduling.entity.Staff;
import com.example.intelligentscheduling.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/shop")
public class ShopController {
    @Autowired
    private ShopService shopService;

    /**
     *  新增门店
     * @param: shop
     * @return R<String>
     **/
    @PostMapping
    public R<String> save(@RequestBody Shop shop){
        boolean loop = shopService.save(shop);
        if(!loop){
            return R.error("新增门店失败");
        }
        return R.success("新增门店成功");
    }

    /**
     *  根据id删除门店
     * @param: id
     * @return R<String>
     **/
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id){

        boolean loop = shopService.removeById(id);

        if(!loop){
            return R.error("删除门店失败");
        }
        return R.success("删除门店成功");
    }

    /**
     *  返回门店信息list集合
     * @return R<List<Shop>>
     **/
    @GetMapping("/list")
    public R<List<Shop>> list(){
        List<Shop> list = shopService.list();
        return R.success(list);
    }

    /**
     *  根据id修改门店信息
     * @return R<String>
     **/
    @PutMapping
    public R<String> update(@RequestBody Shop shop){
        boolean loop = shopService.updateById(shop);
        if(!loop){
            return R.error("门店信息修改失败");
        }
        return R.success("门店信息修改成功");
    }

}
