package ishi_daishi.jp.ne.biglobe.www7b.apollonseye;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import ishi_daishi.jp.ne.biglobe.www7b.apollonseye.R.id;
import ishi_daishi.jp.ne.biglobe.www7b.apollonseye.ui.CameraCallback;
import ishi_daishi.jp.ne.biglobe.www7b.apollonseye.ui.OverlaySurfaceView;
import ishi_daishi.jp.ne.biglobe.www7b.apollonseye.ui.StatusIcon;

public class CameraFragment extends Fragment {

	// ステータスアイコン
	private StatusIcon mStatusIcon = null;
	// カメラ用SurfaceViewのコールバック
	private CameraCallback mCamCallback = null;
	// プログレスバー
	private ProgressBar mProgressBar1 = null;
	private ProgressBar mProgressBar2 = null;
//	private ProgressBar mProgressBar3 = null;
	private ProgressBar mProgressBar4 = null;
	private ProgressBar mProgressBar5 = null;
	private ProgressBar mProgressBarDatetime = null;
	// テキストビュー
	private TextView mStatusTextView = null;
	private TextView mTextViewDatetime = null;
	// オーバーレイ用のビュー
	OverlaySurfaceView mOverlayView = null;

	// ルートビュー
	private View mRootView =null;

	public static CameraFragment newInstance() {
		return new CameraFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_camera, container, false);

		// ActionBarをLights-Outモードに設定する
		final Activity activity = getActivity();
		mRootView.setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				ActionBar actionBar = activity.getActionBar();
				if(actionBar != null){
					mRootView.setSystemUiVisibility(visibility);
					if(visibility == View.STATUS_BAR_VISIBLE){
						actionBar.show();
					}else{
						actionBar.hide();
					}
				}
			}
		});
		mRootView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mRootView.getSystemUiVisibility() == View.STATUS_BAR_VISIBLE){
					mRootView.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
				}else{
					mRootView.setSystemUiVisibility(View.STATUS_BAR_VISIBLE);
				}
			}
		});


		// 端末情報の取得
		// Moverioだったらカメラを起動しない
//		if ( !Build.MODEL.equals("embt2") ) {
		if ( true ) {
			// カメラ用SurfaceViewのコールバックを作成。
			mCamCallback = new CameraCallback();
			// カメラ用SurfaceViewの準備。
			SurfaceView camView = (SurfaceView)mRootView.findViewById(R.id.CameraView);
			// SurfaceViewからHolderを取得。
			SurfaceHolder holder = camView.getHolder();
			// HolderのTypeをPushBufferに指定する。Android V3.0以前には指定が必要。
			//holder.setType( SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS );
			// Holderにコールバックを追加する。
			holder.addCallback( mCamCallback );
		}

		// ステータスアイコンのImageViewを取得
		ImageView stsImgView = (ImageView)mRootView.findViewById(R.id.imageStatusView);
		// ステータスアイコン用のインスタンスを作成
		mStatusIcon =new StatusIcon( stsImgView );

		// オーバーレイのSurfaceViewを取得
		mOverlayView = (OverlaySurfaceView)mRootView.findViewById(R.id.OverlayView);
		// アニメーションスタート
		//overlayView.start();

		// プログレスバーを取得
		mProgressBar1 = (ProgressBar)mRootView.findViewById(R.id.progressBar1);
		mProgressBar1.setMax(100);
		mProgressBar2 = (ProgressBar)mRootView.findViewById(R.id.progressBar2);
		mProgressBar2.setMax(100);
//		mProgressBar3 = (ProgressBar)mRootView.findViewById(R.id.progressBar3);
//		mProgressBar3.setMax(100);
		mProgressBar4 = (ProgressBar)mRootView.findViewById(R.id.progressBar3);
		mProgressBar4.setMax(100);
		mProgressBar5 = (ProgressBar)mRootView.findViewById(R.id.progressBar5);
		mProgressBar5.setMax(100);
		mProgressBarDatetime = (ProgressBar)mRootView.findViewById(R.id.progressBarDatetime);
		mProgressBarDatetime.setMax(100);

		// ステータス表示用のテキストビューを取得
		mStatusTextView = (TextView)mRootView.findViewById(id.textStatusView);
		// Moverioの場合のテキストサイズを設定
		if ( Build.MODEL.equals("embt2") ) {
			mStatusTextView.setTextSize(30);
			TextView textView1 = (TextView)mRootView.findViewById(id.textView1);
			textView1.setTextSize(30);
			TextView textView2 = (TextView)mRootView.findViewById(id.textView2);
			textView2.setTextSize(30);
			TextView textView4 = (TextView)mRootView.findViewById(id.textView4);
			textView4.setTextSize(30);
			TextView textView5 = (TextView)mRootView.findViewById(id.textView5);
			textView5.setTextSize(30);
		}

		// 日時表示用のテキストビューを取得
		mTextViewDatetime = (TextView)mRootView.findViewById(id.textViewDatetime);

		// Moverioの場合、全画面表示する
//		Window win = getWindow();
//		WindowManager.LayoutParams winParams = win.getAttributes();
//		if ( Build.MODEL.equals("embt2") ) {
//			winParams.flags |= WindowManager.LayoutParams.FLAG_SMARTFULLSCREEN;
//			win.setAttributes(winParams);
//		} else {
//			//FLAG_SMARTFULLSCREEN = 0x80000000;
//			winParams.flags |= WindowManager.LayoutParams.FLAG_SMARTFULLSCREEN;
//			winParams.flags |= 0x80000000;
//		}

		return mRootView;
	}


	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();

		// カメラの開放
		mCamCallback.releaseCamera();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * データの設定処理
	 * @param value
	 */
	public void setData(String[] value ) {

		// 警告表示のテキスト変数
		String alertText = "";

		// オーバーレイのビューにデータを設定
		mOverlayView.setData(value);

/*
		Log.v("debug:0", value[0]);
		Log.v("debug:mProgressBar1", value[1]);
		Log.v("debug:mProgressBar2", value[2]);
		Log.v("debug:mProgressBar3", value[3]);
		Log.v("debug:mProgressBar4", value[4]);
//		Log.v("debug:mProgressBar5", value[5]);
 */

		// プログレスバーの設定
		mProgressBar1.setProgress(Integer.valueOf(value[1]));
		mProgressBar2.setProgress(Integer.valueOf(value[2]));
//		mProgressBar3.setProgress(Integer.valueOf(value[2]));
		mProgressBar4.setProgress(Integer.valueOf(value[3]));
		mProgressBar5.setProgress(Integer.valueOf(value[4]));
		mProgressBarDatetime.setProgress(Integer.valueOf(value[5]));

		// ステータスアイコンと警告文の変更
		// いずれかの値が70%以上の場合は危険
//		if ( mProgressBar1.getProgress() > 70 || mProgressBar2.getProgress() > 70 || mProgressBar4.getProgress() > 70 || mProgressBar5.getProgress() > 70 ) {
		if ( mProgressBar1.getProgress() > 70 || mProgressBar4.getProgress() > 70 || mProgressBar5.getProgress() > 70 ) {
			// 危険状態が表示されていない場合に危険状態に変更
			if ( mStatusIcon.getStatus() != StatusIcon.STATUS_EMERGENCY ) {
				mStatusIcon.setStatus(StatusIcon.STATUS_EMERGENCY);
			}
			// 危険です。外出を控えてください。
			alertText = "Solar activity level - Active, stay indoors.";
		}
		// いずれかの値が50%以上の場合は警告
//		else if ( mProgressBar1.getProgress() >= 50 || mProgressBar2.getProgress() >= 50 || mProgressBar4.getProgress() >= 50 || mProgressBar5.getProgress() >= 50 ) {
		else if ( mProgressBar1.getProgress() >= 50 || mProgressBar4.getProgress() >= 50 || mProgressBar5.getProgress() >= 50 ) {
			// 警告状態が表示されていない場合に警告状態に変更
			if ( mStatusIcon.getStatus() != StatusIcon.STATUS_WARNING ) {
				mStatusIcon.setStatus(StatusIcon.STATUS_WARNING);
			}
			// 注意してください。
			alertText = "Solar activity level - Eruptive.";
		}
		// その他は安全
		else {
			// 安全状態が表示されていない場合に安全状態に変更
			if ( mStatusIcon.getStatus() != StatusIcon.STATUS_SAFETY ) {
				mStatusIcon.setStatus(StatusIcon.STATUS_SAFETY);
			}
			// 安全です。太陽活動は静穏です。
			alertText = "Solar activity level - Quiet.";
		}

		// テキストビューのテキストを設定します
		mTextViewDatetime.setText(value[0] + "(UTC)");
		mStatusTextView.setText(alertText);

	}
}
