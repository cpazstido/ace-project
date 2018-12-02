package com.ace.trade.user.service.impl;

import com.ace.trade.common.constants.TradeEnum;
import com.ace.trade.common.protocol.user.ChangeUserMoneyReq;
import com.ace.trade.common.protocol.user.ChangeUserMoneyRes;
import com.ace.trade.common.protocol.user.QueryUserReq;
import com.ace.trade.common.protocol.user.QueryUserRes;
import com.ace.trade.entity.TradeUser;
import com.ace.trade.entity.TradeUserMoneyLog;
import com.ace.trade.entity.TradeUserMoneyLogExample;
import com.ace.trade.mapper.TradeUserMapper;
import com.ace.trade.mapper.TradeUserMoneyLogMapper;
import com.ace.trade.user.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private TradeUserMapper tradeUserMapper;

    @Autowired
    private TradeUserMoneyLogMapper tradeUserMoneyLogMapper;

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


    public ChangeUserMoneyRes changeUserMoney(ChangeUserMoneyReq changeUserMoneyReq) {
        ChangeUserMoneyRes changeUserMoneyRes = new ChangeUserMoneyRes();
        changeUserMoneyRes.setRetCode(TradeEnum.RetEnum.SUCCESS.getCode());
        changeUserMoneyRes.setRetInfo(TradeEnum.RetEnum.SUCCESS.getDesc());

        if(changeUserMoneyReq ==null||changeUserMoneyReq.getUserId()==null||changeUserMoneyReq.getUserMoney()==null){
            throw new RuntimeException("请求参数不正确");
        }
        if(changeUserMoneyReq.getUserMoney().compareTo(BigDecimal.ZERO)<=0){
            throw new RuntimeException("金额不能小于0");
        }

        TradeUserMoneyLog tradeUserMoneyLog = new TradeUserMoneyLog();
        tradeUserMoneyLog.setOrderId(changeUserMoneyReq.getOrderId());
        tradeUserMoneyLog.setUserId(changeUserMoneyReq.getUserId());
        tradeUserMoneyLog.setUserMoney(changeUserMoneyReq.getUserMoney());
        tradeUserMoneyLog.setCreateTime(new Date());
        tradeUserMoneyLog.setMoneyLogType(changeUserMoneyReq.getMoneyLogType());

        TradeUser tradeUser = new TradeUser();
        tradeUser.setUserId(changeUserMoneyReq.getUserId());
        tradeUser.setUserMoney(changeUserMoneyReq.getUserMoney());

        //查询是否有付款记录
        TradeUserMoneyLogExample logExample = new TradeUserMoneyLogExample();
        logExample.createCriteria()
                .andUserIdEqualTo(changeUserMoneyReq.getUserId())
                .andOrderIdEqualTo(changeUserMoneyReq.getOrderId())
                .andMoneyLogTypeEqualTo(TradeEnum.UserMoneyLogTypeEnum.PAID.getCode());
        int count = this.tradeUserMoneyLogMapper.countByExample(logExample);

        //订单付款
        if(changeUserMoneyReq.getMoneyLogType().equals(TradeEnum.UserMoneyLogTypeEnum.PAID.getCode())){
            if(count>0){
                throw new RuntimeException("已经付过款，不能再付款");
            }
            tradeUserMapper.reduceUserMoney(tradeUser);
        }

        //订单退款
        if(changeUserMoneyReq.getMoneyLogType().equals(TradeEnum.UserMoneyLogTypeEnum.REFUND.getCode())){

            if(count ==0){
                throw new RuntimeException("没有付款信息，不能退款");
            }
            //防止多次退款
            logExample = new TradeUserMoneyLogExample();
            logExample.createCriteria()
                    .andUserIdEqualTo(changeUserMoneyReq.getUserId())
                    .andOrderIdEqualTo(changeUserMoneyReq.getOrderId())
                    .andMoneyLogTypeEqualTo(TradeEnum.UserMoneyLogTypeEnum.REFUND.getCode());
            count = this.tradeUserMoneyLogMapper.countByExample(logExample);
            if(count>0){
                throw new RuntimeException("已经退过款了，不能退款");
            }
            tradeUserMapper.addUserMoney(tradeUser);
        }
        this.tradeUserMoneyLogMapper.insert(tradeUserMoneyLog);

        return changeUserMoneyRes;
    }
}
