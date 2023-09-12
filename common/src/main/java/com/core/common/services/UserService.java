package com.core.common.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.core.common.model.contextmodel.*;
import com.core.datamodel.model.cachemodel.RelatedReference;
import com.core.datamodel.model.dbmodel.*;
import com.core.datamodel.model.enums.ERoleType;
import com.core.datamodel.model.securitymodel.UserPrincipalDetails;
import com.core.datamodel.model.enums.EUserActions;
import com.core.datamodel.model.wrapper.AddressWrapper;
import com.core.common.security.UserTokenState;
import com.core.model.wrapper.QRCodeDataWrapper;
import com.core.model.wrapper.ResultListPageable;
import com.google.zxing.WriterException;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService  {


	List<User> findAllUsers();
	String registerUserAndSendActivationCode(CUser CUser, Long createBy);
	UserTokenState commitRegisterUser(String userName ,Integer activeCode,HttpServletResponse response, HttpServletRequest request) throws IOException, ServletException;
	User commitRegisterUser(String userName, Integer verificationCode);
	User getNewUserForCommitRegister(String userName);
	String reSendActivationCode(String userName);
	User getUserInfoForEdit(Long userId);
	User getUserInfoForEdit(String userName);
	String editUser(EUser eUser);
	String fullEditUser(EFUser eFUser);
	Integer updateFailAttemptsAndLockResult(String username);
	String lockUser(Long userId);
	String unlockUser(Long userId);
	String resetUserPassword(Long userId);
	void resetFailAttempts(String username);
	UserAttempts getUserAttempts(Long id);
	UserAttempts getUserAttempts(String username);
	String uploadProfileImage(Long id, MultipartFile image);

	UserPrincipalDetails getCurrentUser();
	User getCurrentUserInfo();
	User getUserInfo(Long userId);
	User getUserInfo(String userName);
	User getUserByUserNameOrEmail(String param);
	User getByAffiliateCode(String affiliateCode);
	List<User> getAffiliateUsers();

	Boolean existUserByUserName(String userName);

	InputStream getMyQRCode(Double amount)  throws WriterException,IOException;
	QRCodeDataWrapper getQRCodeData(String qrCodeText);


	boolean isAuthUser();

	boolean checkIfValidOldPassword(User user, String password);
	String changeUserPassword(ChangePassword changePassword);
	void goToChangePasswordByEmail(HttpServletResponse response, String token);
	String goToChangePasswordByActiveCode(String userName ,Integer activeCode,HttpServletResponse response, HttpServletRequest request) throws IOException, ServletException;

	User generateUserSecret(Long userId);
	Map<String,String> generateAndSend2faCode(C2faRequest c2faRequest);
	Map<String,String> generateAndSend2faCode(User user);
	Map<String,String> generateAndSend2faCode(Long userId);

	Integer getUserCount();
	List<User> getAllUser();

	Boolean addBusinessRoleToUser(Long useId);
	Boolean changeToNormalUserRole(Long useId);

	boolean hasRoleType(ERoleType eRoleType);
    boolean hasExistRole(String role);
	boolean hasExistRole(Long userId,String role);
	boolean hasExistPermission(String permission);

	UserActions getUserActionInfo(String userActionName);
	void createUserLogQueue(EUserActions userAction, String ip, String agent, String machineId) ;
	Boolean createUserLog(Long userId, EUserActions eUserAction, String ip, String agent, String machineId);



	//#region Role
	List<Role> getAllRoles();
	Role getRoleInfo(Long roleId);
	Long createRole(CRole cRole);
	String editRole(ERole eRole);
	String deleteRole(Long roleId);
	//#endregion

	//#region Activity
	Activity getActivityInfo(Long activityId);
	List<Activity> getAllActivities();
	String editActivity(EActivity eActivity);
	//#endregion

	//#region Permission
	Permission getPermissionInfo(Long permissionId);
	List<Permission> getAllPermissions();
	//#endregion

	//#region User Group
	List<UserGroup> getAllUserGroups(Long userId);
	List<UserGroup> getAllActiveUserGroups(Long userId);
	UserGroup getUserGroupInfo(Long userGroupId/*,Long branchId*/);
	UserGroup getActiveUserGroupInfo(Long userGroupId/*,Long branchId*/);
	Long createUserGroup(UserGroupDto userGroupDto);
	String editUserGroup(UserGroupDto userGroupDto);
	String deleteUserGroup(Long userGroupId);

	ResultListPageable<Long> getUserIdsByGroupId(Long userGroupId, Integer start, Integer count);
	List<String> getUserNamesByGroupId(Long userGroupId, Integer start, Integer count);

	UserGroupMember getUserGroupMemberInfo(Long userGroupId,Long userId);
	Long addGroupMember(Long userGroupId,Long userId);
	Long addGroupMember(Long userGroupId,String userName);
	String deleteGroupMember(Long userGroupId,Long userId);
	String deleteGroupMember(Long userGroupId,String userName);

	//#endregion


	//#region User Dynamic Query
	ResultListPageable<User> getUsers(Map<String, Object> requestParams);

	//#endregion User Dynamic Query

}
