package com.trees.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceStatusUtils {

	/**
	 * 校验某个服务是否运行中
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
