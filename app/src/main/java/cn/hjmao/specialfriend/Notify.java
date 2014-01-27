package cn.hjmao.specialfriend;

import cn.hjmao.specialfriend.activity.Main;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Notify {
	@SuppressWarnings("deprecation")
	public static void statusBar(Context context, String content) {
		NotificationManager notifMng = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notif = new Notification();

		Intent intent = new Intent(context, Main.class);
//		intent.setType(SpecialFriend.WeiboTable.CONTENT_TYPE);
		PendingIntent pending = PendingIntent.getActivity(context, 0, intent, 0);

		notif.icon = android.R.drawable.stat_notify_chat;
		notif.tickerText = context.getText(R.string.notif_tickertext);
		notif.defaults = Notification.DEFAULT_SOUND;
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		notif.flags |= Notification.DEFAULT_SOUND;
		
		notif.setLatestEventInfo(context,
				context.getText(R.string.notif_contenttitle),
				context.getText(R.string.notif_contenttext), pending);
		
		notifMng.notify(0, notif);
	}
}
