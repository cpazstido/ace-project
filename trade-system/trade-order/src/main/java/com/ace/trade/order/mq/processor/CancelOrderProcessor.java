package com.ace.trade.order.mq.processor;

import com.ace.trade.common.constants.TradeEnum;
import com.ace.trade.common.protocol.mq.CancelOrderMQ;
import com.ace.trade.common.rocketmq.IMessageProcessor;
import com.ace.trade.entity.TradeOrder;
import com.ace.trade.mapper.TradeOrderMapper;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;

public class CancelOrderProcessor implements IMessageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CancelOrderProcessor.class);

    @Autowired
    private TradeOrderMapper tradeOrderMapper;
    public boolean handleMessage(MessageExt messageExt) {
        try {
            String body = new String(messageExt.getBody(),"UTF-8");
            String msgId = messageExt.getMsgId();
            String tags = messageExt.getTags();
            String keys = messageExt.getKeys();
            logger.info("order CancelOrderProcessor receive message:"+messageExt);

            String messageInfo =
                    "\n========CancelOrderProcessor receive message========\n"
                            +messageExt+"\n"
                            +"========CancelOrderProcessor receive message========\n";
            System.out.println(messageInfo);

            CancelOrderMQ cancelOrderMQ = JSON.parseObject(body,CancelOrderMQ.class);

            TradeOrder record = new TradeOrder();
            record.setOrderId(cancelOrderMQ.getOrderId());
            record.setOrderStatus(TradeEnum.OrderStatusEnum.CANCEL.getStatusCode());
            tradeOrderMapper.updateByPrimaryKeySelective(record);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
