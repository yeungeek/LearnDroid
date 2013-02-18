package com.anhuioss.crowdroid.data.info;

import java.util.ArrayList;

public class busrouteInfo {

	private String city;

	private String begin_pid;

	private String begin_coordinate;

	private String end_pid;

	private String end_coordinate;

	private String extension_info;

	private ArrayList<transfersInfo> transfers;

	private int total_number;

	public String getcity() {
		return city;
	}

	public String getbegin_coordinate() {
		return begin_coordinate;
	}

	public String getend_coordinate() {
		return end_coordinate;
	}

	public String getextension_info() {
		return extension_info;
	}

	public ArrayList<transfersInfo> gettransfers() {
		return transfers;
	}

	public int gettotal_number() {
		return total_number;
	}

	public void setcity(String city) {
		this.city = city;
	}

	public void setbegin_coordinate(String begin_coordinate) {
		this.begin_coordinate = begin_coordinate;
	}

	public void setend_coordinate(String end_coordinate) {
		this.end_coordinate = end_coordinate;
	}

	public void setextension_info(String extension_info) {
		this.extension_info = extension_info;
	}

	public void settransfers(ArrayList<transfersInfo> transfers) {
		this.transfers = transfers;
	}

	public void settotal_number(int total_number) {
		this.total_number = total_number;
	}
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
