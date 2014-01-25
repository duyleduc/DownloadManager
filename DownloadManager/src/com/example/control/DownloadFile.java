package com.example.control;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.DownloadManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.activities.CustomActivity;
import com.example.activities.MainActivity;
import com.example.model.EnumStateFile;
import com.example.model.PartFile;

public class DownloadFile extends AsyncTask<String, String, String> {

	private DownloadManager dm;
	private Context mCon;
	private PartFile mPart;
	private HttpURLConnection mConnection;
	private BufferedInputStream stream;
	private FileOutputStream fos;
	private FileInputStream fis;
	private IsThreadBlock status;

	public DownloadFile(Context context, PartFile mPart, IsThreadBlock status) {
		this.mCon = context;
		this.mPart = mPart;
		this.status = status;
	}

	@Override
	protected void onPreExecute() {

	}

	@Override
	protected String doInBackground(String... urlD) {
		// TODO Auto-generated method stub

		InputStream ins;
		URL url;
		int count = 0;
		byte data[] = new byte[1024 * 53];
		String partDir = CustomActivity.DLMDIR.getPath()
				+ CustomActivity.CACHDIR + "/" + this.mPart.getId();

		try {

			url = new URL(urlD[0]);
			long end = this.mPart.getBegin() + this.mPart.getSizeChunk() - 1;
			mConnection = (HttpURLConnection) url.openConnection();
			String range = "bytes=" + (this.mPart.getBegin()) + "-" + (end);
			mConnection.setRequestProperty("Range", range);
			mConnection.connect();
			Log.e("range", range);
			synchronized (status) {
				status.setNotBlocked();
				status.notify();
			}

			ins = this.mConnection.getInputStream();

			long total = 0;

			FileOutputStream fos = new FileOutputStream(partDir);

			while ((count = ins.read(data)) != -1 && total < end) {
				fos.write(data, 0, count);
				total += count;
			}

			ins.close();
			fos.close();

		} catch (MalformedURLException e) { // url generated
			e.printStackTrace();
		} catch (IOException e) { // httpurlconnection
			synchronized (status) {
				Log.e("inback block", Long.toString(this.mPart.getBegin()));
				status.setBlocked();
				status.notify();
			}
			e.printStackTrace();
		}
		return urlD[0];
	}

	@Override
	protected void onPostExecute(String file_url) {
		this.mPart.setState(EnumStateFile.DOWNLOADED);
		boolean finish = true;
		for (int i = 0; i < this.mPart.getFile().getParts().size(); i++) {
			finish = finish
					&& (this.mPart.getFile().getParts().get(i).getState() == EnumStateFile.DOWNLOADED);
		}

		if (finish) {
			try {
				this.mPart.getmFileDest().close();
				FileOutputStream fos = new FileOutputStream(
						this.mPart.getFile().path);
				for (PartFile part : this.mPart.getFile().getParts()) {
					String partDir = CustomActivity.DLMDIR.getPath()
							+ CustomActivity.CACHDIR + "/" + part.getId();
					FileInputStream fis = new FileInputStream(partDir);

					byte content[] = new byte[1024 * 53];
					int count = 0;
					while ((count = fis.read(content)) != -1) {
						fos.write(content, 0, count);
					}
					fis.close();
					File f = new File(partDir);
					f.delete();

				}
				fos.close();
				CustomActivity.label.setText(Integer.toString((int) System
						.currentTimeMillis() - GetFileSize.beginT));
				Log.e("finis",
						Integer.toString((int) System.currentTimeMillis()
								- GetFileSize.beginT));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
