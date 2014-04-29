package ishi_daishi.jp.ne.biglobe.www7b.apollonseye.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * FtpFileDownLoadTask
 *
 * [NASA data]http://itcd.hq.nasa.gov/networking-ftp.html
 * [FTP Engine]http://commons.apache.org/proper/commons-net/
 * [permission]
 * 	android.permission.INTERNET
 * 	android.permission.WRITE_EXTERNAL_STORAGE
 * @author Team invader
 *
 */
public class FtpFileDownLoadTask extends AsyncTask<String, Integer, String>{
	private static final String TAG = FtpFileDownLoadTask.class.getCanonicalName();
	private static final boolean DEBUG = true;

	/**
	 * public field
	 */
	public static final String ACTION_FILE_DOWNLOAD_COMPLETE = TAG + ".ACTION_FILE_DOWNLOAD_COMPLETE";
	public static final String EXTRA_FILE_PATH = TAG + ".EXTRA_FILE_PATH";
	
	/**
	 * private field
	 */
	private Context mContext = null;
	private FTPClient mFtpClient = null;
	private boolean isLogin = false;
	private FileOutputStream mOutput = null;

	/**
	 * Constructor
	 * @param context
	 */
	public FtpFileDownLoadTask(Context context) {
		this.mContext = context;
	}

	@Override
	protected void onPreExecute() {
		if(DEBUG) {
			Log.d(TAG, "onPreExecute()");
		}
	}

	@Override
	protected String doInBackground(String... params) {
		if(DEBUG) {
			Log.d(TAG, "doInBackground() params length:" + params.length);
			for(String param : params) {
				Log.d(TAG, "param:" + param);
			}
		}
		String ret = null;	// return value

		/**
		 * Params
		 */
		final String host = params[0];
		final int port = Integer.valueOf(params[1]);
		final String user = params[2];
		final String password = params[3];
		final String path = params[4];
		final String filename = params[5];
		final boolean passive = Boolean.valueOf(params[6]);

		/**
		 * File Output
		 */
		final File sdcard = Environment.getExternalStorageDirectory();
		StringBuilder builder = new StringBuilder(0);

		// SD card
		if(sdcard.exists() && sdcard.canWrite()) {
			builder.append(sdcard.getAbsolutePath());
			builder.append(File.separator);
			builder.append(mContext.getPackageName());

			// dir check
			File dir = new File(builder.toString());
			if(!dir.exists()) {
				dir.mkdir();
			}

			// File Output Stream
			builder.setLength(0);	// clear
			builder.append(dir.getPath());
			builder.append(File.separator);
			builder.append(filename);
			ret = builder.toString();
			if(DEBUG) {
				Log.d(TAG, "sdcard output:" + ret);
			}

			try {
				mOutput = new FileOutputStream(builder.toString(), false);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		// Appli Area
		} else {
			builder.append(mContext.getFilesDir().toString());
			builder.append(File.separator);
			builder.append(filename);
			ret = builder.toString();
			if(DEBUG) {
				Log.d(TAG, "Appli Area output:" + ret);
			}

			// File Output Stream
			try {
				mOutput = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		/**
		 * FTP
		 */
		mFtpClient = new FTPClient();
		int replyCode = 0;

		try {
			// connect
			// TODO setDefaultTimeout(nTime)
			mFtpClient.connect(host, port);
			replyCode = mFtpClient.getReplyCode();
			if(!FTPReply.isPositiveCompletion(replyCode)) {
				throw new Exception("connect result:" + replyCode);
			}

			// login
			// TODO setSoTimeout(nTime)
			isLogin = mFtpClient.login(user, password);
			if(!isLogin) {
				throw new Exception("invalied user/password");
			}

			// file list get
			String[] listnames = mFtpClient.listNames();
			replyCode = mFtpClient.getReplyCode();
			if(!FTPReply.isPositiveCompletion(replyCode)) {
				throw new Exception("listNames() failed?:" + replyCode);
			}

			// change dir
			String[] tokens = path.split("/");
			for(String token : tokens) {
				boolean isExist = false;

				// dir search
				for(String name : listnames) {
					if(name.equals(token)) {
						isExist = true;
						break;
					}
				}

				if(isExist) {
					if(!mFtpClient.changeWorkingDirectory(token)) {
						throw new Exception("change dir failed:" + token);
					}

					// file list update
					listnames = mFtpClient.listNames();
					replyCode = mFtpClient.getReplyCode();
					if(!FTPReply.isPositiveCompletion(replyCode)) {
						throw new Exception("listNames() failed?:" + replyCode);
					}
				} else {
					throw new Exception("No such dir:" + token);
				}
			}

			// Passive or Active
			if(passive) {
				mFtpClient.enterLocalPassiveMode();	// Passive
			} else {
				mFtpClient.enterLocalActiveMode();	// Active
			}

			// File Download
			// TODO setDataTimeout(nTime)
			if(!mFtpClient.retrieveFile(filename, mOutput)) {
				replyCode = mFtpClient.getReplyCode();
				throw new Exception("File Download Failed:" + replyCode);
			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeOutputStream();	// Output Stream close
			rleaseFtpClient();		// FTP Client release
		}

		return ret;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		if(DEBUG) {
			Log.d(TAG, "onProgressUpdate() progress:" + progress.length);
		}
	}

	@Override
	protected void onPostExecute(String result) {
		if(DEBUG) {
			Log.d(TAG, "onPostExecute()");
		}
		
		// Param check
		if(result == null) {
			Log.e(TAG, "result is null.");
			return;
		}
		if(result.equals("")) {
			Log.e(TAG, "result is blank.");
			return;
		}
		
		// send file path
		if(DEBUG) {
			Log.d(TAG, "result:" + result);
		}
		Intent intent = new Intent();
		intent.setAction(ACTION_FILE_DOWNLOAD_COMPLETE);
		intent.putExtra(EXTRA_FILE_PATH, result);
		LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
	}

	@Override
    protected void onCancelled() {
		if(DEBUG) {
			Log.d(TAG, "onCancelled()");
		}
		closeOutputStream();	// Output Stream close
		rleaseFtpClient();		// FTP Client release
	}

	// OutputStream close
	private void closeOutputStream() {
		if(DEBUG) {
			Log.d(TAG, "closeOutputStream()");
		}
		if(mOutput != null) {
			try {
				mOutput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// release
			mOutput = null;
		}
	}

	// FTP Client release
	private void rleaseFtpClient() {
		if(DEBUG) {
			Log.d(TAG, "rleaseFtpClient()");
		}

		if(mFtpClient != null) {
			// logout
			if(isLogin) {
				try {
					mFtpClient.logout();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// disconnect
			if(mFtpClient.isConnected()) {
				try {
					mFtpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// release
			mFtpClient = null;
		}
	}
}
