package com.example.activities;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;

import com.example.control.GetFileSize;
import com.example.downloadmanager.R;
import com.example.model.MFile;
import com.example.viewpageradapter.MyFragmentPagerAdapter;

public class MainDownloadManager extends FragmentActivity implements Runnable {
	public static File DLMDIR;
	public final static String DIRNAME = "/DDM";
	public final static String IMDIR = "/images";
	public final static String VIDDIR = "/video";
	public final static String MUDIR = "/music";
	public final static String OTHERDIR = "/other";
	public final static String COMDIR = "/compressed";
	public final static String DOCDIR = "/document";
	public final static String CACHDIR = "/cache";

	public static List<MFile> files = new ArrayList<MFile>();
	public static List<MFile> queues = new ArrayList<MFile>();
	public static List<MFile> downloaded = new ArrayList<MFile>();

	private ActionBar mActionBar;
	private ViewPager mPager;
	private Uri uri;
	private long fLength;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_download_manager);
		createFolder();
		initialize();
		start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void start() {
		uri = getIntent().getData();
		if (uri != null) {
			Thread th = new Thread(this);
			th.start();
		}
	}

	private void startDownload() {
		MFile file = new MFile(uri.toString(), fLength);
		if (MainDownloadManager.files.size() < 2) {
			MainDownloadManager.files.add(file);
			Downloading.mAdapterD.notifyDataSetChanged();
			new GetFileSize(getApplicationContext(), file).executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, uri.toString());
		} else {
			MainDownloadManager.queues.add(file);
			Downloading.mAdapterQ.notifyDataSetChanged();
		}

	}

	private void createFolder() {
		MainDownloadManager.DLMDIR = new File(Environment
				.getExternalStorageDirectory().getPath()
				+ MainDownloadManager.DIRNAME);
		if (!MainDownloadManager.DLMDIR.exists()) {
			MainDownloadManager.DLMDIR.mkdirs();

			// images
			new File(MainDownloadManager.DLMDIR.getPath()
					+ MainDownloadManager.IMDIR).mkdirs();
			// video
			new File(MainDownloadManager.DLMDIR.getPath()
					+ MainDownloadManager.VIDDIR).mkdirs();
			// music
			new File(MainDownloadManager.DLMDIR.getPath()
					+ MainDownloadManager.MUDIR).mkdirs();
			// apk
			new File(MainDownloadManager.DLMDIR.getPath()
					+ MainDownloadManager.OTHERDIR).mkdirs();
			// compressed
			new File(MainDownloadManager.DLMDIR.getPath()
					+ MainDownloadManager.COMDIR).mkdirs();
			// document
			new File(MainDownloadManager.DLMDIR.getPath()
					+ MainDownloadManager.DOCDIR).mkdirs();
			// cache
			new File(MainDownloadManager.DLMDIR.getPath()
					+ MainDownloadManager.CACHDIR).mkdirs();
		}
	}

	private void initialize() {
		mActionBar = getActionBar();

		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mPager = (ViewPager) findViewById(R.id.view_pager);
		FragmentManager fm = getSupportFragmentManager();
		ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				mActionBar.setSelectedNavigationItem(position);
			}

		};
		mPager.setOnPageChangeListener(pageChangeListener);
		MyFragmentPagerAdapter mfga = new MyFragmentPagerAdapter(fm);

		mPager.setAdapter(mfga);
		mActionBar.setDisplayShowTitleEnabled(true);

		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				int tabCurrent = tab.getPosition();
				mPager.setCurrentItem(tabCurrent);
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}
		};

		Tab tab = mActionBar.newTab().setText("Queue")
				.setTabListener(tabListener);

		mActionBar.addTab(tab);

		tab = mActionBar.newTab().setText("History")
				.setTabListener(tabListener);
		mActionBar.addTab(tab);

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		try {
			URL url = new URL(uri.toString());
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.connect();
			final String contentLengthStr = connection
					.getHeaderField("content-length");
			fLength = Long.parseLong(contentLengthStr);

			connection.disconnect();
			handler.sendEmptyMessage(0);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			startDownload();
		}
	};
}
