package com.moraydata.general.primary.repository;

import java.util.List;

import com.moraydata.general.primary.entity.dto.UserBasicInformation;

public interface UserRepositoryCustom<T,ID> {

	List<UserBasicInformation> findLevelUserBasicInformation(Long parentId, int... levels);
	List<Long> findLevelUserIds(Long parentId, int... levels);
}
