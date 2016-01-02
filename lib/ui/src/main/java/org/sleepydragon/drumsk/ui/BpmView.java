package org.sleepydragon.drumsk.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import org.sleepydragon.drumsk.util.Size;

/**
 * A view that allows the user to view and edit the beats-per-minute.
 */
public class BpmView extends View {

    public static final int DEFAULT_CIRCLE_COLOR = Color.BLACK;
    public static final int DEFAULT_TEXT_COLOR = Color.WHITE;
    public static final int DEFAULT_TEXT_CIRCLE_PADDING = 10;

    private Integer mBpm;

    private final Paint mCirclePaint;
    private float mCircleCenterX;
    private float mCircleCenterY;
    private float mCircleRadius;

    private final Paint mTextPaint;
    private float mTextX;
    private float mTextY;
    private String mText;
    private float mTextCirclePadding;

    public BpmView(final Context context) {
        this(context, null);
    }

    public BpmView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BpmView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final int circleColor, textColor;
        final Integer bpm;
        {
            final TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                    R.styleable.BpmView, defStyleAttr, 0);
            circleColor = a.getColor(R.styleable.BpmView_circleColor, DEFAULT_CIRCLE_COLOR);
            textColor = a.getColor(R.styleable.BpmView_textColor, DEFAULT_TEXT_COLOR);
            if (a.hasValue(R.styleable.BpmView_bpm)) {
                bpm = a.getInt(R.styleable.BpmView_bpm, 0);
            } else {
                bpm = null;
            }
            mTextCirclePadding = a.getDimension(R.styleable.BpmView_textCirclePadding,
                    DEFAULT_TEXT_CIRCLE_PADDING);
            a.recycle();
        }

        mCirclePaint = new Paint();
        mCirclePaint.setColor(circleColor);
        mCirclePaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint();
        mTextPaint.setColor(textColor);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);

        setBpm(bpm);
    }

    public void setBpm(@Nullable final Integer value) {
        mBpm = value;
        mText = (value == null) ? null : value.toString();
        invalidate();
    }

    @Nullable
    public Integer getBpm() {
        return mBpm;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        canvas.drawCircle(mCircleCenterX, mCircleCenterY, mCircleRadius, mCirclePaint);
        if (mText != null) {
            canvas.drawText(mText, mTextX, mTextY, mTextPaint);
        }
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final Size size = onMeasureCircle(widthMeasureSpec, heightMeasureSpec);
        onMeasureText();
        setMeasuredDimension(size.width, size.height);
    }

    @NonNull
    private Size onMeasureCircle(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        final int availableWidth = Math.max(0, widthSize - paddingLeft - paddingRight);
        final int availableHeight = Math.max(0, heightSize - paddingTop - paddingBottom);
        final int maxDiameter = Math.min(availableWidth, availableHeight);
        final int diameter;

        switch (widthMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST:
                switch (heightMode) {
                    case MeasureSpec.EXACTLY:
                    case MeasureSpec.AT_MOST:
                        diameter = maxDiameter;
                        break;
                    case MeasureSpec.UNSPECIFIED:
                        diameter = availableWidth;
                        break;
                    default:
                        throw new IllegalArgumentException("unknown heightMode: " + heightMode);
                }
                break;
            case MeasureSpec.UNSPECIFIED:
                switch (heightMode) {
                    case MeasureSpec.EXACTLY:
                    case MeasureSpec.AT_MOST:
                        diameter = availableHeight;
                        break;
                    case MeasureSpec.UNSPECIFIED:
                        diameter = 100; // I guess whatever?
                        break;
                    default:
                        throw new IllegalArgumentException("unknown heightMode: " + heightMode);
                }
                break;
            default:
                throw new IllegalArgumentException("unknown widthMode: " + widthMode);
        }

        final float radius = ((float) diameter) / 2f;
        mCircleCenterX = paddingLeft + radius;
        mCircleCenterY = paddingTop + radius;
        mCircleRadius = radius;

        final int measuredWidth = diameter + paddingLeft + paddingRight;
        final int measuredHeight = diameter + paddingTop + paddingBottom;
        return new Size(measuredWidth, measuredHeight);
    }

    private void onMeasureText() {
        mTextX = mCircleCenterX;
        mTextY = mCircleCenterY;

        final float diameter = mCircleRadius * 2f;
        final float maxHypotenuse = diameter - mTextCirclePadding;
        if (maxHypotenuse < 0f) {
            mTextPaint.setTextSize(1);
            return;
        }

        float min = 0;
        float max = diameter;
        while (true) {
            final float cur = min + ((max - min) / 2f);
            mTextPaint.setTextSize(cur);

            final float width = mTextPaint.measureText("999");
            final float ascent = Math.abs(mTextPaint.ascent());
            final float descent = Math.abs(mTextPaint.descent());
            final float height = ascent + descent;
            final float hypotenuse = (float) Math.hypot(width, height);

            if (hypotenuse > maxHypotenuse) {
                max = cur;
            } else {
                min = cur;
                if (maxHypotenuse - hypotenuse < 1f || max - min < 1f) {
                    break;
                }
            }
        }
    }

}
