package com.weqa.admin.service;

import com.weqa.admin.model.json.FloorplanDetailInput;
import com.weqa.admin.model.json.FloorplanDetailResponse;
import com.weqa.admin.model.json.OrgDetailResponse;
import com.weqa.admin.model.json.SaveFloorplanInput;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Manish Ballav on 8/5/2017.
 */

public interface AdminService {

    @GET("api/admin/OrganizationDetail")
    Call<List<OrgDetailResponse>> orgDetail();

    @POST("api/admin/FloorPlanDetail")
    Call<FloorplanDetailResponse> floorplanDetail(@Body FloorplanDetailInput input);

    @POST("api/admin/SavePlanDetail")
    Call<String> saveFloorplan(@Body SaveFloorplanInput input);

}
