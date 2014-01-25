package com.example.model;

public enum EnumStateFile {
	READY(0),
	DOWNLOADING(1),
	PAUSED(2),
	DOWNLOADED(3);
	private int code;
	private EnumStateFile(int c){
		code = c;
	}
	public int getCode(){
		return code;
	}
}
