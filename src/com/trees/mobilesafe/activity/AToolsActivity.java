package com.trees.mobilesafe.activity;

import com.trees.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AToolsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	// 点击事件-- 进入号码归属地查询的页面
	
	public void numberAddressQuery(View v){
		Intent intent = new Intent(this, NumberAddressQueryActivity.class);
		startActivity(intent);
	}
}
