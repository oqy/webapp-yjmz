package com.minyisoft.webapp.yjmz.common.service.impl;

import org.springframework.stereotype.Service;
import com.minyisoft.webapp.core.service.impl.BaseServiceImpl;
import com.minyisoft.webapp.yjmz.common.model.criteria.AttachmentCriteria;
import com.minyisoft.webapp.yjmz.common.model.AttachmentInfo;
import com.minyisoft.webapp.yjmz.common.persistence.AttachmentDao;
import com.minyisoft.webapp.yjmz.common.service.AttachmentService;

@Service("attachmentService")
public class AttachmentServiceImpl extends BaseServiceImpl<AttachmentInfo, AttachmentCriteria, AttachmentDao> implements
		AttachmentService {
}
