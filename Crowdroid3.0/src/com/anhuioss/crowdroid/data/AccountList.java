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
import android.content.SharedPreferences;

import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.service.tencent.TencentCommHandler;

public class AccountList {

	private static final String FILE_NAME = "account.xml";

	private Context context;

	private ArrayList<AccountData> list;

	private StatusData status;

	// ---------------------------------------------
	/**
	 * Constructor
	 * 
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	// ---------------------------------------------
	public AccountList(Context context, StatusData status) {
		this.context = context;
		this.status = status;
		readXml();

		removeAccount();

	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public void addAccount(AccountData account) {

		boolean isExist = true;

		String service = account.getService();
		String uid = account.getUid();
		String apiUrl = account.getApiUrl();

		for (AccountData listItem : list) {
			String listItemService = listItem.getService();
			String listItemUid = listItem.getUid();
			String listItemApiUrl = listItem.getApiUrl();
			if (status.getCurrentService().equals(
					IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				if (listItemService != null && listItemUid != null
						&& listItemApiUrl != null
						&& listItemService.equals(service)
						&& listItemUid.equals(uid)
						&& listItemApiUrl.equals(apiUrl)) {
					isExist = false;
					break;
				}
			} else {
				if (listItemService != null && listItemUid != null
						&& listItemService.equals(service)
						&& listItemUid.equals(uid)) {
					isExist = false;
					break;
				}
			}
			if (listItemService != null && listItemUid != null
					&& listItemApiUrl != null
					&& listItemService.equals(service)
					&& listItemUid.equals(uid) && listItemApiUrl.equals(apiUrl)) {
				isExist = false;
				break;
			}
		}

		if (isExist) {
			list.add(account);
			saveXml();
		}

		// Debug
		String s = null;
		s = s + "";

	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public void removeAccount(AccountData account) {

		SharedPreferences status = context.getSharedPreferences("status",
				Context.MODE_PRIVATE);
		status.edit().putInt("last_account_id", 0).commit();

		list.remove(account);
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
	public void refreshAccount(AccountData account) {

		String service = account.getService();
		String uid = account.getUid();
		String apiUrl = account.getUid();

		for (AccountData listItem : list) {
			String listItemService = listItem.getService();
			String listItemUid = listItem.getUid();
			String listItemApiUrl = listItem.getApiUrl();
			if (IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS.equals(status
					.getCurrentService())) {
				if (listItemService != null && listItemUid != null
						&& listItemApiUrl != null
						&& listItemService.equals(service)
						&& listItemUid.equals(uid)
						&& listItemApiUrl.equals(apiUrl)) {
					list.remove(listItem);
					list.add(account);
					saveXml();
					break;
				}
			} else {
				if (listItemService != null && listItemUid != null
						&& listItemService.equals(service)
						&& listItemUid.equals(uid)) {
					list.remove(listItem);
					list.add(account);
					saveXml();
					break;
				}
			}
		}

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

		// Save XML Contents To
		// File(/data/data/com.anhuioss.crowdroid/files/account.xml)
		try {
			FileOutputStream fos = context.openFileOutput(FILE_NAME,
					Context.MODE_PRIVATE);
			PrintWriter pw = new PrintWriter(fos);
			StringBuffer accountBuffer = new StringBuffer();
			accountBuffer.append("<?xml version='1.0' encoding='utf-8' ?>");
			accountBuffer.append("<account_list>");
			// Prepare Accounts
			for (AccountData account : list) {
				accountBuffer.append(account.getAccountXML());
			}
			accountBuffer.append("</account_list>");
			pw.write(accountBuffer.toString());
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
	 * 
	 */
	// -----------------------------------------------------------------------------
	public ArrayList<AccountData> getAllAccount() {

		return list;

	}

	// -----------------------------------------------------------------------------
	/**
	 * Get Account Data By Service and Uid<br>
	 * When You Use The Return Value, First To Check The Return Value Is Not
	 * null
	 * 
	 * @param service
	 * @param uid
	 * @return account/null Returns account If There Is, Otherwise Returns null
	 */
	// -----------------------------------------------------------------------------
	public AccountData getAccountByServiceAndUid(String service, String uid) {

		if (service != null && uid != null) {
			for (AccountData account : list) {
				if (account.getService().equals(service)
						&& account.getUid().equals(uid)) {
					return account;
				}
			}
		}
		return null;

	}

	public AccountData getAccountByServiceAndName(String service, String name) {

		if (service != null && name != null) {
			for (AccountData account : list) {
				if (account.getService().equals(service)
						&& account.getUserName().equals(name)) {
					return account;
				}
			}
		}
		return null;

	}

	// -----------------------------------------------------------------------------
	/**
	 * Get Account Data By Service<br>
	 * 
	 * @param service
	 * @return accountList
	 */
	// -----------------------------------------------------------------------------
	public ArrayList<AccountData> getAccountByService(String service) {

		ArrayList<AccountData> accountList = new ArrayList<AccountData>();
		if (service != null) {
			for (AccountData account : list) {
				if (account.getService().equals(service)) {
					accountList.add(account);
				}
			}
		}
		return accountList;

	}

	// -----------------------------------------------------------------------------
	/**
	 * Get Current Account Data<br>
	 * When You Use The Return Value, First To Check The Return Value Is Not
	 * null
	 * 
	 * @return account/null Returns account If There Is, Otherwise Returns null
	 */
	// -----------------------------------------------------------------------------
	public AccountData getCurrentAccount() {

		if (IGeneral.SERVICE_NAME_TENCENT.equals(status.getCurrentService())) {
			String accessToken = TencentCommHandler.getAccessToken();
			String tokenString = TencentCommHandler.getTokenSecret();
			if (!accessToken.equals("") && !tokenString.equals("")
					&& list != null) {
				for (AccountData account : list) {
					if (account.getAccessToken().equals(accessToken)
							&& account.getTokenSecret().equals(tokenString)) {
						return account;
					}
				}
			}
			return null;
		}
		return getAccountByServiceAndUid(status.getCurrentService(),
				status.getCurrentUid());

	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	public ArrayList<AccountData> getMultiTweetAccount() {

		ArrayList<AccountData> multiTweetAccountList = new ArrayList<AccountData>();
		ArrayList<AccountData> twitterAccountList = new ArrayList<AccountData>();

		for (AccountData account : list) {
			if (account.getMultiTweet()) {
				if (IGeneral.SERVICE_NAME_TWITTER.equals(account.getService())) {
					twitterAccountList.add(account);
				} else {
					multiTweetAccountList.add(account);
				}
			}
		}

		for (AccountData twitterAccount : twitterAccountList) {
			multiTweetAccountList.add(twitterAccount);
		}

		return multiTweetAccountList;

	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	private void readXml() {

		FileInputStream fis = null;
		try {
			// Open File
			fis = context.openFileInput(FILE_NAME);
			// XML Parse And Create New Account List
			list = parserAccount(fis);

		} catch (Exception e) {
			list = new ArrayList<AccountData>();
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

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	private void removeAccount() {
		// Delete Sina's Account While AccessToken Is Null
		ArrayList<AccountData> removeList = new ArrayList<AccountData>();
		for (AccountData account : list) {
			if (account.getService().equals(IGeneral.SERVICE_NAME_SINA)
					&& account.getAccessToken().equals("null")) {
				removeList.add(account);
			}
		}
		if (removeList.size() > 0) {
			for (AccountData account : removeList) {
				list.remove(account);
			}
		}
	}

	// -----------------------------------------------------------------------------
	/**
	 * 
	 */
	// -----------------------------------------------------------------------------
	private static ArrayList<AccountData> parserAccount(InputStream is)
			throws XmlPullParserException, IOException {

		ArrayList<AccountData> accountList = new ArrayList<AccountData>();
		AccountData accountInfo;

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xmlPullParser = factory.newPullParser();
		xmlPullParser.setInput(is, "UTF-8");

		// Start Parsing XML Data
		while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {

			if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {

				if (xmlPullParser.getName().equals("account")) {

					accountInfo = new AccountData();
					// -----------------------------------------------------------------------------
					/**
					 * 
					 */
					// -----------------------------------------------------------------------------
					// Read Account
					while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {

						if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {

							// At Message Id
							if (xmlPullParser.getName().equals("at_message_id")) {
								xmlPullParser.next();
								accountInfo.setLastAtMessageId(xmlPullParser
										.getText());
								continue;
							}

							// Direct Message Id
							if (xmlPullParser.getName().equals(
									"direct_message_id")) {
								xmlPullParser.next();
								accountInfo
										.setLastDirectMessageId(xmlPullParser
												.getText());
								continue;
							}

							// General Message Id
							if (xmlPullParser.getName().equals(
									"general_message_id")) {
								xmlPullParser.next();
								accountInfo
										.setLastGeneralMessageId(xmlPullParser
												.getText());
								continue;
							}

							// General Retweet of Me Message Id
							if (xmlPullParser.getName().equals(
									"retweet_of_me_message_id")) {
								xmlPullParser.next();
								accountInfo
										.setLastRetweetOfMeMessageId(xmlPullParser
												.getText());
								continue;
							}
							// General cfb comment Message Id
							if (xmlPullParser.getName()
									.equals("cfb_comment_id")) {
								xmlPullParser.next();
								accountInfo.setLastCfbCommentId(xmlPullParser
										.getText());
								continue;
							}
							// General SNS Feed Share Message Id
							if (xmlPullParser.getName().equals(
									"sns_feed_share_id")) {
								xmlPullParser.next();
								accountInfo
										.setLastFeedShareMessageId(xmlPullParser
												.getText());
								continue;
							}
							// General SNS Feed Status Message Id
							if (xmlPullParser.getName().equals(
									"sns_feed_status_id")) {
								xmlPullParser.next();
								accountInfo
										.setLastFeedStatusMessageId(xmlPullParser
												.getText());
								continue;
							}
							// General SNS Feed Album Message Id
							if (xmlPullParser.getName().equals(
									"sns_feed_album_id")) {
								xmlPullParser.next();
								accountInfo
										.setLastFeedAlbumMessageId(xmlPullParser
												.getText());
								continue;
							}
							// General SNS Feed Blog Message Id
							if (xmlPullParser.getName().equals(
									"sns_feed_blog_id")) {
								xmlPullParser.next();
								accountInfo
										.setLastFeedBlogMessageId(xmlPullParser
												.getText());
								continue;
							}
							// User last_user_follower_count
							if (xmlPullParser.getName().equals(
									"last_user_follower_count")) {
								xmlPullParser.next();
								accountInfo
										.setLastUserFollowerCount(xmlPullParser
												.getText());
								continue;
							}
							// Uid
							if (xmlPullParser.getName().equals("uid")) {
								xmlPullParser.next();
								accountInfo.setUid(xmlPullParser.getText());
								continue;
							}

							// Service
							if (xmlPullParser.getName().equals("service")) {
								xmlPullParser.next();
								accountInfo.setService(xmlPullParser.getText());
								continue;
							}

							// API URL
							if (xmlPullParser.getName().equals("api_url")) {
								xmlPullParser.next();
								accountInfo.setApiUrl(xmlPullParser.getText());
								continue;
							}

							// User Name
							if (xmlPullParser.getName().equals("user_name")) {
								xmlPullParser.next();
								accountInfo
										.setUserName(xmlPullParser.getText());
								continue;
							}

							// User Screen Name
							if (xmlPullParser.getName().equals(
									"user_screen_name")) {
								xmlPullParser.next();
								accountInfo.setUserScreenName(xmlPullParser
										.getText());
								continue;
							}

							// Auth Type
							if (xmlPullParser.getName().equals("auth_type")) {
								xmlPullParser.next();
								accountInfo
										.setAuthType(xmlPullParser.getText());
								continue;
							}

							// Password
							if (xmlPullParser.getName().equals("password")) {
								xmlPullParser.next();
								accountInfo
										.setPassword(xmlPullParser.getText());
								continue;
							}

							// Access Token
							if (xmlPullParser.getName().equals("access_token")) {
								xmlPullParser.next();
								accountInfo.setAccessToken(xmlPullParser
										.getText());
								continue;
							}

							// Token Secret
							if (xmlPullParser.getName().equals("token_secret")) {
								xmlPullParser.next();
								accountInfo.setTokenSecret(xmlPullParser
										.getText());
								continue;
							}

							// Multi Tweet
							if (xmlPullParser.getName().equals("multi_tweet")) {
								xmlPullParser.next();
								accountInfo.setMultiTweet(xmlPullParser
										.getText().equals("true"));
								continue;
							}

						}

						// End Account
						if (xmlPullParser.getEventType() == XmlPullParser.END_TAG) {
							if (xmlPullParser.getName().equals("account")) {
								accountList.add(accountInfo);
								break;
							}

						}

					}

				}

			}

		}

		return accountList;

	}

}
