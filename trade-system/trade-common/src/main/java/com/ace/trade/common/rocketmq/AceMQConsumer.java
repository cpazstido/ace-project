package com.ace.trade.common.rocketmq;

import com.ace.trade.common.exception.AceMQException;
import org.apache.commons.lang3.StringUtils;
import com.alibaba.rocketmq.client.consumer.DefaultMQPullConsumer;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AceMQConsumer {
    public static Logger logger = LoggerFactory.getLogger(AceMQConsumer.class);

    private String groupName;
    private String namesrvAddr;
    private String topic;
    private String tag = "*";//多个tag以||划分
    private int consumeThreadMin = 20;
    private int consumeThreadMax = 64;
    private IMessageProcessor processor;

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setConsumeThreadMin(int consumeThreadMin) {
        this.consumeThreadMin = consumeThreadMin;
    }

    public void setConsumeThreadMax(int consumeThreadMax) {
        this.consumeThreadMax = consumeThreadMax;
    }

    public void setProcessor(IMessageProcessor processor) {
        this.processor = processor;
    }

    public void init() throws AceMQException {
        if(StringUtils.isBlank(this.groupName)){
            throw new AceMQException("groupName is blank!");
        }

        if(StringUtils.isBlank(this.topic)){
            throw new AceMQException("topic is blank!");
        }

        if(StringUtils.isBlank(this.namesrvAddr)){
            throw new AceMQException("namesrvAddr is blank!");
        }

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(this.groupName);
        consumer.setNamesrvAddr(this.namesrvAddr);
        consumer.setConsumeThreadMin(this.consumeThreadMin);
        consumer.setConsumeThreadMax(this.consumeThreadMax);
        try {
            consumer.subscribe(topic,tag);
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

            AceMessageListener listener = new AceMessageListener();
            listener.setMessageProcessor(processor);
            consumer.setMessageListener(listener);
            consumer.start();
            logger.info("consumer is start!groupName:{},namesrvAddr:{}",this.groupName,this.namesrvAddr);logger.info("consumer is start!groupName:{},namesrvAddr:{}",this.groupName,this.namesrvAddr);
        } catch (MQClientException e) {
            logger.error("consumer is error!groupName:{},namesrvAddr:{}",this.groupName,this.namesrvAddr);logger.info("consumer is start!groupName:{},namesrvAddr:{}",this.groupName,this.namesrvAddr,e);
            throw new AceMQException(e);
        }
    }
}
