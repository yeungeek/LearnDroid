package com.yeungeek.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class HashTest {

	static HashMap<ScaleStat, ArrayList<Integer>> statInfo = new HashMap<ScaleStat, ArrayList<Integer>>(
			1);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ScaleStat stat = new ScaleStat();
		stat.setId(1);
		stat.setPatientId(1);
		stat.setPatientName("test");
		stat.setScaleNum(2);
		stat.setTestNo("201201120");
		stat.setUpdateTime(System.currentTimeMillis());
		stat.setQuestionAmount(100);

		ArrayList<Integer> ints = new ArrayList<Integer>();
		ints.add(13);
		statInfo.put(stat, ints);

		ScaleStat info;
		ArrayList<Integer> intT = null;
		Iterator<ScaleStat> iteator = statInfo.keySet().iterator();
		if (iteator.hasNext()) {
			info = iteator.next();
			intT = statInfo.get(info);
		}

		System.out.println(intT);

	}

}
