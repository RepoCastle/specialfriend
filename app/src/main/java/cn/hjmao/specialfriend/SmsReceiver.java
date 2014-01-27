package cn.hjmao.specialfriend;

import cn.hjmao.specialfriend.model.WeiboModel;
import cn.hjmao.specialfriend.model.WeiboModel.WeiboEntry;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("TAG", "onReceive");

		Object[] pdus = (Object[]) intent.getExtras().get("pdus");
		if (pdus != null && pdus.length > 0) {
			SmsMessage[] messages = new SmsMessage[pdus.length];
			for (int i = 0; i < pdus.length; i++) {
				byte[] pdu = (byte[]) pdus[i];
				messages[i] = SmsMessage.createFromPdu(pdu);
			}

			String content = "";
			for (SmsMessage message : messages) {
				content += message.getMessageBody();
			}

			if (messages.length > 0) {
				String sender = messages[0].getOriginatingAddress();
				if (sender.matches(SpecialFriend.SMS_NUMBER_PATTERN)) {
					WeiboEntry weibo = WeiboModel.parse(content);
					if (weibo != null) {
						ContentResolver contentResolver = context.getContentResolver();
						WeiboModel.insert(contentResolver, weibo);
						this.abortBroadcast();
						Notify.statusBar(context, content);
					} else {
						new Exception("It is not a weibo sms").printStackTrace();
					}
				}
			}
		}
	}
}
