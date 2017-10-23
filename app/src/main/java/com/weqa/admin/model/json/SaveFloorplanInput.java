package com.weqa.admin.model.json;

/**
 * Created by Manish Ballav on 10/2/2017.
 */
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SaveFloorplanInput {

    @SerializedName("floorPlanId")
    @Expose
    private Integer floorPlanId;
    @SerializedName("itemDetail")
    @Expose
    private List<ItemDetailSave> itemDetail = null;

    public Integer getFloorPlanId() {
        return floorPlanId;
    }

    public void setFloorPlanId(Integer floorPlanId) {
        this.floorPlanId = floorPlanId;
    }

    public List<ItemDetailSave> getItemDetail() {
        return itemDetail;
    }

    public void setItemDetail(List<ItemDetailSave> itemDetail) {
        this.itemDetail = itemDetail;
    }

}
