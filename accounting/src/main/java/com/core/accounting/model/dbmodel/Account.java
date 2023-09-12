package com.core.accounting.model.dbmodel;

import com.core.accounting.model.view.AccountJsonView;
import com.core.accounting.model.wrapper.AccountWrapper;
import com.core.accounting.model.wrapper.AccountingDashboardDetailWrapper;
import com.core.accounting.model.wrapper.AccountingDashboardWrapper;
import com.core.common.util.Utils;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "accountWrapperMapping",
                classes = {
                        @ConstructorResult(
                                targetClass = AccountWrapper.class,
                                columns = {
                                        @ColumnResult(name = "id", type =Long.class),
                                        @ColumnResult(name = "name", type =String.class),
                                        @ColumnResult(name = "userId", type =Long.class),
                                        @ColumnResult(name = "main", type =Boolean.class),
                                        @ColumnResult(name = "accountTypeId", type =Long.class),
                                        @ColumnResult(name = "accountTypeName", type = String.class),
                                        @ColumnResult(name = "accountTypeDesc", type = String.class),
                                        @ColumnResult(name = "status", type =Integer.class),
                                        @ColumnResult(name = "balance", type =Double.class),
                                        @ColumnResult(name = "block", type =Double.class),
                                        @ColumnResult(name = "capacity", type =Double.class),
                                        @ColumnResult(name = "description", type =String.class),
                                        @ColumnResult(name = "color", type =String.class),
                                        @ColumnResult(name = "theme_id", type =Long.class),
                                        @ColumnResult(name = "createDate", type =Date.class),
                                        @ColumnResult(name = "modifyDate", type =Date.class),
                                        @ColumnResult(name = "accountCreditId", type =Long.class),
                                        @ColumnResult(name = "accountCreditTitle", type =String.class),
                                        @ColumnResult(name = "accountCreditDescription", type =String.class),
                                        @ColumnResult(name = "creditType", type =Integer .class),
                                        @ColumnResult(name = "creditRate", type =Double.class),
                                        @ColumnResult(name = "creditAmount", type =Double.class),
                                        @ColumnResult(name = "creditActive", type =Boolean.class),
                                        @ColumnResult(name = "expireDate", type =Date.class),
                                        @ColumnResult(name = "accountPolicyProfileId", type =Long.class),
                                        @ColumnResult(name = "viewType", type =Integer.class),
                                        @ColumnResult(name = "userCreditId", type =Long.class),
                                        @ColumnResult(name = "version", type =Long.class)
                                }
                        )
                }
        ),
        @SqlResultSetMapping(
                name = "accountingDashboardWrapperMapping",
                classes = {
                        @ConstructorResult(
                                targetClass = AccountingDashboardWrapper.class,
                                columns = {
                                        @ColumnResult(name = "registerUserCount", type =Integer.class),
                                        @ColumnResult(name = "activeMerchantCount", type =Integer.class),
                                        @ColumnResult(name = "sumAllBalance", type =Double.class),
                                        @ColumnResult(name = "sumCreditBalance", type =Double.class),
                                        @ColumnResult(name = "sumAllBlock", type =Double.class)
                                }
                        )
                }
        ),
        @SqlResultSetMapping(
                name = "accountingDashboardDetailWrapperMapping",
                classes = {
                        @ConstructorResult(
                                targetClass = AccountingDashboardDetailWrapper.class,
                                columns = {
                                        @ColumnResult(name = "summeryDate", type =Date.class),
                                        @ColumnResult(name = "registerUserCount", type =Integer.class),
                                        @ColumnResult(name = "registerMerchantCount", type =Integer.class),
                                        @ColumnResult(name = "sumChargeAmount", type =Double.class),
                                        @ColumnResult(name = "sumGetMoneyAmount", type =Double.class)
                                }
                        )
                }
        )
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "Account.getFirstAccountWrapperByUsrIdAndTypeId",
                query = "select distinct a.acc_id as id, a.acc_name as name,a.acc_usr_id as userId, a.acc_main as main,t.act_id as accountTypeId,t.act_name as accountTypeName, t.act_desc as  accountTypeDesc, \n" +
                        "       a.acc_status as status,a.acc_balance as balance,\n" +
                        "       a.acc_block as block, \n" +
                        "       case when nvl(t.act_max_amount,0)=0  then a.acc_capacity when nvl(t.act_max_amount,0)>=nvl(a.acc_capacity,0) then nvl(a.acc_capacity,t.act_max_amount) else nvl(t.act_max_amount,0) end  as capacity, \n" +
                        "       a.acc_description as description,\n" +
                        "       a.acc_color as color, a.acc_ptm_id as theme_id, a.acc_create_date as createDate,\n" +
                        "       a.acc_modif_date as modifyDate, null as accountCreditId, null as accountCreditTitle,\n" +
                        "       null as accountCreditDescription, null as creditType, 0 as creditRate,\n" +
                        "       null as creditAmount, null as creditActive, null as expireDate,uap.uap_apl_id  as accountPolicyProfileId, 1 as viewType, null as userCreditId, a.acc_version as version \n" +
                        "   from sc_account a\n" +
                        "  inner join sc_account_type t on (a.acc_act_id=t.act_id)\n" +
                        "  inner join sc_user_account_policy_profile uap on(a.acc_id=uap.uap_acc_id and uap.uap_usr_id=:userId)\n" +
                        " where a.acc_status=1 and a.acc_usr_id=:userId  and t.act_id=:accountTypeId  order by a.acc_main desc, a.acc_id asc limit 1 ",
                resultSetMapping = "accountWrapperMapping"),
        @NamedNativeQuery(name = "Account.getAccountWrapperById",
                query = "select distinct a.acc_id as id, a.acc_name as name,a.acc_usr_id as userId, a.acc_main as main,t.act_id as accountTypeId,t.act_name as accountTypeName, t.act_desc as  accountTypeDesc, \n" +
                        "       a.acc_status as status,a.acc_balance as balance,\n" +
                        "       a.acc_block as block, \n" +
                        "       case when nvl(t.act_max_amount,0)=0  then a.acc_capacity when nvl(t.act_max_amount,0)>=nvl(a.acc_capacity,0) then nvl(a.acc_capacity,t.act_max_amount) else nvl(t.act_max_amount,0) end  as capacity, \n" +
                        "       a.acc_description as description,\n" +
                        "       a.acc_color as color, a.acc_ptm_id as theme_id, a.acc_create_date as createDate,\n" +
                        "       a.acc_modif_date as modifyDate, null as accountCreditId, null as accountCreditTitle,\n" +
                        "       null as accountCreditDescription, null as creditType, 0 as creditRate,\n" +
                        "       null as creditAmount, null as creditActive, null as expireDate,uap.uap_apl_id  as accountPolicyProfileId, 1 as viewType, null as userCreditId, a.acc_version as version \n" +
                        "   from sc_account a\n" +
                        "  inner join sc_account_type t on (a.acc_act_id=t.act_id)\n" +
                        "  inner join sc_user_account_policy_profile uap on(a.acc_id=uap.uap_acc_id and uap.uap_usr_id=a.acc_usr_id)\n" +
                        " where a.acc_id=:accountId ",
                resultSetMapping = "accountWrapperMapping"),
        @NamedNativeQuery(name = "Account.getAccountWrapperByIdAndUserId",
                query = "select distinct a.acc_id as id, a.acc_name as name,a.acc_usr_id as userId, a.acc_main as main,t.act_id as accountTypeId,t.act_name as accountTypeName, t.act_desc as  accountTypeDesc,\n" +
                        "       a.acc_status as status,a.acc_balance as balance,\n" +
                        "       a.acc_block as block, \n" +
                        "       case when nvl(t.act_max_amount,0)=0  then a.acc_capacity when nvl(t.act_max_amount,0)>=nvl(a.acc_capacity,0) then nvl(a.acc_capacity,t.act_max_amount) else nvl(t.act_max_amount,0) end  as capacity, \n" +
                        "       a.acc_description as description,\n" +
                        "       a.acc_color as color, a.acc_ptm_id as theme_id, a.acc_create_date as createDate,\n" +
                        "       a.acc_modif_date as modifyDate, null as accountCreditId, null as accountCreditTitle,\n" +
                        "       null as accountCreditDescription, null as creditType, 0 as creditRate,\n" +
                        "       null as creditAmount, null as creditActive, null as expireDate,uap.uap_apl_id  as accountPolicyProfileId, 1 as viewType, null as userCreditId, a.acc_version as version \n" +
                        "   from sc_account a\n" +
                        "  inner join sc_account_type t on (a.acc_act_id=t.act_id)\n" +
                        "  inner join sc_user_account_policy_profile uap on(a.acc_id=uap.uap_acc_id and uap.uap_usr_id=a.acc_usr_id)\n" +
                        " where a.acc_id=:accountId and a.acc_usr_id=:userId", // or uap.uap_usr_id=:userId)
                resultSetMapping = "accountWrapperMapping"),

        @NamedNativeQuery(name = "Account.getAccountWrappersOnlyByUserId",
                query = "select distinct a.acc_id as id, a.acc_name as name,a.acc_usr_id as userId, a.acc_main as main,t.act_id as accountTypeId,t.act_name as accountTypeName, t.act_desc as  accountTypeDesc,\n" +
                        "       a.acc_status as status,a.acc_balance as balance,\n" +
                        "       a.acc_block as block, \n" +
                        "       case when nvl(t.act_max_amount,0)=0  then a.acc_capacity when nvl(t.act_max_amount,0)>=nvl(a.acc_capacity,0) then nvl(a.acc_capacity,t.act_max_amount) else nvl(t.act_max_amount,0) end  as capacity, \n" +
                        "       a.acc_description as description,\n" +
                        "       a.acc_color as color, a.acc_ptm_id as theme_id, a.acc_create_date as createDate,\n" +
                        "       a.acc_modif_date as modifyDate, null as accountCreditId, null as accountCreditTitle,\n" +
                        "       null as accountCreditDescription, null as creditType, 0 as creditRate,\n" +
                        "       null as creditAmount, null as creditActive, null as expireDate, uap.uap_apl_id  as accountPolicyProfileId, \n" +
                        "       null as viewType, null as userCreditId, a.acc_version as version \n" +
                        "   from sc_account a\n" +
                        "  inner join sc_account_type t on (a.acc_act_id=t.act_id)\n" +
                        "  inner join sc_user_account_policy_profile uap on(a.acc_id=uap.uap_acc_id and uap.uap_usr_id=:userId)\n" +
                        " where a.acc_status=1 and (a.acc_usr_id=:userId)",
                resultSetMapping = "accountWrapperMapping"),
        @NamedNativeQuery(name = "Account.getAccountWrappersOnlyByUserIdAndTypeId",
                query = "select distinct a.acc_id as id, a.acc_name as name,a.acc_usr_id as userId, a.acc_main as main,t.act_id as accountTypeId,t.act_name as accountTypeName, t.act_desc as  accountTypeDesc,\n" +
                        "       a.acc_status as status,a.acc_balance as balance,\n" +
                        "       a.acc_block as block, \n" +
                        "       case when nvl(t.act_max_amount,0)=0  then a.acc_capacity when nvl(t.act_max_amount,0)>=nvl(a.acc_capacity,0) then nvl(a.acc_capacity,t.act_max_amount) else nvl(t.act_max_amount,0) end  as capacity, \n" +
                        "       a.acc_description as description,\n" +
                        "       a.acc_color as color, a.acc_ptm_id as theme_id, a.acc_create_date as createDate,\n" +
                        "       a.acc_modif_date as modifyDate, null as accountCreditId, null as accountCreditTitle,\n" +
                        "       null as accountCreditDescription, null as creditType, 0 as creditRate,\n" +
                        "       null as creditAmount, null as creditActive, null as expireDate, uap.uap_apl_id  as accountPolicyProfileId, \n" +
                        "       null as viewType, null as userCreditId, a.acc_version as version \n" +
                        "   from sc_account a\n" +
                        "  inner join sc_account_type t on (a.acc_act_id=t.act_id)\n" +
                        "  inner join sc_user_account_policy_profile uap on(a.acc_id=uap.uap_acc_id and uap.uap_usr_id=:userId)\n" +
                        " where a.acc_status=1 and t.act_id=:accountTypeId and a.acc_usr_id=:userId",
                resultSetMapping = "accountWrapperMapping"),
        @NamedNativeQuery(name = "Account.getAccountWrappersByUserId",
                query = "select distinct a.acc_id as id, nvl(acd.acd_title,a.acc_name) as name,a.acc_usr_id as userId, a.acc_main as main,t.act_id as accountTypeId,t.act_name as accountTypeName, t.act_desc as  accountTypeDesc,\n" +
                        "       a.acc_status as status,if(nvl(acd.acd_credit_amount,-1)=-1 ,a.acc_balance,(acd.acd_credit_amount_per_user-uac.uac_used_amount)) as balance,\n" +
                        "       if(nvl(acd.acd_credit_amount,-1)=-1, a.acc_block,(acd.acd_block+uac.uac_block)) as block, \n" +
                        "       case when nvl(t.act_max_amount,0)=0  then a.acc_capacity when nvl(t.act_max_amount,0)>=nvl(a.acc_capacity,0) then nvl(a.acc_capacity,t.act_max_amount) else nvl(t.act_max_amount,0) end  as capacity, \n" +
                        "       a.acc_description as description,\n" +
                        "       a.acc_color as color, a.acc_ptm_id as theme_id, ifnull(acd.acd_create_date,a.acc_create_date) as createDate,\n" +
                        "       ifnull(acd.acd_modify_date,a.acc_modif_date) as modifyDate, acd.acd_id as accountCreditId, acd.acd_title as accountCreditTitle,\n" +
                        "       acd.acd_description as accountCreditDescription, acd.acd_credit_type as creditType, 0 as creditRate,\n" +
                        "       acd.acd_credit_amount as creditAmount, acd.acd_active as creditActive, acd.acd_expire_date as expireDate,uap.uap_apl_id  as accountPolicyProfileId, nvl(acd.acd_view_type,1) as viewType, uac.uac_id as userCreditId, a.acc_version as version \n" +
                        "   from sc_account a\n" +
                        "  inner join sc_account_type t on (a.acc_act_id=t.act_id)\n" +
                        "  inner join sc_user_account_policy_profile uap on(a.acc_id=uap.uap_acc_id and uap.uap_usr_id=:userId)\n" +
                        "  left join sc_user_account_policy_credit_detail uac on(uac.uac_uap_id=uap.uap_id)\n" +
                        "  left join sc_account_credit_detail acd on(uac.uac_acd_id=acd.acd_id and (acd.acd_expire_date is null or (acd.acd_expire_date is not null and acd.acd_expire_date>=UTC_TIMESTAMP())))\n" +
                        " where a.acc_status=1 and (t.act_id in (1,3) or (t.act_id in (4,5) and acd.acd_active=1)) and (a.acc_usr_id=:userId or (:forceOwner=false and uap.uap_usr_id=:userId))",
                resultSetMapping = "accountWrapperMapping"),
        @NamedNativeQuery(name = "Account.getAccountWrappersByUserIdAndTypeId",
                query = "select distinct a.acc_id as id, nvl(acd.acd_title,a.acc_name) as name,a.acc_usr_id as userId, a.acc_main as main,t.act_id as accountTypeId,t.act_name as accountTypeName, t.act_desc as  accountTypeDesc,\n" +
                        "       a.acc_status as status,if(nvl(acd.acd_credit_amount,-1)=-1 ,a.acc_balance,(acd.acd_credit_amount_per_user-uac.uac_used_amount)) as balance,\n" +
                        "       if(nvl(acd.acd_credit_amount,-1)=-1, a.acc_block,(acd.acd_block+uac.uac_block)) as block, \n" +
                        "       case when nvl(t.act_max_amount,0)=0  then a.acc_capacity when nvl(t.act_max_amount,0)>=nvl(a.acc_capacity,0) then nvl(a.acc_capacity,t.act_max_amount) else nvl(t.act_max_amount,0) end  as capacity, \n" +
                        "       a.acc_description as description,\n" +
                        "       a.acc_color as color, a.acc_ptm_id as theme_id, ifnull(acd.acd_create_date,a.acc_create_date) as createDate,\n" +
                        "       ifnull(acd.acd_modify_date,a.acc_modif_date) as modifyDate, acd.acd_id as accountCreditId, acd.acd_title as accountCreditTitle,\n" +
                        "       acd.acd_description as accountCreditDescription, acd.acd_credit_type as creditType, 0 as creditRate,\n" +
                        "       acd.acd_credit_amount as creditAmount, acd.acd_active as creditActive, acd.acd_expire_date as expireDate,uap.uap_apl_id  as accountPolicyProfileId, nvl(acd.acd_view_type,1) as viewType, uac.uac_id as userCreditId, a.acc_version as version \n" +
                        "   from sc_account a\n" +
                        "  inner join sc_account_type t on (a.acc_act_id=t.act_id)\n" +
                        "  inner join sc_user_account_policy_profile uap on(a.acc_id=uap.uap_acc_id and uap.uap_usr_id=:userId)\n" +
                        "  left join sc_user_account_policy_credit_detail uac on(uac.uac_uap_id=uap.uap_id)\n" +
                        "  left join sc_account_credit_detail acd on(uac.uac_acd_id=acd.acd_id and (acd.acd_expire_date is null or (acd.acd_expire_date is not null and acd.acd_expire_date>=UTC_TIMESTAMP())))\n" +
                        " where a.acc_status=1 and (t.act_id in (1,3) or (t.act_id in (4,5) and acd.acd_active=1)) and t.act_id=:accountTypeId and (a.acc_usr_id=:userId or (:forceOwner=false and uap.uap_usr_id=:userId))",
                resultSetMapping = "accountWrapperMapping"),
        @NamedNativeQuery(name = "Account.getAvailableAccountWrappersByOperationTypeCode",
                query = "select distinct a.acc_id as id, nvl(cr.acd_title,a.acc_name) as name,a.acc_usr_id as userId, a.acc_main as main,t.act_id as accountTypeId,t.act_name as accountTypeName, t.act_desc as  accountTypeDesc,\n" +
                        "    a.acc_status as status,if(nvl(cr.acd_credit_amount_per_user,-1)=-1,a.acc_balance,LEAST(cr.acd_credit_amount_per_user,(cr.allUserAmount-cr.uac_used_amount))) as balance,\n" +
                        "    if(nvl(cr.acd_credit_amount_per_user,-1)=-1,a.acc_block,(cr.acd_block+cr.uac_block)) as block,\n" +
                        "    case when nvl(t.act_max_amount,0)=0  then a.acc_capacity when nvl(t.act_max_amount,0)>=nvl(a.acc_capacity,0) then nvl(a.acc_capacity,t.act_max_amount) else nvl(t.act_max_amount,0) end  as capacity,\n" +
                        "    a.acc_description as description, a.acc_color as color, a.acc_ptm_id as theme_id, nvl(cr.acd_create_date,a.acc_create_date) as createDate,\n" +
                        "    nvl(cr.acd_modify_date,a.acc_modif_date) as modifyDate, cr.acd_id as accountCreditId, cr.acd_title as accountCreditTitle,\n" +
                        "    cr.acd_description as accountCreditDescription, cr.acd_credit_type as creditType, 0 as creditRate,\n" +
                        "    cr.acd_credit_amount as creditAmount, cr.acd_active as creditActive, cr.acd_expire_date as expireDate,uap.uap_apl_id  as accountPolicyProfileId,\n" +
                        "    nvl(cr.acd_view_type,1) as viewType, cr.uac_id as userCreditId, a.acc_version as version\n" +
                        "from sc_account a\n" +
                        "inner join sc_account_type t on (a.acc_act_id=t.act_id)\n" +
                        "inner join sc_user_account_policy_profile uap on(a.acc_id=uap.uap_acc_id and uap.uap_usr_id=:userId)\n" +
                        "inner join sc_account_policy_profile_operation_type  apt on(apt.apt_apl_id=uap.uap_apl_id)\n" +
                        "inner join sc_operation_type tt on(tt.opt_id=apt.apt_opt_id)\n" +
                        "left join\n" +
                        "      (SELECT distinct acd.acd_id,acd.acd_acc_id ,acd.acd_title, uac.uac_id, uac.uac_uap_id ,\n" +
                        "              if(acd.acd_spending_restrictions=0,acd.acd_credit_amount_per_user,\n" +
                        "                 if(nvl(acd.acd_max_amount_restrictions,0)=0,\n" +
                        "                    LEAST((acd.acd_rate_restrictions*:amount/100),acd.acd_credit_amount_per_user) ,acd.acd_credit_amount_per_user as allUserAmount,\n" +
                        "                    LEAST(LEAST((acd.acd_rate_restrictions*:amount/100),acd.acd_max_amount_restrictions),acd.acd_credit_amount_per_user)))  as acd_credit_amount_per_user ,\n" +
                        "              acd.acd_credit_type, uac.uac_used_amount, acd.acd_block,uac.uac_block,acd.acd_create_date,acd.acd_modify_date,acd.acd_description,acd.acd_credit_amount ,\n" +
                        "              acd.acd_active , acd.acd_expire_date ,acd.acd_view_type,cml.cml_usr_id,aml.aml_usr_id FROM sc_account_credit_detail acd                    \n" +
                        "        inner join sc_user_account_policy_credit_detail uac on(uac.uac_acd_id=acd.acd_id) \n" +
                        "        left join (select l.aml_acc_id ,l.aml_target_id as aml_usr_id from sc_Account_Merchant_Limit l\n" +
                        "                   where l.aml_type=2\n" +
                        "                   union\n" +
                        "                   select l.aml_acc_id,g.ugm_usr_id as aml_usr_id from sc_Account_Merchant_Limit l\n" +
                        "                   inner join sc_user_group_member g on (l.aml_type=1 and l.aml_target_id=g.ugm_usg_id)) aml on(aml.aml_acc_id=acd.acd_acc_id)\n" +
                        "        left join (select c.cml_acd_id ,c.cml_target_id as cml_usr_id from sc_account_credit_merchant_limit c\n" +
                        "                   where c.cml_type=2\n" +
                        "                   union\n" +
                        "                   select c.cml_acd_id,g.ugm_usr_id as cml_usr_id from sc_account_credit_merchant_limit c\n" +
                        "                   inner join sc_user_group_member g on (c.cml_type=1 and c.cml_target_id=g.ugm_usg_id)) cml on(cml.cml_acd_id=acd.acd_id)\n" +
                        "       where acd.acd_active=1 and uac.uac_active=1 and (acd.acd_expire_date is null or (acd.acd_expire_date is not null and acd.acd_expire_date>=UTC_TIMESTAMP()))) cr on(a.acc_id=cr.acd_acc_id and cr.uac_uap_id=uap.uap_id) \n" +
                        "where uap.uap_usr_id=:userId and tt.opt_code=:operationTypeCode and a.acc_status=1 \n" +
                        " and nvl(nvl(cr.cml_usr_id,cr.aml_usr_id),-1) in (-1,:targetUserId) \n" +
                        " and ((a.acc_act_id not in (4,5)) or (a.acc_act_id in (4,5) and cr.acd_id is not null))\n" +
                        " and  (if(nvl(cr.acd_credit_amount_per_user,-1)=-1,a.acc_balance,LEAST(cr.acd_credit_amount_per_user,(cr.allUserAmount-cr.uac_used_amount)))- \n" +
                        "       if(nvl(cr.acd_credit_amount_per_user,-1)=-1,a.acc_block,(cr.acd_block+cr.uac_block)))>0 \n" +
                        " order by t.act_id asc,cr.acd_expire_date asc,if(nvl(cr.acd_credit_amount_per_user,-1)=-1,a.acc_balance,LEAST(cr.acd_credit_amount_per_user,(cr.allUserAmount-cr.uac_used_amount))) desc",
                resultSetMapping = "accountWrapperMapping"),
        @NamedNativeQuery(name = "Account.getAvailableAccountWrappersByOperationTypeCodeAndIds",
                query = "select distinct a.acc_id as id, nvl(cr.acd_title,a.acc_name) as name,a.acc_usr_id as userId, a.acc_main as main,t.act_id as accountTypeId,t.act_name as accountTypeName, t.act_desc as  accountTypeDesc,\n" +
                        "    a.acc_status as status,if(nvl(cr.acd_credit_amount_per_user,-1)=-1,a.acc_balance,LEAST(cr.acd_credit_amount_per_user,(cr.allUserAmount-cr.uac_used_amount))) as balance,\n" +
                        "    if(nvl(cr.acd_credit_amount_per_user,-1)=-1,a.acc_block,(cr.acd_block+cr.uac_block)) as block,\n" +
                        "    case when nvl(t.act_max_amount,0)=0  then a.acc_capacity when nvl(t.act_max_amount,0)>=nvl(a.acc_capacity,0) then nvl(a.acc_capacity,t.act_max_amount) else nvl(t.act_max_amount,0) end  as capacity,\n" +
                        "    a.acc_description as description, a.acc_color as color, a.acc_ptm_id as theme_id, nvl(cr.acd_create_date,a.acc_create_date) as createDate,\n" +
                        "    nvl(cr.acd_modify_date,a.acc_modif_date) as modifyDate, cr.acd_id as accountCreditId, cr.acd_title as accountCreditTitle,\n" +
                        "    cr.acd_description as accountCreditDescription, cr.acd_credit_type as creditType, 0 as creditRate,\n" +
                        "    cr.acd_credit_amount as creditAmount, cr.acd_active as creditActive, cr.acd_expire_date as expireDate,uap.uap_apl_id  as accountPolicyProfileId,\n" +
                        "    nvl(cr.acd_view_type,1) as viewType, cr.uac_id as userCreditId, a.acc_version as version\n" +
                        "from sc_account a\n" +
                        "inner join sc_account_type t on (a.acc_act_id=t.act_id)\n" +
                        "inner join sc_user_account_policy_profile uap on(a.acc_id=uap.uap_acc_id and uap.uap_usr_id=:userId)\n" +
                        "inner join sc_account_policy_profile_operation_type  apt on(apt.apt_apl_id=uap.uap_apl_id)\n" +
                        "inner join sc_operation_type tt on(tt.opt_id=apt.apt_opt_id)\n" +
                        "left join\n" +
                        "      (SELECT distinct acd.acd_id,acd.acd_acc_id ,acd.acd_title, uac.uac_id,uac.uac_uap_id ,acd.acd_credit_amount_per_user as allUserAmount ,\n" +
                        "              if(acd.acd_spending_restrictions=0,acd.acd_credit_amount_per_user,\n" +
                        "                 if(nvl(acd.acd_max_amount_restrictions,0)=0,\n" +
                        "                    LEAST((acd.acd_rate_restrictions*:amount/100),acd.acd_credit_amount_per_user) ,\n" +
                        "                    LEAST(LEAST((acd.acd_rate_restrictions*:amount/100),acd.acd_max_amount_restrictions),acd.acd_credit_amount_per_user)))  as acd_credit_amount_per_user ,\n" +
                        "              acd.acd_credit_type, uac.uac_used_amount, acd.acd_block,uac.uac_block,acd.acd_create_date,acd.acd_modify_date,acd.acd_description,acd.acd_credit_amount ,\n" +
                        "              acd.acd_active , acd.acd_expire_date ,acd.acd_view_type,cml.cml_usr_id,aml.aml_usr_id FROM sc_account_credit_detail acd                    \n" +
                        "        inner join sc_user_account_policy_credit_detail uac on(uac.uac_acd_id=acd.acd_id) \n" +
                        "        left join (select l.aml_acc_id ,l.aml_target_id as aml_usr_id from sc_Account_Merchant_Limit l\n" +
                        "                   where l.aml_type=2\n" +
                        "                   union\n" +
                        "                   select l.aml_acc_id,g.ugm_usr_id as aml_usr_id from sc_Account_Merchant_Limit l\n" +
                        "                   inner join sc_user_group_member g on (l.aml_type=1 and l.aml_target_id=g.ugm_usg_id)) aml on(aml.aml_acc_id=acd.acd_acc_id)\n" +
                        "        left join (select c.cml_acd_id ,c.cml_target_id as cml_usr_id from sc_account_credit_merchant_limit c\n" +
                        "                   where c.cml_type=2\n" +
                        "                   union\n" +
                        "                   select c.cml_acd_id,g.ugm_usr_id as cml_usr_id from sc_account_credit_merchant_limit c\n" +
                        "                   inner join sc_user_group_member g on (c.cml_type=1 and c.cml_target_id=g.ugm_usg_id)) cml on(cml.cml_acd_id=acd.acd_id)\n" +
                        "       where acd.acd_active=1 and uac.uac_active=1 and (acd.acd_expire_date is null or (acd.acd_expire_date is not null and acd.acd_expire_date>=UTC_TIMESTAMP()))) cr on(a.acc_id=cr.acd_acc_id and cr.uac_uap_id=uap.uap_id) \n" +
                        "where uap.uap_usr_id=:userId and tt.opt_code=:operationTypeCode and a.acc_status=1 and a.acc_id in :accountIds \n" +
                        " and nvl(nvl(cr.cml_usr_id,cr.aml_usr_id),-1) in (-1,:targetUserId) \n" +
                        " and ((a.acc_act_id not in (4,5)) or (a.acc_act_id in (4,5) and cr.acd_id is not null))\n" +
                        " and  (if(nvl(cr.acd_credit_amount_per_user,-1)=-1,a.acc_balance,LEAST(cr.acd_credit_amount_per_user,(cr.allUserAmount-cr.uac_used_amount)))-\n" +
                        "       if(nvl(cr.acd_credit_amount_per_user,-1)=-1,a.acc_block,(cr.acd_block+cr.uac_block)))>0 \n"+
                        " order by t.act_id asc,cr.acd_expire_date asc,if(nvl(cr.acd_credit_amount_per_user,-1)=-1,a.acc_balance,LEAST(cr.acd_credit_amount_per_user,(cr.allUserAmount-cr.uac_used_amount))) desc",
                resultSetMapping = "accountWrapperMapping"),
        @NamedNativeQuery(name = "Account.findDashboardWrapperSummery",
                query = "SELECT u.registerUserCount, mr.activeMerchantCount,ac.sumAllBalance,ac.sumCreditBalance,ac.sumAllBlock  from \n" +
                        " (SELECT count(*)  as registerUserCount FROM sc_user) u,\n" +
                        " (SELECT count(*) as allMerchantCount, sum(if(m.mrc_active=1,1,0)) as activeMerchantCount FROM sc_merchant m ) mr,\n" +
                        " (SELECT sum(acc_balance) as sumAllBalance,sum(if(acc_act_id=5,acc_balance,0)) as sumCreditBalance, sum(acc_block) as sumAllBlock FROM sc_account ) ac",
                resultSetMapping = "accountingDashboardWrapperMapping"),
        @NamedNativeQuery(name = "Account.findDashboardDetailWrapperSummery",
                query = "select date(dates.dt) as summeryDate ,nvl(ur.registerUserCount,0) as registerUserCount, nvl(mr.registerMerchantCount,0) as registerMerchantCount,\n" +
                        "          nvl(tr.sumChargeAmount,0) as sumChargeAmount, nvl(tr.sumGetMoneyAmount,0)  as sumGetMoneyAmount FROM (\n" +
                        "WITH RECURSIVE dates(dt) AS \n" +
                        "(\n" +
                        "  SELECT ADDDATE(UTC_TIMESTAMP,INTERVAL -190 DAY) as dt from dual\n" +
                        "  UNION ALL\n" +
                        "  SELECT ADDDATE(dt,INTERVAL 1 DAY) as dt   FROM  dates\n" +
                        "  WHERE  ADDDATE(dt,INTERVAL 1 DAY)<=date(UTC_TIMESTAMP)\n" +
                        ")\n" +
                        "SELECT DATE(dt) as dt\n" +
                        "FROM  dates  ) dates\n" +
                        "left join (select distinct date(u.usr_create_date) as udate,count(distinct u.usr_id) as registerUserCount \n" +
                        "             from sc_user u group by date(u.usr_create_date)) ur on (ur.udate=dates.dt) \n" +
                        "left join (select distinct date(m.mrc_create_date) as mdate,count(distinct m.mrc_id) as registerMerchantCount  \n" +
                        "             from sc_merchant m group by date(m.mrc_create_date)) mr on (mr.mdate=dates.dt)             \n" +
                        "left join (select distinct date(t.trn_create_date) as tdate, sum(if(t.trn_opt_id=1,t.trn_credit,0)) as sumChargeAmount, sum(if(t.trn_opt_id=2,t.trn_debit,0)) as sumGetMoneyAmount \n" +
                        "             from sc_transaction  t  where t.trn_opt_id in (1,2)   group by date(t.trn_create_date))  tr  on (tr.tdate=dates.dt)\n" +
                        "WHERE  dates.dt<=date(UTC_TIMESTAMP)\n" +
                        "order by dates.dt ",
                resultSetMapping = "accountingDashboardDetailWrapperMapping")

})

@Table(name=Account.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = Account.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "acc_id")),
        @AttributeOverride(name = "version", column = @Column(name = "acc_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "acc_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "acc_modif_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "acc_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "acc_modify_by"))
})
public class Account extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_account";

    @Column(name="acc_name",nullable = false)
    @JsonView(AccountJsonView.AccountShortList.class)
    private String name;

    @Column(name="acc_usr_id",nullable = false)
    @JsonView(AccountJsonView.AccountShortList.class)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acc_act_id",nullable = false,referencedColumnName = "act_id", foreignKey = @ForeignKey(name = "FK_acc_act_id"))
    @JsonView(AccountJsonView.AccountList.class)
    @JsonIgnoreProperties("accounts")
    private AccountType accountType;

    @Column(name="acc_acg_id",nullable = false)
    @JsonView(AccountJsonView.AccountList.class)
    private Long accountCategoryId=1l;

    @Column(name="acc_status",nullable = false)
    @JsonView(AccountJsonView.AccountList.class)
    private Integer status;

    @Column(name="acc_balance",nullable = false)
    @JsonView(AccountJsonView.AccountShortList.class)
    private Double balance=0d;

    @Column(name="acc_block",nullable = false)
    @JsonView(AccountJsonView.AccountShortList.class)
    private Double block=0d;

    @Column(name="acc_capacity",nullable = false)
    @JsonView(AccountJsonView.AccountList.class)
    private Double capacity;

    @Column(name="acc_description")
    @JsonView(AccountJsonView.AccountDetails.class)
    private String description;

    @Column(name="acc_color")
    @JsonView(AccountJsonView.AccountDetails.class)
    private String color;

    @Column(name="acc_ptm_id")
    @JsonView(AccountJsonView.AccountDetails.class)
    private Long theme_id; //theme Id for color and background image , ...

    @Column(name="acc_main")
    @JsonView(AccountJsonView.AccountDetails.class)
    private Boolean main=false;

    @Column(name="acc_order")
    @JsonView(AccountJsonView.AccountDetails.class)
    private Integer order ;

    @OneToMany(targetEntity = AccountCreditDetail.class, mappedBy = "account", fetch =FetchType.LAZY)
    @JsonIgnoreProperties("account")
    private List<AccountCreditDetail> accountCreditDetails = new ArrayList<>();

    @OneToMany(targetEntity = Transaction.class, mappedBy = "account",fetch = FetchType.LAZY)
    @JsonIgnoreProperties("account")
    private List<Transaction> transactions = new ArrayList<Transaction>();

    @OneToMany(targetEntity = UserAccountPolicyProfile.class, mappedBy = "account", fetch =FetchType.LAZY)
    @JsonIgnoreProperties("account")
    private List<UserAccountPolicyProfile> userAccountPolicyProfile = new ArrayList<>();


    @OneToMany(targetEntity = AccountMerchantLimit.class, mappedBy = "account", fetch =FetchType.LAZY)
    @JsonIgnoreProperties("account")
    private List<AccountMerchantLimit> accountMerchantLimits = new ArrayList<>();


    @OneToMany(targetEntity=DepositRequest.class,mappedBy = "account",fetch = FetchType.LAZY)
    @JsonIgnoreProperties("account")
    private List<DepositRequest> depositRequests = new ArrayList<>();

    @OneToMany(targetEntity = RequestRefundMoney.class, mappedBy = "account",fetch = FetchType.LAZY)
    @JsonIgnoreProperties("account")
    private List<RequestRefundMoney> requestRefunds = new ArrayList<>();

    @OneToMany(targetEntity = ManualTransactionRequest.class, mappedBy = "account",fetch = FetchType.LAZY)
    @JsonIgnoreProperties("account")
    private List<ManualTransactionRequest> manualTransactionRequests = new ArrayList<>();

    public String getName() {
        return BaseUtils.isStringSafeEmpty(name) ? this.getAccountType().getName():name;
    }

    @JsonView(AccountJsonView.AccountDetails.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonView(AccountJsonView.AccountDetails.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }

    @Column(name="acc_credit_assign",nullable = false)
    @JsonView(AccountJsonView.AccountList.class)
    private Double creditAssign=0d;

    @JsonView(AccountJsonView.AccountList.class)
    public String getStatusDesc() {
        if (this.status == 1)
            return BaseUtils.getMessageResource("global.status.active");
        else
            return BaseUtils.getMessageResource("global.status.inActive");


    }

    public Double getAvailableBalance() {
        return this.getBalance()-this.getBlock();
    }

    public String getFormatAvailableBalance() {
        return Utils.formatMoney(this.getBalance()-this.getBlock());
    }


}
