package com.main.android;

public class NXTmessage {

// the folowing constants were taken from the leJOS project (http://www.lejos.org) 
    
    // Command types constants. Indicates type of packet being sent or received.
    public static byte DIRECT_COMMAND_REPLY = 0x00;
    public static byte SYSTEM_COMMAND_REPLY = 0x01;
    public static byte REPLY_COMMAND = 0x02;
    public static byte DIRECT_COMMAND_NOREPLY = (byte)0x80; // Avoids ~100ms latency
    public static byte SYSTEM_COMMAND_NOREPLY = (byte)0x81; // Avoids ~100ms latency

    // Direct Commands
    public static final byte START_PROGRAM = 0x00;
    public static final byte STOP_PROGRAM = 0x01;
    public static final byte PLAY_SOUND_FILE = 0x02;
    public static final byte PLAY_TONE = 0x03;
    public static final byte SET_OUTPUT_STATE = 0x04;
    public static final byte SET_INPUT_MODE = 0x05;
    public static final byte GET_OUTPUT_STATE = 0x06;
    public static final byte GET_INPUT_VALUES = 0x07;
    public static final byte RESET_SCALED_INPUT_VALUE = 0x08;
    public static final byte MESSAGE_WRITE = 0x09;
    public static final byte RESET_MOTOR_POSITION = 0x0A;   
    public static final byte GET_BATTERY_LEVEL = 0x0B;
    public static final byte STOP_SOUND_PLAYBACK = 0x0C;
    public static final byte KEEP_ALIVE = 0x0D;
    public static final byte LS_GET_STATUS = 0x0E;
    public static final byte LS_WRITE = 0x0F;
    public static final byte LS_READ = 0x10;
    public static final byte GET_CURRENT_PROGRAM_NAME = 0x11;
    public static final byte MESSAGE_READ = 0x13;
    
    // NXJ additions
    public static byte NXJ_DISCONNECT = 0x20; 
    public static byte NXJ_DEFRAG = 0x21;
    
    // MINDdroidConnector additions
    public static final byte SAY_TEXT = 0x30;
    public static final byte VIBRATE_PHONE = 0x31;
    public static final byte ACTION_BUTTON = 0x32;
    
    // System Commands:
    public static final byte OPEN_READ = (byte)0x80;
    public static final byte OPEN_WRITE = (byte)0x81;
    public static final byte READ = (byte)0x82;
    public static final byte WRITE = (byte)0x83;
    public static final byte CLOSE = (byte)0x84;
    public static final byte DELETE = (byte)0x85;        
    public static final byte FIND_FIRST = (byte)0x86;
    public static final byte FIND_NEXT = (byte)0x87;
    public static final byte GET_FIRMWARE_VERSION = (byte)0x88;
    public static final byte OPEN_WRITE_LINEAR = (byte)0x89;
    public static final byte OPEN_READ_LINEAR = (byte)0x8A;
    public static final byte OPEN_WRITE_DATA = (byte)0x8B;
    public static final byte OPEN_APPEND_DATA = (byte)0x8C;
    public static final byte BOOT = (byte)0x97;
    public static final byte SET_BRICK_NAME = (byte)0x98;
    public static final byte GET_DEVICE_INFO = (byte)0x9B;
    public static final byte DELETE_USER_FLASH = (byte)0xA0;
    public static final byte POLL_LENGTH = (byte)0xA1;
    public static final byte POLL = (byte)0xA2;
    
    public static final byte NXJ_FIND_FIRST = (byte)0xB6;
    public static final byte NXJ_FIND_NEXT = (byte)0xB7;
    public static final byte NXJ_PACKET_MODE = (byte)0xff;
    
    // Error codes    
    public static final byte MAILBOX_EMPTY = (byte)0x40;
    public static final byte FILE_NOT_FOUND = (byte)0x86;
    public static final byte INSUFFICIENT_MEMORY = (byte) 0xFB;
    public static final byte DIRECTORY_FULL = (byte) 0xFC;
    public static final byte UNDEFINED_ERROR = (byte) 0x8A;
    public static final byte NOT_IMPLEMENTED = (byte) 0xFD;

    // Firmware codes
    public static byte[] FIRMWARE_VERSION_LEJOSMINDDROID = { 0x6c, 0x4d, 0x49, 0x64 };
    
    public static byte[] getActionMessage(int actionNr) {
        byte[] message = new byte[3];

        message[0] = DIRECT_COMMAND_NOREPLY;
        message[1] = ACTION_BUTTON;
        message[2] = (byte) actionNr;
        return message;
    }   

    public static byte[] getMotorMessage(int motor, int speed) {
        byte[] message = new byte[12];

        message[0] = DIRECT_COMMAND_NOREPLY;
        message[1] = SET_OUTPUT_STATE;
        message[2] = (byte) motor;

        if (speed == 0) {
            message[3] = 0;
            message[4] = 0;
            message[5] = 0;
            message[6] = 0;
            message[7] = 0;

        } else {
            message[3] = (byte) speed;
            message[4] = 0x03;
            message[5] = 0x01;
            message[6] = 0x00;
            message[7] = 0x20;
        }
        message[8] = 0;
        message[9] = 0;
        message[10] = 0;
        message[11] = 0;

        return message;

    }
}
