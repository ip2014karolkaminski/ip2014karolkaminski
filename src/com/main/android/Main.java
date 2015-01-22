package com.main.android;

import java.io.IOException;
import java.util.ArrayList;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class Main extends Activity
{
	private Button mConnectionButton, mForwardButton, mBackButton, mLeftButton, mRightButton;
	private NXTCommunicator myNXTCommunicator = null;
	private Handler mainHandler;
	private ProgressDialog connectingProgressDialog;
	private String MAC_ADRESS;
	private int mCoordinateX, mCoordinateY;
	private MainView mView;
	public boolean pConnected = false;
	
	
	
	private static final int REQUEST_CONNECT_DEVICE = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mView = new MainView(getApplicationContext(), this);
		mView.setFocusable(true);
		//setContentView(R.layout.main_layout);
		setContentView(mView);
		
//		mConnectionButton = (Button)findViewById(R.id.connection_button);
//		mConnectionButton.setOnClickListener(myConnectionHandler);
//		
//		mForwardButton = (Button)findViewById(R.id.forward_button);
//		mForwardButton.setOnTouchListener(myMoveHandler);
//		mBackButton = (Button)findViewById(R.id.back_button);
//		mBackButton.setOnTouchListener(myMoveHandler);
//		mLeftButton = (Button)findViewById(R.id.left_button);
//		mLeftButton.setOnTouchListener(myMoveHandler);
//		mRightButton = (Button)findViewById(R.id.right_button);
//		mRightButton.setOnTouchListener(myMoveHandler);
		
		MAC_ADRESS = "00:16:53:0C:64:41";
		
	}
	
    @Override
    public boolean onTouchEvent(MotionEvent event) {
	    switch(event.getAction()){
	    case MotionEvent.ACTION_DOWN:
	         break;
	    case MotionEvent.ACTION_MOVE:
	    	mCoordinateX = (int)event.getX();
	    	mCoordinateY = (int)event.getY();
	         break;
	    case MotionEvent.ACTION_UP:
	         break;
	    }
	    
        return super.onTouchEvent(event);
    }
	
	View.OnClickListener myConnectionHandler = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ConnectToNXT();
		}
	};
	
	View.OnTouchListener myMoveHandler = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			int aSpeed = 0;
			int bSpeed = 0;
			
			switch (v.getId()) {
			case R.id.forward_button:
				aSpeed = 40;
				bSpeed = 40;
				break;
			case R.id.back_button:
				aSpeed = -30;
				bSpeed = -30;
				break;
			case R.id.left_button:
				aSpeed = 5;
				bSpeed = 40;
				break;
			case R.id.right_button:
				aSpeed = 40;
				bSpeed = 5;
				break;
			}
			
		    switch(event.getAction()){
		    case MotionEvent.ACTION_DOWN:
		     move(aSpeed, bSpeed);
		         break;
		    case MotionEvent.ACTION_MOVE:
		         //do something on MOVE
		         break;
		    case MotionEvent.ACTION_UP:
		     //do something on UP   
		    move(0, 0);
		         break;
		    }
		    return true;
		}
		
		
	};
	
	public void move(int aSpeed, int bSpeed) {
		sendNXTmessage(myNXTCommunicator.NO_DELAY, myNXTCommunicator.MOTOR_A, aSpeed, 0);
		sendNXTmessage(myNXTCommunicator.NO_DELAY, myNXTCommunicator.MOTOR_B, bSpeed, 0);
	}
	
	public void grab(int speed) {
		sendNXTmessage(myNXTCommunicator.NO_DELAY, myNXTCommunicator.MOTOR_C, speed, 0);
	}
	
	public void ConnectToNXT() {
		startNXTCommunicator(MAC_ADRESS);
	}
	
	private void startNXTCommunicator(String MAC_ADRESS) {
		connectingProgressDialog = ProgressDialog.show(this, "", "Connecting, wait till awesome", true);
		
        if (myNXTCommunicator != null) {
            try {
                myNXTCommunicator.destroyNXTconnection();
            }
            catch (IOException e) { }
        }
        createNXTCommunicator();
        myNXTCommunicator.setMACAddress(MAC_ADRESS);
        myNXTCommunicator.start();
	}
	
	private void createNXTCommunicator() {
		myNXTCommunicator = new NXTCommunicator(myHandler, BluetoothAdapter.getDefaultAdapter());
		mainHandler = myNXTCommunicator.getHandler();
	}
	
    void sendNXTmessage(int delay, int message, int value1, int value2) {
        Bundle myBundle = new Bundle();
        myBundle.putInt("message", message);
        myBundle.putInt("value1", value1);
        myBundle.putInt("value2", value2);
        Message myMessage = myHandler.obtainMessage();
        myMessage.setData(myBundle);

        if (delay == 0)
            mainHandler.sendMessage(myMessage);

        else
            mainHandler.sendMessageDelayed(myMessage, delay);
    }
	
	final Handler myHandler = new Handler() {
		public void handleMessage(Message myMessage) {
			switch (myMessage.getData().getInt("message")) {
				case NXTCommunicator.CONNECTED:
                    connectingProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "IT'S WORKIN!!!!", Toast.LENGTH_SHORT).show();
                    sendNXTmessage(NXTCommunicator.NO_DELAY, NXTCommunicator.GET_FIRMWARE_VERSION, 0, 0);
                    pConnected = true;
					break;
			}
		}
	};
	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		switch(requestCode) {
//		case REQUEST_CONNECT_DEVICE: 
//			if(requestCode == Activity.RESULT_OK) {
//				Toast.makeText(getApplicationContext(), "ACtivityREsult", Toast.LENGTH_SHORT).show();
//			}
//			break;
//		}
//	}
	

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
