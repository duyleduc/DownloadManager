package com.example.activities;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.example.control.DownloadFile;
import com.example.control.GetFileSize;
import com.example.downloadmanager.R;
import com.example.model.EnumStateFile;
import com.example.model.MFile;
import com.example.model.PartFile;

public class CustomActivity extends Activity {
	public static File DLMDIR;
	public final static String DIRNAME = "/DDM";
	public final static String IMDIR = "/images";
	public final static String VIDDIR = "/video";
	public final static String MUDIR = "/music";
	public final static String OTHERDIR = "/other";
	public final static String COMDIR = "/compressed";
	public final static String DOCDIR = "/document";
	public final static String CACHDIR = "/cache";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom);
		createFolder();

		TextView label = (TextView) findViewById(R.id.show_data);
		Uri url = getIntent().getData();
		label.setText(url.toString());
		new GetFileSize(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
				url.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.custom, menu);
		return true;
	}

	private void createFolder() {
		DLMDIR = new File(Environment.getExternalStorageDirectory().getPath()
				+ DIRNAME);
		if (!DLMDIR.exists()) {
			DLMDIR.mkdirs();

			// images
			new File(DLMDIR.getPath() + IMDIR).mkdirs();
			// video
			new File(DLMDIR.getPath() + VIDDIR).mkdirs();
			// music
			new File(DLMDIR.getPath() + MUDIR).mkdirs();
			// apk
			new File(DLMDIR.getPath() + OTHERDIR).mkdirs();
			// compressed
			new File(DLMDIR.getPath() + COMDIR).mkdirs();
			// document
			new File(DLMDIR.getPath() + DOCDIR).mkdirs();
			// cache
			new File(DLMDIR.getPath() + CACHDIR).mkdirs();
		}
	}

}
