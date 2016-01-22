package com.trees.mobilesafe.receiver;


import com.trees.mobilesafe.R;
import com.trees.mobilesafe.activity.LockScreenActivity;
import com.trees.mobilesafe.service.GPSService;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;

public class SMSReceiver extends BroadcastReceiver {

	private SharedPreferences sp;
	private DevicePolicyManager dpm;
	@Override
	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		Object[] pdus = (Object [])intent.getExtras().get("pdus");
		for(Object pdu : pdus){
			SmsMessage sms = SmsMessage.createFromPdu( (byte[] )pdu);
			
			String sender = sms.getOriginatingAddress();
			String safenumber = sp.getString("safenumber", "");
			String body = sms.getMessageBody();
			
			if(sender.contains(safenumber)){
				if("#location".equals(body)){
					// 得到手机的GPS位置
					System.out.println("得到手机的GPS位置");
					Intent gpsServiceintent = new Intent(context, GPSService.class);
					context.startService(gpsServiceintent);
					String lastlocation = sp.getString("lastlocation", "");
					if(TextUtils.isEmpty(lastlocation)){
						SmsManager.getDefault().sendTextMessage(
								sender, null, 
								"getting location .. for heima46",
								null, null);
					}
					abortBroadcast();
				}
				else if ("#alarm#".equals(body)){
					MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
					player.setVolume(1.0f, 1.0f);
//					player.setLooping(true);
					player.start();
					System.out.println("播放报警音乐");
					abortBroadcast();
				}
				else if("#wipdata".equals(body)){
					System.out.println("");
					dpm.wipeData(0);
					abortBroadcast();
				}
				else if("#lockscreen".equals(body)){
					System.out.println("");
					ComponentName who = new ComponentName(context, MyAdmin.class);
					if(dpm.isAdminActive(who)){
						dpm.lockNow();
						dpm.resetPassword("1", 0);
					}
					else{
						Intent openAdmin = new Intent(context, LockScreenActivity.class);
						openAdmin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(openAdmin);
					}

					abortBroadcast();
					
				}
			}
		}
	}

}
