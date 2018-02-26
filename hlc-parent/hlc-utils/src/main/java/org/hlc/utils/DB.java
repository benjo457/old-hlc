package org.hlc.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DB extends Tool {

	private Connection connx;
	protected String url = "jdbc:oracle:thin:@//tagazou.local:1521/TROULALAITOU";
	protected String login = "couac";
	protected String pwd = "couac";
	protected String query = "select * from XYZ";
	protected int nums = 10;
	protected int wait = 2;

	public static String module_desc = "DataBase";
	public static String module_date = "$Date: 2014-10-20 17:29:32 +0200 (lun., 20 oct. 2014) $";
	public static String module_rev  = "$Rev: 101 $";

	private static final Logger LOG = LoggerFactory.getLogger(DB.class);

	public DB() {
		super();
	}

	private void connect() throws SQLException {
		LOG.debug("opening connexion");
		connx = DriverManager.getConnection(this.url, this.login, this.pwd);
	}

	private void query() throws SQLException {
		Statement stmt = connx.createStatement();
		long startTime = System.nanoTime();
		ResultSet rs = stmt.executeQuery(this.query);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		LOG.info("duration: {}", duration / 1000000);
		while (rs.next()) {
			LOG.debug("resu: {}", rs.getString(1));
		}
	}

	private void disconnect() throws SQLException {
		this.connx.close();
	}

	public void help() {
		LOG.info("url: {}", this.url);
		LOG.info("query: {}", this.query);
		LOG.info("login: {}", this.login);
		LOG.info("pwd: *****");
		LOG.info("login: {}", this.login);
		LOG.info("nums: {}", this.nums);
		LOG.info("wait: {}", this.wait);
	}

	public void run() {
		try {
			while (--this.nums > 0) {
				this.connect();
				LOG.debug("connexion OK");
				this.query();
				this.disconnect();
				LOG.debug("disconnected");
				try {
					Thread.sleep(1000 * this.wait);
				} catch (InterruptedException e) {
					LOG.error(e.toString());
					LOG.debug(e.toString(), e);
				}
			}
		} catch (SQLException e) {
			LOG.error(e.toString());
			LOG.debug(e.toString(), e);
		}
	}
}
