package com.anhuioss.crowdroid.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageBuilder {

	// ---------------------------------------------------------------------
	/**
	 * Get BitMap from URL
	 * 
	 * @throws IOException
	 */
	// ---------------------------------------------------------------------
	public static Bitmap returnBitMap(String url) throws IOException {

			// bitmap
			Bitmap bitmap = null;
			int size = 0;
			//bitmap array
			byte[] result = null;
			//read the stream to byte array
			byte[] line = new byte[1024];
	
			HttpURLConnection conn;
			InputStream is;
	
			// Connect
			URL distination = new URL(url);
			conn = (HttpURLConnection) distination.openConnection();
			conn.setDoInput(true);
			conn.connect();
	
			// Get Data
			is = conn.getInputStream();
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	
			while (true) {
				size = is.read(line);
				if (size <= 0) {
					break;
				}
				bos.write(line, 0, size);
			}
	
			result = bos.toByteArray();
	
			try {
				bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
			} catch (OutOfMemoryError e) {}
	
			// Close
			bos.close();
			is.close();
			conn.disconnect();
	
			return bitmap;
			
	}
}
