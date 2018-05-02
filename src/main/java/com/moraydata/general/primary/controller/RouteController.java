package com.moraydata.general.primary.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName RouteController
 * @Description Return specific HTML5 pages required
 * @author MingshuJian
 * @Date 2017年8月25日 下午2:26:29
 * @version 1.0.0
 */
@Controller
public class RouteController {
	
	@GetMapping({"/" , "/index"})
	public String index(HttpServletRequest request) {
		return "index";
	}
}
