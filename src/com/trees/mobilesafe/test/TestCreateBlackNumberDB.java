package com.trees.mobilesafe.test;

import java.util.Random;

import com.trees.mobilesafe.db.BlackNumberDBOpenHelper;
import com.trees.mobilesafe.db.dao.BlackNumberDao;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

public class TestCreateBlackNumberDB extends AndroidTestCase {

	public void testCreateBlackNumberDB(){
		BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(getContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		
	}
	public void add(){
		BlackNumberDao dao = new BlackNumberDao(getContext());
		// 13512345600 - 13512345699
		Random random = new Random();
		for(int i = 0; i < 100; ++i){
			dao.add("1351234560" + i,  String.valueOf(random.nextInt(3)));
		}
		
	}
	public void delete(){
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.delete("119");
	}
	public void update(){
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.update("119", "0");
	}
	public void query(){
		BlackNumberDao dao = new BlackNumberDao(getContext());
		boolean result = dao.query("119");
		assertEquals(true, result);
	}
	public void queryMode(){
		BlackNumberDao dao = new BlackNumberDao(getContext());
		String mode = dao.queryMode("119");
		System.out.println("mode = " + mode);
	}
}
