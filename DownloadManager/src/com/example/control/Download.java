package com.example.control;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import android.app.DownloadManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.activities.CustomActivity;
import com.example.model.EnumStateFile;
import com.example.model.PartFile;

public class Download extends AsyncTask<String, String, String> {
	private int beginT;

	private Context mCon;
	private PartFile mPart;
	private HttpURLConnection mConnection;

	private FileOutputStream fos;
	private FileInputStream fis;
	private int count = 0;
	private RandomAccessFile mRAF;

	public Download(Context context, PartFile mPart) {
		this.mCon = context;
		this.mPart = mPart;
		this.mRAF = this.mPart.getmFileDest();
	}

	@Override
	protected void onPreExecute() {

	}

	@Override
	protected String doInBackground(String... urlD) {
		// TODO Auto-generated method stub

		InputStream ins;
		URL url;

		final byte data[] = new byte[1024 * 32];
		final String partDir = CustomActivity.DLMDIR.getPath()
				+ CustomActivity.CACHDIR + "/" + this.mPart.getId();
		try {
			beginT = (int) System.currentTimeMillis();
			url = new URL(urlD[0]);
			long end = this.mPart.getBegin() + this.mPart.getSizeChunk() - 1;
			mConnection = (HttpURLConnection) url.openConnection();
			String range = "bytes=" + (this.mPart.getBegin()) + "-" + (end);
			mConnection.setRequestProperty("Range", range);
			Log.e("range", range);
			mConnection.connect();
			// mConnection.notify();
			ins = this.mConnection.getInputStream();

			ReadableByteChannel rbc = Channels.newChannel(ins);
			this.fos = new FileOutputStream(partDir);
			this.fos.getChannel().transferFrom(rbc, 0,
					this.mPart.getSizeChunk());

			// ins.close();
			// rbc.close();
			this.fos.close();

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						BufferedInputStream bis = new BufferedInputStream(
								new FileInputStream(partDir));
						mRAF.seek(mPart.getBegin());

						Log.e("seek", Long.toString(mPart.getBegin()));
						while ((count = bis.read(data)) != -1) {
							mRAF.write(data);
						}

						bis.close();
						notify();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}).start();

		} catch (MalformedURLException e) { // url generated
			e.printStackTrace();
		} catch (IOException e) { // httpurlconnection
			e.printStackTrace();
		}
		return urlD[0];
	}

	@Override
	protected void onPostExecute(String file_url) {
		this.mPart.setState(EnumStateFile.DOWNLOADED);
		// boolean finish = true;
		// for (int i = 0; i < this.mPart.getFile().getParts().size(); i++) {
		// finish = finish
		// && (this.mPart.getFile().getParts().get(i).getState() ==
		// EnumStateFile.DOWNLOADED);
		// }
		//
		// if (finish) {
		// try {
		// this.mPart.getmFileDest().close();
		// FileOutputStream fos = new FileOutputStream(
		// this.mPart.getFile().path);
		// for (PartFile part : this.mPart.getFile().getParts()) {
		// String partDir = CustomActivity.DLMDIR.getPath()
		// + CustomActivity.CACHDIR + "/" + part.getId();
		// FileInputStream fis = new FileInputStream(partDir);
		//
		// byte content[] = new byte[1024 * 32];
		// int count = 0;
		// while ((count = fis.read(content)) != -1) {
		// fos.write(content, 0, count);
		// }
		// fis.close();
		// File f = new File(partDir);
		// f.delete();
		//
		// }
		// fos.close();
		//
		// Log.e("finis",
		// Integer.toString((int) System.currentTimeMillis()
		// - beginT));
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
		try {
			this.mRAF.close();
			Log.e("finis",
					Integer.toString((int) System.currentTimeMillis() - beginT));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
