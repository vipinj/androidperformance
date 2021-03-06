package com.num.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Random;

import android.content.Context;
import android.util.Log;

import com.num.Values;
import com.num.listeners.ResponseListener;
import com.num.models.Link;
import com.num.models.Throughput;



public class ThroughputUtil {
	
	static long responseListenerUpdateFrequency = 800;
	
	public static String generateRandom()
	{
		Random number=new Random();
		StringBuilder message=new StringBuilder();
		for(int i=0;i<1405;i++)
		{
			message.append('a'+number.nextInt(52));
		}
		return message.toString();
	}
	
	public static Link uplinkmeasurement(Context context, ResponseListener responseListener) throws UnknownHostException, IOException
	{
		Values session = (Values) context.getApplicationContext();
		DeviceUtil device = new DeviceUtil();
		String countrycode = device.getNetworkCountry(context);
		String serveraddress= session.THROUGHPUT_SERVER_ADDRESS;
		if(countrycode.equals("za"))
		{
			serveraddress = session.SA_THROUGHPUT_SERVER_ADDRESS;
		}
		
		SocketAddress serversocket = new InetSocketAddress(serveraddress,session.UPLINKPORT);
		Socket uplinkclient=new Socket();
		Link link = new Link();
		try{
			uplinkclient.connect(serversocket);
		}
		catch(ConnectException e)
		{
			link.setCount(1);
			link.setMessage_size(0);
			link.setTime(1);
			link.setDstIp(serveraddress);
			link.setDstPort(session.UPLINKPORT+"");
			return link;
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(uplinkclient.getInputStream()));
		PrintWriter out = new PrintWriter(uplinkclient.getOutputStream(), true);
		
		System.out.println("Starting uplink test:");
		String buf= generateRandom();
		byte[] message = buf.getBytes();
		long throughput=0;
		String close = "end";
		long endSecond =  System.currentTimeMillis();
		long start = System.currentTimeMillis();
		//System.out.println(start);
		long end = System.currentTimeMillis();		
		long count=0;
		
		do
		{
			out.write(buf);			
			end = System.currentTimeMillis();
			
			if (end>endSecond+responseListenerUpdateFrequency) {
				endSecond = end;
				link.setCount(count);
				link.setMessage_size(message.length+(Values.TCP_HEADER_SIZE*3));
				link.setTime(end-start);
				link.setDstIp(serveraddress);
				link.setDstPort(session.UPLINKPORT+"");				
				responseListener.onUpdateUpLink(link);
			}
			
			count++;
			//System.out.println("Throughput time diff = "+(end-start));
		}while(end-start<=session.UPLINK_DURATION);
		System.out.println("Writing end");
		out.println();
		out.println(close);
		//char receive_buffer[] = new char[10];
		int total = Integer.parseInt(in.readLine());
		Log.d("Uplink Measurement", "Received total "+ total);
		long time = (long) Integer.parseInt(in.readLine());
		Log.d("Uplink Measurement", "Received Time " + time);
		total+=count*Values.TCP_HEADER_SIZE*3;		
		link.setCount(1);
		link.setMessage_size(total);
		link.setTime(time);
		link.setDstIp(session.THROUGHPUT_SERVER_ADDRESS);
		link.setDstPort(session.UPLINKPORT+"");
		throughput = total*8/time/1000;
		System.out.println(link.toJSON());
		//tensecthroughput = tenseccount*((long)message.length+(54*3))/((end-intermediate)*8);
		try{
			Thread.sleep(2000);
		}
		catch(InterruptedException e1)
		{
			e1.printStackTrace();
		}
		
		System.out.println("Uplink test complete");
		System.out.println("Overall throughput: "+throughput + "kbps");
		
		out.close();
		in.close();
		uplinkclient.close();
		
		return link;
	}

	public static Link downlinkmeasurement(Context context, ResponseListener responseListener) throws IOException
	{
		
		Values session = (Values) context.getApplicationContext();
		DeviceUtil device = new DeviceUtil();
		String countrycode = device.getNetworkCountry(context);
		String serveraddress=session.THROUGHPUT_SERVER_ADDRESS;
		if(countrycode.equals("za"))
		{
			serveraddress = session.SA_THROUGHPUT_SERVER_ADDRESS;
		}
		
		SocketAddress serversocket = new InetSocketAddress(serveraddress, session.DOWNLINKPORT);
		Link link = new Link();
		Socket downlinkclient=new Socket();
		try{
			downlinkclient.connect(serversocket);
		}
		catch(ConnectException e)
		{
			link.setCount(1);
			link.setMessage_size(0);
			link.setTime(1);
			link.setDstIp(serveraddress);
			link.setDstPort(session.DOWNLINKPORT+"");
			return link;
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(downlinkclient.getInputStream()));
		DataOutputStream out = new DataOutputStream(downlinkclient.getOutputStream());
		System.out.println("Starting downlink test:");
		
		out.flush();
		try {
			Thread.sleep(session.NORMAL_SLEEP_TIME);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int messagebytes=0;
		int message = 0;
		long totalbytes=0;
		long slowstartcorrect = 0;
		int count=0;
		long start=System.currentTimeMillis();
		long mid = 0;
		int flag = 0;
		
		long endSecond =  System.currentTimeMillis();
		long end=System.currentTimeMillis();
		char[] buffer=new char[session.DOWNLINK_BUFFER_SIZE];
		do
		{
			message = in.read(buffer, 0, session.DOWNLINK_BUFFER_SIZE);
			//messagebytes=message.length();
			//System.out.println("The message size is "+ message);
			count++;
			if(message<=0)
				break;
			

			if (end>endSecond+responseListenerUpdateFrequency) {
				endSecond = end;
				link.setCount(1);
				link.setMessage_size(totalbytes*((session.TCP_PACKET_SIZE+session.TCP_HEADER_SIZE)/(session.TCP_PACKET_SIZE)));
				link.setTime(end-start);
				link.setDstIp(serveraddress);
				link.setDstPort(session.DOWNLINKPORT+"");
				responseListener.onUpdateDownLink(link);
			}
			
			
			totalbytes+=message;
			end=System.currentTimeMillis();
			if(flag==0&&end-start>=5000)
			{
				flag =1;
				mid = end;
				slowstartcorrect = totalbytes;
			}
		}while(true);
		link.setCount(1);
		link.setMessage_size((totalbytes-slowstartcorrect)*((session.TCP_PACKET_SIZE+session.TCP_HEADER_SIZE)/(session.TCP_PACKET_SIZE)));
		link.setTime(end-mid);
		link.setDstIp(serveraddress);
		link.setDstPort(session.DOWNLINKPORT+"");
		System.out.println("Downlink test complete");
		System.out.println("Packets received " + count);
		if(end-start>0) System.out.println("Throughput: "+ totalbytes*8/(int)(end-start)+ " kbps");
		System.out.println(link.toJSON());
		out.close();
		in.close();
		downlinkclient.close();
		return link;
	}

}
