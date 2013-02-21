package com.renren.android.photos;

import com.renren.android.ResponseBean;

public class PhotosUploadResponseBean extends ResponseBean{

	public PhotosUploadResponseBean(String response) {
		super(response);
		System.out.println(response);
	}
}
