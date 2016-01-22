package com.trees.mobilesafe.activity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle.Control;

import com.trees.mobilesafe.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectContactActivity extends Activity {

	private static final String TAG = "SelectContactActivity";
	private ListView lv_select_contact;
	private List<Map<String, String>> data;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			lv_select_contact.setAdapter(
					new SimpleAdapter(SelectContactActivity.this, 
							data, R.layout.select_item,
							new String[]{"name", "number"}, 
							new int[]{R.id.tv_name, R.id.tv_number}));
			
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);
		lv_select_contact = (ListView) findViewById(R.id.lv_select_contact);
		
		fillData();
		
		lv_select_contact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				String number = data.get(position).get("number").replace("-", "").replace(" ", "");
				// 1. 回传数据
				Intent intent = new Intent();
				intent.putExtra("number", number);
				setResult(1, intent);
				//				2. 当前页面关闭
				finish();
			}
		
		
		});
	}
	private void fillData() {
		new Thread(){
			public void run() {
				data = getContacts();
//				data = getAllContacts();
				handler.sendEmptyMessage(0);
			};
		}.start();
	}
	/*
	 * 得到手机里边所有的联系人
	 */
	private List<Map<String, String>> getAllContacts() {
		List<Map<String, String>> maps = new ArrayList<Map<String,String>>();
		ContentResolver resolver = getContentResolver();
		Uri raw_contacts_uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri data_uri = Uri.parse("content://com.android.contacts/data");
//		Uri raw_contacts_uri = ContactsContract.Contacts.CONTENT_URI;
//		Uri data_uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		Cursor cursor = resolver.query(raw_contacts_uri, new String[]{"contact_id"}, null, null, null);
		while(cursor.moveToNext()){
			Map<String, String> map = new HashMap<String, String>();
//			String contact_id = cursor.getString(0);
			// 获取联系人ID
			String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			if(contact_id != null){
				Cursor datacursor = resolver.query(data_uri, new String[]{"data1", "mimetype"},
						"raw_contact_id=?", new String[]{contact_id}, null);
				while(datacursor.moveToNext()){
					String data1 = datacursor.getString(0);
					String mimetype = datacursor.getString(1);
					if("vnd.android.cursor.item/phone_v2".equals(mimetype)){
						map.put("number", data1);
					}
					else if("vnd.android.cursor.item/name".equals(mimetype)){
						map.put("name", data1);
					}
				}
				datacursor.close();
				if(map.get("name") != null && map.get("number") != null)
					maps.add(map);
			}
		}
		
		return maps;
	}
	
	private List<Map<String, String>> getContacts(){

		List<Map<String, String>> maps = new ArrayList<Map<String,String>>();
		ContentResolver resolver = getContentResolver();
//		Uri raw_contacts_uri = Uri.parse("content://com.android.contacts/raw_contacts");
//		Uri data_uri = Uri.parse("content://com.android.contacts/data");
		// 管理联系人的Uri
//		content://com.android.contacts/contacts
		Uri ContactUri = ContactsContract.Contacts.CONTENT_URI;
		System.out.println(ContactUri.toString());
//		管理联系人电话的uri
//		content://com.android.contacts/data/phones
		Uri PhoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		System.out.println(PhoneUri.toString());
		
		Cursor cursor = resolver.query(ContactUri, null, null, null, null);
		while(cursor.moveToNext()){
			Map<String, String> map = new HashMap<String, String>();
			// 获取联系人ID
			String contactId = cursor.getString(  
					cursor.getColumnIndex(ContactsContract.Contacts._ID));
			// 获取联系人名字
			String name = cursor.getString(   
					cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			
			Cursor phones = getContentResolver().query(PhoneUri, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
			if(phones.moveToNext()){
				String phoneNumber = phones.getString(
						phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				if(phoneNumber != null && name != null){
					map.put("number", phoneNumber);
					map.put("name", name);
				}
				maps.add(map);
			}
			phones.close();
		}
		cursor.close();
		return maps;
	}
}
