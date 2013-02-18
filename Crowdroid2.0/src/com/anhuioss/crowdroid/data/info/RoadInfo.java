package com.anhuioss.crowdroid.data.info;

public class RoadInfo {
	// "type": "0",
	// "road_number": "2",
	// "distance": "16601",
	// "drive_time": "36",
	// "drive_coordinates": "116.22106,39.90652|116.22106,39.90594",
	// "pass_coordinates": "",
	// "begin_coordinate": "116.22106,39.90652",
	// "end_coordinate": "116.27505,40.00236",
	// "extension_info": "0",
	// "roads": [
	// {
	// "rid": "1",
	// "road_name": "银河大街",
	// "coordinates": "116.22106,39.90652|116.22106,39.90594",
	// "begin_coordinate": "116.22106,39.90652",
	// "end_coordinate": "116.22106,39.90594",
	// "road_length": "64",
	// "road_sign": "行驶时间;等级方向;坐标点;道路名称;",
	// "action": "右转",
	// "run_time": "1",
	// "grade": "主要道路",
	// "direction": "南",
	// "assist_info": "",
	// "navigation_tag": ""
	// },

	private String rid;

	private String road_name;

	private String coordinates;

	private String begin_coordinate;

	private String end_coordinate;

	private String road_length;

	private String road_sign;

	private String cation;

	private String run_time;

	private String grade;

	private String direction;

	private String Assist_info;

	private String navigation_tag;

	public void setrid(String rid) {
		this.rid = rid;
	}

	public void setroad_name(String name) {
		this.road_name = name;
	}

	public void setcoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	public void setbegin_coordinate(String coordinate) {
		this.begin_coordinate = coordinate;
	}

	public void setend_coordinate(String coordinate) {
		this.end_coordinate = coordinate;
	}

	public void setroad_length(String length) {
		this.road_length = length;
	}

	public void setroad_sign(String sign) {
		this.road_sign = sign;
	}

	public void setrun_time(String time) {
		this.run_time = time;
	}

	public void setgrade(String grade) {
		this.grade = grade;
	}

	public void setdirection(String direction) {
		this.direction = direction;
	}

	public void setAssist_info(String info) {
		this.Assist_info = info;
	}

	public void setnacigation_tag(String tag) {
		this.navigation_tag = tag;
	}

	public String getrid() {
		return rid;
	}

	public String getroad_name() {
		return road_name;
	}

	public String getcoordinates() {
		return coordinates;
	}

	public String getbegin_coordinate() {
		return begin_coordinate;
	}

	public String getend_coordinate() {
		return end_coordinate;
	}

	public String getroad_length() {
		return road_length;
	}

	public String getroad_sign() {
		return road_sign;
	}

	public String getrun_time() {
		return run_time;
	}

	public String getgrade() {
		return direction;
	}

	public String getdirection() {
		return direction;
	}

	public String getAssist_info() {
		return Assist_info;
	}

	public String getnavigation_tag() {
		return navigation_tag;
	}
}
