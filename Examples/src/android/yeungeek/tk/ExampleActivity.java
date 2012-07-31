
package android.yeungeek.tk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.yeungeek.tk.pinnedheader.PinnedHeaderActivity;

public class ExampleActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        Button mPinnedBtn = (Button) findViewById(R.id.example_pinnedheader);
        mPinnedBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExampleActivity.this, PinnedHeaderActivity.class);
                startActivity(intent);
            }
        });
    }
}
