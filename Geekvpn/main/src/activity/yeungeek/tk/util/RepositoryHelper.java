package yeungeek.tk.util;

import java.util.List;

import yeungeek.tk.VpnProfileRepository;
import yeungeek.tk.editor.GeekPptpVpnProfileEditor;
import yeungeek.tk.wrapper.VpnProfile;
import android.content.Context;

/**
 * @author Anson.Yang
 *
 */
public class RepositoryHelper {
    private Context context;
    private VpnProfileRepository repository;
    private String username;
    private String password;

    public RepositoryHelper(final Context ctx) {
        this.context = ctx;
        repository = VpnProfileRepository.getInstance(ctx);
    }

    public void populatePptpRepository(final String username, final String password, final String[] vpnNames,
            final String[] vpnips) {
        GeekPptpVpnProfileEditor pptp = null;
        for (int i = 0; i < vpnNames.length; i++) {
            pptp = new GeekPptpVpnProfileEditor(context);
            pptp.setName(vpnNames[i]);
            pptp.setServerName(vpnips[i]);
            pptp.setUsername(username);
            pptp.setPassword(password);
            if (!containsRepository(pptp.getName())) {
                pptp.onSave();
            }
        }
    }

    public void clearRepository() {
        List<VpnProfile> profileList = repository.getAllVpnProfiles();

        if (!profileList.isEmpty()) {
            VpnProfile[] ps = profileList.toArray(new VpnProfile[0]);
            for (VpnProfile p : ps) {
                repository.deleteVpnProfile(p);
            }
        }
    }

    public boolean containsRepository(final String name) {
        List<VpnProfile> profileList = repository.getAllVpnProfiles();
        if (!profileList.isEmpty()) {
            VpnProfile[] ps = profileList.toArray(new VpnProfile[0]);
            for (VpnProfile p : ps) {
                // p.getType().getName().equals(VpnType.PPTP.getName())
                if (p.getName().equals(name))
                    return true;
            }
        }

        return false;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(final String password) {
        this.password = password;
    }
}
