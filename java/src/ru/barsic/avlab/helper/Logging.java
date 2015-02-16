package ru.barsic.avlab.helper;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class Logging  {
	static final String LOG_TAG = "myLogs";


	private static final String FILE_NAME = "log.txt";
	private static final String AVLAB_LOG = "AVLabLog";
	private static String DESC = "Log ";
	private static String LINE_SEPARATOR = System.getProperty("line.separator");
	public static final String LAUNCH_SEPARATOR = "------------------------------------------------------------";


	private static DateFormat d = new SimpleDateFormat("dd.MM hh:mm:ss");
	private static BufferedWriter bw;
	private static File file;

	public static int type = 1;

	public static void log(String s){
		if(type > 0)
			System.out.println(DESC + d.format(new Date())+" : "+s);

		if (type < 2)
			writeFileSD(DESC + d.format(new Date())+" : "+s);
	}
	public  static  void log(Exception e, String s){
		log(e + " : " + s);
	}
	public  static  void log(String source, Object obj, String s){
		log(obj + " : " + s);

	}

	public static void closeStream() {
		if (bw != null) {
			try {
				bw.close();
				bw = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	static void writeFileSD(String s) {
		if (bw == null) {
			// проверяем доступность SD
			if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			{
				Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
				return;
			}
			try {
				if (file == null) {
					File sdPath = Environment.getExternalStorageDirectory();
					sdPath = new File(sdPath.getAbsolutePath() + "/" + AVLAB_LOG);
					sdPath.mkdirs();
					file = new File(sdPath, FILE_NAME);
					bw = new BufferedWriter(new FileWriter(file, true));
					bw.write(LAUNCH_SEPARATOR);
					bw.write(LINE_SEPARATOR);
				} else if (bw == null) {
					bw = new BufferedWriter(new FileWriter(file, true));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			bw.write(s);
			bw.write(LINE_SEPARATOR);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
