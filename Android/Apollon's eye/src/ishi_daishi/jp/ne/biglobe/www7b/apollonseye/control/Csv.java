package ishi_daishi.jp.ne.biglobe.www7b.apollonseye.control;

// revised of http://digitechlog.com/2009/05/11/how-to-split-csv-file-in-java-with-split-function.html

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import android.util.Log;

    /**
    * CSV、タブ区切り読み書き
    */
    public class Csv {

        /**
         * Inputstreamから読み込み
         * @param is
         * @return
         */
        public static Vector readInputStream(InputStream is) {
            String s = null;        //    入力文字列

            InputStreamReader reader = new InputStreamReader(is);
            StringBuilder builder = new StringBuilder();
            char[] buf = new char[1024];
            int numRead;
            try {
                while (0 <= (numRead = reader.read(buf))) {
                    builder.append(buf, 0, numRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            	try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
            
            // 文字列に変換
            s = builder.toString();
            return read(s);
        }

        /**
         * ファイル読み込み
         * @param filename
         * @return
         */
        public static Vector readFile(String filename)
        {
            String s = null;        //    入力文字列
            Vector ret = null;

            //    ファイル読み込み
            FileInputStream fi = null;
            try {
                File f = new File(filename);
                byte[] b = new byte[(int) f.length()];
//                FileInputStream fi = new FileInputStream(f);
                fi = new FileInputStream(f);
                fi.read(b);
                s = new String(b);
            } catch(Exception e) {
//                return null;
            	ret = null;
            } finally {
            	try {
					fi.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
            return read(s);
        }

        /**
         * 文字列をＶｅｃｔｏｒに入れる
         * @param filename
         * @return
         */
        public static Vector read(String data) {
            Vector rec = new Vector();
            if ( rec    ==    null ) {
                return null;
            }

            data = data.replaceAll("\r","");    // ￥rがあったとき対策
            String[] strrec = data.split("\n");
            int i;
            for (i = 0 ; i < strrec.length ; i ++ ) {
            	// 先頭に#か:があったら飛ばす
            	if ( strrec[i].indexOf("#") == 0 || strrec[i].indexOf(":") == 0 ) {
//  				  Log.v("debug:csv", String.valueOf(strrec[i].indexOf("#")));
				  continue;
            	}

                // 1文字以上の" "を\tに置き換える
            	strrec[i] = strrec[i].replaceAll(" +","\t");
//			    Log.v("debug:csv", strrec[i]);

            	// 区切り文字
            	rec.add(strrec[i].split("\t"));
            }

            return    rec;
        }
    }
