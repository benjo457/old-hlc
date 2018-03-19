package org.hlc.utils;

//
// tool framework
//   - faire le distingo entre attribs in et out.
//      - annotation ?
//
//

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tool {

	private static final Logger LOG = LoggerFactory.getLogger(Tool.class);
	private static Pattern arg_pat = Pattern.compile("^\\w+=");
	protected static SecureRandom random = null;

	public static String module_desc = "Tool Launcher";
	public static String module_date = "$Date: 2014-10-20 18:34:12 +0200 (lun., 20 oct. 2014) $";
	public static String module_rev  = "$Rev: 104 $";
	
	public Tool() {
	}

	public Tool(String[] args) {
		setArgs(args);
	}

	public static SecureRandom getRandom() {
		if (random == null) {
			random = new SecureRandom();
		}
		return random;
	}

	public static int getRandomInt(int max) {
		int randVal = getRandom().nextInt() & 0xFFFF;
		if (randVal == 0xFFFF) {
			randVal = 0;
		}
		return (max * randVal) / 0xFFFF;
	}

	public void setArgs(String[] args) {
		Matcher arg_mat;
		String attrib, val;
		LOG.debug("class: {}", this.getClass().getName());
		for (String s : args) {
			LOG.debug("arg: {}", s);
			arg_mat = arg_pat.matcher(s);
			if (arg_mat.find()) {
				attrib = s.substring(arg_mat.start(), arg_mat.end() - 1);
				val = s.substring(arg_mat.end());
				try {
					Field field = this.getClass().getDeclaredField(attrib);
					Type type = field.getGenericType();
					LOG.debug("{} type is {}", attrib, type);
					if (type.equals(int.class)) {
						LOG.debug("it's an INT");
						field.setInt(this, Integer.parseInt(val));
					} else {
						field.set(this, val);
					}
					LOG.debug("done setting {}={}", attrib, val);
				} catch (SecurityException e) {
					LOG.error(e.toString());
					LOG.debug(e.toString(), e);
				} catch (NoSuchFieldException e) {
					LOG.error(e.toString());
					LOG.debug(e.toString(), e);
				} catch (IllegalArgumentException e) {
					LOG.error(e.toString());
					LOG.debug(e.toString(), e);
				} catch (IllegalAccessException e) {
					LOG.error(e.toString());
					LOG.debug(e.toString(), e);
				}
			} else {
				LOG.debug("no match");
			}
		}
	}

	public static void info(String desc, String date, String rev) {
		DateTime dt;
		int r;

		dt = new DateTime(date.substring(7, 17) + "T" + date.substring(18, 26) + date.substring(27, 32));
		r = Integer.parseInt(rev.substring(6, rev.length() - 2));
		LOG.info("module {} date {} revision {}", desc,  DateTimeFormat.forPattern("YYYY-MM-dd").print(dt) , r);
	}

	public void help() {
		LOG.info("Please give a command and optionnally arguments");
	}

	public void run() {
		// faire un interprete
		// pouetpouet
	}

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {
		Class<?> c = Class.forName("org.hlc.utils.Tools");
		String[] tool_args = new String[0];
		if (args.length > 0) {
			try {
				// deduire le nom du package - pas en dur
				c = Class.forName("org.hlc.utils." + args[0]);
			} catch (ClassNotFoundException e) {
				LOG.error(e.toString());
				LOG.debug(e.toString(), e);
			}
			tool_args = Arrays.copyOfRange(args, 1, args.length);
		}
		@SuppressWarnings("deprecation")
		Object o = c.newInstance();
		Class<?>[] argTypes = new Class[] { String[].class };
		Method m_setArgs = c.getMethod("setArgs", argTypes);
		Method m_run = c.getMethod("run");
		Method m_help = c.getMethod("help");
		m_setArgs.invoke(o, (Object) tool_args);
		info((String)(c.getField("module_desc").get(null)),
				(String)(c.getField("module_date").get(null)),
				(String)(c.getField("module_rev").get(null)));
		m_help.invoke(o);
		m_run.invoke(o);
	}
}
