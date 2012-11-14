
package yeungeek.tk;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

/**
 * @ClassName: VpnListActivity
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-11-14 下午05:17:40
 */
public class VpnListActivity extends Activity {

    private VpnProfileRepository repository;
    private VpnActor actor;
    private ListView vpnListView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_vpns);

        repository = VpnProfileRepository.getInstance(getApplicationContext());
        actor = new VpnActor(getApplicationContext());

        vpnListView = (ListView) findViewById(R.id.vpn_list);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
