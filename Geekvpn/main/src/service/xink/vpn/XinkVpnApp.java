/*
 * Copyright 2011 yingxinwu.g@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package xink.vpn;

import static org.acra.ReportField.*;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

/**
 * Application context of XinkVpn
 * 
 * @author ywu
 * 
 */
@ReportsCrashes(formKey = "dFFlTmN4RktlOXUySlJna0NmeWFyTUE6MQ",
 mode = ReportingInteractionMode.DIALOG,
 resDialogTitle = R.string.crash_dialog_title,
 resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
 resDialogText = R.string.crash_dialog_text,
 resDialogOkToast = R.string.crash_dialog_ok_toast,
 resDialogIcon = android.R.drawable.ic_dialog_info,

 customReportContent = {
        APP_VERSION_NAME, APP_VERSION_CODE, USER_COMMENT, ANDROID_VERSION, PHONE_MODEL, CUSTOM_DATA, STACK_TRACE, LOGCAT }
)
public class XinkVpnApp extends Application {

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        ACRA.init(this);
        super.onCreate();
    }
}
