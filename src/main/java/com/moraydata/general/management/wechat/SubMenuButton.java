package com.moraydata.general.management.wechat;

import lombok.Getter;
import lombok.Setter;

/**
 * 子菜单
 * @author MingshuJian  
 * @date 2018年4月24日
 */
@Getter
@Setter
public class SubMenuButton extends MenuButton {

	private String key; // click等点击类型必须 菜单KEY值，用于消息接口推送，不超过128字节
	private String url; // view、miniprogram类型必须 网页链接，用户点击菜单可打开链接，不超过1024字节。 type为miniprogram时，不支持小程序的老版本客户端将打开本url。
	private String media_id; // media_id类型和view_limited类型必须 调用新增永久素材接口返回的合法media_id
	private String appid; // miniprogram类型必须 小程序的appid（仅认证公众号可配置）
	private String pagepath; // miniprogram类型必须 小程序的页面路径
}
