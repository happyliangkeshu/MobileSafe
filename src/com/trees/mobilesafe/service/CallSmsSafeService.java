package com.trees.mobilesafe.service;

import com.trees.mobilesafe.db.dao.BlackNumberDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

public class CallSmsSafeService extends Service {

	private InnerSMSReceiver receiver;
	private BlackNumberDao dao;
	private TelephonyManager tm;
	private  MyPhoneStateListener listener;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// 把广播放在服务中
	// 广播接收者用来拦截短信
	private class InnerSMSReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("代码注册的广播");
			// 代码注册的广播要快于功能清单文件
			Object pdus[] = (Object[]) intent.getExtras().get("pdus");
			for(Object pdu : pdus){
				SmsMessage sms = SmsMessage.createFromPdu((byte[])pdu);
				String sender = sms.getOriginatingAddress();
				if(dao.query(sender)){ // 按照号码拦截
					// 要拦截的电话号码
					String mode = dao.queryMode(sender);
					if("1".equals(mode) || "2".equals(mode)){
						System.out.println("拦截到一条短信");
						abortBroadcast();
					}
				}
				
				
				String body = sms.getMessageBody();
				// 按照内容拦截
				if(body.contains("fapiao")){
					System.out.println("拦截到一条广告短信");
					abortBroadcast();
				}
				
			}
		}
		
	}
	@Override
	public void onCreate() {
		
		super.onCreate();
		// 注册监听短信
		dao = new BlackNumberDao(this);
		receiver = new InnerSMSReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver, filter);
		
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		
	}
	
	private class MyPhoneStateListener extends PhoneStateListener{
		
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				// 电话挂断，就实现电话拦截
				if(dao.query(incomingNumber)){
					String mode = dao.queryMode(incomingNumber);
					if("0".equals(mode) || "2".equals(mode)){
						endCall();
						
					}
				}
				
				break;

			default:
				break;
			}
			
		}
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 取消注册监听短信
		unregisterReceiver(receiver);
		receiver = null;
		
		//取消监听电话的注册
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		
	}
	
	
	public void endCall() {
		
		
	}

}
