package yeungeek.tk.editor;

import yeungeek.tk.VpnProfileRepository;
import yeungeek.tk.wrapper.InvalidProfileException;
import yeungeek.tk.wrapper.VpnProfile;
import yeungeek.tk.wrapper.VpnState;
import android.content.Context;

/**
 * @author Anson.Yang
 * 
 */
public abstract class GeekVpnProfileEditor {
    private VpnProfile profile;
    private VpnProfileRepository repository;
    protected Context mContext;

    public GeekVpnProfileEditor(final Context mContext) {
        this.mContext = mContext;
        repository = VpnProfileRepository.getInstance(mContext);
    }

    public void onSave() {
        try {
            profile = createProfile();
            populateProfile();
            saveProfile();
        } catch (InvalidProfileException e) {
            throw e;
        }
    }

    private void populateProfile() {
        profile.setState(VpnState.IDLE);
        doPopulateProfile();
        repository.checkProfile(profile);
    }

    private void saveProfile() {
        repository.addVpnProfile(profile);
    }

    @SuppressWarnings("unchecked")
    protected <T extends VpnProfile> T getProfile() {
        return (T) profile;
    }

    protected abstract VpnProfile createProfile();

    protected abstract void doPopulateProfile();
}
