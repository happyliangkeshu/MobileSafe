package com.trees.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceStatusUtils {

	/**
	 * У��ĳ�������Ƿ�������
	 * @param 
	 * 
	 */
	public static boolean isRuningService(Context context, String serviceName) {
		// ActivityManager 
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> serviceInfos =   am.getRunningServices(100);
		for(RunningServiceInfo rs : serviceInfos){
			String name = rs.service.getClassName();
			if(serviceName.equals(name)){
				return true;
			}
		}
		return false;
	}

}
