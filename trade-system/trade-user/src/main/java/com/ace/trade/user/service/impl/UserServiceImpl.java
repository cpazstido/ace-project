package com.ace.trade.user.service.impl;

import com.ace.trade.common.constants.TradeEnum;
import com.ace.trade.common.protocol.user.QueryUserReq;
import com.ace.trade.common.protocol.user.QueryUserRes;
import com.ace.trade.entity.TradeUser;
import com.ace.trade.mapper.TradeUserMapper;
import com.ace.trade.user.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private TradeUserMapper tradeUserMapper;

    public QueryUserRes queryUserById(QueryUserReq queryUserReq) {
        QueryUserRes queryUserRes = new QueryUserRes();
        queryUserRes.setRetCode(TradeEnum.RetEnum.SUCCESS.getCode());
        queryUserRes.setRetInfo(TradeEnum.RetEnum.SUCCESS.getDesc());

        try {
            if (queryUserReq == null || queryUserReq.getUserId() == null) {
                throw new RuntimeException("请求参数不正确");
            }
            TradeUser tradeUser = tradeUserMapper.selectByPrimaryKey(queryUserReq.getUserId());
            if (tradeUser!=null){
                BeanUtils.copyProperties(tradeUser,queryUserRes);
            }
        } catch (Exception e) {
            queryUserRes.setRetCode(TradeEnum.RetEnum.FAIL.getCode());
            queryUserRes.setRetInfo(TradeEnum.RetEnum.FAIL.getDesc());
        }
        return queryUserRes;
    }
}
