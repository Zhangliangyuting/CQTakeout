package com.sky.controller.user;

import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("UserSetmealController")
@RequestMapping("/user/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {
    @Autowired
    SetmealService setmealService;


    @GetMapping("/list")
    public Result<List<Setmeal>> listByCategoryId(Long categoryId){
        List<Setmeal> setmeals = setmealService.listByCategoryId(categoryId);
        return Result.success(setmeals);
    }

    @GetMapping("/dish/{id}")
    public Result<List<DishItemVO>> dishGetBySetmealId(@PathVariable Long id){
        List<DishItemVO> dishItemVOS = setmealService.getDishItemById(id);
        return Result.success(dishItemVOS);
    }
}
