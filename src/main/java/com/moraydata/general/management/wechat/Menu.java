package com.moraydata.general.management.wechat;

import lombok.Getter;
import lombok.Setter;

/**
 * 微信菜单类
 * @author Mingshu Jian  
 * @date 2018年4月24日
 */
@Getter
@Setter
public class Menu {

	private MenuButton[] button; // 定义名称与json中一致，不然解析名称对不上
}
