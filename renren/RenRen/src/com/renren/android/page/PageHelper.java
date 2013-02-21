package com.renren.android.page;

import java.util.concurrent.Executor;

import com.renren.android.RequestListener;
import com.renren.android.util.Util;

public class PageHelper {
	public void asyncIsPage(Executor pool, final IsPageRequestParam param,
			final RequestListener<IsPageResponseBean> listener) {
		pool.execute(new Runnable() {

			public void run() {
				listener.onStart();
				IsPageResponseBean bean = isPage(param);
				listener.onComplete(bean);
			}
		});
	}

	public IsPageResponseBean isPage(IsPageRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new IsPageResponseBean(response);
	}

	public void asyncGetPageInfo(Executor pool,
			final GetPageInfoRequestParam param,
			final RequestListener<GetPageInfoResponseBean> listener) {
		pool.execute(new Runnable() {

			public void run() {
				listener.onStart();
				GetPageInfoResponseBean bean = getPageInfo(param);
				listener.onComplete(bean);
			}
		});
	}

	public GetPageInfoResponseBean getPageInfo(GetPageInfoRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new GetPageInfoResponseBean(response);
	}

	public void asyncGetPage(Executor pool, final GetPageRequestParam param,
			final RequestListener<GetPageResponseBean> listener) {
		pool.execute(new Runnable() {

			public void run() {
				listener.onStart();
				GetPageResponseBean bean = getPage(param);
				listener.onComplete(bean);
			}
		});
	}

	public GetPageResponseBean getPage(GetPageRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new GetPageResponseBean(response);
	}
}
