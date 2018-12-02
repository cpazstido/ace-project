package com.ace.trade.coupon.service;

import com.ace.trade.common.protocol.coupon.ChangeCouponStatusReq;
import com.ace.trade.common.protocol.coupon.ChangeCouponStatusRes;
import com.ace.trade.common.protocol.coupon.QueryCouponReq;
import com.ace.trade.common.protocol.coupon.QueryCouponRes;

public interface ICouponService {
    QueryCouponRes queryCoupon(QueryCouponReq queryCouponReq);
    ChangeCouponStatusRes changeCouponStatus(ChangeCouponStatusReq changeCouponStatusReq);
}
