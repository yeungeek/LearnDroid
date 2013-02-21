package com.renren.android.newsfeed;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRenData;
import com.renren.android.blog.BlogComments;
import com.renren.android.util.Text_Util;

public class NewsFeedAdapter extends BaseAdapter {
	private BaseApplication mApplication;
	private Context mContext = null;
	private Activity mActivity = null;
	private LayoutInflater mInflater = null;
	private Text_Util mText_Util = null;

	public NewsFeedAdapter(BaseApplication application, Context context,
			Activity activity) {
		mApplication = application;
		mContext = context;
		mActivity = activity;
		mInflater = LayoutInflater.from(mContext);
		mText_Util = new Text_Util();
	}

	public int getCount() {
		return RenRenData.mNewsFeedAllResults.size();
	}

	public Object getItem(int position) {
		return RenRenData.mNewsFeedAllResults.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		int feedType = RenRenData.mNewsFeedAllResults.get(position)
				.getFeed_type();
		NewsFeedViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.newsfeed_item, null);
			holder = new NewsFeedViewHolder();
			FindViewById(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (NewsFeedViewHolder) convertView.getTag();
		}
		NewsFeedResult result = RenRenData.mNewsFeedAllResults.get(position);
		Assignment(holder, position, feedType, result);
		setListener(holder, position, feedType, result);
		return convertView;
	}

	private void FindViewById(NewsFeedViewHolder holder, View convertView) {

		holder.mAvatar = (ImageView) convertView
				.findViewById(R.id.newsfeed_list_avatar);
		holder.mName = (TextView) convertView
				.findViewById(R.id.newsfeed_list_name);
		holder.mMore = (ImageButton) convertView
				.findViewById(R.id.newsfeed_list_more);
		holder.mContent = (TextView) convertView
				.findViewById(R.id.newsfeed_list_content);
		holder.mType = (ImageView) convertView
				.findViewById(R.id.newsfeed_list_type);
		holder.mTime = (TextView) convertView
				.findViewById(R.id.newsfeed_list_time);
		holder.mSource = (TextView) convertView
				.findViewById(R.id.newsfeed_list_source);
		holder.mComment_Root = (LinearLayout) convertView
				.findViewById(R.id.newsfeed_list_comment_root);
		holder.mComment_Count = (TextView) convertView
				.findViewById(R.id.newsfeed_list_comment_count);
		holder.mComment_1 = (TextView) convertView
				.findViewById(R.id.newsfeed_list_comment_1);
		holder.mComment_1_Time = (TextView) convertView
				.findViewById(R.id.newsfeed_list_comment_1_time);
		holder.mComment_2 = (TextView) convertView
				.findViewById(R.id.newsfeed_list_comment_2);
		holder.mComment_2_Time = (TextView) convertView
				.findViewById(R.id.newsfeed_list_comment_2_time);

		holder.mStatus_Root = (LinearLayout) convertView
				.findViewById(R.id.newsfeed_list_status_root);
		holder.mStatus_Name = (TextView) convertView
				.findViewById(R.id.newsfeed_list_status_name);
		holder.mStatus_Content = (TextView) convertView
				.findViewById(R.id.newsfeed_list_status_content);

		holder.mSharePhoto_Root = (LinearLayout) convertView
				.findViewById(R.id.newsfeed_list_sharephoto_root);
		holder.mSharePhoto_Name = (TextView) convertView
				.findViewById(R.id.newsfeed_list_sharephoto_name);
		holder.mSharePhoto_Description = (TextView) convertView
				.findViewById(R.id.newsfeed_list_sharephoto_description);
		holder.mSharePhoto_Image = (ImageView) convertView
				.findViewById(R.id.newsfeed_list_sharephoto_image);
		holder.mSharePhoto_Title = (TextView) convertView
				.findViewById(R.id.newsfeed_list_sharephoto_title);

		holder.mShareAlbum_Root = (LinearLayout) convertView
				.findViewById(R.id.newsfeed_list_sharealbum_root);
		holder.mShareAlbum_Name = (TextView) convertView
				.findViewById(R.id.newsfeed_list_sharealbum_name);
		holder.mShareAlbum_Title = (TextView) convertView
				.findViewById(R.id.newsfeed_list_sharealbum_title);
		holder.mShareAlbum_Image = (ImageView) convertView
				.findViewById(R.id.newsfeed_list_sharealbum_image);

		holder.mShareBlog_Root = (LinearLayout) convertView
				.findViewById(R.id.newsfeed_list_shareblog_root);
		holder.mShareBlog_Name = (TextView) convertView
				.findViewById(R.id.newsfeed_list_shareblog_name);
		holder.mShareBlog_Title = (TextView) convertView
				.findViewById(R.id.newsfeed_list_shareblog_title);
		holder.mShareBlog_Description = (TextView) convertView
				.findViewById(R.id.newsfeed_list_shareblog_description);

		holder.mPublishBlog_Root = (LinearLayout) convertView
				.findViewById(R.id.newsfeed_list_publishblog_root);
		holder.mPublishBlog_Title = (TextView) convertView
				.findViewById(R.id.newsfeed_list_publishblog_title);
		holder.mPublishBlog_Description = (TextView) convertView
				.findViewById(R.id.newsfeed_list_publishblog_description);

		holder.mPublishPhoto_Root = (LinearLayout) convertView
				.findViewById(R.id.newsfeed_list_publishphoto_root);
		holder.mPublishPhoto_Content = (TextView) convertView
				.findViewById(R.id.newsfeed_list_publishphoto_content);
		holder.mPublishPhoto_Image = (ImageView) convertView
				.findViewById(R.id.newsfeed_list_publishphoto_image);
		holder.mPublishPhoto_Title = (TextView) convertView
				.findViewById(R.id.newsfeed_list_publishphoto_title);
	}

	public void Assignment(NewsFeedViewHolder holder, int position,
			int feedType, NewsFeedResult result) {
		mApplication.mHeadBitmap.display(holder.mAvatar, result.getHead_url());
		mText_Util.addIntentLinkToFriendInfo(mContext, mActivity, holder.mName,
				result.getName(), 0, result.getName().length(),
				result.getActor_id());
		holder.mContent.setVisibility(View.VISIBLE);
		holder.mContent.setText(mText_Util.replace(result.getMessage()));
		holder.mType.setImageResource(R.drawable.v5_0_1_newsfeed_status_icon);
		holder.mTime.setText(result.getUpdate_time());
		if (result.getSource_text() != null) {
			holder.mSource.setText("来自" + result.getSource_text());
		}
		/**
		 * 评论
		 */
		if (result.getComments_count() > 0) {
			holder.mComment_Root.setVisibility(View.VISIBLE);
			holder.mComment_Count.setText(result.getComments_count() + "条评论");
			List<Map<String, Object>> commentList = result.getComments();
			if (commentList.size() > 0) {
				if (result.getComments_count() == 1) {
					holder.mComment_1
							.setText(mText_Util
									.replace(commentList.get(0).get("name")
											.toString()
											+ "   "
											+ commentList.get(0).get("text")
													.toString()));
					holder.mComment_1_Time.setText(commentList.get(0)
							.get("time").toString());
					holder.mComment_2.setVisibility(View.GONE);
					holder.mComment_2_Time.setVisibility(View.GONE);
				} else {
					holder.mComment_2.setVisibility(View.VISIBLE);
					holder.mComment_2_Time.setVisibility(View.VISIBLE);
					holder.mComment_1
							.setText(mText_Util
									.replace(commentList.get(0).get("name")
											.toString()
											+ "   "
											+ commentList.get(0).get("text")
													.toString()));
					holder.mComment_1_Time.setText(commentList.get(0)
							.get("time").toString());
					holder.mComment_2
							.setText(mText_Util
									.replace(commentList.get(1).get("name")
											.toString()
											+ "   "
											+ commentList.get(1).get("text")
													.toString()));
					holder.mComment_2_Time.setText(commentList.get(1)
							.get("time").toString());
				}
			} else {
				holder.mComment_Root.setVisibility(View.GONE);
			}

		} else {
			holder.mComment_Root.setVisibility(View.GONE);
		}

		/**
		 * 普通发布的状态或者转发的状态
		 */
		if (result.getAttachment_count() > 0
				&& (feedType == 10 || feedType == 11)) {
			holder.mStatus_Root.setVisibility(View.VISIBLE);
			mText_Util.addIntentLinkToFriendInfo(mContext, mActivity,
					holder.mStatus_Name, result.getAttachment_owner_name(), 0,
					result.getAttachment_owner_name().length(),
					result.getAttachment_owner_id());
			holder.mStatus_Content.setText(mText_Util.replace(result
					.getAttachment_content()));
		} else {
			holder.mStatus_Root.setVisibility(View.GONE);
		}

		/**
		 * 分享照片
		 */
		if (result.getAttachment_count() > 0
				&& (feedType == 32 || feedType == 36)) {
			holder.mSharePhoto_Root.setVisibility(View.VISIBLE);
			if (result.getTrace_text() == null
					|| result.getTrace_text().length() == 0) {
				holder.mContent.setVisibility(View.GONE);
			} else {
				holder.mContent.setVisibility(View.VISIBLE);
				holder.mContent.setText(mText_Util.replace(result
						.getTrace_text()));
			}
			mText_Util.addIntentLinkToFriendInfo(mContext, mActivity,
					holder.mSharePhoto_Name, result.getAttachment_owner_name(),
					0, result.getAttachment_owner_name().length(),
					result.getAttachment_owner_id());
			if (result.getDescription() == null
					|| result.getDescription().equals("")) {
				holder.mSharePhoto_Description.setVisibility(View.GONE);
			} else {
				holder.mSharePhoto_Description.setVisibility(View.VISIBLE);
				holder.mSharePhoto_Description.setText(mText_Util
						.replace(result.getDescription()));
			}
			if (result.getAttachment_raw_src() != null) {
				mApplication.mPhotoBitmap.display(holder.mSharePhoto_Image,
						result.getAttachment_raw_src());
			} else {
				holder.mSharePhoto_Image
						.setImageResource(R.drawable.v5_0_1_photo_default_img);
			}
			if (result.getTitle() == null || result.getTitle().equals("")) {
				holder.mSharePhoto_Title.setVisibility(View.GONE);
			} else {
				holder.mSharePhoto_Title.setVisibility(View.VISIBLE);
				holder.mSharePhoto_Title.setText("【" + result.getTitle() + "】");
			}
			holder.mType
					.setImageResource(R.drawable.v5_0_1_newsfeed_share_icon);
		} else {
			holder.mSharePhoto_Root.setVisibility(View.GONE);
		}
		/**
		 * 分享相册
		 */
		if (result.getAttachment_count() > 0 && feedType == 33) {
			holder.mShareAlbum_Root.setVisibility(View.VISIBLE);
			if (result.getTrace_text() == null
					|| result.getTrace_text().length() == 0) {
				holder.mContent.setVisibility(View.GONE);
			} else {
				holder.mContent.setVisibility(View.VISIBLE);
				holder.mContent.setText(mText_Util.replace(result
						.getTrace_text()));
			}
			mText_Util.addIntentLinkToFriendInfo(mContext, mActivity,
					holder.mShareAlbum_Name, result.getAttachment_owner_name(),
					0, result.getAttachment_owner_name().length(),
					result.getAttachment_owner_id());
			holder.mShareAlbum_Title.setText("【" + result.getTitle() + "】");

			if (result.getAttachment_raw_src() != null) {
				mApplication.mPhotoBitmap.display(holder.mShareAlbum_Image,
						result.getAttachment_raw_src());
			} else {
				holder.mShareAlbum_Image
						.setImageResource(R.drawable.v5_0_1_photo_default_img);
			}
		} else {
			holder.mShareAlbum_Root.setVisibility(View.GONE);
		}

		/**
		 * 分享日志
		 */
		if (result.getAttachment_count() > 0
				&& (feedType == 21 || feedType == 23)) {
			holder.mShareBlog_Root.setVisibility(View.VISIBLE);
			if (result.getTrace_text() == null
					|| result.getTrace_text().length() == 0) {
				holder.mContent.setVisibility(View.GONE);
			} else {
				holder.mContent.setVisibility(View.VISIBLE);
				holder.mContent.setText(mText_Util.replace(result
						.getTrace_text()));
			}
			mText_Util.addIntentLinkToFriendInfo(mContext, mActivity,
					holder.mShareBlog_Name, result.getAttachment_owner_name(),
					0, result.getAttachment_owner_name().length(),
					result.getAttachment_owner_id());

			mText_Util.addIntentLinkToBlog(mContext, mActivity,
					holder.mShareBlog_Title, result.getTitle(), 0, result
							.getTitle().length(), result
							.getAttachment_media_id(), result.getActor_id(),
					result.getName(), result.getDescription(), result
							.getActor_type(), result.getComments_count());
			holder.mShareBlog_Description.setText(result.getDescription());
		} else {
			holder.mShareBlog_Root.setVisibility(View.GONE);
		}

		/**
		 * 发布日志
		 */
		if (feedType == 20 || feedType == 22) {
			holder.mContent.setVisibility(View.GONE);
			holder.mPublishBlog_Root.setVisibility(View.VISIBLE);
			mText_Util.addIntentLinkToBlog(mContext, mActivity,
					holder.mPublishBlog_Title, result.getTitle(), 0, result
							.getTitle().length(), result.getSource_id(), result
							.getActor_id(), result.getName(), result
							.getDescription(), result.getActor_type(), result
							.getComments_count());
			holder.mPublishBlog_Description.setText(result.getDescription());
			holder.mType.setImageResource(R.drawable.v5_0_1_newsfeed_blog_icon);
		} else {
			holder.mPublishBlog_Root.setVisibility(View.GONE);
		}

		/**
		 * 上传图片
		 */
		if (result.getAttachment_count() > 0
				&& (feedType == 30 || feedType == 31)) {
			holder.mContent.setVisibility(View.GONE);
			holder.mPublishPhoto_Root.setVisibility(View.VISIBLE);
			if (result.getAttachment_content() == null
					|| result.getAttachment_content().equals("")) {
				holder.mPublishPhoto_Content.setVisibility(View.GONE);
			} else {
				holder.mPublishPhoto_Content.setVisibility(View.VISIBLE);
				holder.mPublishPhoto_Content.setText(mText_Util.replace(result
						.getAttachment_content()));
			}
			holder.mPublishPhoto_Title.setText("【" + result.getTitle() + "】");
			if (result.getAttachment_raw_src() != null) {
				mApplication.mPhotoBitmap.display(holder.mPublishPhoto_Image,
						result.getAttachment_raw_src());
			} else {
				holder.mPublishPhoto_Image
						.setImageResource(R.drawable.v5_0_1_photo_default_img);
			}
			holder.mType
					.setImageResource(R.drawable.v5_0_1_newsfeed_photo_icon);
		} else {
			holder.mPublishPhoto_Root.setVisibility(View.GONE);
		}
	}

	private void setListener(NewsFeedViewHolder holder, int position,
			final int feedType, final NewsFeedResult result) {
		holder.mComment_Root.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (feedType >= 20 && feedType <= 23) {
					Intent intent = new Intent();
					intent.setClass(mContext, BlogComments.class);
					intent.putExtra("id", result.getSource_id());
					intent.putExtra("uid", result.getActor_id());
					intent.putExtra("avatar", result.getHead_url());
					intent.putExtra("name", result.getName());
					intent.putExtra("title", result.getTitle());
					intent.putExtra("description", result.getDescription());
					intent.putExtra("time", result.getUpdate_time());
					intent.putExtra("count", result.getComments_count());
					intent.putExtra("type", result.getActor_type());
					mContext.startActivity(intent);
					mActivity.overridePendingTransition(R.anim.roll_up,
							R.anim.roll);
				}
			}
		});
	}
}
