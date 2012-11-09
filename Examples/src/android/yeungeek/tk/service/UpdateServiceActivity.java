
package android.yeungeek.tk.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.yeungeek.tk.R;

/**
 * @ClassName: UpdateServiceActivity
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-10-22 下午4:17:07
 */
public class UpdateServiceActivity extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_update_controller);

        Button checkUpdateBtn = (Button) findViewById(R.id.check_update);
        checkUpdateBtn.setOnClickListener(this);

        Button downloadBtn = (Button) findViewById(R.id.download_update);
        downloadBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_update:
                Intent intent = new Intent(UpdateServiceActivity.this, UpdateService.class);
                intent.putExtra(UpdateService.TASK_NAME, UpdateService.CHECK_UPDATE);
                startService(intent);
                break;
            case R.id.download_update:
                startService(new Intent(UpdateServiceActivity.this, UpdateService.class).putExtra(
                        UpdateService.TASK_NAME, UpdateService.DOWNLAOD_UPDATE));
                showToast("Test");
                break;
            default:
                break;
        }
    }

    public void showToast(String tip) {
        Toast toast = Toast.makeText(this, tip,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
