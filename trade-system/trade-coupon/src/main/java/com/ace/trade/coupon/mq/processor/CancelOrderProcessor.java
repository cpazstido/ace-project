package com.ace.trade.coupon.mq.processor;

import com.ace.trade.common.constants.TradeEnum;
import com.ace.trade.common.protocol.coupon.ChangeCouponStatusReq;
import com.ace.trade.common.protocol.coupon.ChangeCouponStatusRes;
import com.ace.trade.common.protocol.mq.CancelOrderMQ;
import com.ace.trade.common.rocketmq.IMessageProcessor;
import com.ace.trade.coupon.service.ICouponService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class CancelOrderProcessor implements IMessageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CancelOrderProcessor.class);

    @Autowired
    private ICouponService couponService;
    public boolean handleMessage(MessageExt messageExt) {
        try {
            String body = new String(messageExt.getBody(),"UTF-8");
            String msgId = messageExt.getMsgId();
            String tags = messageExt.getTags();
            String keys = messageExt.getKeys();
            logger.info("coupon CancelOrderProcessor receive message:"+messageExt);

            CancelOrderMQ cancelOrderMQ = JSON.parseObject(body,CancelOrderMQ.class);
            if(StringUtils.isNotBlank(cancelOrderMQ.getCouponId())){
                ChangeCouponStatusReq changeCouponStatusReq = new ChangeCouponStatusReq();
                changeCouponStatusReq.setOrderId(cancelOrderMQ.getOrderId());
                changeCouponStatusReq.setCouponId(cancelOrderMQ.getCouponId());
                changeCouponStatusReq.setIsUsed(TradeEnum.YesNoEnum.NO.getCode());
                ChangeCouponStatusRes changeCouponStatusRes = this.couponService.changeCouponStatus(changeCouponStatusReq);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
