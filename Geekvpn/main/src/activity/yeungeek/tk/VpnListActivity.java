
package yeungeek.tk;

import static yeungeek.tk.vpn.Constants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yeungeek.tk.util.RepositoryHelper;
import yeungeek.tk.vpn.Utils;
import yeungeek.tk.wrapper.VpnProfile;
import yeungeek.tk.wrapper.VpnState;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * @ClassName: VpnListActivity
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-11-14 下午05:17:40
 */
public class VpnListActivity extends Activity {
    private final static String TAG = VpnListActivity.class.getSimpleName();

    private VpnProfileRepository repository;
    private VpnActor actor;
    private ListView vpnListView;
    private List<Map<String, VpnViewItem>> vpnListViewContent;
    private VpnViewBinder vpnViewBinder = new VpnViewBinder();
    private VpnViewItem activeVpnItem;
    private SimpleAdapter vpnListAdapter;
    private RepositoryHelper reposityoryHelper;
    private BroadcastReceiver stateBroadcastReceiver;

    private static final String ROWITEM_KEY = "vpn";
    // views on a single row will bind to the same data object
    private static final String[] VPN_VIEW_KEYS = new String[] { ROWITEM_KEY, ROWITEM_KEY, ROWITEM_KEY };
    private static final int[] VPN_VIEWS = new int[] { R.id.radioActive, R.id.tgbtnConn, R.id.txtStateMsg };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_vpns);
        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        final String password = intent.getStringExtra("password");

        repository = VpnProfileRepository.getInstance(getApplicationContext());
        actor = new VpnActor(getApplicationContext());

        final String[] vpnNames = getResources().getStringArray(R.array.vpn_names);
        final String[] vpnIps = getResources().getStringArray(R.array.vpn_ips);

        // 加入list
        reposityoryHelper = new RepositoryHelper(getApplicationContext());
        reposityoryHelper.populatePptpRepository(username, password, vpnNames, vpnIps);

        save();

        vpnListViewContent = new ArrayList<Map<String, VpnViewItem>>();
        vpnListView = (ListView) findViewById(R.id.vpn_list);
        buildVpnListView();

        registerReceivers();

        checkAllVpnStatus();
    }

    private void buildVpnListView() {
        loadContent();

        vpnListAdapter = new SimpleAdapter(this, vpnListViewContent, R.layout.vpn_profile, VPN_VIEW_KEYS, VPN_VIEWS);
        vpnListAdapter.setViewBinder(vpnViewBinder);
        vpnListView.setAdapter(vpnListAdapter);
    }

    private void loadContent() {
        vpnListViewContent.clear();
        activeVpnItem = null;

        String activeProfileId = repository.getActiveProfileId();
        List<VpnProfile> allVpnProfiles = repository.getAllVpnProfiles();

        for (VpnProfile vpnProfile : allVpnProfiles) {
            addToVpnListView(activeProfileId, vpnProfile);
        }
    }

    private void addToVpnListView(final String activeProfileId, final VpnProfile vpnProfile) {
        if (vpnProfile == null)
            return;

        VpnViewItem item = makeVpnViewItem(activeProfileId, vpnProfile);

        Map<String, VpnViewItem> row = new HashMap<String, VpnViewItem>();
        row.put(ROWITEM_KEY, item);

        vpnListViewContent.add(row);
    }

    private VpnViewItem makeVpnViewItem(final String activeProfileId, final VpnProfile vpnProfile) {
        VpnViewItem item = new VpnViewItem();
        item.profile = vpnProfile;

        if (vpnProfile.getId().equals(activeProfileId)) {
            item.isActive = true;
            activeVpnItem = item;
        }
        return item;
    }

    private void registerReceivers() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_VPN_CONNECTIVITY);
        stateBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                String action = intent.getAction();

                if (ACTION_VPN_CONNECTIVITY.equals(action)) {
                    onStateChanged(intent);
                } else {
                    Log.d(TAG, "VPNSettings receiver ignores intent:" + intent); //$NON-NLS-1$
                }
            }
        };
        registerReceiver(stateBroadcastReceiver, filter);
    }

    private void onStateChanged(final Intent intent) {
        //Log.d(TAG, "onStateChanged: " + intent); //$NON-NLS-1$

        final String profileName = intent.getStringExtra(BROADCAST_PROFILE_NAME);
        final VpnState state = Utils.extractVpnState(intent);
        final int err = intent.getIntExtra(BROADCAST_ERROR_CODE, VPN_ERROR_NO_ERROR);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stateChanged(profileName, state, err);
            }
        });
    }

    private void stateChanged(final String profileName, final VpnState state, final int errCode) {
        //Log.d(TAG, "stateChanged, '" + profileName + "', state: " + state + ", errCode=" + errCode); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        VpnProfile p = repository.getProfileByName(profileName);

        if (p == null) {
            Log.w(TAG, profileName + " NOT found"); //$NON-NLS-1$
            return;
        }

        p.setState(state);
        refreshVpnListView();
    }
    private void checkAllVpnStatus() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                actor.checkAllStatus();
            }
        }, "vpn-state-checker").start(); //$NON-NLS-1$
    }

    private void save() {
        repository.save();
    }

    private void connect(final VpnProfile p) {
        actor.connect(p);
    }

    private void disconnect() {
        actor.disconnect();
    }

    private void vpnItemActivated(final VpnViewItem activatedItem) {
        if (activeVpnItem == activatedItem)
            return;

        if (activeVpnItem != null) {
            activeVpnItem.isActive = false;
        }

        activeVpnItem = activatedItem;
        actor.activate(activeVpnItem.profile);
        refreshVpnListView();
    }

    private void refreshVpnListView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                vpnListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceivers();
        super.onDestroy();
    }

    private void unregisterReceivers() {
        if (stateBroadcastReceiver != null) {
            unregisterReceiver(stateBroadcastReceiver);
        }
    }

    final class VpnViewBinder implements ViewBinder {

        @Override
        public boolean setViewValue(final View view, final Object data, final String textRepresentation) {
            if (!(data instanceof VpnViewItem))
                return false;

            VpnViewItem item = (VpnViewItem) data;
            boolean bound = true;

            if (view instanceof RadioButton) {
                bindVpnItem((RadioButton) view, item);
            } else if (view instanceof ToggleButton) {
                bindVpnState((ToggleButton) view, item);
            } else if (view instanceof TextView) {
                bindVpnStateMsg(((TextView) view), item);
            } else {
                bound = false;
                Log.d(TAG, "unknown view, not bound: v=" + view + ", data=" + textRepresentation); //$NON-NLS-1$ //$NON-NLS-2$
            }

            return bound;
        }

        private void bindVpnItem(final RadioButton view, final VpnViewItem item) {
            view.setOnCheckedChangeListener(null);

            view.setText(item.profile.getName());
            view.setChecked(item.isActive);

            view.setOnCheckedChangeListener(item);
        }

        private void bindVpnState(final ToggleButton view, final VpnViewItem item) {
            view.setOnCheckedChangeListener(null);

            VpnState state = item.profile.getState();
            view.setChecked(state == VpnState.CONNECTED);
            view.setEnabled(Utils.isInStableState(item.profile));

            view.setOnCheckedChangeListener(item);
        }

        private void bindVpnStateMsg(final TextView textView, final VpnViewItem item) {
            VpnState state = item.profile.getState();
            String txt = getStateText(state);
            textView.setVisibility(TextUtils.isEmpty(txt) ? View.INVISIBLE : View.VISIBLE);
            textView.setText(txt);
        }

        private String getStateText(final VpnState state) {
            String txt = ""; //$NON-NLS-1$
            switch (state) {
            case CONNECTING:
                txt = getString(R.string.connecting);
                break;
            case DISCONNECTING:
                txt = getString(R.string.disconnecting);
                break;
            }

            return txt;
        }
    }

    final class VpnViewItem implements OnCheckedChangeListener {
        VpnProfile profile;
        boolean isActive;

        @Override
        public void onCheckedChanged(final CompoundButton button, final boolean isChecked) {

            if (button instanceof RadioButton) {
                onActivationChanged(isChecked);
            } else if (button instanceof ToggleButton) {
                toggleState(isChecked);
            }
        }

        private void onActivationChanged(final boolean isChecked) {
            if (isActive == isChecked)
                return;

            isActive = isChecked;

            if (isActive) {
                vpnItemActivated(this);
            }
        }

        private void toggleState(final boolean isChecked) {
            if (isChecked) {
                connect(profile);
            } else {
                disconnect();
            }
        }

        @Override
        public String toString() {
            return profile.getName();
        }
    }
}
