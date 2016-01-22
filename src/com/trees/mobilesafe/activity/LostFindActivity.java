package com.trees.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.trees.mobilesafe.R;

public class LostFindActivity extends Activity {
    private static final String TAG = "LostFindActivity";
	private SharedPreferences sp;
	private TextView tv_safenumber;
	private ImageView iv_status;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean configed = sp.getBoolean("configed", false);
		
		// 判断是否做过设置向导
		if(configed){
			// 加载手机防盗页面
			setContentView(R.layout.activity_lost_find);
			tv_safenumber = (TextView) findViewById(R.id.tv_safenumber);
			iv_status = (ImageView) findViewById(R.id.iv_status);
			tv_safenumber.setText(sp.getString("safenumber",""));
			boolean protecting = sp.getBoolean("protecting", false);
			if(protecting){
				iv_status.setImageResource(R.drawable.lock);
			}else{
				iv_status.setImageResource(R.drawable.unlock);
			}
		}
		else{// 进入设置向导页面
				enterSetting();
		}
	}
	
	public void reEnterSetting(View v){
		enterSetting();
	}

	private void enterSetting() {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		//
		finish();
	}
}
