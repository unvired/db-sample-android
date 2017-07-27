package com.unvired.sample.db.util;

import android.annotation.SuppressLint;

import com.unvired.gcm.UnviredGCMListenerService;
import com.unvired.sync.in.NotificationBO;

@SuppressLint("NewApi")
public class GCMIntentService extends UnviredGCMListenerService {

    @Override
    public void onCallBack(String notificationType, String notificationCount, String notificationMessage) {

    }

    @Override
    public void onCallBack(NotificationBO notificationBO) {

    }

}