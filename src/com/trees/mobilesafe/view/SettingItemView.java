package com.trees.mobilesafe.view;

import com.trees.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {
	private CheckBox cb_status;
	private TextView tv_desc, tv_title;
	private String title, update_off, update_on;
	private String tag ="SettingItemView";
	private static final 
	String namespace = 
	"http://schemas.android.com/apk/res/com.trees.mobilesafe";
//	��ʼ�������ļ�      inflate�����Ѳ����ļ�ת��ΪView
//	���һ�����������˭������˭������������ļ��ĸ��ף�
//	Ҳ����˵���Ѳ����ļ������ڴ�����������ؼ�
	private void initView(Context context) {
		View.inflate(context, R.layout.setting_item_view, this);
		cb_status = (CheckBox) findViewById(R.id.cb_status);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		tv_title = (TextView) findViewById(R.id.tv_title);
	}
//	�õ���Ͽؼ��Ƿ�ѡ
	public boolean isChecked(){
		
		return cb_status.isChecked();
	}
//	������Ͽؼ��Ĺ�ѡ״̬
	
	public void setChecked(boolean isChecked){
		cb_status.setChecked(isChecked);
		if(isChecked )
			setDescription(update_on);
		else 
			setDescription(update_off);
	}
	
//	������Ͽؼ���״̬��Ϣ
	public void setDescription (String text){
		tv_desc.setText(text);
	}
	
// Ҫ������ʽ��ʱ��ʹ��
	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	//	�ڲ����ļ�ʵ������ʱ��ʹ��
	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		title = attrs.getAttributeValue(namespace,"title" );
		update_off = attrs.getAttributeValue(namespace,"update_off" );
		update_on = attrs.getAttributeValue(namespace,"update_on" );
		tv_title.setText(title);
		
		// ����description ��Ĭ�����ùر�״̬
		setDescription(update_off);
	}
	// �ڴ�����ʵ������ʱ��ʹ��
	public SettingItemView(Context context) {
		super(context);
		initView(context);
	}

}
