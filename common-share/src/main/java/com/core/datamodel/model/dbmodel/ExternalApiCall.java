package com.core.datamodel.model.dbmodel;

import com.core.datamodel.model.wrapper.ExternalApiCallWrapper;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name="externalApiCallWrapperMapping",
                classes={
                        @ConstructorResult  (
                                targetClass= ExternalApiCallWrapper.class,
                                columns={
                                        @ColumnResult(name="id",type = Long.class),
                                        @ColumnResult(name="apiId",type = Long.class),
                                        @ColumnResult(name="apiName",type = String.class),
                                        @ColumnResult(name="apiDescription",type = String.class),
                                        @ColumnResult(name="status",type = Integer.class),
                                        @ColumnResult(name="address",type = String.class),
                                        @ColumnResult(name="httpMethod",type = String.class),
                                        @ColumnResult(name="requestBody",type = String.class),
                                        @ColumnResult(name="requestKey",type = String.class),
                                        @ColumnResult(name="responseBody",type = String.class),
                                        @ColumnResult(name="responseDate",type = Date.class),
                                        @ColumnResult(name="referenceId",type = String.class),
                                        @ColumnResult(name="trackingId",type = String.class),
                                        @ColumnResult(name="processId",type = String.class),
                                        @ColumnResult(name="transactionId",type = String.class),
                                        @ColumnResult(name="createDate",type = Date.class),
                                        @ColumnResult(name="createBy",type = Long.class),
                                        @ColumnResult(name="modifyDate",type = Date.class),
                                        @ColumnResult(name="modifyBy",type = Long.class)
                                }
                        )
                }
        )})
@Table(name= ExternalApiCall.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = ExternalApiCall.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "eac_id")),
        @AttributeOverride(name = "version", column = @Column(name = "eac_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "eac_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "eac_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "eac_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "eac_modify_by"))
})
public class ExternalApiCall extends AbstractBaseEntity<Long> {
private static final long serialVersionUID = 1L;
public static final String TABLE_NAME = "sc_external_api_call";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eac_eap_id",nullable = false,referencedColumnName = "eap_id", foreignKey = @ForeignKey(name = "FK_eac_eap_id"))
    @JsonIgnoreProperties("externalApiCalls")
    private ExternalApi externalApi;

    @Column(name="eac_process_id")
    private String processId;

    @Column(name="eac_tracking_id",nullable = false)
    private String trackingId;

    @Column(name="eac_status",nullable = false)
    private Integer status=0; //0=create, 1=success, 2=error

    @Column(name = "eac_address",nullable = false)
    private String address;

    @Column(name = "eac_http_method",nullable = false)
    private String httpMethod;

    @Column(name = "eac_request")
    private String request;

    @Column(name = "eac_response")
    private String response;

    @Column(name = "eac_response_date")
    private Date responseDate;

    @Column(name = "eac_reference_id")
    private Long responseReferenceId;

    @Column(name = "eac_transaction_id")
    private String responseTransactionId;

    @Column(name = "eac_request_key")
    private Integer requestKey;


}
