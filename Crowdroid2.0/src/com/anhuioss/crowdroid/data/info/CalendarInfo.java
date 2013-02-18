package com.anhuioss.crowdroid.data.info;

import java.io.Serializable;

public class CalendarInfo implements Serializable {

	private static final long serialVersionUID = 201206261425L;

	private String description;
	private String color;
	private String name;
	private String screenName;
	private String id;

	private String message;
	private String type;
	private String connectUsers;
	private String title;
	private String startTime;
	private String endTime;
	private String messageId;
	private String userId;
	private String index;

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setConnectUsers(String connectUsers) {
		this.connectUsers = connectUsers;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String gerDescription() {
		return description;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getColor() {
		return color;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String gerName() {
		return name;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String gerID() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	public String getType() {
		return type;
	}

	public String getConnectUsers() {
		return connectUsers;
	}

	public String getTitle() {
		return title;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public String getMessageId() {
		return messageId;
	}

	public String getUserId() {
		return userId;
	}

	public String getIndex() {
		return index;
	}

	public String getScreenName() {
		return screenName;
	}
}
