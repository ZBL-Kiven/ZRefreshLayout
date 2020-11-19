package com.zj.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.NoCopySpan.Concrete;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.zj.views.R.styleable;

import static android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE;

@SuppressWarnings("unused")
public class FoldTextView extends AppCompatTextView implements OnClickListener {

    private static final String ellipse = "\u2026", placeholder = "\u0020";
    private int mShowMaxLine;
    private String mFoldText;
    private String mExpandText;
    private Drawable mFoldDrawable;
    private Drawable mExpandDrawable;
    private CharSequence mOriginalText;
    private boolean isExpand;
    private int mTipColor;
    private boolean mTipClickable;
    private final FoldTextView.ExpandSpan mSpan;
    private boolean flag;
    private boolean isShowTipAfterExpand;
    private boolean isExpandSpanClick;
    private boolean isParentClick;
    private float foldTextSize, foldDrawableSize;
    private int mFoldWidth, mFoldSize;
    private OnClickListener listener;

    public FoldTextView(Context context) {
        this(context, null);
    }

    public FoldTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setTextColor(int color) {
        super.setTextColor(color);
    }

    public FoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mFoldText = "";
        this.mExpandText = "";
        this.foldTextSize = 0.0f;
        this.mShowMaxLine = 4;
        this.mSpan = new FoldTextView.ExpandSpan();
        TypedArray arr = context.obtainStyledAttributes(attrs, styleable.FoldTextView);

        try {
            if (attrs != null) {
                this.mShowMaxLine = arr.getInt(styleable.FoldTextView_showMaxLine, 4);
                this.mTipColor = arr.getColor(styleable.FoldTextView_tipColor, -1);
                this.mTipClickable = arr.getBoolean(styleable.FoldTextView_tipClickable, false);
                this.mFoldDrawable = arr.getDrawable(styleable.FoldTextView_foldDrawable);
                this.mExpandDrawable = arr.getDrawable(styleable.FoldTextView_expandDrawable);
                this.mFoldText = arr.getString(styleable.FoldTextView_foldText);
                this.mExpandText = arr.getString(styleable.FoldTextView_expandText);
                this.foldTextSize = arr.getDimension(styleable.FoldTextView_foldTextSize, this.getTextSize());
                this.foldDrawableSize = arr.getDimension(styleable.FoldTextView_drawableSize, this.getTextSize());
                this.isShowTipAfterExpand = arr.getBoolean(styleable.FoldTextView_showTipAfterExpand, false);
                this.isParentClick = arr.getBoolean(styleable.FoldTextView_isSetParentClick, false);
                if (mFoldDrawable == null || mExpandDrawable == null) {
                    if (mFoldDrawable == null) mFoldDrawable = mExpandDrawable;
                    if (mExpandDrawable == null) mExpandDrawable = mFoldDrawable;
                }
                if (TextUtils.isEmpty(mFoldText) || TextUtils.isEmpty(mExpandText)) {
                    if (TextUtils.isEmpty(mFoldText)) mFoldText = mExpandText;
                    if (TextUtils.isEmpty(mExpandText)) mExpandText = mFoldText;
                }
                if (mFoldDrawable != null && !TextUtils.isEmpty(mFoldText)) throw new IllegalArgumentException("unable to use both of text & drawable fold span");
            }
        } finally {
            arr.recycle();
        }
    }

    public void setText(final CharSequence text, final BufferType type) {
        if (!TextUtils.isEmpty(text) && this.mShowMaxLine > 0 && this.mShowMaxLine <= getMaxLines()) {
            if (!this.flag) {
                this.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                    public boolean onPreDraw() {
                        FoldTextView.this.getViewTreeObserver().removeOnPreDrawListener(this);
                        FoldTextView.this.flag = true;
                        FoldTextView.this.formatText(text, type);
                        return true;
                    }
                });
            } else {
                this.formatText(text, type);
            }
        } else {
            super.setText(text, type);
        }
    }

    public void setTipColor(int color) {
        Integer c = null;
        try {
            c = ContextCompat.getColor(getContext(), color);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (c == null) c = color;
        this.mTipColor = c;
    }

    private void formatText(final CharSequence text, final BufferType type) {
        this.mOriginalText = text;
        Layout layout = this.getLayout();
        if (layout == null || !layout.getText().equals(this.mOriginalText)) {
            super.setText(this.mOriginalText, type);
            layout = this.getLayout();
        }
        if (layout == null) {
            this.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    FoldTextView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    FoldTextView.this.translateText(text, FoldTextView.this.getLayout(), type);
                }
            });
        } else {
            this.translateText(text, layout, type);
        }
    }

    private void translateText(CharSequence text, Layout layout, BufferType type) {
        int lineCount = layout.getLineCount();
        if (this.mShowMaxLine >= lineCount) {
            super.setText(text, type);
            return;
        }
        float simpleSize = getPaint().measureText(placeholder);
        if (!TextUtils.isEmpty(mFoldText)) {
            String txt = this.isExpand ? mExpandText : mFoldText;
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextSize(foldTextSize);
            mFoldSize = (int) (paint.measureText(txt) + 0.5f);
        } else if (mFoldDrawable != null) {
            mFoldSize = (int) (foldDrawableSize + 0.5f);
        }
        mFoldWidth = (int) (this.mFoldSize * 1f / simpleSize + 0.5f);
        SpannableStringBuilder sp = fullMaxLine(layout, this.isExpand ? lineCount - 1 : this.mShowMaxLine - 1);
        this.addTip(sp, type);
    }

    private SpannableStringBuilder fullMaxLine(Layout layout, int line) {
        SpannableStringBuilder finalSb = new SpannableStringBuilder();
        int start = layout.getLineStart(line);
        int end = layout.getLineEnd(line);
        final float dw = this.mFoldSize;
        String str = this.mOriginalText.subSequence(0, start).toString();
        String matchText = replaceEndTN(this.mOriginalText.subSequence(start, end).toString());
        StringBuilder lineSb = new StringBuilder(matchText);
        float x = (float) (this.getWidth() - this.getPaddingLeft() - this.getPaddingRight());
        float lineW = (lineSb.length() == 0 ? 0.0F : this.getTextWidth(lineSb.toString()));
        StringBuilder appending = new StringBuilder();
        float sg = this.getTextWidth(placeholder);
        float appendingWidth = 0;
        if (this.isExpand) {
            if (lineW + dw > x) {
                lineW = 0;
                lineSb.append("\n");
            }
            while (lineW + dw + appendingWidth + sg < x) {
                appending.append(placeholder);
                appendingWidth = this.getTextWidth(appending.toString());
            }
            if (TextUtils.isEmpty(mFoldText) && mFoldDrawable != null) for (int i = 0; i < mFoldWidth; i++) appending.append(placeholder);
            return finalSb.append(str).append(lineSb).append(appending);
        } else {
            float ellW = this.getTextWidth(ellipse);
            if (lineW == 0 && dw + ellW > x) throw new IllegalArgumentException("the fold-drawable width + ellipse-text width must be small than FoldTextView width!");
            if (lineW + dw + ellW > x) {
                while (lineW + dw + ellW > x) {
                    lineSb.setLength(lineSb.length() - 1);
                    lineW = (lineSb.length() == 0 ? 0.0F : this.getTextWidth(lineSb.toString()));
                }
            } else {
                while (lineW + dw + ellW + appendingWidth + sg < x) {
                    appending.append(placeholder);
                    appendingWidth = this.getTextWidth(appending.toString());
                }
            }
            if (TextUtils.isEmpty(mFoldText) && mFoldDrawable != null) for (int i = 0; i < mFoldWidth; i++) appending.append(placeholder);
            return finalSb.append(str).append(lineSb).append(ellipse).append(appending);
        }
    }

    private String replaceEndTN(String text) {
        while (text.endsWith("\n") || text.endsWith(" ")) {
            int end = Math.max(text.lastIndexOf("\n"), text.lastIndexOf(" "));
            text = text.substring(0, end);
        }
        return text;
    }

    private void addTip(SpannableStringBuilder span, BufferType type) {
        if (!this.isExpand || this.isShowTipAfterExpand) {
            boolean isTextSpan = false;
            Drawable spanDrawable = null;
            int length;
            if (this.isExpand) {
                if (!TextUtils.isEmpty(this.mExpandText)) {
                    span.append(this.mExpandText);
                    length = this.mExpandText.length();
                    isTextSpan = true;
                } else {
                    length = mFoldWidth;
                    spanDrawable = this.mExpandDrawable;
                }
            } else if (!TextUtils.isEmpty(this.mFoldText)) {
                span.append(this.mFoldText);
                length = this.mFoldText.length();
                isTextSpan = true;
            } else {
                length = mFoldWidth;
                spanDrawable = this.mFoldDrawable;
            }

            if (this.mTipClickable) {
                span.setSpan(this.mSpan, span.length() - length, span.length(), SPAN_INCLUSIVE_EXCLUSIVE);
                if (this.isParentClick) {
                    this.setMovementMethod(FoldTextView.MyLinkMovementMethod.getInstance());
                    this.setClickable(false);
                    this.setFocusable(false);
                    this.setLongClickable(false);
                } else {
                    this.setMovementMethod(LinkMovementMethod.getInstance());
                }
            }
            if (isTextSpan) {
                span.setSpan(new ForegroundColorSpan(this.mTipColor), span.length() - length, span.length(), SPAN_INCLUSIVE_EXCLUSIVE);
                span.setSpan(new AbsoluteSizeSpan((int) (this.foldTextSize + 0.5f)), span.length() - length, span.length(), SPAN_INCLUSIVE_EXCLUSIVE);
            } else {
                spanDrawable.setBounds(0, 0, mFoldSize, mFoldSize);
                final Drawable finalSpanDrawable = spanDrawable.mutate();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    finalSpanDrawable.setColorFilter(new BlendModeColorFilter(mTipColor, BlendMode.SRC_ATOP));
                } else {
                    ColorFilter colorFilter = new PorterDuffColorFilter(mTipColor, PorterDuff.Mode.SRC_ATOP);
                    finalSpanDrawable.setColorFilter(colorFilter);
                }
                span.setSpan(new DynamicDrawableSpan() {
                    public Drawable getDrawable() {
                        return finalSpanDrawable;
                    }
                }, span.length() - length, span.length(), SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }

        super.setText(span, type);
    }

    @Override
    public void onClick(View v) {
        if (this.isExpandSpanClick) {
            this.isExpandSpanClick = false;
        } else {
            this.listener.onClick(v);
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        this.listener = l;
        super.setOnClickListener(this);
    }

    private float getTextWidth(String text) {
        Paint paint = this.getPaint();
        return paint.measureText(text);
    }

    public static class MyLinkMovementMethod extends ScrollingMovementMethod {
        private static FoldTextView.MyLinkMovementMethod sInstance;
        private static final Object FROM_BELOW = new Concrete();

        public MyLinkMovementMethod() {
        }

        @Override
        public boolean canSelectArbitrarily() {
            return true;
        }

        @Override
        protected boolean handleMovementKey(TextView widget, Spannable buffer, int keyCode, int movementMetaState, KeyEvent event) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    if (KeyEvent.metaStateHasNoModifiers(movementMetaState) && event.getAction() == 0 && event.getRepeatCount() == 0 && this.action(1, widget, buffer)) {
                        return true;
                    }
                default:
                    return super.handleMovementKey(widget, buffer, keyCode, movementMetaState, event);
            }
        }

        @Override
        protected boolean up(TextView widget, Spannable buffer) {
            return this.action(2, widget, buffer) || super.up(widget, buffer);
        }

        @Override
        protected boolean down(TextView widget, Spannable buffer) {
            return this.action(3, widget, buffer) || super.down(widget, buffer);
        }

        @Override
        protected boolean left(TextView widget, Spannable buffer) {
            return this.action(2, widget, buffer) || super.left(widget, buffer);
        }

        @Override
        protected boolean right(TextView widget, Spannable buffer) {
            return this.action(3, widget, buffer) || super.right(widget, buffer);
        }

        private boolean action(int what, TextView widget, Spannable buffer) {
            Layout layout = widget.getLayout();
            int padding = widget.getTotalPaddingTop() + widget.getTotalPaddingBottom();
            int areaTop = widget.getScrollY();
            int areaBot = areaTop + widget.getHeight() - padding;
            int lineTop = layout.getLineForVertical(areaTop);
            int lineBot = layout.getLineForVertical(areaBot);
            int first = layout.getLineStart(lineTop);
            int last = layout.getLineEnd(lineBot);
            ClickableSpan[] candidates = buffer.getSpans(first, last, ClickableSpan.class);
            int a = Selection.getSelectionStart(buffer);
            int b = Selection.getSelectionEnd(buffer);
            int selStart = Math.min(a, b);
            int selEnd = Math.max(a, b);
            if (selStart < 0 && buffer.getSpanStart(FROM_BELOW) >= 0) {
                selStart = selEnd = buffer.length();
            }

            if (selStart > last) {
                selEnd = Integer.MAX_VALUE;
                selStart = Integer.MAX_VALUE;
            }

            if (selEnd < first) {
                selEnd = -1;
                selStart = -1;
            }

            int bestStart;
            int bestEnd;
            ClickableSpan[] var20;
            int var21;
            int var22;
            ClickableSpan candidate;
            int start;
            switch (what) {
                case 1:
                    if (selStart == selEnd) {
                        return false;
                    }

                    ClickableSpan[] link = buffer.getSpans(selStart, selEnd, ClickableSpan.class);
                    if (link.length != 1) {
                        return false;
                    }

                    link[0].onClick(widget);
                    break;
                case 2:
                    bestStart = -1;
                    bestEnd = -1;
                    var20 = candidates;
                    var21 = candidates.length;

                    for (var22 = 0; var22 < var21; ++var22) {
                        candidate = var20[var22];
                        start = buffer.getSpanEnd(candidate);
                        if ((start < selEnd || selStart == selEnd) && start > bestEnd) {
                            bestStart = buffer.getSpanStart(candidate);
                            bestEnd = start;
                        }
                    }

                    if (bestStart >= 0) {
                        Selection.setSelection(buffer, bestEnd, bestStart);
                        return true;
                    }
                    break;
                case 3:
                    bestStart = Integer.MAX_VALUE;
                    bestEnd = Integer.MAX_VALUE;
                    var20 = candidates;
                    var21 = candidates.length;

                    for (var22 = 0; var22 < var21; ++var22) {
                        candidate = var20[var22];
                        start = buffer.getSpanStart(candidate);
                        if ((start > selStart || selStart == selEnd) && start < bestStart) {
                            bestStart = start;
                            bestEnd = buffer.getSpanEnd(candidate);
                        }
                    }
                    if (bestEnd < Integer.MAX_VALUE) {
                        Selection.setSelection(buffer, bestStart, bestEnd);
                        return true;
                    }
            }

            return false;
        }

        @Override
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            int action = event.getAction();
            if (action != 1 && action != 0) {
                return super.onTouchEvent(widget, buffer, event);
            } else {
                int x = (int) event.getX();
                int y = (int) event.getY();
                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();
                x += widget.getScrollX();
                y += widget.getScrollY();
                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, (float) x);
                ClickableSpan[] links = buffer.getSpans(off, off, ClickableSpan.class);
                if (links.length != 0) {
                    if (action == 1) {
                        links[0].onClick(widget);
                    } else {
                        Selection.setSelection(buffer, buffer.getSpanStart(links[0]), buffer.getSpanEnd(links[0]));
                    }

                    return true;
                } else {
                    Selection.removeSelection(buffer);
                    return false;
                }
            }
        }

        public void initialize(TextView widget, Spannable text) {
            Selection.removeSelection(text);
            text.removeSpan(FROM_BELOW);
        }

        public void onTakeFocus(TextView view, Spannable text, int dir) {
            Selection.removeSelection(text);
            if ((dir & 1) != 0) {
                text.setSpan(FROM_BELOW, 0, 0, 34);
            } else {
                text.removeSpan(FROM_BELOW);
            }
        }

        public static MovementMethod getInstance() {
            if (sInstance == null) {
                sInstance = new FoldTextView.MyLinkMovementMethod();
            }

            return sInstance;
        }
    }

    private class ExpandSpan extends ClickableSpan {
        private ExpandSpan() { }

        public void onClick(@NonNull View widget) {
            if (FoldTextView.this.mTipClickable) {
                FoldTextView.this.isExpand = !FoldTextView.this.isExpand;
                FoldTextView.this.isExpandSpanClick = true;
                FoldTextView.this.setText(FoldTextView.this.mOriginalText);
            }
        }

        public void updateDrawState(TextPaint ds) {
            ds.setColor(FoldTextView.this.mTipColor);
            ds.setUnderlineText(false);
        }
    }
}
