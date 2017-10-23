package com.weqa.admin.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.weqa.admin.R;
import com.weqa.admin.async.GetFloorplanAsyncTask;
import com.weqa.admin.async.SaveFloorplanAsyncTask;
import com.weqa.admin.model.Hotspot;
import com.weqa.admin.model.json.FloorplanDetailInput;
import com.weqa.admin.model.json.FloorplanDetailResponse;
import com.weqa.admin.model.json.ItemDetail;
import com.weqa.admin.model.json.ItemDetailSave;
import com.weqa.admin.model.json.OrgBasedItemType;
import com.weqa.admin.model.json.OrgDetailResponse;
import com.weqa.admin.model.json.SaveFloorplanInput;
import com.weqa.admin.service.RetrofitBuilder;
import com.weqa.admin.util.PopupMenuUtil;
import com.weqa.admin.util.QRCodeUtil;
import com.weqa.admin.util.SharedPreferencesUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;

public class FloorplanEditActivity extends AppCompatActivity implements OnPhotoTapListener,
                                                                        GetFloorplanAsyncTask.UpdateUI,
                                                                        SaveFloorplanAsyncTask.SaveFloorplan {

    private final static String LOG_TAG = "WEQA-LOG";

    private final static String TEST_QR_CODE = "0001,1,1,2017-09-02 00:00:40";

    private TextView orgText, buildingText, floorText;
    private PhotoView floorplan;
    private ProgressBar progressBar;

    private int originalBitmapWidth, originalBitmapHeight;
    private float hotspotSize;
    private List<Hotspot> hotspots = new ArrayList<Hotspot>();
    private List<Hotspot> newHotspots = new ArrayList<Hotspot>();
    private List<OrgDetailResponse> orgList;

    private float tappedX, tappedY;
    private int absX, absY;
    private String itemTypeSelected;
    private boolean updateFlag = false;
    private boolean popupOpen = false, popupClosed = false;
    private PopupWindow popupWindow;
    private Hotspot hotspotSelected;

    private SharedPreferencesUtil util;

    private int orgId, buildingId, floorplanId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floorplan_edit);

        Intent i = getIntent();
        orgId = i.getIntExtra("ORG_ID", 0);
        buildingId = i.getIntExtra("BUILDING_ID", 0);
        floorplanId = i.getIntExtra("FLOORPLAN_ID", 0);

        String orgName = i.getStringExtra("ORG_NAME");
        String buildingName = i.getStringExtra("BUILDING_NAME");
        String floorName = i.getStringExtra("FLOORPLAN_NAME");

        orgText = (TextView) findViewById(R.id.orgText);
        buildingText = (TextView) findViewById(R.id.buildingText);
        floorText = (TextView) findViewById(R.id.floorText);

        orgText.setText(orgName);
        buildingText.setText(buildingName);
        floorText.setText(floorName);

        floorplan = (PhotoView) findViewById(R.id.floorplan);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        floorplan.setOnPhotoTapListener(this);
        floorplan.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                absX = (int) x;
                absY = (int) y;
                if (popupOpen) {
                    popupWindow.dismiss();
                    popupOpen = false;
                    popupClosed = true;
                }
            }
        });

        Button saveButton = (Button) findViewById(R.id.saveButton);
        Button backButton = (Button) findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveHotspots();
            }
        });

        util = new SharedPreferencesUtil(this, LOG_TAG);
        orgList = util.getOrgDetailList();

        fetchFloorplan(floorplanId);
    }

    public void popupWindowOpened(PopupWindow pW) {
        popupOpen = true;
        popupWindow = pW;
    }

    public void popupWindowClosed() {
        popupOpen = false;
    }

    private void fetchFloorplan(int floorplanId) {

        progressBar.setVisibility(View.VISIBLE);
        floorplan.setVisibility(View.GONE);

        Retrofit retrofit = RetrofitBuilder.getRetrofit();

        Log.d(LOG_TAG, "Calling the API to create new team...");
        GetFloorplanAsyncTask runner = new GetFloorplanAsyncTask(retrofit, LOG_TAG, this);

        FloorplanDetailInput input = new FloorplanDetailInput();
        input.setFloorPlanId(floorplanId);
        runner.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, input);
        Log.d(LOG_TAG, "Waiting for response...");
    }

    @Override
    public void updateUI(FloorplanDetailResponse response) {

        drawFloorplan(response);

        progressBar.setVisibility(View.GONE);
        floorplan.setVisibility(View.VISIBLE);
    }

    public void saveHotspots() {
        progressBar.setVisibility(View.VISIBLE);
        floorplan.setVisibility(View.GONE);

        Retrofit retrofit = RetrofitBuilder.getRetrofit();

        Log.d(LOG_TAG, "Calling the API to create new team...");
        SaveFloorplanAsyncTask runner = new SaveFloorplanAsyncTask(retrofit, LOG_TAG, this);

        SaveFloorplanInput input = new SaveFloorplanInput();
        input.setFloorPlanId(floorplanId);

        List<ItemDetailSave> itemList = new ArrayList<ItemDetailSave>();
        for (Hotspot h : hotspots) {
            ItemDetailSave i = new ItemDetailSave();
            String location = ((int) (h.getX()*originalBitmapWidth)) + "_" + ((int) (h.getY()*originalBitmapHeight));
            i.setItemLocation(location);
            i.setIsEnabled(h.isEnabled());
            i.setItemTypeId(getItemTypeId(h.getItemType()));
            i.setQrCode(h.getQrCode());
            if (newHotspots.indexOf(h) != -1) {
                i.setIsNew(true);
            }
            else {
                i.setIsNew(false);
            }
            itemList.add(i);
        }

        input.setItemDetail(itemList);

        runner.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, input);
        Log.d(LOG_TAG, "Waiting for response...");
    }

    @Override
    public void floorplanSaved() {
        Toast.makeText(this, "Floorplan information saved", Toast.LENGTH_SHORT).show();
        fetchFloorplan(floorplanId);
    }

    private List<String> getItemTypes() {
        List<String> itemTypes = new ArrayList<String>();
        for (OrgDetailResponse r : orgList) {
            if (r.getOrgId() == orgId) {
                List<OrgBasedItemType> itemTypeList = r.getOrgBasedItemType();
                for (OrgBasedItemType i : itemTypeList) {
                    itemTypes.add(i.getItemType());
                }
            }
        }
        return itemTypes;
    }

    @Override
    public void onPhotoTap(ImageView view, float x, float y) {
        tappedX = x;
        tappedY = y;

        if (popupClosed) {
            popupClosed = false;
            return;
        }
        PopupMenuUtil popupMenuUtil = new PopupMenuUtil();
        if (!insideHotspot(x, y)) {
            if (anotherHotspotTooNear(x, y)) {
                Toast.makeText(this, "Another hotspot too close", Toast.LENGTH_SHORT).show();
            }
            else {
                updateFlag = false;
                popupMenuUtil.showCreateHotspotPopup(this, getItemTypes(), floorplan, absX, absY);
            }
        }
        else {
            Hotspot hotspot = getHotspot(x, y);
            if (isInsideNewHotspot(x, y)) {
                popupMenuUtil.showNewHotspotPopupMenu(this, view, hotspot, floorplan, absX, absY);
            }
            else {
                popupMenuUtil.showOldHotspotPopupMenu(this, view, hotspot, floorplan, absX, absY);
            }
        }
    }

    public void itemTypeSelected(String itemType) {
        itemTypeSelected = itemType;
    }

    public void updateQRCode(Hotspot hotspot) {
        updateFlag = true;
        hotspotSelected = hotspot;
    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
/*                QRCodeUtil qrCodeUtil = new QRCodeUtil(util, this);
                if (qrCodeUtil.isQRCodeValid(TEST_QR_CODE, orgList)) {
                    if (updateFlag) {
                        if (isQRCodeUnique(TEST_QR_CODE, hotspotSelected)) {
                            hotspotSelected.setQrCode(TEST_QR_CODE);
                            printHotspot(hotspotSelected);
                            Toast.makeText(this, "WEQA code updated", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(this, "WEQA Code already in use", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        if (isQRCodeUnique(TEST_QR_CODE)) {
                            createHotspot(tappedX, tappedY, itemTypeSelected, TEST_QR_CODE);
                            Toast.makeText(this, itemTypeSelected + " created", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(this, "WEQA Code already in use", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(this, "Invalid WEQA Code", Toast.LENGTH_SHORT).show();
                }*/
            } else {
                String qrCode = result.getContents();
                QRCodeUtil qrCodeUtil = new QRCodeUtil(util, this);
                if (qrCodeUtil.isQRItemCodeValid(qrCode, orgList)) {
                    if (updateFlag) {
                        if (isQRCodeUnique(qrCode, hotspotSelected)) {
                            hotspotSelected.setQrCode(qrCode);
                            Toast.makeText(this, "WEQA code updated", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(this, "WEQA Code already in use", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        if (isQRCodeUnique(qrCode)) {
                            createHotspot(tappedX, tappedY, itemTypeSelected, qrCode);
                            Toast.makeText(this, itemTypeSelected + " created", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(this, "WEQA Code already in use", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "Invalid WEQA Code", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean isQRCodeUnique(String qrCode) {
        boolean unique = true;
        for (Hotspot h : hotspots) {
            if (h.getQrCode().equals(qrCode)) {
                unique = false;
                break;
            }
        }
        return unique;
    }

    private boolean isQRCodeUnique(String qrCode, Hotspot hotspot) {
        boolean unique = true;
        for (Hotspot h : hotspots) {
            if ((!h.equals(hotspot)) && h.getQrCode().equals(qrCode)) {
                unique = false;
                break;
            }
        }
        return unique;
    }

    private void drawFloorplan(FloorplanDetailResponse response) {

        byte[] decodedString = null;
        if (response.getFloorPlanImage() != null && response.getFloorPlanImage().length() > 0) {
            String base64Image = response.getFloorPlanImage();
            decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        } else {
            try {
                File floorplanFile = new File(Environment.getExternalStorageDirectory(),
                        "Pictures/floorplan_" + orgId + "_" + buildingId + "_" + floorplanId);
                FileInputStream fis = new FileInputStream(floorplanFile);

                decodedString = new byte[(int) (floorplanFile.length())];

                fis.read(decodedString, 0, (int) (floorplanFile.length()));
                fis.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        if (decodedString != null) {
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            Log.d(LOG_TAG, "Inside updateFloorplan(): decodedString.size() = " + decodedString.length);

            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            Log.d(LOG_TAG, "Image Width: " + decodedByte.getWidth());
            Log.d(LOG_TAG, "Image Height: " + decodedByte.getHeight());

            originalBitmapWidth = decodedByte.getWidth();
            originalBitmapHeight = decodedByte.getHeight();

            hotspotSize = Math.min(originalBitmapWidth, originalBitmapHeight) / 15.0f;

            floorplan.setImageBitmap(decodedByte);

            updateFloorplanWithHotspots(response.getItemDetail(), decodedByte);
        }
    }

    private void updateFloorplanWithHotspots(List<ItemDetail> hotspotList, Bitmap decodedByte) {
        Matrix matrix = new Matrix();
        floorplan.getSuppMatrix(matrix);

        Bitmap copyBitmap = decodedByte.copy(Bitmap.Config.ARGB_8888, true);

        initHotspotData(hotspotList);
        printHotspotData();

        drawHotspots(copyBitmap);
        floorplan.setImageBitmap(copyBitmap);

        floorplan.setDisplayMatrix(matrix);
    }

    public void drawHotspots(Bitmap bMap) {

        Canvas canvas = new Canvas();
        canvas.setBitmap(bMap);

        int bWidth = bMap.getWidth();
        int bHeight = bMap.getHeight();

        int halfSizeX = (int) (hotspotSize * bWidth / (2 * originalBitmapWidth));
        int halfSizeY = (int) (hotspotSize * bHeight / (2 * originalBitmapHeight));

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        for (Hotspot hotspot : hotspots) {

            printHotspot(hotspot);

            int startX = (int) ((hotspot.getX()) * bWidth);
            int startY = (int) ((hotspot.getY()) * bHeight);

            if (hotspot.isEnabled()) {
                paint.setColor(ContextCompat.getColor(this, R.color.colorDarkGreen));
            } else {
                paint.setColor(ContextCompat.getColor(this, R.color.colorDarkRed));
            }
            paint.setAlpha(140);

            hotspot.setCroppedBitmap(Bitmap.createBitmap(bMap,
                    startX-halfSizeX, startY-halfSizeY, 2*halfSizeX, 2*halfSizeY));

            canvas.drawRect(startX - halfSizeX, startY - halfSizeY, startX + halfSizeX, startY + halfSizeY, paint);
        }
    }

    public void initHotspotData(List<ItemDetail> hotspotList) {
        if (hotspots.size() > 0) {
            hotspots.removeAll(hotspots);
        }
        if (hotspotList.size() == 0) {
            return;
        }
        for (ItemDetail hotspot : hotspotList) {
            String[] xy = hotspot.getItemLocation().split("_");
            float x = (float) (Integer.parseInt(xy[0]) * 1.0 / originalBitmapWidth);
            float y = (float) (Integer.parseInt(xy[1]) * 1.0 / originalBitmapHeight);
            Hotspot h = new Hotspot(x, y);
            h.setEnabled(hotspot.getIsEnabled());
            h.setItemType(getItemType(hotspot.getItemTypeId()));
            h.setQrCode(hotspot.getQrCode());
            hotspots.add(h);
        }
    }

    private String getItemType(int itemTypeId) {
        for (OrgDetailResponse r : orgList) {
            if (r.getOrgId() == orgId) {
                List<OrgBasedItemType> itemTypeList = r.getOrgBasedItemType();
                for (OrgBasedItemType itemType : itemTypeList) {
                    if (itemType.getItemTypeId() == itemTypeId) {
                        return itemType.getItemType();
                    }
                }
            }
        }
        return "";
    }

    private int getItemTypeId(String iType) {
        for (OrgDetailResponse r : orgList) {
            if (r.getOrgId() == orgId) {
                List<OrgBasedItemType> itemTypeList = r.getOrgBasedItemType();
                for (OrgBasedItemType itemType : itemTypeList) {
                    if (itemType.getItemType().equals(iType)) {
                        return itemType.getItemTypeId();
                    }
                }
            }
        }
        return 0;
    }
    private void printHotspot(Hotspot h) {
        Log.d(LOG_TAG, "Hotspot -> " + "Center: (" + h.getX() + ", " + h.getY() + ")");
        Log.d(LOG_TAG, "Hotspot -> " + "Item type: " + h.getItemType() + ", Enabled: " + h.isEnabled());
        Log.d(LOG_TAG, "Hotspot -> " + "QR Code: " + h.getQrCode());
    }

    private void printHotspotData() {
        Log.d(LOG_TAG, "HotspotSize = " + hotspotSize);
        for (Hotspot h : hotspots) {
            Log.d(LOG_TAG, "Center: (" + h.getX() + ", " + h.getY() + ")");
        }
    }

    private boolean anotherHotspotTooNear(float x, float y) {
        for (Hotspot h : hotspots) {
            if ((Math.abs(x - h.getX())*originalBitmapWidth < hotspotSize) && (Math.abs(y - h.getY())*originalBitmapHeight < hotspotSize))
                return true;
        }
        return false;
    }

    private boolean isInsideNewHotspot(float x, float y) {
        float halfSize = hotspotSize/2.0f;
        for (Hotspot h : newHotspots) {
            if ((Math.abs(x - h.getX())*originalBitmapWidth <= halfSize) && (Math.abs(y - h.getY())*originalBitmapHeight <= halfSize))
                return true;
        }
        return false;
    }

    private boolean insideHotspot(float x, float y) {
        float halfSize = hotspotSize/2.0f;
        for (Hotspot h : hotspots) {
            if ((Math.abs(x - h.getX())*originalBitmapWidth <= halfSize) && (Math.abs(y - h.getY())*originalBitmapHeight <= halfSize))
                return true;
        }
        return false;
    }

    private Hotspot getHotspot(float x, float y) {
        float halfSize = hotspotSize/2;
        for (Hotspot h : hotspots) {
            if ((Math.abs(x - h.getX())*originalBitmapWidth <= halfSize) && (Math.abs(y - h.getY())*originalBitmapHeight <= halfSize)) {
                return h;
            }
        }
        return null;
    }

    private void createHotspot(float x, float y, String itemType, String qrCode) {

        Hotspot hotspot = new Hotspot();
        hotspot.setX(x);
        hotspot.setY(y);
        hotspot.setEnabled(true);
        hotspot.setItemType(itemType);
        hotspot.setQrCode(qrCode);
        hotspots.add(hotspot);
        newHotspots.add(hotspot);

        printHotspot(hotspot);

        drawHotspot(x, y, true, hotspot);

    }

    public void overwriteHotspot(Hotspot hotspot) {

        Matrix matrix = new Matrix();
        floorplan.getSuppMatrix(matrix);

        Bitmap floorplanBitmap = ((BitmapDrawable) floorplan.getDrawable()).getBitmap();

        float bWidth = floorplanBitmap.getWidth();
        float bHeight = floorplanBitmap.getHeight();

        int halfSizeX = (int) (hotspotSize*bWidth/(2*originalBitmapWidth));
        int halfSizeY = (int) (hotspotSize*bHeight/(2*originalBitmapHeight));

        int startX = (int) (hotspot.getX()*bWidth);
        int startY = (int) (hotspot.getY()*bHeight);

        Bitmap copyBitmap = floorplanBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas();
        canvas.setBitmap(copyBitmap);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (hotspot.isEnabled()) {
            paint.setColor(ContextCompat.getColor(this, R.color.colorDarkGreen));
        } else {
            paint.setColor(ContextCompat.getColor(this, R.color.colorDarkRed));
        }
        paint.setAlpha(140);

        canvas.drawBitmap(hotspot.getCroppedBitmap(), (startX - halfSizeX), (startY - halfSizeY), null);

        canvas.drawRect(startX-halfSizeX, startY-halfSizeY, startX+halfSizeX, startY+halfSizeY, paint);

        floorplan.setImageBitmap(copyBitmap);

        floorplan.setDisplayMatrix(matrix);
    }

    public void drawHotspot(float x, float y, boolean enabled, Hotspot h) {
        Matrix matrix = new Matrix();
        floorplan.getSuppMatrix(matrix);

        Bitmap floorplanBitmap = ((BitmapDrawable) floorplan.getDrawable()).getBitmap();

        float bWidth = floorplanBitmap.getWidth();
        float bHeight = floorplanBitmap.getHeight();

        int halfSizeX = (int) (hotspotSize*bWidth/(2*originalBitmapWidth));
        int halfSizeY = (int) (hotspotSize*bHeight/(2*originalBitmapHeight));

        int startX = (int) (x*bWidth);
        int startY = (int) (y*bHeight);

        //Log.d(LOG_TAG, "x: " + x + ", y: " + y);
        //Log.d(LOG_TAG, "Bitmap-width: " + bWidth + ", Bitmap-height: " + bHeight);
        //Log.d(LOG_TAG, "Original-Bitmap-width: " + bitmapWidth + ", Original-Bitmap-height: " + bitmapHeight);
        //Log.d(LOG_TAG, "startX: " + startX + ", startY: " + startY);
        //Log.d(LOG_TAG, "halfSizeX: " + halfSizeX + ", halfSizeY: " + halfSizeY);

        h.setCroppedBitmap(Bitmap.createBitmap(floorplanBitmap,
                startX-halfSizeX, startY-halfSizeY, 2*halfSizeX, 2*halfSizeY));

        Bitmap copyBitmap = floorplanBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas();
        canvas.setBitmap(copyBitmap);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (enabled) {
            paint.setColor(ContextCompat.getColor(this, R.color.colorDarkGreen));
        } else {
            paint.setColor(ContextCompat.getColor(this, R.color.colorDarkRed));
        }
        paint.setAlpha(140);

        canvas.drawRect(startX-halfSizeX, startY-halfSizeY, startX+halfSizeX, startY+halfSizeY, paint);

        floorplan.setImageBitmap(copyBitmap);

        floorplan.setDisplayMatrix(matrix);
    }

    public void removeHotspot(Hotspot hotspot) {
        Matrix matrix = new Matrix();
        floorplan.getSuppMatrix(matrix);

        if (hotspot == null) {
            return;
        }

        Bitmap floorplanBitmap = ((BitmapDrawable) floorplan.getDrawable()).getBitmap();

        Bitmap copyBitmap = floorplanBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas();
        canvas.setBitmap(copyBitmap);

        float bWidth = floorplanBitmap.getWidth();
        float bHeight = floorplanBitmap.getHeight();

        int halfSizeX = (int) (hotspotSize*bWidth/(2*originalBitmapWidth));
        int halfSizeY = (int) (hotspotSize*bHeight/(2*originalBitmapHeight));

        int startX = (int) (hotspot.getX()*bWidth);
        int startY = (int) (hotspot.getY()*bHeight);

        canvas.drawBitmap(hotspot.getCroppedBitmap(), (startX - halfSizeX), (startY - halfSizeY), null);

        floorplan.setImageBitmap(copyBitmap);

        hotspots.remove(hotspot);

        if (newHotspots.indexOf(hotspot) != -1) {
            newHotspots.remove(hotspot);
        }

        floorplan.setDisplayMatrix(matrix);
    }

}
