package ishi_daishi.jp.ne.biglobe.www7b.apollonseye.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import ishi_daishi.jp.ne.biglobe.www7b.apollonseye.MainActivity;
import ishi_daishi.jp.ne.biglobe.www7b.apollonseye.R;
import ishi_daishi.jp.ne.biglobe.www7b.apollonseye.SettingFragment;
import ishi_daishi.jp.ne.biglobe.www7b.apollonseye.control.Csv;
import ishi_daishi.jp.ne.biglobe.www7b.apollonseye.control.DataProvider;

public class PeriodicService extends Service{
	private static final String TAG = PeriodicService.class.getCanonicalName();
	private static final boolean DEBUG = true;

	/**
	 * ブロードキャスト用の公開ACTION
	 */
	// センサー値の更新
	public static final String ACTION_DATA_UPDATE = TAG + ".ACTION_DATA_UPDATE";
	// センサー値のキー
	public static final String EXTRA_STRING_VALUES = TAG + ".EXTRA_STRING_VALUES";

	/**
	 * フィールド
	 */
	// 定期処理を行うクラス
	private PeriodicCounter counter = null;
	// CSVから読み出すデータを格納
	private DataProvider csvData = null;
	// File Download Task
	private FtpFileDownLoadTask mTask1 = null;
	private FtpFileDownLoadTask mTask2 = null;
	private String mDownloadFilePath1 = null;
	private String mDownloadFilePath2 = null;
	
	// レシーバ
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			PeriodicService.this.receivedIntent(intent);
		}
	};
	

	@Override
	public IBinder onBind(Intent intent) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// get action
		String action = intent.getAction();
		
		// 更新間隔の読み込み
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String key = getString(R.string.key_interval_time);
		String value = sp.getString(key, "300");
		long period = 300;
		try {
			period = Long.parseLong(value);
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		}

		// 定期処理クラスの生成
		counter = new PeriodicCounter(period, true);
		
		// DEMO Mode
		if(action.equals(MainActivity.ACTION_APPLI_MODE_DEMO)) {
//			Log.v("debug:PeriodicService", "DEMO start");

			// CSVファイルから読み込み準備
			try {
				AssetManager as = getResources().getAssets();
				InputStream is = as.open("ace_swepam_1m.txt");
				InputStream is2 = as.open("Gp_xr_1m.txt");
				csvData = new DataProvider(Csv.readInputStream(is), Csv.readInputStream(is2));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// 定期処理開始
			if(counter != null) {
				counter.execute();
			}
			
		// Network Mode
		} else if(action.equals(MainActivity.ACTION_APPLI_MODE_NETWORK)) {
//			Log.v("debug:PeriodicService", "NETWORK start");

			// File Download Start
			// [Download]ace_swepam_1m.txt 
			if(mTask1 == null || mTask1.getStatus() != AsyncTask.Status.RUNNING) {
				mTask1 = new FtpFileDownLoadTask(getApplicationContext());
				mTask1.execute(
						"ftp.swpc.noaa.gov",		// 00:host
						"21",						// 01:port
						"anonymous",				// 02:user
						"eannahei@hq.nasa.gov",		// 03:pasword(email)
						"pub/lists/ace",			// 04:path
						"ace_swepam_1m.txt",		// 05:file name
						"false");					// 06:Passive(true)/Active(false)
			}
			// [Download]Gp_xr_1m.txt
			if(mTask2 == null || mTask2.getStatus() != AsyncTask.Status.RUNNING) {
				mTask2 = new FtpFileDownLoadTask(getApplicationContext());
				mTask2.execute(
						"ftp.swpc.noaa.gov",		// 00:host
						"21",						// 01:port
						"anonymous",				// 02:user
						"eannahei@hq.nasa.gov",		// 03:pasword(email)
						"pub/lists/xray",			// 04:path
						"Gp_xr_1m.txt",				// 05:file name
						"false");					// 06:Passive(true)/Active(false)
			}
			
			// レシーバの登録
			IntentFilter filter = new IntentFilter();
//			filter.addAction(SettingFragment.ACTION_FILE_DOWNLOAD_START);			// File Download Start
			filter.addAction(FtpFileDownLoadTask.ACTION_FILE_DOWNLOAD_COMPLETE);	// File Download Complete
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiver, filter);
			
		}
		
//		return START_STICKY;
		return START_NOT_STICKY;	// not reboot
	}


	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
		
		// レシーバの解除
		LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mReceiver);
		
		// File Download Task Cancel
		if(mTask1 != null) {
			mTask1.cancel(true);
			mDownloadFilePath1 = null;
		}
		if(mTask2 != null) {
			mTask2.cancel(true);
			mDownloadFilePath2 = null;
		}
		
		// 定期処理停止
		if(counter != null) {
			counter.cancel();
		}
	}
	
	/**
	 * receivedIntent
	 * @param intent
	 */
	private void receivedIntent(Intent intent) {
		String action = intent.getAction();
		
		// File Download Start
		if(action.equals(SettingFragment.ACTION_FILE_DOWNLOAD_START)) {
			// [Download]ace_swepam_1m.txt 
//			if(mTask1 == null || mTask1.getStatus() != AsyncTask.Status.RUNNING) {
//				mTask1 = new FtpFileDownLoadTask(getApplicationContext());
//				mTask1.execute(
//						"ftp.swpc.noaa.gov",		// 00:host
//						"21",						// 01:port
//						"anonymous",				// 02:user
//						"eannahei@hq.nasa.gov",		// 03:pasword(email)
//						"pub/lists/ace",			// 04:path
//						"ace_swepam_1m.txt",		// 05:file name
//						"false");					// 06:Passive(true)/Active(false)
//			}
			// [Download]Gp_xr_1m.txt
//			if(mTask2 == null || mTask2.getStatus() != AsyncTask.Status.RUNNING) {
//				mTask2 = new FtpFileDownLoadTask(getApplicationContext());
//				mTask2.execute(
//						"ftp.swpc.noaa.gov",		// 00:host
//						"21",						// 01:port
//						"anonymous",				// 02:user
//						"eannahei@hq.nasa.gov",		// 03:pasword(email)
//						"pub/lists/xray",			// 04:path
//						"Gp_xr_1m.txt",				// 05:file name
//						"false");					// 06:Passive(true)/Active(false)
//			}
		}
		// File Download Complete
		else if(action.equals(FtpFileDownLoadTask.ACTION_FILE_DOWNLOAD_COMPLETE)) {
			// get file path
			String path = intent.getStringExtra(FtpFileDownLoadTask.EXTRA_FILE_PATH);
			if(DEBUG) {
				Log.d(TAG, "path : " + path);
			}
			
			// download complete: ace_swepam_1m.txt
			if(mDownloadFilePath1 == null && path.contains("ace_swepam_1m.txt")) {
				mDownloadFilePath1 = path;
			
			// download complete: Gp_xr_1m.txt
			} else if(mDownloadFilePath2 == null && path.contains("Gp_xr_1m.txt")) {
				mDownloadFilePath2 = path;
			}
			
			// counter start
			if(mDownloadFilePath1 != null && mDownloadFilePath2 != null) {
				InputStream is1 = null;
				InputStream is2 = null;
				
				// ace_swepam_1m.txt
				try {
					is1 = new FileInputStream(mDownloadFilePath1);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
				// Gp_xr_1m.txt
				try {
					is2 = new FileInputStream(mDownloadFilePath2);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
				csvData = new DataProvider(Csv.readInputStream(is1), Csv.readInputStream(is2));

				// 定期処理開始
				if(counter != null) {
					counter.execute();
				}
			}
		}
	}

	/**
	 * 周期的にカウントを進めるクラス
	 *
	 */
	private class PeriodicCounter extends AbstractPeriodicTask {

		public PeriodicCounter(long period, boolean isDaemon) {
			super(period, isDaemon);
		}

		@Override
		protected void invokersMethod() {

			// CSVファイルからのデータ読み込み
			// ※ここでサーバーからのでーた読み込みなどを切り替える
			String[] values = readCsvData();
			if (values == null) {
				return;
			}

			Intent intent = new Intent();
			intent.setAction(ACTION_DATA_UPDATE);
			intent.putExtra(EXTRA_STRING_VALUES, values);
			LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
		}

		/**
		 * CSVファイルからの読み込み
		 * @return
		 */
		private String[] readCsvData() {

			// 定期的な処理
			String[] values = csvData.get();

			if(values == null) {
				Log.e(TAG,"data is null.");
				return null;
			}
			if(values.length == 0) {
				Log.e(TAG,"length is 0.");
				return null;
			}

			// Null Check
			for(int i = 0; i < values.length; i++) {
				if(values[i] == null) {
					Log.e(TAG,"valies[" + i + "] is null.");
					values[i] = "0";
				}
				if(values[i].equals("")) {
					Log.e(TAG,"valies[" + i + "] is blank.");
					values[i] = "0";
				}
			}

			return values;
		}
	}
}
