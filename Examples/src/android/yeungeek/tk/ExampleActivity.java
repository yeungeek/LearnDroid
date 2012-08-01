
package android.yeungeek.tk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.yeungeek.tk.pinnedheader.PinnedHeaderActivity;
import android.yeungeek.tk.popupwindow.PopupWindowActivity;

public class ExampleActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        Button mPinnedBtn = (Button) findViewById(R.id.example_pinnedheader);
        Button mPopupBtn = (Button) findViewById(R.id.example_popupwindow);

        mPopupBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExampleActivity.this, PopupWindowActivity.class);
                startActivity(intent);
            }
        });

        mPinnedBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExampleActivity.this, PinnedHeaderActivity.class);
                startActivity(intent);
            }
        });
    }
}
