package com.anhuioss.crowdroid.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

public class KeywordFilterList {

	private static final String FILE_NAME = "filter_keyword.xml";

	private ArrayList<KeywordFilterData> list;

	private Context context;

	// ------------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	// ------------------------------------------------------------------------------------
	public KeywordFilterList(Context context) {
		this.context = context;
		readXml();
	}

	// ------------------------------------------------------------------------------------
	/**
	 * Add keyword
	 */
	// ------------------------------------------------------------------------------------
	public void addKeyword(KeywordFilterData keyword) {
		list.add(keyword);
		saveXml();
	}

	// ------------------------------------------------------------------------------------
	/**
	 * Remove keyword
	 */
	// ------------------------------------------------------------------------------------
	public void removeKeyword(KeywordFilterData keyword) {
		list.remove(keyword);
		saveXml();
	}

	// ------------------------------------------------------------------------------------
	/**
	 * Get all keywords
	 * 
	 * @return ArrayList<KeywordFilterData>
	 */
	// ------------------------------------------------------------------------------------
	public ArrayList<KeywordFilterData> getAllKeywords() {
		return list;
	}

	// ------------------------------------------------------------------------------------
	/**
	 * Save xml
	 */
	// ------------------------------------------------------------------------------------
	public synchronized void saveXml() {
		try {
			FileOutputStream fos = context.openFileOutput(FILE_NAME,
					Context.MODE_PRIVATE);
			PrintWriter pw = new PrintWriter(fos);
			StringBuffer buffer = new StringBuffer();
			buffer.append("<?xml version='1.0' encoding='utf-8' ?>");
			buffer.append("<filter_list>");
			for (KeywordFilterData data : list) {
				buffer.append("<filter>");
				buffer.append(data.getKeywordXml());
				buffer.append("</filter>");
			}
			buffer.append("</filter_list>");
			pw.write(buffer.toString());
			pw.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ------------------------------------------------------------------------------------
	/**
	 * Read xml
	 */
	// ------------------------------------------------------------------------------------
	public void readXml() {

		FileInputStream fis = null;
		try {
			// Open File
			fis = context.openFileInput(FILE_NAME);
			// XML Parse And Create New Keyword List
			list = parseKeyword(fis);

		} catch (Exception e) {
			list = new ArrayList<KeywordFilterData>();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// ------------------------------------------------------------------------------------
	/**
	 * Parse keyword
	 * 
	 * @param is
	 * @return ArrayList<KeywordFilterData>
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	// ------------------------------------------------------------------------------------
	private ArrayList<KeywordFilterData> parseKeyword(InputStream is)
			throws XmlPullParserException, IOException {

		ArrayList<KeywordFilterData> keywordList = new ArrayList<KeywordFilterData>();

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xmlPullParser = factory.newPullParser();
		xmlPullParser.setInput(is, "UTF-8");
		int eventType = xmlPullParser.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			String nodeName = xmlPullParser.getName();
			if (eventType == XmlPullParser.START_TAG) {
				if ("keyword".equals(nodeName)) {
					KeywordFilterData info = new KeywordFilterData();
					String word = xmlPullParser.nextText();
					info.setKeyword(word);
					keywordList.add(info);
				}

			}

			eventType = xmlPullParser.next();
		}

		return keywordList;
	}
}