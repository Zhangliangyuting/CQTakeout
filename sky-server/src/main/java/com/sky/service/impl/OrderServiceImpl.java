package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import org.apache.poi.poifs.crypt.dsig.OOXMLURIDereferencer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    AddressBookMapper addressBookMapper;
    @Autowired
    ShoppingCartMapper shoppingCartMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;
    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        Long userId = BaseContext.getCurrentId();
        AddressBook address = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(address==null) throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.select(shoppingCart);
        if(shoppingCarts==null||shoppingCarts.size()==0) throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);


        Orders order = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,order);

        order.setStatus(Orders.PENDING_PAYMENT);

        order.setOrderTime(LocalDateTime.now());

        order.setUserId(userId);
        order.setUserName(userMapper.getById(userId).getName());

        order.setPayStatus(Orders.UN_PAID);

        order.setNumber(String.valueOf(System.currentTimeMillis()));

        order.setAddress(address.getDetail());
        order.setConsignee(address.getConsignee());
        order.setPhone(address.getPhone());

        orderMapper.add(order);

        List<OrderDetail> orderDetailList = new ArrayList<>();
        Long orderId = order.getId();;
        for(ShoppingCart shopping:shoppingCarts){
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shopping,orderDetail);
            orderDetail.setOrderId(orderId);
            orderDetailList.add(orderDetail);
        }

        orderDetailMapper.insertBatch(orderDetailList);

        shoppingCartMapper.deleteByUserId(userId);

        OrderSubmitVO orderSubmitVO= OrderSubmitVO.builder()
                .id(order.getId())
                .orderNumber(order.getNumber())
                .orderAmount(order.getAmount())
                .orderTime(order.getOrderTime()).build();

        return orderSubmitVO;
    }
}
