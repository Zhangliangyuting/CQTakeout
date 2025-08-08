package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.*;

public interface OrderService {

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    PageResult<OrderVO> pageQueryWithDetail(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderVO getByIdWithDetail(Long id);

    void cancelById(Long id) throws Exception;

    void repeatOrder(Long id);

    PageResult<OrderVO> pageQueryWithDishes(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO statistic();

    void rejectOrder(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    void confirmOrder(OrdersConfirmDTO ordersConfirmDTO);

    void cancelOrder(OrdersCancelDTO ordersCancelDTO) throws Exception;

    void delivery(Long id);

    void complete(Long id);

    void remindOrder(Long id);
}
