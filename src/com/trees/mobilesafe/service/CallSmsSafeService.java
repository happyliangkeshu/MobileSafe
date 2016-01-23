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
	
	// �ѹ㲥���ڷ�����
	// �㲥�������������ض���
	private class InnerSMSReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("����ע��Ĺ㲥");
			// ����ע��Ĺ㲥Ҫ���ڹ����嵥�ļ�
			Object pdus[] = (Object[]) intent.getExtras().get("pdus");
			for(Object pdu : pdus){
				SmsMessage sms = SmsMessage.createFromPdu((byte[])pdu);
				String sender = sms.getOriginatingAddress();
				if(dao.query(sender)){ // ���պ�������
					// Ҫ���صĵ绰����
					String mode = dao.queryMode(sender);
					if("1".equals(mode) || "2".equals(mode)){
						System.out.println("���ص�һ������");
						abortBroadcast();
					}
				}
				
				
				String body = sms.getMessageBody();
				// ������������
				if(body.contains("fapiao")){
					System.out.println("���ص�һ��������");
					abortBroadcast();
				}
				
			}
		}
		
	}
	@Override
	public void onCreate() {
		
		super.onCreate();
		// ע���������
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
				// �绰�Ҷϣ���ʵ�ֵ绰����
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
		// ȡ��ע���������
		unregisterReceiver(receiver);
		receiver = null;
		
		//ȡ�������绰��ע��
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		
	}
	
	
	public void endCall() {
		
		
	}

}