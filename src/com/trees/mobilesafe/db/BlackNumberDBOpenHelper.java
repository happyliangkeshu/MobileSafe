package com.trees.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {

	private String tag = "BlackNumberDBOpenHelper";

	// 构造方法-把数据库已经创建了
	public BlackNumberDBOpenHelper(Context context) {
		super(context, "blacknumber.db", null, 1);
	}
	
	// 数据库已经创建，特别适合创建表
	@Override
	public void onCreate(SQLiteDatabase db) {
		// 拦截末世 0 拦截电话 1. 拦截短信 2. 全部拦截
		db.execSQL("create table blacknumber(_id integer primary key autoincrement, number varchar(20), mode varchar(2) )");
		Log.e(tag , "hahah");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// 数据库升级的时候调用的方法

	}

}
