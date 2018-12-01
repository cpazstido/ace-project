package com.ace.trade.order.service;

import com.ace.trade.common.protocol.order.ConfirmOrderReq;
import com.ace.trade.common.protocol.order.ConfirmOrderRes;

public interface IOrderService {
    ConfirmOrderRes confirmOrder(ConfirmOrderReq confirmOrderReq);
}
