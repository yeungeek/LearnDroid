package com.anhuioss.crowdroid.dialog;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.info.TipInfo;

public class TipsDialog extends Dialog implements OnClickListener {

	private TextView tipsText;

	private CheckBox showNextTime;

	private Button closeButton;

	private Context mContext;

	// ----------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param Context
	 *            context
	 */
	// ----------------------------------------------------------------------------
	public TipsDialog(Context context) {
		super(context);
		mContext = context;
	}

	// ----------------------------------------------------------------------------
	/**
	 * onCreate Method
	 * 
	 * @param Bundle
	 *            savedInstanceState
	 */
	// ----------------------------------------------------------------------------
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_tips);
		setTitle(R.string.tips_title);

		// Find Views
		tipsText = (TextView) findViewById(R.id.tips);
		showNextTime = (CheckBox) findViewById(R.id.check_box_show_next_time);
		closeButton = (Button) findViewById(R.id.button_close);

		tipsText.setText(getTipsText());

		// Set Click Listener
		closeButton.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		closeDialog(!showNextTime.isChecked());
	}

	private void closeDialog(boolean isShowNextTime) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getContext());
		Editor editor = prefs.edit();
		if (isShowNextTime) {
			editor.putString("is-show-tip", "true");
		} else {
			editor.putString("is-show-tip", "false");
		}
		editor.commit();
		dismiss();
	}

	private String getTipsText() {

		ArrayList<String> tipsData = getTipsData(mContext);
		if (tipsData.size() == 0) {
			return "";
		}
		StringBuffer result = new StringBuffer("");
		for (String tip : tipsData) {
			result.append(tip + "\n");
		}
		// ------------------------------------------------------------
		result.append("\n");
		String language = mContext.getResources().getConfiguration().locale
				.getLanguage();
		if (language != null && language.equals("zh")) {
			result.append("详细： http://www.anhuioss.com/cn/crowdroid/update.html");
		} else if (language != null && language.equals("ja")) {
			result.append("詳細： http://www.anhuioss.com/crowdroid/update.html");
		} else {
			result.append("Detail : http://www.anhuioss.com/en/crowdroid/update.html");
		}
		// ------------------------------------------------------------
		return result.toString();

	}

	// ----------------------------------------------------------------------------
	/**
	 * Get Tips Form File (assets/tips/tips-xx.xml xx=zh/en/ja)
	 * 
	 * @param context
	 * @return tipsData
	 */
	// ----------------------------------------------------------------------------
	@SuppressWarnings("finally")
	private ArrayList<String> getTipsData(Context context) {

		// Prepare Data For Result
		ArrayList<String> tipsData = new ArrayList<String>();

		try {

			// Get File Name
			String tipsFileName;
			String language = context.getResources().getConfiguration().locale
					.getLanguage();
			if (language != null && language.equals("zh")) {
				tipsFileName = "tips/tips-zh.xml";
			} else if (language != null && language.equals("ja")) {
				tipsFileName = "tips/tips-ja.xml";
			} else {
				tipsFileName = "tips/tips-en.xml";
			}

			// Read Tips File And Get Content
			InputStreamReader inputReader = new InputStreamReader(context
					.getResources().getAssets().open(tipsFileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			StringBuffer message = new StringBuffer();
			String line = bufReader.readLine();
			while (line != null) {
				message.append(line);
				line = bufReader.readLine();
			}

			// Parser And Set Tips To Data
			ArrayList<TipInfo> tips = parseTips(message.toString());
			if (tips.size() > 0) {
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(context);
				String version = prefs.getString("version", "0");
				for (TipInfo tip : tips) {
					if (tip.isShow(version)) {
						tipsData.add(tip.getText());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return tipsData;
		}

	}

	// ----------------------------------------------------------------------------
	/**
	 * Parse Tips From String
	 * 
	 * @param message
	 * @return tips ArrayList
	 */
	// ----------------------------------------------------------------------------
	@SuppressWarnings("finally")
	private ArrayList<TipInfo> parseTips(String message) {

		ArrayList<TipInfo> tips = new ArrayList<TipInfo>();

		try {

			InputStream is = new ByteArrayInputStream(message.getBytes());
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xmlPullParser = factory.newPullParser();
			xmlPullParser.setInput(is, "UTF-8");

			TipInfo tip;

			while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
				if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
					if (xmlPullParser.getName().equals("tip")) {

						tip = new TipInfo();

						while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
							if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {

								// Text
								if (xmlPullParser.getName().equals("text")) {
									xmlPullParser.next();
									tip.setText(xmlPullParser.getText());
									continue;
								}

								// Min-Version
								if (xmlPullParser.getName().equals(
										"min-version")) {
									xmlPullParser.next();
									tip.setMinVersion(xmlPullParser.getText());
									continue;
								}

								// Max-Version
								if (xmlPullParser.getName().equals(
										"max-version")) {
									xmlPullParser.next();
									tip.setMaxVersion(xmlPullParser.getText());
									continue;
								}

							}
							if (xmlPullParser.getEventType() == XmlPullParser.END_TAG) {
								if (xmlPullParser.getName().equals("tip")) {
									tips.add(tip);
									break;
								}

							}
						}
					}
				}

			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return tips;
		}

	}

}
