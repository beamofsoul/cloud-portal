package com.moraydata.general.management.message;

public interface MessageCodeSender {
	
	boolean send(String phone, int code, String content) throws Exception;
}
