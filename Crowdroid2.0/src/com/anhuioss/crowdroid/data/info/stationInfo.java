package com.anhuioss.crowdroid.data.info;

public class stationInfo {
	// "{
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
	// "

	private String name;

	private String longitude;

	private String latitude;

	private String district;

	private String station_info;

	private String address;

	private String telephone;

	public String getname() {
		return name;
	}

	public String getlongitude() {
		return longitude;
	}

	public String getlatitude() {
		return latitude;
	}

	public String getdistrict() {
		return district;
	}

	public String getstation_info() {
		return station_info;
	}

	public String getaddress() {
		return address;
	}

	public void setname(String name) {
		this.name = name;
	}

	public void setlongitude(String longitude) {
		this.longitude = longitude;
	}

	public void setlatitude(String latitude) {
		this.latitude = latitude;
	}

	public void setdistrict(String district) {
		this.district = district;
	}

	public void setstation_info(String station_info) {
		this.station_info = station_info;
	}

	public void setaddress(String address) {
		this.address = address;
	}

}
