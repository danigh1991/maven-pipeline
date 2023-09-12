package com.core.datamodel.model.dbmodel;

import com.core.datamodel.model.view.MyJsonView;
import com.core.model.annotations.MultiLingual;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.core.model.dbmodel.AbstractBaseMultiLingualEntity;
import com.core.model.enums.ERepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name= ExternalApi.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = ExternalApi.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "eap_id")),
        @AttributeOverride(name = "version", column = @Column(name = "eap_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "eap_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "eap_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "eap_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "eap_modify_by"))
})
public class ExternalApi extends AbstractBaseMultiLingualEntity {
private static final long serialVersionUID = 1L;
public static final String TABLE_NAME = "sc_external_api";

    @JsonView(MyJsonView.ExternalApiList.class)
    @Column(name="eap_name",nullable = false)
    private String name;

    @JsonView(MyJsonView.ExternalApiDetails.class)
    @Column(name = "eap_description")
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.EXTERNAL_API)
    private String description;

    @JsonView(MyJsonView.ExternalApiList.class)
    @Column(name="eap_active")
    private Boolean active;

    @JsonView(MyJsonView.ExternalApiDetails.class)
    @Column(name="eap_cacheable",nullable = false)
    private Boolean cacheable=false;

    @JsonView(MyJsonView.ExternalApiDetails.class)
    @Column(name="eap_validity_minutes")
    private Integer validityMinutes=0;

    @JsonView(MyJsonView.ExternalApiDetails.class)
    @Column(name="eap_address",nullable = false)
    private String address;

    @JsonView(MyJsonView.ExternalApiDetails.class)
    @Column(name="eap_http_method",nullable = false)
    private String httpMethod;

    @Column(name="eap_key")
    private String key;

    @Column(name="eap_secret")
    private String secret;

    @Column(name="eap_client_id")
    private String clientId;

    @Column(name="eap_api_key")
    private String apiKey;


    @OneToMany(targetEntity = ExternalApiCall.class, mappedBy = "externalApi", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("externalApi")
    private List<ExternalApiCall> externalApiCalls;

}
