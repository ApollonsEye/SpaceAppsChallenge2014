package ishi_daishi.jp.ne.biglobe.www7b.apollonseye.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import ishi_daishi.jp.ne.biglobe.www7b.apollonseye.R.id;

/////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * カメラのビューにかぶせるサーフェイスビュー
 */
public class OverlaySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	private final static int ONE_FRAME_TICK = 1000 / 25;	// 1フレームの時間
	private final static int MAX_FRAME_SKIPS = 5;			// 時間が余ったとき最大何回フレームをスキップするか

	private int mScrWidth;							// 画面の幅
	private int mScrHeight; 						// 画面の高さ
	private SurfaceHolder mHolder;					// サーフェスホルダー

	private Thread mThreadMove;						// 定期的に更新するためのスレッド

	private int mLineY; 							// ラインのある高さ

	private int nkahun = 10;  						// 1画面あたりの花粉の個数
	private Kahun[] kahun = new Kahun[nkahun];
	private float nrad = 1.0f;						// 1秒辺りの放射線の本数
	private Radiation rad = new Radiation(nrad, Color.RED);
	private float nrad2 = 2.0f;	 					// 1秒辺りの放射線の本数
	private Radiation rad2 = new Radiation(nrad2, Color.YELLOW);
	public int strength = 3;  						// 紫外線の強さ
	public float strengthRad = 3;	 					// 放射線の強さ
	public int strengthPb1 = 3, strengthPb2 = 3;  	// 粒子線の強さ
	private UV uv = new UV( strength );
	private ParticleBeam[] pb1 = new ParticleBeam[10];
//	private ParticleBeam[] pb2 = new ParticleBeam[10];
//	  private ParticleBeam pb1 = new ParticleBeam(Color.RED, Color.RED, 5, 5);

	// データ格納用配列
	private String[] mValue = null ;

	int tempi = 1;
	/**
	 * コンストラクタ
	 * @param context
	 */
	public OverlaySurfaceView(Context context) {
		super(context);

		// サーフェイスホルダーを取り出す
		this.mHolder = this.getHolder();
		this.mHolder.setFormat(PixelFormat.TRANSPARENT);

		// コールバック関数を登録する
		this.mHolder.addCallback(this);
		this.setZOrderMediaOverlay(true);

		// 花粉の初期化
		for ( int i = 0; i < nkahun; i++ ) {
			kahun[i] = new Kahun();
		}

		// 粒子線の初期化
		for ( int i = 0; i < 10; i++ ) {
			pb1[i] = new ParticleBeam(Color.GREEN, Color.GREEN, 10, 5);
//			pb2[i] = new ParticleBeam(Color.RED, Color.RED, 10, 5);
		}
	}

	public OverlaySurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// サーフェイスホルダーを取り出す
		this.mHolder = this.getHolder();
		this.mHolder.setFormat(PixelFormat.TRANSPARENT);

		// コールバック関数を登録する
		this.mHolder.addCallback(this);
		this.setZOrderMediaOverlay(true);

		// 花粉の初期化
		for ( int i = 0; i < nkahun; i++ ) {
//			kahun[i] = new Kahun();
		}

		// 粒子線の初期化
		for ( int i = 0; i < 10; i++ ) {
			pb1[i] = new ParticleBeam(Color.GREEN, Color.GREEN, 5, 5);
//			pb2[i] = new ParticleBeam(Color.RED, Color.RED, 5, 5);
		}
	}

	public OverlaySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// サーフェイスホルダーを取り出す
		this.mHolder = this.getHolder();
		this.mHolder.setFormat(PixelFormat.TRANSPARENT);

		// コールバック関数を登録する
		this.mHolder.addCallback(this);
		this.setZOrderMediaOverlay(true);

		// 花粉の初期化
		for ( int i = 0; i < nkahun; i++ ) {
//			kahun[i] = new Kahun();
		}

		// 粒子線の初期化
		for ( int i = 0; i < 10; i++ ) {
			pb1[i] = new ParticleBeam(Color.GREEN, Color.GREEN, 5, 5);
//			pb2[i] = new ParticleBeam(Color.RED, Color.RED, 5, 5);
		}
	}

	/**
	 * サーフェイスの変更
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		this.mScrWidth = width;
		this.mScrHeight = height;
	}

	/**
	 * サーフェイスが作られた
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		// 更新用スレッドの開始
		this.mThreadMove = new Thread(this);
		this.mThreadMove.start();

	}

	public void start() {
		// 更新用スレッドの開始
		this.mThreadMove = new Thread(this);
		this.mThreadMove.start();
	}

	/**
	 * サーフェイスが破棄された
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		this.mHolder = null;
		this.mThreadMove = null;
	}

	/**
	 * 適当に呼び出される
	 */
	@Override
	public void run() {
		Canvas canvas;
		long beginTime; // 処理開始時間
		long pastTick;	// 経過時間
		int sleep = 0;
		int frameSkipped;	// 何フレーム分スキップしたか

		// フレームレート関連
		int frameCount = 0;
		long beforeTick = 0;
		long currTime = 0;
		String tmp = "";

		// 文字書いたり
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setAntiAlias(true);
		paint.setTextSize(60);

		int count = 0;
		// スレッドが消滅していない間はずっと処理し続ける
		while (this.mThreadMove != null) {
			canvas = null;

			// フレームレートの表示
			frameCount++;
			currTime = System.currentTimeMillis();
			if (beforeTick + 1000 < currTime) {
				beforeTick = currTime;
				tmp = "" + frameCount;
				frameCount = 0;
			}

			try {

				synchronized (this.mHolder) {
					canvas = this.mHolder.lockCanvas();
					// キャンバスとれなかった
					if (canvas == null)
						continue;

					// 背景をクリア
					canvas.drawColor(0, Mode.CLEAR);

					// 現在時刻
					beginTime = System.currentTimeMillis();
					frameSkipped = 0;

					// ////////////////////////////////////////////////////////////
					// ↓アップデートやら描画やら
					this.move();
					canvas.save();
					this.draw(canvas);
					canvas.restore();

					// ////////////////////////////////////////////////////////////

					// 経過時間
					pastTick = System.currentTimeMillis() - beginTime;

					// 余っちゃった時間
					sleep = (int)(ONE_FRAME_TICK - pastTick);

					// 余った時間があるときは待たせる
					if (0 < sleep) {
						try {
							Thread.sleep(sleep);
						} catch (Exception e) {}
					}

					// 描画に時間係過ぎちゃった場合は更新だけ回す
					while (sleep < 0 && frameSkipped < MAX_FRAME_SKIPS) {
						// ////////////////////////////////////////////////////////////
						// 遅れた分だけ更新をかける
						this.move();
						// ////////////////////////////////////////////////////////////
						sleep += ONE_FRAME_TICK;
						frameSkipped++;
					}
//					  canvas.drawText("FPS:" + tmp, 10, 60, paint);
				}
			} finally {
				// キャンバスの解放し忘れに注意
				if (canvas != null) {
					this.mHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}

	/**
	 * サーフェイス内のものを動かす
	 */
	private void move() {
		this.mLineY += 20;
//		  if (this.mScrHeight + (LINE_COUNT * LINE_STEP) < this.mLineY) {
//			  this.mLineY = 0;
//		  }
	}

	/**
	 * システムからの描画呼び出し
	 */
	public void onDraw() {
		Canvas canvas = this.mHolder.lockCanvas();
		this.draw(canvas);
		this.mHolder.unlockCanvasAndPost(canvas);

	}

	/**
	 * サーフェイス内のものの描画
	 */
	public void draw(Canvas canvas) {

		// データが設定されていない場合は何もしない
		// 何故か0～4の各要素にnullが入ることがあり、Integer.valueOfで落ちるので、その対策。
		if (mValue == null || mValue[0] == null || mValue[1] == null || mValue[2] == null || mValue[3] == null || mValue[4] == null) {
//			Log.v("debug:OSV:draw:mValue", "," + mValue[0] + "," + mValue[1] + "," + mValue[2] + "," + mValue[3] + "," + mValue[4]);
			Log.v("debug:OSV:draw:mValue", "null");
			return;
		}

		Paint paint = new Paint(), paint2 = new Paint(), paint3 = new Paint();
		paint.setStrokeWidth(1);

		// 直線の描画
		paint2.setColor(Color.RED);
		paint2.setAntiAlias(true);

		// 画面サイズを取得
		int width = canvas.getWidth();
		int height = canvas.getHeight();
//		  canvas.drawText("Width: " + width + ", Height: " + height, 10, 120, paint2);

		// 上から迫ってくるやつの描画(元データはProton Density)
//		Log.v("debug:value0", "start");
//		Log.v("debug:OSV:draw:mValue", "," + mValue[0] + "," + mValue[1] + "," + mValue[2] + "," + mValue[3] + "," + mValue[4]);

		try{  // 何故か0～4の各要素にnullが入ることがあり、Integer.valueOfで落ちるので、その対策。
			if ( Integer.valueOf(mValue[1]) > 80 ) strength = 5;
			else if ( Integer.valueOf(mValue[1]) > 60 ) strength = 4;
			else if ( Integer.valueOf(mValue[1]) > 40 ) strength = 3;
			else if ( Integer.valueOf(mValue[1]) > 20 ) strength = 2;
			else strength = 1;
			uv.paint(canvas, strength);
	
			// 放射線の描画(元データはX-Ray Short)
			if ( Integer.valueOf(mValue[3]) > 80 ) strengthRad = 5;
			else if ( Integer.valueOf(mValue[3]) > 60 ) strengthRad = 4;
			else if ( Integer.valueOf(mValue[3]) > 40 ) strengthRad = 3;
			else if ( Integer.valueOf(mValue[3]) > 20 ) strengthRad = 2;
			else strengthRad = 0.1f;
			rad.paint(canvas, strengthRad);
	
			// 放射線の描画(元データはX-Ray Long)
			if ( Integer.valueOf(mValue[4]) > 80 ) strengthRad = 5;
			else if ( Integer.valueOf(mValue[4]) > 60 ) strengthRad = 4;
			else if ( Integer.valueOf(mValue[4]) > 40 ) strengthRad = 3;
			else if ( Integer.valueOf(mValue[4]) > 20 ) strengthRad = 2;
			else strengthRad = 0.1f;
			rad2.paint(canvas, strengthRad);
	
			// 花粉の描画
	/*
			for ( int i = 0; i < nkahun; i++ ) {
				kahun[i].move(canvas);
			}
	 */
	
			// 粒子線の描画(Proton Density)
	//		  for ( int i = 0; i < 10; i++ ) {
			if ( Integer.valueOf(mValue[1]) > 80 ) strengthPb1 = 5;
			else if ( Integer.valueOf(mValue[1]) > 60 ) strengthPb1 = 4;
			else if ( Integer.valueOf(mValue[1]) > 40 ) strengthPb1 = 3;
			else if ( Integer.valueOf(mValue[1]) > 20 ) strengthPb1 = 2;
			else strengthPb1 = 1;
			for ( int i = 0; i < strengthPb1; i++ ) {
				pb1[i].move(canvas, (int)(Integer.valueOf(mValue[2]) / 1));
			}
	
	/*
			if ( Integer.valueOf(mValue[2]) > 80 ) strengthPb2 = 5;
			else if ( Integer.valueOf(mValue[2]) > 60 ) strengthPb2 = 4;
			else if ( Integer.valueOf(mValue[2]) > 40 ) strengthPb2 = 3;
			else if ( Integer.valueOf(mValue[2]) > 20 ) strengthPb2 = 2;
			else strengthPb2 = 1;
			for ( int i = 0; i < strengthPb2 * 2; i++ ) {
				pb2[i].move(canvas);
			}
	 */
		} catch (Exception e) {
		      Log.v("debug:OSV:draw:error", e.toString());
		}
			
	}

	/**
	 * データのコピー
	 * @param data
	 */
	public void setData(String[] data) {

		// 配列数が3以下の場合はなにもしない
		if (data.length < 4) {
			return;
		}

		// データのロック
		synchronized (this) {
			String temp = ""; 
			// Cloneがだめな場合はfor文で
			//mValue = (String[])data.clone();
			mValue = new String[data.length];
			for (int i = 0; i < data.length; i++) {
				mValue[i] = data[i];
				temp = temp + "," + mValue[i];
			}
//		      Log.v("debug:OSV:setData:mValue", temp);
		}
    }
}