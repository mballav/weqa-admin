package com.weqa.admin.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.weqa.admin.R;
import com.weqa.admin.model.Hotspot;
import com.weqa.admin.ui.FloorplanEditActivity;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Manish Ballav on 9/30/2017.
 */

public class PopupMenuUtil {

    private static final String LOG_TAG = "WEQA-LOG";

    private PopupWindow popupWindowCreate, popupWindowOldHotspot, popupWindowNewHotspot;

    /*
    public static Rect locateView(View v, float x, float y)
    {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try
        {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe)
        {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = (int) (location.left + v.getWidth()*x);
        location.bottom = (int) (location.top + v.getHeight()*y);
        return location;
    }*/

    public void showNewHotspotPopupMenu(final FloorplanEditActivity activity, View v, final Hotspot hotspot,
                                               PhotoView photoView, int x, int y) {

        LayoutInflater layoutInflater
                = (LayoutInflater) activity.getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_new_hotspot, null);
        popupWindowNewHotspot = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        Button btn1 = (Button)popupView.findViewById(R.id.button1);
        Button btn2 = (Button)popupView.findViewById(R.id.button2);
        Button btn3 = (Button)popupView.findViewById(R.id.button3);

        if (hotspot.isEnabled())
            btn3.setText("Disable");
        else
            btn3.setText("Enable");

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindowNewHotspot.dismiss();
                activity.popupWindowClosed();
                activity.updateQRCode(hotspot);
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindowNewHotspot.dismiss();
                activity.popupWindowClosed();
                String itemType = hotspot.getItemType();
                activity.removeHotspot(hotspot);
                Toast.makeText(activity, itemType + " removed", Toast.LENGTH_SHORT).show();
            }
        });

        btn3.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                popupWindowNewHotspot.dismiss();
                activity.popupWindowClosed();
                if (b.getText().toString().equals("Disable")) {
                    hotspot.setEnabled(false);
                    Toast.makeText(activity, hotspot.getItemType() + " disabled", Toast.LENGTH_SHORT).show();
                }
                else if (b.getText().toString().equals("Enable")) {
                    hotspot.setEnabled(true);
                    Toast.makeText(activity, hotspot.getItemType() + " enabled", Toast.LENGTH_SHORT).show();
                }
                activity.overwriteHotspot(hotspot);
            }});

        int[] loc_int = new int[2];
        photoView.getLocationOnScreen(loc_int);

        popupWindowNewHotspot.showAtLocation(photoView, Gravity.TOP|Gravity.LEFT, loc_int[0] + x, loc_int[1] + y);//showing popup menu

        activity.popupWindowOpened(popupWindowNewHotspot);
    }

    public void showOldHotspotPopupMenu(final FloorplanEditActivity activity, View v, final Hotspot hotspot,
                                               PhotoView photoView, int x, int y) {

//        Rect locationRect = locateView(v, hotspot.getX(), hotspot.getY());

        LayoutInflater layoutInflater
                = (LayoutInflater) activity.getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_old_hotspot, null);
        popupWindowOldHotspot = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        Button btn = (Button)popupView.findViewById(R.id.button1);

        if (hotspot.isEnabled())
            btn.setText("Disable");
        else
            btn.setText("Enable");

        btn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {

                Button b = (Button) v;
                popupWindowOldHotspot.dismiss();
                activity.popupWindowClosed();
                if (b.getText().toString().equals("Disable")) {
                    hotspot.setEnabled(false);
                    Toast.makeText(activity, hotspot.getItemType() + " disabled", Toast.LENGTH_SHORT).show();
                }
                else if (b.getText().toString().equals("Enable")) {
                    hotspot.setEnabled(true);
                    Toast.makeText(activity, hotspot.getItemType() + " enabled", Toast.LENGTH_SHORT).show();
                }
                activity.overwriteHotspot(hotspot);
            }});

        int[] loc_int = new int[2];
        photoView.getLocationOnScreen(loc_int);

        popupWindowOldHotspot.showAtLocation(photoView, Gravity.TOP|Gravity.LEFT, loc_int[0] + x, loc_int[1] + y);//showing popup menu

        activity.popupWindowOpened(popupWindowOldHotspot);
    }

    public void showCreateHotspotPopup(final FloorplanEditActivity activity, List<String> itemTypes,
                                              PhotoView photoView, int x, int y) {

        LayoutInflater layoutInflater
                = (LayoutInflater) activity.getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);

        View popupView = layoutInflater.inflate(R.layout.popup_create_hotspot, null);

        int textSizeInSp = (int) activity.getResources().getDimension(R.dimen.popup_menu_font_size);

        LinearLayout ll = (LinearLayout) popupView.findViewById(R.id.rootContainer);

        RadioGroup radioItemType = new RadioGroup(activity);
        radioItemType.setOrientation(RadioGroup.VERTICAL);

        final RadioButton[] rb = new RadioButton[itemTypes.size()];
        for(int i = 0; i < itemTypes.size(); i++){
            rb[i]  = new RadioButton(activity);
            rb[i].setText(itemTypes.get(i));
            rb[i].setTextColor(ContextCompat.getColor(activity, R.color.colorTABtext));
            //rb[i].setTextSize(convertSpToPixels(textSizeInSp , activity));
            rb[i].setBackgroundColor(ContextCompat.getColor(activity, R.color.colorMENU));
            radioItemType.addView(rb[i]);
        }

        ll.addView(radioItemType);

        Button scanButton = new Button(activity);
        scanButton.setText("Scan WEQA Code");
        scanButton.setTextColor(ContextCompat.getColor(activity, R.color.colorTABtext));
//        scanButton.setTextSize(convertSpToPixels(textSizeInSp , activity));
        scanButton.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
        scanButton.setMinHeight(0);
        scanButton.setTransformationMethod(null);
        scanButton.setMinWidth(0);
        scanButton.setPadding(20, 20, 20, 20);

        ll.addView(scanButton);

        // This overrides the radiogroup onCheckListener
        radioItemType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked)
                {
                    // Changes the textview's text to "Checked: example radiobutton text"
                    activity.itemTypeSelected(checkedRadioButton.getText().toString());
                }
            }
        });

        rb[0].setChecked(true);

        popupWindowCreate = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        // if button is clicked, close the popup window
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindowCreate.dismiss();
                activity.popupWindowClosed();
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        });

        int[] loc_int = new int[2];
        photoView.getLocationOnScreen(loc_int);

        popupWindowCreate.showAtLocation(photoView, Gravity.TOP|Gravity.LEFT, loc_int[0] + x, loc_int[1] + y);//showing popup menu

        activity.popupWindowOpened(popupWindowCreate);
    }

    public static float convertSpToPixels(float sp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
}
