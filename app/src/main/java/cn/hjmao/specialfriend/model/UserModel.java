package cn.hjmao.specialfriend.model;

import java.io.ByteArrayOutputStream;

import provider.MultiTableProvider;
import provider.table.Table;
import provider.table.User;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public class UserModel {
	private static Table userTable = MultiTableProvider
			.getTable(MultiTableProvider.USER_CLASS_NAME);
	private static Uri uri = userTable.getContentUri();

	public static Bitmap getAvatar(String name) {
		Bitmap bitmap = null;

		Cursor cursor = userTable.query(uri, new String[] { User.COLUMN_NAME_AVATAR }, User.COLUMN_NAME_NAME + "=?", new String[] { name }, null);
		cursor.moveToFirst();
		int avatarColumnIndex = cursor.getColumnIndex(User.COLUMN_NAME_AVATAR);
		byte[] in = cursor.getBlob(avatarColumnIndex);
		bitmap = BitmapFactory.decodeByteArray(in, 0, in.length);
		cursor.close();

		return bitmap;
	}

	public static long insert(ContentResolver contentResolver, UserEntry user) {
		long count = -1;
		try {
			ContentValues values = user.toContentValues();
			userTable.insert(UserModel.uri, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	public static class UserEntry {
		private String name;
		private String nickname;
		private Bitmap avatar;

		public UserEntry(String name) {
			this.name = name;
		}

		public UserEntry(String name, Bitmap avatar) {
			this.name = name;
			this.avatar = avatar;
		}

		public UserEntry(String name, String nickname, Bitmap avatar) {
			this.name = name;
			this.nickname = nickname;
			this.avatar = avatar;
		}

		public String getNickname() {
			return nickname;
		}

		public String getName() {
			return name;
		}

		public Bitmap getAvatar() {
			return avatar;
		}

		public ContentValues toContentValues() {
			ContentValues values = new ContentValues();
			values.put(User.COLUMN_NAME_NAME, name);
			values.put(User.COLUMN_NAME_NICKNAME, nickname);

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			avatar.compress(Bitmap.CompressFormat.JPEG, 100, os);
			values.put(User.COLUMN_NAME_AVATAR, os.toByteArray());
			return values;
		}
	}
}
