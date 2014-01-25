package com.example.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.downloadmanager.R;
import com.example.model.MFile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;

public class MainActivity extends Activity {


	public static List<MFile> files = new ArrayList<MFile>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		startDownload();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void startDownload(){
		Intent download = getIntent();
		startActivity(download);

	}



}
