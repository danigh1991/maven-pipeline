package com.core.card.model.dbmodel;

import com.core.card.model.view.CardJsonView;
import com.core.card.model.wrapper.BankCardWrapper;
import com.core.datamodel.model.dbmodel.Province;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "bankCardWrapperMapping",
                classes = {
                        @ConstructorResult(
                                targetClass = BankCardWrapper.class,
                                columns = {
                                        @ColumnResult(name = "id", type =Long.class),
                                        @ColumnResult(name = "name", type =String.class),
                                        @ColumnResult(name = "cardNumber", type =String.class),
                                        @ColumnResult(name = "userId", type =Long.class),
                                        @ColumnResult(name = "active", type =Boolean.class),
                                        @ColumnResult(name = "cardExpireDate", type =String.class),
                                        @ColumnResult(name = "description", type =String.class),
                                        @ColumnResult(name = "createBy", type = Long.class),
                                        @ColumnResult(name = "createDate", type = Date.class),
                                        @ColumnResult(name = "modifyBy", type =Long.class),
                                        @ColumnResult(name = "modifyDate", type =Date.class)
                                }
                        )
                }
        )
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "BankCard.findBankCardWrapperById",
                query = "select c.bcr_id as id, c.bcr_name as name, c.bcr_card_number  as cardNumber,c.bcr_usr_id as userId,\n" +
                        "       c.bcr_active as active, c.bcr_expire as cardExpireDate, c.bcr_description as description, c.bcr_create_by as createBy, \n" +
                        "       c.bcr_create_date as createDate, c.bcr_modify_by as modifyBy, c.bcr_modify_date as modifyDate       \n" +
                        "from sc_bank_card c where c.bcr_id=:bankCardId order by c.bcr_create_date desc",
                resultSetMapping = "bankCardWrapperMapping"),
        @NamedNativeQuery(name = "BankCard.findBankCardWrapperByIdAndUsrId",
                query = "select c.bcr_id as id, c.bcr_name as name, c.bcr_card_number as cardNumber,c.bcr_usr_id as userId,\n" +
                        "       c.bcr_active as active, c.bcr_expire as cardExpireDate, c.bcr_description as description, c.bcr_create_by as createBy, \n" +
                        "       c.bcr_create_date as createDate, c.bcr_modify_by as modifyBy, c.bcr_modify_date as modifyDate       \n" +
                        "from sc_bank_card c  where c.bcr_id=:bankCardId and c.bcr_usr_id=:userId  order by c.bcr_create_date desc",
                resultSetMapping = "bankCardWrapperMapping"),
        @NamedNativeQuery(name = "BankCard.findAllBankCardWrappersByUsrId",
                query = "select c.bcr_id as id, c.bcr_name as name, c.bcr_card_number as cardNumber,c.bcr_usr_id as userId,\n" +
                        "       c.bcr_active as active, c.bcr_expire as cardExpireDate, c.bcr_description as description, c.bcr_create_by as createBy, \n" +
                        "       c.bcr_create_date as createDate, c.bcr_modify_by as modifyBy, c.bcr_modify_date as modifyDate       \n" +
                        "from sc_bank_card c  where c.bcr_usr_id=:userId order by CASE WHEN (c.bcr_expire is null) THEN 0 ELSE 1 END  , c.bcr_id , c.bcr_create_date desc",
                resultSetMapping = "bankCardWrapperMapping"),
        @NamedNativeQuery(name = "BankCard.findMyBankCardWrappersByUsrId",
                query = "select c.bcr_id as id, c.bcr_name as name, c.bcr_card_number as cardNumber,c.bcr_usr_id as userId,\n" +
                        "       c.bcr_active as active, c.bcr_expire as cardExpireDate, c.bcr_description as description, c.bcr_create_by as createBy, \n" +
                        "       c.bcr_create_date as createDate, c.bcr_modify_by as modifyBy, c.bcr_modify_date as modifyDate       \n" +
                        "from sc_bank_card c  where c.bcr_usr_id=:userId and c.bcr_expire is not null order by c.bcr_create_date desc",
                resultSetMapping = "bankCardWrapperMapping"),
        @NamedNativeQuery(name = "BankCard.findOtherBankCardWrappersByUsrId",
                query = "select c.bcr_id as id, c.bcr_name as name, c.bcr_card_number as cardNumber,c.bcr_usr_id as userId,\n" +
                        "       c.bcr_active as active, c.bcr_expire as cardExpireDate, c.bcr_description as description, c.bcr_create_by as createBy, \n" +
                        "       c.bcr_create_date as createDate, c.bcr_modify_by as modifyBy, c.bcr_modify_date as modifyDate       \n" +
                        "from sc_bank_card c  where c.bcr_usr_id=:userId and c.bcr_expire is null order by c.bcr_create_date desc ",
                resultSetMapping = "bankCardWrapperMapping")


})
@Table(name= BankCard.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = BankCard.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "bcr_id")),
        @AttributeOverride(name = "version", column = @Column(name = "bcr_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "bcr_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "bcr_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "bcr_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "bcr_modify_by"))
})
public class BankCard extends AbstractBaseEntity<Long> {
private static final long serialVersionUID = 1L;
public static final String TABLE_NAME = "sc_bank_card";

    @JsonView(CardJsonView.BankCardList.class)
    @Column(name="bcr_name",nullable = false)
    private String name;

    @JsonView(CardJsonView.BankCardList.class)
    @Column(name = "bcr_card_number",nullable = false)
    private String cardNumber;

    @JsonView(CardJsonView.BankCardList.class)
    @Column(name="bcr_active")
    private Boolean active=true;

    @JsonView(CardJsonView.BankCardList.class)
    @Column(name="bcr_expire" ,nullable = false)
    private String expire;

    @Column(name="bcr_usr_id" ,nullable = false)
    private Long userId;

    @Column(name="bcr_description")
    private String description;

    @Column(name="bcr_shp_transaction_id")
    private String shpTransactionId;

    @Column(name="bcr_shp_card_Id")
    private Long shpCardId;

    @Column(name="bcr_shp_masked_pan")
    private String shpMaskedPan;

    @Column(name="bcr_shp_reference_expiry_date")
    private Date shpReferenceExpiryDate;

    @Column(name="bcr_shp_pan")
    private String shpPan;

    @Column(name="bcr_shp_pan_expiry_date")
    private Date shpPanExpiryDate;

    @Column(name="bcr_shp_assurance_level")
    private Integer shpAssuranceLevel;

    @OneToMany(targetEntity = BankCardOperation.class, mappedBy = "bankCard",fetch = FetchType.LAZY)
    @JsonIgnoreProperties("bankCard")
    private List<BankCardOperation> bankCardOperations;

}
