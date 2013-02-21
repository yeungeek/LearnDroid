package com.renren.android.emoticons;

import com.renren.android.RenRenData;
import com.renren.android.ResponseBean;
import com.renren.android.util.Text_Util;

public class EmoticonsResponseBean extends ResponseBean{
	public static final String FILENAME="Emoticons.json";
	public static final String FILEPATH="Emoticons";
	public EmoticonsResponseBean(String response) {
		super(response);
		Text_Util text_Util=new Text_Util();
		text_Util.savedToText(FILENAME, FILEPATH, response);
		EmoticonsHelper helper = new EmoticonsHelper();
		RenRenData.mEmoticonsResults = helper.Resolve(response);
	}
}
