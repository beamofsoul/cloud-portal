package com.moraydata.general.primary.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moraydata.general.management.schedule.PushPublicSentimentJob;
import com.moraydata.general.management.schedule.TemplateMessage;
import com.moraydata.general.management.schedule.TemplateMessage.Action;
import com.moraydata.general.management.util.ResponseEntity;
import com.moraydata.general.primary.entity.User;
import com.moraydata.general.primary.entity.dto.UserBasicInformation;
import com.moraydata.general.primary.service.UserService;

@RestController
@RequestMapping("/wechat")
public class WechatController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PushPublicSentimentJob pushPublicSentimentJob;

	@PostMapping("/sendTemplateMessage2Target")
	public ResponseEntity sendTemplateMessage2Target(@RequestParam Long userId, @RequestParam Long templateMessageId, @RequestParam Integer action) throws Exception {
		Assert.notNull(userId, "SEND_TEMPLATE_MESSAGE_2_TARGET_USER_ID_IS_NULL");
		Assert.notNull(templateMessageId, "SEND_TEMPLATE_MESSAGE_2_TARGET_TEMPLATE_MESSAGE_ID_IS_NULL");
		Assert.notNull(action, "SEND_TEMPLATE_MESSAGE_2_TARGET_ACTION_IS_NULL");
		
		User currentUser = userService.get(userId);
		if (currentUser == null) {
			return ResponseEntity.error("未能通过userId获取到任何用户信息");
		}
		if (!TemplateMessage.Action.exists(action)) {
			return ResponseEntity.error("action格式错误");
		}
		
		try {
			// 1. 判断当前用户的用户级别
			Integer currentUserLevel = currentUser.getLevel();
			Action targetAction = TemplateMessage.Action.getInstance(action);
			if (currentUserLevel.equals(User.Level.FIRST.getValue())) {
				// 1.1  如果是1级用户，判断当前行为
				// 1.1.1 如果是上报功能，判断是否当前模板消息已被设置为有效
				if (targetAction.equals(TemplateMessage.Action.PUSH)) {
					Integer templateMessageAction = pushPublicSentimentJob.getTemplateMessage(templateMessageId).getAction(); // .getTemplateMessageAction(userId, templateMessageId);
					if (TemplateMessage.Action.exists(templateMessageAction)) {
						Action currentAction = TemplateMessage.Action.getInstance(templateMessageAction);
						List<UserBasicInformation> sent2Users = null;
						if (currentAction.equals(TemplateMessage.Action.VALIDATE)) {
							// 1.1.1.1 如果已经被设置为有效，推送给3级用户，并将该消息状态标记为已上报
							sent2Users = userService.getLevelUserBasicInformation(userId, User.Level.THIRD);
						} else {
							// 1.1.1.2 如果未被设置为有效，推送给2级和3级用户，并将该消息状态标记为已上报
							sent2Users = userService.getLevelUserBasicInformation(userId, User.Level.SECOND, User.Level.THIRD);
						}
						// 推送
						pushPublicSentimentJob.sendTemplateMessage(sent2Users, templateMessageId);
						// 标记
						pushPublicSentimentJob.updateAction(templateMessageId, action);
					} else {
						new RuntimeException("获取到了未知的当前模板消息行为: " + templateMessageAction);
					}
				} else if (targetAction.equals(TemplateMessage.Action.VALIDATE)) {
					// 1.1.2 如果是有效功能，推送给2级用户，并将该消息状态标记为有效
					List<UserBasicInformation> sent2Users = userService.getLevelUserBasicInformation(userId, User.Level.SECOND);
					pushPublicSentimentJob.sendTemplateMessage(sent2Users, templateMessageId);
					pushPublicSentimentJob.updateAction(templateMessageId, action);
				} else if (targetAction.equals(TemplateMessage.Action.INVALIDATE)) {
					// 1.1.3 如果是无效功能，修改当前模板消息状态为无效
					pushPublicSentimentJob.updateAction(templateMessageId, action);
				}
			} else if (currentUserLevel.equals(User.Level.SECOND.getValue())) {
				// 1.2 如果是2级用户，判断当前行为
				if (targetAction.equals(TemplateMessage.Action.PUSH)) {
					// 1.2.1 如果是上报功能，推送给3级用户，并将该消息状态标记为已上报
					List<UserBasicInformation> sent2Users = userService.getLevelUserBasicInformation(userId, User.Level.THIRD);
					pushPublicSentimentJob.sendTemplateMessage(sent2Users, templateMessageId);
					pushPublicSentimentJob.updateAction(templateMessageId, action);
				} else if (targetAction.equals(TemplateMessage.Action.INVALIDATE)) {
					// 1.2.2 如果是无效功能，修改当前模板消息状态为无效
					pushPublicSentimentJob.updateAction(templateMessageId, action);
				}
			}
			return ResponseEntity.success("发送模板消息至指定用户成功", Boolean.TRUE);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
}
