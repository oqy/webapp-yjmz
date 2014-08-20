package com.minyisoft.webapp.yjmz.common;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:root-context.xml", "classpath:activiti-context.xml" })
public class WorkFlowServiceTest {
	@Test
	public void testUserQuery() {
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		Assert.assertNotNull(engine);
		Assert.assertNotNull(engine.getRuntimeService());
	}
}
