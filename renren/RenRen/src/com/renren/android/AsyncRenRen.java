package com.renren.android;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.renren.android.blog.BlogAddCommentRequestParam;
import com.renren.android.blog.BlogAddCommentResponseBean;
import com.renren.android.blog.BlogHelper;
import com.renren.android.blog.BlogPublishRequestParam;
import com.renren.android.blog.BlogPublishResponseBean;
import com.renren.android.blog.GetBlogCommentsRequestParam;
import com.renren.android.blog.GetBlogCommentsResponseBean;
import com.renren.android.blog.GetBlogRequestParam;
import com.renren.android.blog.GetBlogResponseBean;
import com.renren.android.blog.GetBlogsRequestParam;
import com.renren.android.blog.GetBlogsResponseBean;
import com.renren.android.emoticons.EmoticonsHelper;
import com.renren.android.emoticons.EmoticonsRequestParam;
import com.renren.android.emoticons.EmoticonsResponseBean;
import com.renren.android.emoticons.EmoticonsResult;
import com.renren.android.friends.FriendsFindRequestParam;
import com.renren.android.friends.FriendsFindResponseBean;
import com.renren.android.friends.FriendsHelper;
import com.renren.android.friends.GetFriendsRequestParam;
import com.renren.android.friends.GetFriendsResponseBean;
import com.renren.android.newsfeed.NewsFeedHelper;
import com.renren.android.newsfeed.NewsFeedPublishRequestParam;
import com.renren.android.newsfeed.NewsFeedPublishResponseBean;
import com.renren.android.newsfeed.NewsFeedRequestParam;
import com.renren.android.newsfeed.NewsFeedResponseBean;
import com.renren.android.page.GetPageInfoRequestParam;
import com.renren.android.page.GetPageInfoResponseBean;
import com.renren.android.page.GetPageRequestParam;
import com.renren.android.page.GetPageResponseBean;
import com.renren.android.page.IsPageRequestParam;
import com.renren.android.page.IsPageResponseBean;
import com.renren.android.page.PageHelper;
import com.renren.android.photos.GetAlbumsRequestParam;
import com.renren.android.photos.GetAlbumsResponseBean;
import com.renren.android.photos.GetPhotosCommentsRequestParam;
import com.renren.android.photos.GetPhotosCommentsResponseBean;
import com.renren.android.photos.GetPhotosRequestParam;
import com.renren.android.photos.GetPhotosResponseBean;
import com.renren.android.photos.PhotosAddCommentRequestParam;
import com.renren.android.photos.PhotosAddCommentResponseBean;
import com.renren.android.photos.PhotosCreateAlbumRequestParam;
import com.renren.android.photos.PhotosCreateAlbumResponseBean;
import com.renren.android.photos.PhotosHelper;
import com.renren.android.photos.PhotosUploadRequestParam;
import com.renren.android.photos.PhotosUploadResponseBean;
import com.renren.android.user.GetInfoRequestParam;
import com.renren.android.user.GetInfoResponseBean;
import com.renren.android.user.GetProfileInfoRequestParam;
import com.renren.android.user.GetProfileInfoResponseBean;
import com.renren.android.user.GetStatusRequestParam;
import com.renren.android.user.GetStatusResponseBean;
import com.renren.android.user.GetVisitorRequestParam;
import com.renren.android.user.GetVisitorResponseBean;
import com.renren.android.user.UserHelper;

public class AsyncRenRen {
	private Executor mPool;
	private EmoticonsHelper mEmoticonsHelper;
	private NewsFeedHelper mNewsFeedHelper;
	private UserHelper mUserHelper;
	private BlogHelper mBlogHelper;
	private PhotosHelper mPhotosHelper;
	private FriendsHelper mFriendsHelper;
	private PageHelper mPageHelper;

	public AsyncRenRen() {
		mPool = Executors.newFixedThreadPool(5);
		mEmoticonsHelper = new EmoticonsHelper();
		mNewsFeedHelper = new NewsFeedHelper();
		mUserHelper = new UserHelper();
		mBlogHelper = new BlogHelper();
		mPhotosHelper = new PhotosHelper();
		mFriendsHelper = new FriendsHelper();
		mPageHelper = new PageHelper();
	}

	public void getEmoticons(EmoticonsRequestParam param,
			RequestListener<EmoticonsResponseBean> listener) {
		mEmoticonsHelper.asyncGet(mPool, param, listener);
	}

	public void downloadEmoticons(List<EmoticonsResult> resultList) {
		mEmoticonsHelper.downloadEmotcons(resultList);
	}

	public void getNewsFeed(NewsFeedRequestParam param,
			RequestListener<NewsFeedResponseBean> listener) {
		mNewsFeedHelper.asyncGet(mPool, param, listener);
	}

	public void publishNewsFeed(NewsFeedPublishRequestParam param,
			RequestListener<NewsFeedPublishResponseBean> listener) {
		mNewsFeedHelper.asyncPublish(mPool, param, listener);
	}

	public void getInfo(GetInfoRequestParam param,
			RequestListener<GetInfoResponseBean> listener) {
		mUserHelper.asyncGetInfo(mPool, param, listener);
	}

	public void getProfileInfo(GetProfileInfoRequestParam param,
			RequestListener<GetProfileInfoResponseBean> listener) {
		mUserHelper.asyncGetProfileInfo(mPool, param, listener);
	}

	public void getVisitor(GetVisitorRequestParam param,
			RequestListener<GetVisitorResponseBean> listener) {
		mUserHelper.asyncGetVisitor(mPool, param, listener);
	}

	public void getAlbums(GetAlbumsRequestParam param,
			RequestListener<GetAlbumsResponseBean> listener) {
		mPhotosHelper.asyncGetAlbums(mPool, param, listener);
	}

	public void getStatus(GetStatusRequestParam param,
			RequestListener<GetStatusResponseBean> listener) {
		mUserHelper.asyncGetStatus(mPool, param, listener);
	}

	public void getBlogs(GetBlogsRequestParam param,
			RequestListener<GetBlogsResponseBean> listener) {
		mBlogHelper.asyncGetBlogs(mPool, param, listener);
	}

	public void getBlog(GetBlogRequestParam param,
			RequestListener<GetBlogResponseBean> listener) {
		mBlogHelper.asyncGetBlog(mPool, param, listener);
	}

	public void getBlogComments(GetBlogCommentsRequestParam param,
			RequestListener<GetBlogCommentsResponseBean> listener) {
		mBlogHelper.asyncGetBlogComments(mPool, param, listener);
	}

	public void addBlogComments(BlogAddCommentRequestParam param,
			RequestListener<BlogAddCommentResponseBean> listener) {
		mBlogHelper.asyncAddComment(mPool, param, listener);
	}

	public void publishBlog(BlogPublishRequestParam param,
			RequestListener<BlogPublishResponseBean> listener) {
		mBlogHelper.asyncPublishBlog(mPool, param, listener);
	}

	public void getPhotos(GetPhotosRequestParam param,
			RequestListener<GetPhotosResponseBean> listener) {
		mPhotosHelper.asyncGetPhotos(mPool, param, listener);
	}

	public void getPhotosComments(GetPhotosCommentsRequestParam param,
			RequestListener<GetPhotosCommentsResponseBean> listener) {
		mPhotosHelper.asyncGetPhotosComments(mPool, param, listener);
	}

	public void addPhotosComment(PhotosAddCommentRequestParam param,
			RequestListener<PhotosAddCommentResponseBean> listener) {
		mPhotosHelper.asyncAddPhotosComment(mPool, param, listener);
	}

	public void PhotosUpload(PhotosUploadRequestParam param,
			RequestListener<PhotosUploadResponseBean> listener) {
		mPhotosHelper.asyncPhotosUpload(mPool, param, listener);
	}

	public void PhotosCreateAlbum(PhotosCreateAlbumRequestParam param,
			RequestListener<PhotosCreateAlbumResponseBean> listener) {
		mPhotosHelper.asyncPhotosCreateAlbum(mPool, param, listener);
	}

	public void getFriends(GetFriendsRequestParam param,
			RequestListener<GetFriendsResponseBean> listener) {
		mFriendsHelper.asyncGetFriends(mPool, param, listener);
	}

	public void findFriends(FriendsFindRequestParam param,
			RequestListener<FriendsFindResponseBean> listener) {
		mFriendsHelper.asyncFindFriends(mPool, param, listener);
	}

	public void isPage(IsPageRequestParam param,
			RequestListener<IsPageResponseBean> listener) {
		mPageHelper.asyncIsPage(mPool, param, listener);
	}

	public void getPageInfo(GetPageInfoRequestParam param,
			RequestListener<GetPageInfoResponseBean> listener) {
		mPageHelper.asyncGetPageInfo(mPool, param, listener);
	}

	public void getPage(GetPageRequestParam param,
			RequestListener<GetPageResponseBean> listener) {
		mPageHelper.asyncGetPage(mPool, param, listener);
	}
}
