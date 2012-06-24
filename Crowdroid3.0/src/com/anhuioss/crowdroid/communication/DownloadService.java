package com.anhuioss.crowdroid.communication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.util.CommResult;

public class DownloadService extends Service {
	
	private Handler mHandler = new Handler();

	DownloadServiceInterface.Stub downloadServiceInterface = new DownloadServiceInterface.Stub() {

		@Override
		public void request(String uid, String url,
				DownloadServiceListener listener) throws RemoteException {

			// HTTP Communication
			download(uid, url, listener);
			
		}

	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return downloadServiceInterface;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	private void connect(final String service, final int type,
			final ApiServiceListener listener, final Map map) {

		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {

				// Get Communication Handler And Request For Result
				CommHandler commHandler = new CommHandler();
				CommResult result = commHandler.request(service, type, map);
				final String statusCode = result.getResponseCode();
				final String message = (String) result.getMessage();

				// Call Back
				mHandler.post(new Runnable() {
					public void run() {
						try {
							listener.requestCompleted(service, type,
									statusCode, message);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				});

			}
		}, "connection");

		// Start Thread
		th.start();

	}
	
	private void download(final String uid, final String url, final DownloadServiceListener listener) {
		
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {

				HttpURLConnection conn;
				InputStream is;

				try {
					
					URL downloadURL = new URL(url);
					conn = (HttpURLConnection) downloadURL.openConnection();
					conn.setDoInput(true);
					conn.connect();
					is = conn.getInputStream();
					final byte[] message = InputStreamToByteArray(is);
					final String statusCode = String.valueOf(conn.getResponseCode());

					// Call Back
					mHandler.post(new Runnable() {
						public void run() {
							try {
								listener.requestCompleted(uid, statusCode, message);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
					});
				
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}, "download");

		// Start Thread
		thread.start();
		
	}
	
	@SuppressWarnings("finally")
	private byte[] InputStreamToByteArray(InputStream is) {
		
		System.gc();
		
		byte[] value = null;
		int size;
		byte[] line = new byte[1024];
		
		try {

			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			while (true) {
				size = is.read(line);
				if (size <= 0) {
					break;
				}
				bos.write(line, 0, size);
			}

			value = bos.toByteArray();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			System.gc();
			
			return value;
			
		}

	}

}
