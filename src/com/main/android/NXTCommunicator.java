package com.main.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class NXTCommunicator extends Thread{
	// Special oui just for LEGO
	//public static final String OUI_LEGO = "00:16:53";
	private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	public static final int GET_FIRMWARE_VERSION = 70;
	public static final int CONNECTED = 10001;
	public static final int STATE_SENDERROR = 1005;
	
	public static final int MOTOR_A = 0;
	public static final int MOTOR_B = 1;
	public static final int MOTOR_C = 2;
	
	public static final int NO_DELAY = 0;
	public static final int DO_ACTION = 52;  
	
    private String mMACaddress;
    private Handler uiHandler;
    private BluetoothAdapter btAdapter;
    private BluetoothSocket nxtBTsocket = null;
    private OutputStream nxtOutputStream = null;
    private InputStream nxtInputStream = null;
    private boolean connected = false;
    
    public NXTCommunicator(Handler uiHandler, BluetoothAdapter btAdapter) {
    	this.uiHandler = uiHandler;
        this.btAdapter = btAdapter;
    }
    
    public void run() {
        try {        
            createNXTconnection();
        }
        catch (IOException e) { }
    }
	
    private void createNXTconnection() throws IOException{
    	BluetoothSocket nxtBTSocketTemporary;
        BluetoothDevice nxtDevice = null;
        nxtDevice = btAdapter.getRemoteDevice(mMACaddress);
        
        nxtBTSocketTemporary = nxtDevice.createRfcommSocketToServiceRecord(SERIAL_PORT_SERVICE_CLASS_UUID);
        try {
            nxtBTSocketTemporary.connect();
            connected = true;
        } catch (IOException e) { 
        	Log.w("AAAAAAAA", "nope");
        }
        
        nxtBTsocket = nxtBTSocketTemporary;
        nxtInputStream = nxtBTsocket.getInputStream();
        nxtOutputStream = nxtBTsocket.getOutputStream();
        
        if (uiHandler != null)
            sendState(CONNECTED);
    }
    
    public void destroyNXTconnection() throws IOException {

    }
    
   public void setMACAddress(String mMACaddress) {
        this.mMACaddress = mMACaddress;
    }
   
   final Handler myHandler = new Handler() {
	   public void handleMessage(Message myMessage) {
		   int message;
		   switch(message = myMessage.getData().getInt("message")) {
		   case MOTOR_A:
		   case MOTOR_B:
		   case MOTOR_C:
			   changeMotorSpeed(message, myMessage.getData().getInt("value1"));
			   break;
		   case DO_ACTION:
			   doAction(myMessage.getData().getInt("value1"));
			   break;
		   }
	   }
   };
   
   private void sendState(int message) {
       Bundle myBundle = new Bundle();
       myBundle.putInt("message", message);
       sendBundle(myBundle);
   }
   
   private void sendBundle(Bundle myBundle) {
       Message myMessage = myHandler.obtainMessage();
       myMessage.setData(myBundle);
       uiHandler.sendMessage(myMessage);
   }
   
   private void changeMotorSpeed(int motor, int speed) {
       if (speed > 100)
           speed = 100;

       else if (speed < -100)
           speed = -100;

       byte[] message = NXTmessage.getMotorMessage(motor, speed);
       sendMessageAndState(message);
   }
   
   private void doAction(int actionNr) {
       byte[] message = NXTmessage.getActionMessage(actionNr);
       sendMessageAndState(message);
   }
   
   private void sendMessageAndState(byte[] message) {
       if (nxtOutputStream == null)
           return;

       try {
           sendMessage(message);
       }
       catch (IOException e) {
           sendState(STATE_SENDERROR);
       }
   }
   
   public void sendMessage(byte[] message) throws IOException {
       if (nxtOutputStream == null)
           throw new IOException();

       int messageLength = message.length;
       nxtOutputStream.write(messageLength);
       nxtOutputStream.write(messageLength >> 8);
       nxtOutputStream.write(message, 0, message.length);
   }
   
   public Handler getHandler() {
       return myHandler;
   }
}
