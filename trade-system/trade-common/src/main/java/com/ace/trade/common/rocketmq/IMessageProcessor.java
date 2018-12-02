package com.ace.trade.common.rocketmq;

import com.alibaba.rocketmq.common.message.MessageExt;

public interface IMessageProcessor {
    /**
     * 处理消息
     * @param messageExt
     * @return
     */
    boolean handleMessage(MessageExt messageExt);
}
