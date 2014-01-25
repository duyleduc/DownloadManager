package com.example.model;

import java.util.ArrayList;
import java.util.List;

public class MFile {

	private EnumStateFile state;
	private String fUrl;
	private long fSize;
	private List<PartFile> parts;
	
	public String path;

	public MFile(String fUrl, long fLength) {
		super();
		state = EnumStateFile.DOWNLOADING;
		this.fUrl = fUrl;
		this.fSize = fLength;
		this.parts = new ArrayList<PartFile>();
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
	public void setfSize(int fSize) {
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

}
