package com.renren.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.renren.android.blog.BlogCommentsResult;
import com.renren.android.blog.BlogsResult;
import com.renren.android.emoticons.EmoticonsResult;
import com.renren.android.friends.FriendsFindResult;
import com.renren.android.friends.FriendsResult;
import com.renren.android.location.CurrentLocationResult;
import com.renren.android.location.NearByResult;
import com.renren.android.newsfeed.NewsFeedResult;
import com.renren.android.page.PageResult;
import com.renren.android.photos.AlbumsResult;
import com.renren.android.photos.PhotosCommentsResult;
import com.renren.android.photos.PhotosResult;
import com.renren.android.user.StatusResult;
import com.renren.android.user.VisitorResult;

public class RenRenData {
	/**
	 * 表情
	 */
	public static List<EmoticonsResult> mEmoticonsResults = new ArrayList<EmoticonsResult>();
	/**
	 * 全部新鲜事
	 */
	public static List<NewsFeedResult> mNewsFeedAllResults = new ArrayList<NewsFeedResult>();
	/**
	 * 最近来访
	 */
	public static List<VisitorResult> mVisitorResults = new ArrayList<VisitorResult>();
	/**
	 * 新鲜事
	 */
	public static List<NewsFeedResult> mNewsFeedResults = new ArrayList<NewsFeedResult>();
	/**
	 * 相册
	 */
	public static List<AlbumsResult> mAlbumsResults = new ArrayList<AlbumsResult>();
	/**
	 * 状态
	 */
	public static List<StatusResult> mStatusResults = new ArrayList<StatusResult>();
	/**
	 * 日志
	 */
	public static List<BlogsResult> mBlogResults = new ArrayList<BlogsResult>();
	/**
	 * 日志评论
	 */
	public static List<BlogCommentsResult> mBlogCommentsResults = new ArrayList<BlogCommentsResult>();
	/**
	 * 照片
	 */
	public static List<PhotosResult> mPhotosResults = new ArrayList<PhotosResult>();
	/**
	 * 照片评论
	 */
	public static List<PhotosCommentsResult> mPhotosCommentsResults = new ArrayList<PhotosCommentsResult>();

	/**
	 * 好友
	 */
	public static List<FriendsResult> mFriendsResults = new ArrayList<FriendsResult>();
	// 根据首字母存放数据
	public static Map<String, List<FriendsResult>> mFriendsMap = new HashMap<String, List<FriendsResult>>();
	// 首字母对应的位置
	public static Map<String, Integer> mFriendsIndexer = new HashMap<String, Integer>();
	// 首字母集
	public static List<String> mFriendsSections = new ArrayList<String>();
	// 首字母位置集
	public static List<Integer> mFriendsPositions = new ArrayList<Integer>();
	// 每个首汉字的位置
	public static Map<String, Map<String, Integer>> mFriendsFirstNamePosition = new HashMap<String, Map<String, Integer>>();
	// 查询好友
	public static List<FriendsFindResult> mFriendsFindResults = new ArrayList<FriendsFindResult>();

	/**
	 * 主页
	 */
	public static List<PageResult> mPageResults = new ArrayList<PageResult>();
	// 根据首字母存放数据
	public static Map<String, List<PageResult>> mPageMap = new HashMap<String, List<PageResult>>();
	// 首字母对应的位置
	public static Map<String, Integer> mPageIndexer = new HashMap<String, Integer>();
	// 首字母集
	public static List<String> mPageSections = new ArrayList<String>();
	// 首字母位置集
	public static List<Integer> mPagePositions = new ArrayList<Integer>();
	// 每个首汉字的位置
	public static Map<String, Map<String, Integer>> mPageFirstNamePosition = new HashMap<String, Map<String, Integer>>();

	/**
	 * 附近优惠
	 */
	public static List<NearByResult> mNearByResults = new ArrayList<NearByResult>();
	
	/**
	 * 附近地点
	 */
	public static List<CurrentLocationResult> mCurrentLocationResults = new ArrayList<CurrentLocationResult>();

}
