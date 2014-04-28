package ishi_daishi.jp.ne.biglobe.www7b.apollonseye;

import ishi_daishi.jp.ne.biglobe.www7b.apollonseye.service.PeriodicService;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getCanonicalName();
	private static final boolean DEBUG = true;

	/**
	 * public field
	 */
	public static final String ACTION_APPLI_MODE_DEMO = TAG + ".ACTION_APPLI_MODE_DEMO";
	public static final String ACTION_APPLI_MODE_NETWORK = TAG + ".ACTION_APPLI_MODE_NETWORK";

	/**
	 * private field
	 */
	// ブロードキャストレシーバー
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			MainActivity.this.receiveIntent(intent);
		}
	};

	// アラーム用のPendingIntent
	private PendingIntent mPendingIntent = null;

	// メインのカメラを表示するフラグメント
	private CameraFragment mCameraFragment = null;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//タイトルバーを非表示にする
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.activity_main);

		// アクションバーを非表示にする
		ActionBar actionBar = getActionBar();
		actionBar.hide();

		// メインのカメラを表示するフラグメントを作成
		mCameraFragment = new CameraFragment();

		// フラグメントの追加
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, mCameraFragment).commit();
		}

		// 定期処理サービスの起動
		Intent intentPeriodic = new Intent();
		intentPeriodic.setClass(getApplicationContext(), PeriodicService.class);
		Log.v("debug:MainActivity", String.valueOf(this.isNetworkAvailable()));

		if(this.isNetworkAvailable()) {
			intentPeriodic.setAction(ACTION_APPLI_MODE_NETWORK);
		} else {
			intentPeriodic.setAction(ACTION_APPLI_MODE_DEMO);
		}
		//startService(intentPeriodic);
		// サービスを呼び出すPendingIntentを作成
		mPendingIntent = PendingIntent.getService(getApplicationContext(), 0, intentPeriodic, 0);
		// 更新時間の指定(10分)
		long loop =  10 * 60 * 1000;
		// アラームマネージャへ設定
		AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 3000, loop, mPendingIntent);

	}

	@Override
	protected void onResume() {
		super.onResume();

		// フィルターの設定
		IntentFilter filter = new IntentFilter();
		filter.addAction(PeriodicService.ACTION_DATA_UPDATE);

		// レシーバの設定
		LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiver, filter);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// レシーバの解除
		LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// アラームマネージャの破棄
		if (mPendingIntent != null) {
			AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
			am.cancel(mPendingIntent);
			mPendingIntent.cancel();
			mPendingIntent = null;
		}

		// 定期処理サービスの停止
		Intent intentPeriodic = new Intent();
		intentPeriodic.setClass(getApplicationContext(), PeriodicService.class);
		stopService(intentPeriodic);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.action_settings) {
			FragmentManager manager = getFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.addToBackStack("Camera");
			transaction.replace(R.id.container, SettingFragment.newInstance());
			transaction.addToBackStack(null);
			transaction.commit();
			// アクションバーを非表示にする
			ActionBar actionBar = getActionBar();
			actionBar.hide();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * センサーサービスからのIntent受信処理
	 * @param intent
	 */
	private void receiveIntent(Intent intent) {
		Log.d(TAG, "receiveIntent");

		// データ取得
		String[] value = intent.getStringArrayExtra(PeriodicService.EXTRA_STRING_VALUES);

		// Fragmentにデータを設定
		mCameraFragment.setData(value);
	}

	/**
	 * Network Check
	 * @return
	 */
	private boolean isNetworkAvailable() {
		boolean ret = false;

		ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo network = manager.getActiveNetworkInfo();
		if(network == null) {
			return false;
		}

		// connect check
		if(network.isAvailable() && network.isConnected()) {
			ret = true;
		}
		return ret;
	}
}
