package ishi_daishi.jp.ne.biglobe.www7b.apollonseye.control;

import java.util.Vector;

import android.util.Log;

// 【テキストファイルを読み込んで現在のパラメータを返す】
// ＜コンストラクタ引数＞
// ＜メソッド＞
//	 public String[] get()
//	 各要素データを0～100に正規化して返す
// ＜変更履歴＞
//	14/04/12 初版作成
public class DataProvider {
	private int i, iSize;  // 読み込んだデータ配列のカウンタとデータの個数
	private int j, jSize;  // 整形済みデータ配列のカウンタとデータの個数

	// 0-100に正規化したデータ
	// 0: yyyy/mm/dd hh:mm
	// 1: Proton Density
	// 2: Bulk Speed
	// 3: xr Short
	// 4: xr Long
	private String[] ret = new String[5];
//	private String[] ret;

//	private float[] minValue, maxValue, nowF;
	private float[] minValue = new float[5];
	private float[] maxValue = new float[5];
	private float[] nowF = new float[5];
	private float fTemp;
	//private float minValue = 1000000.0f, maxValue = 0.0f;
//	long time1, time2; // time1=現在の時刻、time2=前回表示した時の時刻
	private boolean debug = false;
	private Vector rec, rec2;
	private String[] cell;
	private String[][] preparedData;  // 整形済みデータ配列

	// コンストラクタ *色々と古いので使わないこと
	public DataProvider ( Vector data ) {
//		time1 = time2 = System.currentTimeMillis();
//		temp = 0;

		//	  タブ区切りファイルの読み込み
//		// Moverioだったら
//		if ( Build.MODEL.equals("embt2") ) {
//			rec = Csv.read("/mnt/sdcard/Ace_1401peak_1h.csv");
//		} else {
//			rec = Csv.read("/storage/emulated/0/Ace_1401peak_1h.csv");
//		}
//		rec = Csv.read(Environment.getExternalStorageDirectory().getPath() + "/Ace_1401peak_1h.csv");
		rec = (Vector) data.clone();

		if ( rec == null ) return;
		else {
			iSize = rec.size();
//			Log.v("debug:iSize", String.valueOf(iSize));
			preparedData = new String[rec.size()][5];

			// 最終的に使う要素数で初期化
/*
			ret = new String[5];
			minValue = new float[5];
			maxValue = new float[5];
			nowF = new float[5];
 */

			i = j = jSize = 0;
//			Log.v("debug:iSize", String.valueOf(iSize));

//			i = 3; // 最初の3行は飛ばす

//			minValue[0] = minValue[1] = minValue[2] = minValue[3] = 10000000.0f;
//			maxValue[0] = maxValue[1] = maxValue[2] = maxValue[3] = 0.0f;

			// 最大値・最小値を求める
			do {
//				Log.v("debug:i1", String.valueOf(i));

				cell = (String[])rec.elementAt(i);
//				Log.v("debug:i4", String.valueOf(i));
//				Log.v("debug:cell0", cell[0]);

				// エラーデータ（0以外）は読み飛ばす
//				if ( Integer.parseInt(cell[9]) != 0 || Integer.parseInt(cell[6]) != 0 ) {
				if ( Integer.parseInt(cell[6]) != 0 ) {
				//if ( Integer.parseInt(cell[11]) != 0 ) {
					//Log.v("debug:i3", String.valueOf(i));
					i++;
					continue;
				}

//				Log.v("debug:i2", String.valueOf(i));

				// 年月日日時
				preparedData[j][0] = cell[0] + "/" + cell[1] + "/" + cell[2] + " " + cell[3].substring(0, 2) + ":" + cell[3].substring(2, 4);
				Log.v("debug:", preparedData[j][0]);

				// Proton Density
				preparedData[j][1] = cell[7];
				nowF[1] = Float.valueOf(cell[7]);

				// Bulk Speed
				preparedData[j][2] = cell[8];
				nowF[2] = Float.valueOf(cell[8]);

				// 一番最初は現在の値をmaxとminにセットする
				if ( i == 0 ) {
					maxValue[1] = minValue[1] = nowF[1];
					maxValue[2] = minValue[2] = nowF[2];
				}

				if ( maxValue[1] < nowF[1] ) maxValue[1] = nowF[1];
				if ( minValue[1] > nowF[1] ) minValue[1] = nowF[1];
				if ( maxValue[2] < nowF[2] ) maxValue[2] = nowF[2];
				if ( minValue[2] > nowF[2] ) minValue[2] = nowF[2];

				i++;
				j++;

			} while ( i < iSize );

			jSize = j;
			j = 0;

			// 0-100に変換
			for ( i = 0; i < jSize; i++ ) {
				fTemp = Float.valueOf(preparedData[i][1]) - minValue[1];
				preparedData[i][1] = String.valueOf( (int)(fTemp / ( maxValue[1] - minValue[1] ) * 100 ));

				fTemp = Float.valueOf(preparedData[i][2]) - minValue[2];
				preparedData[i][2] = String.valueOf( (int)(fTemp / ( maxValue[2] - minValue[2] ) * 100 ));

				preparedData[i][3] = preparedData[i][1];
				preparedData[i][4] = preparedData[i][2];

			}
		}
//		Log.v("debug:max/minValue[1]", String.valueOf(maxValue[1]) + ", " + String.valueOf(minValue[1]));
//		Log.v("debug:max/minValue[2]", String.valueOf(maxValue[2]) + ", " + String.valueOf(minValue[2]));
//		Log.v("debug:minValue[0]", String.valueOf(minValue[0]));
//		Log.v("debug:maxValue[1]", String.valueOf(maxValue[1]));
//		Log.v("debug:minValue[1]", String.valueOf(minValue[1]));
//		Log.v("debug:maxValue[2]", String.valueOf(maxValue[2]));
//		Log.v("debug:minValue[2]", String.valueOf(minValue[2]));
//		Log.v("debug:maxValue[3]", String.valueOf(maxValue[3]));
//		Log.v("debug:minValue[3]", String.valueOf(minValue[3]));
	}

	// コンストラクタ
	public DataProvider ( Vector data, Vector data2 ) {
		Log.v("debug:DataProvider", "Start");
		rec = (Vector) data.clone();
		rec2 = (Vector) data2.clone();

		// "ace_swepam_1m.txt"の処理
		if ( rec == null ) return;
		else {
			iSize = rec.size();
//			Log.v("debug:iSize", String.valueOf(iSize));
			preparedData = new String[rec.size()][5];

			i = j = jSize = 0;
//			Log.v("debug:iSize", String.valueOf(iSize));

			// 最大値・最小値を求める
			// 読み込んだデータから求める
			do {
//				Log.v("debug:i1", String.valueOf(i));

				cell = (String[])rec.elementAt(i);
//				Log.v("debug:i4", String.valueOf(i));
//				Log.v("debug:cell0", cell[0]);

				// エラーデータ（0以外）は読み飛ばす
//				if ( Integer.parseInt(cell[9]) != 0 || Integer.parseInt(cell[6]) != 0 ) {
				if ( cell.length < 9 || Integer.parseInt(cell[6]) != 0 || cell[8].equals("-9999.9") ) {
				//if ( Integer.parseInt(cell[11]) != 0 ) {
					//Log.v("debug:i3", String.valueOf(i));
					i++;
					continue;
				}

//				Log.v("debug:i2", String.valueOf(i));

				// 年月日日時
				preparedData[j][0] = cell[0] + "/" + cell[1] + "/" + cell[2] + " " + cell[3].substring(0, 2) + ":" + cell[3].substring(2, 4);
/*
				Log.v("debug:org", preparedData[j][0]);
			    Date d = null;
			    DateFormat df1 = new SimpleDateFormat("yyyy/MM/dd hh:mm");
				df1.setTimeZone(TimeZone.getTimeZone("UTC"));
				try {
//					d = df2.parse("2011/03/23 10:23 UTC");
//					d = df2.parse(preparedData[j][0] + " GMT");
					d = df1.parse(preparedData[j][0]);
					Log.v("debug:UTC", df1.format(d));
				} catch (ParseException e) {
					Log.v("debug:", e.toString());
				}

				df1.setTimeZone(TimeZone.getDefault());
				Log.v("debug:local", df1.format(d));

				df1.setTimeZone(TimeZone.getTimeZone("America/New_York"));
				Log.v("debug:America/New_York", df1.format(d));

				df1.setTimeZone(TimeZone.getTimeZone("Japan"));
				Log.v("debug:Japan", df1.format(d));
 */

				/*
				String[] tzIDs = TimeZone.getAvailableIDs();
				for ( i = 0; i < tzIDs.length; i++ ) {
					Log.v("debug:TimeZoneIDs", tzIDs[i]);
				}
				 */

			    //			    DateFormat df2 = new SimpleDateFormat("yyyy/MM/dd hh:mm zzz", java.util.Locale.UK);
//			    DateFormat df2 = new SimpleDateFormat("yyyy/MM/dd hh:mm", java.util.Locale.UK);

				// Proton Density
				preparedData[j][1] = cell[7];
				nowF[1] = Float.valueOf(cell[7]);

				// Bulk Speed
				preparedData[j][2] = cell[8];
				nowF[2] = Float.valueOf(cell[8]);

				// 一番最初は現在の値をmaxとminにセットする
				if ( i == 0 ) {
					maxValue[1] = minValue[1] = nowF[1];
					maxValue[2] = minValue[2] = nowF[2];
				}

				if ( maxValue[1] < nowF[1] ) maxValue[1] = nowF[1];
				if ( minValue[1] > nowF[1] ) minValue[1] = nowF[1];
				if ( maxValue[2] < nowF[2] ) maxValue[2] = nowF[2];
				if ( minValue[2] > nowF[2] ) minValue[2] = nowF[2];

				i++;
				j++;

			} while ( i < iSize );
			if ( true ) {
//			if ( false ) {
			  // 過去1年間のデータで上書き
		      maxValue[1] = 45.1f;
			  minValue[1] = 0.0f;
			  maxValue[2] = 849.9f;
			  minValue[2] = 241.6f;
			}

			jSize = j;
			j = 0;

			// 0-100に変換
			for ( i = 0; i < jSize; i++ ) {
				fTemp = Float.valueOf(preparedData[i][1]) - minValue[1];
				preparedData[i][1] = String.valueOf( (int)(fTemp / ( maxValue[1] - minValue[1] ) * 100 ));

				fTemp = Float.valueOf(preparedData[i][2]) - minValue[2];
				preparedData[i][2] = String.valueOf( (int)(fTemp / ( maxValue[2] - minValue[2] ) * 100 ));

//				preparedData[i][3] = preparedData[i][1];
//				preparedData[i][4] = preparedData[i][2];

			}
		}
//		Log.v("debug:max/minValue[1]", String.valueOf(maxValue[1]) + ", " + String.valueOf(minValue[1]));
//		Log.v("debug:max/minValue[2]", String.valueOf(maxValue[2]) + ", " + String.valueOf(minValue[2]));
//		Log.v("debug:minValue[0]", String.valueOf(minValue[0]));
//		Log.v("debug:maxValue[1]", String.valueOf(maxValue[1]));
//		Log.v("debug:minValue[1]", String.valueOf(minValue[1]));
//		Log.v("debug:maxValue[2]", String.valueOf(maxValue[2]));
//		Log.v("debug:minValue[2]", String.valueOf(minValue[2]));
//		Log.v("debug:maxValue[3]", String.valueOf(maxValue[3]));
//		Log.v("debug:minValue[3]", String.valueOf(minValue[3]));


		// "Gp_xr_1m.txt"の処理
		if ( rec2 == null ) return;
		else {
			iSize = rec2.size();
//			Log.v("debug:iSize rec2", String.valueOf(iSize));
//			preparedData = new String[rec2.size()][5];

//			i = j = jSize = 0;
			i = j = 0;
//			Log.v("debug:iSize", String.valueOf(iSize));

			// 最大値・最小値を求める
			// 読み込んだデータから求める
			do {
//				Log.v("debug:i1", String.valueOf(i));

				cell = (String[])rec2.elementAt(i);
//				Log.v("debug:i4", String.valueOf(i));
//				Log.v("debug:cell0", cell[0]);

				// エラーデータは読み飛ばす
				if ( cell.length < 8 || cell[6].equals( "-1.00e+05" ) || cell[7].equals( "-1.00e+05" ) ) {
				//if ( Integer.parseInt(cell[11]) != 0 ) {
					//Log.v("debug:i3", String.valueOf(i));
					i++;
					continue;
				}

//				Log.v("debug:i2", String.valueOf(i));

				// 年月日日時
//				preparedData[j][0] = cell[0] + "/" + cell[1] + "/" + cell[2] + " " + cell[3].substring(0, 2) + ":" + cell[3].substring(2, 4);
//				Log.v("debug:", preparedData[j][0]);

				// Short
				preparedData[j][3] = cell[6];
//				Log.v("debug:j3", preparedData[j][3]);
				nowF[3] = Float.valueOf(cell[6]);

				// Long
				preparedData[j][4] = cell[7];
				nowF[4] = Float.valueOf(cell[7]);

				// 一番最初は現在の値をmaxとminにセットする
				if ( i == 0 ) {
					maxValue[3] = minValue[3] = nowF[3];
					maxValue[4] = minValue[4] = nowF[4];
				}

				if ( maxValue[3] < nowF[3] ) maxValue[3] = nowF[3];
				if ( minValue[3] > nowF[3] ) minValue[3] = nowF[3];
				if ( maxValue[4] < nowF[4] ) maxValue[4] = nowF[4];
				if ( minValue[4] > nowF[4] ) minValue[4] = nowF[4];

				i++;
				j++;

			} while ( i < iSize && j < jSize );
			if ( true ) {
//			if ( false ) {
			  // "SWPC X-ray alerts are issued at the M5 (5x10E-5 Watts/m2)" at http://www.swpc.noaa.gov/rt_plots/xray_5m.html
              maxValue[3] = 0.0005f;
			  minValue[3] = 0.0f;
			  maxValue[4] = 0.0005f;
			  minValue[4] = 0.0f;
    		}

//			Log.v("debug:max/minValue[3]", String.valueOf(maxValue[3]) + ", " + String.valueOf(minValue[3]));
//			Log.v("debug:max/minValue[4]", String.valueOf(maxValue[4]) + ", " + String.valueOf(minValue[4]));
//			jSize = j;
//			j = 0;

			// 0-100に変換
			for ( i = 0; i < j; i++ ) {
				fTemp = Float.valueOf(preparedData[i][3]) - minValue[3];
				preparedData[i][3] = String.valueOf( (int)(fTemp / ( maxValue[3] - minValue[3] ) * 100 ));

				fTemp = Float.valueOf(preparedData[i][4]) - minValue[4];
				preparedData[i][4] = String.valueOf( (int)(fTemp / ( maxValue[4] - minValue[4] ) * 100 ));

//				preparedData[i][3] = preparedData[i][1];
//				preparedData[i][4] = preparedData[i][2];

			}
		}
//		Log.v("debug:DataProvider", "End");
	}

	public String[] get() {
//		time1 = System.currentTimeMillis();
//		Log.v("debug:time1", String.valueOf(time1));

/*

		do {
			i++;
			if ( i >= iSize ) i = 0;
			cell = (String[])rec.elementAt(i);

			// エラーデータ（0以外）は読み飛ばす
		} while ( Integer.parseInt(cell[6]) != 0 );
 */

//		Log.v("debug:i", String.valueOf(i));

/*
		// 年月日日時
		ret[0] = cell[0] + "/" + cell[1] + "/" + cell[2] + " " + cell[3].substring(0, 2) + ":" + cell[3].substring(2, 4);
//		Log.v("debug:", cell[3].substring(0, 2) + ":" + cell[3].substring(2, 4));

		// 0-100に整形
		nowF[1] = Float.valueOf(cell[7]) - minValue[1];
		//ret[0] = (int)( nowF[0] / ( maxValue[0] - minValue[0] ) * 100 );
		ret[1] = String.valueOf( (int)(nowF[1] / ( maxValue[1] - minValue[1] ) * 100 ));

		nowF[2] = Float.valueOf(cell[8]) - minValue[2];
		//ret[1] = (int)( nowF[1] / ( maxValue[1] - minValue[1] ) * 100 );
		ret[2] = String.valueOf( (int)(nowF[2] / ( maxValue[2] - minValue[2] ) * 100 ));

		ret[3] = ret[1];
		ret[4] = ret[2];
 */

/*
		nowF[2] = Float.valueOf(cell[20]) - minValue[2];
		//ret[2] = (int)( nowF[2] / ( maxValue[2] - minValue[2] ) * 100 );
		ret[2] = String.valueOf( (int)(nowF[2] / ( maxValue[2] - minValue[2] ) * 100 ));

		nowF[3] = Float.valueOf(cell[13]) - minValue[3];
		//ret[2] = (int)( nowF[2] / ( maxValue[2] - minValue[2] ) * 100 );
		ret[3] = String.valueOf( (int)(nowF[3] / ( maxValue[3] - minValue[3] ) * 100 ));
 */

//		Log.v("debug:ret", ret[0] + ", " + ret[1] + ", " + ret[2]);
//		Log.v("debug:ret[1]", ret[1]);
//		Log.v("debug:ret[2]", ret[2]);


/*
		Log.v("time1", String.valueOf(time1));
		Log.v("time2", String.valueOf(time2));
		Log.v("2-1", String.valueOf(time2-time1));

		Log.v("temp", String.valueOf(temp));
		Log.v("iSize", String.valueOf(iSize));
*/
		j++;
		if ( j >= jSize ) j = 0;

		return preparedData[j];

	}
}
