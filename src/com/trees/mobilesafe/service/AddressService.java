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
			myToast(address);
		}
	}
	/**
	 * 自定义土司
	 * 
	 * @param address
	 */
	public void myToast(String address) {
		view = View.inflate(this, R.layout.show_address, null);
		TextView textview = (TextView) view.findViewById(R.id.tv_address);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
//				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
//				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
//					// 双击居中了。。。
//					params.x = wm.getDefaultDisplay().getWidth()/2-view.getWidth()/2;
//					wm.updateViewLayout(view, params);
//					Editor editor = sp.edit();
//					editor.putInt("lastx", params.x);
//					editor.commit();
//				}
			}
		});

		// 给view对象设置一个触摸的监听器
		view.setOnTouchListener(new OnTouchListener() {
			// 定义手指的初始化位置
			int startX;
			int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:// 手指按下屏幕
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					Log.i(TAG, "手指摸到控件");
					break;
				case MotionEvent.ACTION_MOVE:// 手指在屏幕上移动
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					int dx = newX - startX;
					int dy = newY - startY;
					Log.i(TAG, "手指在控件上移动");
					params.x += dx;
					params.y += dy;
					// 考虑边界问题
					if (params.x < 0) {
						params.x = 0;
					}
					if (params.y < 0) {
						params.y = 0;
					}
					if (params.x > (wm.getDefaultDisplay().getWidth() - view
							.getWidth())) {
						params.x = (wm.getDefaultDisplay().getWidth() - view
								.getWidth());
					}
					if (params.y > (wm.getDefaultDisplay().getHeight() - view
							.getHeight())) {
						params.y = (wm.getDefaultDisplay().getHeight() - view
								.getHeight());
					}
					wm.updateViewLayout(view, params);
					// 重新初始化手指的开始结束位置。
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:// 手指离开屏幕一瞬间
					// 记录控件距离屏幕左上角的坐标
					Log.i(TAG, "手指离开控件");
					Editor editor = sp.edit();
					editor.putInt("lastx", params.x);
					editor.putInt("lasty", params.y);
					editor.commit();
					break;
				}
				return false;// 事件处理完毕了。不要让父控件 父布局响应触摸事件了。
			}
		});

		// "半透明","活力橙","卫士蓝","金属灰","苹果绿"
		int[] ids = { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		
		
		
		view.setBackgroundResource(ids[sp.getInt("which", 0)]);
		textview.setText(address);
		// 窗体的参数就设置好了
		params = new WindowManager.LayoutParams();

		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		// 与窗体左上角对其
		params.gravity = Gravity.TOP + Gravity.LEFT;
		// 指定窗体距离左边100 上边100个像素
		params.x = sp.getInt("lastx", 0);
		params.y = sp.getInt("lasty", 0);
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		// android系统里面具有电话优先级的一种窗体类型，记得添加权限。
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		wm.addView(view, params);

	}

	
	private class MyPhoneStateListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {	
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String address = NumberAddressQueryDao.getAddress(incomingNumber);
				myToast(address);
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
	@Override
	public void onDestroy() {
		
		super.onDestroy();
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		// 取消注册监听去电
		unregisterReceiver(receiver);
		receiver = null;
	}
}
