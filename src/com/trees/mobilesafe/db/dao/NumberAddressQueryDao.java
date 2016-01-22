package com.trees.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NumberAddressQueryDao {

	

	private static String path = "/data/data/com.trees.mobilesafe/files/address.db";
	private static String tag = "NumberAddressQueryDao";

	public static String getAddress(String number){
		String address = number;
		Cursor cursor = null;
		// webView 加载图片 加载网络 
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path , null, SQLiteDatabase.OPEN_READONLY);
		// 手机号码 个数 11位 13,14,15,16,17,18   手机号码正则表达式
		if(number.matches("^1[345678]\\d{9}$")){

		 cursor = db.rawQuery("select city from info where mobileprefix = ?", new String[]{number.substring(0, 7)});
			while(cursor.moveToNext()){
				String location = cursor.getString(0);
				Log.e(tag , location);
				address = location;
			}
			cursor.close();
			db.close();

		}
		else{
			switch (number.length()) {
			case 3:
				address = "匪警号码";
				break;
			case 4:
				address = "模拟器";
				break;
			case 5:
				address = "客服电话";
				break;
			case 6:
				address = "六位不知道";
				break;
			case 7:
				address = "七位不知道";
				break;
			case 8:
				address = "八位不知道";
				break;
			case 9:
				address = "九位不知道";
				break;	
			default:
				if(number != null && number.startsWith("0")  && number.length() >= 10){
					cursor = db.rawQuery("select city from info where area = ?", 
							new String[]{number.substring(0, 3)});
					if(cursor.moveToNext() == false){
						cursor = db.rawQuery("select city from info where area = ?", 
								new String[]{number.substring(0, 4)});
					}
					while(cursor.moveToNext()){
						String location = cursor.getString(0);
						address = location;
					}
					cursor.close();
					db.close();
				}
				break;
			}
			
		}

		if(db != null){
			db.close();
		}
		return address ;
	}
}
