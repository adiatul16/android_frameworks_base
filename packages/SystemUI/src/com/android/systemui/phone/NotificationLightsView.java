/*
* Copyright (C) 2019-2021 crDroid Android Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package com.android.systemui.phone;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.android.systemui.R;

public class NotificationLightsView extends RelativeLayout {

    private View mNotificationAnimView;
    private ValueAnimator mLightAnimator;
    private boolean mPulsing;

    public NotificationLightsView(Context context) {
        this(context, null);
    }

    public NotificationLightsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationLightsView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NotificationLightsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Log.e("NotificationLightsView", "new");
    }

    private Runnable mLightUpdate = new Runnable() {
        @Override
        public void run() {
            Log.e("NotificationLightsView", "run");
            animateNotification();
        }
    };

    public void setPulsing(boolean pulsing) {
        if (mPulsing == pulsing) {
            return;
        }
        mPulsing = pulsing;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Log.e("NotificationLightsView", "draw");
    }

    public void animateNotification() {
        int color = Settings.Secure.getIntForUser(mContext.getContentResolver(),
                Settings.Secure.PULSE_AMBIENT_LIGHT_COLOR, 0xFF3980FF,
                UserHandle.USER_CURRENT);
        int duration = Settings.Secure.getIntForUser(mContext.getContentResolver(),
                Settings.Secure.PULSE_AMBIENT_LIGHT_DURATION, 2,
                UserHandle.USER_CURRENT) * 1000;
        int layout = Settings.Secure.getIntForUser(mContext.getContentResolver(),
                Settings.Secure.PULSE_AMBIENT_LIGHT_LAYOUT, 0,
                UserHandle.USER_CURRENT);
        StringBuilder sb = new StringBuilder();
        sb.append("animateNotification color ");
        sb.append(Integer.toHexString(color));
        Log.e("NotificationLightsView", sb.toString());
        ImageView leftViewSolid = (ImageView) findViewById(R.id.notification_animation_left_solid);
        ImageView leftViewFaded = (ImageView) findViewById(R.id.notification_animation_left_faded);
        leftViewSolid.setColorFilter(color);
        leftViewFaded.setColorFilter(color);
        leftViewSolid.setVisibility(layout == 0 ? View.VISIBLE : View.GONE);
        leftViewFaded.setVisibility(layout == 1 ? View.VISIBLE : View.GONE);
        ImageView rightViewSolid = (ImageView) findViewById(R.id.notification_animation_right_solid);
        ImageView rightViewFaded = (ImageView) findViewById(R.id.notification_animation_right_faded);
        rightViewSolid.setColorFilter(color);
        rightViewFaded.setColorFilter(color);
        rightViewSolid.setVisibility(layout == 0 ? View.VISIBLE : View.GONE);
        rightViewFaded.setVisibility(layout == 1 ? View.VISIBLE : View.GONE);
        mLightAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 2.0f});
        mLightAnimator.setDuration(duration);
        mLightAnimator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.e("NotificationLightsView", "onAnimationUpdate");
                float progress = ((Float) animation.getAnimatedValue()).floatValue();
                leftViewSolid.setScaleY(progress);
                leftViewFaded.setScaleY(progress);
                rightViewSolid.setScaleY(progress);
                rightViewFaded.setScaleY(progress);
                float alpha = 1.0f;
                if (progress <= 0.3f) {
                    alpha = progress / 0.3f;
                } else if (progress >= 1.0f) {
                    alpha = 2.0f - progress;
                }
                leftViewSolid.setAlpha(alpha);
                leftViewFaded.setAlpha(alpha);
                rightViewSolid.setAlpha(alpha);
                rightViewFaded.setAlpha(alpha);
            }
        });
        Log.e("NotificationLightsView", "start");
        mLightAnimator.start();
    }
}
