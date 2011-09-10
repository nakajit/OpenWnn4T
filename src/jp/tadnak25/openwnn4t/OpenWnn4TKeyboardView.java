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

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.Keyboard.Key;
import android.util.AttributeSet;
import android.util.Log;

/**
 * The OpenWnn IME's KeyboardView class.
 *
 * @author Copyright (C) 2011  NAKAJI Tadayoshi
 */
public class OpenWnn4TKeyboardView extends KeyboardView {

    public OpenWnn4TKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OpenWnn4TKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
    }

    @Override
    protected boolean onLongPress(Key key) {
        int primaryCode = key.codes[0];
        if (primaryCode == DefaultSoftKeyboard.KEYCODE_QWERTY_EMOJI ||
                primaryCode == DefaultSoftKeyboard.KEYCODE_JP12_EMOJI ||
                primaryCode == DefaultSoftKeyboard.KEYCODE_EISU_KANA ) {
            getOnKeyboardActionListener().onKey(DefaultSoftKeyboard.KEYCODE_LIST_MUSHROOM, key.codes);
            return true;
        }
        return super.onLongPress(key);
    }

}
