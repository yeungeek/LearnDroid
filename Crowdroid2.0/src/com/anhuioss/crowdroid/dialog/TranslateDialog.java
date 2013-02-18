package com.anhuioss.crowdroid.dialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.TranslationService;
import com.anhuioss.crowdroid.communication.TranslationServiceInterface;
import com.anhuioss.crowdroid.communication.TranslationServiceListener;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.service.TranslationHandler;
import com.anhuioss.crowdroid.util.CommResult;
import com.anhuioss.crowdroid.util.HttpCommunicator;

@SuppressWarnings("unused")
public class TranslateDialog extends Dialog implements ServiceConnection {

	private static int LAST_SELECTED_TO = 0;

	private static int LAST_SELECTED_ENGINE = 0;

	private static int LAST_SELECTED_FROM = 0;

	private Button okButton;
	private Button cancelButton;
	private Spinner spinnerEngine;
	private Spinner spinnerFrom;
	private Spinner spinnerTo;

	private ArrayList<String> languageList;

	private View target;
	private String fromLanguage;
	private String toLanguage;
	private String result;
	private String originalText;

	private String st1 = "";

	/** Bing Language List */
	private String[] languageValues = { "ar",
			// these are not same
			"zh-CHS", "zh-CHT", "nl", "en", "fr", "de", "el", "it", "ja", "ko",
			"pt", "ru", "es" };

	/** Baidu Language List */

	private String[] baiduLanguageValues = { "en", "jp" };

	private String ARABIC;
	private String CHINISE_SIMPLE;
	private String CHINESE_TRADITIONAL;
	private String DUTC;
	private String ENGLISH;
	private String FRENCH;
	private String GERMAN;
	private String GREEK;
	private String ITALIAN;
	private String JAPANISE;
	private String KOREA;
	private String PORTUGESE;
	private String RUSSIAN;
	private String SPANISH;

	String translate_engine;

	SettingData settingData;

	private Context mContext;

	private TextView title;

	// private ProgressBar progressBar;

	// Progress Dialog
	private HandleProgressDialog progress = null;

	private TranslationServiceInterface translationServiceInterface;

	private TranslationServiceListener.Stub translationServiceListener = new TranslationServiceListener.Stub() {

		@Override
		public void requestCompleted(String engine, long type,
				String statusCode, String message) throws RemoteException {

			setProgressEnable(false);

			if (statusCode != null && statusCode.equals("200")) {
				switch ((int) type) {
				case TranslationHandler.TYPE_TRANSLATE: {
					showResult(message);
					break;
				}
				case TranslationHandler.TYPE_DETECT: {
					if (message != null) {
						// spinnerFrom.setSelection(getItemPosition(message));
						fromLanguage = message;
					} else {
						spinnerFrom.setSelection(getItemPosition(fromLanguage));
					}
					break;
				}
				default: {

				}
					spinnerTo.setSelection(getItemPosition(toLanguage));
				}
			} else {
				Toast.makeText(mContext, R.string.error, Toast.LENGTH_SHORT)
						.show();
			}

		}
	};

	@Override
	public void onStart() {
		super.onStart();

		// Set Title
		setTitle(R.string.action_dialog_translation);
		setProgressEnable(false);

		// Bind Service
		Intent intent = new Intent(mContext, TranslationService.class);
		mContext.bindService(intent, this, Context.BIND_AUTO_CREATE);

	}

	@Override
	public void onStop() {
		super.onStop();

		closeProgressDialog();

		// Unbind Service
		mContext.unbindService(this);

	}

	@Override
	public void setTitle(CharSequence title) {
		this.title.setText(title);
	}

	// -----------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	// -----------------------------------------------------------------------------
	public TranslateDialog(Context context, View target) {
		super(context);

		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_translate);

		mContext = context;

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) context
				.getApplicationContext();
		settingData = crowdroidApplication.getSettingData();

		// Get Target View to write the translated text
		this.target = target;

		// Init Views
		title = (TextView) findViewById(R.id.dialog_title);
		// progressBar = (ProgressBar) findViewById(R.id.dialog_progress_bar);
		spinnerEngine = (Spinner) findViewById(R.id.Spinner_engine);
		spinnerFrom = (Spinner) findViewById(R.id.Spinner_language_from);
		spinnerTo = (Spinner) findViewById(R.id.Spinner_language_to);
		okButton = (Button) findViewById(R.id.okButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);

		ArrayAdapter<CharSequence> adapterEnginer = ArrayAdapter
				.createFromResource(mContext, R.array.engine,
						android.R.layout.simple_spinner_item);
		adapterEnginer
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerEngine.setAdapter(adapterEnginer);

		translate_engine = settingData.getTranslateEngine();

		ARABIC = mContext.getString(R.string.arabic);
		CHINISE_SIMPLE = mContext.getString(R.string.chinese_simple);
		CHINESE_TRADITIONAL = mContext.getString(R.string.chiness_traditional);
		DUTC = mContext.getString(R.string.dutch);
		ENGLISH = mContext.getString(R.string.english);
		FRENCH = mContext.getString(R.string.french);
		GERMAN = mContext.getString(R.string.german);
		GREEK = mContext.getString(R.string.greek);
		ITALIAN = mContext.getString(R.string.italian);
		JAPANISE = mContext.getString(R.string.japanese);
		KOREA = mContext.getString(R.string.korea);
		PORTUGESE = mContext.getString(R.string.portugese);
		RUSSIAN = mContext.getString(R.string.russian);
		SPANISH = mContext.getString(R.string.spanish);

		/** Language List (Only for display to Spinner) */
		final String[] languageNames = { ARABIC, CHINISE_SIMPLE,
				CHINESE_TRADITIONAL, DUTC, ENGLISH, FRENCH, GERMAN, GREEK,
				ITALIAN, JAPANISE, KOREA, PORTUGESE, RUSSIAN, SPANISH };

		// Set Adapter to Spinner
		// ArrayAdapter<String> languageSelectAdapter = new
		// ArrayAdapter<String>(context
		// , android.R.layout.simple_spinner_item
		// ,languageNameList);
		// languageSelectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// spinnerFrom.setAdapter(languageSelectAdapter);
		// spinnerTo.setAdapter(languageSelectAdapter);

		// ---------------------------------
		// Set listener (Engine Spinner)
		// ---------------------------------
		spinnerEngine.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				translate_engine = (String) spinnerEngine
						.getItemAtPosition(position);

				final ArrayList<String> languageNameList = new ArrayList<String>();

				switch (position) {
				// baidu translate zh、en、jp
				case 0: {
					LAST_SELECTED_ENGINE = 0;

					String[] languageBaiduNames = { CHINISE_SIMPLE, ENGLISH,
							JAPANISE };

					for (int i = 0; i < languageBaiduNames.length; i++) {
						languageNameList.add(languageBaiduNames[i]);
					}
					break;
				}
				case 1: {
					// bing

					LAST_SELECTED_ENGINE = 1;

					for (int i = 0; i < languageNames.length; i++) {
						languageNameList.add(languageNames[i]);
					}

					break;
				}
				}
				ArrayAdapter<String> languageSelectAdapter = new ArrayAdapter<String>(
						mContext, android.R.layout.simple_spinner_item,
						languageNameList);

				languageSelectAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				spinnerFrom.setAdapter(languageSelectAdapter);

				if (LAST_SELECTED_ENGINE == 1) {
					spinnerTo.setAdapter(languageSelectAdapter);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		// ---------------------------------
		// Set listener (From Spinner)
		// ---------------------------------
		spinnerFrom.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				// Baidu From
				if (LAST_SELECTED_ENGINE == 0) {
					// zh to jp 、en

					ArrayList<String> languageBaiduNameList = new ArrayList<String>();

					switch (position) {
					case 0:
						LAST_SELECTED_FROM = 0;

						fromLanguage = "zh";

						languageBaiduNameList.add(ENGLISH);
						languageBaiduNameList.add(JAPANISE);
						break;
					case 1:
						LAST_SELECTED_FROM = 1;

						// en to zh
						fromLanguage = "en";

						languageBaiduNameList.add(CHINISE_SIMPLE);
						break;
					case 2:
						LAST_SELECTED_FROM = 2;

						// jp to zh
						fromLanguage = "jp";
						String[] languageBaiduNames = { CHINISE_SIMPLE };

						for (int i = 0; i < languageBaiduNames.length; i++) {
							languageBaiduNameList.add(languageBaiduNames[i]);
						}
						break;
					}

					ArrayAdapter<String> languageBaiduSelectAdapter = new ArrayAdapter<String>(
							mContext, android.R.layout.simple_spinner_item,
							languageBaiduNameList);

					languageBaiduSelectAdapter
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

					spinnerTo.setAdapter(languageBaiduSelectAdapter);

				} else if (LAST_SELECTED_ENGINE == 1) {

					fromLanguage = languageValues[position];

				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		// ---------------------------------
		// Set listener (To Spinner)
		// ---------------------------------
		spinnerTo.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (LAST_SELECTED_ENGINE == 0) {
					if (LAST_SELECTED_FROM == 0) {
						if (position == 0) {
							toLanguage = "en";

						} else {
							toLanguage = "jp";
						}
					} else if (LAST_SELECTED_FROM == 1
							|| LAST_SELECTED_FROM == 2) {
						toLanguage = "zh";
					}
				} else {
					toLanguage = languageValues[position];
				}
				LAST_SELECTED_TO = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		// ---------------------------------
		// Set Last Select
		// ---------------------------------
		// spinnerTo.setSelection(LAST_SELECTED_TO);

		// ---------------------------------
		// Set listener (OK Button)
		// ---------------------------------
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// Close Dialog
				dismiss();

				setProgressEnable(true);
				// ---------------------
				// ---------------------
				Map<String, Object> parameters;
				parameters = new HashMap<String, Object>();
				parameters.put("query", getSourceText());
				parameters.put("from", fromLanguage);
				parameters.put("to", toLanguage);

				try {
					translationServiceInterface.request(translate_engine,
							TranslationHandler.TYPE_TRANSLATE,
							translationServiceListener, parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}

			}
		});
		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();

			}
		});

	}

	// -----------------------------------------------------------------------------
	/**
	 * Get Item Position from Language Value
	 */
	// -----------------------------------------------------------------------------
	private int getItemPosition(String name) {

		int position = -1;

		if (name != null) {
			for (int i = 0; i < languageValues.length; i++) {

				if (name.equals(languageValues[i])) {
					;
					position = i;
					return position;
				}
			}
		}

		return position;

	}

	private void setProgressEnable(boolean flag) {

		if (flag) {
			// progressBar.setVisibility(View.VISIBLE);
			showProgressDialog();
		} else {
			// progressBar.setVisibility(View.GONE);
			closeProgressDialog();
		}

	}

	// -----------------------------------------------------------------------------
	/**
	 * Get source text from view
	 */
	// -----------------------------------------------------------------------------
	private String getSourceText() {

		String text = null;

		if (target instanceof EditText) {

			text = ((EditText) target).getText().toString();
			if (text != null && text.equals("")) {
				okButton.setEnabled(false);
			}

		} else if (target instanceof TextView) {

			text = ((TextView) target).getText().toString();
			if (text != null && text.equals(" ")) {
				okButton.setEnabled(false);
			}

		}

		originalText = text;

		return text;
	}

	// -----------------------------------------------------------------------------
	/**
	 * Show Translation Result to Alert Dialog.
	 */
	// -----------------------------------------------------------------------------
	private void showResult(final String translatedText) {

		LayoutInflater inflater = LayoutInflater.from(getContext());
		final View textEntryView = inflater.inflate(
				R.layout.dialog_translate_show, null);
		TextView tx1 = (TextView) textEntryView
				.findViewById(R.id.translatedtext);
		tx1.setText(mContext.getString(R.string.translate_content) + "\n"
				+ "\n" + translatedText);
		TextView tx2 = (TextView) textEntryView.findViewById(R.id.originaltext);
		tx2.setText(mContext.getString(R.string.translate_original) + "\n"
				+ "\n" + originalText);
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.translate_result);
		builder.setView(textEntryView);

		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						if (target instanceof EditText
								&& translatedText != null) {
							((EditText) target).setText(translatedText);
						}
						dismiss();
					}
				});
		builder.show();
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {

		translationServiceInterface = TranslationServiceInterface.Stub
				.asInterface(service);
		if ("Bing".equals(translate_engine)) {
			setProgressEnable(true);

			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();
			parameters.put("query", getSourceText());
			try {
				translationServiceInterface.request(translate_engine,
						TranslationHandler.TYPE_DETECT,
						translationServiceListener, parameters);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i("Activity", "onServiceDisconnected");
		translationServiceInterface = null;

	}

	private void showProgressDialog() {
		if (progress == null) {
			progress = new HandleProgressDialog(mContext);
		}
		progress.show();
	}

	private void closeProgressDialog() {
		if (progress != null) {
			progress.dismiss();
		}
	}

}
