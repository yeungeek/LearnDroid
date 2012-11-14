
package yeungeek.tk.webviewdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    private WebView mWebView;
    private Button mButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置窗口风格为进度条
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);
        setupViews();
    }

    // 初始化
    private void setupViews() {
        mWebView = (WebView) findViewById(R.id.webview);
        WebSettings mWebSettings = mWebView.getSettings();
        // 加上这句话才能使用javascript方法
        mWebSettings.setJavaScriptEnabled(true);
        mWebView.getSettings().setPluginState(PluginState.ON);
        mWebView.getSettings().setPluginsEnabled(true);
        mWebView.setVisibility(View.VISIBLE);

        // 增加接口方法,让html页面调用
        mWebView.addJavascriptInterface(new Object() {
            // 这里我定义了一个打开地图应用的方法
            public void startMap() {
                // Intent mIntent = new Intent();
                // ComponentName component = new ComponentName(
                // "com.google.android.apps.maps",
                // "com.google.android.maps.MapsActivity");
                // mIntent.setComponent(component);
                // startActivity(mIntent);
                String url = "http://static.jifenzhong.com/resources/video/m/2012/20120730/50_20120730092638_27308.mp4";
                Intent it = new Intent(Intent.ACTION_VIEW);
                it.setDataAndType(Uri.parse(url), "video/mp4;video/x-msvideo");
                startActivity(it);
                Toast.makeText(getApplicationContext(), "test", 1000).show();
            }
        }, "demo");
        // 加载页面
        // mWebView.loadUrl("file:///android_asset/demo1.html");
        // mWebView.loadUrl("http://172.16.3.35:8080/hudson/demo.html");
        mWebView.loadUrl("http://m.jifenzhong.com/");
        mWebView.setWebViewClient(new WebViewClientEmb());
        mWebView.setWebChromeClient(new WebChromeClient() {
            // 当WebView进度改变时更新窗口进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // Activity的进度范围在0到10000之间,所以这里要乘以100
                MainActivity.this.setProgress(newProgress * 100);
            }
        });
        // 点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                        mWebView.goBack(); // 后退
                        return true; // 已处理
                    }
                }
                return false;
            }
        });

        mButton = (Button) findViewById(R.id.button);
        // 给button添加事件响应,执行JavaScript的fillContent()方法
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("javascript:fillContent()");
            }
        });
    }

    public class WebViewClientEmb extends WebViewClient {
        // 在WebView中而不是系统默认浏览器中显示页面
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.endsWith(".mp4")) {
                Log.d("URL", "start Mp4");
                Intent it = new Intent(Intent.ACTION_VIEW);
                it.setDataAndType(Uri.parse(url), "video/mp4;video/x-msvideo");
                startActivity(it);
                // Intent intent = new Intent();
                // intent.setAction("android.intent.action.VIEW");
                // Uri content_url = Uri.parse(url);
                // intent.setData(content_url);
                // startActivity(intent);
            } else {
                view.loadUrl(url);
            }
            Log.d("URL", url);
            return true;
        }

        // 页面载入前调用
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        // 页面载入完成后调用
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

    }
}
