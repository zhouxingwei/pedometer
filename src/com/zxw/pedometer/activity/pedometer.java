package com.zxw.pedometer.activity;

import com.zxw.pedometer.algorithm.step;
import com.zxw.pedometer.R;
import com.zxw.pedometer.view.stepview;

import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorManager;    //for sensor
import android.content.Context;
import android.widget.EditText;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.os.Bundle;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.ImageView;
import android.os.Message;
import java.lang.Math;
import java.lang.Integer;
import java.lang.Float;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;
import android.content.SharedPreferences;
import android.widget.LinearLayout;

public class pedometer extends Activity implements SensorEventListener{

 	public pedometer()
	{ 

	}
	private step ustep;
	private static final int UPDATE_STEP = 123;
	private static final int SPORT_MODE = 1;
	private static final int STATIC_MODE = 0;
	private Handler mhandler;
	public int stepnum = 0,oldstepnum = 0;
	private int stacSwitch = 0,sportSwitch=0;
	private SensorManager mpd;
	private Sensor psensor;
	private int workmode = 0;
	private TimerTask ttask;
	private Timer timer;
	private float[] average = new float[3];
	private int times =0;
	private int workingNum = 0;
	private int allstep = 0;
	private stepview sview;
	private LinearLayout lay;
	//private TextView distance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
		setContentView(R.layout.pedometer);
		initSensor();
		initConfig();	
		ttask = new TimerTask(){
			public void run(){
				//Log.i("iPosition","stepnum:"+stepnum+" oldstepnum:"+oldstepnum+" staticswitch"+stacSwitch);
				if(workmode == STATIC_MODE && allstep - oldstepnum >= 2)
				{
					stacSwitch++;
					//workingNum = workingNum + stepnum - oldstepnum;
					if(stacSwitch > 3)
					{
						workmode = SPORT_MODE;
						stacSwitch = 0;
						//workingNum = 0;
					}

				}
				if(workmode == STATIC_MODE && allstep - oldstepnum <= 1)
				{
 					stacSwitch = 0;
				}
				if(workmode == SPORT_MODE && allstep == oldstepnum)
				{
					sportSwitch++;
					if(sportSwitch >4)
					{
						workmode = STATIC_MODE;
						sportSwitch = 0;
					}
				}
				if(workmode == SPORT_MODE && allstep > oldstepnum)
				{
					sportSwitch = 0;
				}
				oldstepnum = allstep;
			}
		};
		timer.schedule(ttask,2000,2000);
	    mhandler = new Handler(){
    		@Override
			public void handleMessage(Message msg)
			{
				if(msg.what == UPDATE_STEP)
				{
					String dis = Float.toString(stepnum*(float)0.75)+'m';
					//distance.setText(dis);
					sview.invalidate();
				}
			}
		};
	}
@Override
	protected void onStart()
	{
		super.onStart();
	}
@Override
	protected void onResume()
	{
		super.onResume();
	}
@Override
	protected void onStop()
	{
		super.onStop();

	}
@Override
	protected void onDestroy()
	{
		SharedPreferences.Editor edit = getSharedPreferences("testdata",MODE_PRIVATE).edit();
		edit.putInt("steps",stepnum);
		edit.commit();
		timer.cancel();
		super.onDestroy();
		
	}
	private void write(String content)
	{
		try
		{
			FileOutputStream fs=openFileOutput("pedometer.txt",MODE_APPEND);
			PrintStream ps=new PrintStream(fs);
			ps.println(content);
			ps.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	
	
	}
    private void initSensor() {
		mpd = (SensorManager) getSystemService(SENSOR_SERVICE);
		psensor = mpd.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mpd.registerListener(this,psensor,SensorManager.SENSOR_DELAY_GAME);

	}
	private void initConfig(){
		ustep = new step();
		timer = new Timer();
		sview = new stepview(this);
		lay = (LinearLayout)findViewById(R.id.newview);
		lay.addView(sview);
		
		//distance = (TextView)findViewById(R.id.range);
        //history display
		SharedPreferences pref=getSharedPreferences("testdata",MODE_PRIVATE);
		stepnum = pref.getInt("steps",0);
		//distance.setText(Float.toString(stepnum*(float)0.75)+'m');
	}
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
	public void onSensorChanged(SensorEvent event)
	{
		Sensor sensor = event.sensor; 
		boolean newstep;
		synchronized (this) {

			if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) 
			{
				float[] rawdata = new float[3];
				float newdata = 0; 
				int i;
				times = times+1;
				
				for(i= 0;i<3;i++)
				{
					average[i] = average[i]+event.values[i];
				}
				if(times == 10)
				{
					for (i = 0; i < 3; i++) 
					{
						rawdata[i] = (float)average[i]/10; //event.values[i];
						average[i] = 0;
						
					}
					newdata = (float)Math.sqrt(rawdata[0]*rawdata[0] + rawdata[1]*rawdata[1] + rawdata[2]*rawdata[2]);
					//String outdata = String.format("%f",newdata);
					//write(outdata);    //for analyze sensor data
					newstep = ustep.DetectNewStep(newdata);
					if(newstep)
					{
						allstep++;
						if(workmode == SPORT_MODE)
						{
							stepnum = stepnum + 1;
							mhandler.sendEmptyMessage(UPDATE_STEP);
						}
					}
					times = 0;
				}
			}
		}
	}
	@Override 
	public void onAccuracyChanged(Sensor sensor,int num)
	{

	}
}
