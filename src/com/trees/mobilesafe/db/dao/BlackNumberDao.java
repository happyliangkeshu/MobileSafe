package com.trees.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.trees.mobilesafe.db.BlackNumberDBOpenHelper;
import com.trees.mobilesafe.domain.BlackNumberInfo;

public class BlackNumberDao {

	private BlackNumberDBOpenHelper helper;
	public  BlackNumberDao(Context context){
		helper = new BlackNumberDBOpenHelper(context);
	}
	
//	添加一条黑名单数据
	public void add (String number, String mode){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		db.insert("blacknumber", null, values );
		db.close();
	}
//	删除一条黑名单数据
	public void delete (String number){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("blacknumber", "number=?", new String[]{number});
		db.close();
	}
	public void update (String number, String newmode){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", newmode);
		db.update("blacknumber", values , "number=?", new String[]{number});
		db.close();
	}
	public boolean query(String number){
		SQLiteDatabase db = helper.getWritableDatabase();
		
		Cursor cursor = db.query("blacknumber", null, "number=?", new String[]{number}, null, null, null);
		if(cursor.moveToNext()){
			return true;
		}
		db.close();
		return false;
	}
	
	public String queryMode(String number){
		String result = "2";
		SQLiteDatabase db = helper.getWritableDatabase();
		
		Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "number=?", new String[]{number}, null, null, null);
		if(cursor.moveToNext()){
			result = cursor.getString(0);
		}
		db.close();
		return result;
	}
	
	public List<BlackNumberInfo> queryAll(){
		List<BlackNumberInfo> result = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = helper.getWritableDatabase();
		
		Cursor cursor = db.query("blacknumber", new String[]{"number", "mode"}, null, null, null, null, null);
		while(cursor.moveToNext()){
			
			BlackNumberInfo temp = new BlackNumberInfo(cursor.getString(0), cursor.getString(1));
			result.add(temp)  ;
		}
		db.close();
		return result;
	}
	

	/*
	 * 加载部分数据 ，从哪个地方开始加载二十条
	 * 每次请求返回20条数据
	 */
	public List<BlackNumberInfo> queryPart(int index){
		SystemClock.sleep(600);
		List<BlackNumberInfo> result = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc limit 20 offset ?", new String[]{index + ""});
//		Cursor cursor = db.query("blacknumber", new String[]{"number", "mode"}, null, null, null, null, null);
		while(cursor.moveToNext()){
			
			BlackNumberInfo temp = new BlackNumberInfo(cursor.getString(0), cursor.getString(1));
			result.add(temp)  ;
		}
		db.close();
		return result;
	}
	
	// 得到数据库的总条数
	public int  queryCount(){
		
		int result = 0;
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
		while(cursor.moveToNext()){
			result = cursor.getInt(0);
		}
		db.close();
		return result;
	}
}
