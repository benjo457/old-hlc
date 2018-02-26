package org.hlc.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mail extends Tool {

	protected String host = "localhost";
	protected String from = "charles@radio-londres.uk";
	protected String to = "lucien@pavupapris.fr";
	protected String subj = "messages personnels";
	protected String body = "Les carottes sont cuites";

	public static String module_desc = "Mail";
	public static String module_date = "$Date: 2014-10-20 18:25:25 +0200 (lun., 20 oct. 2014) $";
	public static String module_rev  = "$Rev: 102 $";

	private static final Logger LOG = LoggerFactory.getLogger(Mail.class);

	public Mail() {
		super();
	}

	private void send() {
		try {
			// Set the host smtp address
			Properties props = new Properties();
			props.put("mail.smtp.host", host);

			// create some properties and get the default Session
			Session session = Session.getDefaultInstance(props, null);

			// create a message
			Message msg = new MimeMessage(session);

			// set the from and to address
			msg.setFrom(new InternetAddress(from));
			InternetAddress[] addressTo = new InternetAddress[1];
			addressTo[0] = new InternetAddress(to);
			msg.setRecipients(Message.RecipientType.TO, addressTo);
			
			msg.setSubject(subj);
            msg.setContent(body,"text/html");
            Transport.send(msg);
			
		} catch (AddressException e) {
			LOG.error(e.toString());
			LOG.debug(e.toString(), e);
		} catch (MessagingException e) {
			LOG.error(e.toString());
			LOG.debug(e.toString(), e);
		}

	}
	
	public void help() {
		LOG.info("host: {}", this.host);
		LOG.info("from: {}", this.from);
		LOG.info("to: {}", this.to);
		LOG.info("subj: {}", this.subj);
		LOG.info("body: {}", this.body);
	}
	
	public void run() {
		this.send();
		LOG.info("mail sent");
	}
}
