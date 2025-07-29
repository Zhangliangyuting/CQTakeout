package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("UserDishController")
@RequestMapping("/user/dish")
public class DishController {
    @Autowired
    DishService dishService;
    @GetMapping("/list")
    public Result<List<DishVO>> getByCategoryId(Long categoryId){
        Dish dish = new Dish();
        dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        List<DishVO> dishes= dishService.listWithFlavors(dish);
        return Result.success(dishes);
    }
}
