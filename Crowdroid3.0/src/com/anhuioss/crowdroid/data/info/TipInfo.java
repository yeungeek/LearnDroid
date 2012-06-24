package com.anhuioss.crowdroid.data.info;

public class TipInfo {
	
	private String text = null;
	
	private int minVersion = 0;
	
	private int maxVersion = 0;
	
	private String versionType = null;
	
	private String versionName = null;
	
	private String versionCode = null; 
	
	private String downloadUrl = null;
	
	private String size = null;
	
	private String resolution = null;
	
	public void setResolution(String resolution){
		this.resolution = resolution;
	}
	
	public String getResolution(){
		return resolution;
	}
	
	public String getThemeSize(){
		return size;
	}
	
	public void setThemeSize(String size){
		this.size = size;
	}
	
	public void setDownLoadUrl(String downloadUrl){
		this.downloadUrl = downloadUrl;
	}
	
	public String getDownLoadUrl(){
		return downloadUrl;
	}
	
	public void setVersionCode(String versionCode){
		this.versionCode = versionCode;
	}
	
	public String getVersionCode(){
		return versionCode;
	}
	
	public void setVersionName(String versionName){
		this.versionName = versionName;
	}
	
	public String getVersionName(){
		return versionName;
	}
	
	public void setVersionType(String versionType){
		this.versionType = versionType;
	}
	
	public String getVersionType(){
		return versionType;
	}
	
	//----------------------------------------------------------
	/**
	 * Set Text
	 * @param text
	 */
	//----------------------------------------------------------
	public void setText(String text) {
		this.text = text;
	}
	
	//----------------------------------------------------------
	/**
	 * Get Text
	 * @return
	 */
	//----------------------------------------------------------
	public String getText() {
		return text;
	}
	
	//----------------------------------------------------------
	/**
	 * Set Min-Version
	 * @param minVersion
	 */
	//----------------------------------------------------------
	public void setMinVersion(String minVersion) {
		try {
			int version = Integer.valueOf(minVersion);
			this.minVersion = version;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	//----------------------------------------------------------
	/**
	 * Get Min-Version
	 * @return
	 */
	//----------------------------------------------------------
	public int getMinVersion() {
		return minVersion;
	}
	
	//----------------------------------------------------------
	/**
	 * Set Max-Version
	 * @param maxVersion
	 */
	//----------------------------------------------------------
	public void setMaxVersion(String maxVersion) {
		try {
			int version = Integer.valueOf(maxVersion);
			this.maxVersion = version;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	//----------------------------------------------------------
	/**
	 * Get Max-Version
	 * @return
	 */
	//----------------------------------------------------------
	public int getMaxVersion() {
		return maxVersion;
	}
	
	//----------------------------------------------------------
	/**
	 * Check Tip
	 * @return true/false
	 */
	//----------------------------------------------------------
	public boolean isShow(String versionString) {
		try {
			int version = Integer.valueOf(versionString);
			if(minVersion <= version && version <= maxVersion) {
				return true;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return false;
	}

	
	
}
