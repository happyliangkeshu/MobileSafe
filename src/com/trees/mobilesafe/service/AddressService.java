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
	// 电话服务
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
		// 注册监听去电，广播接收者的代码注册
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);
		
	}
	// 服务里边的内部类，用来监听去电
	private class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent arg1) {
			String number = getResultData();
			String address = NumberAddressQueryDao.getAddress(number);
			showMyToast(address);
		}
	}
	/**
	 * 自定义土司
	 * 
	 * @param address
	 */
	public void myToast(String address) {
		view = View.inflate(this, R.layout.show_address, null);
		TextView tv_address = (TextView) view.findViewById(R.id.tv_address);
		// "半透明","活力橙","卫士蓝","金属灰","苹果绿"
		int[] ids = { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };	
		view.setBackgroundResource(ids[sp.getInt("which", 0)]);
		tv_address.setText(address);

		// 窗体的参数就设置好了
		params = new WindowManager.LayoutParams();
		// 与窗体左上角对其
		params.gravity = Gravity.TOP + Gravity.LEFT;
		// 指定窗体距离左边100 上边100个像素
		params.x = sp.getInt("lastX", 0);
		params.y = sp.getInt("lastY", 0);

		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		// android系统里面具有电话优先级的一种窗体类型，记得添加权限。
		params.type = WindowManager.LayoutParams.TYPE_TOAST;
		wm.addView(view, params);
//		Log.e(tag, "view4 到这里了吗");
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
				// 把这个View移除
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
		// "半透明","活力橙","卫士蓝","金属灰","苹果绿"
		int[] bgs = { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		// 直接利用窗体管理器 添加一个view对象到整个手机系统的窗体上
		view = View.inflate(this, R.layout.show_address, null);
		view.setOnTouchListener(new OnTouchListener() {
			int startX;
			int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// 1. 记录第一次按下
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					Log.e(tag, "按下");
					break;
				case MotionEvent.ACTION_MOVE:
					Log.e(tag, "移动");
					// 2. 来到新的坐标
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					// 3. 计算偏移量
					int dx = newX - startX;
					int dy = newY - startY;
					
					// 4. 根据偏移量，更新控件的信息
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
					//重新记录开始坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					Log.e(tag, "离开");
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
		// 窗体的类型。
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
		// 取消注册监听去电
		unregisterReceiver(receiver);
		receiver = null;
	}
}
