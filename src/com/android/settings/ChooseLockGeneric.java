/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.android.settings;

import com.android.internal.widget.LockPatternUtils;

import android.app.Activity;
import android.app.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ChooseLockGeneric extends Activity {
    private ChooseLockSettingsHelper mChooseLockSettingsHelper;
    DevicePolicyManager mDPM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        mChooseLockSettingsHelper = new ChooseLockSettingsHelper(this);
        
        final LockPatternUtils lockPatternUtils = mChooseLockSettingsHelper.utils();
        
        int quality = getIntent().getIntExtra(LockPatternUtils.PASSWORD_TYPE_KEY, -1);
        if (quality == -1) {
            quality = lockPatternUtils.getPasswordMode();
        }
        int minQuality = mDPM.getPasswordQuality(null);
        if (quality < minQuality) {
            quality = minQuality;
        }
        if (quality >= DevicePolicyManager.PASSWORD_QUALITY_NUMERIC) {
            int minLength = mDPM.getPasswordMinimumLength(null);
            if (minLength < 4) {
                minLength = 4;
            }
            final int maxLength = mDPM.getPasswordMaximumLength(quality);
            Intent intent = new Intent().setClass(this, ChooseLockPassword.class);
            intent.putExtra(LockPatternUtils.PASSWORD_TYPE_KEY, quality);
            intent.putExtra(ChooseLockPassword.PASSWORD_MIN_KEY, minLength);
            intent.putExtra(ChooseLockPassword.PASSWORD_MAX_KEY, maxLength);
            startActivity(intent);
        } else {
            boolean showTutorial = !lockPatternUtils.isPatternEverChosen();
            Intent intent = new Intent();
            intent.setClass(this, showTutorial
                    ? ChooseLockPatternTutorial.class
                    : ChooseLockPattern.class);
            intent.putExtra("key_lock_method", "pattern");
            startActivity(intent);
        }
        finish();
    }
}
