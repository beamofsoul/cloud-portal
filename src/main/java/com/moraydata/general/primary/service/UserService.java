package com.moraydata.general.primary.service;

import java.util.List;

import com.moraydata.general.primary.entity.User;
import com.moraydata.general.primary.entity.dto.UserBasicInformation;

public interface UserService {
	
	User get(Long instanceId);
	List<UserBasicInformation> getLevelUserBasicInformation(Long level1UserId, User.Level... level);
	List<Long> getLevelUserIds(Long level1UserId, User.Level... level);
}
