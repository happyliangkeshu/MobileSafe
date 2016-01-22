package com.trees.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.trees.mobilesafe.R;

public class Setup4Activity extends BaseSetupActivity  {

	private static final String TAG = "Setup4Activity";
	private SharedPreferences sp;
	private CheckBox cb_protecting;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		setContentView(R.layout.activity_setup4);
		cb_protecting = (CheckBox) findViewById(R.id.cb_protecting);
		boolean protecting = sp.getBoolean("protecting", false);
		if(protecting){
			cb_protecting.setText("当前状态：手机防盗已经开启");
		}
		else{
			cb_protecting.setText("当前状态：手机防盗已经开启");
		}
		cb_protecting.setChecked(protecting);
		cb_protecting.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = sp.edit();
				editor.putBoolean("protecting", isChecked);
				editor.commit();
				if(isChecked){
					cb_protecting.setText("当前状态：手机防盗已经开启");
				}
				else{
					cb_protecting.setText("当前状态：手机防盗已经关闭");
				}
				
			}
		});
	}
	@Override
	public void showNext() {
		
		Editor editor = sp.edit();
		editor.putBoolean("configed", true);
		editor.commit();
		
		Intent intent = new Intent(this, LostFindActivity.class);
		startActivity(intent);
		//
		finish();
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
		
	}
	@Override
	public void showPrev() {
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		//
		finish();
		overridePendingTransition(R.anim.tran_prev_in, R.anim.tran_prev_out);
		
	}
}
