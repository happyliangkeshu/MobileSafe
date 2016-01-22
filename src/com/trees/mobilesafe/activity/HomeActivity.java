package com.trees.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.trees.mobilesafe.R;
import com.trees.mobilesafe.utils.MD5Utils;
/*
 * @author trees
 * 
 */

public class HomeActivity extends Activity {

	
	private GridView list_home;
	private SharedPreferences sp;
	private static final String names[] = new String[]{
		"手机防盗", "通讯卫士", "应用管理",
		"进程管理","流量统计","手机杀毒",
		"缓存清理","高级工具","设置中心"
	};
	private static final int ids[] = new int[]{
		R.drawable.safe, R.drawable.callmsgsafe,R.drawable.app,
		R.drawable.taskmanager,R.drawable.netmanager,R.drawable.trojan,
		R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings
	};
	protected static final String TAG = "HomeActivity";
	private AlertDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		setContentView(R.layout.activity_home);
		list_home = (GridView) findViewById(R.id.list_home);
		// 设置适配器
		list_home.setAdapter(new HomeAdapter());
		list_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent;
				switch (position) {
				case 0: // 进入手机防盗
					showLostFindDialog();
					break;
				case 7 :// 进入高级工具
					intent = new Intent(HomeActivity.this, AToolsActivity.class);
					startActivity(intent);
					break;
				case 8: // 进入设置中心
					intent = new Intent(HomeActivity.this, SettingActivity.class);
					startActivity(intent);
					break;

				default:
					break;
				}
				
			}
		});
		
	}
//	根据当前情况，弹出不同对话框
	
	protected void showLostFindDialog() {
		// 判断是否设置密码，如果没有设置，就弹出设置对话框
//		如果设置，就弹出输入对话框
		
		if(isSetupPwd()){
			showEnterDialog();
		}
		else{
			showSetupPwdDialog();
			
		}
		
	}
	
	private void showSetupPwdDialog() {
		// 设置密码对话框
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		View view = View.inflate(HomeActivity.this, R.layout.dialog_setuppwd, null);
		final EditText et_password = (EditText) view.findViewById(R.id.et_password);
		final EditText et_password_confirm = (EditText) view.findViewById(R.id.et_password_confirm);
		Button ok = (Button) view.findViewById(R.id.ok);
		Button cancel = (Button) view.findViewById(R.id.cancel);
//		builder.setView(view);
//		dialog = builder.show();
		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				
			}
		});
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
//				1.得到两个输入框的密码
				String password = et_password.getText().toString().trim();
				String password_confirm = et_password_confirm.getText().toString().trim();
//				2.判断密码是否为空
				if(TextUtils.isEmpty(password) || TextUtils.isEmpty(password_confirm)){
					Toast.makeText(HomeActivity.this, "密码不能为空", 0).show();
					return;
				}
//				3.判断两个密码是否相同
				if(password.equals(password_confirm)){
//					4.保存密码，消掉对话框，进入手机防盗页面
					Editor editor = sp.edit();
					editor.putString("password", MD5Utils.encoder(password));
					editor.commit();
					dialog.dismiss();
					Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
					startActivity(intent);
				}else{
					Toast.makeText(HomeActivity.this, "两次输入密码不一致", 0).show();
				}
				

			}
		});
	}

	private void showEnterDialog() {
		// 弹出输入密码对话框
		// 设置密码对话框
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		View view = View.inflate(HomeActivity.this, R.layout.dialog_enter_pwd, null);
		final EditText et_password = (EditText) view.findViewById(R.id.et_password);
		Button ok = (Button) view.findViewById(R.id.ok);
		Button cancel = (Button) view.findViewById(R.id.cancel);
//		builder.setView(view);
//		dialog = builder.show();
		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				
			}
		});
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
//				1.得到两个输入框的密码
				String password = et_password.getText().toString().trim();
				String password_save = sp.getString("password", null);
//				2.判断密码是否为空
				if(TextUtils.isEmpty(password)){
					Toast.makeText(HomeActivity.this, "密码不能为空", 0).show();
					return;
				}
//				3.判断两个密码是否相同
				if(MD5Utils.encoder(password).equals(password_save)){
//					
					dialog.dismiss();
					Log.e(TAG, "密码正确，消掉对话框，进入手机防盗页面");
					Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
					startActivity(intent);
				}else{
					Toast.makeText(HomeActivity.this, "您输入的密码不正确", 0).show();
				}
				

			}
		});		
		
	}

	/*
	 * 判断是否设置了密码
	 */
	private boolean isSetupPwd(){
		String password = sp.getString("password", null);
		return !TextUtils.isEmpty(password);
	}
	private class HomeAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			
			return names.length;
		}

		@Override
		public Object getItem(int position) {
			
			return null;
		}

		@Override
		public long getItemId(int position) {
			
			return 0;
		}

		@Override
		public View getView(int position, View contentView, ViewGroup parent) {
			View view = View.inflate(HomeActivity.this, R.layout.home_item, null);
			ImageView iv_icon = (ImageView)view.findViewById(R.id.iv_icon);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			tv_name.setText(names[position]);
			iv_icon.setImageResource(ids[position]);
			return view;
		}
		
	}
}
