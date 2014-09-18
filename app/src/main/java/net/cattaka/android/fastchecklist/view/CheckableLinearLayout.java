package net.cattaka.android.fastchecklist.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class CheckableLinearLayout extends LinearLayout implements Checkable {
	private boolean checked;
   private static final int[] CHECKED_STATE_SET = {
        android.R.attr.state_checked
    };
	
	public CheckableLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CheckableLinearLayout(Context context) {
		super(context);
	}

	@Override
	public boolean isChecked() {
		return checked;
	}

	@Override
	public void setChecked(boolean checked) {
		if (this.checked != checked) {
			this.checked = checked;
			refreshDrawableState();
		}
	}

	@Override
	public void toggle() {
		this.checked = !this.checked;
		refreshDrawableState();
	}

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }
}
