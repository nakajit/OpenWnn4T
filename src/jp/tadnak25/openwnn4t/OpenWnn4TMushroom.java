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
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Collections;
import java.util.List;

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
    public static final String EXTRA_MUSHROOM = "replace_key";
    public static final String EXTRA_CANDY = "candidate_key";
    private static final int REQ_MUSHROOM = 0;
    private static final int REQ_CANDY = 1;

    private AlertDialog mOptionsDialog;
    private String mStroke;
    private boolean mCallingCandy;
    private Intent mIntent;
    private PackageManager mPackageManager;

    private class LauncherAdapter extends android.widget.ArrayAdapter {
        private int mIconSize;
        private int mTextViewId;

        public LauncherAdapter(Context context, int textViewResourceId, List<ResolveInfo> objects) {
            super(context, textViewResourceId, objects);
            mTextViewId = textViewResourceId;
            mIconSize = (int)context.getResources().getDimension(android.R.dimen.app_icon_size);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ResolveInfo item = (ResolveInfo)getItem(position);

            CharSequence label = item.loadLabel(mPackageManager);
            ComponentInfo ci = item.activityInfo;
            if (ci == null) ci = item.serviceInfo;
            if (label == null && ci != null) {
                label = ci.name;
            }

            Drawable icon = item.loadIcon(mPackageManager);
            icon.setBounds(0, 0, mIconSize, mIconSize);

            TextView view = (TextView)getLayoutInflater().inflate(mTextViewId, parent, false);
            view.setCompoundDrawables(icon, null, null, null);
            view.setText(label);
            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStroke = getIntent().getStringExtra(EXTRA_MUSHROOM);
        mCallingCandy = getIntent().getBooleanExtra(EXTRA_CANDY, false);
        mPackageManager = getPackageManager();
        mIntent = getTargetIntent();
        showOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String result = null;
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_MUSHROOM) {
                result = data.getStringExtra(EXTRA_MUSHROOM);
                OpenWnn4T.setResultMushroom(new WnnWord(result, mStroke));
            } else if (requestCode == REQ_CANDY) {
                result = data.getStringExtra(EXTRA_CANDY);
                OpenWnn4T.setResultCandy(result);
            }
            OpenWnnJAJP.getInstance().callbackMushroom();
        }
        finish();
    }

    private void showOptionsMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle((mCallingCandy)? R.string.select_candy: R.string.select_mushroom);
        builder.setCancelable(true);
        builder.setOnCancelListener(this);
        builder.setNegativeButton(android.R.string.cancel, this);
        List<ResolveInfo> list = onQueryPackageManager(mIntent);
        final LauncherAdapter adapter = new LauncherAdapter(this, android.R.layout.select_dialog_item, list);
        builder.setAdapter(adapter,
                new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface di, int position) {
                di.dismiss();
                ResolveInfo ri = (ResolveInfo)adapter.getItem(position);
                launchActivity(mIntent, ri.activityInfo);
            }
        });
        mOptionsDialog = builder.create();
        mOptionsDialog.show();
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
    }

    private void launchActivity(Intent intent, ComponentInfo ai) {
        if (ai == null) return;
        if (!mCallingCandy) {
            intent.putExtra(EXTRA_MUSHROOM, mStroke);
        }
        intent.setClassName(ai.packageName, ai.name);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, (mCallingCandy)? REQ_CANDY: REQ_MUSHROOM);
    }

    protected Intent getTargetIntent() {
        Intent intent = new Intent();
        if (mCallingCandy) {
            intent.setAction(ACTION_CANDY);
            intent.addCategory(CATEGORY_CANDY);
        } else {
            intent.setAction(ACTION_MUSHROOM);
            intent.addCategory(CATEGORY_MUSHROOM);
        }
        return intent;
    }

    protected List<ResolveInfo> onQueryPackageManager(Intent queryIntent) {
        List<ResolveInfo> resolveInfo = mPackageManager.queryIntentActivities(queryIntent, 0);
        Collections.sort(resolveInfo, new ResolveInfo.DisplayNameComparator(mPackageManager));
        return resolveInfo;
    }

}
