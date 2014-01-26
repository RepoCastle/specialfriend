package cn.hjmao.specialfriend.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.hjmao.specialfriend.R;
import cn.hjmao.specialfriend.SpecialFriend;
import cn.hjmao.specialfriend.model.UserModel;
import cn.hjmao.specialfriend.model.WeiboModel;
import cn.hjmao.specialfriend.model.WeiboModel.WeiboEntry;

public class HomeActivity extends Activity {
	private List<Map<String, Object>> weiboListItems;
	private ListView weiboListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		showWeiboList();
	}

	@Override
	protected void onResume() {
		super.onResume();
		showWeiboList();
	}

	private void showWeiboList() {
		weiboListView = (ListView) findViewById(R.id.weibo_list);
		weiboListItems = getData();
		MyAdapter adapter = new MyAdapter(this);
		weiboListView.setAdapter(adapter);
	}

	private List<Map<String, Object>> getData() {
		List<WeiboEntry> weibos = WeiboModel.getWeiboList(0, -1,
				SpecialFriend.DEFAULT_WEIBO_COUNT);
		List<Map<String, Object>> logs = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < weibos.size(); i++) {
			WeiboEntry weibo = weibos.get(i);
			String friend = weibo.getFriendNoAT();
			if (friend == null) {
				friend = "unknown";
			}
			String content = weibo.getContent();
			String delta = weibo.getDeltaTime();
			String ref = weibo.getRepost();
			String hasPic = weibo.getHasPic();
			String source = weibo.getSource();
			
			Bitmap bitmap = null;
			try {
				bitmap = UserModel.getAvatar("default");
			} catch (Exception e) {
				if (bitmap == null) {
					bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.user_default_avatar)).getBitmap();
				}
			}
			Map<String, Object> map;
			map = new HashMap<String, Object>();
			map.put("weibo_friend_avatar", bitmap);
			map.put("weibo_body_friend_name", friend);
			map.put("weibo_body_time", delta);
			map.put("weibo_body_content", content);
			map.put("weibo_body_ref_weibo", ref);
			map.put("weibo_body_haspic", hasPic);
			map.put("weibo_body_source", "From: " + source);
			logs.add(map);
		}
		return logs;
	}

	public final class ViewHolder {
		public ImageView avatar;
		public TextView friend;
		public TextView time;
		public TextView content;
		public TextView refWeibo;
		public ImageView hasPic;
		public TextView source;
	}

	public class MyAdapter extends BaseAdapter {

		public int getCount() {
			return weiboListItems.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.weibo_list_item, null);
				holder.avatar = (ImageView) convertView
						.findViewById(R.id.weibo_friend_avatar);
				holder.friend = (TextView) convertView
						.findViewById(R.id.weibo_body_friend_name);
				holder.time = (TextView) convertView
						.findViewById(R.id.weibo_body_time);
				holder.content = (TextView) convertView
						.findViewById(R.id.weibo_body_content);
				holder.refWeibo = (TextView) convertView.findViewById(R.id.weibo_body_ref_weibo);
				holder.hasPic = (ImageView) convertView.findViewById(R.id.weibo_body_haspic);
				holder.source = (TextView) convertView.findViewById(R.id.weibo_body_source);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.avatar.setImageBitmap((Bitmap) weiboListItems.get(position).get("weibo_friend_avatar"));
			holder.friend.setText((String) weiboListItems.get(position).get("weibo_body_friend_name"));
			holder.friend.getPaint().setFakeBoldText(true);
			holder.time.setText((String) weiboListItems.get(position).get("weibo_body_time"));
			String content = (String) weiboListItems.get(position).get("weibo_body_content");
			content = WeiboModel.polish(content);
			holder.content.setText(Html.fromHtml(content));
			
			String refWeiboContent = (String) weiboListItems.get(position).get("weibo_body_ref_weibo");
			RelativeLayout layout=(RelativeLayout) convertView.findViewById(R.id.weibo_body_ref); 
			if (refWeiboContent!=null && refWeiboContent.length()>0) {  
				layout.setVisibility(View.VISIBLE);
				refWeiboContent = WeiboModel.polish(refWeiboContent);
				holder.refWeibo.setText(Html.fromHtml(refWeiboContent));
			} else { 
				layout.setVisibility(View.GONE);
			}
			String hasPic = (String) weiboListItems.get(position).get("weibo_body_haspic");
			if ("1".equals(hasPic)) {
				holder.hasPic.setVisibility(View.VISIBLE);
			} else {
				holder.hasPic.setVisibility(View.GONE);
			}
			String source = (String) weiboListItems.get(position).get("weibo_body_source");
			holder.source.setText(source);
			return convertView;
		}

		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}
	}
}
