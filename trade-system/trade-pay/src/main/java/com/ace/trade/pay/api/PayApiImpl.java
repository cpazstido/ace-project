package com.ace.trade.pay.api;

import com.ace.trade.common.api.IPayApi;
import com.ace.trade.common.constants.TradeEnum;
import com.ace.trade.common.protocol.goods.ReduceGoodsNumberRes;
import com.ace.trade.common.protocol.pay.CallbackPaymentReq;
import com.ace.trade.common.protocol.pay.CallbackPaymentRes;
import com.ace.trade.common.protocol.pay.CreatePaymentReq;
import com.ace.trade.common.protocol.pay.CreatePaymentRes;
import com.ace.trade.pay.service.IPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PayApiImpl implements IPayApi {
    @Autowired
    private IPayService payService;

    @RequestMapping(value = "/createPayment",method = RequestMethod.POST)
    @ResponseBody
    public CreatePaymentRes createPayment(@RequestBody CreatePaymentReq createPaymentReq) {
        return payService.createPayment(createPaymentReq);
    }

    @RequestMapping(value = "/callbackPayment",method = RequestMethod.POST)
    @ResponseBody
    public CallbackPaymentRes callbackPayment(@RequestBody CallbackPaymentReq callbackPaymentReq) {
        CallbackPaymentRes callbackPaymentRes = new CallbackPaymentRes();
        try {
            callbackPaymentRes = payService.callbackPayment(callbackPaymentReq);
        } catch (Exception e) {
            callbackPaymentRes.setRetCode(TradeEnum.RetEnum.FAIL.getCode());
            callbackPaymentRes.setRetInfo(e.getMessage());
        }
        return callbackPaymentRes;
    }
}
