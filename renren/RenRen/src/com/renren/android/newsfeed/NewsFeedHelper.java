package com.renren.android.newsfeed;

import java.util.concurrent.Executor;

import com.renren.android.RequestListener;
import com.renren.android.util.Util;

public class NewsFeedHelper {
	public void asyncGet(Executor pool, final NewsFeedRequestParam param,
			final RequestListener<NewsFeedResponseBean> listener) {
		pool.execute(new Runnable() {

			public void run() {
				listener.onStart();
				NewsFeedResponseBean bean = get(param);
				listener.onComplete(bean);
			}
		});
	}

	public NewsFeedResponseBean get(NewsFeedRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new NewsFeedResponseBean(response);
	}

	public void asyncPublish(Executor pool, final NewsFeedPublishRequestParam param,
			final RequestListener<NewsFeedPublishResponseBean> listener) {
		pool.execute(new Runnable() {
			
			public void run() {
				listener.onStart();
				NewsFeedPublishResponseBean bean = publish(param);
				listener.onComplete(bean);
			}
		});
	}

	public NewsFeedPublishResponseBean publish(NewsFeedPublishRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new NewsFeedPublishResponseBean(response);
	}
}
