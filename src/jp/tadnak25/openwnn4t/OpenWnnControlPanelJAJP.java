/*
 * Copyright (C) 2008,2009  OMRON SOFTWARE Co., Ltd.
 * Copyright (C) 2011  NAKAJI Tadayoshi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.tadnak25.openwnn4t;

import android.content.*;
import android.os.Bundle;
import android.preference.*;

/**
 * The control panel preference class for Japanese IME.
 *
 * @author Copyright (C) 2009 OMRON SOFTWARE CO., LTD.  All Rights Reserved.
 */
public class OpenWnnControlPanelJAJP extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String PREF_5LINES_KEY = "5lines";
    private static final String PREF_SETTINGS_KEY = "keyboard_locale";
    private static final String PREF_USE_HARDKEYBOARD_KEY = "use_hardkeyboard";
    public static final int PREF_KEYBOARD_LOCALE_DEFAULT = R.string.preference_keyboard_locale_default;

    private ListPreference mSettingsKeyPreference;

    /** @see android.preference.PreferenceActivity#onCreate */
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (OpenWnnJAJP.getInstance() == null) {
            new OpenWnnJAJP(this);
        }

        addPreferencesFromResource(R.xml.openwnn_pref_ja);

        mSettingsKeyPreference = (ListPreference) findPreference(PREF_SETTINGS_KEY);
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    /** @see android.preference.PreferenceActivity#onResume */
    @Override public void onResume() {
        super.onResume();
        updateSettingsKeySummary();
    }

    /** @see android.preference.PreferenceActivity#onDestroy */
    @Override protected void onDestroy() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);
        super.onDestroy();
    }

    /** @see android.preference.SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged */
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        updateSettingsKeySummary();
    }

    private void updateSettingsKeySummary() {
        if (mSettingsKeyPreference.getValue().equals("0") || mSettingsKeyPreference.getValue().equals("1")) {
            mSettingsKeyPreference.setValue(getResources().getString(PREF_KEYBOARD_LOCALE_DEFAULT));
        }
        mSettingsKeyPreference.setSummary(
                getResources().getStringArray(R.array.preference_keyboard_locale)
                [mSettingsKeyPreference.findIndexOfValue(mSettingsKeyPreference.getValue())]);
    }

    /**
     * load 5lines preferences
     * <br>
     * @param context  The context
     */
    public static boolean is5Lines(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(PREF_5LINES_KEY, false);
    }

    /**
     * load keyboard locale preferences
     * <br>
     * @param context  The context
     */
    public static String getKeyboardLocale(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(PREF_SETTINGS_KEY, context.getResources().getString(PREF_KEYBOARD_LOCALE_DEFAULT));
    }

    /**
     * load use hardware keyboard preferences
     * <br>
     * @param context  The context
     */
    public static boolean isUseHwKeyboard(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(PREF_USE_HARDKEYBOARD_KEY, false);
    }
}
