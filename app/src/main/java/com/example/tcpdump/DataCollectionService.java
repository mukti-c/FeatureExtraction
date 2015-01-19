package com.example.tcpdump;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;

public class DataCollectionService extends Service {

	// INTERNAL_DATA_PATH is the path to the local data directory
	private static String INTERNAL_DATA_PATH = null;
	
	// TCPDUMPFILENAME is the name of the tcpdump-arm binary in the local data directory
	// tcpdump-arm binary is used as Android runs on the ARM architecture
	private static String TCPDUMPFILENAME = null;
	
	// String for logging
	private static final String TAG = "DataCollectionService";
	
	// For displaying the Notification
	private NotificationManager mNM;
	
	// Notification ID
	private int notifyID = 1;
	
	// TextView to display output
	TextView tvOut;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		INTERNAL_DATA_PATH = getFilesDir().getPath() + "/";
		TCPDUMPFILENAME = "tcpdump";
		//Toast.makeText(getApplicationContext(), INTERNAL_DATA_PATH+TCPDUMPFILENAME, Toast.LENGTH_SHORT).show();
		startTcpdumpCapture();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Display Notification in the Notification bar
		mNM = (NotificationManager) getSystemService (Context.NOTIFICATION_SERVICE);
		showNotification();
		
		// START_NOT_STICKY is for services that are started/stopped implicitly
		// Change to START_NOT_STICKY later.
		return (START_STICKY);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		// Remove the Notification
		mNM.cancel(notifyID);
		//Toast.makeText(getApplicationContext(), "Service stopped.", Toast.LENGTH_SHORT).show();
	}	
	
	private void showNotification() {
		// On clicking the notification, MainActivity should open
				PendingIntent contentIntent = PendingIntent.getActivity(this, 0, 
						new Intent(this, MainActivity.class), 0);
				
		// Use NotificationCompat.Builder to build the notification, display using mNM
		NotificationCompat.Builder mNotifyBuilder= new NotificationCompat.Builder(this)
			.setContentTitle(getText(R.string.notificationTitle))
			.setContentText(getText(R.string.notificationText))
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentIntent(contentIntent);
		
		mNM.notify(notifyID, mNotifyBuilder.build());
	}
	
	private void startTcpdumpCapture () {
		// Starting tcpdump on a separate thread
		new Thread (new Runnable() {

			@Override
			public void run() {
				try {
					startTcpdump();
				} catch (InterruptedException e) {
					Log.e (TAG, "InterruptedException in startTcpdump");
				} catch (IOException e) {
					Log.e (TAG, "IOException in startTcpdump");
				}
			}
		}).start();
	}
	
	private void startTcpdump() throws InterruptedException, IOException {
		Log.v (TAG, "Inside startTcpdump" + System.currentTimeMillis());
		Process sh = null;
		DataOutputStream os = null;
		
		try {
			sh = Runtime.getRuntime().exec("su");
			
			// os is the output stream connected to stdin of the shell.
			// Used for executing commands
			os = new DataOutputStream(sh.getOutputStream());
			
			// Make the tcpdump-arm binary executable
			Log.v (TAG, "CHMOD");
			String command = "su -c chmod 777 " + INTERNAL_DATA_PATH + TCPDUMPFILENAME + "\n";
			sh = Runtime.getRuntime().exec(command);
			
			// Execute the tcpdump command
			Log.v (TAG, "TCPDUMP");
			command = "su -c ." + INTERNAL_DATA_PATH + TCPDUMPFILENAME + " -s 0\n";
			sh = Runtime.getRuntime().exec(command);
			
			// Exit the shell
			//sh = Runtime.getRuntime().exec("exit \n");
			//os.writeBytes(command);  
			//os.flush();  
			//os.writeBytes("exit\n");  
			//os.flush();  
			//os.close();
			
		} catch (Exception e) {
			Log.e (TAG, "Error in startTcpdump\n" + e.getMessage());
			
		} finally {
			try {
				if (os != null) 
					os.close();
			
				if (sh != null)
					sh.destroy();
				
			} catch (Exception e) {
				Log.e (TAG, "Exception in closing DataOutputStream in startTcpdump");
				
			}
		}
	}
}
