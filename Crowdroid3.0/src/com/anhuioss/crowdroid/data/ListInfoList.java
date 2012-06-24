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

import com.anhuioss.crowdroid.data.info.ListInfo;

import android.content.Context;


public class ListInfoList {

	private static final String FILE_NAME = "list.xml";

	private Context context;

	private ArrayList<ListInfo> list;
	

	// ---------------------------------------------
	/**
	 * Constructor
	 * 
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	// ---------------------------------------------
	public ListInfoList(Context context){
		this.context = context;
		readXml();
	}
	
	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public void addList(ListInfo listInfo) {

		list.add(listInfo);
		saveXml();
		
		// Debug
		String s = null;
		s = s + "";
		
	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public void removeList(String id) {

		for(int i = 0; i < list.size(); i++){
			if(list.get(i).getId().equals(id)){
				list.remove(i);
			}
		}
		saveXml();
		
		// Debug
		String s = null;
		s = s + "";
		
	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public synchronized void saveXml() {

		// Save XML Contents To File(/data/data/com.anhuioss.crowdroid/files/list.xml)
		try {
			FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
			PrintWriter pw = new PrintWriter(fos);
			StringBuffer listBuffer = new StringBuffer(); 
			listBuffer.append("<?xml version='1.0' encoding='utf-8' ?>");
			listBuffer.append("<list_list>");
			// Prepare Accounts
			for(ListInfo listInfo : list){
				listBuffer.append(listInfo.getListXML());
			}
			listBuffer.append("</list_list>");
			pw.write(listBuffer.toString());
			pw.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	// -----------------------------------------------------------------------------
	/**
	 * Get Account Data By Service<br>
	 * @param service
	 * @return accountList
	 */
	// -----------------------------------------------------------------------------
	public ArrayList<ListInfo> getListsByUserName(String userName){
		
		ArrayList<ListInfo> listInfoList = new ArrayList<ListInfo>();
		for(ListInfo listInfo : list){
			if(listInfo.getMode().equals(userName)){
				listInfoList.add(listInfo);
			}
		}
		return listInfoList;
		
	}
	
	
	public ArrayList<ListInfo> getAllLists(){
		
		return list;
		
	}
	
	
	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	private void readXml(){
	
		FileInputStream fis = null;
		try {
			// Open File
			fis = context.openFileInput(FILE_NAME);
			// XML Parse And Create New Account List
			list = parserAccount(fis);
	
		} catch (Exception e) {
			list = new ArrayList<ListInfo>();
		} finally{
			try {
				if(fis != null) {
					fis.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	private static ArrayList<ListInfo> parserAccount(InputStream is)
			throws XmlPullParserException, IOException {

		ArrayList<ListInfo> listInfoList = new ArrayList<ListInfo>();
		ListInfo listInfo;

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xmlPullParser = factory.newPullParser();
		xmlPullParser.setInput(is, "UTF-8");

		// Start Parsing XML Data
		while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {

			if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {

				if (xmlPullParser.getName().equals("list")) {

					listInfo = new ListInfo();
					// -----------------------------------------------------------------------------
					/**
					 * 
					 */
					// -----------------------------------------------------------------------------
					// Read Account
					while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {

						if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {

							// Uid
							if (xmlPullParser.getName().equals("slug")) {
								xmlPullParser.next();
								listInfo.setSlug(xmlPullParser.getText());
								continue;
							}

							// Service
							if (xmlPullParser.getName().equals("description")) {
								xmlPullParser.next();
								listInfo.setDescription(xmlPullParser.getText());
								continue;
							}

							// API URL
							if (xmlPullParser.getName().equals("full_name")) {
								xmlPullParser.next();
								listInfo.setFullName(xmlPullParser.getText());
								continue;
							}

							// User Name
							if (xmlPullParser.getName().equals("name")) {
								xmlPullParser.next();
								listInfo.setName(xmlPullParser.getText());
								continue;
							}

							// Auth Type
							if (xmlPullParser.getName().equals("id")) {
								xmlPullParser.next();
								listInfo.setId(xmlPullParser.getText());
								continue;
							}
							
							// Auth Type
							if (xmlPullParser.getName().equals("mode")) {
								xmlPullParser.next();
								listInfo.setMode(xmlPullParser.getText());
								continue;
							}

						}

						// End Account
						if (xmlPullParser.getEventType() == XmlPullParser.END_TAG) {
							if (xmlPullParser.getName().equals("list")) {
								listInfoList.add(listInfo);
								break;
							}

						}

					}

				}

			}

		}

		return listInfoList;

	}

}
