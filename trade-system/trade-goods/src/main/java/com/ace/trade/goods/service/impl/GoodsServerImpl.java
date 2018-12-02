package com.ace.trade.goods.service.impl;

import com.ace.trade.common.constants.TradeEnum;
import com.ace.trade.common.protocol.goods.*;
import com.ace.trade.entity.TradeGoods;
import com.ace.trade.entity.TradeGoodsNumberLog;
import com.ace.trade.entity.TradeGoodsNumberLogKey;
import com.ace.trade.goods.service.IGoodsService;
import com.ace.trade.mapper.TradeGoodsMapper;
import com.ace.trade.mapper.TradeGoodsNumberLogMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class GoodsServerImpl implements IGoodsService {
    @Autowired
    private TradeGoodsMapper tradeGoodsMapper;

    @Autowired
    private TradeGoodsNumberLogMapper tradeGoodsNumberLogMapper;

    public QueryGoodsRes queryGoods(QueryGoodsReq queryGoodsReq) {
        QueryGoodsRes queryGoodsRes = new QueryGoodsRes();
        queryGoodsRes.setRetCode(TradeEnum.RetEnum.SUCCESS.getCode());
        queryGoodsRes.setRetInfo(TradeEnum.RetEnum.SUCCESS.getDesc());
        try {
            if (queryGoodsReq == null || queryGoodsReq.getGoodsId() == null) {
                throw new Exception("查询商品信息ID不正确");
            }
            TradeGoods tradeGoods = this.tradeGoodsMapper.selectByPrimaryKey(queryGoodsReq.getGoodsId());

            if (tradeGoods != null) {
                BeanUtils.copyProperties(tradeGoods, queryGoodsRes);
            } else {
                throw new Exception("未查询到商品");
            }
        } catch (Exception e) {
            queryGoodsRes.setRetCode(TradeEnum.RetEnum.FAIL.getCode());
            queryGoodsRes.setRetInfo(e.getMessage());
        }
        return queryGoodsRes;
    }

    public ReduceGoodsNumberRes reduceGoodsNumber(ReduceGoodsNumberReq reduceGoodsNumberReq) {
        ReduceGoodsNumberRes reduceGoodsNumberRes = new ReduceGoodsNumberRes();
        reduceGoodsNumberRes.setRetCode(TradeEnum.RetEnum.SUCCESS.getCode());
        reduceGoodsNumberRes.setRetInfo(TradeEnum.RetEnum.SUCCESS.getDesc());

        if (reduceGoodsNumberReq == null || reduceGoodsNumberReq.getGoodsId() == null
                || reduceGoodsNumberReq.getGoodsNumber() == null || reduceGoodsNumberReq.getGoodsNumber() <= 0) {
            throw new RuntimeException("扣减库存请求参数不正确");
        }
        TradeGoods tradeGoods = new TradeGoods();
        tradeGoods.setGoodsId(reduceGoodsNumberReq.getGoodsId());
        tradeGoods.setGoodsNumber(reduceGoodsNumberReq.getGoodsNumber());
        int i = this.tradeGoodsMapper.reduceGoodsNumber(tradeGoods);
        if (i <= 0) {
            throw new RuntimeException("扣减库存失败");
        }

        TradeGoodsNumberLog tradeGoodsNumberLog = new TradeGoodsNumberLog();
        tradeGoodsNumberLog.setGoodsId(reduceGoodsNumberReq.getGoodsId());
        tradeGoodsNumberLog.setGoodsNumber(reduceGoodsNumberReq.getGoodsNumber());
        tradeGoodsNumberLog.setOrderId(reduceGoodsNumberReq.getOrderId());
        tradeGoodsNumberLog.setLogTime(new Date());
        this.tradeGoodsNumberLogMapper.insert(tradeGoodsNumberLog);

        return reduceGoodsNumberRes;
    }

    public AddGoodsNumberRes addGoodsNumber(AddGoodsNumberReq addGoodsNumberReq) {
        AddGoodsNumberRes addGoodsNumberRes = new AddGoodsNumberRes();
        addGoodsNumberRes.setRetCode(TradeEnum.RetEnum.SUCCESS.getCode());
        addGoodsNumberRes.setRetInfo(TradeEnum.RetEnum.SUCCESS.getDesc());
        try {
            if (addGoodsNumberReq == null || addGoodsNumberReq.getGoodsId() == null
                    || addGoodsNumberReq.getGoodsNumber() == null || addGoodsNumberReq.getGoodsNumber() <= 0) {
                throw new RuntimeException("增加库存请求参数不正确");
            }
            if(addGoodsNumberReq.getOrderId()!=null){
                TradeGoodsNumberLogKey key = new TradeGoodsNumberLogKey();
                key.setGoodsId(addGoodsNumberReq.getGoodsId());
                key.setOrderId(addGoodsNumberReq.getOrderId());

                TradeGoodsNumberLog tradeGoodsNumberLog = this.tradeGoodsNumberLogMapper.selectByPrimaryKey(key);
                if(tradeGoodsNumberLog==null){
                    throw new Exception("未涨到扣库存记录");
                }
            }
            TradeGoods tradeGoods = new TradeGoods();
            tradeGoods.setGoodsId(addGoodsNumberReq.getGoodsId());
            tradeGoods.setGoodsNumber(addGoodsNumberReq.getGoodsNumber());
            int i = this.tradeGoodsMapper.addGoodsNumber(tradeGoods);
            if(i<=0){
                throw new Exception("增加库存失败");
            }
        } catch (Exception e) {
            addGoodsNumberRes.setRetCode(TradeEnum.RetEnum.FAIL.getCode());
            addGoodsNumberRes.setRetInfo(e.getMessage());
        }

        return addGoodsNumberRes;
    }
}
