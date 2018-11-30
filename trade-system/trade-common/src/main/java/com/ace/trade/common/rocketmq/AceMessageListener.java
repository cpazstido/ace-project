package com.ace.trade.common.rocketmq;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class AceMessageListener implements MessageListenerConcurrently {

    private IMessageProcessor messageProcessor;

    public void setMessageProcessor(IMessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        for (MessageExt messageExt:msgs){
            boolean result = messageProcessor.handleMessage(messageExt);
            if(!result){
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
