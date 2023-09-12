package com.core.datamodel.model.securitymodel;

import com.core.datamodel.model.dbmodel.Permission;
import com.core.datamodel.model.dbmodel.User;
import com.core.datamodel.model.enums.ERoleType;
import com.core.datamodel.model.wrapper.ActivityWrapper;
import com.core.datamodel.util.ShareUtils;
import com.core.model.securitymodel.IUserPrincipalDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;
import java.io.Serializable;
import java.util.*;

public class UserPrincipalDetails implements IUserPrincipalDetails {
    private User user;
    private ERoleType roleType =ERoleType.USER;
    private final List<String> roles =new ArrayList<>();
    private final Set<GrantedAuthority> authorities;
    private List<ActivityWrapper> panelMenus;
    private List<ActivityWrapper> adminPanelMenus;
    private Integer passwordValidityDay=null;

    public UserPrincipalDetails(User user,List<Permission> permissions, List<ActivityWrapper> panelMenus, List<ActivityWrapper> adminPanelMenus,Integer passwordValidityDay) {
        this.user = user;
        List<Long> roleIds=new ArrayList<>();
        user.getRoles().forEach(role -> {
            roles.add(role.getRoleName());
            roleIds.add(role.getId());
            if(role.getRoleType()==ERoleType.ADMIN.getId())
                this.roleType=ERoleType.ADMIN;
        });
        this.authorities=Collections.unmodifiableSet(sortAuthorities(getAuthorities(permissions)));
        this.panelMenus=panelMenus;
        this.adminPanelMenus=adminPanelMenus;
        this.passwordValidityDay=passwordValidityDay;
    }

    @Override
    public Long getId() {
        return user.getId();
    }
    public User getUser() {
        return user;
    }
    @Override
    public String getFirstName() {
        return user.getFirstName();
    }
    @Override
    public String getLastName() {
        return user.getLastName();
    }
    @Override
    public String getAliasName() {
        return user.getAliasName();
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public String getMobileNumber() {
        return user.getMobileNumber();
    }

    @Override
    public Integer getGender() {
        return user.getGender();
    }
    public List<ActivityWrapper> getPanelMenu(){
        return this.panelMenus ;
    }
    public List<ActivityWrapper> getAdminPanelMenus() {
        return adminPanelMenus;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       /* List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        });*/
        return authorities;
    }

    @Override
    public List<String> getRoles() {
        return roles;
    }

    @Override
    public Integer getRoleType() {
        return this.roleType.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        if(user.getExpireDate()==null || user.getExpireDate().getTime()>=(new Date()).getTime())
            return true;
       return  false; //user.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked() && (user.getAccountLockedTo()==null ||(user.getAccountLockedTo()!=null && user.getAccountLockedTo().getTime()<ShareUtils.getCurrentDateTime().getTime()));
    }

    @Override
    public boolean isCredentialsNonExpired() {
        if (!user.isCredentialsNonExpired())
            return false;

        if (this.passwordValidityDay!=null &&  this.passwordValidityDay>0 && ShareUtils.addDayToDate(user.getCredentialChangeDate(),this.passwordValidityDay).getTime()<=(new Date()).getTime())
           return  false;

        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    private static SortedSet<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
        SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet(new AuthorityComparator());
        Iterator var2 = authorities.iterator();

        while(var2.hasNext()) {
            GrantedAuthority grantedAuthority = (GrantedAuthority)var2.next();
            Assert.notNull(grantedAuthority, "GrantedAuthority list cannot contain any null elements");
            sortedAuthorities.add(grantedAuthority);
        }

        return sortedAuthorities;
    }

    public void setUser(User user) {
        this.user = user;
    }


    private Collection<? extends GrantedAuthority> getAuthorities(
            Collection<Permission>  permissions) {

        return getGrantedAuthorities(getPrivileges(permissions));
    }

    private List<String> getPrivileges(Collection<Permission> permissions) {

        List<String> permissionNames = new ArrayList<>();
/*
        List<Permission> collection = new ArrayList<>();
        for (Role role : roles) {
            collection.addAll(role.getPermissions());
        }
*/
        for (Permission item : permissions) {
            permissionNames.add(item.getName());
        }
        return permissionNames;
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    private static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {
        private static final long serialVersionUID = 420L;

        private AuthorityComparator() {
        }

        public int compare(GrantedAuthority g1, GrantedAuthority g2) {
            if (g2.getAuthority() == null) {
                return -1;
            } else {
                return g1.getAuthority() == null ? 1 : g1.getAuthority().compareTo(g2.getAuthority());
            }
        }
    }
}
