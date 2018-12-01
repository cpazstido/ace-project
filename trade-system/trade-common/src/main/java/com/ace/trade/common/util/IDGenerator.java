package com.ace.trade.common.util;

import java.util.UUID;

public class IDGenerator {
    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
