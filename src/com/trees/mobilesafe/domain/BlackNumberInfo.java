package com.trees.mobilesafe.domain;

public class BlackNumberInfo {

	public BlackNumberInfo(String number, String mode) {
		super();
		this.number = number;
		this.mode = mode;
	}
	public BlackNumberInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	String number;
	String mode;
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	@Override
	public String toString() {
		return "BlackNumberInfo [number=" + number + ", mode=" + mode + "]";
	}
	
}
