package com.trees.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;
/*
 * ���������㲥
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	private SharedPreferences sp;
	private TelephonyManager tm;
	@Override
	public void onReceive(Context context, Intent intent) {
//		1.�õ�֮ǰ��sim����Ϣ 
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		if(sp.getBoolean("protecting", false)){
			String save_sim = sp.getString("sim", "");
			//    2.�õ���ǰsim����Ϣ
			tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String currentSim = tm.getSimSerialNumber();
			//   3.�Ƚ�sim����Ϣ�Ƿ�һ��
			if(!save_sim.equals(currentSim)){
				System.out.println("sim�������");
				Toast.makeText(context, "sim����һ��", 1).show();
				//	4.�����һ�¾ͷ��Ͷ��Ÿ���ȫ����
				SmsManager.getDefault().sendTextMessage(
						sp.getString("safenumber", ""), 
						null, 
						"sim change from heima 46",
						null, null);
				
			}
		}



	}

}
