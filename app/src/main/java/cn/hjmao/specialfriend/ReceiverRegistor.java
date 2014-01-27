package cn.hjmao.specialfriend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceiverRegistor extends BroadcastReceiver {
	private static final String TAG = "SpecialFriend.ReceiverRegistor";
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v(TAG, "onReceive");
		SmsService.registrate(context);
	}
}
