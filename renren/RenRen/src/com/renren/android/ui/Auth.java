package com.renren.android.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRen;
import com.renren.android.RenRenData;
import com.renren.android.RequestListener;
import com.renren.android.emoticons.EmoticonsRequestParam;
import com.renren.android.emoticons.EmoticonsResponseBean;

/**
 * Auth��֤����
 * 
 * @author rendongwei
 * 
 */
public class Auth extends Activity {
	private BaseApplication mApplication;
	private WebView mWebView;
	private LinearLayout mLayout;
	private ImageView mIcon;
	private TextView mText;
	private Animation mRotate;
	private WebSettings mSettings;
	private boolean mLoginSuccess = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth);
		mApplication = (BaseApplication) getApplication();
		findViewById();
		init();
		setListener();
	}

	private void findViewById() {
		mWebView = (WebView) findViewById(R.id.auth_webview);
		mLayout = (LinearLayout) findViewById(R.id.auth_loading_layout);
		mIcon = (ImageView) findViewById(R.id.auth_loading_icon);
		mText = (TextView) findViewById(R.id.auth_loading_text);
	}

	private void setListener() {
		mWebView.setWebViewClient(new WebViewClient() {

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// ��д�˷������������ҳ��������ӻ����ڵ�ǰ��webview����ת��������������Ǳ�
				mWebView.loadUrl(url);
				return true;
			}

			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, android.net.http.SslError error) {
				// ��д�˷���������webview����https����
				handler.proceed();
			}

			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// ��д�˷���������ҳ���֮ǰ�Ĵ���,������ʾ��������
				super.onPageStarted(view, url, favicon);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = "���ڼ�����Ȩ��ҳ...";
				handler.sendMessage(msg);
			}

			public void onPageFinished(WebView view, String url) {
				// ��д�˷���������ҳ���֮��Ĵ���
				super.onPageFinished(view, url);
				handler.sendEmptyMessage(1);
				// ������ص�ַ���������ַ���,�����¼�ɹ�
				if (url.contains("http://graph.renren.com/oauth/login_success.html?code=")
						&& !mLoginSuccess) {
					mLoginSuccess = true;
					// ���url��'='��֮����ַ���
					String AuthorizationCode = url.split("=")[1];
					// ������µ�url
					String httpUrl = "https://graph.renren.com/oauth/token?"
							+ "client_id="
							+ RenRen.RENREN_API_KEY
							+ "&client_secret="
							+ RenRen.RENREN_SECRET_KEY
							+ "&redirect_uri=http://graph.renren.com/oauth/login_success.html"
							+ "&grant_type=authorization_code" + "&code="
							+ AuthorizationCode;
					// ������ȡ����Json���ݲ�����
					Resolve(httpConnection(httpUrl));
					// ���ر�����Դ������
					downloadEmotcons();
				}
			}
		});
	}

	/**
	 * ��ʼ��
	 */
	private void init() {
		initAnim();
		initWebView();
	}

	/**
	 * ��ʼ������
	 */
	private void initAnim() {
		mRotate = AnimationUtils.loadAnimation(Auth.this, R.anim.rotate);
		mIcon.startAnimation(mRotate);
	}

	/**
	 * ��ʼ��webView
	 */
	private void initWebView() {
		mSettings = mWebView.getSettings();
		mSettings.setJavaScriptEnabled(true);
		mSettings.setBuiltInZoomControls(true);
		mSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		String httpUrl = "https://graph.renren.com/oauth/authorize?"
				+ "response_type=code"
				+ "&client_id="
				+ RenRen.RENREN_API_KEY
				+ "&display=touch"
				+ "&scope="
				+ RenRen.SCOPE
				+ "&redirect_uri=http://graph.renren.com/oauth/login_success.html";
		mWebView.loadUrl(httpUrl);
	}

	/**
	 * ����Json
	 * 
	 * @param json
	 */
	private void Resolve(String json) {
		try {
			JSONObject object = new JSONObject(json);
			String accessToken = object.getString("access_token");
			String refreshToken = object.getString("refresh_token");
			long expriesTime = object.getLong("expires_in") * 1000;
			int uid = object.getJSONObject("user").getInt("id");
			String name = object.getJSONObject("user").getString("name");
			String headurl = null;
			String headurl_Tiny = null;
			String headurl_Main = null;
			String headurl_large = null;
			JSONArray array = object.getJSONObject("user").getJSONArray(
					"avatar");
			object = null;
			for (int i = 0; i < array.length(); i++) {
				object = array.getJSONObject(i);
				if (object.getString("type").equals("avatar")) {
					headurl = object.getString("url");
				}
				if (object.getString("type").equals("tiny")) {
					headurl_Tiny = object.getString("url");
				}
				if (object.getString("type").equals("main")) {
					headurl_Main = object.getString("url");
				}
				if (object.getString("type").equals("large")) {
					headurl_large = object.getString("url");
				}
			}
			/**
			 * ����AccessToken�Լ��û��Ļ�����Ϣ
			 */
			mApplication.mRenRen.storeAccessToken(accessToken, refreshToken,
					expriesTime, uid, name, headurl, headurl_Tiny,
					headurl_Main, headurl_large);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���ر�����Դ
	 */
	private void downloadEmotcons() {
		EmoticonsRequestParam param = new EmoticonsRequestParam(
				mApplication.mRenRen);

		RequestListener<EmoticonsResponseBean> listener = new RequestListener<EmoticonsResponseBean>() {

			public void onStart() {
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = "���ڼ�����Դ...";
				handler.sendMessage(msg);
			}

			public void onComplete(EmoticonsResponseBean bean) {
				mApplication.mAsyncRenRen
						.downloadEmoticons(RenRenData.mEmoticonsResults);
				startActivity(new Intent(Auth.this, DesktopActivity.class));
				finish();
			}
		};
		mApplication.mAsyncRenRen.getEmoticons(param, listener);
	}

	/**
	 * ��������
	 * 
	 * @param httpUrl
	 *            URL��ַ
	 * @return ����
	 */
	private String httpConnection(String httpUrl) {
		String result = null;
		try {
			URL url = new URL(httpUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());
			BufferedReader buffer = new BufferedReader(in);
			String inputLine = null;
			while ((inputLine = buffer.readLine()) != null) {
				result = inputLine + "\n";
			}
			in.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * ���ڿ��ƽ�������ʾ
	 */
	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mText.setText(msg.obj.toString());
				mLayout.setVisibility(View.VISIBLE);
				mWebView.setVisibility(View.INVISIBLE);
				break;

			case 1:
				mLayout.setVisibility(View.INVISIBLE);
				mWebView.setVisibility(View.VISIBLE);
				break;
			}
		}
	};
}
