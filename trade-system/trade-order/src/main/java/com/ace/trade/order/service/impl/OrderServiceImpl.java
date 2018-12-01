package com.ace.trade.order.service.impl;

import com.ace.trade.common.api.ICouponApi;
import com.ace.trade.common.api.IGoodsApi;
import com.ace.trade.common.api.IUserApi;
import com.ace.trade.common.constants.MQEnums;
import com.ace.trade.common.constants.TradeEnum;
import com.ace.trade.common.exception.AceMQException;
import com.ace.trade.common.exception.AceOrderException;
import com.ace.trade.common.protocol.coupon.ChangeCouponStatusReq;
import com.ace.trade.common.protocol.coupon.ChangeCouponStatusRes;
import com.ace.trade.common.protocol.coupon.QueryCouponReq;
import com.ace.trade.common.protocol.coupon.QueryCouponRes;
import com.ace.trade.common.protocol.goods.QueryGoodsReq;
import com.ace.trade.common.protocol.goods.QueryGoodsRes;
import com.ace.trade.common.protocol.goods.ReduceGoodsNumberReq;
import com.ace.trade.common.protocol.goods.ReduceGoodsNumberRes;
import com.ace.trade.common.protocol.mq.CancelOrderMQ;
import com.ace.trade.common.protocol.order.ConfirmOrderReq;
import com.ace.trade.common.protocol.order.ConfirmOrderRes;
import com.ace.trade.common.protocol.user.ChangeUserMoneyReq;
import com.ace.trade.common.protocol.user.ChangeUserMoneyRes;
import com.ace.trade.common.protocol.user.QueryUserReq;
import com.ace.trade.common.protocol.user.QueryUserRes;
import com.ace.trade.common.rocketmq.AceMQProducer;
import com.ace.trade.common.util.IDGenerator;
import com.ace.trade.entity.TradeOrder;
import com.ace.trade.mapper.TradeOrderMapper;
import com.ace.trade.order.service.IOrderService;
import com.alibaba.druid.support.json.JSONUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.rocketmq.client.producer.SendResult;
import java.math.BigDecimal;
import java.util.Date;

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private IGoodsApi goodsApi;
    @Autowired
    private ICouponApi couponApi;
    @Autowired
    private IUserApi userApi;

    @Autowired
    private AceMQProducer aceMQProducer;
    @Autowired
    private TradeOrderMapper tradeOrderMapper;

    public ConfirmOrderRes confirmOrder(ConfirmOrderReq confirmOrderReq) {
        ConfirmOrderRes confirmOrderRes = new ConfirmOrderRes();
        confirmOrderRes.setRetCode(TradeEnum.RetEnum.SUCCESS.getCode());
        try {
            QueryGoodsReq queryGoodsReq = new QueryGoodsReq();
            queryGoodsReq.setGoodsId(confirmOrderReq.getGoodsId());
            QueryGoodsRes queryGoodsRes = goodsApi.queryGoods(queryGoodsReq);
            //1、检查校验
            checkConfirmOrderReq(confirmOrderReq, queryGoodsRes);
            //2、创建不可见订单
            String orderId = saveNoConfirmOrder(confirmOrderReq);
            //3、调用远程服务，扣优惠券、扣库存、扣余额，如果调用成功-》更改订单状态可见；失败-》发送MQ消息，进行取消订单
        } catch (Exception e) {
            confirmOrderRes.setRetCode(TradeEnum.RetEnum.FAIL.getCode());
            confirmOrderRes.setRetInfo(e.getMessage());
        }
        return confirmOrderRes;
    }

    //调用远程服务，扣优惠券、扣库存、扣余额，如果调用成功-》更改订单状态可见；失败-》发送MQ消息，进行取消订单
    public void callRemoteService(String orderId,ConfirmOrderReq confirmOrderReq){
        try {
            //调用优惠券
            if(StringUtils.isNoneBlank(confirmOrderReq.getCouponId())){
                ChangeCouponStatusReq changeCouponStatusReq = new ChangeCouponStatusReq();
                changeCouponStatusReq.setCouponId(confirmOrderReq.getCouponId());
                changeCouponStatusReq.setIsUsed(TradeEnum.YesNoEnum.YES.getCode());
                changeCouponStatusReq.setOrderId(orderId);
                ChangeCouponStatusRes changeCouponStatusRes = couponApi.changeCouponStatus(changeCouponStatusReq);
                if(!changeCouponStatusRes.getRetCode().equals(TradeEnum.RetEnum.SUCCESS)){
                    throw new Exception("优惠券使用失败！");
                }
            }

            //扣余额
            if(confirmOrderReq.getMoneyPaid()!=null&&confirmOrderReq.getMoneyPaid().compareTo(BigDecimal.ZERO)==1){
                ChangeUserMoneyReq changeUserMoneyReq = new ChangeUserMoneyReq();
                changeUserMoneyReq.setOrderId(orderId);
                changeUserMoneyReq.setUserId(confirmOrderReq.getUserId());
                changeUserMoneyReq.setMoneyLogType(TradeEnum.UserMoneyLogTypeEnum.PAID.getCode());
                ChangeUserMoneyRes changeUserMoneyRes = userApi.changeUserMoney(changeUserMoneyReq);
                if(!changeUserMoneyRes.getRetCode().equals(TradeEnum.RetEnum.SUCCESS.getCode())){
                    throw new Exception("扣用户余额失败！");
                }
            }

            //扣库存
            ReduceGoodsNumberReq reduceGoodsNumberReq = new ReduceGoodsNumberReq();
            reduceGoodsNumberReq.setOrderId(orderId);
            reduceGoodsNumberReq.setGoodsId(confirmOrderReq.getGoodsId());
            reduceGoodsNumberReq.setGoodsNumber(confirmOrderReq.getGoodsNumber());
            ReduceGoodsNumberRes reduceGoodsNumberRes = goodsApi.reduceGoodsNumber(reduceGoodsNumberReq);
            if(!reduceGoodsNumberRes.getRetCode().equals(TradeEnum.RetEnum.SUCCESS.getCode())){
                throw new Exception("扣库存失败！");
            }

            //更改订单状态
            TradeOrder tradeOrder = new TradeOrder();
            tradeOrder.setOrderId(orderId);
            tradeOrder.setOrderStatus(TradeEnum.OrderStatusEnum.CONFIRM.getStatusCode());
            tradeOrder.setConfirmTime(new Date());
            int i=tradeOrderMapper.updateByPrimaryKeySelective(tradeOrder);
            if(i<=0){
                throw new Exception("更改订单状态失败！");
            }
        } catch (Exception e) {
            //发送MQ消息
            CancelOrderMQ cancelOrderMQ = new CancelOrderMQ();
            cancelOrderMQ.setOrderId(orderId);
            cancelOrderMQ.setUserId(confirmOrderReq.getUserId());
            cancelOrderMQ.setGoodsNumber(confirmOrderReq.getGoodsNumber());
            cancelOrderMQ.setGoodsId(confirmOrderReq.getGoodsId());
            cancelOrderMQ.setCouponId(confirmOrderReq.getCouponId());
            cancelOrderMQ.setUserMoney(confirmOrderReq.getMoneyPaid());
            try {
                SendResult sendResult = aceMQProducer.sendMessage(MQEnums.TopicEnum.ORDER_CANCEL,orderId,JSONUtils.toJSONString(cancelOrderMQ));
                System.out.println(sendResult);
            } catch (AceMQException ex) {
                //如果发送失败会有一个定时器轮询，找出长时间未确认的订单，补发取消消息
            }
        }
    }

    public String saveNoConfirmOrder(ConfirmOrderReq confirmOrderReq) throws Exception {
        TradeOrder tradeOrder = new TradeOrder();
        String orderId = IDGenerator.generateUUID();
        tradeOrder.setOrderId(orderId);
        tradeOrder.setUserId(confirmOrderReq.getUserId());
        tradeOrder.setOrderStatus(TradeEnum.OrderStatusEnum.NO_CONFIRM.getStatusCode());
        tradeOrder.setPayStatus(TradeEnum.PayStatusEnum.NO_PAY.getStatusCode());
        tradeOrder.setShoppingStatus(TradeEnum.ShippingStatusEnum.NO_SHIP.getStatusCode());
        tradeOrder.setAddress(confirmOrderReq.getAddress());
        tradeOrder.setConsignee(confirmOrderReq.getConsignee());
        tradeOrder.setGoodsId(confirmOrderReq.getGoodsId());
        tradeOrder.setGoodsNumber(confirmOrderReq.getGoodsNumber());
        tradeOrder.setGoodsPrice(confirmOrderReq.getGoodsPrice());
        BigDecimal goodsAmount = confirmOrderReq.getGoodsPrice().multiply(new BigDecimal((confirmOrderReq.getGoodsNumber())));
        tradeOrder.setGoodsAmount(goodsAmount);
        BigDecimal shippingFee = calculateShippingFee(goodsAmount);
        if(confirmOrderReq.getShippingFee().compareTo(shippingFee)!=0){
            throw new Exception("快递费用不正确");
        }
        tradeOrder.setShoppingFee(shippingFee);
        BigDecimal orderAmount = goodsAmount.add(shippingFee);
        if(orderAmount.compareTo(confirmOrderReq.getOrderAmount())!=0){
            throw new Exception("订单总价异常，请重新下单");
        }
        tradeOrder.setOrderAmount(orderAmount);
        String couponId = confirmOrderReq.getCouponId();
        //优惠券不为空
        if(StringUtils.isNoneBlank(couponId)){
            //查询优惠券状态
            QueryCouponReq queryCouponReq = new QueryCouponReq();
            queryCouponReq.setCouponId(couponId);
            QueryCouponRes queryCouponRes = couponApi.queryCoupon(queryCouponReq);
            if(queryCouponRes==null||!queryCouponRes.getRetCode().equals(TradeEnum.RetEnum.SUCCESS.getCode())){
                throw new Exception("优惠券非法");
            }
            if(!queryCouponRes.getIsUsed().equals(TradeEnum.YesNoEnum.NO.getCode())){
                throw new Exception("优惠券已使用");
            }
            tradeOrder.setCouponId(couponId);
            tradeOrder.setCouponPaid(queryCouponRes.getCouponPrice());
        }else {
            tradeOrder.setCouponPaid(BigDecimal.ZERO);
        }

        //余额支付
        if(confirmOrderReq.getMoneyPaid()!=null){
            int r = confirmOrderReq.getMoneyPaid().compareTo(BigDecimal.ZERO);
            if(r == -1){
                throw new Exception("余额金额非法");
            }
            if(r == 1){
                //判断当前账户余额是否足够
                QueryUserReq queryUserReq = new QueryUserReq();
                queryUserReq.setUserId(confirmOrderReq.getUserId());
                QueryUserRes queryUserRes = userApi.queryUserById(queryUserReq);
                if(queryUserRes==null||!queryUserRes.getRetCode().equals(TradeEnum.RetEnum.SUCCESS.getCode())){
                    throw  new Exception("用户非法");
                }
                if(queryUserRes.getUserMoney().compareTo(confirmOrderReq.getMoneyPaid())==-1){
                    throw new Exception("余额不足");
                }
                tradeOrder.setMoneyPaid(confirmOrderReq.getMoneyPaid());
            }
        }else {
            tradeOrder.setMoneyPaid(BigDecimal.ZERO);
        }
        BigDecimal payAmount = orderAmount.subtract(tradeOrder.getMoneyPaid()).subtract(tradeOrder.getCouponPaid());
        tradeOrder.setPayAmount(payAmount);
        tradeOrder.setAddTime(new Date());

        int ret = this.tradeOrderMapper.insert(tradeOrder);
        if(ret!=1){
            throw new Exception("保存不可见订单失败！");
        }
        return tradeOrder.getOrderId();
    }

    private void checkConfirmOrderReq(ConfirmOrderReq confirmOrderReq, QueryGoodsRes queryGoodsRes) {
        if (confirmOrderReq == null) {
            throw new AceOrderException("下单信息不能为空");
        }

        if (confirmOrderReq.getUserId() == null) {
            throw new AceOrderException("会员账号不能为空");
        }

        if (confirmOrderReq.getGoodsId() == null) {
            throw new AceOrderException("商品编号不能为空");
        }

        if (confirmOrderReq.getGoodsNumber() == null || confirmOrderReq.getGoodsNumber() <= 0) {
            throw new AceOrderException("购买数量不能小于0");
        }

        if (confirmOrderReq.getAddress() == null) {
            throw new AceOrderException("收货地址不能为空");
        }

        if (queryGoodsRes == null || queryGoodsRes.getRetCode().equals(TradeEnum.RetEnum.SUCCESS.getCode())) {
            throw new AceOrderException("未查询到改商品[" + confirmOrderReq.getGoodsId() + "]");
        }

        if (queryGoodsRes.getGoodsNumber() < confirmOrderReq.getGoodsNumber()) {
            throw new AceOrderException("商品库存不足");
        }
        if (queryGoodsRes.getGoodsPrice().compareTo(confirmOrderReq.getGoodsPrice()) != 0) {
            throw new AceOrderException("当前商品价格有变化,请重新下单");
        }
        if (confirmOrderReq.getShippingFee() == null) {
            confirmOrderReq.setShippingFee(BigDecimal.ZERO);
        }
        if (confirmOrderReq.getOrderAmount() == null) {
            confirmOrderReq.setOrderAmount(BigDecimal.ZERO);
        }
    }

    private BigDecimal calculateShippingFee(BigDecimal goodsAmount){
        if(goodsAmount.doubleValue()>100.00){
            return BigDecimal.ZERO;
        }else {
            return new BigDecimal("10");
        }

    }
}
