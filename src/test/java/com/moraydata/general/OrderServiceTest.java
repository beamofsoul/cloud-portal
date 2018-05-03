package com.moraydata.general;

import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import junit.framework.TestCase;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CloudPortalApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class OrderServiceTest extends TestCase {

//	@Autowired
//	private OrderRepository orderRepository;
//	
//	@Test
//	public void codeTest() {
//		System.out.println(orderRepository.nextSequenceValue("order_code"));
//	}
	
//	@Autowired
//	private RedisTemplate<Object, Object> redisTemplate;
//	
//	@Test
//	public void testest() {
//		redisTemplate.opsForValue().get("oauth2:auth:898a8c8b-1f60-481f-9cf1-9de1f0695492");
////		System.out.println(bytes);
//	}
}
