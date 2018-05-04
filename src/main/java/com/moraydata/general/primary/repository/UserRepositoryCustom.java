package com.moraydata.general.primary.repository;

import java.util.List;

import com.moraydata.general.primary.entity.dto.UserBasicInformation;

public interface UserRepositoryCustom<T,ID> {

	List<UserBasicInformation> findAllIdAndUsernameWhoHasOpenId();
	List<UserBasicInformation> findLevel3UserBasicInformation(Long parentId);
	List<UserBasicInformation> findLevel2Or3UserBasicInformation(Long parentId);
}
