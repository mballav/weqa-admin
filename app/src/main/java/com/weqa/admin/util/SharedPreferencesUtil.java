package com.weqa.admin.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weqa.admin.model.json.ItemDetail;
import com.weqa.admin.model.json.OrgDetailResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by pc on 8/1/2017.
 */

public class SharedPreferencesUtil {

    SharedPreferences spAdmin;

    final static String ADMIN_FILENAME = "AdminInfo";

    final static String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";

    final static String ORG_DETAIL = "OD";
    final static String HOTSPOTS = "H";

    private Context context;
    private String logTag;

    public SharedPreferencesUtil(Context context, String logTag) {
        this.context = context;
        this.logTag = logTag;
    }

    public SharedPreferencesUtil(Context context) {
        this.context = context;
        this.logTag = "WEQA-LOG";
    }

    private void putString(SharedPreferences sp, String key, String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void removeKey(SharedPreferences sp, String key) {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    public List<OrgDetailResponse> getOrgDetailList() {
        spAdmin = context.getSharedPreferences(ADMIN_FILENAME, Context.MODE_PRIVATE);
        String orgDetailJson = spAdmin.getString(ORG_DETAIL, null);
        if (orgDetailJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(orgDetailJson,
                    new TypeToken<List<OrgDetailResponse>>() {
                    }.getType()); // myObject - instance of MyObject
        }
        return new ArrayList<OrgDetailResponse>();
    }

    public void addOrgDetail(List<OrgDetailResponse> r) {
        if (r != null) {
            spAdmin = context.getSharedPreferences(ADMIN_FILENAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = spAdmin.edit();
            Gson gson = new Gson();
            String json = gson.toJson(r); // myObject - instance of MyObject
            editor.putString(ORG_DETAIL, json);
            editor.commit();
        }
    }

    public void addHotspots(List<ItemDetail> itemDetailList) {
        if (itemDetailList != null && (itemDetailList.size() > 0)) {
            spAdmin = context.getSharedPreferences(ADMIN_FILENAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = spAdmin.edit();
            Gson gson = new Gson();
            String json = gson.toJson(itemDetailList); // myObject - instance of MyObject
            editor.putString(HOTSPOTS, json);
            editor.commit();
        }
    }
    public List<ItemDetail> getHotspots() {
        spAdmin = context.getSharedPreferences(ADMIN_FILENAME, Context.MODE_PRIVATE);
        String itemDetailListJson = spAdmin.getString(HOTSPOTS, null);
        if (itemDetailListJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(itemDetailListJson,
                    new TypeToken<List<ItemDetail>>() {
                    }.getType()); // myObject - instance of MyObject
        }
        return new ArrayList<ItemDetail>();
    }


    private String getCurrentDate() {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        Date today = Calendar.getInstance().getTime();
        return df.format(today);
    }

}
