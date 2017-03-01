package com.zxw.pedometer.algorithm;

public class step{ 
	private float lastvalue = 0;
	private long lastpeaktime = 0;
	private long curpeaktime = 0; 
	private long curtime=0;
	private float peak;
	private float valley;
	private float thredhold = (float)2.3;
	private float initThredhold = (float)1.3;
	private boolean curDirectionUp = false;
	private boolean lastDirectionUp = false;
	private int peakCount = 0;
	private float[] thredholdbuf = new float[5];  
	private int thredholdnum = 0;

	private int peakUpCount = 0;
	public step(){}
	private boolean DetectPeak(float newValue, float oldValue) 
	{

		lastDirectionUp = curDirectionUp;

		if (newValue >= oldValue) { 
			curDirectionUp = true;
			//peakCount++;
		} else {
			//peakUpCount = peakCount;
			//peakCount = 0;
			curDirectionUp = false;
		}

		if (!curDirectionUp && lastDirectionUp) {
			peak = oldValue;
			return true;
		} else if(!lastDirectionUp && curDirectionUp) {
			valley = oldValue;
		} 

		return false;
	}
	private void UpdateThredhold()
	{
		float sum = 0;
		int usedthred = 0;

		if(thredholdnum >= 5)
		{
			thredholdnum = 0;
		}
		thredholdbuf[thredholdnum] =  peak - valley;
		thredholdnum++;
		for(int i = 0;i < 5;i++)
		{
			sum += thredholdbuf[i];
			if(thredholdbuf[i] > 0.1) usedthred++;
		}

		thredhold = (float)sum/usedthred;
		if(thredhold >= 8)
		{
			thredhold = (float)4.3;
		}
		if(thredhold >= 7 && thredhold < 8)
		{
			thredhold = (float)3.5;
		}
		else if(thredhold >=4 && thredhold < 7)
		{
			thredhold = (float)2.6;
		}
		else if(thredhold >=3 && thredhold < 4)
		{
			thredhold = (float)2.2;
		}
		else
		{
			thredhold = (float)1.5;
		}
	}

	public boolean DetectNewStep(float values) 
	{		
		curtime = System.currentTimeMillis();
		if ((lastvalue > 0) && (DetectPeak(values, lastvalue))) 
	 	{
			lastpeaktime = curpeaktime;
			if (curtime - lastpeaktime >= 230 && (peak - valley >= thredhold)) 
			{
				curpeaktime = curtime;   
				//UpdateThredhold();
				return true;
			}
			if (curtime - lastpeaktime >= 230 && (peak - valley >= initThredhold)) {
				curpeaktime = curtime;
				UpdateThredhold();
			}
		}

		lastvalue = values;
		return false;
	}
}
