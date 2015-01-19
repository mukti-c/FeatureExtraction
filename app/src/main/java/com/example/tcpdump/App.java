package com.example.tcpdump;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class App extends Application {

	final String TAG = "AppInstallation";
	
	private void copyFile (InputStream in, OutputStream out) {
		byte[] buffer = new byte[1024];
		int bytesRead;
		
		try {
			while ((bytesRead = in.read(buffer)) > 0)
				out.write(buffer, 0, bytesRead);
			in.close();
			out.close();
			
		} catch (IOException ioe) {
			Log.d (TAG, "Error in copying file.");
		}
	}
	
	@Override
	public void onCreate() {
		
		super.onCreate();

        // Initialize all global variables
        if (GlobalVariables.last100Conn == null) {
            GlobalVariables.last100Conn = new PastConnQueue();
        }
        else {
            // Clears the queue
            GlobalVariables.last100Conn.clear();
        }

        if (GlobalVariables.lastTwoSec == null) {
            GlobalVariables.lastTwoSec = new LastTwoSecQueue();
        }
        else {
            // Clears the queue
            GlobalVariables.lastTwoSec.clear();
        }
		// Will check if tcpdump is installed on device or not
		// If not installed, it will install
		
		/*
		 * SharedPreferences allows storing of data of an application, in the form of a key, value pair.
		 * Here, key is "isTcpdumpInstalled", will return false if not installed.
		 */
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (!prefs.getBoolean("isTcpdumpInstalled", false)) {
			// Tcpdump is not installed, hence install it.
			
			InputStream in = null;
			OutputStream out = null;
			Log.d (TAG, "Trying to copy the tcpdump executable to the data folder");
			
			try {
				// Get files directory (/data/data/com.example/tcpdump/files)
				String appFilesDirectory = getFilesDir().getPath();
				String filename = "tcpdump";

                in = getApplicationContext().getResources().openRawResource(R.raw.tcpdump);
                File outFile = new File (appFilesDirectory, filename);
                out = new FileOutputStream (outFile);
                copyFile (in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
				
				// Mark that tcpdump has been installed
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean("isTcpdumpInstalled", true);
				editor.commit();
				
			} catch (IOException ioe) {
				Log.d (TAG, "File failed to copy");
			}
		}

        if (!prefs.getBoolean("pcap", false)) {
            // Copy pcap file

            InputStream in = null;
            OutputStream out = null;
            Log.d (TAG, "Trying to copy the pcap file to the data folder");

            try {
                // Get files directory (/data/data/com.example/tcpdump/files)
                String appFilesDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
                String filename = "dump.pcap";

                in = getApplicationContext().getResources().openRawResource(R.raw.dump);
                File outFile = new File (appFilesDirectory, filename);
                out = new FileOutputStream (outFile);
                copyFile (in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;

                // Mark that tcpdump has been installed
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("pcap", true);
                editor.commit();

            } catch (IOException ioe) {
                Log.d (TAG, "File failed to copy");
            }
        }
	}
}
