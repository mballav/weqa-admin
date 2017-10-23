package com.weqa.admin.async;

/**
 * Created by Manish Ballav on 10/1/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.weqa.admin.model.json.OrgDetailResponse;
import com.weqa.admin.service.AdminService;
import com.weqa.admin.util.SharedPreferencesUtil;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by Manish Ballav on 9/21/2017.
 */

public class GetOrgDetailAsyncTask extends AsyncTask<Object, String, String> {

    public static interface UpdateUI {
        public void updateUI(List<OrgDetailResponse> responses);
    }

    public static final String STATUS_OK = "ok";
    public static final String STATUS_FAILURE = "not-ok";

    private Retrofit retrofit;
    private String logTag;
    private Activity activity;
    private List<OrgDetailResponse> responseList;
    private SharedPreferencesUtil sharedPrefUtil;

    public GetOrgDetailAsyncTask(Retrofit retrofit, String logTAG, Activity activity) {
        this.retrofit = retrofit;
        this.logTag = logTAG;
        this.activity = activity;
        sharedPrefUtil = new SharedPreferencesUtil(this.activity.getApplicationContext());
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            getOrgDetail();
        } catch (Exception e) {
            Log.d(logTag, "Error in async task " + e.getMessage());
            final Context context = this.activity.getApplication();
            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Please try again later!", Toast.LENGTH_LONG).show();
                }
            });
            return STATUS_FAILURE;
        }
        return STATUS_OK;
    }

    private void getOrgDetail() {
        AdminService service = retrofit.create(AdminService.class);
        Call<List<OrgDetailResponse>> call1 = service.orgDetail();
        try {
            Log.e(logTag, "Retrofit call now...");

            responseList = call1.execute().body();
        }
        catch (IOException ioe) {
            Log.d(logTag, "Error in retrofit call " + ioe.getMessage());
            final Context context = this.activity.getApplication();
        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(String status) {

        final Activity a = this.activity;
        final Context context = this.activity.getApplication();

        if (status.equals(STATUS_OK)) {

            Gson gson = new Gson();
            String json = gson.toJson(responseList);
            Log.d(logTag, "Output: " + json);
            Log.d(logTag, "Get organization detail response received!");

            sharedPrefUtil.addOrgDetail(responseList);

            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((GetOrgDetailAsyncTask.UpdateUI) activity).updateUI(responseList);
                }
            });
        }
        else {
            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Problem with connectivity... exiting!", Toast.LENGTH_LONG).show();
                    activity.finish();
                }
            });
        }
    }

}

