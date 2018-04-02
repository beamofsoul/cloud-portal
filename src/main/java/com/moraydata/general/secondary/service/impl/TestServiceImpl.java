package com.moraydata.general.secondary.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moraydata.general.secondary.entity.query.QTest;
import com.moraydata.general.secondary.repository.TestRepository;
import com.moraydata.general.secondary.service.TestService;

/**
 * @ClassName TestServiceImpl
 * @Description TODO(用一句话描述这个类的作用)
 * @author MingshuJian
 * @Date 2017年8月31日 下午4:07:57
 * @version 1.0.0
 */
@Service("testService")
public class TestServiceImpl implements TestService {

	@Autowired
	private TestRepository testRepository;
	
	@Transactional("secondaryTransactionManager")
	@Override
	public void test() {
		QTest t = new QTest("Test");
		testRepository.update(t.content, "test1 - 2", t.id.eq(1L));
	}

}
