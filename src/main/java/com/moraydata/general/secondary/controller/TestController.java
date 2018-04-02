package com.moraydata.general.secondary.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moraydata.general.secondary.entity.Test;
import com.moraydata.general.secondary.repository.TestRepository;
import com.moraydata.general.secondary.service.TestService;

/**
 * @ClassName TestController
 * @Description Return specific restful data required for test module
 * @author MingshuJian
 * @Date 2017年8月29日 上午10:52:08
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/test")
public class TestController {
	
	@Autowired
	private TestRepository testRepository;
	
	@Autowired
	private TestService testService;

	@PostMapping("/add")
	public Test createTest(@RequestBody Test test) {
		return testRepository.save(test);
	}
	
	@GetMapping("/list")
	public List<Test> getAllTests() {
		return testRepository.findAll();
	}

	@GetMapping("/{ids}")
	public List<Test> getTests(@PathVariable Long... ids) {
		return testRepository.findAllById(Arrays.asList(ids));
	}
	
	@PutMapping("/update")
	public Test updateTest(@RequestBody Test test) {
		return testRepository.saveAndFlush(test);
	}

	@DeleteMapping("/{id}")
	public void deleteTest(@PathVariable Long id) {
		testRepository.deleteById(id);
	}
	
	@GetMapping("/test")
	public void test() {
		testService.test();
	}
}
