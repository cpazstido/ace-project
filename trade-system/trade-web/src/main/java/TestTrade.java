import com.ace.trade.common.api.*;
import com.ace.trade.common.protocol.coupon.QueryCouponReq;
import com.ace.trade.common.protocol.coupon.QueryCouponRes;
import com.ace.trade.common.protocol.goods.QueryGoodsReq;
import com.ace.trade.common.protocol.goods.QueryGoodsRes;
import com.ace.trade.common.protocol.order.ConfirmOrderReq;
import com.ace.trade.common.protocol.order.ConfirmOrderRes;
import com.ace.trade.common.protocol.user.QueryUserReq;
import com.ace.trade.common.protocol.user.QueryUserRes;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:xml/spring-rest-client.xml"})
public class TestTrade {
    @Autowired
    private IGoodsApi goodsApi;
    @Autowired
    private ICouponApi couponApi;
    @Autowired
    private IUserApi userApi;
    @Autowired
    private IPayApi payApi;
    @Autowired
    private IOrderApi orderApi;

    @Test
    public void testUser(){
        QueryUserReq queryUserReq = new QueryUserReq();
        queryUserReq.setUserId(1);
        QueryUserRes queryUserRes = userApi.queryUserById(queryUserReq);
        System.out.println(JSON.toJSON(queryUserRes));
    }

    @Test
    public void testGoods(){
        QueryGoodsReq queryGoodsReq = new QueryGoodsReq();
        queryGoodsReq.setGoodsId(10000);
        QueryGoodsRes queryGoodsRes = goodsApi.queryGoods(queryGoodsReq);
        System.out.println(JSON.toJSON(queryGoodsRes));
    }

    @Test
    public void testCoupon(){
        QueryCouponReq queryCouponReq = new QueryCouponReq();
        queryCouponReq.setCouponId("123456789");
        QueryCouponRes queryCouponRes = couponApi.queryCoupon(queryCouponReq);
        System.out.println(JSON.toJSON(queryCouponRes));
    }

    @Test
    public void testConfirmOrder(){
        ConfirmOrderReq confirmOrderReq = new ConfirmOrderReq();
        confirmOrderReq.setGoodsId(10000);
        confirmOrderReq.setUserId(1);
        confirmOrderReq.setGoodsNumber(1);
        confirmOrderReq.setAddress("北京");
        confirmOrderReq.setGoodsPrice(new BigDecimal("5000"));
        confirmOrderReq.setOrderAmount(new BigDecimal("5000"));
        confirmOrderReq.setMoneyPaid(new BigDecimal("100"));
        confirmOrderReq.setCouponId("123456789");
        ConfirmOrderRes confirmOrderRes = orderApi.confirmOrder(confirmOrderReq);
        System.out.println(JSON.toJSON(confirmOrderRes));
    }

}
