package com.itcast.logic;

import java.util.HashMap;

public class Task {
	private int taskID;
	private HashMap taskParam;

	public int getTaskID() {
		return taskID;
	}

	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}

	public HashMap getTaskParam() {
		return taskParam;
	}

	public void setTaskParam(HashMap taskParam) {
		this.taskParam = taskParam;
	}

	public Task(int tsid, HashMap hm) {
		this.taskID = tsid;
		this.taskParam = hm;
	}
}
