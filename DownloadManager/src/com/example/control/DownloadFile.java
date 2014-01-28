package com.example.control;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.activities.Downloading;
import com.example.activities.MainDownloadManager;
import com.example.model.EnumStateFile;
import com.example.model.MFile;
import com.example.model.PartFile;

public class DownloadFile extends AsyncTask<String, String, String> {

	public final static int MAX_TIMEOUT = 7000;

	private Context mCon;
	private PartFile mPart;
	private HttpURLConnection mConnection;
	private IsThreadBlock status;

	public DownloadFile(Context context, PartFile mPart, IsThreadBlock status) {
		this.mCon = context;
		this.mPart = mPart;
		this.status = status;
	}

	@Override
	protected void onPreExecute() {
		this.mPart.setState(EnumStateFile.DOWNLOADING);
	}

	@Override
	protected void onProgressUpdate(String... count) {
		long value = Long.parseLong(count[0]);
		long aset = this.mPart.getFile().getDownloadedLenght();
		aset += value;

		MFile file = this.mPart.getFile();
		double per = (double) aset / (double) this.mPart.getFile().getfSize();
		per = Math.round(per * 100 * 1.0) / 1.0;

		file.setPercentDownloaded(Double.toString(per) + "%");
		file.setDownloadedLenght(aset);

		Downloading.mAdapterD.notifyDataSetChanged();

	}

	@Override
	protected String doInBackground(String... urlD) {
		// TODO Auto-generated method stub

		InputStream ins;
		URL url;
		int count = 0;
		byte data[] = new byte[1024 * 50];
		String partDir = MainDownloadManager.DLMDIR.getPath()
				+ MainDownloadManager.CACHDIR + "/" + this.mPart.getId();

		try {

			url = new URL(urlD[0]);
			long end = this.mPart.getBegin() + this.mPart.getSizeChunk() - 1;
			mConnection = (HttpURLConnection) url.openConnection();
			String range = "bytes=" + (this.mPart.getBegin()) + "-" + (end);
			mConnection.setRequestProperty("Range", range);

			mConnection.setReadTimeout(MAX_TIMEOUT);// 2s
			mConnection.setConnectTimeout(MAX_TIMEOUT);
			mConnection.connect();

			synchronized (status) {
				status.setNotBlocked();
				status.notify();
			}

			ins = new BufferedInputStream(this.mConnection.getInputStream());

			long total = 0;

			FileOutputStream fos = new FileOutputStream(partDir);

			while ((count = ins.read(data)) != -1) {
				fos.write(data, 0, count);
				publishProgress(Long.toString(count));
				total += count;
			}
			fos.flush();
			ins.close();
			fos.close();
			this.mConnection.disconnect();
		} catch (MalformedURLException e) { // url generated
			e.printStackTrace();
		} catch (IOException e) { // httpurlconnection
			try {
				mConnection.connect();
				synchronized (status) {
					status.setBlocked();
					status.notify();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			e.printStackTrace();
		}
		return urlD[0];
	}

	@Override
	protected void onPostExecute(String file_url) {
		this.mPart.setState(EnumStateFile.DOWNLOADED);
		boolean finish = true;
		if (this.mPart.getFile().getDownloadedLenght() != this.mPart.getFile()
				.getfSize()) {
			finish = false;
		}

		if (finish) {
			this.mConnection.disconnect();
			try {
				FileOutputStream fos = new FileOutputStream(
						this.mPart.getFile().path);
				for (PartFile part : this.mPart.getFile().getParts()) {

					String partDir = MainDownloadManager.DLMDIR.getPath()
							+ MainDownloadManager.CACHDIR + "/" + part.getId();
					FileInputStream fis = new FileInputStream(partDir);

					byte content[] = new byte[1024 * 50];
					int count = 0;
					while ((count = fis.read(content)) != -1) {
						fos.write(content, 0, count);
					}
					fis.close();
					File f = new File(partDir);
					f.delete();

				}
				fos.flush();
				fos.close();

				MainDownloadManager.downloaded.add(this.mPart.getFile());
				MainDownloadManager.files.remove(this.mPart.getFile());
				Downloading.mAdapterD.notifyDataSetChanged();

				if (MainDownloadManager.queues.size() > 0) {
					MFile file = MainDownloadManager.queues.remove(0);
					new GetFileSize(mCon, file).executeOnExecutor(
							THREAD_POOL_EXECUTOR, file.getfUrl());
					Downloading.mAdapterQ.notifyDataSetChanged();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
