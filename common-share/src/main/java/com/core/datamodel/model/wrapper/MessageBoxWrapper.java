package com.core.datamodel.model.wrapper;

import com.core.datamodel.model.enums.ENotifyTargetType;
import com.core.datamodel.model.view.MyJsonView;
import com.core.datamodel.util.ShareUtils;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.util.BaseUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Getter
@Setter
public class MessageBoxWrapper implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonView(MyJsonView.MessageBoxWrapperList.class)
    private Long id;
    private Long senderUserId;
    @JsonProperty("sender")
    @JsonView(MyJsonView.MessageBoxWrapperList.class)
    private String senderUserName;
    private Long targetUserId;
    @JsonView(MyJsonView.MessageBoxWrapperList.class)
    private Integer notifyTargetType;
    @JsonView(MyJsonView.MessageBoxWrapperList.class)
    private String title;
    @JsonView(MyJsonView.MessageBoxWrapperDetail.class)
    private String description;
    @JsonView(MyJsonView.MessageBoxWrapperList.class)
    private Date seenDate;
    @JsonView(MyJsonView.MessageBoxWrapperList.class)
    private Date createDate;
    private Long createBy;
    private Date modifyDate;
    private Long modifyBy;
    private String additional;
    @JsonView(MyJsonView.MessageBoxWrapperDetail.class)
    private Map<String,Object> additionalArrayList;


    public MessageBoxWrapper(Long id,Long senderUserId,String senderUserName,Long targetUserId, Integer notifyTargetType, String title,String description, Date seenDate, Date createDate, Long createBy, Date modifyDate, Long modifyBy, String additional) {
        this.id = id;
        this.senderUserId=senderUserId;
        this.senderUserName=senderUserName;
        this.targetUserId=targetUserId;
        this.notifyTargetType = notifyTargetType;
        this.title = title;
        this.description=description;
        this.seenDate = seenDate;
        this.createDate = createDate;
        this.createBy = createBy;
        this.modifyDate = modifyDate;
        this.modifyBy = modifyBy;
        this.additional = additional;
    }


    public Map<String, Object> getAdditionalArrayList() {
        if(this.additionalArrayList ==null){
            this.additionalArrayList =new HashMap<>();
            if(!BaseUtils.isStringSafeEmpty(this.additional)){
                String[] additionals=this.additional.split("\\[,\\]");
                for (int i = 0; i < additionals.length; i++) {
                    String[] adds= additionals[i].split("\\[=\\]");

                    if(adds.length==1 && additionals[i].endsWith("[=]")) {
                        String[] tmp=new String[2];
                        tmp[0]= adds[0];
                        tmp[1]= "";
                        adds=tmp;
                    }

                    if(adds.length==2  ){
                        String[] addLabel= adds[0].split("\\[-\\]");
                        if(addLabel.length==1){
                            additionalArrayList.put(ShareUtils.getMessageResource(adds[0]),adds[1]);
                        }else if(addLabel.length==4){
                            Map<String , Object> additionalInfoList=new HashMap<>();
                            additionalInfoList.put("label",ShareUtils.getMessageResource(addLabel[1]));
                            if(addLabel[3].equalsIgnoreCase("show"))
                               additionalInfoList.put("show",true);
                            else
                                additionalInfoList.put("show",false);

                            additionalInfoList.put("type",addLabel[2]);

                            String label="value";
                            if (ShareUtils.isStringSafeEmpty(adds[1])){
                                additionalInfoList.put(label, "");
                            }else {
                                switch (addLabel[2].toLowerCase()) {
                                    case "date":
                                        additionalInfoList.put(label, ShareUtils.getShamsiDate(BaseUtils.parsDate(adds[1])));
                                        break;
                                    case "datetime":
                                        additionalInfoList.put(label, ShareUtils.getShamsiDateTime(BaseUtils.parsDate(adds[1])));
                                        break;
                                    case "money":
                                        additionalInfoList.put(label, ShareUtils.formatMoney(BaseUtils.parsDouble(adds[1]), false));
                                        break;
                                    case "float":
                                        additionalInfoList.put(label, BaseUtils.parsFloat(adds[1]));
                                    case "double":
                                        additionalInfoList.put(label, BaseUtils.parsDouble(adds[1]));
                                        break;
                                    case "long":
                                        additionalInfoList.put(label, BaseUtils.parsLong(adds[1]));
                                        break;
                                    case "integer":
                                        additionalInfoList.put(label, BaseUtils.parsInt(adds[1]));
                                        break;
                                    case "boolean":
                                        additionalInfoList.put(label, BaseUtils.parsBoolean(adds[1]));
                                        break;
                                    default:
                                        additionalInfoList.put(label, adds[1]);
                                        //break;
                                }
                            }
                            additionalArrayList.put(addLabel[0], additionalInfoList);
                        }
                    }
                }
            }
        }
        return additionalArrayList;

    }

    @JsonView(MyJsonView.MessageBoxWrapperList.class)
    public String getNotifyTargetTypeDesc() {
        return ENotifyTargetType.valueOf(this.notifyTargetType).getCaption();
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getSeenDate() {
        return seenDate;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }

}
