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

public class ChartLabelView extends View {

	private Paint emotion_paint = new Paint();
	private Paint desire_paint = new Paint();
	private Paint brac_paint = new Paint();
	private Paint text_paint = new Paint();
	private Paint paint_pass = new Paint();
	private Paint paint_fail = new Paint();

	private String[] type_str = new String[3];
	private String[] pass_str = new String[2];
	
	private int textSize = App.context.getResources().getDimensionPixelSize(R.dimen.sn_text_size);
	private int base = App.context.getResources().getDimensionPixelSize(R.dimen.chart_label_base);
	private int gap = App.context.getResources().getDimensionPixelSize(R.dimen.chart_label_gap);
	private int top = App.context.getResources().getDimensionPixelSize(R.dimen.chart_label_top);
	private int top2 = App.context.getResources().getDimensionPixelSize(R.dimen.chart_label_top2);
	
	private int chart_type = 0;
	
	public ChartLabelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		text_paint.setColor(0xFFAAAAAA);
		text_paint.setTextAlign(Align.CENTER);
		text_paint.setTextSize(textSize);
		text_paint.setTypeface(Typefaces.getWordTypefaceBold());

		emotion_paint.setColor(0xFF2dc7b3);
		desire_paint.setColor(0xFFf19700);
		brac_paint.setColor(0xFFFFFFFF);

		paint_pass.setColor(0xFF5bdebe);
		paint_fail.setColor(0xFFf09600);

		type_str[0] = getResources().getString(R.string.emotion_short);
		type_str[1] = getResources().getString(R.string.craving_short);
		type_str[2] = getResources().getString(R.string.brac_result_short);
		pass_str[0] = getResources().getString(R.string.test_pass);
		pass_str[1] = getResources().getString(R.string.test_fail);
	}

	public void setChartType(int type){
		chart_type = type;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int from = 0;
		int line_len = base * 5 / 2;
		if (chart_type == 3) {
			canvas.drawRect(from, top, from + base, base + top, emotion_paint);
			from += base + gap;
			canvas.drawText(type_str[0], from + line_len / 2, top2, text_paint);
			from += line_len + gap;

			canvas.drawRect(from, top, from + base, base + top, desire_paint);
			from += base + gap;
			canvas.drawText(type_str[1], from + line_len / 2, top2, text_paint);
			from += line_len + gap;

			canvas.drawRect(from, top, from + base, base + top, brac_paint);
			from += base + gap;
			canvas.drawText(type_str[2], from + line_len * 3 / 4, top2, text_paint);
		} else {
			// only two labels
			from += base + gap;
			from += line_len + gap;

			canvas.drawRect(from, top, from + base, base + top, paint_pass);
			from += base + gap;
			canvas.drawText(pass_str[0], from + line_len / 2, top2, text_paint);
			from += line_len + gap;

			canvas.drawRect(from, top, from + base, base + top, paint_fail);
			from += base + gap;
			canvas.drawText(pass_str[1], from + line_len * 3 / 4, top2, text_paint);
		}
	}
}
