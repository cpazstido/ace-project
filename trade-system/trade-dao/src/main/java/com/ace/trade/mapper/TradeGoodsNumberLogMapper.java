package com.ace.trade.mapper;

import com.ace.trade.entity.TradeGoodsNumberLog;
import com.ace.trade.entity.TradeGoodsNumberLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TradeGoodsNumberLogMapper {
    int countByExample(TradeGoodsNumberLogExample example);

    int deleteByExample(TradeGoodsNumberLogExample example);

    int insert(TradeGoodsNumberLog record);

    int insertSelective(TradeGoodsNumberLog record);

    List<TradeGoodsNumberLog> selectByExample(TradeGoodsNumberLogExample example);

    int updateByExampleSelective(@Param("record") TradeGoodsNumberLog record, @Param("example") TradeGoodsNumberLogExample example);

    int updateByExample(@Param("record") TradeGoodsNumberLog record, @Param("example") TradeGoodsNumberLogExample example);
}