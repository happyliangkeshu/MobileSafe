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
		"�ֻ�����", "ͨѶ��ʿ", "Ӧ�ù���",
		"���̹���","����ͳ��","�ֻ�ɱ��",
		"��������","�߼�����","��������"
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
		// ����������
		list_home.setAdapter(new HomeAdapter());
		list_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent;
				switch (position) {
				case 0: // �����ֻ�����
					showLostFindDialog();
					break;
				case 7 :// ����߼�����
					intent = new Intent(HomeActivity.this, AToolsActivity.class);
					startActivity(intent);
					break;
				case 8: // ������������
					intent = new Intent(HomeActivity.this, SettingActivity.class);
					startActivity(intent);
					break;

				default:
					break;
				}
				
			}
		});
		
	}
//	���ݵ�ǰ�����������ͬ�Ի���
	
	protected void showLostFindDialog() {
		// �ж��Ƿ��������룬���û�����ã��͵������öԻ���
//		������ã��͵�������Ի���
		
		if(isSetupPwd()){
			showEnterDialog();
		}
		else{
			showSetupPwdDialog();
			
		}
		
	}
	
	private void showSetupPwdDialog() {
		// ��������Ի���
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
//				1.�õ���������������
				String password = et_password.getText().toString().trim();
				String password_confirm = et_password_confirm.getText().toString().trim();
//				2.�ж������Ƿ�Ϊ��
				if(TextUtils.isEmpty(password) || TextUtils.isEmpty(password_confirm)){
					Toast.makeText(HomeActivity.this, "���벻��Ϊ��", 0).show();
					return;
				}
//				3.�ж����������Ƿ���ͬ
				if(password.equals(password_confirm)){
//					4.�������룬�����Ի��򣬽����ֻ�����ҳ��
					Editor editor = sp.edit();
					editor.putString("password", MD5Utils.encoder(password));
					editor.commit();
					dialog.dismiss();
					Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
					startActivity(intent);
				}else{
					Toast.makeText(HomeActivity.this, "�����������벻һ��", 0).show();
				}
				

			}
		});
	}

	private void showEnterDialog() {
		// ������������Ի���
		// ��������Ի���
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
//				1.�õ���������������
				String password = et_password.getText().toString().trim();
				String password_save = sp.getString("password", null);
//				2.�ж������Ƿ�Ϊ��
				if(TextUtils.isEmpty(password)){
					Toast.makeText(HomeActivity.this, "���벻��Ϊ��", 0).show();
					return;
				}
//				3.�ж����������Ƿ���ͬ
				if(MD5Utils.encoder(password).equals(password_save)){
//					
					dialog.dismiss();
					Log.e(TAG, "������ȷ�������Ի��򣬽����ֻ�����ҳ��");
					Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
					startActivity(intent);
				}else{
					Toast.makeText(HomeActivity.this, "����������벻��ȷ", 0).show();
				}
				

			}
		});		
		
	}

	/*
	 * �ж��Ƿ�����������
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
