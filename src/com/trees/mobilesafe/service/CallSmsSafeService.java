package com.trees.mobilesafe.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.trees.mobilesafe.db.dao.BlackNumberDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
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
						endCall();// 电话被挂断了，生成通话记录是异步的
//						deleteCallLog(incomingNumber);
						// 观察数据变化后，再去删除
						Uri uri = Uri.parse("content://call_log/calls");
						getContentResolver().registerContentObserver(uri, true, new MyContentObserver(new Handler(), incomingNumber));
					}
				}
				
				break;

			default:
				break;
			}
			
		}
	}
	
	// 内容观察者 
	private class MyContentObserver extends ContentObserver{

		private  String incomingNumber;
		public MyContentObserver(Handler handler, String incomingNumber) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			// 
			deleteCallLog(incomingNumber);
			// 取消注册内容观察者
			getContentResolver().unregisterContentObserver(this);
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
	
	// 删除呼叫记录
	public void deleteCallLog(String incomingNumber) {
		ContentResolver cr = getContentResolver();
		Uri uri = Uri.parse("content://call_log/calls");
		cr.delete(uri, "number=?", new String[]{incomingNumber});
		
	}


	public void endCall() {
//		IBinder b = ServiceManager.getService()
		//用反射得到ServiceManager的实例
//		1.得到字节码
		try {
			Class clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
//			2.得到对应的方法getservice
			Method method = clazz.getMethod("getService", String.class);
//			3.得到实例  静态方法 不需要实例
//			4.执行这个方法
			IBinder b = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
//			以上四步就是反射的过程
//			5.拷贝.aidl文件	
//			6.生成Java代码
			ITelephony service = ITelephony.Stub.asInterface(b);
//			7.执行Java中的endCall
			service.endCall();
			// 接下来要删除呼叫记录
		} catch (Exception e) {
			
			e.printStackTrace();
		}

		
	}

}
