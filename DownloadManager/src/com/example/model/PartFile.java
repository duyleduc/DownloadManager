package com.example.model;

import java.io.RandomAccessFile;

public class PartFile {
	private MFile file;
	private int id;
	private long begin; // point begining download
	private EnumStateFile state;
	private long size;
	private RandomAccessFile mFileDest;
	

	public PartFile(MFile file, int id, long startPos, 
			EnumStateFile state, long blockSize, RandomAccessFile outs) {
		super();
		this.file = file;
		this.id = id;
		this.begin = startPos;
		this.state = state;
		
		this.size = blockSize;
		this.mFileDest = outs;
	}

	/**
	 * @return the file
	 */
	public MFile getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(MFile file) {
		this.file = file;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the begin
	 */
	public long getBegin() {
		return begin;
	}

	/**
	 * @param begin
	 *            the begin to set
	 */
	public void setBegin(int begin) {
		this.begin = begin;
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
	 * @return the length
	 */
	public long getSizeChunk() {
		return size;
	}

	/**
	 * @param length
	 *            the length to set
	 */
	public void setLength(int length) {
		this.size = length;
	}

	/**
	 * @return the mFileDest
	 */
	public RandomAccessFile getmFileDest() {
		return mFileDest;
	}

	/**
	 * @param mFileDest the mFileDest to set
	 */
	public void setmFileDest(RandomAccessFile mFileDest) {
		this.mFileDest = mFileDest;
	}


}
