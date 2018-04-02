package com.moraydata.general.primary.repository;

import org.springframework.stereotype.Repository;

import com.moraydata.general.management.repository.BaseMultielementRepository;
import com.moraydata.general.primary.entity.InvitationCode;

@Repository
public interface InvitationCodeRepository extends BaseMultielementRepository<InvitationCode, Long> {

	InvitationCode findByCode(String code);
	
	InvitationCode findByCodeAndType(String code, Integer type);
	
	long deleteByCode(String code);
}
