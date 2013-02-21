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
	 * ����
	 */
	public static List<EmoticonsResult> mEmoticonsResults = new ArrayList<EmoticonsResult>();
	/**
	 * ȫ��������
	 */
	public static List<NewsFeedResult> mNewsFeedAllResults = new ArrayList<NewsFeedResult>();
	/**
	 * �������
	 */
	public static List<VisitorResult> mVisitorResults = new ArrayList<VisitorResult>();
	/**
	 * ������
	 */
	public static List<NewsFeedResult> mNewsFeedResults = new ArrayList<NewsFeedResult>();
	/**
	 * ���
	 */
	public static List<AlbumsResult> mAlbumsResults = new ArrayList<AlbumsResult>();
	/**
	 * ״̬
	 */
	public static List<StatusResult> mStatusResults = new ArrayList<StatusResult>();
	/**
	 * ��־
	 */
	public static List<BlogsResult> mBlogResults = new ArrayList<BlogsResult>();
	/**
	 * ��־����
	 */
	public static List<BlogCommentsResult> mBlogCommentsResults = new ArrayList<BlogCommentsResult>();
	/**
	 * ��Ƭ
	 */
	public static List<PhotosResult> mPhotosResults = new ArrayList<PhotosResult>();
	/**
	 * ��Ƭ����
	 */
	public static List<PhotosCommentsResult> mPhotosCommentsResults = new ArrayList<PhotosCommentsResult>();

	/**
	 * ����
	 */
	public static List<FriendsResult> mFriendsResults = new ArrayList<FriendsResult>();
	// ��������ĸ�������
	public static Map<String, List<FriendsResult>> mFriendsMap = new HashMap<String, List<FriendsResult>>();
	// ����ĸ��Ӧ��λ��
	public static Map<String, Integer> mFriendsIndexer = new HashMap<String, Integer>();
	// ����ĸ��
	public static List<String> mFriendsSections = new ArrayList<String>();
	// ����ĸλ�ü�
	public static List<Integer> mFriendsPositions = new ArrayList<Integer>();
	// ÿ���׺��ֵ�λ��
	public static Map<String, Map<String, Integer>> mFriendsFirstNamePosition = new HashMap<String, Map<String, Integer>>();
	// ��ѯ����
	public static List<FriendsFindResult> mFriendsFindResults = new ArrayList<FriendsFindResult>();

	/**
	 * ��ҳ
	 */
	public static List<PageResult> mPageResults = new ArrayList<PageResult>();
	// ��������ĸ�������
	public static Map<String, List<PageResult>> mPageMap = new HashMap<String, List<PageResult>>();
	// ����ĸ��Ӧ��λ��
	public static Map<String, Integer> mPageIndexer = new HashMap<String, Integer>();
	// ����ĸ��
	public static List<String> mPageSections = new ArrayList<String>();
	// ����ĸλ�ü�
	public static List<Integer> mPagePositions = new ArrayList<Integer>();
	// ÿ���׺��ֵ�λ��
	public static Map<String, Map<String, Integer>> mPageFirstNamePosition = new HashMap<String, Map<String, Integer>>();

	/**
	 * �����Ż�
	 */
	public static List<NearByResult> mNearByResults = new ArrayList<NearByResult>();
	
	/**
	 * �����ص�
	 */
	public static List<CurrentLocationResult> mCurrentLocationResults = new ArrayList<CurrentLocationResult>();

}
