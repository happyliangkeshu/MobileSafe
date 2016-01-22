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


    // ����¼� һ������
    public void lockscreen(View v){
    	ComponentName who = new ComponentName(this, MyAdmin.class);
    	if(dpm.isAdminActive(who)){
    		dpm.lockNow();
    		// ��������
    		dpm.resetPassword("1", 0);
    		// 
//    		dpm.wipeData(0);// ���ֻ��ָ��ɳ�������- Զ��ɾ������
    		// ���sd��������
//    		dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
    	}
    	else{
    		openAdmin(v);
    	}
    	
    }
    public void openAdmin(View v){
    	// ������ͼ�������� ����豸����Ա
    	Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
    	// ��������
    	ComponentName mDeviceAdminSample = new ComponentName(this, MyAdmin.class);
    	
    	intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
    	// �����˵��
    	intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, 
    			"�㼤���豸����ԱȨ�ޣ�����һ�����������Ӱ�ȫ...");
    	startActivity(intent);
    }
    // ж�����
    public void uninstall(View v){
    	// 1. ��Ȩ�޸ɵ���
    	ComponentName who = new ComponentName(this, MyAdmin.class);
    	dpm.removeActiveAdmin(who);
    	// 2. ������ͨӦ�øɵ�
    	Intent intent = new Intent();
    	intent.setAction("android.intent.action.DELETE");
    	intent.addCategory("android.intent.category.DEFAULT");
    	intent.setData(Uri.parse("package:" + getPackageName() ));
    	startActivity(intent);
    }
    
}
