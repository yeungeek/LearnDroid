package com.anhuioss.crowdroid.data.info;

import java.io.Serializable;

public class POIinfo implements Serializable {

	// "poiid": "B2094654D069A6F4419C",
	// "title": "三个贵州人(中关村店)",
	// "address": "北四环西路58号理想国际大厦202-205",
	// "lon": "116.30999",
	// "lat": "39.98435",
	// "category": "83",
	// "city": "0010",
	// "province": null,
	// "country": null,
	// "url": "",
	// "phone": "010-82607678",
	// "postcode": "100000",
	// "weibo_id": "0",
	// "categorys": "64 69 83",
	// "category_name": "云贵菜",
	// "icon": "http://u1.sinaimg.cn/upload/2012/03/23/1/xysh.png",
	// "checkin_num": 0,
	// "checkin_user_num": "0",
	// "tip_num": 0,
	// "photo_num": 0,
	// "todo_num": 0,
	// "distance": 70
	private static final long serialVersionUID = 201210311521L;

	private String poiid;

	private String title;

	private String address;

	private String lon;

	private String lat;

	private String category;

	private String city;

	private String province;

	private String country;

	private String url;

	private String phone;

	private String postcode;

	private String categorys;

	private String categorys_name;

	private String icon;

	private int checkin_num;

	private int checkin_user_num;

	private int tip_num;

	private int photo_num;

	private int todo_num;

	private int distance;

	private String checkin_time;

	public void setpoiid(String poiid) {
		this.poiid = poiid;
	}

	public void settitle(String title) {
		this.title = title;
	}

	public void setcoordinate(String lon, String lat) {
		this.lon = lon;
		this.lat = lat;
	}

	public void setcityare(String city, String province, String country) {
		this.city = city;
		this.province = province;
		this.country = country;

	}

	public void setcityare(String city, String province) {
		this.city = city;
		this.province = province;

	}

	public void setcategory(String category) {
		this.category = category;

	}

	public void setaddress(String address) {
		this.address = address;

	}

	public void seturl(String address) {
		this.url = address;

	}

	public void setphone(String address) {
		this.phone = address;

	}

	public void setpostcode(String address) {
		this.postcode = address;

	}

	public void setcategorys(String address) {
		this.category = address;

	}

	public void setcategorys_name(String address) {
		this.categorys_name = address;

	}

	public void seticon(String address) {
		this.icon = address;

	}

	public void setcheckin_num(int num) {
		this.checkin_num = num;

	}

	public void setcheckin_time(String num) {
		this.checkin_time = num;

	}

	public void setcheckin_user_num(int num) {
		this.checkin_user_num = num;

	}

	public void settip_num(int num) {
		this.tip_num = num;

	}

	public void setphoto_num(int num) {
		this.photo_num = num;

	}

	public void settodo_num(int num) {
		this.todo_num = num;

	}

	public void setdistance(int num) {
		this.distance = num;

	}

	public String getpoiid() {
		return poiid;
	}

	public String gettitle() {
		return title;
	}

	public String getcoordinate() {
		return lon + "," + lat;
	}

	public String getaddress() {
		return address;
	}

	public String getcategory() {
		return category;
	}

	public String getcity() {
		return city;
	}

	public String getprovince() {
		return province;
	}

	public String getcountry() {
		return country;
	}

	public String geturl() {
		return url;
	}

	public String getphone() {
		return phone;
	}

	public String getpostcode() {
		return postcode;
	}

	public String getcategorys() {
		return categorys;
	}

	public String getcategorys_name() {
		return categorys_name;
	}

	public String geticon() {
		return icon;
	}

	public int getcheckin_num() {
		return checkin_num;
	}

	public int getcheckin_user_num() {
		return checkin_user_num;
	}

	public int gettip_num() {
		return tip_num;
	}

	public int gettodo_num() {
		return todo_num;
	}

	public int getphoto_num() {
		return photo_num;
	}

	public int getdistance() {
		return distance;
	}

}
