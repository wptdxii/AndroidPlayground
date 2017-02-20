package com.cloudhome.utils;

import com.cloudhome.application.MyApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class IpConfig {
	

	public static Properties pro = new Properties();

	 static {
		
		InputStream in = IpConfig.class.getClassLoader().getResourceAsStream(
				"config.properties");
		try {
			pro.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getIp() {
	
		return pro.getProperty("ip")+"only_key="+MyApplication.only_key+"&";
	}
	public static String getIp2() {
		
		return pro.getProperty("ip2");
	}
	public static String getIp3() {
		return pro.getProperty("ip3");
	}



	public static String getIp4() {
		return pro.getProperty("ip4");
	}


	public static String getUri(String uri) {
		return getIp() +pro.getProperty("commom")+ pro.getProperty(uri);
	}

	public static String getUri2(String uri) {
			return getIp2() + pro.getProperty(uri)+"only_key="+MyApplication.only_key+"&"+pro.getProperty("commom");
	}
	public static String getUri3(String uri) {
		return getIp3() + pro.getProperty(uri);
	}

	public static String getUri4(String uri) {
		return getIp4()  + pro.getProperty(uri) + pro.getProperty("commom");
	}

	public static String getIp5() {
		return pro.getProperty("ip5");
	}
	public static String getUri5(String uri) {
		return getIp5()  + pro.getProperty(uri);
	}

	public static String getIp6() {
		return pro.getProperty("ip6");
	}

	public static String getUri6(String uri) {
		return pro.getProperty("ip6") +  pro.getProperty(uri);
	}


	public static String getCommon() {
		return pro.getProperty("commom");
	}
	
}
