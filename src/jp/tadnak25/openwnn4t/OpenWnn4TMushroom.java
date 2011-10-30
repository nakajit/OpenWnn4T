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

import android.app.LauncherActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

/**
 * The Mushroom/Candy caller for OpenWnn IME.
 *
 * @author Copyright (C) 2011  NAKAJI Tadayoshi
 */
public class OpenWnn4TMushroom extends LauncherActivity {

    private static final String ACTION_MUSHROOM = "com.adamrocker.android.simeji.ACTION_INTERCEPT";
    private static final String ACTION_CANDY = "com.adamrocker.android.simeji.ACTION_INJECTION";
    private static final String CATEGORY_MUSHROOM = "com.adamrocker.android.simeji.REPLACE";
    private static final String CATEGORY_CANDY = "com.adamrocker.android.simeji.CANDIDATES";
    public static final String EXTRA_MUSHROOM = "replace_key";
    public static final String EXTRA_CANDY = "candidate_key";
    private static final int REQ_MUSHROOM = 0;
    private static final int REQ_CANDY = 1;

    private String mStroke;
    private boolean mCallingCandy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mStroke = getIntent().getStringExtra(EXTRA_MUSHROOM);
        mCallingCandy = getIntent().getBooleanExtra(EXTRA_CANDY, false);

        super.onCreate(savedInstanceState);

        if (mCallingCandy) {
            setTitle(R.string.select_candy);
        } else {
            setTitle(R.string.select_mushroom);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = intentForPosition(position);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if (mCallingCandy) {
            startActivityForResult(intent, REQ_CANDY);
        } else {
            intent.putExtra(EXTRA_MUSHROOM, mStroke);
            startActivityForResult(intent, REQ_MUSHROOM);
        }
    }

    @Override
    protected Intent getTargetIntent() {
        Intent intent = null;
        if (mCallingCandy) {
            intent = new Intent(ACTION_CANDY);
            intent.addCategory(CATEGORY_CANDY);
        } else {
            intent = new Intent(ACTION_MUSHROOM);
            intent.addCategory(CATEGORY_MUSHROOM);
        }
        return intent;
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
}
