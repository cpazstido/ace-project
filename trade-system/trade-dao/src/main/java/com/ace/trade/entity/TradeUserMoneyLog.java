package com.ace.trade.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class TradeUserMoneyLog extends TradeUserMoneyLogKey implements Serializable {
    private BigDecimal userMoney;

    private BigDecimal createTime;

    private static final long serialVersionUID = 1L;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", userMoney=").append(userMoney);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}