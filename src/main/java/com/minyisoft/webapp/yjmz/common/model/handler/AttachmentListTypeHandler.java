package com.minyisoft.webapp.yjmz.common.model.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.minyisoft.webapp.core.utils.ObjectUuidUtils;
import com.minyisoft.webapp.yjmz.common.model.AttachmentInfo;

@Alias("attachmentListHandler")
public class AttachmentListTypeHandler extends BaseTypeHandler<List<AttachmentInfo>> {

	@Override
	public List<AttachmentInfo> getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return _getAttachments(rs.getString(columnName));
	}

	@Override
	public List<AttachmentInfo> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return _getAttachments(rs.getString(columnIndex));
	}

	@Override
	public List<AttachmentInfo> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return _getAttachments(cs.getString(columnIndex));
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, List<AttachmentInfo> attachments, JdbcType arg3)
			throws SQLException {
		List<String> ids = Lists.newArrayList();
		for (AttachmentInfo attachment : attachments) {
			if (attachment != null) {
				ids.add(attachment.getId());
			}
		}
		ps.setString(i, ids.isEmpty() ? null : Joiner.on('|').skipNulls().join(ids));
	}

	private List<AttachmentInfo> _getAttachments(String ids) {
		List<AttachmentInfo> attachments = Lists.newArrayList();
		if (StringUtils.isNotBlank(ids)) {
			for (String id : Splitter.on('|').omitEmptyStrings().trimResults().splitToList(ids)) {
				attachments.add((AttachmentInfo) ObjectUuidUtils.getObject(id));
			}
			return attachments;
		}
		return attachments;
	}

}
