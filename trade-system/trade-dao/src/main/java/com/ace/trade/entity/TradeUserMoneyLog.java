package com.ace.trade.entity;

import java.math.BigDecimal;

public class TradeUserMoneyLog extends TradeUserMoneyLogKey {
    private BigDecimal userMoney;

    private BigDecimal createTime;

    public BigDecimal getUserMoney() {
        return userMoney;
    }

    public void setUserMoney(BigDecimal userMoney) {
        this.userMoney = userMoney;
    }

    public BigDecimal getCreateTime() {
        return createTime;
    }

    public void setCreateTime(BigDecimal createTime) {
        this.createTime = createTime;
    }
}