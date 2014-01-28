package com.example.model;

import java.util.ArrayList;
import java.util.List;

import android.widget.ProgressBar;
import android.widget.TextView;

public class MFile {

	private EnumStateFile state;
	private String fUrl;
	private long fSize;
	private List<PartFile> parts;
	public String path;
	private long downloadedLenght;

	private String percentDownloaded;

	public MFile(String fUrl, long fLength) {
		super();
		state = EnumStateFile.DOWNLOADING;
		this.fUrl = fUrl;
		this.fSize = fLength;
		this.parts = new ArrayList<PartFile>();
		this.downloadedLenght = 0;
		this.percentDownloaded = "0%";
	}

	public MFile() {
		// TODO Auto-generated constructor stub
		this.parts = new ArrayList<PartFile>();
	}

	/**
	 * @return the state
	 */
	public EnumStateFile getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(EnumStateFile state) {
		this.state = state;
	}

	/**
	 * @return the fUrl
	 */
	public String getfUrl() {
		return fUrl;
	}

	/**
	 * @param fUrl
	 *            the fUrl to set
	 */
	public void setfUrl(String fUrl) {
		this.fUrl = fUrl;
	}

	/**
	 * @return the fSize
	 */
	public long getfSize() {
		return fSize;
	}

	/**
	 * @param fSize
	 *            the fSize to set
	 */
	public void setfSize(long fSize) {
		this.fSize = fSize;
	}

	/**
	 * @return the parts
	 */
	public List<PartFile> getParts() {
		return parts;
	}

	/**
	 * @param parts
	 *            the parts to set
	 */
	public void setParts(List<PartFile> parts) {
		this.parts = parts;
	}

	public long getDownloadedLenght() {
		return downloadedLenght;
	}

	public void setDownloadedLenght(long downloadedLenght) {
		this.downloadedLenght = downloadedLenght;
	}

	/**
	 * @return the percentDownloaded
	 */
	public String getPercentDownloaded() {
		return percentDownloaded;
	}

	/**
	 * @param percentDownloaded
	 *            the percentDownloaded to set
	 */
	public void setPercentDownloaded(String percentDownloaded) {
		this.percentDownloaded = percentDownloaded;
	}

}
