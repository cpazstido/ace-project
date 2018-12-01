package com.ace.trade.common.constants;

public class TradeEnum {
    public enum RestServerEnum {
        ORDER("localhost","order",8080),
        PAY("localhost","pay",8081),
        COUPON("localhost","coupon",8082),
        GOODS("localhost","goods",8083),
        USER("localhost","user",8084)
        ;
        private String serverHost;
        private int serverPort;
        private String contextPath;

        public String getServerHost() {
            return serverHost;
        }

        public int getServerPort() {
            return serverPort;
        }

        public String getContextPath() {
            return contextPath;
        }

        public String getServerUrl(){
            return "http://"+this.serverHost+":"+this.serverPort+"/"+this.contextPath+"/";
        }

        RestServerEnum(String serverHost, String contextPath, int serverPort) {
            this.serverHost = serverHost;
            this.serverPort = serverPort;
            this.contextPath = contextPath;
        }
    }

    public enum RetEnum {
        SUCCESS("1", "成功"),
        FAIL("-1", "失败");
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
