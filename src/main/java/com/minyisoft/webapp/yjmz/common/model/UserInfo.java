package com.minyisoft.webapp.yjmz.common.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import com.minyisoft.webapp.core.annotation.Label;
import com.minyisoft.webapp.core.annotation.ModelKey;
import com.minyisoft.webapp.core.model.DataBaseInfo;
import com.minyisoft.webapp.core.model.ISystemOrgObject;
import com.minyisoft.webapp.core.model.ISystemUserObject;
import com.minyisoft.webapp.core.security.utils.DigestUtils;
import com.minyisoft.webapp.core.security.utils.EncodeUtils;
import com.minyisoft.webapp.yjmz.common.model.enumField.UserMaleEnum;
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
	private List<UserOrgEntity> userOrgList = Collections.emptyList();

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
	 * @param oriUserPassword
	 *            用户密码明文
	 */
	public void constructUserPassword(String oriUserPassword) {
		Assert.hasText(oriUserPassword, "用户密码不允许为空");
		if (StringUtils.isBlank(userPasswordSalt)) {
			setUserPasswordSalt(EncodeUtils.encodeHex(DigestUtils.generateSalt(4)));
		}
		setUserPassword(new SimpleHash(ShiroDbRealm.hashAlgorithm, oriUserPassword, userPasswordSalt,
				ShiroDbRealm.hashInterations).toHex());
	}

	/**
	 * 待检查用户密码是否正确
	 * 
	 * @param undeterminedPassword
	 *            待检查密码明文
	 * @return
	 */
	public boolean isPasswordCorrect(String undeterminedPassword) {
		if (StringUtils.isBlank(undeterminedPassword)) {
			return false;
		}
		if (StringUtils.isBlank(userPasswordSalt)) {
			return StringUtils.equals(userPassword, new SimpleHash(ShiroDbRealm.hashAlgorithm, undeterminedPassword,
					null, ShiroDbRealm.hashInterations).toHex());
		} else {
			return StringUtils.equals(userPassword, new SimpleHash(ShiroDbRealm.hashAlgorithm, undeterminedPassword,
					userPasswordSalt, ShiroDbRealm.hashInterations).toHex());
		}
	}

	/**
	 * 当前用户是否属于指定组织结构用户
	 * 
	 * @param org
	 * @return
	 */
	public boolean isBelongTo(ISystemOrgObject org) {
		for (UserOrgEntity userOrg : userOrgList) {
			if (userOrg.getOrg().equals(org)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取指定组织架构的用户级次路径
	 * 
	 * @param org
	 * @return
	 */
	public String getUserPath(ISystemOrgObject org) {
		for (UserOrgEntity userOrg : userOrgList) {
			if (org.equals(userOrg.getOrg())) {
				return userOrg.getUserPath();
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
		for (UserOrgEntity userOrg : userOrgList) {
			if (org.equals(userOrg.getOrg())) {
				return userOrg.getUpperUser();
			}
		}
		return null;
	}

	/**
	 * 获取指定组织架构的用户级次
	 * 
	 * @return
	 */
	public int getUserLevel(ISystemOrgObject org) {
		String userPath = getUserPath(org);
		if (StringUtils.isBlank(userPath)) {
			return 99;
		} else {
			return StringUtils.split(userPath, SystemConstant.ID_SEPARATOR).length;
		}
	}

	/**
	 * 获取用户所在指定类型的组织架构列表
	 * 
	 * @param userType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends ISystemOrgObject> List<T> getOrgList(Class<T> orgClazz) {
		if (orgClazz != null && !CollectionUtils.isEmpty(userOrgList)) {
			Class<?> userClass = ClassUtils.getUserClass(orgClazz);
			List<T> orgList = new ArrayList<T>();
			for (UserOrgEntity userOrg : userOrgList) {
				if (userOrg != null && userClass.isAssignableFrom(userOrg.getOrg().getClass())) {
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
