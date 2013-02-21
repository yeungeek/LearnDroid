package com.renren.android.friends;

import java.util.concurrent.Executor;

import com.renren.android.RequestListener;
import com.renren.android.util.Util;

public class FriendsHelper {
	public void asyncGetFriends(Executor pool,
			final GetFriendsRequestParam param,
			final RequestListener<GetFriendsResponseBean> listener) {
		pool.execute(new Runnable() {

			public void run() {
				listener.onStart();
				GetFriendsResponseBean bean = getFriends(param);
				listener.onComplete(bean);
			}
		});
	}

	public GetFriendsResponseBean getFriends(GetFriendsRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new GetFriendsResponseBean(response);
	}

	public void asyncFindFriends(Executor pool,
			final FriendsFindRequestParam param,
			final RequestListener<FriendsFindResponseBean> listener) {
		pool.execute(new Runnable() {

			public void run() {
				listener.onStart();
				FriendsFindResponseBean bean = findFriends(param);
				listener.onComplete(bean);
			}
		});
	}

	public FriendsFindResponseBean findFriends(FriendsFindRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new FriendsFindResponseBean(response);
	}
}
