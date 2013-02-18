package com.anhuioss.crowdroid.data.info;

import java.io.Serializable;

public class BroadcastInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 201206120937L;

	public String adId = "";

	public String adType = "";

	public String adTitle = "";

	public String adContent = "";

	public String adPicUrl = "";

	public String adStartTime = "";

	public String adEndTime = "";

	public String adScheduleTime = "";

	public void setAdsId(String adId) {
		this.adId = adId;
	}

	public void setAdsType(String adType) {
		this.adType = adType;
	}

	public void setAdsTitle(String adTitle) {
		this.adTitle = adTitle;
	}

	public void setAdsContent(String adContent) {
		this.adContent = adContent;
	}

	public void setAdsPicUrl(String adPicUrl) {
		this.adPicUrl = adPicUrl;
	}

	public void setAdsStartTime(String adStartTime) {
		this.adStartTime = adStartTime;
	}

	public void setAdsEndTime(String adEndTime) {
		this.adEndTime = adEndTime;
	}

	public void setAdsScheduleTime(String adScheduleTime) {
		this.adScheduleTime = adScheduleTime;
	}

	public String getAdsId() {
		return adId;
	}

	public String getAdsType() {
		return adType;
	}

	public String getAdsTitle() {
		return adTitle;
	}

	public String getAdsContent() {
		return adContent;
	}

	public String getAdsPicUrl() {
		return adPicUrl;
	}

	public String getAdsStartTime() {
		return adStartTime;
	}

	public String getAdsEndTime() {
		return adEndTime;
	}

	public String getAdsScheduleTime() {
		return adScheduleTime;
	}
}
