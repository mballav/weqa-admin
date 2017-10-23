package com.weqa.admin.model;

import android.graphics.Bitmap;

/**
 * Created by pc on 7/31/2017.
 */

public class Hotspot {

    private float x;
    private float y;
    private Bitmap croppedBitmap;
    private boolean enabled;
    private String qrCode;
    private String itemType;

    public Hotspot() {
    }

    public Hotspot(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!Hotspot.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        Hotspot c = (Hotspot) obj;
        if (c.x != this.x || c.y != this.y) {
            return false;
        }
        return true;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Bitmap getCroppedBitmap() {
        return croppedBitmap;
    }

    public void setCroppedBitmap(Bitmap croppedBitmap) {
        this.croppedBitmap = croppedBitmap;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
}
