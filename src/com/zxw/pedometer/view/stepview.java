package com.zxw.pedometer.view;
import com.zxw.pedometer.activity.pedometer;

import android.graphics.Paint;
import android.graphics.Canvas;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import java.io.IOException;
import android.util.Log;
import java.lang.Integer;
import android.graphics.RectF;

public class stepview extends View{
	private static float mTotalStep = 100;  // this value should set by user
	private pedometer pmeter;
	private Paint mRingPaint;
	private Paint mTextPaint;
	private Paint mProcessPaint;
	private int width,height,textwidth;
	private RectF mRectF;
    public stepview(Context context) {
        super(context);
		pmeter = (pedometer)context;
		initParam();
		initPaint();
    }
	private void initPaint()
	{
        mRingPaint = new Paint();  
        mRingPaint.setAntiAlias(true);  
        mRingPaint.setColor(0x582196F3);  
        mRingPaint.setStyle(Paint.Style.STROKE);  
        mRingPaint.setStrokeWidth(20);  

        mTextPaint = new Paint();  
        mTextPaint.setAntiAlias(true);  
        mTextPaint.setColor(0x582196F3);  
        mTextPaint.setStyle(Paint.Style.FILL);  
		mTextPaint.setTextSize(100);

        mProcessPaint = new Paint();  
        mProcessPaint.setAntiAlias(true);  
        mProcessPaint.setColor(0x582196F3);  
        mProcessPaint.setStyle(Paint.Style.STROKE);  
        mProcessPaint.setStrokeWidth(80);  
		mRectF = new RectF();
		mRectF.left = (float)100; 
		mRectF.top = (float)(height/2-width/2+100); // 左上角y
		mRectF.right = (float)(width - 100); 
		mRectF.bottom = (float)(height/2+width/2-100);
	}
	private void initParam()
	{
		Display display = pmeter.getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
		Log.i("pedometer","width is "+width+" height is "+height);
	}
	@Override

	public void onDraw(Canvas canvas)
	{
		String str = Integer.toString(pmeter.stepnum);
		textwidth = (int)mTextPaint.measureText(str, 0, str.length());
		
		canvas.drawCircle(width/2,height/2,width/2-50,mRingPaint);
		
		canvas.drawArc(mRectF, -90,((float)pmeter.stepnum/mTotalStep)*360, false, mProcessPaint);
		canvas.drawCircle(width/2,height/2,width/2-150,mRingPaint);
		canvas.drawText(str, width/2 - textwidth/2, height/2 + 18, mTextPaint);
		super.onDraw(canvas);
	}
}
