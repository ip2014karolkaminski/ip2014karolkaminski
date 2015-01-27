package com.main.android;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainView extends SurfaceView implements SurfaceHolder.Callback{

	private Main mActivity;
	private Context mContext;
	private ViewThread mThread;
	private float mBallXPosition, mBallYPosition;
	private float mGreenBallXPosition, mGreenBallYPosition;
	private int mCanvasWidth = 1;
	private int mCanvasHeight = 1;
	private float mCanvasCenterX = 0.0f;
	private float mCanvasCenterY = 0.0f;
	
	public MainView(Context context, Main mainActivity) {
		super(context);
		// TODO Auto-generated constructor stub
		
		mActivity = mainActivity;
		SurfaceHolder holder = getHolder();
		holder.setKeepScreenOn(true);
		holder.addCallback(this);
		this.mContext=context;
		
		mThread = new ViewThread(holder, context, new Handler() {
			@Override
			public void handleMessage(Message m) {

			}
		});
	}
	
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
	    switch(event.getAction()){
	    case MotionEvent.ACTION_DOWN:
	    	if(event.getY() > (mCanvasHeight-100)) {
		    	mBallXPosition = mCanvasWidth/2 - 75;
		    	mBallYPosition = mCanvasHeight/2 - 75;
	    		mGreenBallXPosition = event.getX();
	    		mGreenBallYPosition = event.getY();
	    	} else {
		    	mBallXPosition = event.getX() - 75;
		    	mBallYPosition = event.getY() - 75;
		    	mGreenBallXPosition = mCanvasCenterX-50;
		    	mGreenBallYPosition = mCanvasHeight - 100;
	    	}
	         break;
	    case MotionEvent.ACTION_MOVE:
	    	if(event.getY() > (mCanvasHeight-100)) {
		    	mBallXPosition = mCanvasWidth/2 - 75;
		    	mBallYPosition = mCanvasHeight/2 - 75;
	    		mGreenBallXPosition = event.getX();
		    	mGreenBallYPosition = mCanvasHeight - 100;
	    	} else {
		    	mBallXPosition = event.getX() - 75;
		    	mBallYPosition = event.getY() - 75;
		    	mGreenBallXPosition = mCanvasCenterX-50;
		    	mGreenBallYPosition = mCanvasHeight - 100;
	    	}
	         break;
	    case MotionEvent.ACTION_UP:
	    	mBallXPosition = mCanvasWidth/2 - 75;
	    	mBallYPosition = mCanvasHeight/2 - 75;
	    	mGreenBallXPosition = mCanvasCenterX-50;
	    	mGreenBallYPosition = mCanvasHeight - 100;
	    	
	    	if(!mActivity.pConnected)
	    		mActivity.ConnectToNXT();
	        break;
	    }
	    
	    return true;
    }
	
	class ViewThread extends Thread {
		
		private SurfaceHolder mSurfaceHolder;
		private Bitmap mRedBall, mGreenBall;
		private boolean mThreadSwitcher;
		private Bitmap mBackgroundImage;
		long mElapsedSinceDraw = 0;
		private long mLastTime;
		long mElapsedSinceNXTCommand = 0;
		private Paint paint = new Paint();
		
		public ViewThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
			mSurfaceHolder = surfaceHolder;
			Resources res = context.getResources();
			mRedBall = BitmapFactory.decodeResource(res, R.drawable.red_circle_thumb);
			mGreenBall = BitmapFactory.decodeResource(res, R.drawable.green_ball);
			mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.da_background);
			
			paint.setColor(Color.WHITE);
			paint.setTextSize(50.0f);
		}
		
		
		public void setRunning(boolean b) {
			mThreadSwitcher = b;
		}
		
		public void setSurfaceSize(int width, int height) {
			synchronized (mSurfaceHolder) {
				mCanvasWidth = width;
				mCanvasHeight = height;
				
				mCanvasCenterX = width/2;
				mCanvasCenterY = height/2;
				
				mBallXPosition = width/2 - 75;
				mBallYPosition = height/2 - 75;
				
				mGreenBallXPosition = width/2 - 50;
				mGreenBallYPosition = height - 100;
				
				mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, width, height, true);
				mRedBall = Bitmap.createScaledBitmap(mRedBall, 150, 150, true);
				mGreenBall = Bitmap.createScaledBitmap(mGreenBall, 100, 100, true);
			}
		}
		
		private void doDraw(Canvas mCanvas) {

			if (mActivity.pConnected) {

				// draw the background
				mCanvas.drawBitmap(mBackgroundImage, 0, 0, null);
				mCanvas.drawBitmap(mRedBall, mBallXPosition, mBallYPosition, null);
				mCanvas.drawBitmap(mGreenBall, mGreenBallXPosition,  mGreenBallYPosition, null);
			} else {
				mCanvas.drawText("Tap to connect", mCanvasCenterX - 170, mCanvasCenterY - 25, paint);
			}
		}
		
		public void lockCanvasAndDraw() {
			Canvas c = null;
			try {
				c = mSurfaceHolder.lockCanvas(null);
				synchronized (mSurfaceHolder) {
					doDraw(c);

				}
			} finally {
				if (c != null) {

					mSurfaceHolder.unlockCanvasAndPost(c);

					mElapsedSinceDraw = 0;// back in time
				}
			}
		}
		
		private void updateTime() {// use for blinking
			long now = System.currentTimeMillis();

			if (mLastTime > now)
				return;

			long elapsed = now - mLastTime;
			
			mElapsedSinceDraw += elapsed;
			mElapsedSinceNXTCommand += elapsed;
			mLastTime = now;

		}
		
		@Override
		public void run() {

			while (mThreadSwitcher) {
				
				try {
				    Thread.sleep(30);
				}
				catch (InterruptedException e) {
				}

				updateTime();

				if (mElapsedSinceNXTCommand > 200) { // 200 - update time ... i gues NXTs dont like rush :(

						if(mActivity.pConnected) {
							setTheMotorsSpeed(mBallXPosition+75, mBallYPosition+75);
							grab(mGreenBallXPosition);
						}
					lockCanvasAndDraw();
                    
				}
			}
		}
	}
	
	public void grab(float x) {
		int returnedMotorSpeed = 0;
		float percentage = 0.0f;
		
		if(x < (mCanvasCenterX-50)) {
			percentage = ((mCanvasCenterX-x)/mCanvasCenterX) * 100;
			returnedMotorSpeed = -(int)((percentage/100) * 60);
		} else if (x > (mCanvasCenterX+50)) {
			percentage = ((x - mCanvasCenterX)/mCanvasCenterX) * 100;
			returnedMotorSpeed = (int)((percentage/100) * 60);
		} else {
			returnedMotorSpeed = 0;
		}
		
		mActivity.grab(returnedMotorSpeed);
	}
	
	public void setTheMotorsSpeed(float x, float y) {
		int returnedMotorASpeed = 0;
		int returnedMotorBSpeed = 0;
		float percentageX = 0.0f;
		float percentageY = 0.0f;
		
		
		if(x == (mCanvasCenterX-75) && y == (mCanvasCenterY-75)) {
			returnedMotorASpeed = 0;
			returnedMotorBSpeed = 0;
		} else if (x < mCanvasCenterX && y <= mCanvasCenterY) {
			percentageX = 100 - ((mCanvasCenterX - x)/(mCanvasCenterX) * 100);
			percentageY = 100 - (y/(mCanvasCenterY) * 100);
			
			returnedMotorASpeed = (int)(percentageY/100 * 100);
			returnedMotorBSpeed = (int)(returnedMotorASpeed * (percentageX/100));
		} else if (x >= mCanvasCenterX && y <= mCanvasCenterY) {
			percentageX = 100 - ((x - mCanvasCenterX)/(mCanvasCenterX) * 100);
			percentageY = 100 - (y/(mCanvasCenterY) * 100);
			
			returnedMotorBSpeed = (int)(percentageY/100 * 100);
			returnedMotorASpeed = (int)(returnedMotorBSpeed * (percentageX/100));
		} else if (x < mCanvasCenterX && y > mCanvasCenterY) {
			percentageX = 100 - ((mCanvasCenterX - x)/(mCanvasCenterX) * 100);
			percentageY = 100 - ((y - mCanvasCenterY)/(mCanvasCenterY) * 100);
			
			returnedMotorASpeed = -(int)(100 - ((percentageY/100 * 100)));
			returnedMotorBSpeed = (int)(returnedMotorASpeed * (percentageX/100));
		} else if (x >= mCanvasCenterX && y > mCanvasCenterY) {
			percentageX = 100 - ((x-mCanvasCenterX)/(mCanvasCenterX) * 100);
			percentageY = 100 - ((y - mCanvasCenterY)/(mCanvasCenterY) * 100);
			
			returnedMotorBSpeed = -(int)(100 - ((percentageY/100 * 100)));
			returnedMotorASpeed = (int)(returnedMotorBSpeed * (percentageX/100));
		}
		
		mActivity.move(returnedMotorBSpeed, returnedMotorASpeed);
	}
	
	public ViewThread getThread() {
		return mThread;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (getThread().getState()!=Thread.State.TERMINATED){
			 
			getThread().setRunning(true);
	 
			getThread().start();
		} else{
			mThread = new ViewThread(holder, mContext, new Handler() {
				@Override
				public void handleMessage(Message m) {

				}
			});
		
			getThread().setRunning(true);
			getThread().start();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		getThread().setSurfaceSize(width, height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		boolean retry = true;
		getThread().setRunning(false);
		while (retry) {
			try {
				getThread().join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

}
