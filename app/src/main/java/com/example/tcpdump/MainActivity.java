package com.example.tcpdump;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button bStart, bStop, bRead;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		bStart = (Button) findViewById (R.id.bStartService);
		bStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startService (new Intent(getBaseContext(), DataCollectionService.class));
			}
		});
		
		bStop = (Button) findViewById (R.id.bStopService);
		bStop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				stopService (new Intent(getBaseContext(), DataCollectionService.class));
			}
		});

        bRead = (Button) findViewById (R.id.bReadFile);
        bRead.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent readIntent = new Intent ("com.example.tcpdump.READFILE1");
                startActivity (readIntent);
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}