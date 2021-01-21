//package org.telegram.irooms.ui;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.os.Build;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//
//
//public class EditTextField extends androidx.appcompat.widget.AppCompatEditText {
//    private Context mContext;
//    private Bitmap mClearButton;
//    private Paint mPaint;
//
//    private boolean mClearStatus;
//
//    private int mInitPaddingRight;
//    private int mButtonPadding = dp2px(3);
//
//    public EditTextField(Context context) {
//        super(context);
//        init(context);
//    }
//
//    public EditTextField(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init(context);
//    }
//
//    public EditTextField(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init(context);
//    }
//
//    private void init(Context context) {
//        this.mContext = context;
//
//        int clearButton = android.R.drawable.ic_delete;
//
//        mClearButton = ((BitmapDrawable) getDrawableCompat(clearButton)).getBitmap();
//
//        mPaint = new Paint();
//        mPaint.setAntiAlias(true);
//
//        mInitPaddingRight = getPaddingRight();
//    }
//
//    private void setPadding(boolean isShow) {
//        int paddingRight = mInitPaddingRight + (isShow ? mClearButton.getWidth() + mButtonPadding + mButtonPadding : 0);
//
//        setPadding(getPaddingLeft(), getPaddingTop(), paddingRight, getPaddingBottom());
//    }
//
//
//    private Rect getRect(boolean isShow) {
//        int left, top, right, bottom;
//
//        right = isShow ? getMeasuredWidth() + getScrollX() - mButtonPadding - mButtonPadding : 0;
//        left = isShow ? right - mClearButton.getWidth() : 0;
//        top = isShow ? (getMeasuredHeight() - mClearButton.getHeight()) / 2 : 0;
//        bottom = isShow ? top + mClearButton.getHeight() : 0;
//
//        setPadding(isShow);
//
//        return new Rect(left, top, right, bottom);
//    }
//
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        canvas.save();
//
//        canvas.drawBitmap(mClearButton, null, getRect(hasFocus() && getText().length() > 0), mPaint);
//
//        canvas.restore();
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_UP:
//                if (event.getX() - (getMeasuredWidth() - getPaddingRight()) >= 0) {
//                    setError(null);
//                    this.setText("");
//                }
//                break;
//        }
//
//        return super.onTouchEvent(event);
//    }
//
//
//    private Drawable getDrawableCompat(int resourseId) {
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            return getResources().getDrawable(resourseId, mContext.getTheme());
//        } else {
//            return getResources().getDrawable(resourseId);
//        }
//    }
//
//    public int dp2px(float dipValue) {
//        final float scale = getResources().getDisplayMetrics().density;
//        return (int) (dipValue * scale + 0.5f);
//    }
//}