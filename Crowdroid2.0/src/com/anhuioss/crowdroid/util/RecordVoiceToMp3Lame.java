package com.anhuioss.crowdroid.util;

public class RecordVoiceToMp3Lame {

	public static void init(int inSamplerate, int outChannel,
			int outSamplerate, int outBitrate) {
		init(inSamplerate, outChannel, outSamplerate, outBitrate, 7);
	}

	public native static void init(int inSamplerate, int outChannel,
			int outSamplerate, int outBitrate, int quality);

	public native static int encode(short[] buffer_l, short[] buffer_r,
			int samples, byte[] mp3buf);

	public native static int flush(byte[] mp3buf);

	public native static void close();

}
