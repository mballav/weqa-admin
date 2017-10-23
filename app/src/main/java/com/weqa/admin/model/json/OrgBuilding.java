package com.weqa.admin.model.json;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Manish Ballav on 10/1/2017.
 */

public class OrgBuilding {

    @SerializedName("buildingId")
    @Expose
    private Integer buildingId;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("name")
    @Expose
    private Object name;
    @SerializedName("latitude")
    @Expose
    private Object latitude;
    @SerializedName("longitude")
    @Expose
    private Object longitude;
    @SerializedName("responseOrgFloorDetail")
    @Expose
    private List<ResponseOrgFloorDetail> responseOrgFloorDetail = null;

    public Integer getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Integer buildingId) {
        this.buildingId = buildingId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public Object getLatitude() {
        return latitude;
    }

    public void setLatitude(Object latitude) {
        this.latitude = latitude;
    }

    public Object getLongitude() {
        return longitude;
    }

    public void setLongitude(Object longitude) {
        this.longitude = longitude;
    }

    public List<ResponseOrgFloorDetail> getResponseOrgFloorDetail() {
        return responseOrgFloorDetail;
    }

    public void setResponseOrgFloorDetail(List<ResponseOrgFloorDetail> responseOrgFloorDetail) {
        this.responseOrgFloorDetail = responseOrgFloorDetail;
    }

}