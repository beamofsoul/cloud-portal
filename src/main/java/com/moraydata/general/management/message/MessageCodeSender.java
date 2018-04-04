package com.moraydata.general.management.message;

import java.util.Map;

public interface MessageCodeSender {
	
	boolean send(String phone, Map<String, Object> params) throws Exception;
}
