package com.trees.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * 
 * 
 */
public class MD5Utils {

	public static String encoder(String passwd){
		// ��Ϣժȡ��
		MessageDigest digest;
		StringBuffer buffer;		
			try {
				digest = MessageDigest.getInstance("md5");
//			2.���byte����
				byte bytes[] = digest.digest(passwd.getBytes());
//			3.ÿһ��byte�� 8��������λ��������
				buffer = new StringBuffer();
				for(byte b : bytes){
					int number = b & 0xff;
				
//				4.��int����ת����ʮ������
					String numberstr = Integer.toHexString(number);
				
//				5.����λ�Ĳ�ȫ
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
