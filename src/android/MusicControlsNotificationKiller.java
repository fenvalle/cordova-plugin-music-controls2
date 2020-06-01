package com.homerours.musiccontrols;
import android.app.Notification;
import android.app.Service;
import android.os.IBinder;
import android.os.Binder;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.PowerManager;

import static android.os.PowerManager.PARTIAL_WAKE_LOCK;

public class MusicControlsNotificationKiller extends Service {
	private static int NOTIFICATION_ID;
	private NotificationManager mNM;
	private final IBinder mBinder = new NotificationBinder(this);
	private PowerManager.WakeLock wakeLock;
	private boolean foregroundStarted = false;

	@Override
	public IBinder onBind(Intent intent) {
		this.NOTIFICATION_ID=intent.getIntExtra("notificationID",1);
		return mBinder;
	}
	public class NotificationBinder extends Binder {
		public final Service service;
		public NotificationBinder(Service service) {
			this.service = service;
		}
		public MusicControlsNotificationKiller getInstance() {
			return MusicControlsNotificationKiller.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNM.cancel(NOTIFICATION_ID);
	}

	@Override
	public void onDestroy() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNM.cancel(NOTIFICATION_ID);
		sleepWell();
	}

	public void keepAwake()
	{
		if (foregroundStarted == true) return;
		PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
		wakeLock = pm.newWakeLock(PARTIAL_WAKE_LOCK, "MusicControls:NotificationKiller");
		wakeLock.acquire();
	}
	public void startForeground(Notification notification) {
		try {
			startForeground(NOTIFICATION_ID, notification);
			foregroundStarted = true;
		}
		finally { }
	}
	public void sleepWell()
	{
		if (foregroundStarted == false) return;
		try {
			stopForeground(true);
			this.foregroundStarted = false;
			if (wakeLock == null) return;
			if (wakeLock.isHeld()) { try { wakeLock.release(); } catch (Exception e) { } }
		} finally {
			wakeLock = null;
		}
	}

}
