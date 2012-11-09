
package android.yeungeek.tk.service;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import android.yeungeek.tk.ExampleActivity;
import android.yeungeek.tk.R;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: UpdateService
 * @Description: TODO
 * @author Anson.Yang
 * @date 2012-10-22 下午4:18:01
 */
public class UpdateService extends IntentService {
    private NotificationManager mNM;

    private static final String TAG = "UpdateService";
    public final static String TASK_NAME = "name";
    public final static int CHECK_UPDATE = 0;
    public final static int DOWNLAOD_UPDATE = 1;

    private Context mContext = null;

    private final Map<Integer, Dialog> dialogs = new HashMap<Integer, Dialog>();

    public UpdateService() {
        super(TAG);
        mContext = this;
        Log.d(TAG, "create updateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Bundle bundle = intent.getExtras();
        Log.d(TAG, "handleintent:" + bundle);
        int task = bundle.getInt(TASK_NAME);
        switch (task) {
            case CHECK_UPDATE:
                Log.d(TAG, "check update");
                // showDialog(1);
                updateHandler.sendMessage(Message.obtain(updateHandler, CHECK_UPDATE, null));
                long endTime = System.currentTimeMillis() + 5 * 1000;
                while (System.currentTimeMillis() < endTime) {
                    synchronized (this) {
                        try {
                            wait(endTime - System.currentTimeMillis());
                        } catch (Exception e) {
                        }
                    }
                }
                break;
            case DOWNLAOD_UPDATE:
                Log.d(TAG, "download update");
                showNotification("download update");
                long endTime1 = System.currentTimeMillis() + 5 * 1000;
                while (System.currentTimeMillis() < endTime1) {
                    synchronized (this) {
                        try {
                            wait(endTime1 - System.currentTimeMillis());
                        } catch (Exception e) {
                        }
                    }
                }
                break;
            default:
                break;
        }
        // hideNotification();
    }

    @Override
    public void setIntentRedelivery(boolean enabled) {

        super.setIntentRedelivery(enabled);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "on create");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "on start");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "on StartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "on destroy");
        // Tell the user we stopped.
        Toast.makeText(this, "destroy",
                Toast.LENGTH_SHORT).show();
        // showSystemDialog();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "on bind");
        return super.onBind(intent);
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification(String text) {
        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.stat_sample, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this
        // notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ExampleActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, "service开启",
                text, contentIntent);

        // We show this for as long as our service is processing a command.
        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        // Send the notification.
        // We use a string id because it is a unique number. We use it later to
        // cancel.
        mNM.notify(1, notification);
    }

    private void hideNotification() {
        mNM.cancel(1);
    }

    private void showDialog(int code) {
        Log.d(TAG, "showDialog code:" + code);
        Dialog dialog = dialogs.get(code);
        if (dialog == null) {
            dialog = createDialog(code);
            dialogs.put(code, dialog);
            dialog.show();
        } else {
            dialog.show();
        }
    }

    /**
     * @param code
     * @return
     */
    private Dialog createDialog(int code) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("更新");
        dialogBuilder.setIcon(android.R.drawable.ic_dialog_info);
        switch (code) {
            case 1:
                dialogBuilder.setMessage("更新内容");
                dialogBuilder.setPositiveButton("更新",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                showNotification("download update");
                                // Intent intent = new Intent(mContext,
                                // UpdateService.class);
                                // intent.putExtra(UpdateService.TASK_NAME,
                                // UpdateService.CHECK_UPDATE);
                                // startService(intent);
                                Log.d(TAG, "updateHandler :" + updateHandler);
                            }
                        });
                break;
            default:
                break;
        }
        AlertDialog dialog = dialogBuilder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private void showSystemDialog() {
        /* create ui */
        View v = View.inflate(mContext, R.layout.main, null);
        AlertDialog.Builder b = new AlertDialog.Builder(mContext);
        b.setView(v);
        AlertDialog d = b.create();
        d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        // d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
        d.show();

        /* set size & pos */
        WindowManager.LayoutParams lp = d.getWindow().getAttributes();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (display.getHeight() > display.getWidth()) {
            // lp.height = (int) (display.getHeight() * 0.5);
            lp.width = (int) (display.getWidth() * 1.0);
        } else {
            // lp.height = (int) (display.getHeight() * 0.75);
            lp.width = (int) (display.getWidth() * 0.5);
        }
        d.getWindow().setAttributes(lp);
    }

    private final Handler updateHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHECK_UPDATE:
                    showDialog(1);
                    break;
                default:
                    break;
            }
        }
    };
}
