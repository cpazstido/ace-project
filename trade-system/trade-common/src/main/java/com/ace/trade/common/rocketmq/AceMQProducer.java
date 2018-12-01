package com.ace.trade.common.rocketmq;

import com.ace.trade.common.constants.MQEnums;
import com.ace.trade.common.exception.AceMQException;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AceMQProducer {
    public static Logger LOGGER = LoggerFactory.getLogger(AceMQProducer.class);

    private DefaultMQProducer producer;
    private String groupName;
    private String namesrvAddr;
    private int maxMessageSize = 1024 * 1024 * 4; // 4M
    private int sendMsgTimeout = 10000;

    public void setProducer(DefaultMQProducer producer) {
        this.producer = producer;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public void setMaxMessageSize(int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }

    public void setSendMsgTimeout(int sendMsgTimeout) {
        this.sendMsgTimeout = sendMsgTimeout;
    }

    public void init() throws AceMQException {
        if (StringUtils.isBlank(this.groupName)){
            throw new AceMQException("groupName is blank!");
        }
        if (StringUtils.isBlank(this.namesrvAddr)){
            throw new AceMQException("namesrvAddr is blank!");
        }
        this.producer = new DefaultMQProducer(this.groupName);
        this.producer.setNamesrvAddr(this.namesrvAddr);
        this.producer.setMaxMessageSize(this.maxMessageSize);
        this.producer.setSendMsgTimeout(this.sendMsgTimeout);
        try {
            this.producer.start();
            LOGGER.info(String.format("producer is start!groupName:[%s],namesrvAddr[$s]",this.groupName,this.namesrvAddr));
        } catch (MQClientException e) {
            LOGGER.error(String.format("producer error!groupName:[%s],namesrvAddr[$s]",this.groupName,this.namesrvAddr),e);
            throw new AceMQException(e);
        }
    }

    public SendResult sendMessage(String topic, String tags, String keys, String messageText) throws AceMQException {
        if(StringUtils.isBlank(topic)){
            throw new AceMQException("topic is blank!");
        }
        if(StringUtils.isBlank(messageText)){
            throw new AceMQException("messageText is blank!");
        }
        Message message = new Message(topic,tags,keys,messageText.getBytes());

        try {
            SendResult sendResult = this.producer.send(message);
            return sendResult;
        } catch (Exception e) {
            LOGGER.error("send message error:\ntopic:{}\ntags:{}\nkeys:{}\nmessage:{}\n",
                    topic,
                    tags,
                    keys,
                    messageText,
                    e.getMessage(),e);
            throw new AceMQException(e);
        }
    }

    public SendResult sendMessage(MQEnums.TopicEnum topicEnum,String keys, String messageText) throws AceMQException {
        return this.sendMessage(topicEnum.getTopic(),topicEnum.getTag(),keys,messageText);
    }
}
