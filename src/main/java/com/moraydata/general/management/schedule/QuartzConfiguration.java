//package com.moraydata.general.management.schedule;
//
//import org.quartz.spi.JobFactory;
//import org.quartz.spi.TriggerFiredBundle;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.quartz.AdaptableJobFactory;
//
//@Configuration
//public class QuartzConfiguration {
//	
////	private static final String QUARTZ_PROPERTIES_PATH = "/quartz.properties";
//	
//	@Autowired
//	private AutowireCapableBeanFactory capableBeanFactory;
//	
////	@Autowired
////	private SchedulerFactory schedulerFactory;
//	
////	@Bean
////	public SchedulerFactoryBean schedulerFactoryBean() { 
////		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
////		try {
////			schedulerFactoryBean.setJobFactory(jobFactory());
//////			schedulerFactoryBean.setQuartzProperties(quartzProperties());
//////			schedulerFactoryBean.setSchedulerFactory(schedulerFactory);
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
////		return schedulerFactoryBean;
////	}
//	
//	@Bean
//	public JobFactory jobFactory() {
//		return new AdaptableJobFactory() {
//			@Override
//			protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
//				Object createdJobInstance = super.createJobInstance(bundle);
//				capableBeanFactory.autowireBean(createdJobInstance); // 这一步解决不能spring注入bean的问题
//				return createdJobInstance;
//			}
//		};
//	}
//	
////	@Bean
////	public Properties quartzProperties() throws IOException {
////		ClassPathResource quartzPropertiesLocation = new ClassPathResource(QUARTZ_PROPERTIES_PATH);
////		if (quartzPropertiesLocation.exists()) {
////			PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
////			propertiesFactoryBean.setLocation(quartzPropertiesLocation);
////			propertiesFactoryBean.afterPropertiesSet(); 
////			return propertiesFactoryBean.getObject();
////		}
////		return null;
////	}
//	
////	/**
////	 * Quartz初始化监听器
////	 * 该监听器可以在工程停止再启动时让已有的定时任务继续运行
////	 */
////	@Bean
////	public QuartzInitializerListener quartzInitializerListener() {
////		return new QuartzInitializerListener();
////	}
//	
////	/**
////	 * 通过SchedulerFactoryBean获取Scheduler的实例
////	 */
////	@Bean
////	public Scheduler scheduler() {
////		return schedulerFactoryBean().getScheduler();
////	}
//	
////	@Bean
////	public JobDetail testQuartzDetail() {
////		return JobBuilder.newJob(TestQuartz.class).withIdentity("testQuartz").storeDurably().build();
////	}
////	
////	@Bean
////	public Trigger testQuartzTrigger() {
////		SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
////				.withIntervalInSeconds(5) // 设置时间周期单位秒
////				.repeatForever();
////		return TriggerBuilder.newTrigger().forJob(testQuartzDetail())
////				.withIdentity("testQuartz")
////				.withSchedule(scheduleBuilder)
////				.build();
////	}
//}
