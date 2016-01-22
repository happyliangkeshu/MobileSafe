
package com.trees.mobilesafe.activity;


import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.trees.mobilesafe.R;
import com.trees.mobilesafe.db.dao.NumberAddressQueryDao;

public class NumberAddressQueryActivity extends Activity {

	private EditText et_number;
	private TextView tv_result;
	private Vibrator vibrator;
	// 振动服务 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_address_query);
		et_number = (EditText) findViewById(R.id.et_number);
		tv_result = (TextView) findViewById(R.id.tv_result);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		et_number.addTextChangedListener(new TextWatcher() {
			// 当文本改变的时候被回调
			@Override
			public void onTextChanged(CharSequence s, int start, int arg2, int arg3) {
				if(s != null && s.length() >= 3){
					String address = NumberAddressQueryDao.getAddress(s.toString());
					tv_result.setText(address);
				}
				else if(s != null && s.length() < 3){
					tv_result.setText("");
				}
				
			}
			// 当文本改变前的时候回调
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void query(View v){
//		1. 先得到电话号码
		String number = et_number.getText().toString();
//		2.判断是否为空
		if(TextUtils.isEmpty(number)){
			Toast.makeText(getApplicationContext(), "电话号码不能为空", 0).show();
			// 抖动效果
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
//			shake.setInterpolator(new Interpolator() {		
//				@Override
//				public float getInterpolation(float input) {
//					// 方程式  
//					
//					return 0;
//				}
//			});
			et_number.startAnimation(shake);
			//震动效果
			vibrator.vibrate(2000); // 震动
//			vibrator.vibrate(pa)
			long [] pattern = {500, 500, 1000, 1000, 2000, 2000};
			// -1代表不重复， 0 重复 2
			vibrator.vibrate(pattern, -1);
		}
		else{
			
			String address = NumberAddressQueryDao.getAddress(number);
			tv_result.setText(address);
			// 网络查询， 本地数据库查询
			System.out.println(number + "number");
		}
	}
	
}
