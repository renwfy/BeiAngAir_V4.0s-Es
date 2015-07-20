package com.beiang.airdog.ui.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.beiang.airdog.api.API;
import com.beiang.airdog.constant.Constants.AirdogFC;
import com.beiang.airdog.constant.Constants.Command;
import com.beiang.airdog.constant.Constants.Device;
import com.beiang.airdog.constant.Constants.SubDevice;
import com.beiang.airdog.net.business.BsOperationHub;
import com.beiang.airdog.net.business.entity.CurrentDevice;
import com.beiang.airdog.net.business.entity.DevEntity;
import com.beiang.airdog.net.business.homer.CommandPair;
import com.beiang.airdog.net.httpcloud.aync.abs.BaseMsg.RspMsgBase;
import com.beiang.airdog.net.httpcloud.aync.abs.ReqCbk;
import com.beiang.airdog.ui.base.BaseMultiPartActivity;
import com.beiang.airdog.ui.model.AirInfo;
import com.beiang.airdog.ui.model.AirdogCycle;
import com.beiang.airdog.ui.model.MenuEntity;
import com.beiang.airdog.ui.model.OPEntity;
import com.beiang.airdog.utils.EParse;
import com.beiang.airdog.utils.LogUtil;
import com.beiang.airdog.view.AlertDialog;
import com.beiang.airdog.view.AlertDialog.AlertDialogCallBack;
import com.beiang.airdog.view.WeatherView;
import com.beiang.airdog.widget.Toast;
import com.broadlink.beiangair.R;
import com.dtr.zxing.activity.EncodeActivity;

public class AirdogActivity extends BaseMultiPartActivity implements OnClickListener,OnItemClickListener {
	private DevEntity mDevice;
	private AirInfo mAirInfo;

	Handler mHandler;
	private boolean canRefrash;
	private boolean isActivity;

	private WeatherView weather_layout;
	
	TextView tv_inside_temper_value,tv_inside_humidity_value,tv_inside_time,tv_inside_date,tv_dev_nickname;
	TextView tv_pm25_11;
	ImageView iv_cr, battery;

	Animation roatAnim;
	AnimationDrawable animBatteryCharging;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_airdog);
		
		initMenuData();
		setOnItemClickListener(this);
		
		isActivity = true;
		mDevice = CurrentDevice.instance().curDevice;
		mAirInfo = mDevice.airInfo;
		if(mAirInfo == null){
			mAirInfo = new AirInfo();
		}
		mHandler = new Handler();
		roatAnim = AnimationUtils.loadAnimation(mActivity, R.anim.rotate_anim);

		
		initView();
		setData();
		weather_layout.load();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		iv_cr.clearAnimation();
		iv_cr.setAnimation(roatAnim);
		
		canRefrash = true;
		queryMXData();
	}

	private void initView() {
		weather_layout = (WeatherView) findViewById(R.id.weather_layout);
		
		tv_inside_temper_value = (TextView) findViewById(R.id.tv_inside_temper_value);
		tv_inside_humidity_value = (TextView) findViewById(R.id.tv_inside_humidity_value);
		tv_inside_time = (TextView) findViewById(R.id.tv_inside_time);
		tv_inside_date = (TextView) findViewById(R.id.tv_inside_date);
		tv_dev_nickname = (TextView) findViewById(R.id.tv_dev_nickname);
		tv_pm25_11 = (TextView) findViewById(R.id.tv_pm25_11);

		iv_cr = (ImageView) findViewById(R.id.iv_cr);
		battery = (ImageView) findViewById(R.id.battery_charging);
	}

	private void initMenuData() {
		// TODO Auto-generated method stub
		List<MenuEntity> menus = new ArrayList<MenuEntity>();
		MenuEntity menu = new MenuEntity();
		menu.setMenu_key("clock");
		menu.setMenu_name("闹钟");
		menu.setMenu_icon(R.drawable.ic_menu_clock);
		menus.add(menu);

		menu = new MenuEntity();
		menu.setMenu_key("record");
		menu.setMenu_name("语音");
		menu.setMenu_icon(R.drawable.ic_menu_record);
		menus.add(menu);

		menu = new MenuEntity();
		menu.setMenu_key("volume");
		menu.setMenu_name("音量");
		menu.setMenu_icon(R.drawable.ic_menu_volume);
		menus.add(menu);
		
		menu = new MenuEntity();
		menu.setMenu_key("control");
		menu.setMenu_name("控制");
		menu.setMenu_icon(R.drawable.ic_menu_share);
		menus.add(menu);
		
		menu = new MenuEntity();
		menu.setMenu_key("cycle");
		menu.setMenu_name("自控");
		menu.setMenu_icon(R.drawable.ic_menu_share);
		menus.add(menu);

		menu = new MenuEntity();
		menu.setMenu_key("edit");
		menu.setMenu_name("自定义");
		menu.setMenu_icon(R.drawable.ic_menu_edit);
		menus.add(menu);
		
		menu = new MenuEntity();
		menu.setMenu_key("Auth");
		menu.setMenu_name("授权");
		menu.setMenu_icon(R.drawable.ic_menu_auth);
		menus.add(menu);

	    menu = new MenuEntity();
		menu.setMenu_key("share");
		menu.setMenu_name("分享");
		menu.setMenu_icon(R.drawable.ic_menu_share);
		menus.add(menu);
		prepareOptionsMenu(menus);
	}

	private void setData() {
		tv_dev_nickname.setText(mDevice.nickName);

		if (mAirInfo != null) {
			//pm25
			tv_pm25_11.setText(mAirInfo.getAirValue()+"");
			
			int temp = mAirInfo.getTem();
			if(temp >128){
				temp = temp - 256;
			}
			tv_inside_temper_value.setText( temp+ "");
			tv_inside_humidity_value.setText(mAirInfo.getHum() + "");
			
			int dian = mAirInfo.getPosition();
			if(dian >= 128){
				battery.setBackgroundResource(R.anim.battery_charging_anim);
				animBatteryCharging = (AnimationDrawable) battery.getBackground();
				animBatteryCharging.stop();
				animBatteryCharging.start();
			}else{
				if(animBatteryCharging != null){
					animBatteryCharging.stop();
				}
				battery.setBackgroundColor(Color.parseColor("#00000000"));

				if(dian >= 85){
					battery.setImageResource(R.drawable.ic_battery_4);
				}
				if(25<=dian&&dian<85){
					battery.setImageResource(R.drawable.ic_battery_3);
				}
				if(25<=dian&&dian<55){
					battery.setImageResource(R.drawable.ic_battery_2);
				}
				if(5<=dian&&dian<25){
					battery.setImageResource(R.drawable.ic_battery_1);
				}
				if(dian<5){
					battery.setImageResource(R.drawable.ic_battery_0);
				}
			}
		}

		Calendar curCalendar = Calendar.getInstance();
		int curY = curCalendar.get(Calendar.YEAR);
		int curM = curCalendar.get(Calendar.MONTH) + 1;
		int curD = curCalendar.get(Calendar.DAY_OF_MONTH);
		tv_inside_date.setText(curY + "/" + String.format("%02d", curM) + "/" + String.format("%02d", curD));

		int curH = curCalendar.get(Calendar.HOUR_OF_DAY);
		int curMin = curCalendar.get(Calendar.MINUTE);
		tv_inside_time.setText(String.format("%02d", curH) + ":" + String.format("%02d", curMin));
	}
	

	/**************
	 * 定时器
	 */
	private void startMxRefreshTimer() {
		mHandler.postDelayed(task, 4500);
	}

	private void stopMxRefreshTimer() {
		canRefrash = false;
		mHandler.removeCallbacks(task);
	}

	private Runnable task = new Runnable() {
		public void run() {
			if (canRefrash && isActivity) {
				queryMXData();
			}
		}
	};

	private void queryMXData() {
		LogUtil.i("queryMXData()");
		BsOperationHub.instance().queryDevStatus(mDevice.devId, "beiang_status", new ReqCbk<RspMsgBase>() {
			@Override
			public void onSuccess(RspMsgBase rspData) {
				// TODO Auto-generated method stub
				if (canRefrash) {
					if (rspData.isSuccess()) {
						AirInfo airInfo = CurrentDevice.instance().queryDevice.airInfo;
						if (airInfo != null) {
							mAirInfo = airInfo;
							setData();
						}
					}
					canRefrash = true;
					startMxRefreshTimer();
				}
			}

			@Override
			public void onFailure(ErrorObject err) {
				// TODO Auto-generated method stub
				if (canRefrash) {
					canRefrash = true;
					startMxRefreshTimer();
				}
			}

		});
	}
	
	
	private void transportCloud(final boolean isAuto, byte[] data) {
		showDialog("请稍后");
		LogUtil.i(data);
		BsOperationHub.instance().sendCtrlCmd(mDevice.devId, data, new ReqCbk<RspMsgBase>() {
			@Override
			public void onSuccess(RspMsgBase rspData) {
				// TODO Auto-generated method stub
				hideDialog();
				if (rspData.isSuccess()) {
					CommandPair.RspCommand rsp = (CommandPair.RspCommand) rspData;
					if (rsp.reply != null) {
						if(isAuto){
							Toast.show(mActivity, "自动控制");
						}else{
							Toast.show(mActivity, "手动控制");
						}
						return;
					}
				}
				Toast.show(mActivity, "设置失败");
			}

			@Override
			public void onFailure(ErrorObject err) {
				// TODO Auto-generated method stub
				hideDialog();
				Toast.show(mActivity, "设置失败");
			}
		});
	}
	
	private void qDecode() {
		String codeStr = "";
		if (mDevice != null) {
			codeStr = API.AppAuthUrl + "id=" + mDevice.devId + "&auth=" + 0;
			launchSearch(codeStr);
		}
	}

	/***
	 * 生成二维码
	 * 
	 * @param text
	 */
	/*private void launchSearch(String text) {
		Intent intent = new Intent();
		intent.setClass(mActivity, EncodeActivity.class);
		intent.setAction(Intents.Encode.ACTION);
		intent.putExtra(Intents.Encode.TYPE, Contents.Type.TEXT);
		intent.putExtra(Intents.Encode.DATA, text);
		intent.putExtra(Intents.Encode.FORMAT, BarcodeFormat.QR_CODE.toString());
		startActivity(intent);
	}*/
	private void launchSearch(String text) {
		Intent intent = new Intent();
		intent.setClass(mActivity, EncodeActivity.class);
		intent.putExtra(EncodeActivity.CONTENT, text);
		startActivity(intent);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		canRefrash = false;
		stopMxRefreshTimer();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		MenuEntity ety = (MenuEntity) parent.getAdapter().getItem(position);
		if ("Auth".equals(ety.getMenu_key())) {
			if (!"owner".equals(mDevice.role)) {
				Toast.show(mActivity, "不能获取此种权限");
				return;
			}
			qDecode();
		}
		if ("cycle".equals(ety.getMenu_key())) {
			AlertDialog.show(mActivity, R.drawable.ic_top_header, "自动控制", true, new AlertDialogCallBack() {
				
				@Override
				public void onRight() {
					// TODO Auto-generated method stub
					OPEntity opEntity = new OPEntity();
					opEntity.setDevType(Device.DT_Airdog);
					opEntity.setSubDevType(SubDevice.SDT_Airdog);
					
					AirdogCycle cycle = new AirdogCycle();
					cycle.setIsCylce(0);
					opEntity.setbEntity(cycle);
					
					opEntity.setCommand(Command.WTITE);
					opEntity.setFunction(AirdogFC.CYCLE.getValue());
					transportCloud(true, EParse.parseparseOPEntity(opEntity));
				}
				
				@Override
				public void onLeft() {
					// TODO Auto-generated method stub
					OPEntity opEntity = new OPEntity();
					opEntity.setDevType(Device.DT_Airdog);
					opEntity.setSubDevType(SubDevice.SDT_Airdog);
					
					AirdogCycle cycle = new AirdogCycle();
					cycle.setIsCylce(1);
					opEntity.setbEntity(cycle);
					
					opEntity.setCommand(Command.WTITE);
					opEntity.setFunction(AirdogFC.CYCLE.getValue());
					transportCloud(false, EParse.parseparseOPEntity(opEntity));
					
				}
			});
		}
	}

	/***
	 * OnClickListener
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}

	protected void onDestroy() {
		// TODO Auto-generated method stub
		isActivity = false;
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode != RESULT_OK){
			return;
		}
		if(requestCode == 200){
			tv_dev_nickname.setText(mDevice.nickName);
		}
	}

}
