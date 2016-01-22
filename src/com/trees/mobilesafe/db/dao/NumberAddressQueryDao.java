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
		// webView ����ͼƬ �������� 
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path , null, SQLiteDatabase.OPEN_READONLY);
		// �ֻ����� ���� 11λ 13,14,15,16,17,18   �ֻ�����������ʽ
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
				address = "�˾�����";
				break;
			case 4:
				address = "ģ����";
				break;
			case 5:
				address = "�ͷ��绰";
				break;
			case 6:
				address = "��λ��֪��";
				break;
			case 7:
				address = "��λ��֪��";
				break;
			case 8:
				address = "��λ��֪��";
				break;
			case 9:
				address = "��λ��֪��";
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
