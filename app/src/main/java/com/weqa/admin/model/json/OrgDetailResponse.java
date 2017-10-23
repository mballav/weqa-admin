package com.weqa.admin.model.json;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Manish Ballav on 10/1/2017.
 */

public class OrgDetailResponse {

    @SerializedName("orgId")
    @Expose
    private Integer orgId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mobileNo")
    @Expose
    private String mobileNo;
    @SerializedName("emailId")
    @Expose
    private Object emailId;
    @SerializedName("contactPerson")
    @Expose
    private String contactPerson;
    @SerializedName("phone")
    @Expose
    private Object phone;
    @SerializedName("orgBuilding")
    @Expose
    private List<OrgBuilding> orgBuilding = null;
    @SerializedName("orgBasedItemType")
    @Expose
    private List<OrgBasedItemType> orgBasedItemType = null;

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public Object getEmailId() {
        return emailId;
    }

    public void setEmailId(Object emailId) {
        this.emailId = emailId;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public Object getPhone() {
        return phone;
    }

    public void setPhone(Object phone) {
        this.phone = phone;
    }

    public List<OrgBuilding> getOrgBuilding() {
        return orgBuilding;
    }

    public void setOrgBuilding(List<OrgBuilding> orgBuilding) {
        this.orgBuilding = orgBuilding;
    }

    public List<OrgBasedItemType> getOrgBasedItemType() {
        return orgBasedItemType;
    }

    public void setOrgBasedItemType(List<OrgBasedItemType> orgBasedItemType) {
        this.orgBasedItemType = orgBasedItemType;
    }

}
