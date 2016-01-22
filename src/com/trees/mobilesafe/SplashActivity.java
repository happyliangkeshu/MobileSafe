package com.trees.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.trees.mobilesafe.activity.HomeActivity;
import com.trees.mobilesafe.utils.StreamTools;
/**
 * ����ҳ��
 * 1.��ʾlogo-���logo�ö���
 * 2.�ж��Ƿ�������
 * 3.�Ƿ�����
 * 4.�жϺϷ���
 * 5.У���Ƿ���sdcard
 * 
 * @author trees
 *
 */
public class SplashActivity extends Activity {

	protected static final String TAG = "SplashActivity";
	protected static final int ENTER_HOME = 1;
	protected static final int SHOW_UPDATE_DIALOG = 2;
	protected static final int URL_ERROR = 3;
	protected static final int NETWORK_ERROR = 4;
	protected static final int JSON_ERROR = 5;
	private TextView tv_splash_version;
	private TextView tv_splash_updateinfo;
	// ������������Ϣ
	private String description;
	private SharedPreferences sp;
	// 
	private String apkurl;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case  ENTER_HOME://������ҳ��
				enterHome();
				break;
			case SHOW_UPDATE_DIALOG: // ���������Ի���
				Log.e(TAG, "���°汾�����������Ի���");
				showUpdateDialog();
				break;
			case URL_ERROR:// URL�쳣
				enterHome();
				Toast.makeText(SplashActivity.this, "URL�쳣", 0).show();
				break;
			case NETWORK_ERROR:// �����쳣
				enterHome();
				Toast.makeText(getApplicationContext(), "�����쳣", 0).show();
				break;
			case JSON_ERROR:// JSON�����쳣
				enterHome();
				Toast.makeText(getApplicationContext(), "JSON�����쳣", 0).show();
				break;
			default:
				break;
			}
			
		};
	};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        
        setContentView(R.layout.activity_splash);
        tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
        tv_splash_version.setText("�汾�ţ�" + getVersionName());
        tv_splash_updateinfo = (TextView) findViewById(R.id.tv_splash_updateinfo);
       
        copyDB();
        if( sp.getBoolean("update", false))
        	checkVersion();
        else{
        	// �ӳ����������ҳ��
        	handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					enterHome();
					
				}
			}, 2000);
        }
        
        AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
        aa.setDuration(1000);
        findViewById(R.id.rl_splash_root).startAnimation(aa);
    }
    private void copyDB() {
    	File file = new  File(getFilesDir(), "address.db");
    	if(file.exists() && file.length() > 0){
    		System.out.println("���ݿ��Ѿ����ڣ�����Ҫ������");
    		return;
    	}
		try {
			InputStream is = getAssets().open("address.db");
			
			FileOutputStream fos = new FileOutputStream(file);
			int len = 0;
			byte buffer[] = new byte[1024];
			while((len = is.read(buffer)) != -1 ){
				fos.write(buffer, 0, len);
			}
			is.close();
			fos.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	protected void showUpdateDialog() {
// this = Activity.this �� Context������
//    	�Ի����ǹ�����Activity�ϣ��Ի�����Activity��һ����
//    	���û��Activity���޷������Ի���
//    	Activity.this��context������
//    	�����еĸ��಻һ���У�����û������
//    	�����е�����һ���� ����������
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("��ʾ");
		builder.setMessage(description);
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				enterHome();
			}
		});
		builder.setNegativeButton("�´���˵", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				enterHome();
			}
		});
		builder.setPositiveButton("��������", new OnClickListener() {
		@Override
			public void onClick(DialogInterface dialog, int which) {
				// ����apk  afinal jar��  �滻��װ
				// �ж�sd���Ƿ����	
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					FinalHttp http = new FinalHttp();
					http.download(apkurl,
					Environment.getExternalStorageDirectory()+"/MobileSafe2.0.apk" // �ⲿ�洢��ַ
						, new AjaxCallBack<File>() {
							@Override
							public void onFailure(Throwable t, int errorNo,
									String strMsg) {
								
								super.onFailure(t, errorNo, strMsg);
								t.printStackTrace();
								Toast.makeText(getApplicationContext(), "����ʧ����", 0).show();
							}

							@Override
							public void onLoading(long count, long current) {
								super.onLoading(count, current);
								tv_splash_updateinfo.setVisibility(View.VISIBLE);
								int progress = (int)(current * 100.0 / count);
								tv_splash_updateinfo.setText("���ؽ��ȣ�" + progress +"%");
							}

							@Override
							public void onSuccess(File t) {
								super.onSuccess(t);
								installApk(t);
								Toast.makeText(getApplicationContext(), "���سɹ�", 0).show();
							}

							private void installApk(File t) {
								Intent intent = new Intent();
								intent.setAction("android.intent.action.VIEW");
								intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");
								startActivity(intent);
							}
							
						});
				
					}
				else {
					Toast.makeText(getApplicationContext(), "sdcard������", 0).show();
				}
			}
		});
		builder.show();
		
    }
	protected void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		// �ر�����ҳ��
		finish();
		
	}
	private void checkVersion(){
    	
    	new Thread(){
    		public void run(){
    			Message msg = Message.obtain();
    			long startTime = System.currentTimeMillis();
    			// �������磬�õ����µİ汾��Ϣ
    			try {
					URL url = new URL(getString(R.string.serverurl));
					HttpURLConnection con= (HttpURLConnection) url.openConnection();
					con.setRequestMethod("GET");
					con.setConnectTimeout(4000);
					if(con.getResponseCode() == 200){
						// ����ɹ�������ת��ΪString����
						InputStream is = con.getInputStream();
						String result = StreamTools.readFromStream(is);
						Log.e(TAG, "result=" + result);
						
						JSONObject obj = new JSONObject(result);
						String version = (String) obj.get("version");
						description = (String) obj.get("description");
						apkurl = (String) obj.get("apkurl");
						if(getVersionName().equals(version)){
							// û���°汾������ҳ��
							msg.what = ENTER_HOME;
						}
						else{
							//���������Ի���
							msg.what = SHOW_UPDATE_DIALOG;
						}
					}
    			} catch (MalformedURLException e) {
					// 
					e.printStackTrace();
					msg.what = URL_ERROR;
				} catch (IOException e) {
					// �����쳣
					e.printStackTrace();
					msg.what = NETWORK_ERROR;
				} catch (JSONException e) {
					// ����json�쳣
					e.printStackTrace();
					msg.what = JSON_ERROR;
				}
    			finally{
    				long endTime = System.currentTimeMillis();
    				long dTime = endTime - startTime;
    				if(dTime < 2000){
    					SystemClock.sleep(2000 - dTime);
    				}
    				handler.sendMessage(msg);
    				
    				
    			}
    			
    			
    		}
    	}.start();
    }
    private String getVersionName(){
    	// ��������
    	PackageManager pm = getPackageManager();
    	// �����嵥�ļ���Ϣ
			try {
				PackageInfo packInfo = pm.getPackageInfo(getPackageName(), 0);
				return packInfo.versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
    	return null;
    }
    
}
