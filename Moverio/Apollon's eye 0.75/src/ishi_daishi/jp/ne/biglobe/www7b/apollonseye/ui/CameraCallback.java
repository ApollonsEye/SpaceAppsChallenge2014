package ishi_daishi.jp.ne.biglobe.www7b.apollonseye.ui;

import java.io.IOException;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

public class CameraCallback implements SurfaceHolder.Callback {

	private static final String TAG = "CameraView";

	private Camera mCam;

	/**
	 * コンストラクタ
	 */
	public CameraCallback(){
		try{
			// カメラのインスタンスを作成
			mCam = Camera.open();
		}
		catch ( Exception e ){
			Log.d(TAG, "Error: failed to open Camera > " + e.getMessage());
		}
	}

	/**
	 * SurfaceViewの作成
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			// カメラインスタンスに、画像表示先を設定
			mCam.setPreviewDisplay(holder);
			// プレビュー開始
			mCam.startPreview();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 * SurfaceViewの変更
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// 画面回転に対応する場合は、ここでプレビューを停止し、
		// 回転による処理を実施、再度プレビューを開始する。
	}

	/**
	 * 	SurfaceViewの破棄
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// カメラの開放
		releaseCamera();
	}


	/**
	 * カメラインスタンスの取得。
	 * @return
	 */
	public static Camera getCameraInstance(){
		Camera result = null;

		try {
			// カメラインスタンスの取得
			result = Camera.open();
		}
		catch (Exception e){
			e.printStackTrace();
		}

		// カメラインスタンスが取得できない場合はNULLを返す。
		return result;
	}

	/**
	 * カメラの開放
	 */
	public void releaseCamera() {

		if ( mCam != null ) {
			// SurfaceViewの破棄
			mCam.setPreviewCallback(null);
			// カメラプレビューの停止
			mCam.stopPreview();
			// カメラのインスタンス破棄
			mCam.release();
			// カメラのインスタンスをNULLにする
			mCam = null;
		}
	}
}
