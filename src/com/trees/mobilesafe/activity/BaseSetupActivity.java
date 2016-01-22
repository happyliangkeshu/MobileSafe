package com.trees.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {
//	1. ����һ������ʶ����
	private GestureDetector detector;
	protected SharedPreferences sp;
	@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			sp = getSharedPreferences("config", MODE_PRIVATE);
//			2. ʵ��������ʶ����
			detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){


				
				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2,
						float velocityX, float velocityY) {
					//	���λ������� �ٶȵ�λ������ÿ��
					if(Math.abs(velocityX) < 100){
						Toast.makeText(getApplicationContext(), "�磬����", 0).show();
						return true;
					}
					
					//	����y�᷽��б��
					if(Math.abs(e2.getRawY() - e1.getRawY()) > 100){
						Toast.makeText(getApplicationContext(), "�磬��������������", 0).show();
						return true;
					}

					
					if(e2.getRawX() -e1.getRawX() > 200){
						//	��ʾ��һ��ҳ��
						showPrev();
						return true;
					}
					if(e1.getRawX() - e2.getRawX() > 200){
						//	��ʾ��һ��ҳ��
						showNext();
						return true;
					}
					return super.onFling(e1, e2, velocityX, velocityY);
				}
				
				
			});
		}
	
	/*
	 * ��ʾ��һ��ҳ��ĳ��󷽷�
	 */
      public abstract void showNext() ;
      /*
  	 * ��ʾ��һ��ҳ��ĳ��󷽷�
  	 */
      public abstract void showPrev() ;

     /**
      * ��һ����ť�ĵ���¼�
      * @param v
      */
  	public void next(View v){
		showNext();
	}
  	
  	public void prev(View v){
		showPrev();
	}
	//	3.ʹ������ʶ����
	@Override
		public boolean onTouchEvent(MotionEvent event) {
			detector.onTouchEvent(event);
			return super.onTouchEvent(event);
		}
}
