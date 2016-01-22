package com.trees.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.trees.mobilesafe.R;
import com.trees.mobilesafe.view.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {

	protected static final String TAG = "Setup2Activity";
	private SettingItemView siv_bind_sim;
	/**
	 * �绰����-��ȡSIM����Ϣ����������͹Ҷϵ绰
	 */
	private TelephonyManager tm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		siv_bind_sim = (SettingItemView) findViewById(R.id.siv_bind_sim);
		String sim = sp.getString("sim", "");
		if(TextUtils.isEmpty(sim)){
			// û�а�sim��
			siv_bind_sim.setChecked(false);
		}
		else{ // 
			siv_bind_sim.setChecked(true);
		}
		siv_bind_sim.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if(siv_bind_sim.isChecked()){
					siv_bind_sim.setChecked(false);
					editor.putString("sim", "");
				}
				else{
					siv_bind_sim.setChecked(true);
					// ��ȡsim���Ĵ���
//					tm.getLine1Number();// �õ�SIM���ĵ绰
					String sim = tm.getSimSerialNumber();
					sim = "123";
					Log.e(TAG, "--" + sim);
					editor.putString("sim", sim);
				}
				editor.commit();
			}
		});
	}
	@Override
	public void showNext() {
		String sim = sp.getString("sim", "");
		if(TextUtils.isEmpty(sim)){
			Toast.makeText(this, "���sim��", 0).show();
			return ;
		}
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		//
		finish();
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
		
	}
	@Override
	public void showPrev() {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		//
		finish();	
		overridePendingTransition(R.anim.tran_prev_in, R.anim.tran_prev_out);
		
	}
	
}
