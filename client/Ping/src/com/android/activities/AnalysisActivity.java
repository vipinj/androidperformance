package com.android.activities;


import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;

import com.android.R;
import com.android.Session;
import com.android.helpers.ThreadPoolHelper;
import com.android.helpers.ServiceHelper;
import com.android.listeners.BaseResponseListener;
import com.android.listeners.ResponseListener;
import com.android.models.Battery;
import com.android.models.Device;
import com.android.models.GPS;
import com.android.models.Measurement;
import com.android.models.Model;
import com.android.models.Network;
import com.android.models.Ping;
import com.android.models.Sim;
import com.android.models.Throughput;
import com.android.models.Usage;
import com.android.models.Wifi;
import com.android.services.PerformanceServiceAll;
import com.android.tasks.InstallBinariesTask;
import com.android.tasks.MeasurementTask;
import com.android.tasks.SummaryTask;


public class AnalysisActivity extends Activity 
{
	
	//private TableLayout table;
	private LinearLayout table;
	private Button testButton;
	private Button configButton;
	private TextView apptext;
	private TextView devicetext;
	//private TextView tv;
	private Activity activity;
	private ThreadPoolHelper serverhelper;
	private Session session = null;
	private boolean firstPing=true;
	public String serviceTag = "PerformanceService";
	
	public static final String SETTINGS_FILE_NAME = "PingSettings";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		ThreadPoolHelper serverhelper = new ThreadPoolHelper(10,30);

		serverhelper.execute(new InstallBinariesTask(this,new HashMap<String,String>(), new String[0], new com.android.listeners.FakeListener()));
		serverhelper.execute(new SummaryTask(this,new Listener()));
		try {
			Thread.sleep(250);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(serverhelper.getThreadPoolExecutor().getActiveCount()>0){
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}

			Log.v(this.toString(),"Installing Binaries...");
		}
		Log.v(this.toString(),"Binaries Installed");
		
		setContentView(R.layout.main_screen);
		
		activity = this;
				
		serverhelper = new ThreadPoolHelper(5,10);
		testButton=(Button)findViewById(R.id.test);
		apptext = (TextView) findViewById(R.id.apptext);
		devicetext = (TextView) findViewById(R.id.devicetext);
		//configButton=(Button)findViewById(R.id.config);
		
		
		
		
		ServiceHelper.processStopService(this,"com.android.services.PerformanceService");
		ServiceHelper.processStartService(this,"com.android.services.PerformanceService");
		
		testButton.setOnClickListener(new OnClickListener()  {
			public void onClick(View v) {	

				ServiceHelper.processStopService(activity,"com.android.services.PerformanceService");
				
				Intent myIntent = new Intent(v.getContext(), RunActivity.class);
                startActivity(myIntent);
			}
		});
		
		/*configButton.setOnClickListener(new OnClickListener()  {
			public void onClick(View v) {		
				Intent myIntent = new Intent(v.getContext(), ConfigActivity.class);
                startActivityForResult(myIntent, 0);
			}
		});*/

	}	
	
	public class Listener extends BaseResponseListener{

		public void onComplete(String response) {
			// TODO Auto-generated method stub
			
		}

		public void onCompletePing(Ping response) {
			// TODO Auto-generated method stub
			
		}

		public void onCompleteMeasurement(Measurement response) {
			// TODO Auto-generated method stub
			
		}

		public void onCompleteDevice(Device response) {
			// TODO Auto-generated method stub
			
		}

		public void onCompleteBattery(Battery response) {
			// TODO Auto-generated method stub
			
		}

		public void onUpdateProgress(int val) {
			// TODO Auto-generated method stub
			
		}

		public void onCompleteGPS(GPS gps) {
			// TODO Auto-generated method stub
			
		}

		public void onCompleteUsage(Usage usage) {
			// TODO Auto-generated method stub
			
		}

		public void onCompleteThroughput(Throughput throughput) {
			// TODO Auto-generated method stub
			
		}

		public void makeToast(String text) {
			// TODO Auto-generated method stub
			
		}

		public void onCompleteSignal(String signalStrength) {
			// TODO Auto-generated method stub
			
		}

		public void onCompleteWifi(Wifi wifiList) {
			// TODO Auto-generated method stub
			
		}

		public void onCompleteNetwork(Network network) {
			// TODO Auto-generated method stub
			
		}

		public void onCompleteSIM(Sim sim) {
			// TODO Auto-generated method stub
			
		}

		public void onCompleteSummary(JSONObject Object) {
			Message msg=Message.obtain(UIHandler, 0, Object);
			UIHandler.sendMessage(msg);
			
		}
		
	}
	
	private Handler UIHandler = new Handler(){
		public void  handleMessage(Message msg) {
			JSONObject obj = (JSONObject)msg.obj;
			
			try {
				String appCount = "" + obj.get("total-apps");
				apptext.setText(appCount + " Apps Logged");
				String deviceCount = "" + obj.get("total-device");
				devicetext.setText(deviceCount + " Devices Logged");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	};
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent!=null){
	        Bundle extras = intent.getExtras();
	        //tv.setText(extras!=null ? extras.getString("returnKey") : "empty");
        }
    }
	
	
}