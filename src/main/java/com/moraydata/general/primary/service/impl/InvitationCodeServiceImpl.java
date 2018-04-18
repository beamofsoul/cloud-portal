package com.moraydata.general.primary.service.impl;

import static com.moraydata.general.management.util.BooleanExpressionUtils.addExpression;
import static com.moraydata.general.management.util.BooleanExpressionUtils.like;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toBoolean;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toInteger;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toLocalDateTime;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toLong;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.management.util.InvitationCodeUtils;
import com.moraydata.general.primary.entity.InvitationCode;
import com.moraydata.general.primary.entity.InvitationCode.Type;
import com.moraydata.general.primary.entity.query.QInvitationCode;
import com.moraydata.general.primary.repository.InvitationCodeRepository;
import com.moraydata.general.primary.service.InvitationCodeService;
import com.moraydata.general.primary.service.UserService;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service("invitationCodeService")
public class InvitationCodeServiceImpl extends BaseAbstractService implements InvitationCodeService {

	@Autowired
	private InvitationCodeRepository invitationCodeRepository;
	
	@Autowired
	private UserService userService;

	@Override
	@Transactional
	public long delete(Long... instanceIds) {
		return invitationCodeRepository.deleteByIds(instanceIds);
	}

	@Override
	public InvitationCode get(Long instanceId) {
		return invitationCodeRepository.findOne(instanceId);
	}
	

	@Override
	public List<InvitationCode> get(Long... instanceIds) {
		return invitationCodeRepository.findByIds(instanceIds);
	}

	@Override
	public List<InvitationCode> get() {
		return invitationCodeRepository.findAll();
	}

	@Override
	public Page<InvitationCode> get(Pageable pageable) {
		return invitationCodeRepository.findAll(pageable);
	}

	@Override
	public Page<InvitationCode> get(Pageable pageable, Predicate predicate) {
		return invitationCodeRepository.findAll(predicate, pageable);
	}
	
	@Override
	public BooleanExpression search(JSONObject conditions) {
		if (conditions == null) return null;
		
		QInvitationCode invitationCode = QInvitationCode.invitationCode;
		BooleanExpression exp = null;
		
		String id = conditions.getString(invitationCode.id.getMetadata().getName());
		exp = addExpression(id, exp, invitationCode.id.eq(toLong(id)));
		
		String userId = conditions.getString(invitationCode.userId.getMetadata().getName());
		exp = addExpression(userId, exp, invitationCode.userId.eq(toLong(userId)));
		
		String code = conditions.getString(invitationCode.code.getMetadata().getName());
		exp = addExpression(code, exp, invitationCode.code.like(like(code)));
		
		String expiredDate = conditions.getString(invitationCode.expiredDate.getMetadata().getName());
		exp = addExpression(expiredDate, exp, invitationCode.expiredDate.before(toLocalDateTime(expiredDate)));
		
		String available = conditions.getString(invitationCode.available.getMetadata().getName());
		exp = addExpression(available, exp, invitationCode.available.eq(toBoolean(available)));
		
		String type = conditions.getString(invitationCode.type.getMetadata().getName());
		exp = addExpression(type, exp, invitationCode.type.eq(toInteger(type)));
		
		return exp;
	}

	@Override
	public InvitationCode get(String code) {
		return invitationCodeRepository.findByCode(code);
	}
	
	@Override
	public InvitationCode get(String code, InvitationCode.Type type) {
		return invitationCodeRepository.findByCodeAndType(code, type.getValue());
	}

	@Transactional(readOnly = false)
	@Override
	public List<InvitationCode> create(Long userId, int numberOfCodes, Type type) throws Exception {
		if (numberOfCodes > 0 && userId != null && userId.longValue() != 0L) {
			List<InvitationCode> entities = new ArrayList<InvitationCode>();
			for (int i = 0; i < numberOfCodes; i++) {
				entities.add(new InvitationCode(null, userId, InvitationCodeUtils.generate(), null, true, type.getValue()));
			}
			userService.decreaseCountOfInvitationCodes(userId, numberOfCodes);
			return invitationCodeRepository.saveAll(entities);
		}
		return new ArrayList<InvitationCode>(0);
	}

	@Override
	public long delete(String code) {
		return invitationCodeRepository.deleteByCode(code);
	}

	@Transactional
	@Override
	public long updateAvailable(String code, boolean available) {
		QInvitationCode $ = new QInvitationCode("InvitationCode");
		return invitationCodeRepository.update($.available, available, $.code.eq(code));
	}

	@Override
	public long deleteByUserIds(Long... userIds) throws Exception {
		QInvitationCode $ = new QInvitationCode("InvitationCode");
		return invitationCodeRepository.deleteByPredicate($.userId.in(userIds));
	}
}
