package com.trees.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;
/*
 * 监听开机广播
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	private SharedPreferences sp;
	private TelephonyManager tm;
	@Override
	public void onReceive(Context context, Intent intent) {
//		1.得到之前的sim卡信息 
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		if(sp.getBoolean("protecting", false)){
			String save_sim = sp.getString("sim", "");
			//    2.得到当前sim卡信息
			tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String currentSim = tm.getSimSerialNumber();
			//   3.比较sim卡信息是否一致
			if(!save_sim.equals(currentSim)){
				System.out.println("sim卡被变更");
				Toast.makeText(context, "sim卡不一致", 1).show();
				//	4.如果不一致就发送短信给安全号码
				SmsManager.getDefault().sendTextMessage(
						sp.getString("safenumber", ""), 
						null, 
						"sim change from heima 46",
						null, null);
				
			}
		}



	}

}
