package com.moraydata.general.management.util;

import java.util.UUID;

public class InvitationCodeUtils {

	public static String generate() {
		return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
	}
}
