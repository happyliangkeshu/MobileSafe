package com.trees.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.trees.mobilesafe.R;
import com.trees.mobilesafe.service.AddressService;
import com.trees.mobilesafe.utils.ServiceStatusUtils;
import com.trees.mobilesafe.view.SettingClickView;
import com.trees.mobilesafe.view.SettingItemView;

public class SettingActivity extends Activity {
	private String tag = "SettingActivity" ;
	private SettingItemView siv_update;
	private SettingItemView  siv_showaddress;
	private SettingClickView scv_changeposition;
	private SettingClickView scv_changebg;
	
	private SharedPreferences sp;
	private Intent addressIntent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		setContentView(R.layout.activity_setting);
		siv_update = (SettingItemView) findViewById(R.id.siv);

		
		boolean update = sp.getBoolean("update", false);
		
		
		siv_update.setChecked(update);
		siv_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if(siv_update.isChecked()){
					siv_update.setChecked(false);
//					siv_update.setDescription("当前自动升级已经关闭");
					editor.putBoolean("update", false);
				}
				else{
					siv_update.setChecked(true);
//					siv_update.setDescription("当前自动升级已经开启");
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
		siv_showaddress = (SettingItemView) findViewById(R.id.siv_showaddress);
		
		boolean addressServie = ServiceStatusUtils.isRuningService(
				this, "com.trees.mobilesafe.service.AddressService");
		siv_showaddress.setChecked(addressServie);
		addressIntent = new Intent(this, AddressService.class);
		Log.e(tag   ,   "---- addressServie " + addressServie );
		siv_showaddress.setOnClickListener(new OnClickListener() {
		@Override
			public void onClick(View v) {
				
				if(siv_showaddress.isChecked()){
					siv_showaddress.setChecked(false);
					stopService(addressIntent);
					Log.e(tag   ,   "---- stopService" );
				}else{
					siv_showaddress.setChecked(true);
					startService(addressIntent);
					Log.e(tag ,   "---- startService" );
				}
				
			}
		});
		// 设置归属地显示框风格
		scv_changebg = (SettingClickView) findViewById(R.id.scv_changebg);
		final String items[] = {"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
		
		scv_changebg.setDescription(items[sp.getInt("which", 0)]);
		scv_changebg.setTitle("归属地提示风格");
		scv_changebg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("归属地提示框风格");
				builder.setSingleChoiceItems(items, sp.getInt("which", 0), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 保存起来
						Editor editor = sp.edit();
						editor.putInt("which", which);
						editor.commit();
						// 设置一下
						scv_changebg.setDescription(items[which]);
						dialog.dismiss();
					}

				});
				builder.setNegativeButton("cancel", null);
				builder.show();
			}
		});
		scv_changeposition = (SettingClickView) findViewById(R.id.scv_changeposition);
		scv_changeposition.setTitle("归属地提示框位置");
		scv_changeposition.setDescription("设置归属地提示框显示位置");
		scv_changeposition.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// 跳转到拖动的Activity里面
				Intent intent = new Intent(SettingActivity.this, DragViewActivity.class);
				startActivity(intent);
				
			}
		});
	}
}
