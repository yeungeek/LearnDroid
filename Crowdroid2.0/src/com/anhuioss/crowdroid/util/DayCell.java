package com.anhuioss.crowdroid.util;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.Shader;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

public class DayCell extends View {
	// types
	public interface OnItemClick {
		public void OnClick(DayCell item);
	}

	// fields
	private final static float fTextSize = 12;
	private final static int iMargin = 1;

	// fields
	private int iDateYear = 0;
	private int iDateMonth = 0;
	private int iDateDay = 0;

	// fields
	private OnItemClick itemClick = null;
	private TextPaint pt = new TextPaint();
	private Rect rect = new Rect();

	private String sDate = "";

	private String message = "";

	public String ids = "";

	// fields
	private boolean bSelected = false;
	private boolean bIsActiveMonth = false;
	private boolean bToday = false;
	private boolean bHoliday = false;
	private boolean bHolidaySUNDAY = false;
	private boolean bTouchedDown = false;

	private Context context;

	// methods
	public DayCell(Context context, int iWidth, int iHeight) {
		super(context);
		this.context = context;
		setFocusable(true);
		setLayoutParams(new LayoutParams(iWidth, iHeight, 1));
	}

	public DayCell(Context context) {
		super(context);
		this.context = context;
		setFocusable(true);
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, 1.0f));
	}

	public boolean getSelected() {
		return this.bSelected;
	}

	@Override
	public void setSelected(boolean bEnable) {
		if (this.bSelected != bEnable) {
			this.bSelected = bEnable;
			this.invalidate();
		}
	}

	public void setData(int iYear, int iMonth, int iDay, boolean bToday,
			boolean bHoliday, boolean bHolidaySUNDAY, int iActiveMonth,
			String message) {
		iDateYear = iYear;
		iDateMonth = iMonth;
		iDateDay = iDay;

		this.sDate = Integer.toString(iDateDay);
		this.bIsActiveMonth = (iDateMonth == iActiveMonth);
		this.bToday = bToday;
		this.bHoliday = bHoliday;
		this.bHolidaySUNDAY = bHolidaySUNDAY;
		this.message = message;
	}

	public void setItemClick(OnItemClick itemClick) {
		this.itemClick = itemClick;
	}

	private int getTextHeight() {
		return (int) (-pt.ascent() + pt.descent());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean bResult = super.onKeyDown(keyCode, event);
		if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
				|| (keyCode == KeyEvent.KEYCODE_ENTER)) {
			doItemClick();
		}
		return bResult;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean bResult = super.onKeyUp(keyCode, event);
		return bResult;
	}

	public void doItemClick() {
		if (itemClick != null)
			itemClick.OnClick(this);
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		invalidate();
	}

	public Calendar getDate() {
		Calendar calDate = Calendar.getInstance();
		calDate.clear();
		calDate.set(Calendar.YEAR, iDateYear);
		calDate.set(Calendar.MONTH, iDateMonth);
		calDate.set(Calendar.DAY_OF_MONTH, iDateDay);
		return calDate;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// init rectangles
		rect.set(0, 0, this.getWidth(), this.getHeight());
		rect.offset(1, 1);
		pt.setAntiAlias(true);
		canvas.drawRGB(142, 229, 238);

		// drawing
		final boolean bFocused = IsViewFocused();

		drawDayView(canvas, bFocused);
		drawDayNumber(canvas, bFocused);
	}

	private void drawDayView(Canvas canvas, boolean bFocused) {
		if (bSelected || bFocused) {
			LinearGradient lGradBkg = null;

			if (bFocused) {
				lGradBkg = new LinearGradient(rect.left, 0, rect.right, 0,
						DayStyle.iColorBkgFocusDark,
						DayStyle.iColorBkgFocusLight, Shader.TileMode.CLAMP);
			}

			if (bSelected) {
				lGradBkg = new LinearGradient(rect.left, 0, rect.right, 0,
						DayStyle.iColorBkgSelectedDark,
						DayStyle.iColorBkgSelectedLight, Shader.TileMode.CLAMP);
			}

			if (lGradBkg != null) {
				pt.setShader(lGradBkg);
				canvas.drawRect(rect, pt);
			}
			pt.setShader(null);

		} else {
			pt.setColor(DayStyle.getColorBkg(bHoliday, bHolidaySUNDAY, bToday));
			canvas.drawRect(rect, pt);
		}
		if (!"".equals(message)) {
			pt.setColor(DayStyle.iColorRectBkgSchedule);
			canvas.drawRect(rect, pt);
		}
	}

	public void drawDayNumber(Canvas canvas, boolean bFocused) {
		// draw day number
		pt.setTypeface(null);
		pt.setAntiAlias(true);
		pt.setShader(null);
		pt.setTextSize(fTextSize);
		if (!"".equals(message)) {
			pt.setTextAlign(Paint.Align.LEFT);
			sDate = sDate + "  " + message;
		}

		int iTextPosX = (int) rect.right - (int) pt.measureText(sDate);
		int iTextPosY = (int) rect.bottom + (int) (-pt.ascent())
				- getTextHeight();

		iTextPosX -= ((int) rect.width() / 2)
				- ((int) pt.measureText(sDate) / 2);
		iTextPosY -= ((int) rect.height() / 2) - (getTextHeight() / 2);

		// draw text
		if (bSelected || bFocused) {
			if (bSelected)
				pt.setColor(DayStyle.iColorTextSelected);
			if (bFocused)
				pt.setColor(DayStyle.iColorTextFocused);
		} else {
			pt.setColor(DayStyle.getColorText(bToday));
		}
		if (!"".equals(message)) {
			pt.setColor(DayStyle.iColorTextBkgSchedule);
		}
		StaticLayout layout = new StaticLayout(sDate, pt, (int) rect.right
				- (int) rect.left, Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
		canvas.translate(2, 2);

		layout.draw(canvas);
		// canvas.drawText(sDate, iTextPosX, iTextPosY , pt);
	}

	public boolean IsViewFocused() {
		return (this.isFocused() || bTouchedDown);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean bHandled = false;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			bHandled = true;
			bTouchedDown = true;
			invalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_CANCEL) {
			bHandled = true;
			bTouchedDown = false;
			invalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			bHandled = true;
			bTouchedDown = false;
			invalidate();
			doItemClick();
		}
		return bHandled;
	}

	public String getMessage() {
		return message;
	}

	public void setData(int iYear, int iMonth, int iDay, boolean bToday,
			boolean bHoliday, boolean bHolidaySUNDAY, int iActiveMonth,
			String message, String ids) {
		iDateYear = iYear;
		iDateMonth = iMonth;
		iDateDay = iDay;

		this.sDate = Integer.toString(iDateDay);
		this.bIsActiveMonth = (iDateMonth == iActiveMonth);
		this.bToday = bToday;
		this.bHoliday = bHoliday;
		this.bHolidaySUNDAY = bHolidaySUNDAY;
		this.message = message;
		this.ids = ids;
	}

	public String getMessageIds() {
		return ids;
	}
}
