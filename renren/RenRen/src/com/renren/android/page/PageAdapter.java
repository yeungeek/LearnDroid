package com.renren.android.page;

import java.util.Arrays;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRenData;
import com.renren.android.friends.FriendsFind;
import com.renren.android.friends.FriendsGridViewAdapter;
import com.renren.android.ui.base.PinnedHeaderListView;
import com.renren.android.ui.base.PinnedHeaderListView.PinnedHeaderAdapter;
import com.renren.android.user.Friend;

public class PageAdapter extends BaseAdapter implements SectionIndexer,
		PinnedHeaderAdapter, OnScrollListener {
	private BaseApplication mApplication;
	private Context mContext;
	private Activity mActivity;
	private int mCount;
	private Map<String, Integer> mMap;
	private FriendsGridViewAdapter mAdapter;
	private int mLocationPosition = -1;
	private boolean mLocation;

	public PageAdapter(BaseApplication application, Context context,
			Activity activity) {
		mApplication = application;
		mContext = context;
		mActivity = activity;
		mCount = RenRenData.mPageResults.size();
	}

	public int getCount() {
		return RenRenData.mPageResults.size();
	}

	public Object getItem(int position) {
		return RenRenData.mPageResults.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final PinnedHeaderListView mListView = (PinnedHeaderListView) parent;
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.page_item, null);
			holder = new ViewHolder();
			holder.mFindFriends = (TextView) convertView
					.findViewById(R.id.page_item_findfriends);
			holder.mHeaderParent = (LinearLayout) convertView
					.findViewById(R.id.page_item_header_parent);
			holder.mHeader = (LinearLayout) convertView
					.findViewById(R.id.page_item_header);
			holder.mHeaderText = (TextView) convertView
					.findViewById(R.id.page_item_header_text);
			holder.mHeaderIcon = (ImageView) convertView
					.findViewById(R.id.page_item_header_icon);
			holder.mHeaderGridView = (GridView) convertView
					.findViewById(R.id.page_item_header_gridview);
			holder.mInfo = (RelativeLayout) convertView
					.findViewById(R.id.page_item_info);
			holder.mAvatar = (ImageView) convertView
					.findViewById(R.id.page_item_avatar);
			holder.mName = (TextView) convertView
					.findViewById(R.id.page_item_name);
			holder.mFansCount = (TextView) convertView
					.findViewById(R.id.page_item_fancount);
			holder.mType = (TextView) convertView
					.findViewById(R.id.page_item_type);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position == 0 && mLocation == false) {
			holder.mFindFriends.setVisibility(View.VISIBLE);
		} else {
			holder.mFindFriends.setVisibility(View.GONE);
		}
		int section = getSectionForPosition(position);
		final PageResult result = RenRenData.mPageMap.get(
				RenRenData.mPageSections.get(section)).get(
				position - getPositionForSection(section));
		mApplication.mHeadBitmap.display(holder.mAvatar, result.getHeadurl());
		holder.mName.setText(result.getName());
		holder.mFansCount.setText("关注者 : " + result.getFansCount());
		holder.mType.setText("类型 : " + result.getCategory_name());
		holder.mHeaderText.setText(RenRenData.mPageSections.get(section));
		if (getPositionForSection(section) == position) {
			holder.mHeaderParent.setVisibility(View.VISIBLE);
			mMap = RenRenData.mPageFirstNamePosition
					.get(RenRenData.mPageSections.get(section));
			mAdapter = new FriendsGridViewAdapter(mContext, mMap);
			holder.mHeaderGridView.setAdapter(mAdapter);
			holder.mHeaderGridView
					.setOnItemClickListener(new OnItemClickListener() {

						public void onItemClick(AdapterView<?> parent,
								View view, int positions, long id) {
							mLocation = true;
							int section = getSectionForPosition(position);
							Map<String, Integer> map = RenRenData.mPageFirstNamePosition
									.get(RenRenData.mPageSections.get(section));
							mLocationPosition = (Integer) map.values()
									.toArray()[positions];
							mListView.setSelection(mLocationPosition);
						}
					});
		} else {
			holder.mHeaderParent.setVisibility(View.GONE);
		}
		holder.mInfo.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, Friend.class);
				intent.putExtra("uid", result.getPageId());
				mContext.startActivity(intent);
				mActivity
						.overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		});
		holder.mFindFriends.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mContext.startActivity(new Intent(mContext, FriendsFind.class));
				mActivity
						.overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView mFindFriends;
		LinearLayout mHeaderParent;
		LinearLayout mHeader;
		TextView mHeaderText;
		ImageView mHeaderIcon;
		GridView mHeaderGridView;
		RelativeLayout mInfo;
		ImageView mAvatar;
		TextView mName;
		TextView mFansCount;
		TextView mType;
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (view instanceof PinnedHeaderListView) {
			((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
		}
	}

	public int getPinnedHeaderState(int position) {
		int realPosition = position;
		if (realPosition == 0
				|| (mLocation == true && mLocationPosition != -1 && mLocationPosition == realPosition)) {
			return PINNED_HEADER_GONE;
		}
		mLocation = false;
		mLocationPosition = -1;
		int section = getSectionForPosition(realPosition);
		int nextSectionPosition = getPositionForSection(section + 1);
		if (nextSectionPosition != -1
				&& realPosition == nextSectionPosition - 1) {
			return PINNED_HEADER_PUSHED_UP;
		}
		return PINNED_HEADER_VISIBLE;
	}

	public void configurePinnedHeader(View header, int position, int alpha) {
		int realPosition = position;
		int section = getSectionForPosition(realPosition);
		String title = (String) getSections()[section];
		((TextView) header.findViewById(R.id.friends_list_header_text))
				.setText(title);
	}

	public Object[] getSections() {
		return RenRenData.mPageSections.toArray();
	}

	public int getPositionForSection(int section) {
		if (section < 0 || section >= RenRenData.mPageSections.size()) {
			return -1;
		}
		return RenRenData.mPagePositions.get(section);
	}

	public int getSectionForPosition(int position) {
		if (position < 0 || position >= mCount) {
			return -1;
		}
		int index = Arrays.binarySearch(RenRenData.mPagePositions.toArray(),
				position);
		return index >= 0 ? index : -index - 2;

	}
}
