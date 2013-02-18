package com.anhuioss.crowdroid.data.info;

public class ColumnInfo {
	private String userId = "";

	private String createdTime = "";

	private String columnId = "";

	private String columnName = "";

	private String columnDesc = "";

	private String columnIdStr = "";

	private String userIdStr = "";

	private String createdTimeStr = "";

	public ColumnInfo() {

	}

	public void setuserId(String userid) {
		this.userId = userid;
	}

	public void setcreatedTime(String time) {
		this.createdTime = time;
	}

	public void setcolumnId(String id) {
		this.columnId = id;
	}

	public void setcolumnName(String name) {
		this.columnName = name;
	}

	public void setcolumnDesc(String desc) {
		this.columnDesc = desc;
	}

	public String getuserId() {
		return userId;
	}

	public String getcreatedTime() {
		return createdTime;
	}

	public String getcolumnId() {
		return columnId;
	}

	public String getcolumnName() {
		return columnName;
	}

	public String getcolumnDesc() {
		return columnDesc;
	}
	// "userId": "6082865741971470649",
	// "createdTime": "1335419069583",
	// "columnId": "-6133891146838855639",
	// "columnName": "物理定律",
	// "columnDesc": "不以人类意志为转移的物理定律",
	// "columnIdStr": "-6133891146838855639",
	// "userIdStr": "6082865741971470649",
	// "createdTimeStr": "1335419069583"

}
