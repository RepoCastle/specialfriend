package provider.table;

public class User extends Table {
	private static String[] attributes = new String[] { "name:TEXT", "nickname:TEXT", "avatar:BLOB" };

	public static final String COLUMN_NAME_NAME = "name";
	public static final String COLUMN_NAME_NICKNAME = "nickname";
	public static final String COLUMN_NAME_AVATAR = "avatar";
	
	@Override
	public String[] getAttributes() {
		return attributes;
	}

}
