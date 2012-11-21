package yeungeek.tk;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    private Button mLoginBtn;
    private String username;
    private String password;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoginBtn = (Button) findViewById(R.id.longin_btn);

        mLoginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                username = getEditorText(R.id.login_username);
                password = getEditorText(R.id.login_password);

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "text is null", Toast.LENGTH_SHORT).show();
                } else {
                    new LoginTask()
                            .execute("http://fengchinet.com/celogin.php?userid=" + username + "&pwd="
                            + password);
                }
            }
        });
    }

    public String getEditorText(final int id) {
        String value = null;
        EditText editor = (EditText) findViewById(id);
        if (editor != null) {
            Editable editable = editor.getText();
            if (editable != null) {
                value = editable.toString().trim();
            }
        }
        return value;
    }

    /**
     * 请求
     * 
     * @author Anson.Yang
     * 
     */
    class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(final String... params) {
            Log.d("MainActivity", "url:" + params[0]);
            HttpGet hg = new HttpGet(params[0]);
            try {
                HttpResponse hr = new DefaultHttpClient().execute(hg);
                if (hr.getStatusLine().getStatusCode() == 200) {
                    String result = EntityUtils.toString(hr.getEntity());
                    Log.d("MainActivity", "MainActivity result:" + result);
                    return result;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            Log.d("MainActivity", "result:" + result);
            String[] rs = result.split(",");
            int code = Integer.parseInt(rs[0]);
            if (200 == code) {
                Intent intent = new Intent(MainActivity.this, VpnListActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(MainActivity.this, "login error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
