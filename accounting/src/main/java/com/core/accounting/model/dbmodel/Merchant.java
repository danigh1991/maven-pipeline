package com.core.accounting.model.dbmodel;

import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.model.wrapper.MerchantWrapper;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name="merchantWrapperMapping",
                classes={
                        @ConstructorResult(
                                targetClass= MerchantWrapper.class,
                                columns={
                                        @ColumnResult(name="id",type = Long.class),
                                        @ColumnResult(name="merchantCategoryId",type = Long.class),
                                        @ColumnResult(name="merchantCategoryName",type = String.class),
                                        @ColumnResult(name="merchantCategoryCode",type = Integer.class),
                                        @ColumnResult(name="userId",type = Long.class),
                                        @ColumnResult(name="userName",type = String.class),
                                        @ColumnResult(name="name",type = String.class),
                                        @ColumnResult(name="description",type = String.class),
                                        @ColumnResult(name="active",type = Boolean.class),
                                        @ColumnResult(name="address",type = String.class),
                                        @ColumnResult(name="lat",type = Double.class),
                                        @ColumnResult(name="lan",type = Double.class),
                                        @ColumnResult(name="postalCode",type = String.class),
                                        @ColumnResult(name="email",type = String.class),
                                        @ColumnResult(name="mobileNumber",type = String.class),
                                        @ColumnResult(name="phoneNumber",type = String.class),
                                        @ColumnResult(name="cityId",type = Long.class),
                                        @ColumnResult(name="cityName",type = String.class),
                                        @ColumnResult(name="regionId",type = Long.class),
                                        @ColumnResult(name="regionName",type = String.class),
                                        @ColumnResult(name="wallet",type = Boolean.class),
                                        @ColumnResult(name="card",type = Boolean.class),
                                        @ColumnResult(name="cardNumber",type = String.class),
                                        @ColumnResult(name="otherMerchantViewPolicy",type = Integer.class),
                                        @ColumnResult(name="createBy",type = Long.class),
                                        @ColumnResult(name="createDate",type = Date.class),
                                        @ColumnResult(name="modifyBy",type = Long.class),
                                        @ColumnResult(name="modifyDate",type = Date.class)
                                }
                        )
                }
        )
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "Merchant.findMerchantWrapperInfoByUserAccountPolicyCreditDetailId",
                query = "select distinct m.mrc_id as id,mg.mrg_id as merchantCategoryId, mg.mrg_name as merchantCategoryName, mg.mrg_code as merchantCategoryCode ,  m.mrc_usr_id as userId, u.usr_username as userName,\n" +
                        "                             m.mrc_name as name, m.mrc_description as description, m.mrc_active as active,   m.mrc_address as address, m.mrc_lat as lat,   m.mrc_lan as lan, m.mrc_postal_code as postalCode,\n" +
                        "                             m.mrc_email as email, m.mrc_mobile_number as mobileNumber, m.mrc_phone_number as phoneNumber,  m.mrc_cty_id as cityId, c.cty_name as cityName, m.mrc_crg_id as regionId,\n" +
                        "                             r.crg_name as regionName, m.mrc_wallet as wallet, m.mrc_card as card, m.mrc_card_number as cardNumber, m.mrc_other_merchant_view_policy as otherMerchantViewPolicy ,\n" +
                        "                             m.mrc_create_by as createBy,  m.mrc_create_date as createDate, m.mrc_modify_by as modifyBy,  m.mrc_modify_date as modifyDate\n" +
                        "                    from    (select distinct cml.cml_usr_id,aml.aml_usr_id,nvl(cml.cml_usr_id,aml.aml_usr_id) as merchantUserId\n" +
                        "                                        from sc_account_credit_detail acd\n" +
                        "                                        inner join sc_account ac on (acd.acd_acc_id=ac.acc_id)\n" +
                        "                                        inner join sc_user_account_policy_credit_detail uac on (uac.uac_acd_id=acd.acd_id)\n" +
                        "                                        inner join sc_user_account_policy_profile uap on(uac.uac_uap_id= uap.uap_id)\n" +
                        "                                        left join  (select l.aml_acc_id ,l.aml_target_id as aml_usr_id from sc_account_merchant_limit l\n" +
                        "                                                    where l.aml_type=2\n" +
                        "                                                    union\n" +
                        "                                                    select l.aml_acc_id,g.ugm_usr_id as aml_usr_id from sc_account_merchant_limit l\n" +
                        "                                                    inner join sc_user_group_member g on (l.aml_type=1 and l.aml_target_id=g.ugm_usg_id)) aml on(aml.aml_acc_id=acd.acd_acc_id)\n" +
                        "                                        left join   (select c.cml_acd_id ,c.cml_target_id as cml_usr_id from sc_account_credit_merchant_limit c\n" +
                        "                                                     where c.cml_type=2\n" +
                        "                                                     union\n" +
                        "                                                     select c.cml_acd_id,g.ugm_usr_id as cml_usr_id from sc_account_credit_merchant_limit c\n" +
                        "                                                     inner join sc_user_group_member g on (c.cml_type=1 and c.cml_target_id=g.ugm_usg_id)) cml on(cml.cml_acd_id=acd.acd_id)\n" +
                        "                                         where acd.acd_active=1 and uac.uac_active=1   and ac.acc_act_id in (4,5) and uac.uac_id=:userAccountPolicyCreditDetailId\n" +
                        "                                          and (acd.acd_expire_date is null or (acd.acd_expire_date is not null and acd.acd_expire_date>=UTC_TIMESTAMP()))) k\n" +
                        "                                         inner  join sc_merchant m  on (m.mrc_usr_id=k.merchantuserid)\n" +
                        "                                         inner join sc_merchant_category mg on (m.mrc_mrg_id=mg.mrg_id)\n" +
                        "                                         inner join sc_user u on (m.mrc_usr_id=u.usr_id)\n" +
                        "                                         inner join sc_city c on (m.mrc_cty_id=c.cty_id)\n" +
                        "                                         inner join sc_city_region r on (c.cty_id=r.crg_cty_id and m.mrc_crg_id=r.crg_id)\n" +
                        "                     where  m.mrc_active=1" ,
                resultSetMapping = "merchantWrapperMapping"),
        @NamedNativeQuery(name = "Merchant.findMerchantWrapperInfoByUserAccountPolicyCreditDetailIdAndUserId",
                query = "select distinct m.mrc_id as id,mg.mrg_id as merchantCategoryId, mg.mrg_name as merchantCategoryName, mg.mrg_code as merchantCategoryCode ,  m.mrc_usr_id as userId, u.usr_username as userName,\n" +
                        "                             m.mrc_name as name, m.mrc_description as description, m.mrc_active as active,   m.mrc_address as address, m.mrc_lat as lat,   m.mrc_lan as lan, m.mrc_postal_code as postalCode,\n" +
                        "                             m.mrc_email as email, m.mrc_mobile_number as mobileNumber, m.mrc_phone_number as phoneNumber,  m.mrc_cty_id as cityId, c.cty_name as cityName, m.mrc_crg_id as regionId,\n" +
                        "                             r.crg_name as regionName, m.mrc_wallet as wallet, m.mrc_card as card, m.mrc_card_number as cardNumber, m.mrc_other_merchant_view_policy as otherMerchantViewPolicy ,\n" +
                        "                             m.mrc_create_by as createBy,  m.mrc_create_date as createDate, m.mrc_modify_by as modifyBy,  m.mrc_modify_date as modifyDate\n" +
                        "                    from    (select distinct cml.cml_usr_id,aml.aml_usr_id,nvl(cml.cml_usr_id,aml.aml_usr_id) as merchantUserId\n" +
                        "                                        from sc_account_credit_detail acd\n" +
                        "                                        inner join sc_account ac on (acd.acd_acc_id=ac.acc_id)\n" +
                        "                                        inner join sc_user_account_policy_credit_detail uac on (uac.uac_acd_id=acd.acd_id)\n" +
                        "                                        inner join sc_user_account_policy_profile uap on(uac.uac_uap_id= uap.uap_id)\n" +
                        "                                        left join  (select l.aml_acc_id ,l.aml_target_id as aml_usr_id from sc_account_merchant_limit l\n" +
                        "                                                    where l.aml_type=2\n" +
                        "                                                    union\n" +
                        "                                                    select l.aml_acc_id,g.ugm_usr_id as aml_usr_id from sc_account_merchant_limit l\n" +
                        "                                                    inner join sc_user_group_member g on (l.aml_type=1 and l.aml_target_id=g.ugm_usg_id)) aml on(aml.aml_acc_id=acd.acd_acc_id)\n" +
                        "                                        left join   (select c.cml_acd_id ,c.cml_target_id as cml_usr_id from sc_account_credit_merchant_limit c\n" +
                        "                                                     where c.cml_type=2\n" +
                        "                                                     union\n" +
                        "                                                     select c.cml_acd_id,g.ugm_usr_id as cml_usr_id from sc_account_credit_merchant_limit c\n" +
                        "                                                     inner join sc_user_group_member g on (c.cml_type=1 and c.cml_target_id=g.ugm_usg_id)) cml on(cml.cml_acd_id=acd.acd_id)\n" +
                        "                                         where acd.acd_active=1 and uac.uac_active=1   and ac.acc_act_id in (4,5) and uac.uac_id=:userAccountPolicyCreditDetailId\n" +
                        "                                          and uap_usr_id=:userId and (acd.acd_expire_date is null or (acd.acd_expire_date is not null and acd.acd_expire_date>=UTC_TIMESTAMP()))) k\n" +
                        "                                         inner  join sc_merchant m  on (m.mrc_usr_id=k.merchantuserid)\n" +
                        "                                         inner join sc_merchant_category mg on (m.mrc_mrg_id=mg.mrg_id)\n" +
                        "                                         inner join sc_user u on (m.mrc_usr_id=u.usr_id)\n" +
                        "                                         inner join sc_city c on (m.mrc_cty_id=c.cty_id)\n" +
                        "                                         inner join sc_city_region r on (c.cty_id=r.crg_cty_id and m.mrc_crg_id=r.crg_id)\n" +
                        "                     where  m.mrc_active=1",
                resultSetMapping = "merchantWrapperMapping")
})
@Table(name= Merchant.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = Merchant.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "mrc_id")),
        @AttributeOverride(name = "version", column = @Column(name = "mrc_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "mrc_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "mrc_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "mrc_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "mrc_modify_by"))
})
public class Merchant extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_merchant";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mrc_mrg_id",referencedColumnName = "mrg_id", foreignKey = @ForeignKey(name = "fk_mrc_mrg_id"),nullable = false)
    @JsonIgnoreProperties("merchants")
    private MerchantCategory merchantCategory;

    @JsonView(AccountJsonView.MerchantList.class)
    @Column(name="mrc_usr_id")
    private Long userId;

    @JsonView(AccountJsonView.MerchantList.class)
    @Column(name="mrc_name")
    private String name;

    @JsonView(AccountJsonView.MerchantDetail.class)
    @Column(name="mrc_description")
    private String description;

    @JsonView(AccountJsonView.MerchantList.class)
    @Column(name="mrc_active")
    private Boolean active;

    @JsonView(AccountJsonView.MerchantDetail.class)
    @Column(name="mrc_address")
    private String address;

    @JsonView(AccountJsonView.MerchantDetail.class)
    @Column(name = "mrc_lat")
    private Double lat;

    @JsonView(AccountJsonView.MerchantDetail.class)
    @Column(name = "mrc_lan")
    private Double lan;

    @JsonView(AccountJsonView.MerchantDetail.class)
    @Column(name = "mrc_postal_code")
    private String postalCode;

    @JsonView(AccountJsonView.MerchantDetail.class)
    @Column(name = "mrc_email")
    private String email;

    @JsonView(AccountJsonView.MerchantDetail.class)
    @Column(name = "mrc_mobile_number")
    private String mobileNumber;

    @JsonView(AccountJsonView.MerchantDetail.class)
    @Column(name = "mrc_phone_number")
    private String phoneNumber;

    @JsonView(AccountJsonView.MerchantDetail.class)
    @Column(name = "mrc_cty_id")
    private Long cityId;

    @JsonView(AccountJsonView.MerchantDetail.class)
    @Column(name = "mrc_crg_id")
    private Long regionId;

    @JsonView(AccountJsonView.MerchantDetail.class)
    @Column(name = "mrc_wallet")
    private Boolean wallet=true;

    @JsonView(AccountJsonView.MerchantDetail.class)
    @Column(name = "mrc_card")
    private Boolean card=false;

    @JsonView(AccountJsonView.MerchantDetail.class)
    @Column(name = "mrc_card_number")
    private String cardNumber;

    @JsonView(AccountJsonView.MerchantDetail.class)
    @Column(name = "mrc_other_merchant_view_policy")
    private Integer otherMerchantViewPolicy;

    @JsonView(AccountJsonView.MerchantDetail.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonView(AccountJsonView.MerchantDetail.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }


    @JsonView(AccountJsonView.MerchantList.class)
    public Long getMerchantCategoryId() {
        return merchantCategory.getId();
    }
}
