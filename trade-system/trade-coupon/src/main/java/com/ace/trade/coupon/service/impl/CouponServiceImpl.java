package com.ace.trade.coupon.service.impl;

import com.ace.trade.common.constants.TradeEnum;
import com.ace.trade.common.protocol.coupon.ChangeCouponStatusReq;
import com.ace.trade.common.protocol.coupon.ChangeCouponStatusRes;
import com.ace.trade.common.protocol.coupon.QueryCouponReq;
import com.ace.trade.common.protocol.coupon.QueryCouponRes;
import com.ace.trade.coupon.service.ICouponService;
import com.ace.trade.entity.TradeCoupon;
import com.ace.trade.mapper.TradeCouponMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CouponServiceImpl implements ICouponService {
    @Autowired
    private TradeCouponMapper tradeCouponMapper;

    public QueryCouponRes queryCoupon(QueryCouponReq queryCouponReq) {
        QueryCouponRes queryCouponRes = new QueryCouponRes();
        queryCouponRes.setRetCode(TradeEnum.RetEnum.SUCCESS.getCode());
        queryCouponRes.setRetInfo(TradeEnum.RetEnum.SUCCESS.getDesc());

        try {
            if (queryCouponReq == null || StringUtils.isBlank(queryCouponReq.getCouponId())) {
                throw new Exception("请求参数不正确，优惠券编号为空");
            }
            TradeCoupon tradeCoupon = this.tradeCouponMapper.selectByPrimaryKey(queryCouponReq.getCouponId());
            if (tradeCoupon != null) {
                BeanUtils.copyProperties(tradeCoupon, queryCouponRes);
            } else {
                throw new Exception("未查询到改优惠券");
            }
        } catch (Exception e) {
            queryCouponRes.setRetCode(TradeEnum.RetEnum.FAIL.getCode());
            queryCouponRes.setRetInfo(e.getMessage());
        }
        return queryCouponRes;
    }

    public ChangeCouponStatusRes changeCouponStatus(ChangeCouponStatusReq changeCouponStatusReq) {
        ChangeCouponStatusRes changeCouponStatusRes = new ChangeCouponStatusRes();
        changeCouponStatusRes.setRetCode(TradeEnum.RetEnum.SUCCESS.getCode());
        changeCouponStatusRes.setRetInfo(TradeEnum.RetEnum.SUCCESS.getDesc());

        try {
            if (changeCouponStatusReq == null || StringUtils.isBlank(changeCouponStatusReq.getCouponId())) {
                throw new Exception("请求参数不正确，优惠券编号为空");
            }

            //使用优惠券
            TradeCoupon record = new TradeCoupon();
            record.setCouponId(changeCouponStatusReq.getCouponId());
            record.setOrderId(changeCouponStatusReq.getOrderId());

            if (changeCouponStatusReq.getIsUsed().equals(TradeEnum.YesNoEnum.YES.getCode())) {
                //使用优惠券
                int i = this.tradeCouponMapper.useCoupon(record);
                if (i <= 0) {
                    throw new Exception("使用该优惠券失败");
                }
            } else if (changeCouponStatusReq.getIsUsed().equals(TradeEnum.YesNoEnum.NO.getCode())) {
                //退款时退优惠券
                this.tradeCouponMapper.unUseCoupon(record);
            }

        } catch (Exception e) {
            changeCouponStatusRes.setRetCode(TradeEnum.RetEnum.FAIL.getCode());
            changeCouponStatusRes.setRetInfo(e.getMessage());
        }
        return changeCouponStatusRes;
    }
}
