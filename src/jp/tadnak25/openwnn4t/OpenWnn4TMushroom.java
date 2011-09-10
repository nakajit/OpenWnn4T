/*
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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;

/**
 * The Mushroom/Candy caller for OpenWnn IME.
 *
 * @author Copyright (C) 2011  NAKAJI Tadayoshi
 */
public class OpenWnn4TMushroom extends Activity
        implements DialogInterface.OnCancelListener, DialogInterface.OnClickListener {

    private static final String ACTION_MUSHROOM = "com.adamrocker.android.simeji.ACTION_INTERCEPT";
    private static final String ACTION_CANDY = "com.adamrocker.android.simeji.ACTION_INJECTION";
    private static final String CATEGORY_MUSHROOM = "com.adamrocker.android.simeji.REPLACE";
    private static final String CATEGORY_CANDY = "com.adamrocker.android.simeji.CANDIDATES";
    private static final String EXTRA_MUSHROOM = "replace_key";
    private static final String EXTRA_CANDY = "candidate_key";
    private static final String ICON_MUSHROOM = "\u24A8 ";
    private static final String ICON_CANDY = "\u249E ";
    private static final int REQ_MUSHROOM = 0;
    private static final int REQ_CANDY = 1;

    // Contextual menu positions
    private static final int POS_METHOD = 0;
    private static final int POS_SETTINGS = 1;

    private AlertDialog mOptionsDialog;
    private String mStroke;
    private boolean isShowCandidates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStroke = getIntent().getStringExtra(EXTRA_MUSHROOM);
        showOptionsMenu();
        isShowCandidates = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String result = null;
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_MUSHROOM) {
                result = data.getStringExtra(EXTRA_MUSHROOM);
                OpenWnn4T.setResultMushroom(new WnnWord(result, mStroke));
                finish();
            } else if (requestCode == REQ_CANDY) {
                result = data.getStringExtra(EXTRA_CANDY);
                showCandidates(result);
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        if (isShowCandidates && !isFinishing()) {
            mOptionsDialog.dismiss();
            finish();
            isShowCandidates = false;
        }
        super.onPause();
    }

    private void showOptionsMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.preference_dictionary_menu);
        builder.setCancelable(true);
        builder.setOnCancelListener(this);
        builder.setNegativeButton(android.R.string.cancel, this);
        PackageManager pm = getPackageManager();
        final List<ResolveInfo> resolveInfoMushroom = getMushrooms(pm);
        final List<ResolveInfo> resolveInfoCandy = getCandies(pm);
        final int mushroomCount = resolveInfoMushroom.size();
        final int candyCount = resolveInfoCandy.size();
        final int totalCount = mushroomCount + candyCount;
        CharSequence[] optionList = new CharSequence[totalCount + 2];
        for (int i = 0; i < mushroomCount; i++) {
            optionList[i] = ICON_MUSHROOM + resolveInfoMushroom.get(i).loadLabel(pm);
        }
        for (int i = 0; i < candyCount; i++) {
            optionList[mushroomCount + i] = ICON_CANDY + resolveInfoCandy.get(i).loadLabel(pm);
        }
        optionList[totalCount + POS_METHOD] = getString(R.string.select_input_method);
        optionList[totalCount + POS_SETTINGS] = getString(R.string.preference_ime_setting_app);
        builder.setItems(optionList,
                new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface di, int position) {
                di.dismiss();
                if (position < mushroomCount) {
                    launchMushroom(resolveInfoMushroom.get(position).activityInfo);
                } else if (position < totalCount) {
                    launchCandy(resolveInfoCandy.get(position - mushroomCount).activityInfo);
                } else if (position == totalCount + POS_METHOD) {
                    showInputMethodPicker();
                    finish();
                } else if (position == totalCount + POS_SETTINGS) {
                    launchSettings();
                    finish();
                }
            }
        });
        mOptionsDialog = builder.create();
        mOptionsDialog.show();
    }

    private void showCandidates(String candidatesString) {
        JSONArray candidates = null;
        try {
            candidates = new JSONArray(candidatesString);
        } catch (Exception e) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.user_dictionary_list_words);
        builder.setCancelable(true);
        builder.setOnCancelListener(this);
        builder.setNegativeButton(android.R.string.cancel, this);
        final int totalCount = candidates.length();
        final CharSequence[] optionList = new CharSequence[totalCount];
        for (int i = 0; i < totalCount; i++) {
            try {
                optionList[i] = candidates.getString(i);
            } catch (Exception e) {
                optionList[i] = "";
            }
        }
        builder.setItems(optionList,
                new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface di, int position) {
                di.dismiss();
                OpenWnn4T.setResultMushroom(new WnnWord((String)optionList[position], ""));
                finish();
                isShowCandidates = false;
            }
        });
        mOptionsDialog = builder.create();
        mOptionsDialog.show();
        isShowCandidates = true;
    }

    /** @see android.content.DialogInterface.OnClickListener#onClick */
    public void onClick(DialogInterface di, int position) {
        /* for Negative button */
        onCancel(di);
    }

    /** @see android.content.DialogInterface.OnCancelListener#onCancel */
    public void onCancel(DialogInterface di) {
        di.dismiss();
        finish();
        isShowCandidates = false;
    }

    private void launchMushroom(ActivityInfo ai) {
        Intent intent = new Intent(ACTION_MUSHROOM);
        intent.addCategory(CATEGORY_CANDY);
        intent.setClassName(ai.packageName, ai.name);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(EXTRA_MUSHROOM, mStroke);
        startActivityForResult(intent, REQ_MUSHROOM);
    }

    private void launchCandy(ActivityInfo ai) {
        Intent intent = new Intent(ACTION_CANDY);
        intent.addCategory(CATEGORY_CANDY);
        intent.setClassName(ai.packageName, ai.name);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, REQ_CANDY);
    }

    private void launchSettings() {
        Intent intent = new Intent();
        intent.setClass(OpenWnn4TMushroom.this, OpenWnnControlPanelJAJP.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void showInputMethodPicker() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .showInputMethodPicker();
    }

    private List<ResolveInfo> getMushrooms(PackageManager pm) {
        Intent intent = new Intent(ACTION_MUSHROOM);
        intent.addCategory(CATEGORY_MUSHROOM);
        List<ResolveInfo> resolveInfo = pm.queryIntentActivities(intent, 0);
        Collections.sort(resolveInfo, new ResolveInfo.DisplayNameComparator(pm));
        return resolveInfo;
    }

    private List<ResolveInfo> getCandies(PackageManager pm) {
        Intent intent = new Intent(ACTION_CANDY);
        intent.addCategory(CATEGORY_CANDY);
        List<ResolveInfo> resolveInfo = pm.queryIntentActivities(intent, 0);
        Collections.sort(resolveInfo, new ResolveInfo.DisplayNameComparator(pm));
        return resolveInfo;
    }

    public static Intent createIntentMenu(Context context, String transferText) {
        Intent intent = new Intent();
        intent.setClass(context, OpenWnn4TMushroom.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(EXTRA_MUSHROOM, transferText);
        return intent;
    }

}
