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
 * 分批加载解决了 1. 时间等待问题 2.节约流量
 * 
 */
public class CallSmsSafeActivity extends Activity {

	// 从哪个地方开始加载下一个20条
	private int index = 0;
//	数据库总条数
	private int dbCount = 0;
	private ListView lv_blacknumber;
	private BlackNumberDao dao ;
	private ViewHolder holder;
	private LinearLayout ll_loading;
	private List<BlackNumberInfo> infos;
	private CallSmsSafeAdapter adapter;
	
	// 防止重复加载
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
//			lv_blacknumber.setSelection(index); 不推荐
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
			
			// 当状态发生变化时，回调， 
//			静止 到 滑动
//			滑动到静止    
//			手指滑动 到惯性滚动
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE://静止状态-空闲
					if(isLoading){
						Toast.makeText(getApplicationContext(), "数据正在加载", 0).show();
						return;
					}
					
					int lastPosition = lv_blacknumber.getLastVisiblePosition();
					int currenttotalSize = infos.size(); 
					if(index >= dbCount){
						Toast.makeText(getApplicationContext(), "没有数据了", 0).show();
						return;
					}
					if(lastPosition == currenttotalSize -1){
						isLoading = true;
						Toast.makeText(getApplicationContext(), "加载更多数据", 0).show();
						index += 20;
						fillData();
						
					}
					break;
				case  OnScrollListener.SCROLL_STATE_FLING: //滚动状态
				break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: //触摸滑动状态
					break;
				default:
					break;
				}
				
			}
//			当滚动的时候，执行这个方法
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
				System.out.println("使用历史缓存的view" + position);
				holder = (ViewHolder)view.getTag();
			}
			else{
				view = View.inflate(CallSmsSafeActivity.this, R.layout.callsmssafe_item, null);
				System.out.println("重新创建view对象" + position);
				// 当这个view对象被创建时，把ID给查找了，并且放在容器类
				holder = new ViewHolder();
				holder.tv_number = (TextView) view.findViewById(R.id.tv_number);
				holder.tv_mode = (TextView) view.findViewById(R.id.tv_mode);
				holder.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
				
				// view 对象和容器进行关联  保存view对象的层次结构
				view.setTag(holder);
			}

			
//			得到黑名单数据
			final BlackNumberInfo info = infos.get(position);
			holder.tv_number.setText(info.getNumber());
			String mode = info.getMode();
			if("0".equals(mode)){
				holder.tv_mode.setText("电话拦截");
			}
			else if("1".equals(mode)){
				holder.tv_mode.setText("短信拦截");
			}
			else{
				holder.tv_mode.setText("短信+电话拦截");
			}
			
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
//					1. 把数据库中的数据删除
					dao.delete(info.getNumber());
//					2. 当前列表数据删除
					infos.remove(info);
//					3.刷新数据
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
	 * 加载数据
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
	 * 用来装view对应的两个孩子
	 */
	 static class ViewHolder{
		TextView tv_number;
		TextView tv_mode;
		ImageView iv_delete;
	}
	 
//	 点击事件，弹出对话框，然后添加黑名单
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
				// 1.得到电话号码
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
//				2. 判断是否为空
				if(TextUtils.isEmpty(number)){
					Toast.makeText(getApplicationContext(), "电话号码不能为空", 0).show();
					return;
				}
//				3.保存到数据库里边
				dao.add(number, mode);
//				4.保存到当前列表
	
				infos.add(0, new BlackNumberInfo(number, mode));
//				5.对话框消掉, 刷新UI
				dialog.dismiss();
				adapter.notifyDataSetChanged();
			}
		});
		dialog.setView(contentView, 0, 0, 0, 0);
		dialog.show();
	}
}
