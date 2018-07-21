package framework;

public class Const {	
	public static final int SERVER_MESSAGE_PORT = 3021;
	public static final int SERVER_FILE_PORT = 3022;
	
	public static final String IP_REGEX = "((\\w|[1-9]\\w|1\\w{2}|2[0-4]\\w|25[0-5])\\.){3}(\\w|[1-9]\\w|1\\w{2}|2[0-4]\\w|25[0-5])";
	public static final String PORT_REGEX = "(\\w|[1-9]\\w|[1-9]\\w\\w|[1-9]\\w\\w\\w|[1-5]\\w\\w\\w\\w|6[0-4]\\w\\w\\w|65[0-4]\\w\\w|655[0-2]\\w|6553[0-5])";
	
	public static final String USERNAME_REGEX = "([a-zA-Z]{1}\\w{3,19})";
	
	public static final String RUN_CLIENT_REGEX = "^(enter|open|connect|start)\\s" + USERNAME_REGEX + "\\s" + IP_REGEX + "$";
	public static final String RUN_SERVER_REGEX = "^(enter|open|connect|start)$";
	
	public static final String STOP_REGEX = "^(exit|close|disconnect|stop)$";
	
	public static final String PING_REGEX = "^ping\\s" + IP_REGEX + ":" + PORT_REGEX + "$";
	
	public static final String DOWNLOAD_REGEX = "^(download|get)\\s[0-9]+$";
	
	public static final String UPLOAD_REGEX = "^(upload|put)$";
	
	
	
}
