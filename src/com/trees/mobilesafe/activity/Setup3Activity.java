package com.trees.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.trees.mobilesafe.R;

public class Setup3Activity extends BaseSetupActivity {

	private EditText et_safenumber;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		et_safenumber = (EditText) findViewById(R.id.et_safenumber);
		et_safenumber.setText(sp.getString("safenumber", ""));
	}

	@Override
	public void showNext() {
		String number = et_safenumber.getText().toString().trim();
		if(TextUtils.isEmpty(number)){
			Toast.makeText(getApplicationContext(), "安全号码还没有设置", 0).show();
			return;
		}
		Editor editor = sp.edit();
		editor.putString("safenumber", number);
 		editor.commit();
		Intent intent = new Intent(this, Setup4Activity.class);
		startActivity(intent);
		//
		finish();
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
		
	}
	@Override
	public void showPrev() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		//
		finish();	
		overridePendingTransition(R.anim.tran_prev_in, R.anim.tran_prev_out);
		
	}
	// 选择联系人按钮的点击事件，  进入联系人列表
	public void selectContact(View v){
		Intent intent = new Intent(this, SelectContactActivity.class);
		startActivityForResult(intent, 0);
	}	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		if(data == null){
			return ;
		}
		String number = data.getStringExtra("number");
		et_safenumber.setText(number);
	}
}
