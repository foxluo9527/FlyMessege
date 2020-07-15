package com.example.flymessagedome.ui.widget;

import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 最大化的RecyclerView，嵌套于ScrollView之中使用
 */
public class MaxRecyclerView extends RecyclerView {

	public MaxRecyclerView(android.content.Context context, android.util.AttributeSet attrs){
		super(context, attrs);
	}
	public MaxRecyclerView(android.content.Context context){
		super(context);
	}
	/**
	 * 设置不滚动
	 */
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
	private float lastX, lastY;
	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {

		boolean intercept = super.onInterceptTouchEvent(e);

		switch (e.getAction()) {

			case MotionEvent.ACTION_DOWN:
				lastX = e.getX();
				lastY = e.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				// 只要横向大于竖向，就拦截掉事件。
				// 部分机型点击事件（slopx==slopy==0），会触发MOVE事件。
				// 所以要加判断(slopX > 0 || sloy > 0)
				float slopX = Math.abs(e.getX() - lastX);
				float slopY = Math.abs(e.getY() - lastY);
				//  Log.log("slopX=" + slopX + ", slopY="  + slopY);
				if((slopX > 0 || slopY > 0) && slopX >= slopY){
					requestDisallowInterceptTouchEvent(true);
					intercept = true;
				}
				break;
			case MotionEvent.ACTION_UP:
				intercept = false;
				break;
		}
		// Log.log("intercept"+e.getAction()+"=" + intercept);
		return intercept;
	}
}
