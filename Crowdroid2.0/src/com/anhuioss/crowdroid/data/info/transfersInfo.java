package com.anhuioss.crowdroid.data.info;

import java.util.ArrayList;

public class transfersInfo {

	// {
	// "type": "0",
	// "city": "0010",
	// "begin_pid": "P010A00CHR9",
	// "begin_coordinate": "116.22106,39.90652",
	// "end_pid": "P010A00CWWJ",
	// "end_coordinate": "116.27505,40.00236",
	// "extension_info": "0",
	// "transfers": [
	// {
	// "result_id": "1",
	// "distance": "18534米",
	// "expense": "0",
	// "before_len": "160米",
	// "after_len": "820米",
	// "nav_count": "2",
	// "drive_coordinates": "116.22106,39.90652|116.22106,39.90594",

	private String result_id;

	private String distance;

	private String before_len;

	private String after_len;

	private String nav_count;

	private String drive_coordinates;

	private ArrayList<linesInfo> lines;

	public String getresult_id() {
		return result_id;
	}

	public String getdistance() {
		return distance;
	}

	public String getbefore_len() {
		return before_len;
	}

	public String getafter_len() {
		return after_len;
	}

	public String getnav_count() {
		return nav_count;
	}

	public String getdrive_coordinates() {
		return drive_coordinates;
	}

	public ArrayList<linesInfo> getlines() {
		return lines;
	}

	public void setresult_id(String result_id) {
		this.result_id = result_id;
	}

	public void setdistance(String distance) {
		this.distance = distance;
	}

	public void setbefore_len(String before_len) {
		this.before_len = before_len;
	}

	public void setafter_len(String after_len) {
		this.after_len = after_len;
	}

	public void setnav_count(String nav_count) {
		this.nav_count = nav_count;
	}

	public void setdrive_coordinates(String drive_coordinates) {
		this.drive_coordinates = drive_coordinates;
	}

	public void setlines(ArrayList<linesInfo> lines) {
		this.lines = lines;
	}

	// "lines": [
	// {
	// "seg_id": "1",
	// "name": "598路",
	// "distance": "4470米",
	// "after_len": "160米",
	// "stations_num": "7",
	// "stations": [
	// {
	// "name": "西直门",
	// "longitude": "116.21946",
	// "latitude": "116.21946",
	// "district": "110102",
	// "station_info": "地铁4号线(公益西桥-安河桥北)",
	// "address": "北京西直门",
	// "telephone": ""
	// },
	// ...
	// ]
	// },
	// ...
	// ]
	// },
	// ...
	// ],
	// "total_number": 6
	// }

}
