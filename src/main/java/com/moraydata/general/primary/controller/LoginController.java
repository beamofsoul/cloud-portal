package com.moraydata.general.primary.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.management.util.JSONUtils;
import com.moraydata.general.management.util.PageUtils;
import com.moraydata.general.primary.entity.Login;
import com.moraydata.general.primary.service.LoginService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/admin/login")
public class LoginController {

	@Resource
	private LoginService loginService;
	
    @ApiOperation(value = "创建登录记录", notes = "根据Login对象创建登录记录")
    @PreAuthorize("authenticated and hasPermission('Login','Login:add')")
	@PostMapping("/add")
    public Login createLogin(@RequestBody Login Login) {
        return loginService.create(Login);
    }
    
    @ApiOperation(value="获取所有登录记录详细信息", notes="获取登录记录信息列表")
	@GetMapping("/list")
	public List<Login> getAllLogins() {
		return loginService.get();
	}
	
    @ApiOperation(value="获取登录记录详细信息列表", notes="根据一组登录记录ID获取对应的登录记录信息列表")
	@GetMapping("/list/{ids}")
	public List<Login> getLogins(@PathVariable Long... ids) {
		return loginService.get(ids);
	}
    
    @ApiOperation(value="获取某个特定登录记录详细信息", notes="根据一个登录记录ID获取对应的登录记录信息")
	@GetMapping("/single/{id}")
	public Login getLogins(@PathVariable Long id) {
		return loginService.get(id);
	}
    
    @ApiOperation(value = "更新登录记录", notes = "根据Login对象更新登录记录")
    @PreAuthorize("authenticated and hasPermission('Login','Login:update')")
	@PutMapping("/update")
	public Login updateLogin(@RequestBody Login Login) {
		return loginService.update(Login);
	}
    
    @ApiOperation(value="删除一个或多个登录记录", notes="根据一个或多个登录记录ID删除对应的登录记录信息")
    @PreAuthorize("authenticated and hasPermission('Login','Login:delete')")
    @DeleteMapping("/delete/{ids}")
	public long deleteLogins(@PathVariable Long... ids) {
		return loginService.delete(ids);
	}
    
    /***************************************************************************/
    
    @ApiOperation(value="分页查询多个登录记录", notes="根据一个或多个查询条件查询对应的分页登录信息")
    @PreAuthorize("authenticated and hasPermission('login','login:list')")
	@GetMapping("/page")
    public Page<Login> getPageableLogins(@RequestParam Map<String, Object> params) {
		JSONObject jsonParams = JSONObject.parseObject(JSON.toJSONString(params));
		Object conditions = jsonParams.get("conditions");
		return loginService.get(PageUtils.parsePageable(params), 
				loginService.search(JSONUtils.formatAndParseObject(conditions)));
	}
}
