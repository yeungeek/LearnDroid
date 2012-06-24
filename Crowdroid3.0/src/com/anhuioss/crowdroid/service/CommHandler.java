package com.anhuioss.crowdroid.service;

import java.util.Map;

import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.service.cfb.CfbCommHandler;
import com.anhuioss.crowdroid.service.renren.RenRenCommHandler;
import com.anhuioss.crowdroid.service.sina.SinaCommHandler;
import com.anhuioss.crowdroid.service.sohu.SohuCommHandler;
import com.anhuioss.crowdroid.service.tencent.TencentCommHandler;
import com.anhuioss.crowdroid.service.twitter.TwitterCommHandler;
import com.anhuioss.crowdroid.service.twitterproxy.TwitterProxyCommHandler;
import com.anhuioss.crowdroid.util.CommResult;

public class CommHandler {

	// Type Id
	public static final int TYPE_GET_HOME_TIMELINE = 0;

	public static final int TYPE_GET_AT_MESSAGE = 1;

	public static final int TYPE_GET_DIRECT_MESSAGE_RECEIVE = 2;

	public static final int TYPE_GET_DIRECT_MESSAGE_SEND = 3;

	public static final int TYPE_GET_MY_TIME_LINE = 4;

	public static final int TYPE_UPDATE_STATUS = 5;

	public static final int TYPE_DESTROY = 6;

	public static final int TYPE_VERIFY_USER = 7;

	public static final int TYPE_GET_USER_INFO = 8;

	public static final int TYPE_GET_FRIENDS_LIST = 9;

	public static final int TYPE_GET_FOLLOWERS_LIST = 10;

	public static final int TYPE_SHOW_RELATION = 11;

	public static final int TYPE_CHECK_NEWEST_AT_MESSAGE = 12;

	public static final int TYPE_CHECK_NEWEST_DIRECT_MESSAGE = 13;

	public static final int TYPE_CHECK_NEWEST_GENERAL_MESSAGE = 14;

	public static final int TYPE_DIRECT_MESSAGE = 15;

	public static final int TYPE_SET_FOLLOW = 16;

	public static final int TYPE_UPLOAD_IMAGE = 17;

	public static final int TYPE_RETWEET = 18;

	public static final int TYPE_GET_FIND_PEPPLE_INFO = 19;

	public static final int TYPE_GET_FAVORITE_LIST = 20;

	public static final int TYPE_GET_USER_STATUS_LIST = 21;

	public static final int TYPE_GET_MESSAGE_BY_ID = 22;

	public static final int TYPE_SET_FAVORITE = 23;

	public static final int TYPE_SEARCH_INFO = 24;

	public static final int TYPE_GET_REGISTRATION_ELEMENT = 25;

	public static final int TYPE_GET_NEW_TOKEN = 26;

	public static final int TYPE_GET_USER_LIST = 27;

	public static final int TYPE_GET_CFB_SETTING = 28;

	public static final int TYPE_GET_SHORT_URL = 29;

	public static final int TYPE_GET_MY_LISTS = 30;

	public static final int TYPE_GET_LIST_TIMELINE = 31;

	public static final int TYPE_GET_FOLLOW_LISTS = 32;

	public static final int TYPE_GET_RETWEET_TO_ME_TIME_LINE = 33;

	public static final int TYPE_GET_RETWEET_BY_ME_TIME_LINE = 34;

	public static final int TYPE_GET_RETWEET_OF_ME_TIME_LINE = 35;

	public static final int TYPE_SET_FOLLOW_LIST = 36;

	public static final int TYPE_REGISTER_MESSAGE_TO_API = 37;

	public static final int TYPE_GET_COMMENTS_BY_ID = 38;

	public static final int TYPE_UPDATE_COMMENTS = 39;

	public static final int TYPE_GET_TREND_LIST = 40;

	public static final int TYPE_GET_TREND_TIMELNE = 41;

	public static final int TYPE_NOTIFICATION_UNREAD_MESSAGE = 42;

	public static final int TYPE_NOTIFICATION_FOLLOW = 43;

	public static final int TYPE_GET_EMOTION = 44;

	public static final int TYPE_NOTIFICATION_LEAVE = 45;

	public static final int TYPE_GET_HOT_USERS = 46;

	public static final int TYPE_GET_SUGGESTION_USERS = 47;

	public static final int TYPE_CLEAR_UNREAD_MESSAGE = 48;

	public static final int TYPE_GET_COMMENT_TIMELINE = 49;

	public static final int TYPE_REPLY_TO_COMMENT = 50;

	public static final int TYPE_GET_TIMELINE_BY_ID = 51;

	public static final int TYPE_GET_TRENDS_BY_TYPE = 52;

	public static final int TYPE_GET_ACCESS_TOKEN = 53;

	public static final int TYPE_GET_LOCATIONS_AVAILIABLE_TRENDS = 54;

	public static final int TYPE_GET_TRENDS_BY_WOEID = 55;

	public static final int TYPE_GET_PUBLIC_TIMELINE = 56;

	public static final int TYPE_GET_HOT_RETWEET_TIMELINE = 57;

	public static final int TYPE_GET_RETWEETED_LIST_BY_ID = 58;

	public static final int TYPE_GET_RETWEETED_USER_LIST_BY_ID = 59;

	public static final int TYPE_UPDATE_PROFILE = 60;

	public static final int TYPE_GET_SUGGESTION_SLUG = 61;

	public static final int TYPE_GET_GROUP_LIST_SLUG = 62;

	public static final int TYPE_GET_GROUP_TIMELINE = 63;

	public static final int TYPE_GET_GROUP_USER_LIST = 64;

	// RenRen------------more--------------------------
	public static final int TYPE_GET_STATUS_TIMELINE = 65;

	public static final int TYPE_GET_BLOG_TIMELINE = 66;

	public static final int TYPE_GET_ALBUMS_TIMELINE = 67;

	public static final int TYPE_GET_BLOG_CONTENT = 68;

	public static final int TYPE_UPDATE_BLOG = 69;

	public static final int TYPE_CREATE_NEW_ALBUM = 70;

	public static final int TYPE_GET_ALBUM_PHOTOS = 71;

	public static final int TYPE_GET_PAGE_CATEGORY = 72;

	public static final int TYPE_GET_PAGE_LIST_BY_CATEGORY = 73;

	public static final int TYPE_UPDATE_LBS_MESSAGE = 74;

	public static final int TYPE_GET_LBS_TIMELINE = 75;

	public static final int TYPE_GET_AROUNG_PEOPLE_BY_LBS = 76;

	public static final int TYPE_GET_LBS_LOCATION_LIST = 77;

	public static final int TYPE_UPDATE_USER_IMAGE = 78;

	public static final int TYPE_UPDATE_MOOD_STATUS = 79;

	public static final int TYPE_GET_MOOD_STATUS_LIST = 80;

	public static final int TYPE_UPDATE_TAGS = 81;

	public static final int TYPE_GET_USER_TAGS_LIST = 82;

	public static final int TYPE_GET_SUGGESTED_TAGS_LIST = 83;
	
	public static final int TYPE_GET_FAMOUS_LIST = 84;
	
	public static final int TYPE_GET_AREA_TIMELINE = 85;

	public CommResult request(String service, int type, Map map) {

		CommResult result = new CommResult();

		// HTTP Communication
		switch (type) {
		case TYPE_GET_HOME_TIMELINE: {

			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business
				// result = CfbCommHandler.getVersionMessage(map);
				result = CfbCommHandler.getHomeTimeline(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.getHomeTimeLine(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.getHomeTimeLine(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.getHomeTimeline(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.getHomeTimeline(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.getHomeTimeline(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.getHomeTimeline(map);
			}

			break;
		}
		case TYPE_GET_AT_MESSAGE: {
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business
				result = CfbCommHandler.getAtMessageTimeline(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.getAtMessage(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.getAtMessage(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.getAtMessageTimeline(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.getAtMessageTimeline(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.getAtMessageTimeline(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.getHomeShareTimeline(map);
			}

			break;
		}
		case TYPE_GET_DIRECT_MESSAGE_RECEIVE: {
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business
				result = CfbCommHandler.getDirectMessageReceive(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.getDirectMessage(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.getDirectMessage(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.getDirectMessageReceived(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.getDirectMessageReceived(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.getDirectMessageReceived(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.getHomePhotoTimeline(map);
			}
			break;
		}
		case TYPE_GET_DIRECT_MESSAGE_SEND: {
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business
				result = CfbCommHandler.getDirectMessageSend(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.getDirectMessageSent(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.getDirectMessageSent(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.getDirectMessageSent(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.getDirectMessageSent(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.getDirectMessageSent(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.getHomeBlogTimeline(map);
			}

			break;
		}
		case TYPE_GET_MY_TIME_LINE: {

			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business
				result = CfbCommHandler.getMyTimeline(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.getMyTimeLine(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.getMyTimeLine(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.getMyTimeline(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.getMyTimeline(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.getMyTimeline(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.getHomeStateTimeline(map);
			}

			break;
		}
		case TYPE_UPDATE_STATUS: {

			// UPDATE
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business
				result = CfbCommHandler.updateStatus(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.updateStatus(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.updateStatus(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.updateStatus(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.updateStatus(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.updateStatus(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.updateStatus(map);
			}

			break;
		}
		case TYPE_DESTROY: {
			// destory status
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business
				result = CfbCommHandler.destroyStatus(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.destroyStatus(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.destroyStatus(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.destroyStatus(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.destroyStatus(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.destoryStatus(map);
			}

			break;
		}
		case TYPE_VERIFY_USER: {

			// VERIFY USER
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business
				result = CfbCommHandler.valifyUser(map);

			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.verifyUser(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.verifyUser(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.verifyUser(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.verifyUser(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.verifyUser(map);
			}

			break;
		}
		case TYPE_GET_USER_INFO: {

			// GET USER INFO
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business
				result = CfbCommHandler.getUserInfo(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.getUserInfo(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.getUserInfo(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.getUserInfo(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.getUserInfo(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.getUserInfo(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.getUserInfo(map);
			}

			break;

		}
		case TYPE_GET_FRIENDS_LIST: {

			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business

			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.getFriendsList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.getFriendsList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.getFollowList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.getFollowList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.getFollowList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.getFriends(map);
			}

			break;

		}
		case TYPE_GET_FOLLOWERS_LIST: {

			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business

			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.getFollowersList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.getFollowersList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.getFollowersList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.getFollowersList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.getFollowersList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.getVisitors(map);
			}

			break;

		}
		case TYPE_SHOW_RELATION: {

			// DIRECT MESSAGE
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business

			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.showRelation(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.showRelation(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.showRelationNew(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.showRelationNew(map);
			}

			break;

		}

		case TYPE_CHECK_NEWEST_AT_MESSAGE: {

			break;
		}
		case TYPE_CHECK_NEWEST_DIRECT_MESSAGE: {

			break;
		}
		case TYPE_CHECK_NEWEST_GENERAL_MESSAGE: {

			break;
		}
		case TYPE_DIRECT_MESSAGE: {

			// DIRECT MESSAGE
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business
				result = CfbCommHandler.directMessage(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.directMessage(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.directMessage(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.directMessage(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.directMessage(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.directMessage(map);
			}

			break;

		}
		case TYPE_SET_FOLLOW: {

			// UPLOAD INMAGE
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business

			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.setFollow(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.setFollow(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.setFollow(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.setFollow(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.setFollow(map);
			}

			break;

		}
		case TYPE_UPLOAD_IMAGE: {

			// UPLOAD INMAGE
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business
				result = CfbCommHandler.uploadImage(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.uploadImage(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.uploadImage(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.uploadImage(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.uploadImage(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.uploadImage(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.uploadPhotos(map);
			}

			break;

		}
		case TYPE_RETWEET: {

			// RETWEET
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business
				result = CfbCommHandler.retweet(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.retweet(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.retweet(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.retweet(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.retweet(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.retweet(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.retweetState(map);
			}

			break;
		}
		case TYPE_GET_FIND_PEPPLE_INFO: {

			// GET FIND PEPPLE INFO
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business
				result = CfbCommHandler.getFindPeopleInfo(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.getFindPeopleInfo(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.getFindPeopleInfo(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.getFindPeopleInfo(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.getFindPeopleInfo(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.getFindPeopleInfo(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.getSerchFriends(map);
			}

			break;
		}
		case TYPE_GET_FAVORITE_LIST: {

			// GET FIND PEPPLE INFO
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business
				result = CfbCommHandler.getFavoriteList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.getFavoriteList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.getFavoriteList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.getFavoriteList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.getFavoriteList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.getFavoriteList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.getMyTimeline(map);
			}
			break;

		}
		case TYPE_GET_USER_STATUS_LIST: {
			// GET FIND PEPPLE INFO
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business
				result = CfbCommHandler.getUserStatusList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.getUserStatusList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.getUserStatusList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.getUserStatusList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.getUserStatusList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.getUserStatusList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.getMyShareTimeline(map);
			}
			break;

		}
		case TYPE_GET_MESSAGE_BY_ID: {

			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.getMessageById(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				result = TwitterProxyCommHandler.getMessageById(map);
			}
			break;

		}
		case TYPE_SET_FAVORITE: {

			// SEARCH INFOMATION
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business
				result = CfbCommHandler.setFavorite(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.setFavorite(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.setFavorite(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.setFavorite(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.setFavorite(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.setFavorite(map);
			}
			break;

		}
		case TYPE_SEARCH_INFO: {

			// SEARCH INFOMATION
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business
				result = CfbCommHandler.searchInfo(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.searchinfo(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.searchInfo(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.searchInfo(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.searchInfo(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.searchInfo(map);
			}
			break;

		}
		case TYPE_GET_REGISTRATION_ELEMENT: {
			break;
		}
		case TYPE_GET_NEW_TOKEN: {

			// GET NEW TOKEN
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business

			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.getNewToken(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy

			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.getNewToken(map);

			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.getNewToken(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.getNewToken(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.getNewToken(map);
			}
			break;

		}
		case TYPE_GET_USER_LIST: {

			// GET USER LIST
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business
				result = CfbCommHandler.getUserList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.getFollowersList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				// Twitter Proxy
				result = TwitterProxyCommHandler.getFollowersList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.getFollowersList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.getFollowersList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				// Sohu
				result = SohuCommHandler.getFollowersList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.getFriends(map);
			}
			break;

		}
		case TYPE_GET_CFB_SETTING: {

			// CFB
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				result = CfbCommHandler.getCFBsetting(map);
			}
			break;

		}
		case TYPE_GET_SHORT_URL: {

			// GET USER LIST
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				// Crowdroid For Business
				result = CfbCommHandler.getShortUrl(map);
			}
			break;
		}
		case TYPE_GET_MY_LISTS: {

			// Twitter
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.getMyLists(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				result = TwitterProxyCommHandler.getMyLists(map);
			}
			break;
		}
		case TYPE_GET_LIST_TIMELINE: {

			// Twitter
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.getListTimeline(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				result = TwitterProxyCommHandler.getListTimeline(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				result = RenRenCommHandler.getMyStateTimeline(map);
			}
			break;
		}
		case TYPE_GET_FOLLOW_LISTS: {

			// Twitter
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.getFollowLists(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				result = TwitterProxyCommHandler.getFollowLists(map);
			}

			break;
		}
		case TYPE_GET_RETWEET_TO_ME_TIME_LINE: {

			// Twitter
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.getRetweetToMeTimeline(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				result = TwitterProxyCommHandler.getRetweetToMeTimeline(map);
			}
			break;
		}
		case TYPE_GET_RETWEET_BY_ME_TIME_LINE: {

			// Twitter
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.getRetweetByMeTimeline(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				result = TwitterProxyCommHandler.getRetweetByMeTimeline(map);
			}
			break;
		}
		case TYPE_GET_RETWEET_OF_ME_TIME_LINE: {

			// Twitter
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.getRetweetOfMeTimeline(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				result = TwitterProxyCommHandler.getRetweetOfMeTimeline(map);
			}
			// RenRen share
			else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				result = RenRenCommHandler.getVisitors(map);

			}
			break;
		}
		case TYPE_SET_FOLLOW_LIST: {

			// Twitter
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.setFollowList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				result = TwitterProxyCommHandler.setFollowList(map);
			}
			break;
		}
		case TYPE_REGISTER_MESSAGE_TO_API: {

			// Twitter
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.registerMessageToAPI(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				result = TwitterCommHandler.registerMessageToAPI(map);
			}
			break;
		}
		case TYPE_GET_COMMENTS_BY_ID: {

			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				result = CfbCommHandler.getCommentsById(map);
			}// Sina
			else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				result = SinaCommHandler.getCommentsById(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				result = TencentCommHandler.getCommentsById(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				result = SohuCommHandler.getCommentsById(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				result = RenRenCommHandler.getCommentsById(map);
			}

			break;
		}
		case TYPE_UPDATE_COMMENTS: {

			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				result = CfbCommHandler.updateComments(map);
			}// Sina
			else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				result = SinaCommHandler.updateComments(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				result = TencentCommHandler.updateComments(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				result = SohuCommHandler.updateComments(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				result = RenRenCommHandler.updateComments(map);
			}
			break;
		}
		case TYPE_GET_TREND_LIST: {

			if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				result = SinaCommHandler.getTrendList(map);
			}
			break;
		}
		case TYPE_GET_TREND_TIMELNE: {

			// Sina
			if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				result = SinaCommHandler.getTrendTimeline(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				result = TencentCommHandler.getTrendTimeline(map);
			}
			break;
		}
		case TYPE_NOTIFICATION_UNREAD_MESSAGE: {
			// sina
			if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				result = SinaCommHandler.getUnreadMessage(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				result = TencentCommHandler.getUnreadMessage(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				result = SohuCommHandler.getUnreadMessage(map);
			}
			break;
		}
		case TYPE_NOTIFICATION_FOLLOW: {
			// twitter
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.notificationFollow(map);
			}
			break;
		}
		case TYPE_GET_EMOTION: {
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				result = CfbCommHandler.getEmotions(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				result = SinaCommHandler.getEmotions(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				result = TencentCommHandler.getEmotions(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				result = RenRenCommHandler.getEmotions(map);
			}

			break;
		}
		case TYPE_NOTIFICATION_LEAVE: {
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.notificationLeave(map);
			}
			break;
		}
		case TYPE_GET_HOT_USERS: {
			if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				result = SinaCommHandler.getHotUsers(map);
			}
			break;
		}
		case TYPE_GET_SUGGESTION_USERS: {
			if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				result = SinaCommHandler.getSuggestionUsers(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				result = TencentCommHandler.getSuggestionUsers(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.getSuggestionUsers(map);
			}
			break;
		}
		case TYPE_CLEAR_UNREAD_MESSAGE: {
			if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				result = SinaCommHandler.clearUnreadMessage(map);
			}
			break;
		}
		case TYPE_GET_COMMENT_TIMELINE: {
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				result = CfbCommHandler.getCommentsTimeine(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				result = SinaCommHandler.getCommentTimeline(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				result = TencentCommHandler.getCommentTimeline(map);

			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				result = SohuCommHandler.getCommentTimeline(map);
			}
			break;
		}
		case TYPE_REPLY_TO_COMMENT: {
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				result = CfbCommHandler.replyToComment(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				result = SinaCommHandler.replyToComment(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				result = TencentCommHandler.replyToComment(map);
			}
			break;
		}
		case TYPE_GET_TIMELINE_BY_ID: {
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				result = CfbCommHandler.getCommentById(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				result = SinaCommHandler.getTimelineById(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				result = SohuCommHandler.getTimelineById(map);
			}
			break;
		}
		case TYPE_GET_TRENDS_BY_TYPE: {
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				result = CfbCommHandler.getTrendsByType(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				result = SinaCommHandler.getTrendsByType(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.getTrends(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				result = TwitterProxyCommHandler.getTrends(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				result = TencentCommHandler.getTrendsByType(map);
			}
			break;
		}
		case TYPE_GET_ACCESS_TOKEN: {
			if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				result = TencentCommHandler.getAccessToken(map);
			}
			break;
		}

		case TYPE_GET_LOCATIONS_AVAILIABLE_TRENDS: {
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.getLocationsAvailiableTrends(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				result = TwitterProxyCommHandler
						.getLocationsAvailiableTrends(map);
			}
			break;
		}
		case TYPE_GET_TRENDS_BY_WOEID: {
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.getTrends(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				result = TwitterProxyCommHandler.getTrends(map);
			}
			break;
		}
		case TYPE_GET_PUBLIC_TIMELINE: {
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.getPublicTimeLine(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				result = TwitterProxyCommHandler.getPublicTimeLine(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				result = SinaCommHandler.getPublicTimeLine(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				result = SinaCommHandler.getPublicTimeLine(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				result = TencentCommHandler.getPublicTimeLine(map);
			}
			break;
		}
		case TYPE_GET_HOT_RETWEET_TIMELINE: {
			if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				result = TencentCommHandler.getHotRetweetTimeline(map);
			}
			break;
		}
		case TYPE_GET_RETWEETED_LIST_BY_ID: {
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.getRetweetedListById(map);
			}
			break;
		}

		case TYPE_GET_RETWEETED_USER_LIST_BY_ID: {
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.getRetweetedUserListById(map);
			}
			break;
		}
		case TYPE_UPDATE_PROFILE: {
			if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				result = SinaCommHandler.updateProfile(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				result = TencentCommHandler.updateProfile(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
				result = SohuCommHandler.updateProfile(map);
			}
			break;
		}

		case TYPE_GET_SUGGESTION_SLUG: {
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)
					|| service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				result = TwitterCommHandler.getSuggestionSlugs(map);
			}
			break;
		}
		case TYPE_GET_GROUP_LIST_SLUG: {
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.getGroupListSlugs(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				result = TwitterProxyCommHandler.getGroupListSlugs(map);
			}
			break;
		}
		case TYPE_GET_GROUP_TIMELINE: {
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.getGroupTimeline(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				result = TwitterProxyCommHandler.getGroupTimeline(map);
			}
			break;
		}
		case TYPE_GET_GROUP_USER_LIST: {
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.getGroupUserlist(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				result = TwitterProxyCommHandler.getGroupUserlist(map);
			}
			break;
		}
		case TYPE_GET_STATUS_TIMELINE: {
			if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.getProfileStatus(map);
			}
			break;
		}
		case TYPE_GET_BLOG_TIMELINE: {
			if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.getUserBlogList(map);
			}
			break;
		}
		case TYPE_GET_ALBUMS_TIMELINE: {
			if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.getAlbums(map);
			}
			break;
		}
		case TYPE_GET_BLOG_CONTENT: {
			if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.getBlogContent(map);
			}
			break;
		}
		case TYPE_UPDATE_BLOG: {
			if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.updateBlog(map);
			}
			break;
		}
		case TYPE_CREATE_NEW_ALBUM: {
			if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.createNewAlbum(map);
			}
			break;
		}
		case TYPE_GET_ALBUM_PHOTOS: {
			if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.getAlbumPhotos(map);
			}
			break;
		}
		case TYPE_GET_PAGE_CATEGORY: {
			if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.getPageCategory(map);
			}
			break;
		}
		case TYPE_GET_PAGE_LIST_BY_CATEGORY: {
			if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.getPageListByCategory(map);
			}
			break;
		}
		case TYPE_UPDATE_LBS_MESSAGE: {
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.updateLBSMessage(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.updateLBSMessage(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				// RenRen
				result = RenRenCommHandler.UpdateLBSMessage(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.UpdateLBSMessage(map);
			}
			break;
		}
		case TYPE_GET_LBS_TIMELINE: {
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				result = TwitterCommHandler.getLBSTimeLine(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.getLBSTimeline(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.getLBSTimeline(map);
			}
			break;
		}
		case TYPE_GET_AROUNG_PEOPLE_BY_LBS: {
			if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.getLBSPeople(map);
			}
			break;
		}
		case TYPE_GET_LBS_LOCATION_LIST: {
			if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
				// Twitter
				result = TwitterCommHandler.getLBSLocationList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.getLBSLocationList(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				// Sina
				result = SinaCommHandler.getLBSLocationList(map);
			}
			break;
		}
		case TYPE_UPDATE_USER_IMAGE: {
			if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.updateUserImage(map);
			}
			break;
		}
		case TYPE_UPDATE_MOOD_STATUS: {
			if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.updateMoodStatus(map);
			}
			break;
		}
		case TYPE_GET_MOOD_STATUS_LIST: {
			if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				// Tencent
				result = TencentCommHandler.getMoodStatus(map);
			}
			break;
		}
		case TYPE_UPDATE_TAGS: {
			if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				result = SinaCommHandler.updateTags(map);
			} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				result = TencentCommHandler.updateTags(map);
			}
			break;
		}
		case TYPE_GET_USER_TAGS_LIST: {
			if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				result = SinaCommHandler.getUserTagsList(map);
			}
			break;
		}
		case TYPE_GET_SUGGESTED_TAGS_LIST: {
			if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
				result = SinaCommHandler.getSuggestionsTagsList(map);
			}
			break;
		}
		case TYPE_GET_FAMOUS_LIST: {
			if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				result = TencentCommHandler.getFamouslist(map);
			}
			break;
		}
		case TYPE_GET_AREA_TIMELINE: {
			if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
				result = TencentCommHandler.getAreaTimeline(map);
			}
			break;
		}
		default: {
			break;
		}
		}
		System.gc();
		return result;
	}

}
