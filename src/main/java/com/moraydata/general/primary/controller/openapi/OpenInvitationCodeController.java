package com.moraydata.general.primary.controller.openapi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moraydata.general.management.util.ResponseEntity;
import com.moraydata.general.primary.entity.InvitationCode;
import com.moraydata.general.primary.service.InvitationCodeService;
import com.moraydata.general.primary.service.UserService;

@RequestMapping("/open/invitationCode")
@RestController
public class OpenInvitationCodeController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private InvitationCodeService invitationCodeService;
	
	@GetMapping("one4BindParent")
	public ResponseEntity one4BindParent(@RequestParam Long userId) {
		Assert.notNull(userId, "ONE_4_BIND_PARENT_USER_ID_IS_NULL");
		
		if (userId.intValue() == 0) {
			return ResponseEntity.error("用户ID格式错误");
		}
		
		try {
			Integer countOfInvitationCodes = userService.getCountOfInvitationCodes(userId);
			if (countOfInvitationCodes == null) {
				return ResponseEntity.error("无法通过输入的用户ID找到任何用户信息");
			}
			if (countOfInvitationCodes == 0) {
				return ResponseEntity.error("当前用户已无可用邀请码");
			}
			List<InvitationCode> data = invitationCodeService.create(userId, 1, InvitationCode.Type.BIND_PARENT_USER_ID);
			return ResponseEntity.success("创建邀请码成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
}
