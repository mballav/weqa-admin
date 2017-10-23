package com.weqa.admin.model.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Manish Ballav on 10/1/2017.
 */

public class ItemDetail {

    @SerializedName("itemLocation")
    @Expose
    private String itemLocation;
    @SerializedName("qrCode")
    @Expose
    private String qrCode;
    @SerializedName("isEnabled")
    @Expose
    private Boolean isEnabled;
    @SerializedName("itemTypeId")
    @Expose
    private Integer itemTypeId;

    public String getItemLocation() {
        return itemLocation;
    }

    public void setItemLocation(String itemLocation) {
        this.itemLocation = itemLocation;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Integer getItemTypeId() {
        return itemTypeId;
    }

    public void setItemTypeId(Integer itemTypeId) {
        this.itemTypeId = itemTypeId;
    }

}
