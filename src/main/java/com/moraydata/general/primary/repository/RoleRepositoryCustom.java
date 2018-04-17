package com.moraydata.general.primary.repository;

import java.util.List;

import com.moraydata.general.primary.entity.Role;

public interface RoleRepositoryCustom<T,ID> {

	List<Role> findAllAvailable();
}
