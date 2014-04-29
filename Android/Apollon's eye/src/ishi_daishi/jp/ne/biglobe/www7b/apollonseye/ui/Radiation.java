package ishi_daishi.jp.ne.biglobe.www7b.apollonseye.ui;

import java.util.Random;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

// 【放射線を描画】
// ＜コンストラクタ引数＞
//   float:1秒辺りの放射線本数（現状、FPS以上に増やしても変化なし）
//   int:放射線の色（省略可）
// ＜メソッド＞
//  void paint (Canvas canvas)
//  呼ばれる度にコンストラクタで指定された本数（確率）で線を描画する
//  void paint (Canvas canvas, float n)
// ＜変更履歴＞
//  14/04/08 コンストラクタ第一引数をintからfloatに変更
//  14/04/07 初版作成
class Radiation {
  private int o, color, width, height;
  float nrad;
  long time1, time2;
  private Paint paint = new Paint();
  private Random ran = new Random();
  private boolean debug = false;

  // コンストラクタ
  Radiation (float n) {
	nrad = n;  
	time1 = -1;
	color = Color.argb( 0x77, 0x7f, 0xff, 0x7f);
  }

  Radiation (float n, int c) {
	nrad = n;  
	time1 = -1;
	color = c;
  }

  // 直線の描画 
  void paint (Canvas canvas, float nr) {
	nrad = nr;  

	if ( time1 != -1 ) {
      if ( debug ) Log.v("time1", String.valueOf(time1));
	  time2 = System.currentTimeMillis();
	  if ( debug ) Log.v("time2", String.valueOf(time2));
	  if ( debug ) Log.v("time2 - time1", String.valueOf((long)(time2 - time1)));
	  if ( debug ) Log.v("nextInt", String.valueOf( 1000/ nrad / ( time2 - time1 ) ) );
      int temp = (int)( 1000/ nrad / ( time2 - time1 ) ) - 1;
      if ( temp <= 0 ) { o = 0; }
      else { o = ran.nextInt( temp ); }
      if ( debug ) Log.v("o", String.valueOf(o));
      time1 = time2;
//      Log.v("time2 - time1", String.valueOf(time2 - time1));
	} else {
		if ( debug ) Log.v("time1*", String.valueOf(time1));
        time1 = System.currentTimeMillis();
		return;
	}

	if ( o == 0 ) {
  	  paint.setAntiAlias(true);

      // 画面サイズを取得
      int width = canvas.getWidth();
      int height = canvas.getHeight();
    
      int n = ran.nextInt(width);
      int m = ran.nextInt(width);
    
      paint.setStrokeWidth(4);
      paint.setColor( color );
      canvas.drawLine(n, 0, m, height, paint);
		
	}
  }
  
	  // 直線の描画 
	  void paint (Canvas canvas) {

		if ( time1 != -1 ) {
	      if ( debug ) Log.v("time1", String.valueOf(time1));
		  time2 = System.currentTimeMillis();
		  if ( debug ) Log.v("time2", String.valueOf(time2));
		  if ( debug ) Log.v("time2 - time1", String.valueOf((long)(time2 - time1)));
		  if ( debug ) Log.v("nextInt", String.valueOf( 1000/ nrad / ( time2 - time1 ) ) );
	      int temp = (int)( 1000/ nrad / ( time2 - time1 ) ) - 1;
	      if ( temp <= 0 ) { o = 0; }
	      else { o = ran.nextInt( temp ); }
	      if ( debug ) Log.v("o", String.valueOf(o));
	      time1 = time2;
//	      Log.v("time2 - time1", String.valueOf(time2 - time1));
		} else {
			if ( debug ) Log.v("time1*", String.valueOf(time1));
	        time1 = System.currentTimeMillis();
			return;
		}

		if ( o == 0 ) {
	  	  paint.setAntiAlias(true);

	      // 画面サイズを取得
	      int width = canvas.getWidth();
	      int height = canvas.getHeight();
	    
	      int n = ran.nextInt(width);
	      int m = ran.nextInt(width);
	    
	      paint.setStrokeWidth(4);
	      paint.setColor( color );
	      canvas.drawLine(n, 0, m, height, paint);
			
		}

  }
}

// 【紫外線を描画】
// ＜コンストラクタ引数＞
//  int:紫外線の強さ(min=1, max=5)
// ＜メソッド＞
//  void paint (Canvas canvas, int s)
//  紫外線を描画する
// ＜変更履歴＞
//  14/04/10 初版作成
class UV {
	private int o, color, width, height, count, reverse = 1;
	private int strength, LowerLimit, add;
	private long time1, time2;
	private Paint paint = new Paint();
	private boolean debug = false;
	
	// コンストラクタ
	UV (int s) {
		strength = s;
		count = 0;
	}
	
	// 紫外線の描画 
	void paint (Canvas canvas, int s) {
		strength = s;
	
 	   paint.setAntiAlias(true);
	
	   // 画面サイズを取得
	   int width = canvas.getWidth();
	   int height = canvas.getHeight();
	 
	   paint.setStrokeWidth(5);
	   paint.setStyle(Paint.Style.FILL);

	   paint.setColor(Color.argb(30, 255, 0, 255) );
	   canvas.drawArc(new RectF( -strength * 100, -count, width + strength * 100, count), 0, 180, false, paint);
	   paint.setColor(Color.argb(30, 255, 0, 255) );
	   canvas.drawArc(new RectF( -strength * 100, (int)(-count*0.8), width + strength * 100, (int)(count*0.8) ), 0, 180, false, paint);
	   paint.setColor(Color.argb(30, 255, 0, 255) );
	   canvas.drawArc(new RectF( -strength * 100, (int)(-count*0.6), width + strength * 100, (int)(count*0.6) ), 0, 180, false, paint);
	   paint.setColor(Color.argb(30, 255, 0, 255) );
	   canvas.drawArc(new RectF( -strength * 100, (int)(-count*0.4), width + strength * 100, (int)(count*0.4) ), 0, 180, false, paint);
	   paint.setColor(Color.argb(30, 255, 0, 255) );
	   canvas.drawArc(new RectF( -strength * 100, (int)(-count*0.2), width + strength * 100, (int)(count*0.2) ), 0, 180, false, paint);

	    count += add;
	    
	    // アニメーションの速度を調整
	    switch ( strength ) {
	    case 1:
	    	LowerLimit = 100;
	    	add = 5 * reverse;
	    	break;
	    case 2:
	    	LowerLimit = height / 4;
	    	add = 10 * reverse;
	    	break;
	    case 3:
	    	LowerLimit = height / 2;
	    	add = 20 * reverse;
	    	break;
	    case 4:
	    	LowerLimit = height / 4 * 3;
	    	add = 20 * reverse;
	    	break;
	    case 5:
	    	LowerLimit = height - 100;
	    	add = 40 * reverse;
	    	break;
	    }

	    if ( count > LowerLimit ) reverse = -1;
	    if ( count < 0 ) reverse = 1;
	}
}

// 【花粉を1つ描画】
// ＜コンストラクタ引数＞
//  int:花粉の色（省略可）
// ＜メソッド＞
//  void move(Canvas canvas)
//  呼ばれる度に花粉を１回動かす
// ＜変更履歴＞
//  14/04/07 初版作成
class Kahun {
	private int iCircleX, iCircleY, color, r, width, height;
	private Paint paint = new Paint();
	private Random ran = new Random();
	//private Canvas canvas;
	//private SurfaceHolder mHolder;  // サーフェイスホルダー

  // コンストラクタ
  Kahun () {
      iCircleX = -100;
      iCircleY = -100;

      // 円の描画
      paint.setColor( Color.YELLOW );
//      canvas.drawCircle(iCircleX, iCircleY, 5, paint);
  }
    
  Kahun (int c) {
      iCircleX = -100;
      iCircleY = -100;

      // 円の描画
      paint.setColor( c );
//      canvas.drawCircle(iCircleX, iCircleY, 5, paint);
  }

  void move(Canvas canvas) {
	  // 初期化する場合
	  if ( iCircleX == -100 && iCircleY == -100 ) {
	      // 画面サイズを取得
	      width = canvas.getWidth();
	      height = canvas.getHeight();

	      iCircleX = ran.nextInt(width);
	      iCircleY = ran.nextInt(height);
	  }

	  int o = ran.nextInt(3);
      if ( o == 0 ) { iCircleX = iCircleX + 8; }
      if ( o == 1 ) { iCircleX = iCircleX - 8; }
      if ( o == 2 ) { iCircleY = iCircleY + 8; }
      if ( o == 3 ) { iCircleY = iCircleY - 7; }
      canvas.drawCircle(iCircleX, iCircleY, 5, paint);
      if ( iCircleY > height + 50 ) { iCircleY = 0; }
  }
  
}

// 【粒子線を1つ描画】
// ＜コンストラクタ引数＞
//  int:粒子線の色
//  int:粒子の色
//  int:粒子の大きさ
//  int:速さ
// ＜メソッド＞
//  void move(Canvas canvas, int s)
//  呼ばれる度に１回動かす
//  int:速さ
// ＜変更履歴＞
//  14/04/27 粒子が左下向きにも移動するように修正。
//  14/04/12 初版作成
class ParticleBeam {
	private int beamColor, particleColor, particleSize, speed, r, startX, endX, nowX, nowY, width, height;
	private float slope, intercept;
	private boolean debug = false;
	private Paint paint = new Paint();
	private Random ran = new Random();
	private double theta;
	
  // コンストラクタ
  ParticleBeam (int bc, int pc, int ps, int sp) {
	  startX = endX = nowX = nowY = 0;
	  
      paint.setColor( pc );
  	  paint.setAntiAlias(true);
      paint.setStrokeWidth(5);
      
      switch (sp) {
        case 5: speed = 20; break;
      }
  }

  void move(Canvas canvas, int s) {
    speed = s;
    
    // 初期化する場合
    if ( nowY == 0 ) {

      // 画面サイズを取得
      width = canvas.getWidth();
      height = canvas.getHeight();
    
      // 線の始点を決定
      startX = ran.nextInt(width);
//      endX = ran.nextInt(width);
      
      // 直線の傾きと切片を計算
      slope = ran.nextFloat() * 1;
      if ( ran.nextInt(2) == 1 ) slope = - slope;
    	theta = Math.atan(slope);
//      Log.v("Debug:ran.nextInt", String.valueOf(ran.nextInt(2)));
//      intercept = -slope * startX;

//      Log.v("Debug:startX", String.valueOf(startX));
//      if ( debug ) Log.v("endX", String.valueOf(endX));
//      Log.v("debug:slope", String.valueOf(slope));
//      if ( debug ) Log.v("intercept", String.valueOf(intercept));
      
      intercept = -slope * startX;
      nowX = startX;
      nowY = 0;
	}
//  	canvas.drawLine(n, 0, m, height, paint);

  	canvas.drawCircle(nowX, nowY, 5, paint);
  	
  	// 移動させる
  	if ( theta >= 0 ) nowX += speed * Math.cos(theta);
  	else nowX -= speed * Math.cos(theta);
//  	nowY += speed * Math.sin(theta);
//  	nowX += speed;
  	nowY += (int)(nowX * slope + intercept);
//    Log.v("debug:theta,X,Y", String.valueOf(slope) + "," + String.valueOf(nowX) + "," + String.valueOf(nowY));

  	// 画面外に出てしまったら最初に戻す 
  	if ( nowY > height ) { nowY = 0; }
  	if ( nowX > width || nowX < 0 ) { nowY = 0; }
  }
  
}

