package com.renren.android.message;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.renren.android.R;
import com.renren.android.ui.base.FlipperLayout.OnOpenListener;

public class Message {
	private Context mContext;
	private View mMessage;

	private ImageView mFlip;
	private Button mEdit;
	private ViewPager mPager;
	private RadioGroup mTab;
	private RadioButton mMessageButton;
	private RadioButton mFriendButton;
	private RadioButton mBirthDayButton;

	private ViewPagerAdapter mAdapter;
	private OnOpenListener mOnOpenListener;
	private List<View> mList = new ArrayList<View>();

	public Message(Context context) {
		mContext = context;
		mMessage = LayoutInflater.from(context).inflate(R.layout.message, null);
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		mFlip = (ImageView) mMessage.findViewById(R.id.message_flip);
		mEdit = (Button) mMessage.findViewById(R.id.message_edit);
		mPager = (ViewPager) mMessage.findViewById(R.id.message_pager);
		mTab = (RadioGroup) mMessage.findViewById(R.id.message_radiogroup);
		mMessageButton = (RadioButton) mMessage
				.findViewById(R.id.message_message);
		mFriendButton = (RadioButton) mMessage
				.findViewById(R.id.message_friend);
		mBirthDayButton = (RadioButton) mMessage
				.findViewById(R.id.message_birthday);
	}

	private void setListener() {
		mFlip.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mOnOpenListener != null) {
					mOnOpenListener.open();
				}
			}
		});
		mEdit.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(mContext, "暂时没有可编辑内容", Toast.LENGTH_SHORT)
						.show();
			}
		});
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			public void onPageSelected(int arg0) {

			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			public void onPageScrollStateChanged(int arg0) {
				if (arg0 == ViewPager.SCROLL_STATE_IDLE) {
					switch (mPager.getCurrentItem()) {
					case 0:
						mEdit.setVisibility(View.VISIBLE);
						mMessageButton.setChecked(true);
						break;

					case 1:
						mEdit.setVisibility(View.VISIBLE);
						mFriendButton.setChecked(true);
						break;

					case 2:
						mBirthDayButton.setChecked(true);
						mEdit.setVisibility(View.GONE);
						break;
					}
				}
			}
		});
		mTab.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.message_message:
					mPager.setCurrentItem(0);
					mEdit.setVisibility(View.VISIBLE);
					break;

				case R.id.message_friend:
					mPager.setCurrentItem(1);
					mEdit.setVisibility(View.VISIBLE);
					break;

				case R.id.message_birthday:
					mPager.setCurrentItem(2);
					mEdit.setVisibility(View.GONE);
					break;
				}
			}
		});
	}

	private void init() {
		View message = LayoutInflater.from(mContext).inflate(
				R.layout.message_message, null);
		View friend = LayoutInflater.from(mContext).inflate(
				R.layout.message_friend, null);
		View birthday = LayoutInflater.from(mContext).inflate(
				R.layout.message_birthday, null);
		mList.add(message);
		mList.add(friend);
		mList.add(birthday);
		mAdapter = new ViewPagerAdapter();
		mPager.setAdapter(mAdapter);
		mPager.setCurrentItem(0);
	}

	private class ViewPagerAdapter extends PagerAdapter {

		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mList.get(arg1));
		}

		public void finishUpdate(View arg0) {

		}

		public int getCount() {
			return mList.size();
		}

		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mList.get(arg1));
			return mList.get(arg1);

		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		public Parcelable saveState() {
			return null;
		}

		public void startUpdate(View arg0) {

		}

	}

	public View getView() {
		mPager.setCurrentItem(0);
		mMessageButton.setChecked(true);
		mEdit.setVisibility(View.VISIBLE);
		return mMessage;
	}

	public void setOnOpenListener(OnOpenListener onOpenListener) {
		mOnOpenListener = onOpenListener;
	}
}
