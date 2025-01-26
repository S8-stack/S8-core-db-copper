package com.s8.core.db.copper.io;

import java.nio.file.Path;

import com.s8.core.arch.titanium.db.TiPathComposer;


/**
 * 
 * @author pierreconvert
 *
 */
public class PathComposer implements TiPathComposer {

	public final static int FOLDER_DEPTH = 4;

	
	private final Path root;
	
	public PathComposer(Path root) {
		this.root = root;
	}
	
	
	private static char map(char c) {
		/** digits and letters */
		if((c >= 48 && c < 58) || (c >= 65 && c <91) || (c >= 97 && c <123)) {
			return c;
		}
		else if (c == 45) { // -
			return '-';
		}
		else if (c == 32) { // ' ' space
			return '_';
		}
		else if (c == 47) { // /
			return '-';
		}
		else if((c >= 32 && c < 48)) { // common special chars
			return '-';
		}
		else {
			return '-';
		}
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static long computeHashcode(String value) {
		int n = value.length();
		long hashcode = 0L;
		for(int index = n-1; index >= 0; index--) {
			hashcode = 31 * hashcode + map(value.charAt(index));
		}
		return hashcode;
	}

	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static String flattenName(String value) {
		int n = value.length();
		StringBuilder builder = new StringBuilder();
		for(int index = 0; index < n; index++) {
			builder.append(map(value.charAt(index)));	
		}
		return builder.toString();
	}
	
	
	@Override
	public Path composePath(String value) {
		StringBuilder builder = new StringBuilder();
		long hashcode = computeHashcode(value);
		
		int shift = 0;
		for(int depth=0; depth < FOLDER_DEPTH; depth++) {
			int octet = (int) ((hashcode >> shift) & 0xffL);
			shift += 8;
		
			builder.append('n');
			builder.append(String.format("%02x", octet & 0xff));
			builder.append('/');
		}
		
		builder.append('/');
		builder.append(flattenName(value));
		
		String pathname = builder.toString();
		
		// root resolver
		return root.resolve(pathname);
		
	}

}
