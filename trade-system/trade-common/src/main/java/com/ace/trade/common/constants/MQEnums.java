package com.ace.trade.common.constants;

public class MQEnums {
    public enum TopicEnum{
        ORDER_CONFIRM("orderTopic","confirm"),
        ORDER_CANCEL("orderTopic","cancel"),
        PAY_PAID("orderTopic","paid"),
        ;
        private String topic;
        private String tag;

        public String getTopic() {
            return topic;
        }

        public String getTag() {
            return tag;
        }

        TopicEnum(String topic, String tag) {
            this.topic = topic;
            this.tag = tag;
        }
    }

    public enum ConsumerStatusEnum{
        PROCESSING("0","正在处理"),
        SUCCESS("1","处理成功"),
        FAIL("2","处理失败"),
        ;
        private String statusCode;
        private String desc;

        ConsumerStatusEnum(String statusCode, String desc) {
            this.statusCode = statusCode;
            this.desc = desc;
        }

        public String getStatusCode() {
            return statusCode;
        }

        public String getDesc() {
            return desc;
        }
    }
}
