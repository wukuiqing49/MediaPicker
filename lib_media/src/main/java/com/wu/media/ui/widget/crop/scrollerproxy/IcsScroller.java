package com.wu.media.ui.widget.crop.scrollerproxy;

import android.content.Context;

public class IcsScroller extends GingerScroller
{
    public IcsScroller(Context context)
    {
        super(context);
    }

    @Override
    public boolean computeScrollOffset()
    {
        return mScroller.computeScrollOffset();
    }
}