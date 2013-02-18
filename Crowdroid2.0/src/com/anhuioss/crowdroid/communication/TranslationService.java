package com.anhuioss.crowdroid.communication;

import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import com.anhuioss.crowdroid.service.TranslationHandler;
import com.anhuioss.crowdroid.util.CommResult;

public class TranslationService extends Service {

	/** Handler */
	private Handler mHandler = new Handler();

	/** Translation Service Interface */
	private TranslationServiceInterface.Stub translationServiceInterface = new TranslationServiceInterface.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void request(String engine, long type,
				TranslationServiceListener listener, Map map)
				throws RemoteException {

			// HTTP Communication And Call Back
			connect(engine, type, listener, map);

		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return translationServiceInterface;
	}

	// -----------------------------------------------------------------------------
	/**
	 * HTTP Communication
	 */
	// -----------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private void connect(final String engine, final long type,
			final TranslationServiceListener listener, final Map map) {

		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {

				// Get Communication Handler And Request For Result
				TranslationHandler translationHandler = new TranslationHandler();
				CommResult result = translationHandler.request(engine, type,
						map);
				final String statusCode = result.getResponseCode();
				final String message = (String) result.getMessage();

				// Call Back
				mHandler.post(new Runnable() {
					public void run() {
						try {

							String messageId = (String) map.get("message_id");

							listener.requestCompleted(engine,
									messageId != null ? Long.valueOf(messageId)
											: type, statusCode, message);
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
