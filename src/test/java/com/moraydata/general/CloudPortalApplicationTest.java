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
	
//	@Autowired
//	private PermissionRepository permissionRepository;
	
	@Test
	public void testtest() {
//		List<Permission> relational = permissionRepository.getRelational(1L);
//		relational.forEach(System.out::println);
	}
	
	@Test
	public void testRedisKeySet() {
//		Long count = redisTemplate.opsForSet().add("wechat:scan:login:set", "test_open_id3");
//		Boolean isMemeber = redisTemplate.opsForSet().isMember("wechat:scan:login:set", "test_open_id2");
//		System.out.println(count);
//		System.out.println(isMemeber);
//		Long removed = redisTemplate.opsForSet().remove("wechat:scan:login:set", "test_open_id2");
//		System.out.println(removed);
	}
	
//	@Autowired
//	private UserService userService;
	
	@Test
	public void testUpdatePhone() {
//		try {
//			boolean updatePhone = userService.updatePassword("messageCode:beamofsoul#18600574873#1523616098452", "Justin1987.");
////			boolean updatePhone = userService.updatePhone("messageCode:beamofsoul#18600574873#1523616098452", "15806660675");
//			System.out.println(updatePhone);
//		} catch (Exception e) {
//			e.printStackTrace();
//			e.printStackTrace();
//		}
	}
	
	@Test
	public void testExistsByUsername() {
//		try {
//			boolean flag = userService.existsByUsername("beamofsoul");
//			System.out.println(flag);
//		} catch (Exception e) {
//			e.printStackTrace();
//			e.printStackTrace();
//		}
	}
	
//	@Autowired
//	private JedisConnectionFactory jedisConnectionFactory;
//	
//	@Autowired
//	private RedisTemplate<Object, Object> redisTemplate;
	
	@Test
	public void testRedisDatabase() {
//		System.out.println(jedisConnectionFactory.getDatabase());
//		
//		redisTemplate.opsForValue().set("adfadfad", "adfadfasd");
	}
}
