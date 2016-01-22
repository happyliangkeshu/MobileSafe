package com.trees.mobilesafe.activity;

import com.trees.mobilesafe.receiver.MyAdmin;

import android.R;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class LockScreenActivity extends Activity {

	private DevicePolicyManager dpm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_lockscreen);
        dpm =  (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
//        dpm.lockNow();
        lockscreen(null);
        finish();
    }


    // 点击事件 一键锁屏
    public void lockscreen(View v){
    	ComponentName who = new ComponentName(this, MyAdmin.class);
    	if(dpm.isAdminActive(who)){
    		dpm.lockNow();
    		// 设置密码
    		dpm.resetPassword("1", 0);
    		// 
//    		dpm.wipeData(0);// 让手机恢复成出厂设置- 远程删除数据
    		// 清楚sd卡的数据
//    		dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
    	}
    	else{
    		openAdmin(v);
    	}
    	
    }
    public void openAdmin(View v){
    	// 定义意图、动作、 添加设备管理员
    	Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
    	// 激活的组件
    	ComponentName mDeviceAdminSample = new ComponentName(this, MyAdmin.class);
    	
    	intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
    	// 激活的说明
    	intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, 
    			"你激活设备管理员权限，可以一键锁屏，更加安全...");
    	startActivity(intent);
    }
    // 卸载软件
    public void uninstall(View v){
    	// 1. 把权限干掉，
    	ComponentName who = new ComponentName(this, MyAdmin.class);
    	dpm.removeActiveAdmin(who);
    	// 2. 当成普通应用干掉
    	Intent intent = new Intent();
    	intent.setAction("android.intent.action.DELETE");
    	intent.addCategory("android.intent.category.DEFAULT");
    	intent.setData(Uri.parse("package:" + getPackageName() ));
    	startActivity(intent);
    }
    
}
