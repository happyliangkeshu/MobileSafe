package com.trees.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * 
 * 
 */
public class MD5Utils {

	public static String encoder(String passwd){
		// 信息摘取器
		MessageDigest digest;
		StringBuffer buffer;		
			try {
				digest = MessageDigest.getInstance("md5");
//			2.编程byte数组
				byte bytes[] = digest.digest(passwd.getBytes());
//			3.每一个byte个 8个二进制位做与运算
				buffer = new StringBuffer();
				for(byte b : bytes){
					int number = b & 0xff;
				
//				4.把int类型转换成十六进制
					String numberstr = Integer.toHexString(number);
				
//				5.不足位的补全
					if(numberstr.length() == 1){
						buffer.append("0");
					}
					buffer.append(numberstr);
				}		
				return buffer.toString();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return "";
			}	
	
		
	}
}
