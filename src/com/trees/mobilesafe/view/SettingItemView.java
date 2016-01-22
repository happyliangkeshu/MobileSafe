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
//	初始化布局文件      inflate方法把布局文件转换为View
//	最后一个参数，添加谁进来，谁就是这个布局文件的父亲，
//	也就是说，把布局文件挂载在传进来的这个控件
	private void initView(Context context) {
		View.inflate(context, R.layout.setting_item_view, this);
		cb_status = (CheckBox) findViewById(R.id.cb_status);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		tv_title = (TextView) findViewById(R.id.tv_title);
	}
//	得到组合控件是否勾选
	public boolean isChecked(){
		
		return cb_status.isChecked();
	}
//	设置组合控件的勾选状态
	
	public void setChecked(boolean isChecked){
		cb_status.setChecked(isChecked);
		if(isChecked )
			setDescription(update_on);
		else 
			setDescription(update_off);
	}
	
//	设置组合控件的状态信息
	public void setDescription (String text){
		tv_desc.setText(text);
	}
	
// 要设置样式的时候使用
	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	//	在布局文件实例化的时候使用
	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		title = attrs.getAttributeValue(namespace,"title" );
		update_off = attrs.getAttributeValue(namespace,"update_off" );
		update_on = attrs.getAttributeValue(namespace,"update_on" );
		tv_title.setText(title);
		
		// 设置description ，默认设置关闭状态
		setDescription(update_off);
	}
	// 在代码中实例化的时候使用
	public SettingItemView(Context context) {
		super(context);
		initView(context);
	}

}
