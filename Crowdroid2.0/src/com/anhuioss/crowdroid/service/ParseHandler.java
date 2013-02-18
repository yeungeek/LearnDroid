package com.anhuioss.crowdroid.service;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.data.info.CalendarInfo;
import com.anhuioss.crowdroid.data.info.EmotionInfo;
import com.anhuioss.crowdroid.data.info.ListInfo;
import com.anhuioss.crowdroid.data.info.LocationInfo;
import com.anhuioss.crowdroid.data.info.POIinfo;
import com.anhuioss.crowdroid.data.info.RoadInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.TrendInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.data.info.transfersInfo;
import com.anhuioss.crowdroid.service.cfb.CfbParseHandler;
import com.anhuioss.crowdroid.service.renren.RenRenParserHandler;
import com.anhuioss.crowdroid.service.sina.SinaCommHandler;
import com.anhuioss.crowdroid.service.sina.SinaParserHandler;
import com.anhuioss.crowdroid.service.sohu.SohuParserHandler;
import com.anhuioss.crowdroid.service.tencent.TencentParserHandler;
import com.anhuioss.crowdroid.service.twitter.TwitterParseHandler;
import com.anhuioss.crowdroid.service.twitterproxy.TwitterProxyParseHandler;
import com.anhuioss.crowdroid.service.wangyi.WangyiParserHandler;

public class ParseHandler {

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

	public static final int TYPE_REGISTER_MESSAGE_TO_API = 37;

	public static final int TYPE_GET_COMMENTS_BY_ID = 38;

	public static final int TYPE_UPDATE_COMMENTS = 39;

	public static final int TYPE_GET_TREND_LIST = 40;

	public static final int TYPE_GET_TREND_TIMELINE = 41;

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

	public static final int TYPE_GET_CFB_SCADULE_TYPE = 86;

	public static final int TYPE_GET_CFB_USER = 87;

	public static final int TYPE_GET_CFB_MONTH_SCHEDULE = 89;

	public static final int TYPE_GET_HOT_FOLLOWRETWEET_TIMELINE = 90;

	public static final int TYPE_GET_COLUMN_TIME_LINE = 91;

	public static final int TYPE_GET_USER_COLUMN_TIME_LINE = 92;

	public static final int TYPE_GET_LBS_NEAR_POI = 93;

	public static final int TYPE_GET_LBS_HERE_USER = 94;

	public static final int TYPE_GET_LBS_NEAR_USER = 95;

	public static final int TYPE_GET_LBS_HERE_TIMELINE = 96;

	public static final int TYPE_GET_LBS_NEAR_TIMELINE = 97;

	public static final int TYPE_GET_LBS_HERE_COMMENT = 98;

	public static final int TYPE_GET_LBS_NEAR_COMMENT = 99;

	public static final int TYPE_GET_LBS_HERE_PHOTO = 100;

	public static final int TYPE_GET_LBS_NEAR_PHOTO = 101;

	public static final int TYPE_LBS_SEND_STATUS = 102;

	public static final int TYPE_LBS_SEND_PHOTO = 103;

	public static final int TYPE_LBS_SEND_COMMENT = 104;

	public static final int TYPE_LBS_GET_USER_POI = 105;

	public static final int TYPE_LBS_GET_SEARCH_POI = 106;

	public static final int TYPE_LBS_GET_SEARCH_POI_BY_GEO = 107;

	public static final int TYPE_LBS_GET_WAY_CAR = 108;

	public static final int TYPE_LBS_GET_WAY_BUS = 109;

	public static final int TYPE_CFB_PHOTO_COMMENT_LIST = 113;

	public static final int TYPE_CFB_GET_PHOTO = 117;

	public static final int TYPE_CFB_GET_DOCUMENT = 118;

	public static final int TYPE_CFB_GET_VEDIO = 119;

	public static final int TYPE_GET_ALBUM_DOCUMENT = 120;

	public static final int TYPE_GET_ALBUM_VIDEO = 121;

	public static final int TYPE_CFB_COMMENT_DOCUMENT = 122;

	public static final int TYPE_CFB_COMMENT_VIDEO = 123;

	public static final int TYPE_CFB_DOCUMENT_COMMENT_LIST = 129;

	public static final int TYPE_CFB_VIDEO_COMMENT_LIST = 130;

	public static final int TYPE_CFB_DETAIL_MSG = 131;

	public Object parser(String service, int type, String statusCode,
			String message) {

		// Parser
		try {
			switch (type) {
			case TYPE_GET_HOME_TIMELINE: {

				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();

				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					// Crowdroid For Business
					timeLineInfoList = CfbParseHandler.parseTimeLine(message);

				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// Twitter
					timeLineInfoList = TwitterParseHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					// Twitter Proxy
					timeLineInfoList = TwitterProxyParseHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					// Sina
					timeLineInfoList = SinaParserHandler.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					timeLineInfoList = TencentParserHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Sohu
					timeLineInfoList = SohuParserHandler.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					// RenRen
					timeLineInfoList = RenRenParserHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// 163
					timeLineInfoList = WangyiParserHandler.parseTimeline(
							message, "home_count");
				}

				return timeLineInfoList;
			}
			case TYPE_GET_AT_MESSAGE: {

				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();

				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					// Crowdroid For Business
					timeLineInfoList = CfbParseHandler.parseTimeLine(message);

				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// Twitter
					timeLineInfoList = TwitterParseHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					// Twitter Proxy
					timeLineInfoList = TwitterProxyParseHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					// Sina
					timeLineInfoList = SinaParserHandler.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					timeLineInfoList = TencentParserHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Sohu
					timeLineInfoList = SohuParserHandler.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					// RenRen share
					timeLineInfoList = RenRenParserHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// 163
					timeLineInfoList = WangyiParserHandler.parseTimeline(
							message, "at");
				}

				return timeLineInfoList;

			}
			case TYPE_GET_DIRECT_MESSAGE_RECEIVE: {

				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();

				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					// Crowdroid For Business
					timeLineInfoList = CfbParseHandler.parseDirectMessage(
							message, "received");

				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// Twitter
					timeLineInfoList = TwitterParseHandler
							.parseDirectMessage(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					// Twitter Proxy
					timeLineInfoList = TwitterProxyParseHandler
							.parseDirectMessage(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					// Sina
					timeLineInfoList = SinaParserHandler
							.parseDirectMessageReceived(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					timeLineInfoList = TencentParserHandler
							.parseDirectMessageReceived(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Sohu
					timeLineInfoList = SohuParserHandler
							.parseDirectMessageReceived(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					// RenRen
					timeLineInfoList = RenRenParserHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// 163
					timeLineInfoList = WangyiParserHandler
							.parseDirectMessageReceived(message);
				}

				return timeLineInfoList;
			}
			case TYPE_GET_DIRECT_MESSAGE_SEND: {

				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();

				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					// Crowdroid For Business
					timeLineInfoList = CfbParseHandler.parseDirectMessage(
							message, "sent");

				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// Twitter
					timeLineInfoList = TwitterParseHandler
							.parseDirectMessage(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					// Twitter Proxy
					timeLineInfoList = TwitterProxyParseHandler
							.parseDirectMessage(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					// Sina
					timeLineInfoList = SinaParserHandler
							.parseDirectMessageSent(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					timeLineInfoList = TencentParserHandler
							.parseDirectMessageSent(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Sohu
					timeLineInfoList = SohuParserHandler
							.parseDirectMessageSent(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					// RenRen
					timeLineInfoList = RenRenParserHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// 163
					timeLineInfoList = WangyiParserHandler
							.parseDirectMessageSent(message);
				}

				return timeLineInfoList;
			}
			case TYPE_GET_MY_TIME_LINE: {

				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();

				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					// Crowdroid For Business
					timeLineInfoList = CfbParseHandler.parseTimeLine(message);

				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// Twitter
					timeLineInfoList = TwitterParseHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					// Twitter Proxy
					timeLineInfoList = TwitterProxyParseHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					// Sina
					timeLineInfoList = SinaParserHandler.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					timeLineInfoList = TencentParserHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Sohu
					timeLineInfoList = SohuParserHandler.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					// RenRen
					timeLineInfoList = RenRenParserHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// 163
					timeLineInfoList = WangyiParserHandler.parseTimeline(
							message, "home");
				}

				return timeLineInfoList;

			}
			case TYPE_UPDATE_STATUS: {

				break;
			}
			case TYPE_DESTROY: {

				break;
			}
			case TYPE_VERIFY_USER: {

				UserInfo userInfo = new UserInfo();

				// VERIFY USER
				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					// Crowdroid For Business
					userInfo = CfbParseHandler.parseUserInfo(message);

				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// Twitter
					userInfo = TwitterParseHandler
							.parseUserInformation(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					// Twitter Proxy
					userInfo = TwitterProxyParseHandler.parseUserInfo(message);

				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					// Sina
					userInfo = SinaParserHandler.parseUserInfo(message);

				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					userInfo = TencentParserHandler.parseUserInfo(message);

				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Sohu
					userInfo = SohuParserHandler.parseUserInfo(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					// RenRen
					userInfo = RenRenParserHandler.parseVerifyInfo(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// 163
					userInfo = WangyiParserHandler.parseUserInfo(message);
				}

				return userInfo;
			}
			case TYPE_GET_USER_INFO: {

				UserInfo userInfo = new UserInfo();

				// GET USER INFO
				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					// Crowdroid For Business
					userInfo = CfbParseHandler.parseUserInfo(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// Twitter
					userInfo = TwitterParseHandler
							.parseUserInformation(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					// Twitter Proxy
					userInfo = TwitterProxyParseHandler
							.parseUserInformation(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					// Sina
					userInfo = SinaParserHandler.parseUserInfo(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					userInfo = TencentParserHandler.parseUserInfo(message);

				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Sohu
					userInfo = SohuParserHandler.parseUserInfo(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					// RenRen
					userInfo = RenRenParserHandler.parseUserInfo(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// 163
					userInfo = WangyiParserHandler.parseUserInfo(message);
				}

				return userInfo;

			}
			case TYPE_GET_FRIENDS_LIST: {

				ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();

				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					// Crowdroid For Business

				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// Twitter
					userInfoList = TwitterParseHandler
							.parseFriendsList(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					// Twitter Proxy
					userInfoList = TwitterProxyParseHandler
							.parseFriendsList(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					// Sina
					userInfoList = SinaParserHandler.parseFriendsList(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					userInfoList = TencentParserHandler
							.parseFriendsList(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Sohu
					userInfoList = SohuParserHandler.parseFriendsList(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					// RenRen
					userInfoList = RenRenParserHandler
							.parseFriendsList(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// 163
					userInfoList = WangyiParserHandler
							.parseFriendsList(message);
				}

				return userInfoList;

			}
			case TYPE_GET_FOLLOWERS_LIST: {

				ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();

				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					// Crowdroid For Business

				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// Twitter
					userInfoList = TwitterParseHandler
							.parseFollowersList(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					// Twitter Proxy
					userInfoList = TwitterProxyParseHandler
							.parseFollowersList(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					// Sina
					userInfoList = SinaParserHandler
							.parseFollowersList(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					userInfoList = TencentParserHandler
							.parseFollowersList(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Sohu
					userInfoList = SohuParserHandler.parseAtInfo(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					// RenRen
					userInfoList = RenRenParserHandler
							.parseVisitorList(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// 163
					userInfoList = WangyiParserHandler
							.parseFollowersList(message);
				}

				return userInfoList;

			}
			case TYPE_SHOW_RELATION: {

				String[] relation = new String[] {};

				// GET USER INFO
				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					// Crowdroid For Business

				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// Twitter
					relation = TwitterParseHandler.parseRelation(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					// Sina
					relation = SinaParserHandler.parseRelationNew(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Sohu
					relation = SohuParserHandler.parseRelationNew(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// 163
					relation = WangyiParserHandler.parseRelationNew(message);
				}

				return relation;

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

				break;
			}
			case TYPE_SET_FOLLOW: {

				break;
			}
			case TYPE_UPLOAD_IMAGE: {

				String imageUrl = null;

				// VERIFY USER
				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					// Crowdroid For Business

				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// Twitter
					imageUrl = TwitterParseHandler.parseUploadImage(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					// Twitter Proxy
					imageUrl = TwitterProxyParseHandler
							.parseUploadImage(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// Twitter
					imageUrl = WangyiParserHandler.parseUploadImage(message);
				}
				return imageUrl;

			}
			case TYPE_RETWEET: {

				break;
			}
			case TYPE_GET_FIND_PEPPLE_INFO: {

				ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();

				// VERIFY USER
				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					// Crowdroid For Business
					userInfoList = CfbParseHandler.parseFriendsList(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// Twitter
					userInfoList = TwitterParseHandler
							.parseStrangersInfo(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					// Twitter Proxy
					userInfoList = TwitterProxyParseHandler
							.parseStrangersInfo(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					// Sina
					userInfoList = SinaParserHandler
							.parseUserSearchInfo(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					userInfoList = TencentParserHandler
							.parseStrangersInfo(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Sohu
					userInfoList = SohuParserHandler
							.parseStrangersInfo(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					// RenRen
					userInfoList = RenRenParserHandler
							.parseSerchUserList(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// 163
					userInfoList = WangyiParserHandler
							.parseStrangersInfo(message);
				}

				return userInfoList;
			}
			case TYPE_GET_FAVORITE_LIST: {

				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();

				// VERIFY USER
				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					// Crowdroid For Business
					timeLineInfoList = CfbParseHandler
							.parseFavoriteTimeLine(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// Twitter
					timeLineInfoList = TwitterParseHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					// Twitter Proxy
					timeLineInfoList = TwitterProxyParseHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					// Sina
					timeLineInfoList = SinaParserHandler
							.parseFavouriteTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					timeLineInfoList = TencentParserHandler
							.parseFavoriteList(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Sohu
					timeLineInfoList = SohuParserHandler.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					// RenRen
					timeLineInfoList = RenRenParserHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// 163
					timeLineInfoList = WangyiParserHandler.parseTimeline(
							message, "home");
				}

				return timeLineInfoList;

			}
			case TYPE_GET_USER_STATUS_LIST: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();

				// VERIFY USER
				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					// Crowdroid For Business
					timeLineInfoList = CfbParseHandler.parseTimeLine(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// Twitter
					timeLineInfoList = TwitterParseHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					// Twitter Proxy
					timeLineInfoList = TwitterProxyParseHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					// Sina
					timeLineInfoList = SinaParserHandler.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					timeLineInfoList = TencentParserHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Sohu
					timeLineInfoList = SohuParserHandler.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					// RenRen
					timeLineInfoList = RenRenParserHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// 163
					timeLineInfoList = WangyiParserHandler.parseTimeline(
							message, "home");
				}

				return timeLineInfoList;
			}
			case TYPE_GET_MESSAGE_BY_ID: {

				TimeLineInfo timeLineInfo = new TimeLineInfo();

				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					timeLineInfo = TwitterParseHandler
							.parseReplyStatus(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					timeLineInfo = TwitterProxyParseHandler
							.parseReplyStatus(message);
				}
				return timeLineInfo;
			}
			case TYPE_SET_FAVORITE: {

				break;
			}
			case TYPE_SEARCH_INFO: {

				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();

				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					// Crowdroid For Business
					timeLineInfoList = CfbParseHandler.parseSearchInfo(message);

				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// Twitter
					timeLineInfoList = TwitterParseHandler
							.parseSearchInfo(message);

				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					// Twitter Proxy
					timeLineInfoList = TwitterProxyParseHandler
							.parseSearchInfo(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					// Sina
					timeLineInfoList = SinaParserHandler
							.parseKeywordSearchInfo(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					timeLineInfoList = TencentParserHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Sohu
					timeLineInfoList = SohuParserHandler
							.parseTimelineWithSearch(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// 163
					timeLineInfoList = WangyiParserHandler.parseTimeline(
							message, "home");
				}
				return timeLineInfoList;
			}
			case TYPE_GET_REGISTRATION_ELEMENT: {

				break;
			}
			case TYPE_GET_NEW_TOKEN: {

				break;
			}
			case TYPE_GET_USER_LIST: {

				ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();

				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					// Crowdroid For Business
					userInfoList = CfbParseHandler.parseFollowersList(message);

				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// Twitter
					userInfoList = TwitterParseHandler
							.parseFollowersInfo(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					// Twitter Proxy
					userInfoList = TwitterProxyParseHandler
							.parseFollowersInfo(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					// Sina
					userInfoList = SinaParserHandler
							.parseFollowersInfo(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					userInfoList = TencentParserHandler
							.parseFollowersInfo(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Sohu
					userInfoList = SohuParserHandler.parseAtInfo(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					// RenRen
					userInfoList = RenRenParserHandler
							.parseFriendsList(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// 163
					userInfoList = WangyiParserHandler
							.parseFollowersInfo(message);
				}
				return userInfoList;
			}
			case TYPE_GET_CFB_SETTING: {
				String maxCharactor = new String();

				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					maxCharactor = CfbParseHandler.parseCFBSetting(message);
					return maxCharactor;
				}

				break;
			}
			case TYPE_GET_SHORT_URL: {

				String shortUrl = new String();

				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					// Crowdroid For Business
					shortUrl = CfbParseHandler.parseShortUrl(message);
				}
				return shortUrl;

			}
			case TYPE_GET_MY_LISTS: {

				// Twitter
				ArrayList<ListInfo> listInfoList = new ArrayList<ListInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					listInfoList = TwitterParseHandler.parseLists(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					listInfoList = TwitterProxyParseHandler.parseLists(message);
				}
				return listInfoList;
			}
			case TYPE_GET_LIST_TIMELINE: {

				// Twitter
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					timeLineInfoList = TwitterParseHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					timeLineInfoList = TwitterProxyParseHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					timeLineInfoList = RenRenParserHandler
							.parseTimeline(message);
				}
				return timeLineInfoList;
			}
			case TYPE_GET_FOLLOW_LISTS: {

				// Twitter
				ArrayList<ListInfo> listInfoList = new ArrayList<ListInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					listInfoList = TwitterParseHandler.parseLists(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					listInfoList = TwitterProxyParseHandler.parseLists(message);
				}
				return listInfoList;
			}
			case TYPE_GET_RETWEET_TO_ME_TIME_LINE: {

				// Twitter
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					timeLineInfoList = TwitterParseHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					timeLineInfoList = TwitterProxyParseHandler
							.parseTimeline(message);
				}
				return timeLineInfoList;
			}
			case TYPE_GET_RETWEET_BY_ME_TIME_LINE: {

				// Twitter
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					timeLineInfoList = TwitterParseHandler
							.parseRetweetBy(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					timeLineInfoList = TwitterProxyParseHandler
							.parseRetweetBy(message);
				}
				return timeLineInfoList;
			}
			case TYPE_GET_RETWEET_OF_ME_TIME_LINE: {

				// Twitter
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					timeLineInfoList = TwitterParseHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					timeLineInfoList = TwitterProxyParseHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					// renren 最近来访
					timeLineInfoList = RenRenParserHandler
							.parseVisitors(message);
				}
				return timeLineInfoList;
			}
			case TYPE_REGISTER_MESSAGE_TO_API: {

				// Twitter
				String url = null;
				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					url = TwitterParseHandler
							.parseRegisterMessageToApi(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					url = TwitterParseHandler
							.parseRegisterMessageToApi(message);
				}
				return url;

			}
			case TYPE_GET_COMMENTS_BY_ID: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					timeLineInfoList = CfbParseHandler
							.parseCommentTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					timeLineInfoList = SinaParserHandler
							.parseCommentTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					timeLineInfoList = TencentParserHandler
							.parseCommentTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Sohu
					timeLineInfoList = SohuParserHandler
							.parseCommentTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					// RenRen
					timeLineInfoList = RenRenParserHandler
							.parseCommentTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// 163
					timeLineInfoList = WangyiParserHandler
							.parseCommentTimeline(message);
				}
				return timeLineInfoList;
			}
			case TYPE_UPDATE_COMMENTS: {
				break;
			}
			case TYPE_GET_TREND_LIST: {
				ArrayList<TrendInfo> trendLists = new ArrayList<TrendInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					trendLists = SinaParserHandler.parseTrendList(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					trendLists = TencentParserHandler.parseTrendList(message);
				}
				return trendLists;
			}
			case TYPE_GET_TREND_TIMELINE: {
				ArrayList<TimeLineInfo> trendTimelineList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					trendTimelineList = SinaParserHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					trendTimelineList = TencentParserHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Sohu
					trendTimelineList = TencentParserHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// 163
					trendTimelineList = WangyiParserHandler.parseTimeline(
							message, "home");
				}
				return trendTimelineList;
			}
			case TYPE_NOTIFICATION_UNREAD_MESSAGE: {
				String[] count = new String[2];
				if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					count = SinaParserHandler.parseUnreadMessage(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					count = TencentParserHandler.parseUnreadMessage(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Sohu
					count = SohuParserHandler.parseUnreadMessage(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// 163
					count = WangyiParserHandler.parseUnreadMessage(message);
				}
				return count;
			}
			case TYPE_NOTIFICATION_FOLLOW: {

			}
			case TYPE_NOTIFICATION_LEAVE: {

			}
			case TYPE_GET_EMOTION: {
				ArrayList<EmotionInfo> emotionList = new ArrayList<EmotionInfo>();
				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					emotionList = CfbParseHandler.parseEmotions(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					emotionList = SinaParserHandler.parseEmotions(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					emotionList = TencentParserHandler.parseEmotions(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					emotionList = RenRenParserHandler.parseEmotions(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					emotionList = SinaParserHandler.parseEmotions(message);
				}
				return emotionList;
			}
			case TYPE_GET_HOT_USERS: {
				ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					userInfoList = SinaParserHandler.parseHotUsers(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					userInfoList = TencentParserHandler.parseHotUsers(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// Tencent
					userInfoList = WangyiParserHandler.parseHotUsers(message);
				}
				return userInfoList;

			}
			case TYPE_GET_SUGGESTION_USERS: {
				ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					userInfoList = SinaParserHandler.parseHotUsers(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					userInfoList = TencentParserHandler.parseHotUsers(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// Twitter
					userInfoList = TwitterParseHandler.parseHotUsers(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// Twitter
					userInfoList = WangyiParserHandler.parseHotUsers(message);
				}
				return userInfoList;
			}
			case TYPE_CLEAR_UNREAD_MESSAGE: {

			}
			case TYPE_GET_COMMENT_TIMELINE: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					timeLineInfoList = CfbParseHandler
							.parseUserCommentTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					timeLineInfoList = SinaParserHandler
							.parseUserCommentTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					timeLineInfoList = TencentParserHandler
							.parseUserCommentTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Sohu
					timeLineInfoList = SohuParserHandler
							.parseUserCommentTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// Sohu
					timeLineInfoList = WangyiParserHandler
							.parseUserCommentTimeline(message);
				}
				return timeLineInfoList;
			}
			case TYPE_GET_TIMELINE_BY_ID: {
				TimeLineInfo timeLineInfo = new TimeLineInfo();
				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					timeLineInfo = CfbParseHandler.parseCommentById(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					timeLineInfo = SinaParserHandler.parseTimelineById(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					// Sohu
					timeLineInfo = SohuParserHandler.parseTimelineById(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// 163
					timeLineInfo = WangyiParserHandler
							.parseTimelineById(message);
				}
				return timeLineInfo;
			}
			case TYPE_GET_TRENDS_BY_TYPE: {
				ArrayList<TrendInfo> trendLists = new ArrayList<TrendInfo>();
				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					trendLists = CfbParseHandler.parseTrendByType(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					trendLists = SinaParserHandler
							.parseTrendListByType(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)
						|| service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					trendLists = TwitterParseHandler
							.parseTrendListByType(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					trendLists = TencentParserHandler
							.parseTrendListByType(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					// 163
					trendLists = WangyiParserHandler
							.parseTrendListByType(message);
				}
				return trendLists;
			}
			case TYPE_GET_LOCATIONS_AVAILIABLE_TRENDS: {
				ArrayList<TrendInfo> trendLists = new ArrayList<TrendInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)
						|| service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					trendLists = TwitterParseHandler
							.parseTrendsAvailable(message);
				}
				return trendLists;
			}
			case TYPE_GET_TRENDS_BY_WOEID: {
				ArrayList<TrendInfo> trendLists = new ArrayList<TrendInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)
						|| service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					trendLists = TwitterParseHandler
							.parseTrendsByWoeid(message);
				}
				return trendLists;
			}
			case TYPE_GET_PUBLIC_TIMELINE: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();

				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					// Twitter
					timeLineInfoList = TwitterParseHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					// Twitter-proxy
					timeLineInfoList = TwitterParseHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					timeLineInfoList = SinaParserHandler.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
					timeLineInfoList = SohuParserHandler.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					timeLineInfoList = TencentParserHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					timeLineInfoList = WangyiParserHandler.parseTimeline(
							message, "home");
				}
				return timeLineInfoList;
			}
			case TYPE_GET_HOT_RETWEET_TIMELINE: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					timeLineInfoList = TencentParserHandler
							.parseTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					timeLineInfoList = WangyiParserHandler
							.parseTimeline1(message);
				}
				return timeLineInfoList;
			}
			case TYPE_GET_RETWEETED_LIST_BY_ID: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					timeLineInfoList = TwitterParseHandler
							.parseRetweetedListById(message);
				}
				return timeLineInfoList;
			}

			case TYPE_GET_RETWEETED_USER_LIST_BY_ID: {
				ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					userInfoList = TwitterParseHandler
							.parseRetweetedUserListById(message);
				}
				return userInfoList;
			}
			case TYPE_UPDATE_PROFILE: {
				break;
			}
			case TYPE_GET_SUGGESTION_SLUG: {
				ArrayList<ListInfo> listInfoList = new ArrayList<ListInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)
						|| service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					listInfoList = TwitterParseHandler
							.parseSuggestionSulg(message);
				}
				return listInfoList;
			}
			case TYPE_GET_GROUP_LIST_SLUG: {
				ArrayList<ListInfo> listInfoList = new ArrayList<ListInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)
						|| service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					listInfoList = TwitterParseHandler
							.parseGroupListSulg(message);
				}
				return listInfoList;
			}
			case TYPE_GET_GROUP_TIMELINE: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)
						|| service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					timeLineInfoList = TwitterParseHandler
							.parseTimeline(message);
				}
				return timeLineInfoList;
			}
			case TYPE_GET_GROUP_USER_LIST: {
				ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)
						|| service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					// Twitter
					userInfoList = TwitterParseHandler
							.parseGroupUserList(message);
				}
				return userInfoList;
			}
			case TYPE_GET_STATUS_TIMELINE: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					timeLineInfoList = RenRenParserHandler
							.parseStatusList(message);
				}
				return timeLineInfoList;
			}
			case TYPE_GET_BLOG_TIMELINE: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					timeLineInfoList = RenRenParserHandler
							.parseBlogTimeline(message);
				}
				return timeLineInfoList;
			}
			case TYPE_GET_ALBUMS_TIMELINE: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					timeLineInfoList = RenRenParserHandler
							.parseAlbumsList(message);
				} else if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					timeLineInfoList = CfbParseHandler.parseAlbumsList(message);
				}
				return timeLineInfoList;
			}
			case TYPE_GET_BLOG_CONTENT: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					timeLineInfoList = RenRenParserHandler
							.parseBlogContent(message);
				}
				return timeLineInfoList;
			}
			case TYPE_UPDATE_BLOG: {
				break;
			}
			case TYPE_CREATE_NEW_ALBUM: {
				break;
			}
			case TYPE_GET_ALBUM_PHOTOS: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					timeLineInfoList = RenRenParserHandler
							.parseAlbumPhotos(message);
				} else if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					timeLineInfoList = CfbParseHandler
							.parseAlbumPhotos(message);
				}
				return timeLineInfoList;
			}
			case TYPE_GET_PAGE_CATEGORY: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					timeLineInfoList = RenRenParserHandler
							.parsePageCategory(message);
				}
				return timeLineInfoList;
			}
			case TYPE_GET_PAGE_LIST_BY_CATEGORY: {
				ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
					userInfoList = RenRenParserHandler
							.parsePageListByCategory(message);
				}
				return userInfoList;
			}
			case TYPE_UPDATE_LBS_MESSAGE: {
				break;
			}
			case TYPE_GET_LBS_TIMELINE: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					timeLineInfoList = TwitterParseHandler
							.parseLBSTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					timeLineInfoList = TencentParserHandler
							.parseLBSTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					timeLineInfoList = SinaParserHandler
							.parseLBSTimeline(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					timeLineInfoList = WangyiParserHandler.parseTimeline(
							message, "home");
				}
				return timeLineInfoList;
			}
			case TYPE_GET_AROUNG_PEOPLE_BY_LBS: {
				ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					userInfoList = TencentParserHandler.parseLBSPeople(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					userInfoList = WangyiParserHandler
							.parseStrangersInfo(message);
				}
				return userInfoList;
			}

			case TYPE_GET_LBS_LOCATION_LIST: {
				ArrayList<LocationInfo> locationInfoList = new ArrayList<LocationInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
					locationInfoList = TwitterParseHandler
							.parseLocationList(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					locationInfoList = TencentParserHandler
							.parseLocationList(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					locationInfoList = SinaParserHandler
							.parseLocationList(message);
				} else if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					locationInfoList = WangyiParserHandler
							.parseLocationList(message);
				}
				return locationInfoList;
			}

			case TYPE_GET_MOOD_STATUS_LIST: {

				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();

				if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {

					timeLineInfoList = TencentParserHandler
							.parseMOODTimeline(message);
				}

				return timeLineInfoList;
			}
			case TYPE_UPDATE_TAGS: {

			}
			case TYPE_GET_USER_TAGS_LIST: {

				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();

				if (service.equals(IGeneral.SERVICE_NAME_SINA)) {

					timeLineInfoList = SinaParserHandler
							.parseUserTagsList(message);
				}

				return timeLineInfoList;
			}
			case TYPE_GET_SUGGESTED_TAGS_LIST: {

				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();

				if (service.equals(IGeneral.SERVICE_NAME_SINA)) {

					timeLineInfoList = SinaParserHandler
							.parseSuggestionTagsList(message);
				}

				return timeLineInfoList;
			}
			case TYPE_GET_FAMOUS_LIST: {

				ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();

				if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {

					userInfoList = TencentParserHandler
							.parseFamousList(message);
				}

				return userInfoList;
			}
			case TYPE_GET_AREA_TIMELINE: {

				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();

				if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
					// Tencent
					timeLineInfoList = TencentParserHandler
							.parseTimeline(message);
				}
				return timeLineInfoList;
			}
			case TYPE_GET_CFB_SCADULE_TYPE: {

				ArrayList<CalendarInfo> timeLineInfoList = new ArrayList<CalendarInfo>();

				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					// cfb
					timeLineInfoList = CfbParseHandler
							.parseCaledarInfo(message);
				}
				return timeLineInfoList;
			}
			case TYPE_GET_CFB_USER: {

				ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();

				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					// cfb
					userInfoList = CfbParseHandler
							.parseScheduleUserInfo(message);
				}
				return userInfoList;
			}
			case TYPE_GET_CFB_MONTH_SCHEDULE: {

				ArrayList<CalendarInfo> calendarInfoList = new ArrayList<CalendarInfo>();

				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					// cfb
					calendarInfoList = CfbParseHandler
							.parseCaledarInfoList(message);
				}
				return calendarInfoList;
			}
			case TYPE_GET_HOT_FOLLOWRETWEET_TIMELINE: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					timeLineInfoList = WangyiParserHandler
							.parseTimeline1(message);
				}
				return timeLineInfoList;

			}
			case TYPE_GET_COLUMN_TIME_LINE: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					timeLineInfoList = WangyiParserHandler
							.parseColumnTimeline(message);
				}
				return timeLineInfoList;

			}
			case TYPE_GET_USER_COLUMN_TIME_LINE: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_WANGYI)) {
					timeLineInfoList = WangyiParserHandler
							.parseColumnTimeline(message);
				}
				return timeLineInfoList;

			}
			case TYPE_GET_LBS_NEAR_POI: {
				ArrayList<POIinfo> poislist = new ArrayList<POIinfo>();
				if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					poislist = SinaParserHandler.parsenearPOI(message);
				}
				return poislist;
			}
			case TYPE_GET_LBS_HERE_TIMELINE: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					timeLineInfoList = SinaParserHandler.parseTimeline(message);
				}
				return timeLineInfoList;
			}
			case TYPE_GET_LBS_NEAR_TIMELINE: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					timeLineInfoList = SinaParserHandler.parseTimeline(message);
				}
				return timeLineInfoList;
			}
			case TYPE_GET_LBS_HERE_COMMENT: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					timeLineInfoList = SinaParserHandler.parseTimeline(message);
				}
				return timeLineInfoList;
			}
			case TYPE_GET_LBS_HERE_USER: {
				ArrayList<UserInfo> timeLineInfoList = new ArrayList<UserInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					timeLineInfoList = SinaParserHandler.parseuserList(message);
				}
				return timeLineInfoList;
			}
			case TYPE_GET_LBS_NEAR_USER: {
				ArrayList<UserInfo> timeLineInfoList = new ArrayList<UserInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					timeLineInfoList = SinaParserHandler.parseuserList(message);
				}
				return timeLineInfoList;
			}
			case TYPE_GET_LBS_HERE_PHOTO: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					timeLineInfoList = SinaParserHandler.parseTimeline(message);
				}
				return timeLineInfoList;
			}
			case TYPE_GET_LBS_NEAR_PHOTO: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					timeLineInfoList = SinaParserHandler.parseTimeline(message);
				}
				return timeLineInfoList;
			}
			case TYPE_LBS_GET_USER_POI: {
				ArrayList<POIinfo> poislist = new ArrayList<POIinfo>();
				if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					poislist = SinaParserHandler.parseuserPOI(message);
				}
				return poislist;
			}
			case TYPE_LBS_GET_SEARCH_POI: {
				ArrayList<POIinfo> poislist = new ArrayList<POIinfo>();
				if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					poislist = SinaParserHandler.parsesearchPOI(message);
				}
				return poislist;
			}
			case TYPE_LBS_GET_SEARCH_POI_BY_GEO: {
				ArrayList<POIinfo> poislist = new ArrayList<POIinfo>();
				if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					poislist = SinaParserHandler.parsesearchPOI(message);
				}
				return poislist;
			}
			case TYPE_LBS_GET_WAY_CAR: {
				ArrayList<RoadInfo> poislist = new ArrayList<RoadInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					poislist = SinaParserHandler.parsewaycar(message);
				}
				return poislist;
			}
			case TYPE_LBS_GET_WAY_BUS: {
				ArrayList<transfersInfo> poislist = new ArrayList<transfersInfo>();
				if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
					poislist = SinaParserHandler.parsewaybus(message);
				}
				return poislist;
			}
			case TYPE_CFB_PHOTO_COMMENT_LIST: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					timeLineInfoList = CfbParseHandler
							.parsePhotoCommentList(message);
				}
				return timeLineInfoList;
			}
			case TYPE_CFB_GET_VEDIO: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					timeLineInfoList = CfbParseHandler.parseVideosList(message);
				}
				return timeLineInfoList;

			}
			case TYPE_CFB_GET_DOCUMENT: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					timeLineInfoList = CfbParseHandler
							.parseDocumentsList(message);
				}
				return timeLineInfoList;

			}
			case TYPE_GET_ALBUM_DOCUMENT: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					timeLineInfoList = CfbParseHandler
							.parseAlbumDocuments(message);
				}
				return timeLineInfoList;

			}
			case TYPE_GET_ALBUM_VIDEO: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					timeLineInfoList = CfbParseHandler
							.parseAlbumVideos(message);
				}
				return timeLineInfoList;

			}
			case TYPE_CFB_DOCUMENT_COMMENT_LIST: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					timeLineInfoList = CfbParseHandler
							.parsePhotoCommentList(message);
				}
				return timeLineInfoList;
			}
			case TYPE_CFB_VIDEO_COMMENT_LIST: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					timeLineInfoList = CfbParseHandler
							.parsePhotoCommentList(message);
				}
				return timeLineInfoList;
			}
			case TYPE_CFB_DETAIL_MSG: {
				ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
				if (service
						.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
					timeLineInfoList = CfbParseHandler.parseDetailMag(message);
				}
				return timeLineInfoList;
			}
			default: {
			}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ArrayList<Object>();

	}

}
