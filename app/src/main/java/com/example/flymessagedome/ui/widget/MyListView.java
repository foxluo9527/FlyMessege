package com.example.flymessagedome.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 自定义ListView
 */
public class MyListView extends ListView {

  public MyListView(Context context) {
    super(context);
  }

  public MyListView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    heightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,//右移运算符，相当于除于4
        MeasureSpec.AT_MOST);//测量模式取最大值
    super.onMeasure(widthMeasureSpec,heightMeasureSpec);//重新测量高度
  }
}

