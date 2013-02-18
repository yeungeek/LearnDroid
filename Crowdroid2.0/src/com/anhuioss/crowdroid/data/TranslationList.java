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

public class TranslationList {

	private static final String FILE_NAME = "auto_translation.xml";

	private ArrayList<TranslationData> list;

	private Context context;

	// ----------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param Context
	 *            context
	 */
	// ----------------------------------------------------------------------------
	public TranslationList(Context context) {
		this.context = context;
		readXml();
	}

	// ----------------------------------------------------------------------------
	/**
	 * Add translation data
	 * 
	 * @param TranslationData
	 *            data
	 */
	// ----------------------------------------------------------------------------
	public void addTranslationData(TranslationData data) {

		boolean isExist = true;

		String service = data.getService();
		String userName = data.getUserName();

		for (TranslationData listItem : list) {
			String listItemService = listItem.getService();
			String listItemUid = listItem.getUserName();
			if (listItemService != null && listItemUid != null
					&& listItemService.equals(service)
					&& listItemUid.equals(userName)) {
				isExist = false;
				break;
			}
		}

		if (isExist) {
			list.add(data);
			saveXml();
		}

	}

	// ----------------------------------------------------------------------------
	/**
	 * Remove translation data
	 * 
	 * @param TranslationData
	 *            data
	 */
	// ----------------------------------------------------------------------------
	public void removeTranslationData(TranslationData data) {
		list.remove(data);
		saveXml();
	}

	// ----------------------------------------------------------------------------
	/**
	 * Get all translation data
	 * 
	 * @return ArrayList<TranslationData> list
	 */
	// ----------------------------------------------------------------------------
	public ArrayList<TranslationData> getAllTranslationData() {
		return list;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Get translation data
	 * 
	 * @param String
	 *            service
	 * @return ArrayList<TranslationData> extractedList
	 */
	// ----------------------------------------------------------------------------
	public ArrayList<TranslationData> getTranslationData(String service) {
		ArrayList<TranslationData> extractedList = new ArrayList<TranslationData>();
		for (TranslationData data : list) {
			if (data.getService().equals(service)) {
				extractedList.add(data);
			}
		}
		return extractedList;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Save xml
	 */
	// ----------------------------------------------------------------------------
	public synchronized void saveXml() {
		try {
			FileOutputStream fos = context.openFileOutput(FILE_NAME,
					Context.MODE_PRIVATE);
			PrintWriter pw = new PrintWriter(fos);
			StringBuffer buffer = new StringBuffer();
			buffer.append("<?xml version='1.0' encoding='utf-8' ?>");
			buffer.append("<translation_list>");

			for (TranslationData data : list) {
				buffer.append("<translation>");
				buffer.append(data.getTranslationXml());
				buffer.append("</translation>");
			}

			buffer.append("</translation_list>");
			pw.write(buffer.toString());
			pw.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ----------------------------------------------------------------------------
	/**
	 * Read xml
	 */
	// ----------------------------------------------------------------------------
	public void readXml() {
		FileInputStream fis = null;
		try {
			fis = context.openFileInput(FILE_NAME);
			list = parseTranslation(fis);
		} catch (Exception e) {
			list = new ArrayList<TranslationData>();
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

	// ----------------------------------------------------------------------------
	/**
	 * Parse translation
	 * 
	 * @param InputStream
	 *            is
	 * @return ArrayList<TranslationData> translationList
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	// ----------------------------------------------------------------------------
	private ArrayList<TranslationData> parseTranslation(InputStream is)
			throws XmlPullParserException, IOException {

		ArrayList<TranslationData> translationList = new ArrayList<TranslationData>();

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xmlPullParser = factory.newPullParser();
		xmlPullParser.setInput(is, "UTF-8");

		while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
			if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
				if (xmlPullParser.getName().equals("translation")) {
					TranslationData info = new TranslationData();

					while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
						if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
							if (xmlPullParser.getName().equals("user_name")) {
								String userName = xmlPullParser.nextText();
								info.setUserName(userName);
								continue;
							}
							if (xmlPullParser.getName().equals("service")) {
								String service = xmlPullParser.nextText();
								info.setService(service);
								continue;
							}
							if (xmlPullParser.getName().equals("from")) {
								String from = xmlPullParser.nextText();
								info.setFrom(from);
								continue;
							}
							if (xmlPullParser.getName().equals("to")) {
								String to = xmlPullParser.nextText();
								info.setTo(to);
								continue;
							}
							if (xmlPullParser.getName().equals("engine")) {
								String engine = xmlPullParser.nextText();
								info.setEngine(engine);
								continue;
							}
						}
						if (xmlPullParser.getEventType() == XmlPullParser.END_TAG) {
							if (xmlPullParser.getName().equals("translation")) {
								translationList.add(info);
								break;
							}
						}
					}
				}
			}
		}
		return translationList;
	}
}
