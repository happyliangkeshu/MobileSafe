package com.trees.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.trees.mobilesafe.R;
import com.trees.mobilesafe.db.dao.NumberAddressQueryDao;

public class AddressService extends Service {
	private  MyPhoneStateListener listener;
	protected static final String TAG = "AddressService";
	// �绰����
	private TelephonyManager tm;
	private OutCallReceiver receiver;
	
	
	private WindowManager wm;
	private View view;
	
	private WindowManager.LayoutParams params;
	private SharedPreferences sp;
	private String tag = "AddressService";
	@Override
	public IBinder onBind(Intent arg0) {
		
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		sp = getSharedPreferences("config", MODE_PRIVATE);
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		// ע�����ȥ�磬�㲥�����ߵĴ���ע��
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);
		
	}
	// ������ߵ��ڲ��࣬��������ȥ��
	private class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent arg1) {
			String number = getResultData();
			String address = NumberAddressQueryDao.getAddress(number);
			showMyToast(address);
		}
	}
	/**
	 * �Զ�����˾
	 * 
	 * @param address
	 */
	public void myToast(String address) {
		view = View.inflate(this, R.layout.show_address, null);
		TextView tv_address = (TextView) view.findViewById(R.id.tv_address);
		// "��͸��","������","��ʿ��","������","ƻ����"
		int[] ids = { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };	
		view.setBackgroundResource(ids[sp.getInt("which", 0)]);
		tv_address.setText(address);

		// ����Ĳ��������ú���
		params = new WindowManager.LayoutParams();
		// �봰�����ϽǶ���
		params.gravity = Gravity.TOP + Gravity.LEFT;
		// ָ������������100 �ϱ�100������
		params.x = sp.getInt("lastX", 0);
		params.y = sp.getInt("lastY", 0);

		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		// androidϵͳ������е绰���ȼ���һ�ִ������ͣ��ǵ����Ȩ�ޡ�
		params.type = WindowManager.LayoutParams.TYPE_TOAST;
		wm.addView(view, params);
//		Log.e(tag, "view4 ����������");
	}

	
	private class MyPhoneStateListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {	
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String address = NumberAddressQueryDao.getAddress(incomingNumber);
				showMyToast(address);
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				// �����View�Ƴ�
				if (view != null) {
					wm.removeView(view);
				}
				break;
			default:
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
		
	}
	
	public void showMyToast(String address) {
		int which = sp.getInt("which", 0);
		// "��͸��","������","��ʿ��","������","ƻ����"
		int[] bgs = { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		// ֱ�����ô�������� ���һ��view���������ֻ�ϵͳ�Ĵ�����
		view = View.inflate(this, R.layout.show_address, null);
		view.setOnTouchListener(new OnTouchListener() {
			int startX;
			int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// 1. ��¼��һ�ΰ���
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					Log.e(tag, "����");
					break;
				case MotionEvent.ACTION_MOVE:
					Log.e(tag, "�ƶ�");
					// 2. �����µ�����
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					// 3. ����ƫ����
					int dx = newX - startX;
					int dy = newY - startY;
					
					// 4. ����ƫ���������¿ؼ�����Ϣ
					params.x += dx;
					params.y += dy;
					if(params.x < 0){
						params.x = 0;
					}
					if(params.y < 0){
						params.y = 0;
					}
					if(params.x > wm.getDefaultDisplay().getWidth()-view.getWidth()){
						params.x  = wm.getDefaultDisplay().getWidth()-view.getWidth();
					}
					if(params.y > wm.getDefaultDisplay().getHeight()-view.getHeight()){
						params.y  = wm.getDefaultDisplay().getHeight()-view.getHeight();
					}
					
					wm.updateViewLayout(view, params);
					//���¼�¼��ʼ����
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					Log.e(tag, "�뿪");
					Editor editor = sp.edit();
					editor.putInt("lastX", params.x);
					editor.putInt("lastY", params.y);
					editor.commit();
					break;
				}
				return true;
			}
		});
		view.setBackgroundResource(bgs[which]);
		TextView tv = (TextView) view.findViewById(R.id.tv_address);
		tv.setText(address);
		Log.e(tag, "111111111111");
		params = new WindowManager.LayoutParams();
		params.gravity = Gravity.TOP + Gravity.LEFT;
		params.x = sp.getInt("lastX", 0);
		params.y = sp.getInt("lastY", 0);
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags =WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
//				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
				
		params.format = PixelFormat.TRANSLUCENT;
		// ��������͡�
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		Log.e(tag, "2222222222");
		wm.addView(view, params);
		Log.e(tag, "3333333333");
		/*new Thread(){
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				wm.removeView(view);
			};
		}.start();*/
	}

	@Override
	public void onDestroy() {
		
//		super.onDestroy();
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		// ȡ��ע�����ȥ��
		unregisterReceiver(receiver);
		receiver = null;
	}
}
