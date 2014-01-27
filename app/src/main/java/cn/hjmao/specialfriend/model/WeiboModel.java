package cn.hjmao.specialfriend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import provider.MultiTableProvider;
import provider.table.Table;
import provider.table.Weibo;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import cn.hjmao.specialfriend.SpecialFriend;
import cn.hjmao.specialfriend.utils.Date;

public class WeiboModel {
	private static final Table weiboTable = MultiTableProvider.getTable(MultiTableProvider.WEIBO_CLASS_NAME);
	private static final Uri uri = weiboTable.getContentUri();

	public static WeiboEntry parse(String smsContent) {
		int subjectStart = smsContent.indexOf(SpecialFriend.WEIBO_SUBJECT_TAG);
		int contentStart = smsContent.indexOf(SpecialFriend.WEIBO_CONTENT_TAG);
		int repostStart = smsContent.indexOf(SpecialFriend.WEIBO_REPOST_TAG);
		int extraStart = smsContent.indexOf(SpecialFriend.WEIBO_EXTRA_TAG);
		int contentEnd = smsContent.indexOf(SpecialFriend.WEIBO_END_TAG);

		if (subjectStart >= contentStart || contentStart >= repostStart || repostStart > extraStart || extraStart >= contentEnd) {
			return null;
		}
		String friend = smsContent.substring(subjectStart+SpecialFriend.WEIBO_SUBJECT_TAG.length(), contentStart);
		String content = smsContent.substring(contentStart+SpecialFriend.WEIBO_CONTENT_TAG.length(), repostStart);
		String repost = smsContent.substring(repostStart+SpecialFriend.WEIBO_REPOST_TAG.length(), extraStart);
		String extra = smsContent.substring(extraStart + SpecialFriend.WEIBO_EXTRA_TAG.length(), contentEnd);
		String TAG = "WeiboModel";
		Log.v(TAG, friend);
		Log.v(TAG, content);
		Log.v(TAG, repost);
		Log.v(TAG, extra);
		String hasPic = "0";
		String source = "UNKNOWN";
		int index = extra.indexOf("|");
		if (index != -1) {
			hasPic = extra.substring(0, index);
			if (index < extra.length()-1) {
				source = extra.substring(index + 1);
			}
		}
		WeiboEntry weibo = new WeiboEntry(friend, content, repost, hasPic, source, Long.toString(Date.now()));
		return weibo;
	}
	
	public static long insert(ContentResolver contentResolver, WeiboEntry weibo) {
		long count = -1;
		try {
			ContentValues values = weibo.toContentValues();
			weiboTable.insert(WeiboModel.uri, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	public static List<WeiboEntry> getWeiboList(int start, int end, int count) {
		List<WeiboEntry> weibos = new ArrayList<WeiboEntry>();
		
		Cursor cursor = weiboTable.query(WeiboModel.uri, null, null, null, Weibo.DEFAULT_SORT_ORDER + " LIMIT " + count);
		
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			String friend = cursor.getString(cursor.getColumnIndex(Weibo.COLUMN_NAME_FRIEND));
			String content = cursor.getString(cursor.getColumnIndex(Weibo.COLUMN_NAME_CONTENT));
			String repost = cursor.getString(cursor.getColumnIndex(Weibo.COLUMN_NAME_REPOST));
			String haspic = cursor.getString(cursor.getColumnIndex(Weibo.COLUMN_NAME_HASPIC));
			String source = cursor.getString(cursor.getColumnIndex(Weibo.COLUMN_NAME_SOURCE));
			String time = cursor.getString(cursor.getColumnIndex(Weibo.COLUMN_NAME_CREATEAT));
			weibos.add(new WeiboEntry(friend, content, repost, haspic, source, time));
		}
		cursor.close();
		return weibos;
	}
	
	public static String polish(String content) {
		String polished = "";
		Matcher matcher = SpecialFriend.WEIBO_NAME_PATTERN.matcher(content);
		
		int start = 0;
		while (matcher.find(start)) {
			int sindex = matcher.start();
			int eindex = matcher.end();
			String weiboName = matcher.group(1);
			weiboName = "<font color=" + SpecialFriend.WEIBO_NAME_POLISH_COLOR + ">" + weiboName + "</font>";
			polished += content.substring(start, sindex) + weiboName;
			start = eindex;
		}
		polished += content.substring(start);
		
		return polished;
	}
	
	public static class WeiboEntry {
		
		private String friend;
		private String content;
		private String repost;
		private String hasPic;
		private String source;
		private String createat;
		
		public WeiboEntry(String friend, String content, String repost, String hasPic, String source, String createat) {
			this.friend = friend;
			this.content = content;
			this.repost = repost;
			this.hasPic = hasPic;
			this.source = source;
			this.createat = createat;
		}
		
		public ContentValues toContentValues() {
			ContentValues values = new ContentValues();
			try {
				values.put(Weibo.COLUMN_NAME_FRIEND, friend);
				values.put(Weibo.COLUMN_NAME_CONTENT, content);
				values.put(Weibo.COLUMN_NAME_REPOST, repost);
				values.put(Weibo.COLUMN_NAME_HASPIC, hasPic);
				values.put(Weibo.COLUMN_NAME_SOURCE, source);
				values.put(Weibo.COLUMN_NAME_CREATEAT, createat);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return values;
		}
		

		public String getFriend() {
			return friend;
		}
		public String getFriendNoAT() {
			int index = friend.indexOf("@");
			String name = friend;
			if (index != -1) {
				name = name.substring(index+1);
			}
			return name;
		}

		public String getContent() {
			return content;
		}

		public String getRepost() {
			return repost;
		}

		public void setRepost(String repost) {
			this.repost = repost;
		}

		public String getHasPic() {
			return hasPic;
		}

		public String getSource() {
			return source;
		}

		public String getCreateat() {
			return createat;
		}

		public String getDeltaTime() {
			String deltaStr = "";
			
			int count = 0;
			String unit = "s";
			
			long delta = (Date.now() - new Long(createat)) / 1000;
			long hour;
			long min;
			long sec;
			
			sec = delta;
			min = sec / 60;
			if (min == 0) {
				count = (int) delta;
				unit = "secs";
			} else {
				if ((hour = min / 60) == 0) {
					count = (int) min;
					unit = "mins";
				} else {
					if ((hour / 24) == 0) {
						count = (int) hour;
						unit = "hours";
					} else {
						count = 0;
					}
				}
			}
			if (count == 0) {
				deltaStr = Date.mills2dayHourMin(new Long(createat));
			} else	 {
				if (count == 1) {
					unit = unit.substring(0, unit.length()-1);
				}
				deltaStr = count + " " + unit + " ago";
			}
			
			return deltaStr;
		}
	}

}
