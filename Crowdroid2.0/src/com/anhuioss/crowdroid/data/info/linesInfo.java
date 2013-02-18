package com.anhuioss.crowdroid.data.info;

import java.util.ArrayList;

public class linesInfo {

	// "seg_id": "1",
	// "name": "598路",
	// "distance": "4470米",
	// "after_len": "160米",
	// "stations_num": "7",
	// "stations": [
	// {

	private String seg_id;

	private String name;

	private String distance;

	private String after_len;

	private String stations_num;

	private ArrayList<stationInfo> stations;

	public String getseg_id() {
		return seg_id;
	}

	public String getname() {
		return name;
	}

	public String getdistance() {
		return distance;
	}

	public String getafter_len() {
		return after_len;
	}

	public String getstations_num() {
		return stations_num;
	}

	public ArrayList<stationInfo> getstations() {
		return stations;
	}

	public void setseg_id(String seg_id) {
		this.seg_id = seg_id;
	}

	public void setname(String name) {
		this.name = name;
	}

	public void setdistance(String distance) {
		this.distance = distance;
	}

	public void setafter_len(String after_len) {
		this.after_len = after_len;
	}

	public void setstations_num(String stations_num) {
		this.stations_num = stations_num;
	}

	public void setstations(ArrayList<stationInfo> stations) {
		this.stations = stations;
	}
}
