package ubicomp.soberdiary.storytelling.ui;

import ubicomp.soberdiary.main.App;
import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.main.ui.Typefaces;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.View;

public class ChartYAxisView extends View {

	private Paint text_paint_small = new Paint();
	private String high;
	
	private int bar_width = App.context.getResources().getDimensionPixelSize(R.dimen.chart_bar_size);
	private int bar_bottom = App.context.getResources().getDimensionPixelSize(R.dimen.chart_bar_margin);
	private int chartHeight = App.context.getResources().getDimensionPixelSize(R.dimen.chart_height);
	private int textSize = App.context.getResources().getDimensionPixelSize(R.dimen.sn_text_size);
	
	private int chart_type = 0;
	
	public ChartYAxisView(Context context, AttributeSet attrs) {
		super(context, attrs);
		text_paint_small.setColor(0xFF3c3b3b);
		text_paint_small.setTextAlign(Align.CENTER);
		text_paint_small.setTextSize(textSize);
		text_paint_small.setTypeface(Typefaces.getDigitTypeface());
		high = getResources().getString(R.string.high);
		bar_width = App.context.getResources().getDimensionPixelSize(R.dimen.chart_bar_size);
	}
	
	public void setChartType(int type){
		chart_type = type;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int max_height = (chartHeight - bar_bottom) * 4 / 10;
		int _bottom = chartHeight - bar_bottom;

		// Draw Y axis label
		canvas.drawText("0", 3 * bar_width / 2, _bottom, text_paint_small);
		String maxLabel;
		if (chart_type == 0)
			maxLabel = "5";
		else if (chart_type == 1)
			maxLabel = "10";
		else if (chart_type == 2)
			maxLabel = "0.5";
		else
			maxLabel = high;
		canvas.drawText(maxLabel, 3 * bar_width / 2, _bottom - max_height, text_paint_small);
	}

}