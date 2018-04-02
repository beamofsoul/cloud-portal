package com.moraydata.general;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import junit.framework.TestCase;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CloudPortalApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class CloudPortalApplicationTest extends TestCase {

//	@Autowired
//	private UserService userService;	
	
	@Test
	public void updatePassowrdTest() {
//		Assert.assertEquals(userService.updatePassword(1L, "Justin1987.", "123456"), Boolean.TRUE);
	}
	
	@Test
	public void sendMessageCodeTest() {
//		Map<String, Object> map = userService.sendMessageCode("beamofsoul","18600574873", new Date(System.currentTimeMillis()));
//		map.entrySet().stream().forEach(e -> {
//			System.out.println(e.getKey() + " = " + e.getValue());
//		});
	}
	
	@Test
	public void cacheTest() {
//		CacheUtils.put("test", "TestObject1", new com.moraydata.general.secondary.entity.Test(99L, "中国China"));
	}
	
//	@Autowired
//	private RedisTemplate<Object, Object> redisTemplate;
	
	@Test
	public void saveMessageCodeInRedisTest() {
//		redisTemplate.opsForValue().set("messageCode:beamofsoul#18600574873", String.valueOf(123456), 5 * 60, TimeUnit.SECONDS);
//		System.out.println(redisTemplate.opsForValue().get("messageCode:beamofsoul#18600574873"));
//		redisTemplate.execute(new RedisCallback<Object>() {
//			@Override
//			public Object doInRedis(RedisConnection connection) throws DataAccessException {
//				return connection.setEx("messageCode:beamofsoul#18600574873".getBytes(), Long.valueOf(5 * 60), String.valueOf(123456).getBytes());
//			}
//		});
//		shardedJedis.setex("messageCode:" + "beamofsoul" + "#" + "18600574873", 5 * 60, String.valueOf(123456));
	}
}
