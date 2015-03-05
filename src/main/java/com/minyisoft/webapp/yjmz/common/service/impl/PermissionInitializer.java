package com.minyisoft.webapp.yjmz.common.service.impl;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.minyisoft.webapp.yjmz.common.service.PermissionService;

@Component
public class PermissionInitializer implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() == null) {
			PermissionService permissionService = (PermissionService) event.getApplicationContext().getBean(
					"permissionService");
			permissionService.initPermission();
		}
	}
}
