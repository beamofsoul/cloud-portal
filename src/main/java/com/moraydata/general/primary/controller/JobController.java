package com.moraydata.general.primary.controller;

import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.moraydata.general.management.util.PageUtils;
import com.moraydata.general.management.util.ResponseEntity;
import com.moraydata.general.primary.entity.JobAndTrigger;
import com.moraydata.general.primary.service.JobAndTriggerService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/job")
public class JobController {

	@Autowired
	private JobAndTriggerService jobAndTriggerService;
	
	@Autowired
	private Scheduler scheduler; // 任务调度器
	
	/**
	 * 增加任务
	 * @param jobClassName 任务全路径名
	 * @param jobGroupName 任务分组标识
	 * @param cronExpression 任务执行周期表达式
	 * @return null
	 */
	@PostMapping("/scheduling")
	public ResponseEntity scheduling(@RequestParam String jobClassName, @RequestParam String jobGroupName, @RequestParam String cronExpression) {
		try {
			scheduler.start();
			JobDetail jobDetail = JobBuilder.newJob(getClass(jobClassName).getClass()).withIdentity(jobClassName, jobGroupName).build();
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
			CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobClassName, jobGroupName).withSchedule(scheduleBuilder).build();
			scheduler.scheduleJob(jobDetail, trigger);
			log.info("创建定时任务成功\nClass: {}\nGroup: {}\nCronExpression: {}", jobClassName, jobGroupName, cronExpression);
			return ResponseEntity.success("创建定时任务成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("创建定时任务失败\nClass: {}\nGroup: {}\nCronExpression: {}\nException: {}", jobClassName, jobGroupName, cronExpression, e.getMessage());
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 暂停任务
	 * @param jobClassName 任务全路径名
	 * @param jobGroupName 任务分组标识
	 * @return null
	 */
	@PutMapping("/pausing")
	public ResponseEntity pausing(@RequestParam String jobClassName, @RequestParam String jobGroupName) {
		try {
			scheduler.pauseJob(JobKey.jobKey(jobClassName, jobGroupName));
			log.info("暂停定时任务成功\nClass: {}\nGroup: {}", jobClassName, jobGroupName);
			return ResponseEntity.success("暂停定时任务成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("暂停定时任务失败\nClass: {}\nGroup: {}\nException: {}", jobClassName, jobGroupName, e.getMessage());
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 重启任务
	 * @param jobClassName 任务全路径名
	 * @param jobGroupName 任务分组标识
	 * @return null
	 */
	@PutMapping("/resuming")
	public ResponseEntity resuming(@RequestParam String jobClassName, @RequestParam String jobGroupName) {
		try {
			scheduler.resumeJob(JobKey.jobKey(jobClassName, jobGroupName));
			log.info("重启定时任务成功\nClass: {}\nGroup: {}", jobClassName, jobGroupName);
			return ResponseEntity.success("重启定时任务成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("重启定时任务失败\nClass: {}\nGroup: {}\nException: {}", jobClassName, jobGroupName, e.getMessage());
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 重构任务
	 * @param jobClassName 任务全路径名
	 * @param jobGroupName 任务分组标识
	 * @param cronExpression 任务执行周期表达式
	 * @return null
	 */
	@PutMapping("rescheduling")
	public ResponseEntity rescheduling(@RequestParam String jobClassName, @RequestParam String jobGroupName, @RequestParam String cronExpression) {
		try {
			TriggerKey triggerKey = TriggerKey.triggerKey(jobClassName, jobGroupName);
			// 重建表达式构建器
			CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
			// 通过trigger标识获取之前任务的trigger实例
			CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
			// 按照新的表达式构建器来重新构建trigger
			cronTrigger = cronTrigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
			// 按照新的trigger重新设置任务的执行
			scheduler.rescheduleJob(triggerKey, cronTrigger);
			log.info("更新定时任务成功\nClass: {}\nGroup: {}\nCronExpression: {}", jobClassName, jobGroupName, cronExpression);
			return ResponseEntity.success("更新定时任务成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("更新定时任务失败\nClass: {}\nGroup: {}\nCronExpression: {}\nException: {}", jobClassName, jobGroupName, cronExpression, e.getMessage());
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 删除任务
	 * @param jobClassName 任务全路径名
	 * @param jobGroupName 任务分组标识
	 * @return null
	 */
	@DeleteMapping("/deletion")
	public ResponseEntity deletion(@RequestParam String jobClassName, @RequestParam String jobGroupName) {
		try {
			TriggerKey triggerKey = TriggerKey.triggerKey(jobClassName, jobGroupName);
			scheduler.pauseTrigger(triggerKey);
			scheduler.unscheduleJob(triggerKey);
			scheduler.deleteJob(JobKey.jobKey(jobClassName, jobGroupName));
			log.info("删除定时任务成功\nClass: {}\nGroup: {}", jobClassName, jobGroupName);
			return ResponseEntity.success("删除定时任务成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("删除定时任务失败\nClass: {}\nGroup: {}\nException: {}", jobClassName, jobGroupName, e.getMessage());
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取符合查询后的分页任务数据
	 * @param map ->
	 * 				conditions 每个key对应属性，每个value对应搜索内容
	 * 				pageable key可以有page、size、sort和direction，具体value针对每个属性值
	 * @return Page<User>
	 */
	@GetMapping("/page")
	public ResponseEntity page(@RequestParam Map<String, Object> map) {
		Assert.notNull(map, "PAGE_MAP_IS_NULL");
		
		try {
			Page<JobAndTrigger> data =  jobAndTriggerService.get(PageUtils.parsePageable(JSON.parseObject(map.get("pageable").toString())), jobAndTriggerService.search(JSON.parseObject(map.get("conditions").toString())));
			return ResponseEntity.success("获取分页任务信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	private static QuartzJobBean getClass(String className) throws Exception {
		Class<?> clazz = Class.forName(className);
		return (QuartzJobBean) clazz.newInstance();
	}
}
