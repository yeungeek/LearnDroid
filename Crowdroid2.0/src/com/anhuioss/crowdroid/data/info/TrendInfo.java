package com.anhuioss.crowdroid.data.info;

public class TrendInfo {

	private String name;

	private String num;

	private String hotword;

	private String trendId;

	private String woeid;

	private String country;

	// ----------------Set-------------------

	public void setName(String name) {
		this.name = name;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public void setHotword(String hotword) {
		this.hotword = hotword;
	}

	public void setTrendId(String trendId) {
		this.trendId = trendId;
	}

	public void setWoeid(String woeid) {
		this.woeid = woeid;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	// ----------------Get-------------------

	public String getName() {
		return name;
	}

	public String getNum() {
		return num;
	}

	public String getHotword() {
		return hotword;
	}

	public String getTrendId() {
		return trendId;
	}

	public String getWoeid() {
		return woeid;
	}

	public String getCountry() {
		return country;
	}

}
