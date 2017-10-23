package com.weqa.admin.util;

import android.content.Context;
import android.util.Log;

import com.weqa.admin.model.CodeConstants;
import com.weqa.admin.model.json.OrgBuilding;
import com.weqa.admin.model.json.OrgDetailResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Manish Ballav on 8/31/2017.
 */

public class QRCodeUtil {

    private static String LOG_TAG = "WEQA-LOG";

    private static SimpleDateFormat QR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private static double BUILDING_RADIUS = 500;

    private SharedPreferencesUtil util;
    private Context context;
    private double latitude, longitude;

    public QRCodeUtil(SharedPreferencesUtil util, Context context) {
        this.util = util;
        this.context = context;
    }

    public static long getBuildingId(String qrCode) {
        String[] tokens = qrCode.split(",");

        if (tokens.length < 4)
            return 0L;

        return Long.parseLong(tokens[2]);
    }

    public boolean isQRItemCodeValid(String qrCode, List<OrgDetailResponse> orgList) {
        String[] tokens = qrCode.split(",");

        if ((tokens.length != 4) || (!tokens[0].startsWith(CodeConstants.QR_CODE_ITEM)))
            return false;

        String codeType = tokens[0];
        long orgId = Long.parseLong(tokens[1]);
        long buildingId = Long.parseLong(tokens[2]);

        Date qrDate = null;
        try {
            qrDate = QR_DATE_FORMAT.parse(tokens[3]);
        }
        catch (ParseException pe) {
            Log.e(LOG_TAG, "Invalid QR Code. Error parsing.", pe);
            return false;
        }

        if (!isBuildingValid(orgId, buildingId, orgList)) {
            return false;
        }
        return true;
    }

    /**
     * Validates if orgId and buildingId are part of the authentication information
     * for the user.
     *
     * @param orgId
     * @param buildingId
     * @return
     */
    private boolean isBuildingValid(long orgId, long buildingId, List<OrgDetailResponse> orgList) {

        boolean found = false;

        for (OrgDetailResponse o : orgList) {
            if (o.getOrgId() == orgId) {
                for (OrgBuilding b : o.getOrgBuilding()) {
                    if (b.getBuildingId() == buildingId) {
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }
        }
        return found;
    }

}
