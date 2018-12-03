package com.ace.trade.goods.mq.processor;

import com.ace.trade.common.constants.MQEnums;
import com.ace.trade.common.constants.TradeEnum;
import com.ace.trade.common.protocol.goods.AddGoodsNumberReq;
import com.ace.trade.common.protocol.mq.CancelOrderMQ;
import com.ace.trade.common.rocketmq.IMessageProcessor;
import com.ace.trade.entity.TradeMqConsumerLog;
import com.ace.trade.entity.TradeMqConsumerLogExample;
import com.ace.trade.entity.TradeMqConsumerLogKey;
import com.ace.trade.goods.service.IGoodsService;
import com.ace.trade.mapper.TradeMqConsumerLogMapper;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class CancelOrderProcessor implements IMessageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CancelOrderProcessor.class);

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private TradeMqConsumerLogMapper tradeMqConsumerLogMapper;

    public boolean handleMessage(MessageExt messageExt) {
        TradeMqConsumerLog mqConsumerLog = null;
        try {
            String groupName = "goods_orderTopic_cancel_group";
            String body = new String(messageExt.getBody(), "UTF-8");
            String msgId = messageExt.getMsgId();
            String tags = messageExt.getTags();
            String keys = messageExt.getKeys();
            logger.info("goods CancelOrderProcessor receive message:" + messageExt);

            String messageInfo =
                    "\n========CancelOrderProcessor receive message========\n"
                            + messageExt + "\n"
                            + "========CancelOrderProcessor receive message========\n";
            System.out.println(messageInfo);

            TradeMqConsumerLogKey key = new TradeMqConsumerLogKey();
            key.setGroupName(groupName);
            key.setMsgTag(tags);
            key.setMsgKeys(keys);

            mqConsumerLog = this.tradeMqConsumerLogMapper.selectByPrimaryKey(key);
            if (mqConsumerLog != null) {
                String consumerStatus = mqConsumerLog.getConsumerStatus();
                if (MQEnums.ConsumerStatusEnum.SUCCESS.getStatusCode().equals(consumerStatus)) {
                    //返回成功，重复的处理消息
                    logger.warn("已经处理过，不用再处理了");
                    return true;
                } else if (MQEnums.ConsumerStatusEnum.PROCESSING.getStatusCode().equals(consumerStatus)) {
                    //返回失败，说明有消费者正在处理当中，稍后再试
                    logger.warn("正在处理，稍后再试");
                    return false;
                } else if (MQEnums.ConsumerStatusEnum.FAIL.getStatusCode().equals(consumerStatus)) {
                    if (mqConsumerLog.getConsumerTimes() >= 3) {
                        //让这个消息不在重试，转人工处理
                        logger.warn("超过3次，不在处理");
                        return true;
                    }
                    //更新处理中状态
                    TradeMqConsumerLog updateMqConsumerLog = new TradeMqConsumerLog();
                    updateMqConsumerLog.setGroupName(mqConsumerLog.getGroupName());
                    updateMqConsumerLog.setMsgTag(mqConsumerLog.getMsgTag());
                    updateMqConsumerLog.setMsgKeys(mqConsumerLog.getMsgKeys());
                    updateMqConsumerLog.setConsumerStatus(MQEnums.ConsumerStatusEnum.PROCESSING.getStatusCode());

                    //防止并发
                    TradeMqConsumerLogExample example = new TradeMqConsumerLogExample();
                    example.createCriteria().andGroupNameEqualTo(mqConsumerLog.getGroupName())
                            .andMsgKeysEqualTo(mqConsumerLog.getMsgKeys())
                            .andMsgTagEqualTo(mqConsumerLog.getMsgTag())
                            .andConsumerTimesEqualTo(mqConsumerLog.getConsumerTimes());
                    //乐观锁的方式防止并发更新
                    int i = this.tradeMqConsumerLogMapper.updateByExampleSelective(updateMqConsumerLog, example);
                    if (i <= 0) {
                        logger.warn("并发更新处理状态，一会儿重试");
                        return false;
                    }
                }
            } else {
                //新插入去重表，并发时用主键冲突控制
                try {
                    mqConsumerLog = new TradeMqConsumerLog();
                    mqConsumerLog.setGroupName(groupName);
                    mqConsumerLog.setMsgKeys(keys);
                    mqConsumerLog.setMsgTag(tags);
                    mqConsumerLog.setMsgId(msgId);
                    mqConsumerLog.setConsumerTimes(0);
                    mqConsumerLog.setMsgBody(body);
                    mqConsumerLog.setConsumerStatus(MQEnums.ConsumerStatusEnum.PROCESSING.getStatusCode());
                    this.tradeMqConsumerLogMapper.insertSelective(mqConsumerLog);
                } catch (Exception e) {
                    logger.warn("主键冲突，说明有订阅正在处理，稍后再试");
                    return false;
                }
            }

            //业务逻辑处理
            CancelOrderMQ cancelOrderMQ = JSON.parseObject(body, CancelOrderMQ.class);
            AddGoodsNumberReq addGoodsNumberReq = new AddGoodsNumberReq();
            addGoodsNumberReq.setGoodsId(cancelOrderMQ.getGoodsId());
            addGoodsNumberReq.setGoodsNumber(cancelOrderMQ.getGoodsNumber());
            addGoodsNumberReq.setOrderId(cancelOrderMQ.getOrderId());
            goodsService.addGoodsNumber(addGoodsNumberReq);

            //更新消息处理成功
            TradeMqConsumerLog updateMqConsumerLog = new TradeMqConsumerLog();
            updateMqConsumerLog.setGroupName(mqConsumerLog.getGroupName());
            updateMqConsumerLog.setMsgKeys(mqConsumerLog.getMsgKeys());
            updateMqConsumerLog.setMsgTag(mqConsumerLog.getMsgTag());
            updateMqConsumerLog.setConsumerStatus(MQEnums.ConsumerStatusEnum.SUCCESS.getStatusCode());
            updateMqConsumerLog.setConsumerTimes(mqConsumerLog.getConsumerTimes() + 1);
            this.tradeMqConsumerLogMapper.updateByPrimaryKeySelective(updateMqConsumerLog);

            return true;
        } catch (Exception e) {
            //更新消息处理失败
            TradeMqConsumerLog updateMqConsumerLog = new TradeMqConsumerLog();
            updateMqConsumerLog.setGroupName(mqConsumerLog.getGroupName());
            updateMqConsumerLog.setMsgTag(mqConsumerLog.getMsgTag());
            updateMqConsumerLog.setMsgKeys(mqConsumerLog.getMsgKeys());
            updateMqConsumerLog.setConsumerStatus(MQEnums.ConsumerStatusEnum.FAIL.getStatusCode());
            updateMqConsumerLog.setConsumerTimes(mqConsumerLog.getConsumerTimes() + 1);
            this.tradeMqConsumerLogMapper.updateByPrimaryKeySelective(updateMqConsumerLog);
            return false;
        }
    }
}
