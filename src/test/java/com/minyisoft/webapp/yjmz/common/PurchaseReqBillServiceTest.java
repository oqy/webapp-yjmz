package com.minyisoft.webapp.yjmz.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.minyisoft.webapp.core.security.utils.PermissionUtils;
import com.minyisoft.webapp.yjmz.common.service.CompanyService;
import com.minyisoft.webapp.yjmz.oa.model.PurchaseReqBillInfo;
import com.minyisoft.webapp.yjmz.oa.service.PurchaseReqBillService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:root-context.xml", "classpath:activiti-context.xml" })
public class PurchaseReqBillServiceTest {
	@Autowired
	private PurchaseReqBillService purchaseReqBillService;
	@Autowired
	private CompanyService companyService;

	@Test
	@Transactional
	public void testUserQuery() {
		PurchaseReqBillInfo bill = new PurchaseReqBillInfo();
		bill.setCompany(companyService.getValue("AAABR8QfEibuuXFj8idFsYAHGd6C03Ww"));
		PermissionUtils.stopPermissionCheck();
		purchaseReqBillService.addNew(bill);
	}
}
