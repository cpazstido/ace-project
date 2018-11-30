package com.ace.trade.common.constants;

public class TradeEnum {
    public enum RetEnum{
        SUCCESS("1","成功"),
        FAIL("-1","失败");
        private String code;
        private String desc;

        RetEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}
