package org.telegram.irooms;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.util.TypedValue;

public class Utils {
    public static long getTaskIdFromString(String desc) {
        String taskIdentificationPart = desc.subSequence(0, desc.indexOf('\n')).toString();
        if (taskIdentificationPart.contains("Task #")) {
            try {
                long taskId = Long.parseLong(taskIdentificationPart.substring(taskIdentificationPart.indexOf("#") + 1));
                return taskId;
            } catch (Exception x) {
            }
        }
        return -1;
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
        }
        return false;
    }

    public static int px2dp(Resources resource, float px) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                px,
                resource.getDisplayMetrics()
        );
    }
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getColor(int status_code) {
        switch (status_code) {
            case 0:
                return Color.parseColor("#ffd87b29");
             //   return Color.rgb(255, 127, 80);
            case 1:
                return Color.parseColor("#ff3c7eb0");
            case 2:
                return Color.parseColor("#ff00a60e");
        }
        return Color.RED;
    }
}
