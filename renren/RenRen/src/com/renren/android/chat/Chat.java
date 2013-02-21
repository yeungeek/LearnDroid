package com.renren.android.chat;

import com.renren.android.R;
import com.renren.android.ui.base.FlipperLayout.OnOpenListener;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Chat {
	private Context mContext;
	private View mChat;

	private ImageView mFlip;
	private ImageView mAddFriends;
	private Button mChoose;

	private OnOpenListener mOnOpenListener;

	public Chat(Context context) {
		mContext = context;
		mChat = LayoutInflater.from(context).inflate(R.layout.chat, null);

		findViewById();
		setListener();
	}

	private void findViewById() {
		mFlip = (ImageView) mChat.findViewById(R.id.chat_flip);
		mAddFriends = (ImageView) mChat.findViewById(R.id.chat_addfriends);
		mChoose = (Button) mChat.findViewById(R.id.chat_choose);
	}

	private void setListener() {
		mFlip.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mOnOpenListener != null) {
					mOnOpenListener.open();
				}
			}
		});
		mAddFriends.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(mContext, "暂时不支持该功能", Toast.LENGTH_SHORT).show();
			}
		});
		mChoose.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(mContext, "暂时不支持该功能", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public View getView() {
		return mChat;
	}

	public void setOnOpenListener(OnOpenListener onOpenListener) {
		mOnOpenListener = onOpenListener;
	}
}
