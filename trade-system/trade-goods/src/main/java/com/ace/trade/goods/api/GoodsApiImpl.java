package com.ace.trade.goods.api;

import com.ace.trade.common.api.IGoodsApi;
import com.ace.trade.common.constants.TradeEnum;
import com.ace.trade.common.protocol.goods.QueryGoodsReq;
import com.ace.trade.common.protocol.goods.QueryGoodsRes;
import com.ace.trade.common.protocol.goods.ReduceGoodsNumberReq;
import com.ace.trade.common.protocol.goods.ReduceGoodsNumberRes;
import com.ace.trade.goods.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GoodsApiImpl implements IGoodsApi {
    @Autowired
    private IGoodsService goodsService;
    @RequestMapping(value = "/queryGoods",method = RequestMethod.POST)
    @ResponseBody
    public QueryGoodsRes queryGoods(@RequestBody QueryGoodsReq queryGoodsReq) {
        return goodsService.queryGoods(queryGoodsReq);
    }

    @RequestMapping(value = "/reduceGoodsNumber",method = RequestMethod.POST)
    @ResponseBody
    public ReduceGoodsNumberRes reduceGoodsNumber(@RequestBody ReduceGoodsNumberReq reduceGoodsNumberReq) {
        ReduceGoodsNumberRes reduceGoodsNumberRes = new ReduceGoodsNumberRes();
        try {
            reduceGoodsNumberRes = goodsService.reduceGoodsNumber(reduceGoodsNumberReq);
        } catch (Exception e) {
            reduceGoodsNumberRes.setRetCode(TradeEnum.RetEnum.FAIL.getCode());
            reduceGoodsNumberRes.setRetInfo(e.getMessage());
        }
        return reduceGoodsNumberRes;
    }
}
