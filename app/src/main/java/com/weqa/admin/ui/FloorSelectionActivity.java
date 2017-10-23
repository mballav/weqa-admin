package com.weqa.admin.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.weqa.admin.R;
import com.weqa.admin.model.json.OrgBuilding;
import com.weqa.admin.model.json.OrgDetailResponse;
import com.weqa.admin.model.json.ResponseOrgFloorDetail;
import com.weqa.admin.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class FloorSelectionActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {

    private static final String LOG_TAG = "WEQA-LOG";

    private Spinner orgSpinner, buildingSpinner, floorSpinner;

    private List<OrgDetailResponse> orgDetailList;
    private SharedPreferencesUtil util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_selection);

        orgSpinner = (Spinner) findViewById(R.id.orgSpinner);
        buildingSpinner = (Spinner) findViewById(R.id.buildingSpinner);
        floorSpinner = (Spinner) findViewById(R.id.floorSpinner);

        Button nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnTouchListener(this);
        nextButton.setOnClickListener(this);

        util = new SharedPreferencesUtil(this, LOG_TAG);
        orgDetailList = util.getOrgDetailList();

        addItemsToOrgSpinner();
    }

    @Override
    public void onClick(View view) {

        OrgDetailResponse r = orgDetailList.get(orgSpinner.getSelectedItemPosition());
        OrgBuilding b = r.getOrgBuilding().get(buildingSpinner.getSelectedItemPosition());
        ResponseOrgFloorDetail f = b.getResponseOrgFloorDetail().get(floorSpinner.getSelectedItemPosition());

        Intent i = new Intent(this, FloorplanEditActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        i.putExtra("ORG_ID", r.getOrgId());
        i.putExtra("BUILDING_ID", b.getBuildingId());
        i.putExtra("FLOORPLAN_ID", f.getFloorPlanId());

        i.putExtra("ORG_NAME", r.getName());
        i.putExtra("BUILDING_NAME", b.getAddress());
        i.putExtra("FLOORPLAN_NAME", "Floor " + f.getFloorLevel());

        this.startActivity(i);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Button b = (Button) v;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            b.setBackgroundResource(R.drawable.super_rounded_button_yellow);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            b.setBackgroundResource(R.drawable.super_rounded_button_grey);
        }
        return false;
    }

    public void addItemsToOrgSpinner() {

        List<String> list = new ArrayList<String>();
        for (OrgDetailResponse r : orgDetailList) {
            list.add(r.getName());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item3, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item3);
        orgSpinner.setAdapter(dataAdapter);
        orgSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                addItemsToBuildingSpinner(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void addItemsToBuildingSpinner(final int positionOrg) {

        final List<String> list = new ArrayList<String>();
        OrgDetailResponse r = orgDetailList.get(positionOrg);
        for (OrgBuilding b : r.getOrgBuilding()) {
            list.add(b.getAddress());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item3, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item3);
        buildingSpinner.setAdapter(dataAdapter);
        buildingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                addItemsToFloorSpinner(positionOrg, pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void addItemsToFloorSpinner(int positionOrg, int positionBuilding) {

        List<String> list = new ArrayList<String>();
        OrgDetailResponse r = orgDetailList.get(positionOrg);
        OrgBuilding b = r.getOrgBuilding().get(positionBuilding);
        for (ResponseOrgFloorDetail f : b.getResponseOrgFloorDetail()) {
            list.add("Floor " + f.getFloorLevel());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item3, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item3);
        floorSpinner.setAdapter(dataAdapter);
    }

}
