package com.anhuioss.crowdroid.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

public class FileService {
	private Context context;

	public FileService(Context context) {
		super();
		this.context = context;
	}

	/**
	 * 写入文件到SD卡
	 * 
	 * @throws IOException
	 */
	public void saveToSD(String fileNameStr, String fileContentStr)
			throws IOException {

		// 备注:Java File类能够创建文件或者文件夹，但是不能两个一起创建
		File file = new File(Constants.DEFAULT_SAVE_FILE_PATH);
		// if(!file.exists())
		// {
		// file.mkdirs();
		// }
		// File sd=Environment.getExternalStorageDirectory();
		// String path=sd.getPath()+"/myfiles";
		// File file=new File(path);
		// if(file.exists()){
		// file.delete();
		// }
		if (!file.exists()) {
			file.mkdir();
		}
		File file1 = new File(file, fileNameStr);
		FileOutputStream fos = new FileOutputStream(file1);
		fos.write(fileContentStr.getBytes());
		fos.close();
	}

	/**
	 * 保存文件到手机
	 * 
	 * @param fileNameStr
	 *            文件名
	 * @param fileContentStr
	 *            文件内容
	 * @throws IOException
	 */
	public void save(String fileNameStr, String fileContentStr)
			throws IOException {
		// 私有操作模式：创建出来的文件只能被本应用访问，其它应用无法访问该文件，另外采用私有操作模式创建的文件，写入文件中的内容会覆盖原文件的内容
		FileOutputStream fos = context.openFileOutput(fileNameStr,
				context.MODE_PRIVATE);
		fos.write(fileContentStr.getBytes());
		fos.close();
	}

	public void saveAppend(String fileNameStr, String fileContentStr)
			throws IOException {
		// 追加操作模式:不覆盖源文件，但是同样其它应用无法访问该文件
		FileOutputStream fos = context.openFileOutput(fileNameStr,
				context.MODE_APPEND);
		fos.write(fileContentStr.getBytes());
		fos.close();
	}

	public void saveReadable(String fileNameStr, String fileContentStr)
			throws IOException {
		// 读取操作模式:可以被其它应用读取，但不能写入
		FileOutputStream fos = context.openFileOutput(fileNameStr,
				context.MODE_WORLD_READABLE);
		fos.write(fileContentStr.getBytes());
		fos.close();
	}

	public void saveWriteable(String fileNameStr, String fileContentStr)
			throws IOException {
		// 写入操作模式:可以被其它应用写入，但不能读取
		FileOutputStream fos = context.openFileOutput(fileNameStr,
				context.MODE_WORLD_WRITEABLE);
		fos.write(fileContentStr.getBytes());
		fos.close();
	}

	public void saveReadWriteable(String fileNameStr, String fileContentStr)
			throws IOException {
		// 读写操作模式:可以被其它应用读写
		FileOutputStream fos = context.openFileOutput(fileNameStr,
				context.MODE_WORLD_READABLE + context.MODE_WORLD_WRITEABLE);
		fos.write(fileContentStr.getBytes());
		fos.close();
	}

	/**
	 * 读取文件内容
	 * 
	 * @param fileNamestr
	 *            文件名
	 * @return
	 * @throws IOException
	 */
	public String read(String fileNamestr) throws IOException {
		FileInputStream fis = context.openFileInput(fileNamestr);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = fis.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		byte[] data = bos.toByteArray();

		return new String(data);
	}
}
