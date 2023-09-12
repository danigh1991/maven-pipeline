package com.core.datamodel.model.dbmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.core.datamodel.model.view.MyJsonView;
import com.core.datamodel.util.ShareUtils;
import com.core.datamodel.util.json.JsonDateShortSerializer;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.datamodel.util.json.JsonDateTimeShortSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.jboss.aerogear.security.otp.api.Base32;

@Getter
@Setter
@Entity
@Table(name = User.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = User.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "usr_id")),
        @AttributeOverride(name = "version", column = @Column(name = "usr_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "usr_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "usr_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "usr_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "usr_modify_by"))
})
public class User  extends AbstractBaseEntity<Long> {
	private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_user";

    @Column(name = "usr_username",nullable = false)
    @JsonView(MyJsonView.UserInfoView.class)
    private String username;

    @Column(name = "usr_password",nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "usr_unique_code")
    private String uniqueCode;

    @Column(name = "usr_first_name")
    @JsonView(MyJsonView.UserInfoList.class)
    private String firstName;

    @Column(name = "usr_last_name")
    @JsonView(MyJsonView.UserInfoList.class)
    private String lastName;

    @Column(name = "usr_mobilenumber")
    @JsonView(MyJsonView.UserInfoView.class)
    private String mobileNumber;

    @Column(name = "usr_email")
    @JsonView(MyJsonView.UserInfoView.class)
    private String email;


    @Column(name = "usr_address")
    @JsonView(MyJsonView.UserInfoView.class)
    private String address;

    @Column(name = "usr_state",nullable = false)
    private Integer state;

    @Column(name = "usr_avatar")
    private String avatar;

    @JsonView(MyJsonView.UserInfoView.class)
    @Column(name = "usr_gender",nullable = false)
    private Integer gender;

    @JsonView({MyJsonView.UserInfoViewFull.class})
    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnoreProperties("users")
    @JoinTable(name = "sc_user_roles",
            joinColumns = @JoinColumn(name = "url_usr_id",nullable = false, referencedColumnName = "usr_id", foreignKey = @ForeignKey(name = "FK_url_usr_id")),
            inverseJoinColumns = @JoinColumn(name = "url_rol_id",nullable = false,  referencedColumnName = "rol_id", foreignKey = @ForeignKey(name = "FK_url_rol_id")))
    private List<Role> roles;

    @JsonView(MyJsonView.UserInfoViewFull.class)
    @Column(name = "usr_limit_ip")
    private String limitIp;

    @Column(name = "usr_accountnonexpired",nullable = false)
    private boolean accountNonExpired;

    @JsonView({MyJsonView.UserInfoViewFull.class})
    @Column(name = "usr_account_expire_date")
    private Date expireDate;

    @JsonView({MyJsonView.UserInfoViewFull.class})
    @Column(name = "usr_accountnonlocked",nullable = false)
    private boolean accountNonLocked;

    @Column(name = "usr_account_locked_to")
    private Date accountLockedTo;

    @JsonView({MyJsonView.UserInfoViewFull.class})
    @Column(name = "usr_credentialsnonexpired",nullable = false)
    private boolean credentialsNonExpired;

    @JsonView({MyJsonView.UserInfoViewFull.class})
    @Column(name = "usr_credential_change_date")
    private Date credentialChangeDate;

    @JsonView({MyJsonView.UserInfoViewFull.class})
    @Column(name = "usr_enabled",nullable = false)
    private boolean enabled;

    @Transient
    @JsonIgnore
    private Integer activationCode;

    @Transient
    @JsonIgnore
    private String tmpPass;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usr_cty_id",referencedColumnName = "cty_id", foreignKey = @ForeignKey(name = "FK_usr_cty_id"))
    @JsonIgnoreProperties("users")
    @JsonView(MyJsonView.UserInfoView.class)
    private City city;

    @Column(name = "usr_send_notify",nullable = false)
    @JsonView(MyJsonView.UserInfoView.class)
    private boolean sendNotify=true;

    @Column(name = "usr_send_sms",nullable = false)
    @JsonView(MyJsonView.UserInfoView.class)
    private boolean sendSms=true;

    @Column(name = "usr_send_email",nullable = false)
    @JsonView(MyJsonView.UserInfoView.class)
    private boolean sendEmail=true;

    @JsonView(MyJsonView.UserInfoView.class)
    @Column(name = "usr_alias_name")
    private String aliasName;

    @JsonView(MyJsonView.UserInfoView.class)
    @Column(name = "usr_show_alias_name")
    private Boolean showAliasName=false;

    @Column(name = "usr_last_login")
    private Date lastLogin;

    @JsonView(MyJsonView.UserInfoView.class)
    @Column(name = "usr_affiliate_code")
    private String affiliateCode;

    @JsonView(MyJsonView.UserInfoView.class)
    @Column(name = "usr_affiliate_reagent")
    private String affiliateReagent;

    @Column(name = "usr_affiliate_rate",nullable = false)
    private Double affiliateRate=0d;

    @Column(name = "usr_affiliate_reagent_rate",nullable = false)
    private Double affiliateReagentRate=0d;

    @JsonView(MyJsonView.UserInfoView.class)
    @Column(name = "usr_using_2fa",nullable = false)
    private Boolean using2FA=false;

    @Column(name = "usr_secret")
    private String secret;

    @JsonView(MyJsonView.UserInfoViewFull.class)
    @Column(name = "usr_login_type")
    private Long loginType;

    @JsonView(MyJsonView.UserInfoView.class)
    @Column(name = "usr_verified")
    private Boolean verified=false;

    @Column(name = "usr_verify_response")
    private String verifyResponse;


    @Transient
    private transient List<String> limitIpList=null;


   /* @OneToMany(targetEntity=DiscountItemLike.class,fetch = FetchType.LAZY,mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("user")
    private List<DiscountItemLike> discountItemLikes =new ArrayList<>();*/


/*    @OneToOne(fetch = FetchType.LAZY,
            cascade =  CascadeType.ALL,
            mappedBy = "user")
    @JsonIgnoreProperties("user")
    private UserMeta userMeta;*/

    public User() {
        super();
        this.secret = Base32.random();
    }

    @JsonView(MyJsonView.UserInfoView.class)
    @JsonSerialize(using=JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }
    @JsonSerialize(using=JsonDateTimeSerializer.class)
    public Date getLastModified() {
        return modifyDate;
    }

    private String getLogoVersion(){
        if (this.modifyDate==null)
            return "?v=1";
        return  "?v=" + String.valueOf(createDate.getTime()-modifyDate.getTime()==0?1:createDate.getTime()-modifyDate.getTime());
    }

    @JsonView({MyJsonView.UserInfoView.class})
    public String getLogoPath() {
        return "/u/" + getId() +getLogoVersion();
    }

    @JsonSerialize(using=JsonDateTimeSerializer.class)
    public Date getLastLogin() {
        return lastLogin;
    }

    @JsonView(MyJsonView.UserInfoList.class)
    public String getRightName(){
        return !BaseUtils.isStringSafeEmpty(this.getLastName()) ?
                                      (!BaseUtils.isStringSafeEmpty(this.getFirstName()) ?
                                        this.getFirstName() +" ": "" ) + this.getLastName()
                : BaseUtils.getMessageResource("global.user") +this.getId().toString();

    }

    @JsonView(MyJsonView.UserInfoView.class)
    @JsonSerialize(using= JsonDateShortSerializer.class)
    public Date getExpireDate() {
        return expireDate;
    }

    @JsonView(MyJsonView.UserInfoView.class)
    @JsonSerialize(using= JsonDateTimeShortSerializer.class)
    public Date getCredentialChangeDate() {
        return credentialChangeDate;
    }

    @JsonView({MyJsonView.UserInfoViewFull.class})
    public List<String> getLimitIpList() {
        if(this.limitIpList==null) {
            if (!ShareUtils.isStringSafeEmpty(this.limitIp))
                this.limitIpList=Arrays.asList(this.limitIp.split("\\|"));
            else
                this.limitIpList=new ArrayList<>();
        }
        return this.limitIpList;
    }

    @JsonView(MyJsonView.UserInfoView.class)
    public String getGenderCaption(){
        if (this.getGender()==null)
            return "";
        return this.getGender() == 1 ? ShareUtils.getMessageResource("loginModal.sex.man") : ShareUtils.getMessageResource("loginModal.sex.woman");
    }
}

