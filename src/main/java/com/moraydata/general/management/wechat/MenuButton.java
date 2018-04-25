package com.moraydata.general.management.wechat;

import java.util.HashMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 微信菜单基类
 * @author Mingshu Jian  
 * @date 2018年4月24日
 */
@Getter
@Setter
public class MenuButton {
	
	private String name; // 菜单标题，不超过16个字节，子菜单不超过60个字节
	private String type; // 菜单的响应动作类型，view表示网页类型，click表示点击类型，miniprogram表示小程序类型
	
	@RequiredArgsConstructor(access=AccessLevel.PROTECTED)
	public static enum Type {
		VIEW("view"), CLICK("click"), MINI_PROGRAM("miniprogram");
		@Getter private final String value;
		private static HashMap<String, Type> codeValueMap = new HashMap<>(3);
		static {
			for (Type type : Type.values()) {
				codeValueMap.put(type.value, type);
			}
		}
		public static Type getInstance(String code) {
			return codeValueMap.get(code);
		}
	}
}
