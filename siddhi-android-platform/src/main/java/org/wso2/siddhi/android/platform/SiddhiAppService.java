/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.siddhi.android.platform;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;

/**
 * Android Service class to execute Siddhi Apps.
 */
public class SiddhiAppService extends Service {

    //Keep this instance since there should be a method for Siddhi extensions to access android
    // service. Could not get the instance dynamically without having access to a Android activity.
    private static SiddhiAppService curreentInstance;
    private RequestController requestController;
    private AppManager appManager;

    //Android Service notification details
    public static final String SIDDHI_CHANNEL_ID = "org.wso2.ANDROID_SIDDHI_PLATFORM";
    public static final String NOTIFICATION_CHANNEL_NAME = "SIDDHI_CHANNEL";
    public static final int NOTIFICATION_ID = 100;
    public static final String NOTIFICATION_TITLE = "Siddhi";
    public static final String NOTIFICATION_BODY = "Siddhi Platform Service started";

    public SiddhiAppService() {
        appManager = new AppManager();
        requestController = new RequestController();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SiddhiAppService.curreentInstance = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, createNotification(SIDDHI_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NOTIFICATION_TITLE, NOTIFICATION_BODY, R.drawable.icon, NOTIFICATION_ID,
                false));
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return requestController;
    }

    public static int getAppIcon() {
        return R.drawable.icon;
    }

    private class RequestController extends SiddhiAppController.Stub {

        @Override
        public String startSiddhiApp(String siddhiApp) throws RemoteException {
            return appManager.startApp(siddhiApp);
        }

        @Override
        public void stopSiddhiApp(String siddhiAppName) throws RemoteException {
            appManager.stopApp(siddhiAppName);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SiddhiAppService.curreentInstance = null;
    }

    public Notification createNotification(String notificationChanelId,
                                           String notificationChannelName, String notificationTitle,
                                           String notificationBody, int notificationIcon,
                                           int notificationId,
                                           boolean enableStyle) {
        //create android notifications
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(notificationChanelId,
                    notificationChannelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            Notification.Builder builder = new Notification.Builder(getApplicationContext(),
                    notificationChanelId)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationBody)
                    .setSmallIcon(notificationIcon)
                    .setAutoCancel(true);
            if (enableStyle) {
                builder.setStyle(new Notification.BigTextStyle().bigText(notificationBody));
            }
            notification = builder.build();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                    notificationChannelName)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationBody)
                    .setSmallIcon(notificationIcon);
            if (enableStyle) {
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(notificationBody));
            }
            notification = builder.build();
        }
        notificationManager.notify(notificationId, notification);
        return notification;
    }

    public static SiddhiAppService getServiceInstance(){
        return SiddhiAppService.curreentInstance;
    }
}
