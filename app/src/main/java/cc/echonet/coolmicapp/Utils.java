package cc.echonet.coolmicapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.UUID;

import cc.echonet.coolmicapp.Configuration.Manager;
import cc.echonet.coolmicapp.Configuration.Profile;

/**
 * Created by stjauernick@de.loewenfelsen.net on 10/13/16.
 */

public class Utils {
    public static String getStringByName(Context context, String name) {
        int resid = context.getResources().getIdentifier(name, "string", context.getPackageName());

        if (resid == 0)
            return null;

        return context.getString(resid);
    }

    public static String getStringByName(Context context, String name, int subid) {
        int resid;

        if (subid < 0) {
            resid = context.getResources().getIdentifier(String.format(Locale.ENGLISH, "%s_n%d", name, -subid), "string", context.getPackageName());
        } else {
            resid = context.getResources().getIdentifier(String.format(Locale.ENGLISH, "%s_p%d", name, subid), "string", context.getPackageName());
        }

        if (resid == 0)
            return null;

        return context.getString(resid);
    }


    public static boolean checkRequiredPermissions(Context context) {
        int grantedCount = 0;

        for (String permission : Constants.REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                grantedCount++;
            }
        }

        return grantedCount == Constants.REQUIRED_PERMISSIONS.length;
    }


    private static boolean shouldShowRequestPermissionRationale(Activity activity) {
        int grantedCount = 0;

        for (String permission : Constants.REQUIRED_PERMISSIONS) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                grantedCount++;
            }
        }

        return grantedCount > 0;
    }

    static void requestPermissions(Activity activity) {
        if (!checkRequiredPermissions(activity)) {
            if (shouldShowRequestPermissionRationale(activity)) {
                Toast.makeText(activity, R.string.settingsactivity_toast_permission_denied, Toast.LENGTH_SHORT).show();
            }

            ActivityCompat.requestPermissions(activity, Constants.REQUIRED_PERMISSIONS, Constants.PERMISSION_CHECK_REQUEST_CODE);
        } else {
            Toast.makeText(activity, R.string.settingsactivity_toast_permissions_granted, Toast.LENGTH_LONG).show();
        }
    }

    static boolean onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Constants.PERMISSION_CHECK_REQUEST_CODE) {
            boolean permissions_ok = true;

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    permissions_ok = false;
                    Toast.makeText(activity, activity.getString(R.string.settingsactivity_permission_not_granted, permissions[i]), Toast.LENGTH_LONG).show();
                }
            }

            if (permissions_ok) {
                Toast.makeText(activity, R.string.settingsactivity_permissions_all_granted, Toast.LENGTH_LONG).show();
            }

            return true;
        } else {
            return false;
        }
    }

    static void loadCMTSData(Context context, String profileName) {
        Manager manager = new Manager(context);
        Profile profile = manager.getProfile(profileName);

        profile.loadCMTSData();

        Toast.makeText(context, R.string.settings_conn_defaults_loaded, Toast.LENGTH_SHORT).show();
    }

    public static AlertDialog.Builder buildAlertDialogCMTSTOS(Activity activity) {
        AlertDialog.Builder alertDialogCMTSTOS = new AlertDialog.Builder(activity);
        alertDialogCMTSTOS.setTitle(R.string.coolmic_tos_title);
        alertDialogCMTSTOS.setMessage(R.string.coolmic_tos);
        alertDialogCMTSTOS.setNegativeButton(R.string.coolmic_tos_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return alertDialogCMTSTOS;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}
