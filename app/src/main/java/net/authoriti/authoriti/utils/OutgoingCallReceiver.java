package net.authoriti.authoriti.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class OutgoingCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Vishal : ", intent.toString());
        Toast.makeText(context, "Outgoing call catched!", Toast.LENGTH_LONG).show();
    }
}
