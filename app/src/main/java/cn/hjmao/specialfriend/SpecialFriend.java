package cn.hjmao.specialfriend;

import java.util.regex.Pattern;


public class SpecialFriend {
	public static final String AUTHORITY = "cn.hjmao.specialfriend";
	public static final String DATABASE_NAME = "specialfriend.db";

//    1252014781969374
//    <user>Huajian<content>HelloworldContent<repost>RepostContent<extra>1|mad.png<end>

	public static final String WEIBO_SUBJECT_TAG = "<user>";
	public static final String WEIBO_CONTENT_TAG = "<content>";
	public static final String WEIBO_REPOST_TAG = "<repost>";
	public static final String WEIBO_EXTRA_TAG = "<extra>";
	public static final String WEIBO_END_TAG = "<end>";

	public static final Pattern WEIBO_NAME_PATTERN = Pattern.compile("(@[^:|^@|^ |^;|^/]+)");
	public static final String WEIBO_NAME_POLISH_COLOR = "#001A4C";
	public static final int DEFAULT_WEIBO_COUNT = 50;
	public static final String SMS_NUMBER_PATTERN = "^1252014781969374$";
}
