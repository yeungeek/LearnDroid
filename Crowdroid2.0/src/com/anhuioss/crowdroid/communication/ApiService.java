package com.anhuioss.crowdroid.communication;

import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.util.CommResult;

public class ApiService extends Service {

	/** Handler */
	private Handler mHandler = new Handler();

	/** Api Service Interface */
	private ApiServiceInterface.Stub apiServiceInterface = new ApiServiceInterface.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void request(String service, int type,
				ApiServiceListener listener, Map map) throws RemoteException {

			// HTTP Communication And Call Back
			connect(service, type, listener, map);

		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return apiServiceInterface;
	}

	// -----------------------------------------------------------------------------
	/**
	 * HTTP Communication
	 */
	// -----------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
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

}
