package com.anhuioss.crowdroid.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.data.SettingData;

public class NotificationTimerReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		// Check Setting Parameter and create Intent Data
		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) context
				.getApplicationContext();
		SettingData settingData = crowdroidApplication.getSettingData();
		boolean isNotification = settingData.isNotification();
		boolean isAt = settingData.isAtMessage();
		boolean isGeneral = settingData.isGeneralMessage();
		boolean isDirect = settingData.isDirectMessage();
		boolean isFollower = settingData.isFollowerMessage();
		boolean istwFollow = settingData.isTwitterFollowerMessage();
		boolean isUnfollow = settingData.isUnfollowerMessage();
		boolean isComment = settingData.isCommentMessage();
		boolean isRetweetOfMe = settingData.isRetweetOfMe();
		boolean isFeedState = settingData.isFeedState();
		boolean isFeedAlbum = settingData.isFeedAlbum();
		boolean isFeedShare = settingData.isFeedShare();
		boolean isFeedBlog = settingData.isFeedBlog();
		// Start Notification Service with Intent Data
		if (isNotification) {

			Intent i = new Intent(context, NotificationService.class);
			i.setAction(NotificationService.ACTION_NOTIFICATION);
			Bundle bundle = new Bundle();
			bundle.putBoolean(NotificationService.INTENT_DATA_AT_CHECK, isAt);
			bundle.putBoolean(NotificationService.INTENT_DATA_NORMAL_CHECK,
					isGeneral);
			bundle.putBoolean(NotificationService.INTENT_DATA_DIRECT_CHECK,
					isDirect);
			bundle.putBoolean(
					NotificationService.INTENT_DATA_TWITTER_FOLLOW_CHECK,
					istwFollow);
			bundle.putBoolean(NotificationService.INTENT_DATA_FOLLOWER_CHECK,
					isFollower);
			bundle.putBoolean(NotificationService.INTENT_DATA_UNFOLLOW_CHECK,
					isUnfollow);
			bundle.putBoolean(NotificationService.INTENT_DATA_COMMENT_CHECK,
					isComment);
			bundle.putBoolean(
					NotificationService.INTENT_DATA_RETWEET_OF_ME_CHECK,
					isRetweetOfMe);
			bundle.putBoolean(NotificationService.INTENT_DATA_FEED_SHARE_CHECK,
					isFeedShare);
			bundle.putBoolean(NotificationService.INTENT_DATA_FEED_STATE_CHECK,
					isFeedState);
			bundle.putBoolean(NotificationService.INTENT_DATA_FEED_BLOG_CHECK,
					isFeedBlog);
			bundle.putBoolean(NotificationService.INTENT_DATA_FEED_ALBUM_CHECK,
					isFeedAlbum);

			i.putExtras(bundle);
			context.startService(i);

		}

	}

}
