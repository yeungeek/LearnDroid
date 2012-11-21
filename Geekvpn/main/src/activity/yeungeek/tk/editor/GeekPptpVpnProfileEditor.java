package yeungeek.tk.editor;

import yeungeek.tk.wrapper.PptpProfile;
import yeungeek.tk.wrapper.VpnProfile;
import android.content.Context;

/**
 * @author Anson.Yang
 * 
 */
public class GeekPptpVpnProfileEditor extends GeekVpnProfileEditor {
    private String name;
    private String serverName;
    private String username;
    private String password;

    public GeekPptpVpnProfileEditor(final Context mContext) {
        super(mContext);
    }

    public GeekPptpVpnProfileEditor(final Context mContext, final String name, final String serverName,
            final String username, final String password) {
        super(mContext);
        this.name = name;
        this.serverName = serverName;
        this.username = username;
        this.password = password;
    }

    @Override
    protected VpnProfile createProfile() {
        return new PptpProfile(mContext);
    }

    @Override
    protected void doPopulateProfile() {
        PptpProfile profile = getProfile();
        profile.setName(name);
        profile.setServerName(serverName);
        // 可以不设置
        profile.setDomainSuffices("8.8.8.8");
        profile.setUsername(username);
        profile.setPassword(password);
        profile.setEncryptionEnabled(true);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the serverName
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * @param serverName the serverName to set
     */
    public void setServerName(final String serverName) {
        this.serverName = serverName;
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
