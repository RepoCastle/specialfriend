package provider.table;

public class Weibo extends Table {
	private static String[] attributes = new String[] { "friend:TEXT", "content:TEXT", "repost:TEXT", "haspic:TEXT", "source:TEXT", "created:LONG" };

	public static final String COLUMN_NAME_FRIEND = "friend";
	public static final String COLUMN_NAME_CONTENT = "content";
	public static final String COLUMN_NAME_REPOST = "repost";
	public static final String COLUMN_NAME_HASPIC = "haspic";
	public static final String COLUMN_NAME_SOURCE = "source";
	public static final String COLUMN_NAME_CREATEAT = "created";
	public static final String DEFAULT_SORT_ORDER = COLUMN_NAME_CREATEAT + " DESC";
	
	@Override
	public String[] getAttributes() {
		return attributes;
	}
}
