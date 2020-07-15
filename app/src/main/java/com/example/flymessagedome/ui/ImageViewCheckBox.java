package com.example.flymessagedome.ui;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.example.flymessagedome.R;

public class ImageViewCheckBox extends androidx.appcompat.widget.AppCompatImageView
{
	public static final int CHECK_STATE_DISABLED = 0;
	public static final int CHECK_STATE_UNCHECKED = 1;
	public static final int CHECK_STATE_CHECKED = 2;
	
	private int check_bkg_id;
	private int uncheck_bkg_id;
	private int disable_check_bkg_id;
	
	private int checkState;
	
	public ImageViewCheckBox(Context context)
	{
		this(context, null);
	}
	
	public ImageViewCheckBox(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
		
	}
	
	public ImageViewCheckBox(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	private void init(AttributeSet attrs)
	{
		TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.ImageViewCheckBox); 
		checkState = t.getInteger(R.styleable.ImageViewCheckBox_default_state, CHECK_STATE_UNCHECKED);
		check_bkg_id = t.getResourceId(R.styleable.ImageViewCheckBox_checked_bkg, 0);
		uncheck_bkg_id = t.getResourceId(R.styleable.ImageViewCheckBox_unchecked_bkg, 0);
		disable_check_bkg_id = t.getResourceId(R.styleable.ImageViewCheckBox_checked_disabled, 0);		
		
		setBkgByCheckState();
		setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				changeState();
			}
		});
		
		t.recycle();
	}
	public boolean isChecked(){
		return checkState == CHECK_STATE_CHECKED;
	}
	public void setChecked(boolean isChecked){
		if(checkState == CHECK_STATE_DISABLED)
		{
			return;
		}
		if (isChecked){
			checkState = CHECK_STATE_CHECKED;
		}else {
			checkState = CHECK_STATE_UNCHECKED;
		}
		setBkgByCheckState();
	}
	public void changeState()
	{
		if(checkState == CHECK_STATE_DISABLED)
		{
			return;
		}
		
		if(checkState == CHECK_STATE_UNCHECKED)
		{
			checkState = CHECK_STATE_CHECKED;
		}
		else if(checkState == CHECK_STATE_CHECKED)
		{
			checkState = CHECK_STATE_UNCHECKED;
		}
		
		setBkgByCheckState();
		notifyListner();
	}

	
	public void setCheckDisabled()
	{
		this.checkState = CHECK_STATE_DISABLED;
		setBkgByCheckState();
	}

	private void setBkgByCheckState()
	{
		if(checkState == CHECK_STATE_UNCHECKED)
		{
			setBackgroundResource(uncheck_bkg_id);
		}
		else if(checkState == CHECK_STATE_DISABLED)
		{
			setBackgroundResource(disable_check_bkg_id);
		}
		else
		{
			setBackgroundResource(check_bkg_id);
		}
		
	}

	public interface OnCheckStateChangedListener
	{
		public void onCheckStateChanged(boolean isChecked);
	}
	
	private OnCheckStateChangedListener listener;
	
	public void setOnCheckStateChangedListener(OnCheckStateChangedListener listener)
	{
		this.listener = listener;
	}
	
	private void notifyListner()
	{
		if(this.listener != null)
		{
			if(checkState == CHECK_STATE_UNCHECKED)
			{
				this.listener.onCheckStateChanged(false);
			}
			else if(checkState == CHECK_STATE_CHECKED)
			{
				this.listener.onCheckStateChanged(true);
			}
			
		}
	}
}