package com.bootdo.util;

import com.server;
import org.springframework.stereotype.Component;

@Component
public class MsgUtil {

    public static String getMsg(String code){
        return server.ac.getBean(MessagesConfig.class).getMap().get(code);
    }
}
