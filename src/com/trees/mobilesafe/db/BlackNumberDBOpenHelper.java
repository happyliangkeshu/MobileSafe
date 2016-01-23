package com.trees.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {

	private String tag = "BlackNumberDBOpenHelper";

	// ���췽��-�����ݿ��Ѿ�������
	public BlackNumberDBOpenHelper(Context context) {
		super(context, "blacknumber.db", null, 1);
	}
	
	// ���ݿ��Ѿ��������ر��ʺϴ�����
	@Override
	public void onCreate(SQLiteDatabase db) {
		// ����ĩ�� 0 ���ص绰 1. ���ض��� 2. ȫ������
		db.execSQL("create table blacknumber(_id integer primary key autoincrement, number varchar(20), mode varchar(2) )");
		Log.e(tag , "hahah");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// ���ݿ�������ʱ����õķ���

	}

}
