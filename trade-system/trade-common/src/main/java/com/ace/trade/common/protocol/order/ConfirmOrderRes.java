package com.ace.trade.common.protocol.order;

import com.ace.trade.common.protocol.BaseRes;

public class ConfirmOrderRes extends BaseRes {
    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
