package com.trees.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trees.mobilesafe.R;

/**
 * �����Զ������Ͽؼ���������������TextView ������һ��ImageVeiw,����һ��View
 * @author Administrator
 *
 */
public class SettingClickView extends RelativeLayout {
	private TextView tv_desc;
	private TextView tv_title;
	
	private  String desc_on;
	private String desc_off;
	private String namespace = 
			"http://schemas.android.com/apk/res/com.trees.mobilesafe";
	/**
	 * ��ʼ�������ļ�
	 * @param context
	 */
	private void iniView(Context context) {
		
		//��һ�������ļ�---��View ���Ҽ�����SettingItemView
		View.inflate(context, R.layout.setting_click_view, this);
		tv_desc = (TextView) this.findViewById(R.id.tv_desc);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		
	}

	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		iniView(context);
	}
	/**
	 * �������������Ĺ��췽���������ļ�ʹ�õ�ʱ�����
	 * @param context
	 * @param attrs
	 */

	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		iniView(context);
		String title = attrs.getAttributeValue(namespace, "title");
		desc_on = attrs.getAttributeValue(namespace, "desc_on");
		desc_off = attrs.getAttributeValue(namespace, "desc_off");
		tv_title.setText(title);
		setDescription(desc_off);
	}

	

	public SettingClickView(Context context) {
		super(context);
		iniView(context);
	}
	
	
	/**
	 * ������Ͽؼ���״̬
	 */
	
	public void setChecked(boolean checked){
		if(checked){
			setDescription(desc_on);
		}else{
			setDescription(desc_off);
		}
	}
	
	/**
	 * ���� ��Ͽؼ���������Ϣ
	 */
	
	public void setDescription(String text){
		tv_desc.setText(text);
	}
	
	/**
	 * ������Ͽؼ��ı���
	 */
	
	public void setTitle(String title){
		tv_title.setText(title);
	}
	
	

}
