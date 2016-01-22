package com.trees.mobilesafe.activity;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.trees.mobilesafe.R;

public class DragViewActivity extends Activity {

	private SharedPreferences sp;
	protected static final String TAG = "DragViewActivity";
	private ImageView iv_dragview;

	private WindowManager wm;
	private int windowWidth;
	private int windowHeight;

	private TextView tv_bottom;
	private TextView tv_top;

	long[] mHits = new long[2];

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		setContentView(R.layout.activity_drag_view);

		tv_bottom = (TextView) findViewById(R.id.tv_bottom);
		tv_top = (TextView) findViewById(R.id.tv_top);
		iv_dragview = (ImageView) findViewById(R.id.iv_dragview);
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		windowWidth = wm.getDefaultDisplay().getWidth();
		windowHeight = wm.getDefaultDisplay().getHeight();

		/**
		 * 1.��ָ������Ļ��ʱ�򣬼�¼��һ�ΰ��µ����꣨10�� 10�� 2.��ָ����Ļ���ƶ��������µ����꣬��20�� 10�� 3.
		 * ����λ�õ�ƫ������ �����ƶ��ľ��� ��x�᷽�� ��20 -10 ����y�᷽�� 10-10 4.����ƫ���������¿ؼ���λ�ã���x����10
		 * y�᷽��0 5.�ѵ�һ�ΰ��µ��������¼�¼ �ٵ�1
		 */
		int lastX = sp.getInt("lastX", 0);
		int lastY = sp.getInt("lastY", 0);
		Log.e(TAG, "ȡ�� ���������  " + "����left " + iv_dragview.getLeft() + " ����top"
				+ iv_dragview.getTop());
		if (lastY > windowHeight / 2) {
			// �ؼ�������
			tv_top.setVisibility(View.VISIBLE);
			tv_bottom.setVisibility(View.INVISIBLE);
		} else {
			// �ؼ�������
			tv_top.setVisibility(View.INVISIBLE);
			tv_bottom.setVisibility(View.VISIBLE);
		}

		// iv_dragview.layout(lastX, lastY,iv_dragview.getWidth() + lastX,
		// iv_dragview.getHeight() + lastY);
		// ʹ�õ�һ���׶ξ������õķ���
		LayoutParams params = (LayoutParams) iv_dragview.getLayoutParams();
		params.leftMargin = lastX;
		params.topMargin = lastY;
		iv_dragview.setLayoutParams(params);
		/**
		 * һ���ؼ�����view�Ӵ�������ʾ���̵���Ҫ���� ���췽�� ����onmeasure ָ��λ�� ondraw(canvas)
		 */
		
		
		iv_dragview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ʶ�����������ε������λʱ���ڣ��������Σ���˫��
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					Log.i(TAG, "˫���ˡ��������̾��С���");
					iv_dragview.layout((windowWidth -iv_dragview.getWidth() ) >> 1,
							iv_dragview.getTop(), 
							(windowWidth + iv_dragview.getWidth()) >> 1,
							iv_dragview.getBottom());
					int lastx = iv_dragview.getLeft();
					int lasty = iv_dragview.getTop();
					Editor editor = sp.edit();
					editor.putInt("lastx", lastx);
					editor.putInt("lasty", lasty);
					editor.commit();
				}
			}
		});
		
		
		iv_dragview.setOnTouchListener(new OnTouchListener() {

			float startX = 0;
			float startY = 0;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// 1.��¼��ָ��һ�ΰ��µ�����
					startX = event.getRawX();
					startY = event.getRawY();
					Log.e(TAG, "��һ�ΰ��µ�����  " + "startX: " + startX + " startY:"
							+ startY);
					break;
				case MotionEvent.ACTION_MOVE:
					// 2. ����һ���µ�����
					float newX = event.getRawX();
					float newY = event.getRawY();
					// 3. ����ƫ����
					int dx = (int) (newX - startX);
					int dy = (int) (newY - startY);
					Log.e(TAG, "ƫ����  " + "dx: " + dx + " dy:" + dy);
					int newl = iv_dragview.getLeft() + dx;
					int newt = iv_dragview.getTop() + dy;
					int newr = iv_dragview.getRight() + dx;
					int newb = iv_dragview.getBottom() + dy;
					// �ж� λ���Ƿ�Ϸ���
					if (newl < 0 || newt < 0 || newr > windowWidth
							|| newb > (windowHeight - getStatusBarHeight())) {
						break;
					}
					if (newt > windowHeight / 2) {
						// �ؼ�������
						tv_top.setVisibility(View.VISIBLE);
						tv_bottom.setVisibility(View.INVISIBLE);
					} else {
						// �ؼ�������
						tv_top.setVisibility(View.INVISIBLE);
						tv_bottom.setVisibility(View.VISIBLE);
					}

					iv_dragview.layout(newl, newt, newr, newb);

					// 5. ���¼�¼����
					startX = event.getRawX();
					startY = event.getRawY();

					break;
				case MotionEvent.ACTION_UP:
					Log.e(TAG, "��ָ�뿪��Ļ");
					Editor editor = sp.edit();
					editor.putInt("lastX", iv_dragview.getLeft());
					editor.putInt("lastY", iv_dragview.getTop());
					editor.commit();
					Log.e(TAG, "���������  " + "����left " + iv_dragview.getLeft()
							+ " ����top" + iv_dragview.getTop());
					break;

				default:
					break;
				}

				return true;
			}
		});
	}

	private int statusBarHeight;

	private int getStatusBarHeight() {
		if (statusBarHeight == 0) {
			try {
				Class c = Class.forName("com.android.internal.R$dimen");
				Object o = c.newInstance();
				Field field = c.getField("status_bar_height");
				int x = (Integer) field.get(o);
				statusBarHeight = getResources().getDimensionPixelSize(x);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return statusBarHeight;
	}
}
