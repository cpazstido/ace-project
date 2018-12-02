package com.ace.trade.coupon.api;

import com.ace.trade.common.api.ICouponApi;
import com.ace.trade.common.protocol.coupon.ChangeCouponStatusReq;
import com.ace.trade.common.protocol.coupon.ChangeCouponStatusRes;
import com.ace.trade.common.protocol.coupon.QueryCouponReq;
import com.ace.trade.common.protocol.coupon.QueryCouponRes;
import com.ace.trade.coupon.service.ICouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CouponApiImpl implements ICouponApi {
    @Autowired
    private ICouponService couponService;

    @RequestMapping(value = "/queryCoupon",method = RequestMethod.POST)
    @ResponseBody
    public QueryCouponRes queryCoupon(@RequestBody QueryCouponReq queryCouponReq) {
        return this.couponService.queryCoupon(queryCouponReq);
    }

    @RequestMapping(value = "/changeCouponStatus",method = RequestMethod.POST)
    @ResponseBody
    public ChangeCouponStatusRes changeCouponStatus(@RequestBody ChangeCouponStatusReq changeCouponStatusReq) {
        return this.couponService.changeCouponStatus(changeCouponStatusReq);
    }
}
