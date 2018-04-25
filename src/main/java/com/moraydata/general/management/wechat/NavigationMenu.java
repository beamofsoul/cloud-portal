package com.moraydata.general.management.wechat;

import lombok.Getter;
import lombok.Setter;

/**
 * 导航菜单
 * @author Mingshu Jian  
 * @date 2018年4月24日
 */
@Getter
@Setter
public class NavigationMenu extends MenuButton {

	private SubMenuButton[] sub_button; // 包含多个子菜单 定义名称与json中一致，不然解析名称对不上
}
