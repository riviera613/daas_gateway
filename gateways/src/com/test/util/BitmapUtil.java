package com.test.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Used to compress and decompress a bitmap
 * Normal format:		like "111110001111000", '1' means open and '0' means stop
 * Compressed format:	"111110001111000" -> "1-5-3-4-3", "000001110000111" -> "0-5-3-4-3"
 * @author Riviera
 *
 */
public class BitmapUtil {
	
	private BitmapUtil() {
		
	}
	
	/**
	 * compress a bitmap
	 * for example: from "111110001111000" to "1-5-3-4-3", first num means start bit(0 or 1)
	 * @param bitmap
	 * @return compressed bitmap or ""(for illegal input)
	 */
	public static String compress(String bitmap) {
		if(bitmap == null || bitmap.length() == 0)
			return "";
		
		StringBuilder sb = new StringBuilder();
		boolean flag = (bitmap.charAt(0) == '1');
		sb.append(flag ? '1' : '0');
		int count = 1;
		for(int i = 1; i <= bitmap.length(); i++) {
			if(i == bitmap.length() || (flag && bitmap.charAt(i) == '0') || (!flag && bitmap.charAt(i) == '1')) {
				sb.append('-');
				sb.append(count);
				flag = !flag;
				count = 1;
			} else if(bitmap.charAt(i) == '0' || bitmap.charAt(i) == '1') {
				count++;
			} else {
				return "";
			}
		}
		return sb.toString();
	}
	
	/**
	 * decompress a compressed bitmap
	 * uncompleted method
	 * @param compressedBitmap
	 * @return bitmap or ""(for illegal input)
	 */
	public static String decompress(String compressedBitmap) {
		if(compressedBitmap == null || compressedBitmap.length() == 0)
			return "";
		
		String[] info = compressedBitmap.split("-");
		StringBuilder sb = new StringBuilder();
		boolean flag;
		if(info[0].equals("0"))
			flag = false;
		else if(info[0].equals("1"))
			flag = true;
		else
			return "";
		
		for(int i = 1; i < info.length; i++) {
			int count = Integer.valueOf(info[i]);
			char ch = flag ? '1' : '0';
			for(int j = 0; j < count; j++)
				sb.append(ch);
			flag = !flag;
		}
		return sb.toString();
	}
	
	/**
	 * compare two bitmap and return their difference
	 * @param remote
	 * @param local
	 * @return a json, include open, stop and new
	 */
	public static Map<String, List<Integer>> compare(String remote, String local) {
		if(remote.length() > local.length())
			return null;
		Map<String, List<Integer>> result = new TreeMap<>();
		result.put("open", new ArrayList<Integer>());
		result.put("stop", new ArrayList<Integer>());
		result.put("new", new ArrayList<Integer>());
		int i = 0;
		for(i = 0; i < remote.length(); i++) {
			boolean r = (remote.charAt(i) == '1');
			boolean l = (local.charAt(i) == '1');
			if(r && !l)
				result.get("stop").add(i);
			else if(!r && l)
				result.get("open").add(i);
		}
		for(; i < local.length(); i++)
			result.get("new").add(i);
		return result;
	}
}
