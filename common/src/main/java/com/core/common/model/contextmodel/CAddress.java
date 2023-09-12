package com.core.common.model.contextmodel;

import com.core.common.util.json.JsonCleanHtmlAndXssDeserializer;
import com.core.common.util.validator.annotation.Length;
import com.core.common.util.validator.annotation.NotNullStr;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class CAddress implements Serializable {

    @NotNull(message = "{common.province.id_required}")
    private Long provinceId;
    @NotNull(message = "{common.city.id_required}")
    private Long cityId;
    private Long regionId;
    @NotNullStr(message = "{common.address.address_required}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String address;
    @NotNullStr(message = "{common.address.postalCode_required}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String postalCode;
    @NotNull(message = "{common.address.latlng_required}")
    private Double lat;
    @NotNull(message = "{common.address.latlng_required}")
    private Double lan;
    @NotNullStr(message = "{common.address.mobileNumber_required}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String mobileNumber;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String email;
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String phonNumber;
    @NotNullStr(message = "{common.address.personName_required}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String personName;
    @NotNullStr(message = "{common.address.num_required}")
    @Length(max = 10, message = "{common.address.num_len}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String num;
    @Length(max = 10, message = "{common.address.unit_len}")
    @JsonDeserialize(using = JsonCleanHtmlAndXssDeserializer.class)
    private String unit;

    private List<Long> targetBranchIds  ;

    public Long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLan() {
        return lan;
    }

    public void setLan(Double lan) {
        this.lan = lan;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonNumber() {
        return phonNumber;
    }

    public void setPhonNumber(String phonNumber) {
        this.phonNumber = phonNumber;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<Long> getTargetBranchIds() {
        return targetBranchIds;
    }

    public void setTargetBranchIds(List<Long> targetBranchIds) {
        this.targetBranchIds = targetBranchIds;
    }
}
