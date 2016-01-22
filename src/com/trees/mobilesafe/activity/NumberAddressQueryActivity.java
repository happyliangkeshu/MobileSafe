
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
	// �񶯷��� 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_address_query);
		et_number = (EditText) findViewById(R.id.et_number);
		tv_result = (TextView) findViewById(R.id.tv_result);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		et_number.addTextChangedListener(new TextWatcher() {
			// ���ı��ı��ʱ�򱻻ص�
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
			// ���ı��ı�ǰ��ʱ��ص�
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
//		1. �ȵõ��绰����
		String number = et_number.getText().toString();
//		2.�ж��Ƿ�Ϊ��
		if(TextUtils.isEmpty(number)){
			Toast.makeText(getApplicationContext(), "�绰���벻��Ϊ��", 0).show();
			// ����Ч��
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
//			shake.setInterpolator(new Interpolator() {		
//				@Override
//				public float getInterpolation(float input) {
//					// ����ʽ  
//					
//					return 0;
//				}
//			});
			et_number.startAnimation(shake);
			//��Ч��
			vibrator.vibrate(2000); // ��
//			vibrator.vibrate(pa)
			long [] pattern = {500, 500, 1000, 1000, 2000, 2000};
			// -1�����ظ��� 0 �ظ� 2
			vibrator.vibrate(pattern, -1);
		}
		else{
			
			String address = NumberAddressQueryDao.getAddress(number);
			tv_result.setText(address);
			// �����ѯ�� �������ݿ��ѯ
			System.out.println(number + "number");
		}
	}
	
}
