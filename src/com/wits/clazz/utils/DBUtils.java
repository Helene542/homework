package com.wits.clazz.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.lang.ObjectUtils;

public class DBUtils {

	/**
	 * ���o�s�u
	 * 
	 * @return
	 * @throws Exception
	 */
	public Connection getConnectionPool(String jdbcName) throws Exception {
		Connection connection = null;
		try {
			DataSource ds = null;
			Context ctx = new InitialContext();

			if (ctx == null) {
				throw new Exception("�L�k���o���A������JNDI.");
			}

			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/" + jdbcName.toLowerCase());
			if (ds == null) {
				throw new Exception("�L�k���o���A�����s�u�x�s���귽.");
			}
			connection = ds.getConnection();
			System.out.println("�q�s�u�x�s�����o:" + connection); // for test
			return connection;
		} catch (Exception ex) {
			String driver = "com.mysql.cj.jdbc.Driver";
			String dbUrl = "jdbc:mysql://localhost:3306/world";
			String userId = "root";
			String passWord = "Kaninchen.542";
			try {
				Class.forName(driver);// 1.���JJDBC Driver
				try {
					connection = DriverManager.getConnection(dbUrl, userId, passWord);// 2.�إ߳s�u
					System.out.println("���o�@�Z�s�u����:" + connection.getClass().getName()); // for test
					return connection;
				} catch (SQLException e) {
					throw new Exception("�إ߳s�u����", e);
				}
			} catch (ClassNotFoundException e) {
				throw new Exception("���JJDBC Driver����:" + driver);
			}
		}
	}
	
	/**
	 * ����@DB�s�u
	 * @param dbKind
	 * @param dbParams
	 * @return
	 * @throws Exception
	 */
	public Connection getConnectionDirect(String dbKind, Map<String, String> dbParams) throws Exception{
		Connection conn = null;
		try {
			// 1.���J JDBC �X�ʵ{��
			Class.forName(dbParams.get(dbKind + "_DRIVER"));

			// 2.����JDBC URL
			String dbUrl = dbParams.get(dbKind + "_URL");
			String userId = dbParams.get(dbKind + "_USER");
			String passWord = dbParams.get(dbKind + "_PASS");

			// 3.���oConnection
			conn = DriverManager.getConnection(dbUrl, userId, passWord);

			if (!conn.isClosed()) {
				System.out.println("��Ʈw�s�u���\");
			}

		} catch (ClassNotFoundException e) {
			System.out.println("�䤣���X�ʵ{�����O");

		} catch (SQLException se) {
			System.out.println(se.getMessage());

		}
		return conn;
	}

	/**
	 * �����s�u
	 * 
	 * @param con
	 */
	public void closeConnection(Connection con) {
		if (null != con) {
			try {
				con.close();
			} catch (SQLException e) {
				System.out.println("�����s�u�o�Ϳ��~,, e:" + e.getMessage());
			}
		}

	}

	/**
	 * �ϥ� Statement �d�߸��
	 * 
	 * @param queryString
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, String>> queryDataByStatement(String queryString, Connection con) throws Exception {

		if (null == con) {
			throw new Exception("�s�u���šA�Х����o�s�u��A�ާ@..!");
		}

		Statement stmt = null;
		ResultSet rs = null;
		List<Map<String, String>> rtnList = new ArrayList<Map<String, String>>();

		try {

			stmt = con.createStatement();
			rs = stmt.executeQuery(queryString);

			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Map<String, String> dataMap = new HashMap<String, String>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					Object obj = rs.getObject(i);
					dataMap.put(rsmd.getColumnName(i), ObjectUtils.toString(obj, ""));
				}
				rtnList.add(dataMap);

			}
		} catch (Exception e) {
			throw new Exception("�d�߸�Ƶo�Ͳ��`, e:" + e.getMessage());

		} finally {

			if (null != rs) {
				rs.close();
			}

			if (stmt != null) {
				stmt.close();
			}

		}
		return rtnList;
	}

	/**
	 * �ϥ� PreparedStatement �d��
	 * 
	 * @param queryString
	 * @param paramMap
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, String>> queryDataByPreparedStatement(String queryString,
			Map<String, String> paramMap, Connection con) throws Exception {

		if (null == con) {
			throw new Exception("�s�u���šA�Х����o�s�u��A�ާ@..!");
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Map<String, String>> rtnList = new ArrayList<Map<String, String>>();

		try {

			pstmt = con.prepareStatement(queryString);
			rs = pstmt.executeQuery(queryString);

			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				Map<String, String> dataMap = new HashMap<String, String>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					Object obj = rs.getObject(i);
					dataMap.put(rsmd.getColumnName(i), ObjectUtils.toString(obj, ""));
				}
				rtnList.add(dataMap);

			}
		} catch (Exception e) {
			throw new Exception("�d�߸�Ƶo�Ͳ��`, e:" + e.getMessage());

		} finally {

			if (null != rs) {
				rs.close();
			}

			if (pstmt != null) {
				pstmt.close();
			}

		}
		return rtnList;
	}

}