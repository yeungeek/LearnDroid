package com.anhuioss.crowdroid.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.Preference;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.LoginActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.AccountList;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.service.cfb.CfbCommHandler;

public class AddCrowdroidForBusinessAccountActivity extends
		BasicAddPreferenceActivity implements ServiceConnection {

	private AccountList accountList;

	private StatusData statusData;

	private ApiServiceInterface apiServiceInterface;

	private String apiUrl;

	private String userScreenName;

	private String userPassword;

	private AutoCompleteTextView serverAddress;

	private AutoCompleteTextView userName;

	private EditText password;

	private CheckBox checkedToShowPassword;

	private View textEntryView;

	private ArrayAdapter<String> cfb_ip_history;

	private ArrayAdapter<String> cfb_name_history;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			setProgressBarIndeterminateVisibility(false);

			// Parser
			if (statusCode != null && statusCode.equals("200")) {

				UserInfo userInfo = new UserInfo();
				ParseHandler parseHandler = new ParseHandler();
				userInfo = (UserInfo) parseHandler.parser(service, type,
						statusCode, message);

				AccountData accountData = new AccountData();
				accountData.setApiUrl(apiUrl);
				accountData.setUserName(userScreenName);
				accountData.setUserScreenName(userInfo.getScreenName());
				accountData.setPassword(userPassword);
				accountData.setUid(userInfo.getUid());
				accountData
						.setService(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS);

				statusData
						.setCurrentService(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS);
				statusData.setCurrentUid(userInfo.getUid());

				accountList.addAccount(accountData);

				CfbCommHandler.setAccount(userScreenName, userPassword, apiUrl);

				// Refresh
				createItemList();

				finish();

			} else {
				showErrorMessage(statusCode);
			}

		}
	};

	/**
	 * Add clicked listener
	 */
	private Preference.OnPreferenceClickListener addClickedListener = new Preference.OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			LayoutInflater factory = LayoutInflater
					.from(AddCrowdroidForBusinessAccountActivity.this);
			textEntryView = factory.inflate(
					R.layout.dialog_add_crowdroidforbusiness_account, null);
			serverAddress = (AutoCompleteTextView) textEntryView
					.findViewById(R.id.edit_cfb_severaddress);
			userName = (AutoCompleteTextView) textEntryView
					.findViewById(R.id.edit_cfb_username);
			password = (EditText) textEntryView
					.findViewById(R.id.edit_cfb_password);
			checkedToShowPassword = (CheckBox) textEntryView
					.findViewById(R.id.show_cfb_password);
			// 保存ip地址
			SharedPreferences sp_ip = getSharedPreferences("cfb_ip_strs", 0);
			String save_history = sp_ip.getString("cfb_ip_history", "");
			String[] hisArrays = save_history.split(",");
			cfb_ip_history = new ArrayAdapter<String>(
					AddCrowdroidForBusinessAccountActivity.this,
					android.R.layout.simple_dropdown_item_1line, hisArrays);
			//
			if (hisArrays.length > 50) {
				String[] newArrays = new String[50];
				System.arraycopy(hisArrays, 0, newArrays, 0, 50);
				cfb_ip_history = new ArrayAdapter<String>(
						AddCrowdroidForBusinessAccountActivity.this,
						android.R.layout.simple_dropdown_item_1line, newArrays);
			}
			serverAddress.setAdapter(cfb_ip_history);
			// 保存用户名
			SharedPreferences sp_name = getSharedPreferences("cfb_name_strs", 0);
			String save_name_history = sp_name
					.getString("cfb_name_history", "");
			String[] his_name_Arrays = save_name_history.split(",");
			cfb_name_history = new ArrayAdapter<String>(
					AddCrowdroidForBusinessAccountActivity.this,
					android.R.layout.simple_dropdown_item_1line,
					his_name_Arrays);
			if (his_name_Arrays.length > 50) {
				String[] newArrays = new String[50];
				System.arraycopy(his_name_Arrays, 0, newArrays, 0, 50);
				cfb_name_history = new ArrayAdapter<String>(
						AddCrowdroidForBusinessAccountActivity.this,
						android.R.layout.simple_dropdown_item_1line, newArrays);
			}
			userName.setAdapter(cfb_name_history);

			ArrayAdapter<CharSequence> adapter = ArrayAdapter
					.createFromResource(
							AddCrowdroidForBusinessAccountActivity.this,
							R.array.services,
							android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			checkedToShowPassword
					.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (isChecked) {
								password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
							} else {
								password.setInputType(InputType.TYPE_CLASS_TEXT
										| InputType.TYPE_TEXT_VARIATION_PASSWORD);
							}

						}

					});
			AlertDialog dialog = new AlertDialog.Builder(
					AddCrowdroidForBusinessAccountActivity.this)
					.setTitle(R.string.add)
					.setView(textEntryView)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									apiUrl = serverAddress.getText().toString();
									userScreenName = userName.getText()
											.toString();
									userPassword = password.getText()
											.toString();

									if (apiUrl.equals("")) {
										Toast.makeText(
												AddCrowdroidForBusinessAccountActivity.this,
												getResources()
														.getString(
																R.string.service_address_empty),
												Toast.LENGTH_SHORT).show();
									} else if (userScreenName.equals("")) {
										Toast.makeText(
												AddCrowdroidForBusinessAccountActivity.this,
												getResources()
														.getString(
																R.string.user_name_empty),
												Toast.LENGTH_SHORT).show();
									} else if (userPassword.equals("")) {
										Toast.makeText(
												AddCrowdroidForBusinessAccountActivity.this,
												getResources()
														.getString(
																R.string.password_empty),
												Toast.LENGTH_SHORT).show();
									} else {
										try {

											setProgressBarIndeterminateVisibility(true);

											// 保存ip地址
											SharedPreferences sp_ip = getSharedPreferences(
													"cfb_ip_strs", 0);
											String save_IPStr = sp_ip
													.getString(
															"cfb_ip_history",
															"");
											String[] hisArrays = save_IPStr
													.split(",");
											for (int i = 0; i < hisArrays.length; i++) {
												if (hisArrays[i].equals(apiUrl)) {
													continue;
												} else {
													StringBuilder sb_ip = new StringBuilder(
															save_IPStr);
													sb_ip.append(apiUrl + ",");
													sp_ip.edit()
															.putString(
																	"cfb_ip_history",
																	sb_ip.toString())
															.commit();
												}
											}

											// 保存用户名
											SharedPreferences sp_name = getSharedPreferences(
													"cfb_name_strs", 0);
											String save_nameStr = sp_name
													.getString(
															"cfb_name_history",
															"");
											String[] his_nameArrays = save_nameStr
													.split(",");
											for (int i = 0; i < his_nameArrays.length; i++) {
												if (his_nameArrays[i]
														.equals(userScreenName)) {
													continue;
												} else {
													StringBuilder sb_name = new StringBuilder(
															save_nameStr);
													sb_name.append(userScreenName
															+ ",");
													sp_name.edit()
															.putString(
																	"cfb_name_history",
																	sb_name.toString())
															.commit();
												}
											}

											// Prepare Parameters
											Map<String, Object> parameters;
											parameters = new HashMap<String, Object>();
											parameters.put("name",
													userScreenName);
											parameters.put("password",
													userPassword);
											parameters.put("apiUrl", apiUrl);

											// Request
											apiServiceInterface
													.request(
															IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS,
															CommHandler.TYPE_VERIFY_USER,
															apiServiceListener,
															parameters);

										} catch (RemoteException e) {
											e.printStackTrace();
										}
									}

								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).create();
			dialog.show();

			return true;
		}
	};

	/**
	 * Item clicked listener
	 */
	private Preference.OnPreferenceClickListener itemClickedListener = new Preference.OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			final int id = Integer.valueOf(preference.getKey());
			final CharSequence[] item = getResources().getStringArray(
					R.array.item);
			AlertDialog dialog = new AlertDialog.Builder(
					AddCrowdroidForBusinessAccountActivity.this).setItems(item,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							switch (which) {
							// Edit
							case 0:
								LayoutInflater factory = LayoutInflater
										.from(AddCrowdroidForBusinessAccountActivity.this);
								textEntryView = factory
										.inflate(
												R.layout.dialog_add_crowdroidforbusiness_account,
												null);
								serverAddress = (AutoCompleteTextView) textEntryView
										.findViewById(R.id.edit_cfb_severaddress);
								userName = (AutoCompleteTextView) textEntryView
										.findViewById(R.id.edit_cfb_username);
								password = (EditText) textEntryView
										.findViewById(R.id.edit_cfb_password);
								checkedToShowPassword = (CheckBox) textEntryView
										.findViewById(R.id.show_cfb_password);

								checkedToShowPassword
										.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

											@Override
											public void onCheckedChanged(
													CompoundButton buttonView,
													boolean isChecked) {
												if (isChecked) {
													password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
												} else {
													password.setInputType(InputType.TYPE_CLASS_TEXT
															| InputType.TYPE_TEXT_VARIATION_PASSWORD);
												}

											}

										});

								// Set Account Data
								AccountData account = accountList
										.getAccountByService(
												IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
										.get(id);
								serverAddress.setText(account.getApiUrl());
								userName.setText(account.getUserName());
								password.setText(account.getPassword());

								ArrayAdapter<CharSequence> adapter = ArrayAdapter
										.createFromResource(
												AddCrowdroidForBusinessAccountActivity.this,
												R.array.services,
												android.R.layout.simple_spinner_item);
								adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
								new AlertDialog.Builder(
										AddCrowdroidForBusinessAccountActivity.this)
										.setTitle(R.string.edit)
										.setView(textEntryView)
										.setPositiveButton(
												R.string.confirm,
												new DialogInterface.OnClickListener() {
													// Confirm Handle
													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {

														apiUrl = serverAddress
																.getText()
																.toString();
														userScreenName = userName
																.getText()
																.toString();
														userPassword = password
																.getText()
																.toString();
														AccountData accountData = accountList
																.getAccountByService(
																		IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
																.get(id);
														accountList
																.removeAccount(accountData);
														createItemList();

														try {

															setProgressBarIndeterminateVisibility(true);

															// Prepare
															// Parameters
															Map<String, Object> parameters;
															parameters = new HashMap<String, Object>();
															parameters
																	.put("name",
																			userScreenName);
															parameters
																	.put("password",
																			userPassword);
															parameters.put(
																	"apiUrl",
																	apiUrl);

															// Request
															apiServiceInterface
																	.request(
																			IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS,
																			CommHandler.TYPE_VERIFY_USER,
																			apiServiceListener,
																			parameters);
														} catch (RemoteException e) {
															e.printStackTrace();
														}
													}
												})
										// Cancle Handle
										.setNegativeButton(
												R.string.cancel,
												new DialogInterface.OnClickListener() {

													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {

													}
												}).show();
								break;
							// Delete
							case 1:
								new AlertDialog.Builder(
										AddCrowdroidForBusinessAccountActivity.this)
										.setTitle(R.string.confirm)
										.setMessage(R.string.whether_delete)
										.setPositiveButton(
												R.string.confirm,
												new DialogInterface.OnClickListener() {

													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
														final AccountData accountData = accountList
																.getAccountByService(
																		IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
																.get(id);
														if (accountData
																.getUserName()
																.equals(nowusername)
																|| accountData
																		.getUserScreenName()
																		.equals(nowusername)) {
															AlertDialog dialognext = new AlertDialog.Builder(
																	AddCrowdroidForBusinessAccountActivity.this)
																	.setTitle(
																			R.string.confirm)
																	.setMessage(
																			R.string.weather_delate_now)
																	.setPositiveButton(
																			R.string.ok,
																			new DialogInterface.OnClickListener() {

																				@Override
																				public void onClick(
																						DialogInterface dialog,
																						int which) {

																					accountList
																							.removeAccount(accountData);
																					Intent i = new Intent(
																							AddCrowdroidForBusinessAccountActivity.this,
																							LoginActivity.class);
																					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
																					startActivity(i);
																					// createItemList();
																				}
																			})
																	.setNegativeButton(
																			R.string.cancel,
																			new DialogInterface.OnClickListener() {

																				@Override
																				public void onClick(
																						DialogInterface dialog,
																						int which) {
																					// TODO
																					// Auto-generated
																					// method
																					// stub
																					dialog.dismiss();

																				}
																			})
																	.create();
															dialognext.show();
														} else {
															accountList
																	.removeAccount(accountData);
															createItemList();
														}

													}
												})
										.setNegativeButton(
												R.string.cancel,
												new DialogInterface.OnClickListener() {

													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {

													}
												}).show();
								break;
							default:
								break;
							}

						}

					}).create();
			dialog.show();

			return true;
		}
	};

	/**
	 * onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();

		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		accountList = crowdroidApplication.getAccountList();
		statusData = crowdroidApplication.getStatusData();

		showWhatIsCrowdroidForBusiness();

		initView(getString(R.string.add_crowdroidforbusiness_account), "",
				getString(R.string.crowdroidforbusiness_account_list), null,
				addClickedListener, itemClickedListener);

		createItemList();
	}

	@Override
	public void onStop() {
		super.onStop();

		// Unbind Service
		unbindService(this);
	}

	/**
	 * Create item list
	 */
	@Override
	protected void createItemList() {
		ArrayList<HashMap<String, String>> itemList = new ArrayList<HashMap<String, String>>();

		for (AccountData account : accountList
				.getAccountByService(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
			HashMap<String, String> item = new HashMap<String, String>();
			item.put("title", account.getUserName());
			item.put("summary",
					getString(R.string.server) + ":" + account.getService()
							+ "\n" + getString(R.string.server_address) + ":"
							+ account.getApiUrl());
			itemList.add(item);
		}

		refresh(itemList);
	}

	/**
	 * Show error dialog
	 */
	@SuppressWarnings("unused")
	private void showErrorDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.register_error);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).create();
		builder.show();
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
