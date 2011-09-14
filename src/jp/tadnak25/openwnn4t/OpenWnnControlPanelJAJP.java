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
    private static final String PREF_SKINS_KEY = "keyboard_skin";
    private static final String PREF_KEY_HEIGHT_RATIO = "key_height_ratio";
    public static final int PREF_KEYBOARD_LOCALE_DEFAULT = R.string.preference_keyboard_locale_default;
    public static final int PREF_KEY_HEIGHT_RATIO_DEFAULT = R.string.preference_key_height_ratio_100;

    private ListPreference mSettingsKeyPreference;
    private ListPreference mSkinsPreference;
    private EditTextPreference mKeyHeightRatioPreference;

    /** @see android.preference.PreferenceActivity#onCreate */
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (OpenWnnJAJP.getInstance() == null) {
            new OpenWnnJAJP(this);
        }

        addPreferencesFromResource(R.xml.openwnn_pref_ja);

        mSettingsKeyPreference = (ListPreference) findPreference(PREF_SETTINGS_KEY);
        mSkinsPreference = (ListPreference) findPreference(PREF_SKINS_KEY);
        mKeyHeightRatioPreference = (EditTextPreference) findPreference(PREF_KEY_HEIGHT_RATIO);
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    /** @see android.preference.PreferenceActivity#onResume */
    @Override public void onResume() {
        super.onResume();
        updateSettingsKeySummary();
    }

    /** @see android.preference.PreferenceActivity#onStop */
    @Override public void onStop() {
        OpenWnnJAJP wnn = OpenWnnJAJP.getInstance();
        int code = OpenWnnEvent.CHANGE_INPUT_VIEW;
        OpenWnnEvent ev = new OpenWnnEvent(code);
        try {
            wnn.onEvent(ev);
        } catch (Exception ex) {
        }
        if (!isFinishing()) {
            finish();
        }
        super.onStop();
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
        mSkinsPreference.setSummary(
                getResources().getStringArray(R.array.keyboard_skin)
                [mSkinsPreference.findIndexOfValue(mSkinsPreference.getValue())]);
        String ratioText = mKeyHeightRatioPreference.getText();
        int ratio = (ratioText.length() == 0)? 100: Integer.parseInt(ratioText);
        if (ratio == 100 || ratio == 0) {
            ratioText = getResources().getString(PREF_KEY_HEIGHT_RATIO_DEFAULT);
        } else {
            ratioText = ratioText + "%";
        }
        mKeyHeightRatioPreference.setSummary(ratioText);
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
        String locale = pref.getString(PREF_SETTINGS_KEY, context.getResources().getString(PREF_KEYBOARD_LOCALE_DEFAULT));
        if (locale.equals("0") || locale.equals("1")) {
            pref.edit().putString(PREF_SETTINGS_KEY, context.getResources().getString(PREF_KEYBOARD_LOCALE_DEFAULT));
        }
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

    /**
     * load ratio for key height preferences
     * <br>
     * @param context  The context
     */
    public static int getKeyHeightRatio(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String ratioText = pref.getString(PREF_KEY_HEIGHT_RATIO, "100");
        int ratio = (ratioText.length() == 0)? 100: Integer.parseInt(ratioText);
        return (ratio == 0)? 100: ratio;
    }
}
