package com.minyisoft.webapp.yjmz.common.model;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.core.model.DataBaseInfo;
import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.model.ISystemUserObject;
import com.minyisoft.webapp.core.security.utils.DigestUtils;
import com.minyisoft.webapp.core.security.utils.EncodeUtils;
import com.minyisoft.webapp.yjmz.common.model.enumField.CompanyStatusEnum;
import com.minyisoft.webapp.yjmz.common.model.enumField.UserMaleEnum;
import com.minyisoft.webapp.yjmz.common.model.enumField.UserStatusEnum;
import com.minyisoft.webapp.yjmz.common.security.ShiroDbRealm;
import com.minyisoft.webapp.yjmz.common.util.SystemConstant;

/**
 * @author qingyong_ou 系统用户
 * 
 */
@Getter
@Setter
@ModelKey(0x147C41EBF72L)
public class UserInfo extends DataBaseInfo implements ISystemUserObject {
	@Label("用户状态")
	@NotNull
	private UserStatusEnum status = UserStatusEnum.INCUMBENCY;
	@Label("用户登录账号")
	@NotBlank
	private String userLoginName;
	@Label("用户登录密码")
	@NotBlank
	private String userPassword;
	// 用户登录密码附加字符串，由系统随机生成
	private String userPasswordSalt;
	@Label("用户性别")
	@NotNull
	private UserMaleEnum userMale = UserMaleEnum.MALE;
	@Label("手机号码")
	@NotBlank
	private String cellPhoneNumber;
	// 登录次数
	private int loginCount;
	// 最近登录时间
	private Date lastLoginTime;
	// 会员微信OpenID
	private String weixinOpenId;
	// 默认登录组织，即登录后首先进入的组织，无组织时为null
	private ISystemOrgObject defaultLoginOrg;
	// 用户所在组织列表
	private List<UserOrgRelationInfo> orgRelations;

	/**
	 * 禁止在程序外部直接设定password，统一通过constructUserPassword方法进行设置
	 * 
	 * @param password
	 */
	private void setUserPassword(String password) {
		userPassword = password;
	}

	/**
	 * 禁止在程序外部直接设定salt，统一通过constructUserPassword方法进行设置
	 * 
	 * @param salt
	 */
	private void setUserPasswordSalt(String salt) {
		userPasswordSalt = salt;
	}

	/**
	 * 创建用户密码
	 * 
	 * @param plainPassword
	 *            用户密码明文
	 */
	public void constructUserPassword(String plainPassword) {
		Assert.hasText(plainPassword, "用户密码不允许为空");
		if (StringUtils.isBlank(userPasswordSalt)) {
			setUserPasswordSalt(EncodeUtils.encodeHex(DigestUtils.generateSalt(4)));
		}
		setUserPassword(ShiroDbRealm.hashPassword(plainPassword, userPasswordSalt));
	}

	/**
	 * 待检查用户密码是否正确
	 * 
	 * @param plainPassword
	 *            待检查密码明文
	 * @return
	 */
	public boolean isPasswordCorrect(String plainPassword) {
		return StringUtils.isBlank(plainPassword) ? false : StringUtils.equals(userPassword,
				ShiroDbRealm.hashPassword(plainPassword, userPasswordSalt));
	}

	public Optional<UserOrgRelationInfo> getOrgRelation(ISystemOrgObject org) {
		if (org != null) {
			for (UserOrgRelationInfo userOrg : orgRelations) {
				if (userOrg.getOrg().equals(org)) {
					return Optional.of(userOrg);
				}
			}
		}
		return Optional.absent();
	}

	/**
	 * 当前用户是否属于指定组织结构用户
	 * 
	 * @param org
	 * @return
	 */
	public boolean isBelongTo(ISystemOrgObject org) {
		return getOrgRelation(org).isPresent();
	}

	/**
	 * 获取指定组织架构的用户级次路径
	 * 
	 * @param org
	 * @return
	 */
	public String getUserPath(ISystemOrgObject org) {
		Optional<UserOrgRelationInfo> optionalUserOrgEntity = getOrgRelation(org);
		if (optionalUserOrgEntity.isPresent()) {
			UserOrgRelationInfo orgRelation = optionalUserOrgEntity.get();
			if (orgRelation.getUpperUser() != null) {
				return orgRelation.getUpperUser().getUserPath(org) + SystemConstant.ID_SEPARATOR + getId();
			} else {
				return SystemConstant.ID_SEPARATOR + getId();
			}
		}
		return null;
	}

	/**
	 * 获取指定组织架构的上级用户
	 * 
	 * @param org
	 * @return
	 */
	public UserInfo getUpperUser(ISystemOrgObject org) {
		Optional<UserOrgRelationInfo> optionalUserOrgEntity = getOrgRelation(org);
		return optionalUserOrgEntity.isPresent() ? optionalUserOrgEntity.get().getUpperUser() : null;
	}

	/**
	 * 获取用户所在指定类型的组织架构列表
	 * 
	 * @param userType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends ISystemOrgObject> List<T> getOrgList(Class<T> orgClazz) {
		if (orgClazz != null && !orgRelations.isEmpty()) {
			Class<?> userClass = ClassUtils.getUserClass(orgClazz);
			List<T> orgList = Lists.newArrayList();
			for (UserOrgRelationInfo userOrg : orgRelations) {
				if (userOrg != null
						&& userClass.isAssignableFrom(userOrg.getOrg().getClass())
						&& (!(userOrg.getOrg() instanceof CompanyInfo) || ((CompanyInfo) userOrg.getOrg()).getStatus() == CompanyStatusEnum.NORMAL)) {
					orgList.add((T) userOrg.getOrg());
				}
			}
			return orgList;
		}
		return Collections.emptyList();
	}

	/**
	 * 判断当前用户是否指定用户的上级
	 * 
	 * @param targetUser
	 * @return
	 */
	public boolean isLeaderTo(ISystemOrgObject org, UserInfo targetUser) {
		if (!isBelongTo(org) || targetUser == null || this.equals(targetUser) || !targetUser.isBelongTo(org)) {
			return false;
		}
		return StringUtils.indexOf(targetUser.getUserPath(org), SystemConstant.ID_SEPARATOR + getId()
				+ SystemConstant.ID_SEPARATOR) >= 0;
	}
}
