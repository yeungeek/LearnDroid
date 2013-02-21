package com.renren.android.photos;

import java.util.concurrent.Executor;

import com.renren.android.RequestListener;
import com.renren.android.util.Util;

public class PhotosHelper {
	public void asyncGetAlbums(Executor pool,
			final GetAlbumsRequestParam param,
			final RequestListener<GetAlbumsResponseBean> listener) {
		pool.execute(new Runnable() {

			public void run() {
				listener.onStart();
				GetAlbumsResponseBean bean = getAlbums(param);
				listener.onComplete(bean);
			}
		});
	}

	public GetAlbumsResponseBean getAlbums(GetAlbumsRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new GetAlbumsResponseBean(response);
	}

	public void asyncGetPhotos(Executor pool,
			final GetPhotosRequestParam param,
			final RequestListener<GetPhotosResponseBean> listener) {
		pool.execute(new Runnable() {
			public void run() {
				listener.onStart();
				GetPhotosResponseBean bean = getPhotos(param);
				listener.onComplete(bean);
			}
		});
	}

	public GetPhotosResponseBean getPhotos(GetPhotosRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new GetPhotosResponseBean(response);
	}

	public void asyncGetPhotosComments(Executor pool,
			final GetPhotosCommentsRequestParam param,
			final RequestListener<GetPhotosCommentsResponseBean> listener) {
		pool.execute(new Runnable() {

			public void run() {
				listener.onStart();
				GetPhotosCommentsResponseBean bean = getPhotosComments(param);
				listener.onComplete(bean);
			}
		});
	}

	public GetPhotosCommentsResponseBean getPhotosComments(
			GetPhotosCommentsRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new GetPhotosCommentsResponseBean(response);
	}

	public void asyncAddPhotosComment(Executor pool,
			final PhotosAddCommentRequestParam param,
			final RequestListener<PhotosAddCommentResponseBean> listener) {
		listener.onStart();
		PhotosAddCommentResponseBean bean = addPhotosComment(param);
		listener.onComplete(bean);
	}

	public PhotosAddCommentResponseBean addPhotosComment(
			PhotosAddCommentRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new PhotosAddCommentResponseBean(response);
	}

	public void asyncPhotosUpload(Executor pool,
			final PhotosUploadRequestParam param,
			final RequestListener<PhotosUploadResponseBean> listener) {
		pool.execute(new Runnable() {

			public void run() {
				listener.onStart();
				PhotosUploadResponseBean bean = photosUpload(param);
				listener.onComplete(bean);
			}
		});
	}

	public PhotosUploadResponseBean photosUpload(PhotosUploadRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new PhotosUploadResponseBean(response);
	}

	public void asyncPhotosCreateAlbum(Executor pool,
			final PhotosCreateAlbumRequestParam param,
			final RequestListener<PhotosCreateAlbumResponseBean> listener) {
		pool.execute(new Runnable() {

			public void run() {
				listener.onStart();
				PhotosCreateAlbumResponseBean bean = photosCreateAlbum(param);
				listener.onComplete(bean);
			}
		});
	}

	public PhotosCreateAlbumResponseBean photosCreateAlbum(
			PhotosCreateAlbumRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new PhotosCreateAlbumResponseBean(response);
	}
}
