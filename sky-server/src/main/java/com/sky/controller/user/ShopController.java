package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("UserShopController")
@RequestMapping("user/shop")
@Api(tags = "店铺相关接口")
public class ShopController {
    @Autowired
    RedisTemplate redisTemplate;
    public static final String KEY= "SHOP_STATUS";
    @GetMapping("/status")
    public Result<Integer> getStatus(){
        Integer status = (Integer)redisTemplate.opsForValue().get(KEY);
        return Result.success(status);
    }
}
