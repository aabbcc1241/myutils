package myutils.connection;

import myutils.FileUtils;
import myutils.CollectionUtils;
import myutils.gui.NonEditableTableModel;

import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;

/**
 * @author beenotung
 */
@SuppressWarnings({"WeakerAccess", "UnusedDeclaration"})
public class MyDatabaseConnector {
  private static MyPortForwardingThread portForwardingThread = null;
  private static Connection connection = null;
  private static MySqlServerInfo mySqlServerInfoForm = null;

  /**
   * My Database connection methods init, connection, commit
   */
  public static void saveMySqlServerInfo(MySqlServerInfo mySqlServerInfoForm) {
    MyDatabaseConnector.mySqlServerInfoForm = mySqlServerInfoForm;
  }

  private static void checkConnection() throws SQLException {
    if (connection == null)
      connect();
  }

  private static void connect() throws SQLException {
    if (connection == null) {
      connection = DriverManager.getConnection(
          mySqlServerInfoForm.getUrlWithoutDB(),
          mySqlServerInfoForm.getMysqlUsername(),
          mySqlServerInfoForm.getMysqlPassword());
    }
  }

  public static void commit() throws SQLException {
    checkConnection();
    connection.commit();
  }

  public static void disconnect() throws SQLException {
    if (connection != null)
      connection.close();
    connection = null;
  }

  /**
   * My Database connection methods get connection, preparedStatement
   */
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
    String string = CollectionUtils.StringListToString(FileUtils.readFile(Paths.get(filename)),
        " ");
    return connection.prepareStatement(string);
  }

  /**
   * My Database connection methods single execute
   */
  public static ResultSet executeQuery(String sqlQuery) throws SQLException {
    checkConnection();
    return connection.createStatement().executeQuery(sqlQuery);
  }

  private static int executeUpdate(String sqlQuery) throws SQLException {
    checkConnection();
    return connection.createStatement().executeUpdate(sqlQuery);
  }

  public static boolean execute(String sqlQuery) throws SQLException {
    checkConnection();
    return connection.createStatement().execute(sqlQuery);
  }

  private static int executeUpdate(PreparedStatement preparedStatement)
      throws SQLException {
    checkConnection();
    return preparedStatement.executeUpdate();
  }

  private static ResultSet executeQuery(PreparedStatement preparedStatement)
      throws SQLException {
    checkConnection();
    return preparedStatement.executeQuery();
  }

  public static boolean executeSQLFile(String filename) throws IOException,
      SQLException {
    checkConnection();
    String sqlQuery = CollectionUtils.StringListToString(
        FileUtils.readFile(Paths.get(filename)), " ");
    return execute(sqlQuery);
  }

  /**
   * My Database connection methods batch (/vector) execute
   */
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
    Vector<ResultSet> resultSets = new Vector<>();
    for (String sqlQuery : sqlQuerys)
      resultSets.add(executeQuery(sqlQuery));
    return resultSets;
  }

  public static Vector<Integer> executeUpdate_Strings(Vector<String> sqlQuerys)
      throws SQLException {
    checkConnection();
    Vector<Integer> sqlStatuses = new Vector<>();
    for (String sqlQuery : sqlQuerys)
      sqlStatuses.add(executeUpdate(sqlQuery));
    return sqlStatuses;
  }

  public static Vector<Boolean> execute(Vector<String> sqlQuerys) throws SQLException {
    checkConnection();
    Vector<Boolean> hasResultSets = new Vector<>();
    for (String sqlQuery : sqlQuerys)
      hasResultSets.add(execute(sqlQuery));
    return hasResultSets;
  }

  public static Vector<Integer> executeUpdate_PreparedStatements(
      Vector<PreparedStatement> preparedStatements) throws SQLException {
    checkConnection();
    Vector<Integer> sqlStatuses = new Vector<>();
    for (PreparedStatement preparedStatement : preparedStatements)
      sqlStatuses.add(executeUpdate(preparedStatement));
    return sqlStatuses;
  }

  public static Vector<ResultSet> executeQuery_PreparedStatements(
      Vector<PreparedStatement> preparedStatements) throws SQLException {
    checkConnection();
    Vector<ResultSet> resultSets = new Vector<>();
    for (PreparedStatement preparedStatement : preparedStatements)
      resultSets.add(executeQuery(preparedStatement));
    return resultSets;
  }

  /**
   * debug method *
   */
  public static void printSQLException(SQLException e) {
    System.out.println();
    System.out.println("SQLException ErrorCode: " + e.getErrorCode());
    System.out.println("SQLException SQLState: " + e.getSQLState());
    System.out.println("SQLException Message: " + e.getMessage());
    e.printStackTrace();
  }

  public static DefaultTableModel getTableModel(ResultSet resultSet)
      throws SQLException {
    DefaultTableModel model = new DefaultTableModel(0, 0);
    String[] titles = new String[resultSet.getMetaData().getColumnCount()];
    for (int i = 0; i < titles.length; i++)
      titles[i] = resultSet.getMetaData().getColumnLabel(i + 1);
    model.setColumnIdentifiers(titles);
    while (resultSet.next()) {
      Object[] rowData = new Object[titles.length];
      for (int i = 0; i < rowData.length; i++)
        rowData[i] = resultSet.getObject(i + 1);
      model.addRow(rowData);
    }
    return model;
  }

  public static void resetTableModel(NonEditableTableModel model, ResultSet resultSet)
      throws SQLException {
    String[] titles = new String[resultSet.getMetaData().getColumnCount()];
    for (int i = 0; i < titles.length; i++)
      titles[i] = resultSet.getMetaData().getColumnLabel(i + 1);
    model.setColumnIdentifiers(titles);
    while (model.getRowCount() > 0)
      model.removeRow(0);
    while (resultSet.next()) {
      Object[] rowData = new Object[titles.length];
      for (int i = 0; i < rowData.length; i++)
        rowData[i] = resultSet.getObject(i + 1);
      model.addRow(rowData);
    }
  }

  public static void resetTableModel(DefaultTableModel model, ResultSet resultSet)
      throws SQLException {
    String[] titles = new String[resultSet.getMetaData().getColumnCount()];
    for (int i = 0; i < titles.length; i++)
      titles[i] = resultSet.getMetaData().getColumnLabel(i + 1);
    model.setColumnIdentifiers(titles);
    while (model.getRowCount() > 0)
      model.removeRow(0);
    while (resultSet.next()) {
      Object[] rowData = new Object[titles.length];
      for (int i = 0; i < rowData.length; i++)
        rowData[i] = resultSet.getObject(i + 1);
      model.addRow(rowData);
    }
  }

  public static void addToTableModel(DefaultTableModel model, ResultSet resultSet)
      throws SQLException {
    while (resultSet.next()) {
      Object[] rowData = new Object[resultSet.getMetaData().getColumnCount()];
      for (int i = 0; i < rowData.length; i++)
        rowData[i] = resultSet.getObject(i + 1);
      model.addRow(rowData);
    }
  }

  public static Vector<Object[]> resultSetToVectors(ResultSet resultSet) throws SQLException {
    Vector<Object[]> vectors = new Vector<>();
    while (resultSet.next()) {
      Object[] rowData = new Object[resultSet.getMetaData().getColumnCount()];
      for (int i = 0; i < rowData.length; i++)
        rowData[i] = resultSet.getObject(i + 1);
      vectors.add(rowData);
    }
    return vectors;
  }
}
