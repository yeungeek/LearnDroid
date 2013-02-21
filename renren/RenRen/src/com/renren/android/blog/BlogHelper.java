package com.renren.android.blog;

import java.util.concurrent.Executor;

import com.renren.android.RequestListener;
import com.renren.android.util.Util;

public class BlogHelper {
	public void asyncGetBlogComments(Executor pool,
			final GetBlogCommentsRequestParam param,
			final RequestListener<GetBlogCommentsResponseBean> listener) {
		pool.execute(new Runnable() {

			public void run() {
				listener.onStart();
				GetBlogCommentsResponseBean bean = getBlogComments(param);
				listener.onComplete(bean);
			}
		});
	}

	public GetBlogCommentsResponseBean getBlogComments(
			GetBlogCommentsRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new GetBlogCommentsResponseBean(response);
	}

	public void asyncGetBlogs(Executor pool, final GetBlogsRequestParam param,
			final RequestListener<GetBlogsResponseBean> listener) {
		pool.execute(new Runnable() {

			public void run() {
				listener.onStart();
				GetBlogsResponseBean bean = getBlogs(param);
				listener.onComplete(bean);
			}
		});
	}

	public GetBlogsResponseBean getBlogs(GetBlogsRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new GetBlogsResponseBean(response);
	}

	public void asyncGetBlog(Executor pool, final GetBlogRequestParam param,
			final RequestListener<GetBlogResponseBean> listener) {
		pool.execute(new Runnable() {

			public void run() {
				listener.onStart();
				GetBlogResponseBean bean = getBlog(param);
				listener.onComplete(bean);
			}
		});
	}

	public GetBlogResponseBean getBlog(GetBlogRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new GetBlogResponseBean(response);
	}

	public void asyncAddComment(Executor pool,
			final BlogAddCommentRequestParam param,
			final RequestListener<BlogAddCommentResponseBean> listener) {
		pool.execute(new Runnable() {

			public void run() {
				listener.onStart();
				BlogAddCommentResponseBean bean = addComment(param);
				listener.onComplete(bean);
			}
		});
	}

	public BlogAddCommentResponseBean addComment(
			BlogAddCommentRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new BlogAddCommentResponseBean(response);
	}

	public void asyncPublishBlog(Executor pool,
			final BlogPublishRequestParam param,
			final RequestListener<BlogPublishResponseBean> listener) {
		pool.execute(new Runnable() {
			
			public void run() {
				listener.onStart();
				BlogPublishResponseBean bean=publishBlog(param);
				listener.onComplete(bean);
			}
		});
	}

	public BlogPublishResponseBean publishBlog(BlogPublishRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new BlogPublishResponseBean(response);
	}
}
