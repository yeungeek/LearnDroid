package com.renren.android.location;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.renren.android.RenRenData;

public class SaxParser extends DefaultHandler {

	public interface OnSaxParserListener {
		public abstract void start();

		public abstract void end();
	}

	private String mTag = null;
	private NearByResult mResult = null;
	private OnSaxParserListener mListener = null;

	public void setOnSaxParserListener(OnSaxParserListener listener) {
		mListener = listener;
	}

	public void startDocument() throws SAXException {
		super.startDocument();
		if (mListener != null) {
			mListener.start();
		}
		RenRenData.mNearByResults = new ArrayList<NearByResult>();
	}

	public void endDocument() throws SAXException {
		super.endDocument();
		if (mListener != null) {
			mListener.end();
		}
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if ("url".equals(qName)) {
			mResult = new NearByResult();
		}
		mTag = qName;
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		if ("url".equals(qName)) {
			RenRenData.mNearByResults.add(mResult);
			mResult = null;
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		String data = new String(ch, start, length);
		if (mTag != null) {
			if ("loc".equals(mTag)) {
				mResult.setLoc(data);
			} else if ("title".equals(mTag)) {
				mResult.setTitle(data);
			} else if ("address".equals(mTag)) {
				mResult.setAddress(data);
			} else if ("image".equals(mTag)) {
				mResult.setImage(data);
			} else if ("value".equals(mTag)) {
				mResult.setValue(data);
			} else if ("price".equals(mTag)) {
				mResult.setPrice(data);
			} else if ("rebate".equals(mTag)) {
				mResult.setRebate(data);
			} else if ("latitude".equals(mTag)) {
				mResult.setLatitude(Double.parseDouble(data));
			} else if ("longitude".equals(mTag)) {
				mResult.setLongitude(Double.parseDouble(data));
			}else if ("name".equals(mTag)) {
				mResult.setName(data);
			}
		}

	}
}
