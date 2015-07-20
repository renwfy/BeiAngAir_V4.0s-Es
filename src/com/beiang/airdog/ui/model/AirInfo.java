package com.beiang.airdog.ui.model;

import com.broadlink.beiangtecheair.parse.lib.EairInfo;

/***
 * 原始的模型不能添加属性
 * 
 * @author LSD
 * 
 */
public class AirInfo extends EairInfo {
	private int airTvoc;// TVOC
	private int signal;// 信号强度
	private int clean;// 清洁复位

	public int getAirTvoc() {
		return airTvoc;
	}

	public void setAirTvoc(int airTvoc) {
		this.airTvoc = airTvoc;
	}

	public int getSignal() {
		return signal;
	}

	public void setSignal(int signal) {
		this.signal = signal;
	}

	public int getClean() {
		return clean;
	}

	public void setClean(int clean) {
		this.clean = clean;
	}

}
