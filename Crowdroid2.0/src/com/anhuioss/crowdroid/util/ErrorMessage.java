package com.anhuioss.crowdroid.util;

import android.content.Context;

import com.anhuioss.crowdroid.R;

public class ErrorMessage {

	public static final int ERROR_MESSAGE_SERVER = R.string.error_message_server;

	public static final int ERROR_MESSAGE_BADGATEWAY = R.string.error_message_badgateway;

	public static final int ERROR_MESSAGE_SERVICE_UNAVAILABLE = R.string.error_message_service_unavailable;

	public static final int ERROR_MESSAGE = R.string.error_message;

	public static final int ERROR_MESSAGE_INPUT = R.string.error_message_input;

	public static final int ERROR = R.string.error;

	// -----------------------------------------------------------------------------
	/**
	 * Get an error message by status code.
	 * 
	 * @param context
	 * @param statusCodeString
	 *            the string for status code
	 * @return error message
	 */
	// -----------------------------------------------------------------------------
	public static String getErrorMessage(Context context,
			String statusCodeString) {

		if (statusCodeString == null) {
			return context.getString(ERROR);
		}

		int statusCode = -1;
		try {

			statusCode = Integer.valueOf(statusCodeString);
			switch (statusCode) {

			case 400:
				return context.getString(R.string.error_msg_400);
			case 401:
				return context.getString(R.string.error_msg_401);
			case 403:
				return context.getString(R.string.error_msg_403);
			case 404:
				return context.getString(R.string.error_msg_404);
			case 500:
				return context.getString(R.string.error_msg_500);
			case 501:
				return context.getString(R.string.error_msg_501);
			case 502:
				return context.getString(R.string.error_msg_502);
			case 503:
				return context.getString(R.string.error_msg_503);
			case 504:
				return context.getString(R.string.error_msg_504);
			case 505:
				return context.getString(R.string.error_msg_505);
			default:
				return context.getString(ERROR);
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
			return context.getString(ERROR);
		}

	}

}
