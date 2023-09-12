package com.core.common.services.impl;

import com.core.common.services.CommonService;
import com.core.common.util.Utils;
import com.core.datamodel.model.dbmodel.Permission;
import com.core.datamodel.model.securitymodel.UserPrincipalDetails;
import com.core.datamodel.model.wrapper.ActivityWrapper;
import com.core.datamodel.repository.ActivityRepository;
import com.core.datamodel.repository.PermissionRepository;
import com.core.datamodel.services.CacheService;
import com.core.common.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.core.datamodel.model.dbmodel.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service("appUserDetailsService")
public class AppUserDetailsService implements UserDetailsService {
	@Autowired
	private UserService userService;

	@Autowired
	private CommonService commonService;

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private PermissionRepository permissionRepository;
	@Autowired
	private CacheService cacheService;
	@Value("${jwt.expires_in}")
	private int EXPIRES_IN;
	@Autowired
	ObjectMapper jsonMapper;

	@Override
	public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

		UserDetails result = (UserDetails) cacheService.getCacheValue("userDetailsByUserName", Utils.addGlobalCacheKey(s));
		if (result != null){
			((UserPrincipalDetails)result).setUser(userService.getUserInfo(((UserPrincipalDetails)result).getId()));
			if (result.isEnabled() && result.isAccountNonExpired() && result.isAccountNonLocked() && result.isCredentialsNonExpired())
			    return result;
		}
		User user = userService.getUserByUserNameOrEmail(s);

		List<Long> roleIds=new ArrayList<>();
		user.getRoles().forEach(role -> {
			roleIds.add(role.getId());
		});

		//String jsonString = jsonMapper.writerWithView(MyJsonView.PanelMenu.class).writeValueAsString(panelMenuRepository.findPanelMenuByRoleIds(roleIds));
		List<ActivityWrapper> panelMenus = activityRepository.findPanelMenuByRoleIds(1,roleIds);
		panelMenus.forEach(pm -> { pm.setRoleFilterIds(roleIds);});
        List<ActivityWrapper> adminPanelMenus = activityRepository.findPanelMenuByRoleIds(2,roleIds);
		adminPanelMenus.forEach(pm -> { pm.setRoleFilterIds(roleIds);});
	    List<Permission> permissions =permissionRepository.findPermissionByRole(roleIds);
		result= new UserPrincipalDetails(user,permissions,panelMenus,adminPanelMenus,commonService.getPasswordValidityDay());

		cacheService.putCacheValue("userDetailsByUserName", Utils.addGlobalCacheKey(s), result,EXPIRES_IN, TimeUnit.SECONDS);
		return result;
	}
}
