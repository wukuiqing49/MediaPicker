package com.wu.media.ui.widget.record;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wu.media.R;


/**
 * 作 者 : wkq
 * <p>
 * 时 间 : 2020/9/22 15:46
 * <p>
 * 名 字 : TypeButton
 * <p>
 * 简 介 : 录制的取消和返回
 */
public class TypeButton extends androidx.appcompat.widget.AppCompatImageView {

    public static final int TYPE_CANCEL = 0x001;
    public static final int TYPE_CONFIRM = 0x002;
    private int button_size;

    public TypeButton(@NonNull Context context) {
        this(context, null);
    }

    public TypeButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TypeButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TypeButtonStyle);
        int drawable = array.getResourceId(R.styleable.TypeButtonStyle_typeBg, R.drawable.fb_wancheng);
        float typeSize = array.getDimension(R.styleable.TypeButtonStyle_typeSize, 30);
        setImageResource(drawable);
        button_size = (int)typeSize;

        array.recycle();

    }

//    public TypeButton(Context context, int type, int size) {
//        super(context);

//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(button_size, button_size);
    }
}
