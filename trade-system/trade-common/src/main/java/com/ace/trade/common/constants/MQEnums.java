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
}
