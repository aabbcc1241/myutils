package myutils.connection;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import myutils.FileUtils;
import myutils.Utils;

/**
 * @author beenotung
 */
public class MyDatabaseConnector {
	private static MyPortForwardingThread portForwardingThread = null;
	private static Connection connection = null;

	/** constructor **/
	public MyDatabaseConnector() throws SQLException {
		connect();
	}

	/**
	 * My Database connection methods init, connection, commit
	 **/
	public static void checkConnection() throws SQLException {
		if (connection == null)
			connect();
	}

	public static void connect() throws SQLException {
		if (portForwardingThread == null) {
			portForwardingThread = new MyPortForwardingThread(
					MySecureInfo.getMySSHInfoForm(),
					MySecureInfo.getMyPortforwardInfoForm());
			portForwardingThread.start();
		}
		if (connection == null) {
			MySqlServerInfo mySqlServerInfoForm = MySecureInfo.getMySqlServerInfoForm();
			connection = DriverManager.getConnection(
					mySqlServerInfoForm.getUrlWithoutDB(),
					mySqlServerInfoForm.getMysqlusername(),
					mySqlServerInfoForm.getMysqlpassword());
		}
	}

	public void commit() throws SQLException {
		checkConnection();
		connection.commit();
	}

	/**
	 * My Database connection methods get connection, preparedStatement
	 **/
	public static Connection getConnection() throws SQLException {
		checkConnection();
		return connection;
	}

	public static PreparedStatement getPreparedStatement(String sql) throws SQLException {
		checkConnection();
		return connection.prepareStatement(sql);
	}

	public static PreparedStatement getPreparedStatementFromSQLFile(String filename)
			throws IOException, SQLException {
		checkConnection();
		String string = Utils.StringListToString(FileUtils.readFile(Paths.get(filename)),
				" ");
		return connection.prepareStatement(string);
	}

	/**
	 * My Database connection methods single execute
	 **/
	public static ResultSet executeQuery(String sqlQuery) throws SQLException {
		checkConnection();
		return connection.createStatement().executeQuery(sqlQuery);
	}

	public static int executeUpdate(String sqlQuery) throws SQLException {
		checkConnection();
		return connection.createStatement().executeUpdate(sqlQuery);
	}

	public static boolean execute(String sqlQuery) throws SQLException {
		checkConnection();
		return connection.createStatement().execute(sqlQuery);
	}

	public static int executeUpdate(PreparedStatement preparedStatement)
			throws SQLException {
		checkConnection();
		return preparedStatement.executeUpdate();
	}

	public static ResultSet executeQuery(PreparedStatement preparedStatement)
			throws SQLException {
		checkConnection();
		return preparedStatement.executeQuery();
	}

	public static boolean executeSQLFile(String filename) throws IOException,
			SQLException {
		checkConnection();
		String sqlQuery = Utils.StringListToString(
				FileUtils.readFile(Paths.get(filename)), " ");
		return execute(sqlQuery);
	}

	/**
	 * My Database connection methods batch (/vector) execute
	 *
	 * @throws ProcessingException
	 **/
	public static int[] executeBatch(ArrayList<String> sqlQuerys) throws SQLException {
		checkConnection();
		Statement statement = connection.createStatement();
		for (String sqlQuery : sqlQuerys) {
			statement.addBatch(sqlQuery);
		}
		return statement.executeBatch();
	}

	public static Vector<ResultSet> executeQuery_Strings(Vector<String> sqlQuerys)
			throws SQLException {
		checkConnection();
		Vector<ResultSet> resultSets = new Vector<ResultSet>();
		for (String sqlQuery : sqlQuerys)
			resultSets.add(executeQuery(sqlQuery));
		return resultSets;
	}

	public static Vector<Integer> executeUpdate_Strings(Vector<String> sqlQuerys)
			throws SQLException {
		checkConnection();
		Vector<Integer> sqlStatuss = new Vector<Integer>();
		for (String sqlQuery : sqlQuerys)
			sqlStatuss.add(executeUpdate(sqlQuery));
		return sqlStatuss;
	}

	public static Vector<Boolean> execute(Vector<String> sqlQuerys) throws SQLException {
		checkConnection();
		Vector<Boolean> hasResultSets = new Vector<Boolean>();
		for (String sqlQuery : sqlQuerys)
			hasResultSets.add(execute(sqlQuery));
		return hasResultSets;
	}

	public static Vector<Integer> executeUpdate_PreparedStatements(
			Vector<PreparedStatement> preparedStatements) throws SQLException {
		checkConnection();
		Vector<Integer> sqlStatuss = new Vector<Integer>();
		for (PreparedStatement preparedStatement : preparedStatements)
			sqlStatuss.add(executeUpdate(preparedStatement));
		return sqlStatuss;
	}

	public static Vector<ResultSet> executeQuery_PreparedStatements(
			Vector<PreparedStatement> preparedStatements) throws SQLException {
		checkConnection();
		Vector<ResultSet> resultSets = new Vector<ResultSet>();
		for (PreparedStatement preparedStatement : preparedStatements)
			resultSets.add(executeQuery(preparedStatement));
		return resultSets;
	}

	/** debug method **/
	public static void printSQLException(SQLException e) {
		System.out.println();
		System.out.println("SQLException ErrorCode: " + e.getErrorCode());
		System.out.println("SQLException SQLState: " + e.getSQLState());
		System.out.println("SQLException Message: " + e.getMessage());
		e.printStackTrace();
	}
}