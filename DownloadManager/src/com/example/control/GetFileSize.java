package com.example.control;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.activities.CustomActivity;
import com.example.activities.MainActivity;
import com.example.model.EnumStateFile;
import com.example.model.MFile;
import com.example.model.PartFile;

public class GetFileSize extends AsyncTask<String, String, String> {
	public final static int PART = 1024 * 1024 / 2;
	public final static int PARTS_4 = 1024 * 1024 * 2;
	public final static List<String> IMAGE_TYPE = new ArrayList<String>() {
		{
			add("JPEG");
			add("PNG");
			add("JPG");
			add("GIF");
		}
	};

	public final static List<String> COMPRESSED = new ArrayList<String>() {
		{
			add("ZIP");
			add("RAR");
			add("7Z");
		}
	};

	public final static List<String> VIDEO_TYPE = new ArrayList<String>() {
		{
			add("MP4");
			add("3GP");
			add("TS");
			add("AAC");
			add("MKV");
		}
	};

	public final static List<String> MUSIC_TYPE = new ArrayList<String>() {
		{
			add("MP3");
			add("FLAC");
			add("MID");
			add("WAV");
		}
	};

	public final static List<String> DOC_TYPE = new ArrayList<String>() {
		{
			add("PDF");
			add("DOC");
			add("DOCX");
			add("XLS");
			add("XLXS");
		}
	};

	public static int beginT;

	private RandomAccessFile mOuts;
	private long fLength;
	private int fId; // id of thread
	private RandomAccessFile mFileDest;
	private MFile aFile;
	private Context mContext;
	private String mUrl;

	public GetFileSize(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	protected String doInBackground(String... urls) {
		// TODO Auto-generated method stub
		this.mUrl = urls[0];
		try {
			beginT = (int) System.currentTimeMillis();
			URL url = new URL(this.mUrl);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.connect();
			this.fLength = connection.getContentLength();
			Log.e("file size", Long.toString(this.fLength));
			connection.disconnect();
			if (this.fLength <= 0) {
				return null;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return urls[0];
	}

	@Override
	protected void onPostExecute(String file_url) {
		if (file_url != null) {
			aFile = new MFile(this.mUrl, fLength);
			String dir = setPathDownloadedFile(this.mUrl);
			aFile.path = dir;
			try {
				mFileDest = new RandomAccessFile(dir, "rw");
				mFileDest.setLength(this.fLength);
				aFile.path = dir;

				if (this.fLength <= PART) {// one part
					beginDownload(fLength);
				} else if (this.fLength <= PARTS_4) {// 4 parts

					long partSize = (this.fLength / 4) + 1;
					beginDownload(partSize);
				} else {// 8 parts
					long partSize = (this.fLength / 8) + 1;
					beginDownload(partSize);
				}
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

	}

	private void beginDownload(long chunkSize) {
		MainActivity.files.add(this.aFile);
		this.fId = MainActivity.files.size();
		long blockSize = chunkSize;
		long startPos = 0;
		int i = 0;

		while (startPos < this.fLength) {
			i++;
			int id = this.fId * 10 + i;
			PartFile aPart = new PartFile(aFile, id, startPos,
					EnumStateFile.READY, blockSize, this.mFileDest);
			aFile.getParts().add(aPart);

			IsThreadBlock status = new IsThreadBlock("<" + startPos + ", "
					+ (startPos + blockSize - 1) + ">");

			if (status.getIsBlocked()) {
				try {
					wait(1000);
					Log.e("wait", Long.toString(startPos));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
			}

			new DownloadFile(mContext, aPart, status).executeOnExecutor(
					THREAD_POOL_EXECUTOR, this.mUrl);

			startPos += blockSize;

			if (startPos + chunkSize > this.fLength) {
				blockSize = this.fLength - startPos;
			}
			if (blockSize <= 0)
				break;
		}

	}

	private String setPathDownloadedFile(String fileName) {
		String extension = fileName.substring(fileName.lastIndexOf(".") + 1)
				.toUpperCase();
		String type;
		String name = fileName.substring(fileName.lastIndexOf("/") + 1);
		if (COMPRESSED.contains(extension)) {
			type = CustomActivity.COMDIR;
		} else if (IMAGE_TYPE.contains(extension)) {
			type = CustomActivity.IMDIR;
		} else if (DOC_TYPE.contains(extension)) {
			type = CustomActivity.DOCDIR;
		} else if (MUSIC_TYPE.contains(extension)) {
			type = CustomActivity.MUDIR;
		} else if (VIDEO_TYPE.contains(extension)) {
			type = CustomActivity.VIDDIR;
		} else {
			type = CustomActivity.OTHERDIR;
		}
		return CustomActivity.DLMDIR.getPath() + type + "/" + name;
	}
}
