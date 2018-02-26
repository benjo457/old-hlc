package org.hlc.utils;

import javax.net.ssl.TrustManager;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.ConnectionClosedEventListener;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LDAP extends Tool implements ConnectionClosedEventListener {

	private static final Logger LOG = LoggerFactory.getLogger(LDAP.class);
	
	protected String host="ldap.secu.dom";
	protected int port=389;
	protected String scheme="ldap";
	protected boolean useTls = true;
	protected boolean connected = false;
	protected TrustManager tm = null;
	protected LdapNetworkConnection connection = null;
	protected long refreshInterval = 10 * 1000;

	public static String module_desc = "LDAP";
	public static String module_date = "$Date: 2014-10-20 18:34:12 +0200 (lun., 20 oct. 2014) $";
	public static String module_rev  = "$Rev: 104 $";
	
	public LDAP() {
		super();
	}
	
	protected boolean connect() {
		if (this.connection == null) {
			this.connection = new LdapNetworkConnection(this.host, this.port, false);
			this.connection.setTimeOut(-1L);
			this.connection.addConnectionClosedEventListener(this);
			try {
				this.connection.connect();
				this.connected = true;
				LOG.info("connect OK");
				return true;
			} catch (LdapException e) {
				LOG.error(e.toString());
				LOG.debug(e.toString(), e);
			}
		}
		return false;
	}

	protected void disconnect() {
		this.connected = false;
		try {
			this.connection.unBind();
			LOG.debug("Unbound from the server {}", this.host);
		} catch (Exception e) {
			LOG.error(e.toString());
			LOG.debug(e.toString(), e);
		}
		try {
			this.connection.close();
			LOG.debug("Connection closed for the server {}", this.host);
		} catch (Exception e) {
			LOG.error(e.toString());
			LOG.debug(e.toString(), e);
		}
		this.connection = null;
		LOG.info("disconnect OK");
	}

	public void connectionClosed() {
		if (!this.connected) { return; }
		boolean reconnected = false;
		disconnect();
		while (!reconnected) {
			try {
				Thread.sleep(refreshInterval);
			} catch (InterruptedException e) {
				LOG.error(e.toString());
				LOG.debug(e.toString(), e);
			}
			LOG.debug("Trying to reconnect!!!");
			reconnected = connect();
		}
	}
	
	public void help() {
		LOG.info("host: {}", this.host);
		LOG.info("port: {}", this.port);
		LOG.info("scheme: {}", this.scheme);
		LOG.info("useTls: {}", this.useTls);
	}

	public void run() {
		connect();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			LOG.error(e.toString());
			LOG.debug(e.toString(), e);
		}
		disconnect();
	}
}
