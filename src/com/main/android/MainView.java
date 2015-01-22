package com.main.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainView extends SurfaceView implements SurfaceHolder.Callback{

	private Main mActivity;
	private Context mContext;
	private ViewThread mThread;
	private static final int REDRAW_SCHED = 100;
	private float mBallXPosition, mBallYPosition;
	private float mGreenBallXPosition, mGreenBallYPosition;
	private int mCanvasWidth = 1;
	private int mCanvasHeight = 1;
	private float mCanvasCenterX = 0.0f;
	private float mCanvasCenterY = 0.0f;
	private String testText = "DEFAULT";
	
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
	    
        //return super.onTouchEvent(event);
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
		private boolean isConnected;
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
				
//				float mAHeight = mActionButton.getHeight();
//				float mAWidth = mActionButton.getWidth();
//				mActionButton = Bitmap.createScaledBitmap(mActionButton, width, (Math.round((width * (mAHeight / mAWidth)))), true);
//				mActionDownButton = Bitmap.createScaledBitmap(mActionDownButton, width, (Math.round((width * (mAHeight / mAWidth)))), true);
				
				mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, width, height, true);
				mRedBall = Bitmap.createScaledBitmap(mRedBall, 150, 150, true);
				mGreenBall = Bitmap.createScaledBitmap(mGreenBall, 100, 100, true);

//				int temp_ratio = mCanvasWidth / 64;
//				GOAL_WIDTH = mCanvasWidth / temp_ratio;
//
//				ICON_MAX_SIZE = (GOAL_WIDTH / 8) * 6;
//				ICON_MIN_SIZE = (GOAL_WIDTH / 4);
//
//				temp_ratio = mCanvasHeight / 64;
//				GOAL_HEIGHT = mCanvasHeight / temp_ratio;
//
//				mTarget = Bitmap.createScaledBitmap(mTarget, GOAL_WIDTH, GOAL_HEIGHT, true);
//				mTargetInactive = Bitmap.createScaledBitmap(mTargetInactive, GOAL_WIDTH, GOAL_HEIGHT, true);
			}
		}
		
		private void doDraw(Canvas mCanvas) {

			if (mActivity.pConnected) {

				// draw the background
				mCanvas.drawBitmap(mBackgroundImage, 0, 0, null);
				mCanvas.drawBitmap(mRedBall, mBallXPosition, mBallYPosition, null);
				mCanvas.drawBitmap(mGreenBall, mGreenBallXPosition,  mGreenBallYPosition, null);
				//mCanvas.drawText(testText, 30.0f, 30.0f, paint);
				//mCanvas.drawLine(mCanvasCenterX, 0.0f, mCanvasCenterX, mCanvasHeight, paint);
				//mCanvas.drawLine(0.0f, mCanvasCenterY, mCanvasWidth, mCanvasCenterY, paint);
				//draw pressed action button
				//mCanvas.drawBitmap(mActionDownButton, 0, mCanvasHeight - mActionButton.getHeight(), null);
				//draw icon in goal
				// draw the goal
				//mCanvas.drawBitmap(mTargetInactive, (mCanvasWidth - mTarget.getWidth()) / 2, ((mCanvasHeight - mActionButton.getHeight()) / 2)
				//		- (mTarget.getHeight() / 2), null);
			} else {
				//mCanvas.drawBitmap(mRedBall, mBallXPosition, mBallYPosition, null);
				mCanvas.drawText("Tap to connect", mCanvasCenterX - 170, mCanvasCenterY - 25, paint);
//				// Draw the background image. Operations on the Canvas accumulate
//				if (thread.isInGoal()) { // icon is in goal
//					mInGoal = true;
//					mGrowAdjust = calcGrowAdjust(mX, mY);
//				} else {
//					mGrowAdjust = ICON_MAX_SIZE;
//					if (mInGoal) {// was in goal before
//						mInGoal = false;
//						vibrate();
//					}
//				}
//
//				// draw the background
//				mCanvas.drawBitmap(mBackgroundImage, 0, 0, null);
//
//				// draw the action button
//				mCanvas.drawBitmap(mActionPressed ? mActionDownButton : mActionButton, 0, mCanvasHeight - mActionButton.getHeight(), null);
//				mActionPressed = false;
//
//				// draw the goal
//				mCanvas.drawBitmap(mTarget, (mCanvasWidth - mTarget.getWidth()) / 2,
//						((mCanvasHeight - mActionButton.getHeight()) / 2) - (mTarget.getHeight() / 2), null);
//
//				// update the icon location and draw (or blink) it
//				if (mInGoal) {
//
//					mIconOrange.setBounds((int) mX - (mGrowAdjust / 2), (int) mY - ((mGrowAdjust / 2)), ((int) mX + (mGrowAdjust / 2)), (int) mY
//							+ (mGrowAdjust / 2));
//					mIconOrange.draw(mCanvas);
//
//				} else {
//
//					// boundary checking, don't want the move_icon going off-screen.
//					if (mX + ICON_MAX_SIZE / 2 >= mCanvasWidth) {// set at outer edge
//
//						mX = mCanvasWidth - (ICON_MAX_SIZE / 2);
//					} else if (mX - (ICON_MAX_SIZE / 2) < 0) {
//						mX = ICON_MAX_SIZE / 2;
//					}
//
//					// boundary checking, don't want the move_icon rolling
//					// off-screen.
//					if (mY + ICON_MAX_SIZE / 2 >= (mCanvasHeight - mActionButton.getHeight())) {// set at outer edge
//
//						mY = mCanvasHeight - mActionButton.getHeight() - ICON_MAX_SIZE / 2;
//					} else if (mY - ICON_MAX_SIZE / 2 < 0) {
//						mY = ICON_MAX_SIZE / 2;
//					}
//
//					if (mLastTime > mNextPulse) {
//
//						mPulsingTiltIcon = mPulsingTiltIcon == mIconOrange ? mIconWhite : mIconOrange;
//						 
//						mNextPulse = mPulsingTiltIcon == mIconOrange ? mLastTime + calcNextPulse() : mLastTime + 90;
//					}
//
//					mPulsingTiltIcon.setBounds((int) mX - (mGrowAdjust / 2), (int) mY - (mGrowAdjust / 2), ((int) mX + mGrowAdjust / 2),
//							((int) mY + mGrowAdjust / 2));
//					mPulsingTiltIcon.draw(mCanvas);
//
//				}
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

			// double elapsed = (now - mLastTime) / 1000.0;
			long elapsed = now - mLastTime;
			
			mElapsedSinceDraw += elapsed;
			mElapsedSinceNXTCommand += elapsed;
			mLastTime = now;

		}
		
		@Override
		public void run() {
		    
		//	Log.d(TAG, "--run--");
			while (mThreadSwitcher) {
				
				try {
				    Thread.sleep(30);
				}
				catch (InterruptedException e) {
				}

				updateTime();
				//updateMoveIndicator(mAccelX, mAccelY);
				//doActionButtonFeedback();
				// is it time to update the screen?
				//if (mElapsedSinceDraw > REDRAW_SCHED) {

					//is it time to update motor movement?
					if (mElapsedSinceNXTCommand > 200) { // 200 - update time ... i gues NXTs dont like rush :(
//						//calculate and send command to move motors							
//						doMotorMovement(-mNumAcY, -mNumAcX);
//					}
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
		
		testText = ("MOTOR_A: " + returnedMotorBSpeed + " MOTOR_B: " + returnedMotorASpeed);
		mActivity.move(returnedMotorBSpeed, returnedMotorASpeed);
		
		
//		float valueX = Math.abs(mCanvasCenterX - x);
//		float valueY = Math.abs(mCanvasCenterY - y);
//		
//		float percentage = valueX/valueY * 100;
//		float turn = percentage/100 * 100;  // 50 is the most comfortable speed to make device turn
//		float forward = (100-percentage)/100 * 100; // i guess 100 is maximum speed of NXT
//		
//		
//		if(valueX < mCanvasCenterX) {
//			testText = ("MOTOR_A: " + (int)turn + " MOTOR_B: " + (int)forward);
//			mActivity.move((int)turn, (int)forward);
//		} else if (valueX >= mCanvasCenterX) {
//			testText = ("MOTOR_A: " + (int)forward + " MOTOR_B: " + (int)turn);
//			mActivity.move((int)forward, (int)turn);
//		}
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
