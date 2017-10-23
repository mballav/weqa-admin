package com.weqa.admin.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.weqa.admin.R;
import com.weqa.admin.async.GetOrgDetailAsyncTask;
import com.weqa.admin.model.json.OrgDetailResponse;
import com.weqa.admin.service.RetrofitBuilder;

import java.util.List;

import retrofit2.Retrofit;

public class SplashScreenActivity extends AppCompatActivity implements GetOrgDetailAsyncTask.UpdateUI {

    private static final String LOG_TAG = "WEQA-LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getOrgDetail();
    }

    private void getOrgDetail() {

        Retrofit retrofit = RetrofitBuilder.getRetrofit();

        Log.d(LOG_TAG, "Calling the API to get organization detail...");
        GetOrgDetailAsyncTask runner = new GetOrgDetailAsyncTask(retrofit, LOG_TAG, this);

        runner.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Log.d(LOG_TAG, "Waiting for response...");
    }

    @Override
    public void updateUI(List<OrgDetailResponse> responses) {
        Intent i = new Intent(this, FloorSelectionActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(i);
    }
}
