package com.renren.android.user;

import java.util.concurrent.Executor;

import com.renren.android.RequestListener;
import com.renren.android.util.Util;

public class UserHelper {
	public void asyncGetInfo(Executor pool, final GetInfoRequestParam param,
			final RequestListener<GetInfoResponseBean> listener) {
		pool.execute(new Runnable() {
			
			public void run() {
				listener.onStart();
				GetInfoResponseBean bean = getInfo(param);
				listener.onComplete(bean);
			}
		});
	}

	public GetInfoResponseBean getInfo(GetInfoRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new GetInfoResponseBean(response);
	}

	public void asyncGetProfileInfo(Executor pool,
			final GetProfileInfoRequestParam param,
			final RequestListener<GetProfileInfoResponseBean> listener) {
		pool.execute(new Runnable() {

			public void run() {
				listener.onStart();
				GetProfileInfoResponseBean bean = getProfileInfo(param);
				listener.onComplete(bean);
			}
		});
	}

	public GetProfileInfoResponseBean getProfileInfo(
			GetProfileInfoRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new GetProfileInfoResponseBean(response);
	}

	public void asyncGetVisitor(Executor pool,
			final GetVisitorRequestParam param,
			final RequestListener<GetVisitorResponseBean> listener) {
		pool.execute(new Runnable() {

			public void run() {
				listener.onStart();
				GetVisitorResponseBean bean = getVisitor(param);
				listener.onComplete(bean);
			}
		});
	}

	public GetVisitorResponseBean getVisitor(GetVisitorRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new GetVisitorResponseBean(response);
	}



	public void asyncGetStatus(Executor pool,
			final GetStatusRequestParam param,
			final RequestListener<GetStatusResponseBean> listener) {
		pool.execute(new Runnable() {

			public void run() {
				listener.onStart();
				GetStatusResponseBean bean = getStatus(param);
				listener.onComplete(bean);
			}
		});
	}

	public GetStatusResponseBean getStatus(GetStatusRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new GetStatusResponseBean(response);
	}
}
