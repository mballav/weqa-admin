package com.weqa.admin.model.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Manish Ballav on 10/1/2017.
 */

public class ResponseOrgFloorDetail {

    @SerializedName("floorId")
    @Expose
    private Integer floorId;
    @SerializedName("floorPlanId")
    @Expose
    private Integer floorPlanId;
    @SerializedName("floorName")
    @Expose
    private String floorName;
    @SerializedName("floorLevel")
    @Expose
    private Integer floorLevel;
    @SerializedName("floorPlanDescription")
    @Expose
    private Object floorPlanDescription;
    @SerializedName("floorPlanImage")
    @Expose
    private Object floorPlanImage;

    public Integer getFloorId() {
        return floorId;
    }

    public void setFloorId(Integer floorId) {
        this.floorId = floorId;
    }

    public Integer getFloorPlanId() {
        return floorPlanId;
    }

    public void setFloorPlanId(Integer floorPlanId) {
        this.floorPlanId = floorPlanId;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public Integer getFloorLevel() {
        return floorLevel;
    }

    public void setFloorLevel(Integer floorLevel) {
        this.floorLevel = floorLevel;
    }

    public Object getFloorPlanDescription() {
        return floorPlanDescription;
    }

    public void setFloorPlanDescription(Object floorPlanDescription) {
        this.floorPlanDescription = floorPlanDescription;
    }

    public Object getFloorPlanImage() {
        return floorPlanImage;
    }

    public void setFloorPlanImage(Object floorPlanImage) {
        this.floorPlanImage = floorPlanImage;
    }

}
