package com.weqa.admin.model.json;

/**
 * Created by Manish Ballav on 10/1/2017.
 */
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FloorplanDetailResponse {

    @SerializedName("floorPlanId")
    @Expose
    private Integer floorPlanId;
    @SerializedName("floorPlanImage")
    @Expose
    private String floorPlanImage;
    @SerializedName("itemDetail")
    @Expose
    private List<ItemDetail> itemDetail = null;

    public Integer getFloorPlanId() {
        return floorPlanId;
    }

    public void setFloorPlanId(Integer floorPlanId) {
        this.floorPlanId = floorPlanId;
    }

    public String getFloorPlanImage() {
        return floorPlanImage;
    }

    public void setFloorPlanImage(String floorPlanImage) {
        this.floorPlanImage = floorPlanImage;
    }

    public List<ItemDetail> getItemDetail() {
        return itemDetail;
    }

    public void setItemDetail(List<ItemDetail> itemDetail) {
        this.itemDetail = itemDetail;
    }

}

