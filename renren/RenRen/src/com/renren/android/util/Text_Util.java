package com.renren.android.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.renren.android.R;
import com.renren.android.RenRenData;
import com.renren.android.blog.Blog;
import com.renren.android.emoticons.EmoticonsHelper;
import com.renren.android.emoticons.EmoticonsResponseBean;
import com.renren.android.emoticons.EmoticonsResult;
import com.renren.android.user.Friend;

/**
 * 文本工具类
 * 
 * @author rendongwei
 * 
 */
public class Text_Util {
	/**
	 * 正则表达式
	 * 
	 * @return
	 */
	private Pattern buildPattern() {
		/**
		 * 查看表情名称数据是否存在,不存在则从本地读取Json,并解析
		 */
		if (RenRenData.mEmoticonsResults.size() == 0
				|| RenRenData.mEmoticonsResults == null) {
			String json = readFromFile(EmoticonsResponseBean.FILENAME,
					EmoticonsResponseBean.FILEPATH);
			if (json != null) {
				EmoticonsHelper helper = new EmoticonsHelper();
				RenRenData.mEmoticonsResults = helper.Resolve(json);
			}
		}

		StringBuilder patternString = new StringBuilder(
				RenRenData.mEmoticonsResults.size() * 3);
		patternString.append('(');
		for (EmoticonsResult result : RenRenData.mEmoticonsResults) {
			String s = result.getEmotion();
			patternString.append(Pattern.quote(s));
			patternString.append('|');
		}
		patternString.replace(patternString.length() - 1,
				patternString.length(), ")");
		return Pattern.compile(patternString.toString());
	}

	/**
	 * 将文本中的表情符号替换为表情图片
	 * 
	 * @param text
	 *            需要转换的字符
	 * @return 带有表情的字符
	 */
	public CharSequence replace(CharSequence text) {
		try {
			SpannableStringBuilder builder = new SpannableStringBuilder(text);
			Pattern pattern = buildPattern();
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				Bitmap bitmap = getLoaclEmoticons(matcher.group());
				ImageSpan span = new ImageSpan(bitmap);
				builder.setSpan(span, matcher.start(), matcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			return builder;
		} catch (Exception e) {
			return text;
		}
	}

	/**
	 * 从SD卡中根据表情符号获取表情图片
	 * 
	 * @param imageName
	 *            表情的名称
	 * @return 表情的Bitmap
	 */
	private Bitmap getLoaclEmoticons(String imageName) {
		File dir = new File("/sdcard/RenRenForAndroid/Emoticons/");
		if (!dir.exists() || dir == null) {
			dir.mkdirs();
		}
		File[] cacheFiles = dir.listFiles();
		int i = 0;
		if (cacheFiles != null) {
			for (; i < cacheFiles.length; i++) {
				if (imageName.equals(cacheFiles[i].getName())) {
					break;
				}
			}
		}
		if (i < cacheFiles.length) {
			/**
			 * 因表情图片较小,则这里返回了一个60*60的Bitmap,该数值可根据情况调整
			 */
			return Bitmap.createScaledBitmap(BitmapFactory
					.decodeFile("/sdcard/RenRenForAndroid/Emoticons/"
							+ imageName), 60, 60, true);
		}
		return null;
	}

	/**
	 * 保存文本到SD卡中
	 * 
	 * @param context
	 *            上下文
	 * @param fileName
	 *            文件的名称
	 * @param filePath
	 *            文件的路径
	 * @param stringToWrite
	 *            写入的字符串
	 */
	public void savedToText(String fileName, String filePath,
			String stringToWrite) {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			String foldername = "/sdcard/RenRenForAndroid/" + filePath;

			File folder = new File(foldername);
			if (folder == null || !folder.exists()) {
				folder.mkdirs();
			}
			File targetFile = new File(foldername + "/" + fileName);
			OutputStreamWriter osw;

			try {

				if (!targetFile.exists()) {
					targetFile.createNewFile();
					osw = new OutputStreamWriter(new FileOutputStream(
							targetFile), "UTF-8");
					osw.write(stringToWrite);
					osw.close();

				} else {
					/**
					 * 再次写入时不采用拼接的方法,而是重新写
					 */
					osw = new OutputStreamWriter(new FileOutputStream(
							targetFile, false), "UTF-8");
					osw.write(stringToWrite);
					osw.flush();
					osw.close();
				}
			} catch (Exception e) {

			}
		}
	}

	/**
	 * 读取SD卡中文件
	 * 
	 * @param fileName
	 *            文件的名称
	 * @param filePath
	 *            文件的路径
	 * @return 文件中的字符
	 */
	public String readFromFile(String fileName, String filePath) {

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			String foldername = "/sdcard/RenRenForAndroid/" + filePath + "/";
			File folder = new File(foldername);

			if (folder == null || !folder.exists()) {
				folder.mkdirs();
			}

			File targetFile = new File(foldername + "/" + fileName);
			String readedStr = "";

			try {
				if (!targetFile.exists()) {
					targetFile.createNewFile();
				} else {
					InputStream in = new BufferedInputStream(
							new FileInputStream(targetFile));
					BufferedReader br = new BufferedReader(
							new InputStreamReader(in, "UTF-8"));
					String tmp;
					while ((tmp = br.readLine()) != null) {
						readedStr += tmp;
					}
					br.close();
					in.close();
					return readedStr;
				}
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	/**
	 * 添加超链接到好友资料
	 * 
	 * @param context
	 *            上下文
	 * @param activity
	 *            Activity
	 * @param view
	 *            显示的TextView
	 * @param text
	 *            修改的字符
	 * @param start
	 *            添加超链接的初始位置
	 * @param end
	 *            添加超链接的结束位置
	 * @param uid
	 *            该参数为FriendInfo所需数据,用来传递使用
	 */
	public void addIntentLinkToFriendInfo(final Context context,
			final Activity activity, final TextView view,
			final CharSequence text, final int start, final int end,
			final int uid) {
		SpannableString sp = new SpannableString(text);
		sp.setSpan(new IntentSpan(new OnClickListener() {

			public void onClick(View view) {

				Intent userIntent = new Intent();
				userIntent.setClass(context, Friend.class);
				userIntent.putExtra("uid", uid);
				context.startActivity(userIntent);
				activity.overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		}), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(
				new ForegroundColorSpan(context.getResources().getColor(
						R.color.link_color)), start, end,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(sp);
		view.setMovementMethod(LinkMovementMethod.getInstance());
	}

	/**
	 * 超链接到日志
	 * 
	 * @param context
	 *            上下文
	 * @param activity
	 *            Activity
	 * @param view
	 *            显示的TextView
	 * @param text
	 *            修改的字符
	 * @param start
	 *            添加超链接的初始位置
	 * @param end
	 *            添加超链接的结束位置
	 * @param id
	 *            日志的ID
	 * @param uid
	 *            发布者ID
	 * @param type
	 *            发布者类型(user,page)
	 */
	public void addIntentLinkToBlog(final Context context,
			final Activity activity, final TextView view,
			final CharSequence text, final int start, final int end,
			final int id, final int uid, final String name,
			final String description, final String type, final int count) {
		SpannableString sp = new SpannableString(text);
		sp.setSpan(new IntentSpan(new OnClickListener() {

			public void onClick(View view) {

				Intent userIntent = new Intent();
				userIntent.setClass(context, Blog.class);
				userIntent.putExtra("id", id);
				userIntent.putExtra("uid", uid);
				userIntent.putExtra("name", name);
				userIntent.putExtra("description", description);
				userIntent.putExtra("type", type);
				userIntent.putExtra("count", count);
				context.startActivity(userIntent);
				activity.overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		}), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(
				new ForegroundColorSpan(context.getResources().getColor(
						R.color.link_color)), start, end,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(sp);
		view.setMovementMethod(LinkMovementMethod.getInstance());
	}

	public void addStrikethrough(final TextView view, CharSequence text,
			final int start, final int end) {
		SpannableString sp = new SpannableString(text);
		sp.setSpan(new StrikethroughSpan(), start, end,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(sp);
	}

	public String getCharacterPinYin(char c) {
		HanyuPinyinOutputFormat format = null;
		format = new HanyuPinyinOutputFormat();
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		String[] pinyin = null;
		try {
			pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		// 如果c不是汉字，toHanyuPinyinStringArray会返回null
		if (pinyin == null)
			return null;
		// 只取一个发音，如果是多音字，仅取第一个发音
		return pinyin[0];
	}

	// 转换一个字符串
	public String getStringPinYin(String str) {
		StringBuilder sb = new StringBuilder();
		String tempPinyin = null;
		for (int i = 0; i < str.length(); ++i) {
			tempPinyin = getCharacterPinYin(str.charAt(i));
			if (tempPinyin == null) {
				// 如果str.charAt(i)非汉字，则保持原样
				sb.append(str.charAt(i));
			} else {
				sb.append(tempPinyin);
			}
		}
		return sb.toString();
	}
}
