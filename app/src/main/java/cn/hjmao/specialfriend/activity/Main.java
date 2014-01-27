package cn.hjmao.specialfriend.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import cn.hjmao.specialfriend.R;
import cn.hjmao.specialfriend.SmsService;

@SuppressWarnings("deprecation")
public class Main extends TabActivity {
	private RadioGroup group;
	private TabHost tabHost;
	public static final String TAB_HOME = "tabHome";
	public static final String TAB_MESSAGE = "tabMessage";
	public static final String TAB_FRIENDS = "tabFriends";
	public static final String TAB_DISCOVERY = "tabDiscovery";
	public static final String TAB_SETTING = "tabSetting";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (!SmsService.isRegisterred) {
			SmsService.registrate(this);
			SmsService.isRegisterred = true;
		}

		// InputStream input =
		// getResources().openRawResource(R.drawable.user_default_avatar);
		// Bitmap bitmap = BitmapFactory.decodeStream(input,null,null);
		// UserEntry user = new UserEntry("default", "default", bitmap);
		// ContentResolver contentResolver = this.getContentResolver();
		// UserModel.insert(contentResolver, user);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		group = (RadioGroup) findViewById(R.id.main_radio);
		tabHost = getTabHost();
		tabHost.addTab(tabHost.newTabSpec(TAB_HOME).setIndicator(TAB_HOME)
				.setContent(new Intent(this, HomeActivity.class)));
		tabHost.addTab(tabHost.newTabSpec(TAB_MESSAGE)
				.setIndicator(TAB_MESSAGE)
				.setContent(new Intent(this, MessageActivity.class)));
		tabHost.addTab(tabHost.newTabSpec(TAB_FRIENDS)
				.setIndicator(TAB_FRIENDS)
				.setContent(new Intent(this, FriendsActivity.class)));
		tabHost.addTab(tabHost.newTabSpec(TAB_DISCOVERY)
				.setIndicator(TAB_DISCOVERY)
				.setContent(new Intent(this, DiscoveryActivity.class)));
		tabHost.addTab(tabHost.newTabSpec(TAB_SETTING)
				.setIndicator(TAB_SETTING)
				.setContent(new Intent(this, SettingActivity.class)));
		tabHost.setCurrentTabByTag(TAB_HOME);

		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radio_button0:
					tabHost.setCurrentTabByTag(TAB_HOME);
					break;
				case R.id.radio_button1:
					tabHost.setCurrentTabByTag(TAB_MESSAGE);
					break;
				case R.id.radio_button2:
					tabHost.setCurrentTabByTag(TAB_FRIENDS);
					break;
				case R.id.radio_button3:
					tabHost.setCurrentTabByTag(TAB_DISCOVERY);
				case R.id.radio_button4:
					tabHost.setCurrentTabByTag(TAB_SETTING);
				default:
					break;
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}