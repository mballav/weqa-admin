package com.weqa.admin.model.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Manish Ballav on 10/1/2017.
 */

public class FloorplanDetailInput {

    @SerializedName("FloorPlanId")
    @Expose
    private Integer floorPlanId;

    /**
     * No args constructor for use in serialization
     *
     */
    public FloorplanDetailInput() {
    }

    /**
     *
     * @param floorPlanId
     */
    public FloorplanDetailInput(Integer floorPlanId) {
        super();
        this.floorPlanId = floorPlanId;
    }

    public Integer getFloorPlanId() {
        return floorPlanId;
    }

    public void setFloorPlanId(Integer floorPlanId) {
        this.floorPlanId = floorPlanId;
    }

}
