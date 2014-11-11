package myutils.connection;

/**
 * @author beenotung
 */
public class MySecureInfo {

	// digit ocean server
	private static final String sshHost = "128.199.172.14";
	private static final String sshUsername = "beeno";
	private static final String sshPassword = "wpc1415";
	private static final int portforwardLocalPort = 1234;
	private static final String portforwardRemoteHost = "localhost";
	private static final int portforwardRemotePort = 3306;
	private static final String mysqlProtocol = "jdbc:mariadb";// jdbc:mysql
	private static final String mysqlHost = "localhost";
	private static final int mysqlPort = portforwardLocalPort;
	private static final String mysqlUsername = "beeno";
	private static final String mysqlPassword = "wpc1415";
	private static final String mysqlDatabasename = "beeno";

	// local server
	// private static final int mysqlPort = 3306;
	// private static final String mysqlUsername = "root";
	// private static final String mysqlPassword = "mysqlB(10v2TC";
	// private static final String mysqlDatabasename = "nndb"; // wholesaler

	/**
	 * @return my secure info @
	 */
	public static MySSHInfo getMySSHInfoForm() {
		return new MySSHInfo(sshHost, sshUsername, sshPassword);
	}

	public static MyPortforwardInfo getMyPortforwardInfoForm() {
		return new MyPortforwardInfo(portforwardLocalPort, portforwardRemoteHost,
				portforwardRemotePort);
	}

	public static MySqlServerInfo getMySqlServerInfoForm() {
		return new MySqlServerInfo(mysqlProtocol, mysqlHost, mysqlPort,
				mysqlDatabasename, mysqlUsername, mysqlPassword);
	}

}