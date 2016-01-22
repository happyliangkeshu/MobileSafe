package com.trees.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {
//	1. 定义一个手势识别器
	private GestureDetector detector;
	protected SharedPreferences sp;
	@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			sp = getSharedPreferences("config", MODE_PRIVATE);
//			2. 实例化手势识别器
			detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){


				
				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2,
						float velocityX, float velocityY) {
					//	屏蔽滑动慢， 速度单位，像素每秒
					if(Math.abs(velocityX) < 100){
						Toast.makeText(getApplicationContext(), "哥，快点吧", 0).show();
						return true;
					}
					
					//	屏蔽y轴方向，斜滑
					if(Math.abs(e2.getRawY() - e1.getRawY()) > 100){
						Toast.makeText(getApplicationContext(), "哥，不可以这样滑动", 0).show();
						return true;
					}

					
					if(e2.getRawX() -e1.getRawX() > 200){
						//	显示上一个页面
						showPrev();
						return true;
					}
					if(e1.getRawX() - e2.getRawX() > 200){
						//	显示下一个页面
						showNext();
						return true;
					}
					return super.onFling(e1, e2, velocityX, velocityY);
				}
				
				
			});
		}
	
	/*
	 * 显示下一个页面的抽象方法
	 */
      public abstract void showNext() ;
      /*
  	 * 显示上一个页面的抽象方法
  	 */
      public abstract void showPrev() ;

     /**
      * 下一步按钮的点击事件
      * @param v
      */
  	public void next(View v){
		showNext();
	}
  	
  	public void prev(View v){
		showPrev();
	}
	//	3.使用手势识别器
	@Override
		public boolean onTouchEvent(MotionEvent event) {
			detector.onTouchEvent(event);
			return super.onTouchEvent(event);
		}
}
