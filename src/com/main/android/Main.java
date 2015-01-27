package com.main.android;

import java.io.IOException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;


public class Main extends Activity
{
	private NXTCommunicator myNXTCommunicator = null;
	private Handler mainHandler;
	private ProgressDialog connectingProgressDialog;
	private static final String MAC_ADRESS = "00:16:53:0C:64:41";
	private MainView mView;
	public boolean pConnected = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mView = new MainView(getApplicationContext(), this);
		mView.setFocusable(true);
		setContentView(mView);
	}
	
	View.OnClickListener myConnectionHandler = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ConnectToNXT();
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
