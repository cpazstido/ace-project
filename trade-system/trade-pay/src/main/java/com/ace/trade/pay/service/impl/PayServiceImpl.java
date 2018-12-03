package com.ace.trade.pay.service.impl;

import com.ace.trade.common.constants.MQEnums;
import com.ace.trade.common.constants.TradeEnum;
import com.ace.trade.common.exception.AceMQException;
import com.ace.trade.common.protocol.pay.CallbackPaymentReq;
import com.ace.trade.common.protocol.pay.CallbackPaymentRes;
import com.ace.trade.common.protocol.pay.CreatePaymentReq;
import com.ace.trade.common.protocol.pay.CreatePaymentRes;
import com.ace.trade.common.rocketmq.AceMQProducer;
import com.ace.trade.common.rocketmq.PaidMQ;
import com.ace.trade.common.util.IDGenerator;
import com.ace.trade.entity.TradeMqProducerTemp;
import com.ace.trade.entity.TradePay;
import com.ace.trade.entity.TradePayExample;
import com.ace.trade.mapper.TradeMqProducerTempMapper;
import com.ace.trade.mapper.TradePayMapper;
import com.ace.trade.pay.service.IPayService;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class PayServiceImpl implements IPayService {
    @Autowired
    private TradePayMapper tradePayMapper;

    @Autowired
    private TradeMqProducerTempMapper tradeMqProducerTempMapper;

    @Autowired
    private AceMQProducer aceMQProducer;
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    public CreatePaymentRes createPayment(CreatePaymentReq createPaymentReq) {
        CreatePaymentRes createPaymentRes = new CreatePaymentRes();
        createPaymentRes.setRetCode(TradeEnum.RetEnum.SUCCESS.getCode());
        createPaymentRes.setRetInfo(TradeEnum.RetEnum.SUCCESS.getDesc());

        try {
            TradePayExample payExample = new TradePayExample();
            payExample.createCriteria().andOrderIdEqualTo(createPaymentReq.getOrderId())
                    .andIsPaidEqualTo(TradeEnum.YesNoEnum.YES.getCode());
            int count = this.tradePayMapper.countByExample(payExample);
            if (count > 0) {
                throw new Exception("该订单已支付");
            }
            String payId = IDGenerator.generateUUID();
            TradePay tradePay = new TradePay();
            tradePay.setPayId(payId);
            tradePay.setOrderId(createPaymentReq.getOrderId());
            tradePay.setIsPaid(TradeEnum.YesNoEnum.NO.getCode());
            tradePay.setPayAmount(createPaymentReq.getPayAmount());
            tradePayMapper.insert(tradePay);
            System.out.println("创建支付订单成功：" + payId);
        } catch (Exception e) {
            createPaymentRes.setRetCode(TradeEnum.RetEnum.FAIL.getCode());
            createPaymentRes.setRetInfo("创建支付订单失败：" + e.getMessage());
        }
        return createPaymentRes;
    }

    @Transactional
    public CallbackPaymentRes callbackPayment(CallbackPaymentReq callbackPaymentReq) {
        CallbackPaymentRes callbackPaymentRes = new CallbackPaymentRes();
        callbackPaymentRes.setRetCode(TradeEnum.RetEnum.SUCCESS.getCode());
        callbackPaymentRes.setRetInfo(TradeEnum.RetEnum.SUCCESS.getDesc());
        if (callbackPaymentReq.getIsPaid().equals(TradeEnum.YesNoEnum.YES.getCode())) {
            //更新支付状态
            TradePay tradePay = tradePayMapper.selectByPrimaryKey(callbackPaymentReq.getPayId());
            if (tradePay == null) {
                throw new RuntimeException("未找到支付单");
            }

            if (tradePay.getIsPaid().equals(TradeEnum.YesNoEnum.YES.getCode())) {
                throw new RuntimeException("该支付单已支付");
            }

            tradePay.setIsPaid(TradeEnum.YesNoEnum.YES.getCode());
            int i = tradePayMapper.updateByPrimaryKeySelective(tradePay);
            //发送可靠消息
            if (i == 1) {
                final PaidMQ paidMQ = new PaidMQ();
                paidMQ.setPayAmount(tradePay.getPayAmount());
                paidMQ.setOrderId(tradePay.getOrderId());
                paidMQ.setPayId(tradePay.getPayId());
                final TradeMqProducerTemp mqProducerTemp = new TradeMqProducerTemp();
                mqProducerTemp.setId(IDGenerator.generateUUID());
                mqProducerTemp.setGroupName("payProducerGroup");
                mqProducerTemp.setMsgKeys(tradePay.getPayId());
                mqProducerTemp.setMsgTag(MQEnums.TopicEnum.PAY_PAID.getTag());
                mqProducerTemp.setMsgBody(JSON.toJSONString(paidMQ));
                mqProducerTemp.setCreateTime(new Date());
                tradeMqProducerTempMapper.insert(mqProducerTemp);
                //异步发送mq消息通知库存系统发货
                //发送成功清空发送表;
                //发送失败定时任务重新发送并清空发送表
                executorService.submit(new Runnable() {
                    public void run() {
                        try {
                            SendResult sendResult = aceMQProducer.sendMessage(MQEnums.TopicEnum.PAY_PAID, paidMQ.getPayId(), JSON.toJSONString(paidMQ));
                            System.out.println(sendResult);
                            if (sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
                                tradeMqProducerTempMapper.deleteByPrimaryKey(mqProducerTemp.getId());
                                System.out.println("删除消息表成功");
                            }
                        } catch (AceMQException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                throw new RuntimeException("该支付单已支付");
            }
        }
        return callbackPaymentRes;
    }
}
