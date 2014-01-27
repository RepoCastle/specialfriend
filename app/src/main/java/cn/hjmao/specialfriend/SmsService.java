package cn.hjmao.specialfriend;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class SmsService extends Service {
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	private static final String TAG = "SmsService";
	public static boolean isRegisterred = false;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		if (!SmsService.isRegisterred) {
			Log.v(TAG, "Start to Receiver registerred.");
			IntentFilter filter = new IntentFilter(ACTION);
			filter.setPriority(Integer.MAX_VALUE);
			SmsReceiver myService = new SmsReceiver();
			registerReceiver(myService, filter);
			Log.v(TAG, "Receiver registerred.");
			SmsService.isRegisterred = true;
		}
	}
	
	public static void registrate(Context context) {
		Intent intent2 = new Intent();
		intent2.setClass(context, SmsService.class);
		context.startService(intent2);
	}
}