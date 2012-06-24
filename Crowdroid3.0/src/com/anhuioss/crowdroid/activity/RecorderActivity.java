package com.anhuioss.crowdroid.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.RecordVoiceToMp3Lame;

public class RecorderActivity extends Activity implements OnClickListener,
		OnLongClickListener, OnFocusChangeListener, ServiceConnection {

	private ImageButton mRecButton;
	private ImageButton mSTTButton;
	private ImageButton mSaveButton;
	private ImageButton mCancelButton;
	private TextView mTimerView;
	private ListView mListSpeechToText;
	private Spinner mSpinnerLanguage;

	private StatusData statusData;

	private static Context context;
	private static EditText edit;

	private static String audioFilePath = "";
	private static File audioFile;
	private static String DIR_PATH;

	private Timer timer;
	private TimerTask timerTask;
	private Message mMsg;

	private int mSampleRate = 8000;

	private long timerCount = 0;
	private long timerUnit = 1000;
	
	private int languageId;
	private int lastLanguageId;
	private String languageSelected;

	private static boolean isRecording;
	public static boolean isSave;

	private static final int SECOND_TIMER = 1000;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	
	private String[] punctuation = new String[] {",", ".", "!", "?", "，", "。", "！", "？"};

	Handler timerHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == SECOND_TIMER) {
				timerCount++;
				//录音时间设置为60秒
				int totalSec = 60;

				totalSec = (int) (totalSec - timerCount);
				
				
				if (totalSec == 0) {
					saveToMp3File();
					finish();
				} else {
					int min = (totalSec / 60);
					int sec = (totalSec % 60);

					mTimerView.setText(String.format("%1$02d:%2$02d", min, sec));
				}
				
			}
			super.handleMessage(msg);
		}

	};

	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			SendMessageActivity sendMessageActivity = (SendMessageActivity) context;
			sendMessageActivity.setTitle(getString(R.string.app_name));
			sendMessageActivity.setProgressBarIndeterminateVisibility(false);

			if (statusCode != null && statusCode.equals("200")) {

				ParseHandler parseHandler = new ParseHandler();
				String audioUrl = (String) parseHandler.parser(service, type,
						statusCode, message);
				if (audioUrl != null) {
					int length = edit.getText().length();
					edit.append("\n" + audioUrl);
					edit.setSelection(length);
				}

			} else {
				Toast.makeText(RecorderActivity.this, ErrorMessage.getErrorMessage(RecorderActivity.this, statusCode), Toast.LENGTH_SHORT).show();
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recorder_new);

		mRecButton = (ImageButton) findViewById(R.id.recorder_rec);
		mSTTButton = (ImageButton) findViewById(R.id.recorder_stt);
		mSaveButton = (ImageButton) findViewById(R.id.recorder_save);
		mCancelButton = (ImageButton) findViewById(R.id.recorder_cancel);
		mTimerView = (TextView) findViewById(R.id.text_recorder_time);
		mListSpeechToText = (ListView) findViewById(R.id.list_speech_to_text);
		
		mSpinnerLanguage = (Spinner) findViewById(R.id.spinner_speech_language);
		selectedSpeechLanguage();

		mRecButton.setOnClickListener(this);
		mSaveButton.setOnClickListener(this);
		mCancelButton.setOnClickListener(this);

		mRecButton.setOnLongClickListener(this);
		mSaveButton.setOnLongClickListener(this);
		mCancelButton.setOnClickListener(this);

		mRecButton.setOnFocusChangeListener(this);
		mSaveButton.setOnFocusChangeListener(this);
		mCancelButton.setOnFocusChangeListener(this);
		
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() != 0) {
			mSTTButton.setOnClickListener(this);
		} else {
			mSTTButton.setEnabled(false);
			Toast.makeText(this, getString(R.string.speech_not_recongnizer), Toast.LENGTH_SHORT)
					.show();
		}
		mListSpeechToText.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, punctuation));
		
		mListSpeechToText.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String speechText = (String) parent.getItemAtPosition(position);			
				int editIndex = edit.getSelectionStart();				
				edit.getText().insert(editIndex, speechText);
				
				finish();
				

			}

		});
		
		

	}

	@Override
	protected void onStart() {
		super.onStart();

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();

		DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/DCIM/Media";

		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			changeButton(false);
		} else {
			Toast.makeText(this, getString(R.string.no_sdcard_mounted), Toast.LENGTH_SHORT)
					.show();
			mRecButton.setEnabled(false);
			mSaveButton.setEnabled(false);
			
		}
		
		isRecording = false;
		isSave = false;
		audioFile = new File("");
		
		SharedPreferences languageStatus = getSharedPreferences("status", Context.MODE_PRIVATE);
		lastLanguageId = languageStatus.getInt("last_speech_language", 0);
		mSpinnerLanguage.setSelection(lastLanguageId);

		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {

		if (!isSave) {
			audioFile.delete();
		}
		isRecording = false;
		isSave = false;
		unbindService(this);
		
		SharedPreferences status = getSharedPreferences("status", Context.MODE_PRIVATE);
		status.edit().putInt("last_speech_language", languageId).commit();

		super.onStop();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.recorder_rec: {

			addMp3File();
			startRecVoiceToMp3();
			recordTimer();

			isRecording = true;
			isSave = false;

			changeButton(true);
			mSTTButton.setEnabled(false);

			break;
		}
		case R.id.recorder_stt: {
			startVoiceRecognitionActivity();
			mTimerView.setText("");
			mCancelButton.setEnabled(true);
			break;
		}
		case R.id.recorder_save: {
			saveToMp3File();
			finish();
			break;
		}
		case R.id.recorder_cancel: {
			isRecording = false;
			audioFile.delete();
			finish();
			break;
		}
		default:
			break;
		}

	}

	private void startRecVoiceToMp3() {

		{
			System.loadLibrary("mp3lame");
		}

		if (isRecording) {
			return;
		}

		new Thread() {
			@Override
			public void run() {
				android.os.Process
						.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
				final int bufferSize = AudioRecord.getMinBufferSize(
						mSampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
						AudioFormat.ENCODING_PCM_16BIT);

				// CHANNEL_IN_MONO --- Level 5, CHANNEL_CONFIGURATION_MONO
				AudioRecord audioRecord = new AudioRecord(
						MediaRecorder.AudioSource.MIC, mSampleRate,
						AudioFormat.CHANNEL_CONFIGURATION_MONO,
						AudioFormat.ENCODING_PCM_16BIT, bufferSize);
				short[] buffer = new short[bufferSize];

				//
				byte[] mp3buffer = new byte[(int) (7200 + buffer.length * 2 * 1.25)];

				FileOutputStream output = null;
				try {
					output = new FileOutputStream(audioFile);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				// Lame init
				RecordVoiceToMp3Lame.init(mSampleRate, 1, mSampleRate, 32);

				try {
					try {
						audioRecord.startRecording();

						int readSize = 0;
						while (isRecording) {
							readSize = audioRecord.read(buffer, 0, bufferSize);
							if (readSize > 0) {
								int encResult = RecordVoiceToMp3Lame.encode(
										buffer, buffer, readSize, mp3buffer);
								if (encResult != 0) {
									try {
										output.write(mp3buffer, 0, encResult);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						}

						int flushResult = RecordVoiceToMp3Lame.flush(mp3buffer);
						if (flushResult != 0) {
							try {
								output.write(mp3buffer, 0, flushResult);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						try {
							output.close();
						} catch (IOException e) {
							e.printStackTrace();
						}

					} finally {
						audioRecord.stop();
						audioRecord.release();
					}
				} finally {
					RecordVoiceToMp3Lame.close();
				}
			}

		}.start();

	}

	public void addMp3File() {

		File directory = new File(DIR_PATH);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		// File name
		Calendar c = Calendar.getInstance();
		Date d = c.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		String fileName = sdf.format(d) + ".mp3";
		audioFile = new File(directory, fileName);
		audioFilePath = audioFile.getAbsolutePath();

	}
	
	public void saveToMp3File() {
		isRecording = false;
		isSave = true;
		if (statusData.getCurrentService() != IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS) {
			Toast.makeText(this, getString(R.string.can_not_be_sent), Toast.LENGTH_SHORT).show();
		}
	}

	private void startVoiceRecognitionActivity() {

		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageSelected);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech Recognition");

		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == RESULT_OK) {

			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			// edit.setText(matches.get(0));
			mListSpeechToText.setAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, matches));

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	public void recordTimer() {
		if (timer == null) {
			if (timerTask == null) {
				timerTask = new TimerTask() {

					@Override
					public void run() {
						if (mMsg == null) {
							mMsg = new Message();
						} else {
							mMsg = Message.obtain();
						}
						mMsg.what = SECOND_TIMER;
						timerHandler.sendMessage(mMsg);
					}

				};
			}
			timer = new Timer(true);
			timer.schedule(timerTask, timerUnit, timerUnit);
		}

	}
	
	private void selectedSpeechLanguage() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.speech_language,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		mSpinnerLanguage.setAdapter(adapter);
		mSpinnerLanguage.setPrompt(getString(R.string.speech_language_selected));
		
		mSpinnerLanguage
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						languageId = mSpinnerLanguage.getSelectedItemPosition();
						
												
						switch (languageId) {
						case 0: {
							languageSelected = "en_US";
							break;
						}
						case 1: {
							languageSelected = "zh_CN";
							break;
						}
						case 2: {
							languageSelected = "ja_JP";
							break;
						}
						default: {
							break;
						}
						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}

				});
	}

	public void changeButton(boolean change) {
		if (change) {
			mRecButton.setEnabled(false);

			mSaveButton.setEnabled(true);
			mCancelButton.setEnabled(true);

		} else {
			mRecButton.setEnabled(true);

			mSaveButton.setEnabled(false);
			mCancelButton.setEnabled(false);
		}
	}

	public static String getAudioFilePath() {
		String audioFilePathTemp = null;

		if (isSave) {
			audioFilePathTemp = audioFilePath;
		} else {
			audioFilePathTemp = "";
		}

		audioFilePath = "";
		return audioFilePathTemp;
	}

	public static void setContextAndEdit(Context mContext, EditText updateText) {
		context = mContext;
		edit = updateText;
	}

	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.recorder_rec: {
			Toast.makeText(RecorderActivity.this,
					getString(R.string.recorder_rec), Toast.LENGTH_SHORT)
					.show();
			break;
		}
		case R.id.recorder_stt: {
			Toast.makeText(RecorderActivity.this,
					getString(R.string.recorder_stt), Toast.LENGTH_SHORT)
					.show();
			break;
		}
		case R.id.recorder_save: {
			Toast.makeText(RecorderActivity.this,
					getString(R.string.recorder_save), Toast.LENGTH_SHORT)
					.show();
			break;
		}
		case R.id.recorder_cancel: {
			Toast.makeText(RecorderActivity.this,
					getString(R.string.recorder_cancel), Toast.LENGTH_SHORT)
					.show();
			break;
		}
		default:
			break;
		}
		return true;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (!hasFocus) {
			return;
		}
		switch (v.getId()) {
		case R.id.recorder_rec: {
			Toast.makeText(RecorderActivity.this,
					getString(R.string.recorder_rec), Toast.LENGTH_SHORT)
					.show();
			break;
		}
		case R.id.recorder_stt: {
			Toast.makeText(RecorderActivity.this,
					getString(R.string.recorder_stt), Toast.LENGTH_SHORT)
					.show();
			break;
		}
		case R.id.recorder_save: {
			Toast.makeText(RecorderActivity.this,
					getString(R.string.recorder_save), Toast.LENGTH_SHORT)
					.show();
			break;
		}
		case R.id.recorder_cancel: {
			Toast.makeText(RecorderActivity.this,
					getString(R.string.recorder_cancel), Toast.LENGTH_SHORT)
					.show();
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i("Activity", "onServiceDisconnected");
		apiServiceInterface = null;

	}

}
