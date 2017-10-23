package com.weqa.admin.async;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.weqa.admin.model.json.FloorplanDetailInput;
import com.weqa.admin.model.json.FloorplanDetailResponse;
import com.weqa.admin.service.AdminService;
import com.weqa.admin.util.SharedPreferencesUtil;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by Manish Ballav on 10/1/2017.
 */

public class GetFloorplanAsyncTask extends AsyncTask<Object, String, String> {

    public static interface UpdateUI {
        public void updateUI(FloorplanDetailResponse response);
    }

    public static final String STATUS_OK = "ok";
    public static final String STATUS_FAILURE = "not-ok";

    private Retrofit retrofit;
    private String logTag;
    private Activity activity;
    private FloorplanDetailResponse response;
    private SharedPreferencesUtil sharedPrefUtil;

    public GetFloorplanAsyncTask(Retrofit retrofit, String logTAG, Activity activity) {
        this.retrofit = retrofit;
        this.logTag = logTAG;
        this.activity = activity;
        sharedPrefUtil = new SharedPreferencesUtil(this.activity.getApplicationContext());
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            getFloorplanDetail((FloorplanDetailInput) params[0]);
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

    private void getFloorplanDetail(FloorplanDetailInput input) {
        AdminService service = retrofit.create(AdminService.class);
        Call<FloorplanDetailResponse> call1 = service.floorplanDetail(input);
        try {
            Log.e(logTag, "Retrofit call now...");
            Gson gson = new Gson();
            String inputJson = gson.toJson(input);
            Log.d(logTag, "Input: " + inputJson);

            response = call1.execute().body();
        }
        catch (IOException ioe) {
            Log.d(logTag, "Error in retrofit call" + ioe.getMessage());
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
            String json = gson.toJson(response);
            Log.d(logTag, "Output: " + json);
            Log.d(logTag, "Get floorplan response received!");

            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((GetFloorplanAsyncTask.UpdateUI) activity).updateUI(response);
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



