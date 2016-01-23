package com.trees.mobilesafe.activity;


import java.util.List;

import com.trees.mobilesafe.R;
import com.trees.mobilesafe.db.dao.BlackNumberDao;
import com.trees.mobilesafe.domain.BlackNumberInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
/*
 * �������ؽ���� 1. ʱ��ȴ����� 2.��Լ����
 * 
 */
public class CallSmsSafeActivity extends Activity {

	// ���ĸ��ط���ʼ������һ��20��
	private int index = 0;
//	���ݿ�������
	private int dbCount = 0;
	private ListView lv_blacknumber;
	private BlackNumberDao dao ;
	private ViewHolder holder;
	private LinearLayout ll_loading;
	private List<BlackNumberInfo> infos;
	private CallSmsSafeAdapter adapter;
	
	// ��ֹ�ظ�����
	private boolean isLoading = false;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(adapter == null ){
				adapter = new CallSmsSafeAdapter();
				lv_blacknumber.setAdapter(adapter);
			}
			else{
				adapter.notifyDataSetChanged();
			}
			isLoading = false;
//			lv_blacknumber.setSelection(index); ���Ƽ�
			ll_loading.setVisibility(View.INVISIBLE);
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		fillData();
		lv_blacknumber.setOnScrollListener(new OnScrollListener() {
			
			// ��״̬�����仯ʱ���ص��� 
//			��ֹ �� ����
//			��������ֹ    
//			��ָ���� �����Թ���
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE://��ֹ״̬-����
					if(isLoading){
						Toast.makeText(getApplicationContext(), "�������ڼ���", 0).show();
						return;
					}
					
					int lastPosition = lv_blacknumber.getLastVisiblePosition();
					int currenttotalSize = infos.size(); 
					if(index >= dbCount){
						Toast.makeText(getApplicationContext(), "û��������", 0).show();
						return;
					}
					if(lastPosition == currenttotalSize -1){
						isLoading = true;
						Toast.makeText(getApplicationContext(), "���ظ�������", 0).show();
						index += 20;
						fillData();
						
					}
					break;
				case  OnScrollListener.SCROLL_STATE_FLING: //����״̬
				break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: //��������״̬
					break;
				default:
					break;
				}
				
			}
//			��������ʱ��ִ���������
			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	private class CallSmsSafeAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if(convertView != null){
				view = convertView;
				System.out.println("ʹ����ʷ�����view" + position);
				holder = (ViewHolder)view.getTag();
			}
			else{
				view = View.inflate(CallSmsSafeActivity.this, R.layout.callsmssafe_item, null);
				System.out.println("���´���view����" + position);
				// �����view���󱻴���ʱ����ID�������ˣ����ҷ���������
				holder = new ViewHolder();
				holder.tv_number = (TextView) view.findViewById(R.id.tv_number);
				holder.tv_mode = (TextView) view.findViewById(R.id.tv_mode);
				holder.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
				
				// view ������������й���  ����view����Ĳ�νṹ
				view.setTag(holder);
			}

			
//			�õ�����������
			final BlackNumberInfo info = infos.get(position);
			holder.tv_number.setText(info.getNumber());
			String mode = info.getMode();
			if("0".equals(mode)){
				holder.tv_mode.setText("�绰����");
			}
			else if("1".equals(mode)){
				holder.tv_mode.setText("��������");
			}
			else{
				holder.tv_mode.setText("����+�绰����");
			}
			
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
//					1. �����ݿ��е�����ɾ��
					dao.delete(info.getNumber());
//					2. ��ǰ�б�����ɾ��
					infos.remove(info);
//					3.ˢ������
					adapter.notifyDataSetChanged();
				}
			});
			return view;
		}
		@Override
		public Object getItem(int arg0) {
			
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			
			return 0;
		}


		
	}
	/*
	 * ��������
	 */
	private void fillData() {
		dao = new BlackNumberDao(this);
		ll_loading.setVisibility(View.VISIBLE);
		new Thread(){
			public void run() {
				if(infos == null){
					infos = dao.queryPart(index);
				}
				else{
					infos.addAll(dao.queryPart(index));
				}
				dbCount = dao.queryCount();
				handler.sendEmptyMessage(0);
			};
		}.start();
	}
	/*
	 * ����װview��Ӧ����������
	 */
	 static class ViewHolder{
		TextView tv_number;
		TextView tv_mode;
		ImageView iv_delete;
	}
	 
//	 ����¼��������Ի���Ȼ����Ӻ�����
	 private AlertDialog dialog;
	public void addBlackNumber(View v){
		AlertDialog.Builder builder  = new Builder(this);
		dialog  = builder.create();
		
		View contentView = View.inflate(this, R.layout.dialog_addblacknumber, null);
		
		final EditText et_number  = (EditText) contentView.findViewById(R.id.et_number);
		final RadioGroup rg_mode = (RadioGroup) contentView.findViewById(R.id.rg_mode);
		Button ok = (Button) contentView.findViewById(R.id.ok);
		Button cancel = (Button) contentView.findViewById(R.id.cancel);
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				
			}
		});
		
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// 1.�õ��绰����
				String number = et_number.getText().toString().trim();
				int checkedId = rg_mode.getCheckedRadioButtonId();
				String mode = "2";
				switch (checkedId ) {
				case R.id.rb_number:
						mode = "0";
					break;
				case R.id.rb_sms:
					mode = "1";
					break;
				case R.id.rb_all:
					mode = "2";
					break;
				default:
					break;
				}
//				2. �ж��Ƿ�Ϊ��
				if(TextUtils.isEmpty(number)){
					Toast.makeText(getApplicationContext(), "�绰���벻��Ϊ��", 0).show();
					return;
				}
//				3.���浽���ݿ����
				dao.add(number, mode);
//				4.���浽��ǰ�б�
	
				infos.add(0, new BlackNumberInfo(number, mode));
//				5.�Ի�������, ˢ��UI
				dialog.dismiss();
				adapter.notifyDataSetChanged();
			}
		});
		dialog.setView(contentView, 0, 0, 0, 0);
		dialog.show();
	}
}
