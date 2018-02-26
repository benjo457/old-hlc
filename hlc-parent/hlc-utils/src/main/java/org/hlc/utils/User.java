package org.hlc.utils;

import java.text.Normalizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class User extends Tool {
	
	protected String gn = "Dagobert";
	protected String sn = "Lefranc";
	protected String uid;
	protected String pwd = "trustno1";
	protected static String special = "#!?=+-";

	public static String module_desc = "User";
	public static String module_date = "$Date: 2014-10-20 18:25:25 +0200 (lun., 20 oct. 2014) $";
	public static String module_rev  = "$Rev: 102 $";

	private static final Logger LOG = LoggerFactory.getLogger(User.class);
	
	public User() {
		super();
	}
	
	/**
	 * @return an ID in the form TRIXXXX with TRI being the employee trigram and XXXX are four random numbers.
	 */
	private String genId() {
		char[] newID = new char[7];
		if (gn==null) {
			newID[0] = (char) ('A' + getRandomInt(26));
		} else if (gn.length()==0) {
			newID[0] = (char) ('A' + getRandomInt(26));
		} else {
			gn = Normalizer.normalize(gn, Normalizer.Form.NFD);
			gn = gn.replaceAll("[^\\p{ASCII}]", "");
			LOG.debug("normalized gn: {}", gn);
			newID[0] = Character.toUpperCase(gn.charAt(0));
		}
		if (sn==null) {
			newID[1] = (char) ('A' + getRandomInt(26));
			newID[2] = (char) ('A' + getRandomInt(26));
		} else if (sn.length()==0) {
			newID[1] = (char) ('A' + getRandomInt(26));
			newID[2] = (char) ('A' + getRandomInt(26));
		} else {
			sn = Normalizer.normalize(sn, Normalizer.Form.NFD);
			sn = sn.replaceAll("[^\\p{ASCII}]", "");
			LOG.debug("normalized sn: {}", sn);
			newID[1] = Character.toUpperCase(sn.charAt(0));
			newID[2] = Character.toUpperCase(sn.charAt(sn.length() - 1));
		}
		for (int i = 3; i < 7; i++) {
			newID[i] = (char) ('0' + getRandomInt(10));
		}
		return new String(newID);
	}
	
	
	/**
	 * @return a eight character password with at least one upper case one lower case one digit and one special
	 *  - parametrer avec une spec de politique mdp.
	 */
	protected static String genPwd() {
		char[] newPWD = new char[10];
		newPWD[0] = (char) ('A' + getRandomInt(26));
		newPWD[1] = (char) ('a' + getRandomInt(26));
		newPWD[2] = (char) ('0' + getRandomInt(10));
		newPWD[3] = special.charAt(getRandomInt(special.length()));
		for (int i = 4; i < 10; i++) {
			int decal = getRandomInt(62 + special.length());
			if (decal < 26) {
				newPWD[i] = (char) ('A' + decal);
			} else if (decal < 52) {
				newPWD[i] = (char) ('a' + (decal - 26));
			} else if (decal < 62) {
				newPWD[i] = (char) ('0' + (decal - 52));
			} else {
				newPWD[i] = special.charAt(decal - 62);
			}
		}
		for (int i = 0; i < 10; i++) {
			int decal = getRandomInt(8 - i);
			char c = newPWD[decal + i];
			newPWD[decal + i] = newPWD[i];
			newPWD[i] = c;
		}
		return new String(newPWD);
	}

	public void help() {
		LOG.info("gn: {}", this.gn);
		LOG.info("sn: {}", this.sn);
	}
	
	public void run() {
		// print generated ID and password (and adpassword)
		this.uid = this.genId();
		this.pwd = genPwd();
		LOG.info("uid: {}",this.uid);
		LOG.info("pwd: {}",this.pwd);
	}

}
