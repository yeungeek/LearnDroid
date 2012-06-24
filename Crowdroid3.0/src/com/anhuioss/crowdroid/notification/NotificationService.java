package com.anhuioss.crowdroid.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.anhuioss.crowdroid.AtMessageTimelineActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.DirectMessageSendActivity;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.MyTimelineActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.DirectMessageReceiveActivity;
import com.anhuioss.crowdroid.RetweetOfMeActivity;
import com.anhuioss.crowdroid.CommentTimelineActivity;
import com.anhuioss.crowdroid.activity.FollowedActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.sns.StatusTimelineActivity;

public class NotificationService extends Service implements ServiceConnection {

	public static boolean isNotifyChecking = false;

	public static String INTENT_DATA_NORMAL_CHECK = "normal_check";
	public static String INTENT_DATA_AT_CHECK = "at_check";
	public static String INTENT_DATA_DIRECT_CHECK = "direct_check";
	public static String INTENT_DATA_FOLLOWER_CHECK = "follower_check";
	public static String INTENT_DATA_TWITTER_FOLLOW_CHECK = "twitter_follow_check";
	public static String INTENT_DATA_UNFOLLOW_CHECK = "unfollow_check";
	public static String INTENT_DATA_COMMENT_CHECK = "comment_check";
	public static String INTENT_DATA_RETWEET_OF_ME_CHECK = "retweet_of_me_check";
	public static String INTENT_DATA_FEED_SHARE_CHECK = "feed_share_check";
	public static String INTENT_DATA_FEED_STATE_CHECK = "feed_state_check";
	public static String INTENT_DATA_FEED_ALBUM_CHECK = "feed_album_check";
	public static String INTENT_DATA_FEED_BLOG_CHECK = "feed_blog_check";
	public static String ACTION_NOTIFICATION = "com.anhuioss.crowdroid.NOTIFICATION_TIMER";

	private int at = 0;

	private int direct = 0;

	private int normal = 0;

	private int follower = 0;

	// twitter
	private int twFollower = 0;

	private int twUnfollow = 0;

	private int unReadComment = 0;

	private int unReadCfbComment = 0;

	private int unRetweetOfMe = 0;

	// RenRen

	private int feedShare = 0;

	private int feedState = 0;

	private int feedAlbum = 0;

	private int feedBlog = 0;

	// ----------------------------
	private StatusData statusData;

	private AccountData accountData;

	private String followerCount;

	private int size = 0;

	private int setfollower = 0;

	private int setComment = 0;

	private int setTwFollower = 0;

	private int setTwUnfollower = 0;

	private int setRetweetOfMe = 0;

	private int setCfbComment = 0;

	private ArrayList<Integer> notificationFlags = new ArrayList<Integer>();
	private ArrayList<Integer> notificationFlagsBackup = new ArrayList<Integer>();

	private ApiServiceInterface apiServiceInterface = null;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			if (statusCode != null && statusCode.equals("200")
					&& message != null && !message.equals("[null]")) {

				// Parser
				if (type == CommHandler.TYPE_NOTIFICATION_UNREAD_MESSAGE) {

					if (!IGeneral.SERVICE_NAME_TENCENT.equals(service)) {
						String[] unreadCount = new String[2];
						ParseHandler parseHandler = new ParseHandler();
						unreadCount = (String[]) parseHandler.parser(service,
								type, statusCode, message);

						try {
							follower = Integer.valueOf(unreadCount[0]);
							unReadComment = Integer.valueOf(unreadCount[1]);
						} catch (NumberFormatException e) {
						}

						if (follower > 0) {
							setFollowerNotification();
						}
						if (unReadComment > 0) {
							setCommentNotification();
						}
					} else {
						// Tencent
						ParseHandler parseHandler = new ParseHandler();
						String[] unreadData = (String[]) parseHandler.parser(
								service, type, statusCode, message);
						normal = Integer.valueOf(unreadData[0]);
						if (!notificationFlagsBackup.contains(0)) {
							normal = 0;
						}
						at = Integer.valueOf(unreadData[1]);
						if (!notificationFlagsBackup.contains(1)) {
							at = 0;
						}
						direct = Integer.valueOf(unreadData[2]);
						if (!notificationFlagsBackup.contains(2)) {
							direct = 0;
						}
						follower = Integer.valueOf(unreadData[3]);
						if (!notificationFlagsBackup.contains(42)) {
							follower = 0;
						}
						if (follower > 0) {
							setFollowerNotification();
						}
					}

				} else if (type == CommHandler.TYPE_GET_USER_INFO) {

					UserInfo userInfo = new UserInfo();
					ParseHandler parseHandle = new ParseHandler();
					userInfo = (UserInfo) parseHandle.parser(service, type,
							statusCode, message);
					followerCount = userInfo.getFollowerCount();
					String oldFollowerCount = accountData
							.getLastUserFollowerCount();
					// get follow or unFollow count
					try {
						if (!followerCount.equals("")
								&& Integer.parseInt(followerCount) > Integer
										.parseInt(oldFollowerCount)) {
							// Get Follower Count
							twFollower = Integer.parseInt(followerCount)
									- Integer.parseInt(oldFollowerCount);
						} else if (!followerCount.equals("")
								&& Integer.parseInt(followerCount) < Integer
										.parseInt(oldFollowerCount)) {
							// Get Unfollow Count
							twUnfollow = Integer.parseInt(oldFollowerCount)
									- Integer.parseInt(followerCount);
						} else {
							twFollower = 0;
							twUnfollow = 0;
						}
					} catch (NumberFormatException e) {
						twFollower = 0;
						twUnfollow = 0;
					}
					setFollowAndLeaveNotification();
				} else {

					ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();
					ParseHandler parseHandler = new ParseHandler();
					timelineInfoList = (ArrayList<TimeLineInfo>) parseHandler
							.parser(service, type, statusCode, message);

					setNotificationData(type, timelineInfoList);

				}
			}

			size = size - 1;
			if (size == 0) {
				setNotification();
			}

		}

	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);

		normal = 0;
		at = 0;
		direct = 0;
		follower = 0;
		twFollower = 0;
		twUnfollow = 0;
		unReadComment = 0;
		unRetweetOfMe = 0;
		unReadCfbComment = 0;
		// RenRen
		feedShare = 0;
		feedState = 0;
		feedAlbum = 0;
		feedBlog = 0;

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		accountData = crowdroidApplication.getAccountList().getCurrentAccount();

		// Bind Api Service
		Intent intentApiService = new Intent(this, ApiService.class);
		bindService(intentApiService, this, Context.BIND_AUTO_CREATE);

		isNotifyChecking = true;

		// Check
		check(intent.getExtras());

	}

	@Override
	public void onDestroy() {

		isNotifyChecking = false;

		// Unbind Service
		unbindService(this);

		super.onDestroy();

	}

	private void check(Bundle bundle) {

		notificationFlags = new ArrayList<Integer>();
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_RENREN)) {

			if (bundle.getBoolean(INTENT_DATA_FEED_SHARE_CHECK)) {
				notificationFlags.add(1);
			}
			if (bundle.getBoolean(INTENT_DATA_FEED_STATE_CHECK)) {
				notificationFlags.add(4);
			}
			if (bundle.getBoolean(INTENT_DATA_FEED_ALBUM_CHECK)) {
				notificationFlags.add(2);
			}
			if (bundle.getBoolean(INTENT_DATA_FEED_BLOG_CHECK)) {
				notificationFlags.add(3);
			}
		} else {
			if (bundle.getBoolean(INTENT_DATA_NORMAL_CHECK)) {
				notificationFlags.add(0);
			}
			if (bundle.getBoolean(INTENT_DATA_AT_CHECK)) {
				notificationFlags.add(1);
			}
			if (bundle.getBoolean(INTENT_DATA_DIRECT_CHECK)) {
				notificationFlags.add(2);
			}
		}
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SINA)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SOHU)) {
			if (bundle.getBoolean(INTENT_DATA_COMMENT_CHECK)) {
				setComment = 1;
			}
			if (bundle.getBoolean(INTENT_DATA_FOLLOWER_CHECK)) {
				setfollower = 1;
			}
			if (bundle.getBoolean(INTENT_DATA_COMMENT_CHECK)
					|| bundle.getBoolean(INTENT_DATA_FOLLOWER_CHECK)) {
				notificationFlags.add(42);
			}
		}

		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TWITTER)) {
			if (bundle.getBoolean(INTENT_DATA_TWITTER_FOLLOW_CHECK)) {
				setTwFollower = 1;
			}
			if (bundle.getBoolean(INTENT_DATA_UNFOLLOW_CHECK)) {
				setTwUnfollower = 1;
			}
			if (bundle.getBoolean(INTENT_DATA_RETWEET_OF_ME_CHECK)) {
				setRetweetOfMe = 1;
			}
			if (bundle.getBoolean(INTENT_DATA_TWITTER_FOLLOW_CHECK)
					|| bundle.getBoolean(INTENT_DATA_UNFOLLOW_CHECK)) {
				notificationFlags.add(8);
			}
			if (bundle.getBoolean(INTENT_DATA_RETWEET_OF_ME_CHECK)) {
				notificationFlags.add(35);
			}
		}

		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TENCENT)) {
			if (bundle.getBoolean(INTENT_DATA_FOLLOWER_CHECK)) {
				notificationFlags.add(42);
			}
			notificationFlagsBackup = notificationFlags;
			notificationFlags = new ArrayList<Integer>();
			notificationFlags.add(42);
		}

		// ------------------
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
			if (bundle.getBoolean(INTENT_DATA_COMMENT_CHECK)) {

				setCfbComment = 1;
				notificationFlags.add(49);
			}
		}

		// ------------------
		size = notificationFlags.size();

		if (apiServiceInterface != null) {

			new Thread(new Runnable() {

				@Override
				public void run() {
					try {

						for (Integer type : notificationFlags) {

							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							// Prepare Parameters
							Map<String, Object> parameters;
							parameters = new HashMap<String, Object>();
							parameters.put("page", "1");
							parameters.put("uid", statusData.getCurrentUid());
							String screenName = "";
							if (accountData != null
									&& accountData.getUserName() != null) {
								screenName = accountData.getUserName();
							}
							parameters.put("screen_name", screenName);

							// Request
							apiServiceInterface.request(
									statusData.getCurrentService(), type,
									apiServiceListener, parameters);

						}

					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}).start();

		}

	}

	private void setNotification() {

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(
				com.anhuioss.crowdroid.R.drawable.icon, null,
				System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_SOUND;
		notification.number = 0;

		if (at > 20) {
			at = 20;
		}
		if (direct > 20) {
			direct = 20;
		}
		if (normal > 20) {
			normal = 20;
		}
		if (setRetweetOfMe == 0) {
			unRetweetOfMe = 0;
		} else if (unRetweetOfMe > 20) {
			unRetweetOfMe = 20;
		}
		if (setCfbComment == 0) {
			unReadCfbComment = 0;
		} else if (unReadCfbComment > 20) {
			unReadCfbComment = 20;
		}

		// ------------------
		if (feedShare > 20) {
			feedShare = 20;
		}
		if (feedState > 20) {
			feedState = 20;
		}
		if (feedAlbum > 20) {
			feedAlbum = 20;
		}
		if (feedBlog > 20) {
			feedShare = 20;
		}
		// ------------------

		int count = 0;
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_RENREN)) {
			count = feedShare + feedState + feedAlbum + feedBlog;
		} else {
			count = at + direct + normal + unRetweetOfMe + unReadCfbComment;
		}

		notification.number = count;

		Intent notificationIntent = new Intent();
		StringBuffer sb = new StringBuffer();
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_RENREN)) {

			// renren feed :share state album blog
			if (feedShare > 0) {
				notificationIntent = new Intent(this,
						AtMessageTimelineActivity.class);
			} else if (feedState > 0) {
				notificationIntent = new Intent(this, MyTimelineActivity.class);
			} else if (feedAlbum > 0) {
				notificationIntent = new Intent(this,
						DirectMessageReceiveActivity.class);
			} else if (feedBlog > 0) {
				notificationIntent = new Intent(this,
						DirectMessageSendActivity.class);
			}
			if (feedShare > 0) {
				sb.append(getString(R.string.share) + "(" + feedShare + ")"
						+ "\n");
			}
			if (feedState > 0) {
				sb.append(getString(R.string.status) + "(" + feedState + ")"
						+ "\n");
			}
			if (feedAlbum > 0) {
				sb.append(getString(R.string.album) + "(" + feedAlbum + ")"
						+ "\n");
			}
			if (feedBlog > 0) {
				sb.append(getString(R.string.blog) + "(" + feedBlog + ")"
						+ "\n");
			}
		} else {
			if (direct > 0) {
				notificationIntent = new Intent(this,
						DirectMessageReceiveActivity.class);
			} else if (at > 0) {
				notificationIntent = new Intent(this,
						AtMessageTimelineActivity.class);
			} else if (unRetweetOfMe > 0) {
				notificationIntent = new Intent(this, RetweetOfMeActivity.class);
			} else if (normal > 0) {
				notificationIntent = new Intent(this,
						HomeTimelineActivity.class);
			} else if (unReadCfbComment > 0) {
				notificationIntent = new Intent(this,
						CommentTimelineActivity.class);
			}

			if (direct > 0) {
				sb.append(getString(R.string.notification_direct_message) + "("
						+ direct + ")" + "\n");
			}

			if (at > 0) {
				sb.append(getString(R.string.notification_at_message) + "("
						+ at + ")" + "\n");
			}

			if (normal > 0) {
				sb.append(getString(R.string.home) + "(" + normal + ")" + "\n");
			}

			if (unRetweetOfMe > 0) {
				sb.append(getString(R.string.retweet_of_me) + "("
						+ unRetweetOfMe + ")" + "\n");
			}

			if (unReadCfbComment > 0
					&& statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				sb.append(getString(R.string.comment) + "(" + unReadCfbComment
						+ ")" + "\n");
			}
		}

		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		notificationIntent.putExtra("notification", true);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(getApplicationContext(), "Crowdroid",
				sb.toString(), contentIntent);

		if (count > 0) {
			((CrowdroidApplication) getApplicationContext())
					.setIsComeFromNotification(0);
			notificationManager.notify(1, notification);
		} else {
			notificationManager.cancel(1);
		}

		isNotifyChecking = false;

		stopSelf();

	}

	// For Sina
	private void setFollowerNotification() {

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification_follower = new Notification(
				com.anhuioss.crowdroid.R.drawable.icon, null,
				System.currentTimeMillis());
		notification_follower.flags = Notification.FLAG_AUTO_CANCEL;
		notification_follower.defaults = Notification.DEFAULT_SOUND;
		notification_follower.number = 0;

		if (!IGeneral.SERVICE_NAME_TENCENT.equals(statusData
				.getCurrentService())) {
			if (follower > 20) {
				follower = 20;
			}

			if (setfollower == 0) {
				follower = 0;
			}
		}

		notification_follower.number = follower;

		Intent notificationIntent = new Intent();

		if (follower > 0) {
			notificationIntent = new Intent(this, FollowedActivity.class);
			notificationIntent.putExtra("notification", true);
			Bundle bundle = new Bundle();
			bundle.putString("context", "NotificationService");
			notificationIntent.putExtras(bundle);
		}

		StringBuffer sb = new StringBuffer();
		if (follower > 0) {
			sb.append(getString(R.string.notification_new_follower) + "("
					+ follower + ")" + "\n");
		}

		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent followerIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification_follower.setLatestEventInfo(getApplicationContext(),
				"Crowdroid", sb.toString(), followerIntent);

		if (follower > 0) {
			((CrowdroidApplication) getApplicationContext())
					.setIsComeFromNotification(2);
			notificationManager.notify(2, notification_follower);
		} else {
			notificationManager.cancel(2);
		}

	}

	// For sina Comment notification
	private void setCommentNotification() {

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification_follower = new Notification(
				com.anhuioss.crowdroid.R.drawable.icon, null,
				System.currentTimeMillis());
		notification_follower.flags = Notification.FLAG_AUTO_CANCEL;
		notification_follower.defaults = Notification.DEFAULT_SOUND;
		notification_follower.number = 0;

		if (unReadComment > 20) {
			unReadComment = 20;
		}

		if (setComment == 0) {
			unReadComment = 0;
		}

		notification_follower.number = unReadComment;

		Intent notificationIntent = new Intent();

		if (unReadComment > 0) {
			notificationIntent = new Intent(this, CommentTimelineActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("context", "NotificationService");
			notificationIntent.putExtras(bundle);
		}

		StringBuffer sb = new StringBuffer();
		if (unReadComment > 0) {
			sb.append(getString(R.string.notification_comment) + "("
					+ unReadComment + ")" + "\n");
		}

		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent followerIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification_follower.setLatestEventInfo(getApplicationContext(),
				"Crowdroid", sb.toString(), followerIntent);

		if (unReadComment > 0) {
			((CrowdroidApplication) getApplicationContext())
					.setIsComeFromNotification(1);
			notificationManager.notify(4, notification_follower);
		} else {
			notificationManager.cancel(4);
		}

	}

	// For twitter
	private void setFollowAndLeaveNotification() {

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification_follower = new Notification(
				com.anhuioss.crowdroid.R.drawable.icon, null,
				System.currentTimeMillis());
		notification_follower.flags = Notification.FLAG_AUTO_CANCEL;
		notification_follower.defaults = Notification.DEFAULT_SOUND;
		notification_follower.number = 0;

		if (twFollower > 20) {
			twFollower = 20;
		}

		if (twUnfollow > 20) {
			twUnfollow = 20;
		}

		if (setTwFollower == 0) {
			twFollower = 0;
		}
		if (setTwUnfollower == 0) {
			twUnfollow = 0;
		}

		int count = twFollower + twUnfollow;
		notification_follower.number = count;

		Intent notificationIntent = new Intent();
		notificationIntent.putExtra("notification", true);

		if (count > 0) {
			notificationIntent = new Intent(this, FollowedActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("follower_count", followerCount);
			bundle.putString("context", "NotificationService");
			notificationIntent.putExtras(bundle);
		}

		StringBuffer sb = new StringBuffer();
		if (twFollower > 0) {
			sb.append(getString(R.string.notification_new_follower) + "("
					+ twFollower + ")" + "\n");
		}
		if (twUnfollow > 0) {
			sb.append(getString(R.string.notification_unfollow) + "("
					+ twUnfollow + ")" + "\n");
		}

		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent followerIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification_follower.setLatestEventInfo(getApplicationContext(),
				"Crowdroid", sb.toString(), followerIntent);

		if (count > 0) {
			((CrowdroidApplication) getApplicationContext())
					.setIsComeFromNotification(2);
			notificationManager.notify(2, notification_follower);
		} else {
			notificationManager.cancel(2);
		}

	}

	private void setNotificationData(int type,
			ArrayList<TimeLineInfo> timelineInfoList) {

		switch (type) {
		case CommHandler.TYPE_GET_HOME_TIMELINE: {
			Long oldId = statusData.getNewestGeneralMessageId();
			normal = getNewestCount(oldId, timelineInfoList);
			break;
		}
		case CommHandler.TYPE_GET_AT_MESSAGE: {
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)) {
				feedShare = getNewestShareCount(
						statusData.getNewestFeedShareMessageId(),
						timelineInfoList);
			} else {
				at = getNewestCount(statusData.getNewestAtMessageId(),
						timelineInfoList);
			}

			break;
		}
		case CommHandler.TYPE_GET_DIRECT_MESSAGE_RECEIVE: {
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)) {
				feedAlbum = getNewestCount(
						statusData.getNewestFeedAlbumMessageId(),
						timelineInfoList);
			} else {
				direct = getNewestCount(statusData.getNewestDirectMessageId(),
						timelineInfoList);
			}

			break;
		}
		case CommHandler.TYPE_GET_DIRECT_MESSAGE_SEND: {
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)) {
				feedBlog = getNewestCount(
						statusData.getNewestFeedBlogMessageId(),
						timelineInfoList);
			}
			break;
		}
		case CommHandler.TYPE_GET_MY_TIME_LINE: {
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)) {
				feedState = getNewestCount(
						statusData.getNewestFeedStatusMessageId(),
						timelineInfoList);
			}
			break;
		}
		case CommHandler.TYPE_GET_RETWEET_OF_ME_TIME_LINE: {
			unRetweetOfMe = getNewestCount(
					statusData.getNewestRetweetOfMeMessageId(),
					timelineInfoList);
		}
		case CommHandler.TYPE_GET_COMMENT_TIMELINE: {
			unReadCfbComment = getNewestCount(statusData.getNewestCommentId(),
					timelineInfoList);
		}
		}

	}

	private int getNewestCount(Long oldNewestId,
			ArrayList<TimeLineInfo> timelineInfoList) {

		int count = 0;

		if (timelineInfoList.size() > 0) {

			for (TimeLineInfo timeLineInfo : timelineInfoList) {
				if (Long.valueOf(timeLineInfo.getMessageId()) > oldNewestId) {
					count++;
				}
			}
		}
		return count;
	}

	// -----------renren feed share--------------------------------
	private int getNewestShareCount(Long oldNewestId,
			ArrayList<TimeLineInfo> timelineInfoList) {
		int count = 0;

		if (timelineInfoList.size() > 0) {

			for (TimeLineInfo timeLineInfo : timelineInfoList) {
				if (Long.valueOf(timeLineInfo.getPostId()) > oldNewestId) {
					count++;
				}
			}
		}
		return count;
	}

	private String format(String time) {

		StringBuffer id = new StringBuffer();

		String tmp = time;
		Pattern p = Pattern.compile("\\d");
		Matcher m = p.matcher(tmp);
		while (m.find()) {
			id.append(m.group());
		}

		return id.toString().substring(0, 8);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		apiServiceInterface = null;
	}

}
