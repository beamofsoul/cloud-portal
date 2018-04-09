package com.moraydata.general;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CloudPortalApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class InvitationCodeServiceTest {

//	@Autowired
//	private InvitationCodeService invitationCodeService;
//	
	@Test
	public void testCreateInvitationCode() {
//		List<InvitationCode> entities = invitationCodeService.create(1L, 5, InvitationCode.Type.BIND_PARENT_USER_ID);
//		entities.forEach(System.out::println);
	}
	
	@Test
	public void testUpdateAvaiable() {
//		long count = invitationCodeService.updateAvailable("3083E213CB8E40A1B8F4FEC23598D614", false);
//		Assert.assertEquals(count, 1);
	}
}
