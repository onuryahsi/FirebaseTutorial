package com.onuryahsi.firebaseexample.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Vibrator;
import android.widget.Toast;

public class ClientBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            if (noConnectivity) // No internet
            {
                Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
