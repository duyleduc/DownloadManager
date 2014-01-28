package com.example.control;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.example.activities.Downloading;
import com.example.activities.MainDownloadManager;
import com.example.model.EnumStateFile;
import com.example.model.MFile;
import com.example.model.PartFile;

public class GetFileSize extends AsyncTask<String, String, String> {

	public final static int PART = 1024 * 1024 / 2;
	public final static int PARTS_4 = 1024 * 1024 * 2;
	public final static List<String> IMAGE_TYPE = new ArrayList<String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			add("JPEG");
			add("PNG");
			add("JPG");
			add("GIF");
		}
	};

	public final static List<String> COMPRESSED = new ArrayList<String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			add("ZIP");
			add("RAR");
			add("7Z");
		}
	};

	public final static List<String> VIDEO_TYPE = new ArrayList<String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			add("MP4");
			add("3GP");
			add("TS");
			add("AAC");
			add("MKV");
		}
	};

	public final static List<String> MUSIC_TYPE = new ArrayList<String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			add("MP3");
			add("FLAC");
			add("MID");
			add("WAV");
		}
	};

	public final static List<String> DOC_TYPE = new ArrayList<String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			add("PDF");
			add("DOC");
			add("DOCX");
			add("XLS");
			add("XLXS");
		}
	};

	private long fLength;
	private int fId; // id of thread
	private RandomAccessFile mFileDest;
	private MFile aFile;
	private Context mContext;
	private String mUrl;

	public GetFileSize(Context mContext, MFile url) {
		this.mContext = mContext;
		this.aFile = url;
		this.mUrl = this.aFile.getfUrl();
	}

	@Override
	protected String doInBackground(String... urls) {
		// TODO Auto-generated method stub
		this.fLength = aFile.getfSize();
		String dir = setPathDownloadedFile(this.mUrl);
		aFile.path = dir;
		chooseNbParts(fLength);
		return urls[0];
	}

	public void beginDownload(long chunkSize) {

		Downloading.mAdapterD.notifyDataSetChanged();

		this.fId = MainDownloadManager.files.size();
		long blockSize = chunkSize;
		long startPos = 0;
		int i = 0;
		String name = this.mUrl.substring(this.mUrl.lastIndexOf("/") + 1,
				this.mUrl.lastIndexOf("."));
		while (startPos < this.fLength) {
			i++;
			int id = this.fId * 10 + i;
			PartFile aPart = new PartFile(aFile, name + id, startPos,
					EnumStateFile.READY, blockSize, this.mFileDest);
			aFile.getParts().add(aPart);

			IsThreadBlock status = new IsThreadBlock("<" + startPos + ", "
					+ (startPos + blockSize - 1) + ">");

			if (status.getIsBlocked()) {
				try {
					wait(1000);
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

	public void chooseNbParts(long length) {
		if (length <= PART) {// one part
			beginDownload(length);
		} else if (length <= PARTS_4) {// 4 parts

			long partSize = (length / 4) + 1;
			beginDownload(partSize);
		} else {// 8 parts
			long partSize = (length / 6) + 1;
			beginDownload(partSize);
		}
	}

	private String setPathDownloadedFile(String fileName) {
		String extension = fileName.substring(fileName.lastIndexOf(".") + 1)
				.toUpperCase();
		String type;

		String name = fileName.substring(fileName.lastIndexOf("/") + 1);
		if (COMPRESSED.contains(extension)) {
			type = MainDownloadManager.COMDIR;
		} else if (IMAGE_TYPE.contains(extension)) {
			type = MainDownloadManager.IMDIR;
		} else if (DOC_TYPE.contains(extension)) {
			type = MainDownloadManager.DOCDIR;
		} else if (MUSIC_TYPE.contains(extension)) {
			type = MainDownloadManager.MUDIR;
		} else if (VIDEO_TYPE.contains(extension)) {
			type = MainDownloadManager.VIDDIR;
		} else {
			type = MainDownloadManager.OTHERDIR;
		}
		return MainDownloadManager.DLMDIR.getPath() + type + "/" + name;
	}

}
