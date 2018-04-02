package com.moraydata.general.primary.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.primary.entity.InvitationCode;
import com.moraydata.general.primary.entity.InvitationCode.Type;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

public interface InvitationCodeService {
	
	long delete(Long... instanceIds);
	InvitationCode get(Long instanceId);
	List<InvitationCode> get(Long... instanceIds);
	Page<InvitationCode> get(Pageable pageable);
	Page<InvitationCode> get(Pageable pageable, Predicate predicate);
	List<InvitationCode> get();
	BooleanExpression search(JSONObject conditions);
	
	InvitationCode get(String code);
	InvitationCode get(String code, Type type);
	List<InvitationCode> create(Long userId, int numberOfCodes, Type type);
	long delete(String code);
	long updateAvailable(String code, boolean available);
//	boolean exists(Long userId, String code);
}
