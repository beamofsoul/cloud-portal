package com.moraydata.general;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.moraydata.general.management.schedule.PushPublicSentimentJob;
import com.moraydata.general.management.schedule.TemplateMessage;

import junit.framework.TestCase;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CloudPortalApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class PushPublicSentimentJobTest extends TestCase {

	@Autowired
	private PushPublicSentimentJob pushPublicSentimentJob;
	
	@Test
	public void testGetTemplateMessage() throws Exception {
		TemplateMessage templateMessage = pushPublicSentimentJob.getTemplateMessage(63L);
		Assert.assertEquals("aa2", templateMessage.getKeyword1Value());
	}
	
	@Test
	public void testUpdateAction() throws Exception {
		pushPublicSentimentJob.updateAction(63L, TemplateMessage.Action.PUSH.getValue());
	}
}
