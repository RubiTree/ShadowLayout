/*
 * Copyright (c) 2017 SaltedFish Captain
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

package com.saltedfishcaptain.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import static android.R.attr.bitmap;
import static android.R.attr.radius;

/**
 * Description:
 * <p>
 * Attention:
 * <p>
 * Created by SaltedFish Captain ; On 2017-06-05.
 */

public class SuperShadowLayout extends FrameLayout {
    private int shadowColor; // 阴影颜色
    private float shadowCornerRadius; // 阴影实体边缘的圆角半径
    private float shadowBlurRadius; // 投影半径，对应PS阴影设置中的大小，是阴影渐变区的半径，上下左右都会增加区域，0会导致没有阴影
    private float shadowOffsetX; // 阴影X方向的偏移
    private float shadowOffsetY; // 阴影Y方向的偏移
    private float shadowPaddingLeft; // 阴影区域左侧的缩进
    private float shadowPaddingRight; // 阴影区域右侧的缩进
    private float shadowPaddingTop; // 阴影区域上方的缩进
    private float shadowPaddingBottom; // 阴影区域下方的缩进

    private boolean invalidateShadowOnSizeChanged; // size改变很快但改变得很小，对阴影的精确性要求不高但对性能要求较高时，可以把这个设置为false，并且手动调用invalidateShadow来刷新shadow
    private boolean forceInvalidateShadow = false;

    private Path mCornerShadowPath;
    private Paint mCornerShadowPaint;
    private Paint mEdgeShadowPaint;
    private Paint roundRectPaint;

    public SuperShadowLayout(Context context) {
        super(context);
        initView(context, null);
    }

    public SuperShadowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public SuperShadowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        initAttributes(context, attrs);

        int paddingLeft = (int) (shadowBlurRadius - shadowOffsetX - shadowPaddingLeft);
        int paddingRight = (int) (shadowBlurRadius + shadowOffsetX - shadowPaddingRight);

        int paddingTop = (int) (shadowBlurRadius - shadowOffsetY - shadowPaddingTop);
        int paddingBottom = (int) (shadowBlurRadius + shadowOffsetY - shadowPaddingBottom);

        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        initPaint();
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        if (attrs == null) return;

        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.ShadowLayout, 0, 0);

        shadowCornerRadius = attr.getDimension(R.styleable.ShadowLayout_shadow_corner_radius,
                getResources().getDimension(R.dimen.shadow_layout_default_corner_radius));
        shadowBlurRadius = attr.getDimension(R.styleable.ShadowLayout_shadow_blur_radius,
                getResources().getDimension(R.dimen.shadow_layout_default_shadow_radius));
        shadowColor = attr.getColor(R.styleable.ShadowLayout_shadow_color,
                getResources().getColor(R.color.shadow_layout_default_shadow_color));
        shadowOffsetX = attr.getDimension(R.styleable.ShadowLayout_shadow_offset_x, 0);
        shadowOffsetY = attr.getDimension(R.styleable.ShadowLayout_shadow_offset_y, 0);
        shadowPaddingLeft = attr.getDimension(R.styleable.ShadowLayout_shadow_padding_left, 0);
        shadowPaddingRight = attr.getDimension(R.styleable.ShadowLayout_shadow_padding_right, 0);
        shadowPaddingTop = attr.getDimension(R.styleable.ShadowLayout_shadow_padding_top, 0);
        shadowPaddingBottom = attr.getDimension(R.styleable.ShadowLayout_shadow_padding_bottom, 0);
        invalidateShadowOnSizeChanged = attr.getBoolean(R.styleable.ShadowLayout_invalidate_shadow_on_size_changed, true);

        attr.recycle();
    }

    private void initPaint() {
        roundRectPaint = new Paint();
        mCornerShadowPaint = new Paint();
        mEdgeShadowPaint = new Paint();

        roundRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        roundRectPaint.setColor(shadowColor);
//        roundRectPaint.setStrokeWidth(5);

        mCornerShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mCornerShadowPaint.setStyle(Paint.Style.FILL);

        mEdgeShadowPaint = new Paint(mCornerShadowPaint);
        mEdgeShadowPaint.setAntiAlias(false);
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0 && (getBackground() == null || invalidateShadowOnSizeChanged || forceInvalidateShadow)) {
            forceInvalidateShadow = false;
            setBackgroundCompat(w, h);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (forceInvalidateShadow) {
            forceInvalidateShadow = false;
            setBackgroundCompat(right - left, bottom - top);
        }
    }

    /*-------------------------------------------------*/

    private void setBackgroundCompat(int w, int h) {

        /**
         * Cause {@link Paint#setShadowLayer(float, float, float, int)} can not be previewed in xml
         */
        if (isInEditMode()) {
            setBackgroundColor(shadowColor);
            return;
        }

        w= toSmallEvenNum(w);
        h= toSmallEvenNum(h);

        Bitmap bitmap = createShadowBitmap(w, h);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(drawable);
        } else {
            setBackground(drawable);
        }
    }

    private int toSmallEvenNum(int num) {
        if (num % 2 != 0) {
            return num - 1;
        }
        return num;
    }

    private Bitmap createShadowBitmap(int shadowWidth, int shadowHeight) {

        Bitmap output = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        RectF innerShadowRect = new RectF(
                shadowBlurRadius,
                shadowBlurRadius,
                shadowWidth - shadowBlurRadius,
                shadowHeight - shadowBlurRadius);

        buildShadowCorners();


        canvas.translate(shadowWidth / 2, shadowHeight / 2); // ?
//        canvas.translate(shadowWidth/2+shadowBlurRadius,shadowHeight/2+shadowBlurRadius); // ?

        drawShadow(canvas, innerShadowRect);

        canvas.translate(-shadowWidth / 2, -shadowHeight / 2);

        canvas.drawRoundRect(innerShadowRect, shadowCornerRadius, shadowCornerRadius, roundRectPaint);

        return output;
    }

    /*--------------------------------------------------------------------------------------------*/

    private void buildShadowCorners() {
        mCornerShadowPath = new Path();

        RectF innerBounds = new RectF(-shadowCornerRadius, -shadowCornerRadius, shadowCornerRadius, shadowCornerRadius);
        RectF outerBounds = new RectF(innerBounds);
        outerBounds.inset(-shadowBlurRadius, -shadowBlurRadius);

        mCornerShadowPath.setFillType(Path.FillType.EVEN_ODD);

        mCornerShadowPath.moveTo(-shadowCornerRadius, 0);
        mCornerShadowPath.rLineTo(-shadowBlurRadius, 0);
        mCornerShadowPath.arcTo(outerBounds, 180f, 90f, false); // outer arc
        mCornerShadowPath.arcTo(innerBounds, 270f, -90f, false); // inner arc
        mCornerShadowPath.close();

        int mShadowEndColor = transparentColor(shadowColor);
        float startRatio = shadowCornerRadius / (shadowCornerRadius + shadowBlurRadius);
        mCornerShadowPaint.setShader(new RadialGradient(0, 0, shadowCornerRadius + shadowBlurRadius,
                new int[]{shadowColor, shadowColor, mShadowEndColor},
                new float[]{0f, startRatio, 1f}, Shader.TileMode.CLAMP));


        mEdgeShadowPaint.setShader(new LinearGradient(0, -shadowCornerRadius + shadowBlurRadius, 0,
                -shadowCornerRadius - shadowBlurRadius,
                new int[]{shadowColor, shadowColor, mShadowEndColor},
                new float[]{0f, .5f, 1f}, Shader.TileMode.CLAMP));
        mEdgeShadowPaint.setAntiAlias(false);
    }

    private int transparentColor(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(0, red, green, blue);
    }

    private void drawShadow(Canvas canvas, RectF innerShadowRect) {
        float horizontalEdgesLength = innerShadowRect.width() - 2 * shadowCornerRadius;
        float verticalEdgesLength = innerShadowRect.height() - 2 * shadowCornerRadius;

        final boolean drawHorizontalEdges = horizontalEdgesLength > 0;
        final boolean drawVerticalEdges = verticalEdgesLength > 0;

//        float horizontalHalfEdgesLength = (int) (horizontalEdgesLength / 2 + .5f);
//        float verticalHalfEdgesLength = (int) (verticalEdgesLength / 2 + .5f);
//
//        // LT
//        int saved = canvas.save();
//        canvas.translate(-horizontalHalfEdgesLength, -verticalHalfEdgesLength);
//        drawCornerAndRightEdgeShadow(canvas, horizontalHalfEdgesLength * 2, drawHorizontalEdges);
//        canvas.restoreToCount(saved);
//
//        // RB
//        saved = canvas.save();
//        canvas.translate(horizontalHalfEdgesLength, verticalHalfEdgesLength);
//        canvas.rotate(180f);
//        drawCornerAndRightEdgeShadow(canvas, horizontalHalfEdgesLength * 2, drawHorizontalEdges);
//        canvas.restoreToCount(saved);
//
//        // LB
//        saved = canvas.save();
//        canvas.translate(-horizontalHalfEdgesLength, verticalHalfEdgesLength);
//        canvas.rotate(270f);
//        drawCornerAndRightEdgeShadow(canvas, verticalHalfEdgesLength * 2, drawVerticalEdges);
//        canvas.restoreToCount(saved);
//
//        // RT
//        saved = canvas.save();
//        canvas.translate(horizontalHalfEdgesLength, -verticalHalfEdgesLength);
//        canvas.rotate(90f);
//        drawCornerAndRightEdgeShadow(canvas, verticalHalfEdgesLength * 2, drawVerticalEdges);
//        canvas.restoreToCount(saved);

        float horizontalHalfEdgesLength = horizontalEdgesLength/2;
        float verticalHalfEdgesLength = verticalEdgesLength/2;

        // LT
        int saved = canvas.save();
        canvas.translate(-horizontalHalfEdgesLength, -verticalHalfEdgesLength);
        drawCornerAndRightEdgeShadow(canvas, horizontalEdgesLength, drawHorizontalEdges);
        canvas.restoreToCount(saved);

        // RB
        saved = canvas.save();
        canvas.translate(horizontalHalfEdgesLength, verticalHalfEdgesLength);
        canvas.rotate(180f);
        drawCornerAndRightEdgeShadow(canvas, horizontalEdgesLength, drawHorizontalEdges);
        canvas.restoreToCount(saved);

        // LB
        saved = canvas.save();
        canvas.translate(-horizontalHalfEdgesLength, verticalHalfEdgesLength);
        canvas.rotate(270f);
        drawCornerAndRightEdgeShadow(canvas, verticalEdgesLength, drawVerticalEdges);
        canvas.restoreToCount(saved);

        // RT
        saved = canvas.save();
        canvas.translate(horizontalHalfEdgesLength, -verticalHalfEdgesLength);
        canvas.rotate(90f);
        drawCornerAndRightEdgeShadow(canvas, verticalEdgesLength, drawVerticalEdges);
        canvas.restoreToCount(saved);
    }

    private void drawCornerAndRightEdgeShadow(Canvas canvas, float edgesLength, boolean isDrawEdge) {
        final float edgeShadowTop = -shadowCornerRadius - shadowBlurRadius;

        canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
        if (isDrawEdge) {
            canvas.drawRect(0, edgeShadowTop, edgesLength, -shadowCornerRadius, mEdgeShadowPaint);
        }
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    protected int getSuggestedMinimumWidth() {
        return 0;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return 0;
    }

    /*--------------------------------------------------------------------------------------------*/

    public void invalidateShadow() {
        forceInvalidateShadow = true;
        requestLayout();
        invalidate();
    }
}