package com.beiang.airdog.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.beiang.airdog.constant.AirConstant;
import com.beiang.airdog.net.business.BsOperationHub;
import com.beiang.airdog.net.business.entity.CurrentDevice;
import com.beiang.airdog.net.business.entity.DevEntity;
import com.beiang.airdog.net.business.homer.CommandPair;
import com.beiang.airdog.net.httpcloud.aync.abs.BaseMsg.RspMsgBase;
import com.beiang.airdog.net.httpcloud.aync.abs.ReqCbk;
import com.beiang.airdog.ui.base.BaseMultiPartActivity;
import com.beiang.airdog.ui.model.AirInfo;
import com.beiang.airdog.utils.EParse;
import com.beiang.airdog.view.WeatherView;
import com.broadlink.beiangair.R;

public class DeviceTestActivity extends BaseMultiPartActivity implements OnClickListener {
	private DevEntity mDevice;
	private AirInfo mAirInfo;
	private WeatherView weather_layout;
	ImageView iv_power;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		mDevice = CurrentDevice.instance().curDevice;
		mAirInfo = mDevice.airInfo;
		if(mAirInfo == null){
			mAirInfo = new AirInfo();
		}

		initView();
		setData();
		weather_layout.load();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		queryMXData();
	}

	private void initView() {
		weather_layout = (WeatherView) findViewById(R.id.weather_layout);

		iv_power = (ImageView) findViewById(R.id.iv_power);
		iv_power.setOnClickListener(this);
		iv_power.setTag(0);
	}

	private void setData() {
		if (mAirInfo != null) {
			// 设置按钮状态
			if (mAirInfo.getOnoff() == AirConstant.UNABLE) {
				// 关机状态下
				iv_power.setImageResource(R.drawable.ic_power_off_selector);
			} else {
				// 开机状态下
				iv_power.setImageResource(R.drawable.ic_power_on_selector);
			}
		}

	}

	private void queryMXData() {
		BsOperationHub.instance().queryDevStatus(mDevice.devId, "beiang_status", new ReqCbk<RspMsgBase>() {
			@Override
			public void onSuccess(RspMsgBase rspData) {
				// TODO Auto-generated method stub
				if (rspData.isSuccess()) {
					AirInfo airInfo = CurrentDevice.instance().queryDevice.airInfo;
					if (airInfo != null) {
						mAirInfo = airInfo;
						setData();
					}
				}
			}

			@Override
			public void onFailure(ErrorObject err) {
				// TODO Auto-generated method stub
			}

		});
	}

	private void controlBeiAngAir(byte[] data) {
		setData();
		BsOperationHub.instance().sendCtrlCmd(mDevice.devId, data, new ReqCbk<RspMsgBase>() {
			@Override
			public void onSuccess(RspMsgBase rspData) {
				// TODO Auto-generated method stub
				if (rspData.isSuccess()) {
					CommandPair.RspCommand rsp = (CommandPair.RspCommand) rspData;
					if (rsp.reply != null) {
						AirInfo airInfo = EParse.parseEairByte(rsp.reply);
						mAirInfo = airInfo;
						setData();
					}
				}
			}

			@Override
			public void onFailure(ErrorObject err) {
				// TODO Auto-generated method stub
			}
		});
	}

	/***
	 * OnClickListener
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mAirInfo == null) {
			return;
		}
		switch (v.getId()) {
		case R.id.iv_power:
			// 开关机时风速1档
			mAirInfo.setPosition(mAirInfo.getPosition() == 0 ? 1 : mAirInfo.getPosition());
			if (mAirInfo.getOnoff() == AirConstant.ENABLE) {
				mAirInfo.setOnoff(AirConstant.UNABLE);
			} else {
				mAirInfo.setOnoff(AirConstant.ENABLE);
			}
			controlBeiAngAir(EParse.parseEairInfo(mAirInfo));
			break;
		default:
			break;
		}
	}
}
