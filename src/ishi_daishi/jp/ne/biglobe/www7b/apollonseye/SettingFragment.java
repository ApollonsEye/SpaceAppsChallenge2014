package ishi_daishi.jp.ne.biglobe.www7b.apollonseye;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import ishi_daishi.jp.ne.biglobe.www7b.apollonseye.R;

public class SettingFragment extends PreferenceFragment implements OnPreferenceClickListener,OnPreferenceChangeListener{
	private static final String TAG = SettingFragment.class.getCanonicalName();
	private static final boolean DEBUG = true;
	
	/**
	 * public field
	 */
	public static final String ACTION_FILE_DOWNLOAD_START = TAG + ".ACTION_FILE_DOWNLOAD_START";
	
	/**
	 * private field
	 */
	private Preference mFileDownload = null;
	private EditTextPreference mInterval = null;

	public static SettingFragment newInstance() {
		return new SettingFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		String key = null;
		
		// File Download
		key = getString(R.string.key_file_download);
		mFileDownload = (Preference)findPreference(key);
		
		// Interval time
		key = getString(R.string.key_interval_time);
		mInterval = (EditTextPreference)findPreference(key);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		// File Download
		if(mFileDownload != null) {
			mFileDownload.setOnPreferenceClickListener(this);	// click listener
		}
		
		// Interval time
		if(mInterval != null) {
			mInterval.setOnPreferenceChangeListener(this);	// change value listener
			mInterval.setSummary(mInterval.getText());		// set summary
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		// File Download
		if(mFileDownload != null) {
			mFileDownload.setOnPreferenceClickListener(null);	// click listener
		}
		
		// Interval time
		if(mInterval != null) {
			mInterval.setOnPreferenceChangeListener(null);	// change value listener
		}
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		boolean ret = false;
		String key = preference.getKey();
		
		if(DEBUG) {
			Log.d(TAG, "key:" + key);
		}
		
		// File DownLoad
		if(key.equals(getString(R.string.key_file_download))) {
			Intent intent = new Intent();
			intent.setAction(ACTION_FILE_DOWNLOAD_START);
			LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
			ret = true;
		}
		
		return ret;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		boolean ret = true;	// true:commit
		String key = preference.getKey();
		
		// Interval time
		if(key.equals(getString(R.string.key_interval_time))) {
			try {
				int value = Integer.parseInt(newValue.toString());
				
				// input value check
				if(value > 0) {
					preference.setSummary(newValue.toString());
				} else {
					ret = false;
				}
			} catch (NumberFormatException e) {
				ret = false;
				e.printStackTrace();
			}
		}
		
		return ret;
	}

}
