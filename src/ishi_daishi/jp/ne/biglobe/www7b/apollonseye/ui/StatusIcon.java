package ishi_daishi.jp.ne.biglobe.www7b.apollonseye.ui;

import android.graphics.BitmapFactory;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import ishi_daishi.jp.ne.biglobe.www7b.apollonseye.R;

public class StatusIcon {

	/// ステータス-安全
	public static final int STATUS_SAFETY = 0;
	/// ステータス-警告
	public static final int STATUS_WARNING = -10;
	/// ステータス-危険
	public static final int STATUS_EMERGENCY = -50;


	// 現在のステータスをあらわす値
	private int mStatus = STATUS_SAFETY;

	// 画像を表示するイメージビュー
	private ImageView mStatusImage = null;

	// 実行するアニメーションセット-安全
	private ScaleAnimation mScaleAnimationSafety = null;

	// 実行するアニメーションセット-警告
	private ScaleAnimation mScaleAnimationWarning = null;

	// 実行するアニメーションセット-危険
	private ScaleAnimation mScaleAnimationEmergency = null;

	/**
	 * コンストラクタ
	 * @param statusImageView	アニメーションさせるビューを指定する。
	 */
	public StatusIcon (ImageView statusImageView) {

		// イメージビューのインスタンスを保持
		mStatusImage = statusImageView;
		// 安全のイメージを設定
		mStatusImage.setImageResource(R.drawable.ic_sts_safety);

		// 画像を読み込まずに画像サイズ情報を取得する
		BitmapFactory.Options options = new BitmapFactory.Options() ;
		options.inJustDecodeBounds = true ;
		// サイズの取得
		BitmapFactory.decodeResource(statusImageView.getContext().getResources(), R.drawable.ic_sts_warning, options);

		// 実行するアニメーションセット-安全を作成
		mScaleAnimationSafety = new ScaleAnimation(1, 1, 1, 1);
		// アニメーション後は元の状態に戻す
		mScaleAnimationSafety.setFillBefore( true );
		// アニメーションの再生時間を指定する
		mScaleAnimationSafety.setDuration(500);

		// 実行するアニメーションセット-警告を作成
		mScaleAnimationWarning = new ScaleAnimation(1.0f, 0.8f, 1.0f, 0.8f, options.outWidth, 0);
		// アニメーション後は元の状態に戻す
		mScaleAnimationWarning.setFillBefore( false );
		// アニメーションを繰り返し(無限)に設定する
		mScaleAnimationWarning.setRepeatCount( Animation.INFINITE );
		// アニメーションの繰り返しを反復繰り返しとなる
		mScaleAnimationWarning.setRepeatMode( Animation.REVERSE );
		// アニメーションの再生時間を指定する
		mScaleAnimationWarning.setDuration(600);

		// 実行するアニメーションセット-危険を作成
		mScaleAnimationEmergency = new ScaleAnimation(1.0f, 0.8f, 1.0f, 0.8f, options.outWidth, 0);
		// アニメーション後は元の状態に戻す
		mScaleAnimationEmergency.setFillBefore( false );
		// アニメーションを繰り返し(無限)に設定する
		mScaleAnimationEmergency.setRepeatCount( Animation.INFINITE );
		// アニメーションの繰り返しを反復繰り返しとなる
		mScaleAnimationEmergency.setRepeatMode( Animation.REVERSE );
		// アニメーションの再生時間を指定する
		mScaleAnimationEmergency.setDuration(300);

		// ステータスの設定とアニメーションを開始
		setStatus(STATUS_SAFETY);
	}

	/**
	 * ステータスを設定し、アニメーションを開始する。
	 * @param status	ステータスの設定。
	 */
	public void setStatus(int status) {

		// 現在のステータスを保持
		mStatus = status;

		// ステータスによって表示する画像を変更
		switch (mStatus) {
			// ステータス-安全の場合
			case STATUS_SAFETY:
				mStatusImage.setImageResource(R.drawable.ic_sts_safety);
				// アニメーションを開始
				mStatusImage.startAnimation(mScaleAnimationSafety);
				break;
			// ステータス-警告の場合
			case STATUS_WARNING:
				mStatusImage.setImageResource(R.drawable.ic_sts_warning);
				// アニメーションを開始
				mStatusImage.startAnimation(mScaleAnimationWarning);
				break;
			// ステータス-危険の場合
			case STATUS_EMERGENCY:
				mStatusImage.setImageResource(R.drawable.ic_sts_emergency);
				// アニメーションを開始
				mStatusImage.startAnimation(mScaleAnimationEmergency);
				break;
			// デフォルトの場合
			default:
				break;
		}
	}

	/**
	 * ステータスを取得する
	 * @param status	設定されているステータスを返す。
	 */
	public int getStatus() {
		return mStatus;
	}

}
