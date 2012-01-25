package com.android.helpers;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.android.models.Measure;
import com.android.models.Ping;
import com.android.utils.CommandLineUtil;
import com.android.utils.ParseUtil;

public class TracerouteHelper {
	
	public static CommandLineUtil cmdUtil;
	public static String output;
	
	
	/**
	 * Pinghelp helps run ping command by creating cmd and inputs
	 * @return
	 */
	public static String tracerouteHelp(String ip_address) {
		
		String options = "-lv -w 3";
		cmdUtil = new CommandLineUtil();
		
		output = cmdUtil.runCommand("traceroute", ip_address, options);
		
		
		//Measure ping_measurement = ParseUtil.PingParser(output);

		
		return "";
	}

	

}