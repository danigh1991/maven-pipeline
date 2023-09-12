package com.core.datamodel.model.wrapper;

import com.core.model.enums.ERepository;
import com.core.model.annotations.MultiLingual;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.util.HtmlUtils;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class AddressWrapper extends AbstractMultiLingualWrapper {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long userId;
    private Long countryId;
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.COUNTRY,fieldName = "name",targetIdFieldName = "countryId")
    private String countryName;
    private Long provinceId;
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.PROVINCE,fieldName = "name",targetIdFieldName = "provinceId")
    private String provinceName;
    private Long cityId;
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.CITY,fieldName = "name",targetIdFieldName = "cityId")
    private String cityName;
    private Long regionId;
    @MultiLingual(destinationTable = ERepository.MultiLingualBase,targetType = ERepository.REGION,fieldName = "name",targetIdFieldName = "regionId")
    private String regionName;
    private String address;
    private String postalCode;
    private Double lat;
    private Double lan;
    private String mobileNumber;
    private String email;
    private String phoneNumber;
    private String personName;
    @JsonIgnore
    private String targetBranchIds;
    private String num;
    private String unit;

    private List<Long> branchIds=null;



    public AddressWrapper(Long id, Long userId,Long countryId, String countryName, Long provinceId, String provinceName, Long cityId, String cityName, Long regionId, String regionName, String address, String postalCode, Double lat, Double lan, String mobileNumber, String email, String phoneNumber,String personName,String targetBranchIds,String num,String  unit) {
        this.id = id;
        this.userId = userId;
        this.countryId=countryId;
        this.countryName=countryName;
        this.provinceId = provinceId;
        this.provinceName = provinceName;
        this.cityId = cityId;
        this.cityName = cityName;
        this.regionId = regionId;
        this.regionName = regionName;
        this.address = address;
        this.postalCode = postalCode;
        this.lat = lat;
        this.lan = lan;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.personName=personName;
        this.targetBranchIds= targetBranchIds;
        this.num=num;
        this.unit=unit;

        this.prepareMultiLingual();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String toJson(){
        return HtmlUtils.htmlEscape(BaseUtils.getAsJson(this));
    }

    public String toString(){
       return BaseUtils.getMessageResource("userAddress.toString" ,
               this.getProvinceName() , this.getCityName() ,
                        (this.getRegionName()!=null?this.getRegionName()+", ":"") + this.getAddress() +
                                   (!BaseUtils.isStringSafeEmpty(this.getNum())? ", " + BaseUtils.getMessageResource("userAddress.num",this.getNum()):"" ) +
                                   (!BaseUtils.isStringSafeEmpty(this.getUnit())? ", " + BaseUtils.getMessageResource("userAddress.unit",this.getUnit()):"" )
                        ,this.getPostalCode(), this.getCountryName());
    }

    public ShortProvince getShortProvince(){
        return new ShortProvince(this.getProvinceId(), this.getProvinceName(), "", true, this.getCountryId(), this.getCountryName());
    }

    public ShortCity getShortCity(){
        return new ShortCity(this.getCityId(), this.getCityName(), "");
    }

    public class ShortProvince{
        private Long id;
        private String name;
        private String englishName;
        private Long countryId;
        private String countryName;
        private Boolean active;

        public ShortProvince(Long id, String name, String englishName, Boolean active, Long countryId, String countryName) {
            this.id = id;
            this.name = name;
            this.englishName = englishName;
            this.active = active;
            this.countryId = countryId;
            this.countryName = countryName;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEnglishName() {
            return englishName;
        }

        public Boolean getActive() {
            return active;
        }

        public Long getCountryId() {
            return countryId;
        }

        public String getCountryName() {
            return countryName;
        }
    }
    public class ShortCity {
        private Long id;
        private String name;
        private String englishName;

        public ShortCity(Long id, String name, String englishName) {
            this.id = id;
            this.name = name;
            this.englishName = englishName;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEnglishName() {
            return englishName;
        }
    }

    public String getTargetBranchIds() {
        return targetBranchIds;
    }

    public void setTargetBranchIds(String targetBranchIds) {
        this.targetBranchIds = targetBranchIds;
    }

    public List<Long> getBranchIds() {
        if(branchIds==null && !BaseUtils.isStringSafeEmpty(this.getTargetBranchIds())){
            String[] tmpIds= this.getTargetBranchIds().split(",");
            branchIds=Arrays.stream(tmpIds).mapToLong(b-> Long.valueOf(b)).boxed().collect(Collectors.toList());
        }
        return branchIds;
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
}
