package ubicomp.soberdiary.statistic.ui;

import java.util.ArrayList;

import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.main.ui.TextSize;
import ubicomp.soberdiary.main.ui.Typefaces;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;

@SuppressLint("ViewConstructor")
public class RadarChart4 extends View {

	private ArrayList<Double> scoreList;
	private ArrayList<String> labelList;
	private String title;
	private PointF topCorner,leftCorner,rightCorner, bottomCorner,center;
	private PointF p0,p1,p2,p3;
	
	private Paint chartLine, valueLine, valuePaint, titlePaint, labelPaint;
	private TextPaint labelTextPaint;
	
	private Typeface wordTypeface,wordTypefaceBold;
	private int titleSize,labelSize;
	
	private int type = -1;
	
	public RadarChart4(Context context,ArrayList<Double> scoreList,ArrayList<String> labelList,String title) {
		super(context);
		this.scoreList = scoreList;
		this.labelList = labelList;
		this.title = title;
		this.setBackgroundResource(R.drawable.toast_small);
		wordTypefaceBold = Typefaces.getWordTypefaceBold();
		wordTypeface = Typefaces.getWordTypeface();
		titleSize = TextSize.largeTitleSize();
		labelSize = TextSize.normalTextSize();
		createPaints();
		type = -1;
	}
	
	private void createPaints(){
		chartLine = new Paint();
		chartLine.setColor(0xFFababab);
		chartLine.setStyle(Style.STROKE);
		chartLine.setStrokeWidth(2);
		
		valueLine = new Paint();
		valueLine.setColor(0xFFF19700);
		valueLine.setStyle(Style.STROKE);
		valueLine.setStrokeWidth(3);
		
		valuePaint = new Paint();
		valuePaint.setColor(0xFFF19700);
		valuePaint.setStyle(Style.FILL);
		valuePaint.setAlpha(100);
		
		titlePaint = new Paint();
		titlePaint.setColor(0xFF000000);
		titlePaint.setTextSize(titleSize);
		titlePaint.setTypeface(wordTypefaceBold);
		titlePaint.setTextAlign(Align.LEFT);
		
		labelPaint = new Paint();
		labelPaint.setColor(0xFF000000);
		labelPaint.setTextSize(labelSize);
		labelPaint.setTypeface(wordTypeface);
		labelPaint.setTextAlign(Align.CENTER);
		
		labelTextPaint = new TextPaint();
		labelTextPaint.setColor(0xFF000000);
		labelTextPaint.setTextSize(labelSize);
		labelTextPaint.setTypeface(wordTypeface);
		labelTextPaint.setTextAlign(Align.CENTER);
		
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas){
		Rect area = new Rect(getLeft(),getTop(),getRight(),getBottom());
		int limit = Math.min(area.width(), area.height());
		
		topCorner = new PointF(limit/2,limit/5);
		leftCorner = new PointF(limit*3/20,limit*11/20);
		rightCorner = new PointF(limit*17/20,limit*11/20);
		bottomCorner = new PointF(limit/2,limit*9/10);
		center = new PointF((leftCorner.x+rightCorner.x)/2.f,(topCorner.y+bottomCorner.y)/2.f);

		canvas.drawLine(topCorner.x, topCorner.y, leftCorner.x, leftCorner.y, chartLine);
		canvas.drawLine(leftCorner.x, leftCorner.y, bottomCorner.x, bottomCorner.y, chartLine);
		canvas.drawLine(bottomCorner.x, bottomCorner.y, rightCorner.x, rightCorner.y,chartLine);
		canvas.drawLine(rightCorner.x, rightCorner.y, topCorner.x, topCorner.y, chartLine);
		canvas.drawLine(center.x, center.y, leftCorner.x, leftCorner.y, chartLine);
		canvas.drawLine(center.x, center.y, rightCorner.x, rightCorner.y, chartLine);
		canvas.drawLine(center.x, center.y, topCorner.x, topCorner.y, chartLine);
		canvas.drawLine(center.x, center.y, bottomCorner.x, bottomCorner.y, chartLine);
		
		canvas.drawLine(topCorner.x/3+center.x*2/3, topCorner.y/3+center.y*2/3, leftCorner.x/3+center.x*2/3, leftCorner.y/3+center.y*2/3, chartLine);
		canvas.drawLine(topCorner.x*2/3+center.x/3, topCorner.y*2/3+center.y/3, leftCorner.x*2/3+center.x/3, leftCorner.y*2/3+center.y/3, chartLine);
		canvas.drawLine(leftCorner.x/3+center.x*2/3, leftCorner.y/3+center.y*2/3, bottomCorner.x/3+center.x*2/3, bottomCorner.y/3+center.y*2/3, chartLine);
		canvas.drawLine(leftCorner.x*2/3+center.x/3, leftCorner.y*2/3+center.y/3, bottomCorner.x*2/3+center.x/3, bottomCorner.y*2/3+center.y/3, chartLine);
		canvas.drawLine(bottomCorner.x/3+center.x*2/3, bottomCorner.y/3+center.y*2/3, rightCorner.x/3+center.x*2/3, rightCorner.y/3+center.y*2/3, chartLine);
		canvas.drawLine(bottomCorner.x*2/3+center.x/3, bottomCorner.y*2/3+center.y/3, rightCorner.x*2/3+center.x/3, rightCorner.y*2/3+center.y/3, chartLine);
		canvas.drawLine(rightCorner.x/3+center.x*2/3, rightCorner.y/3+center.y*2/3, topCorner.x/3+center.x*2/3, topCorner.y/3+center.y*2/3, chartLine);
		canvas.drawLine(rightCorner.x*2/3+center.x/3, rightCorner.y*2/3+center.y/3, topCorner.x*2/3+center.x/3, topCorner.y*2/3+center.y/3, chartLine);
		
		double s0,s1,s2,s3;
		s0 = scoreList.get(0);
		s1 = scoreList.get(1);
		s2 = scoreList.get(2);
		s3 = scoreList.get(3);
		
		p0 = new PointF((float)(topCorner.x*s0 + center.x*(1-s0)),(float)(topCorner.y*s0 + center.y*(1-s0)));
		p1 = new PointF((float)(leftCorner.x*s1 + center.x*(1-s1)),(float)(leftCorner.y*s1 + center.y*(1-s1)));
		p2 = new PointF((float)(bottomCorner.x*s2 + center.x*(1-s2)),(float)(bottomCorner.y*s2 + center.y*(1-s2)));
		p3 = new PointF((float)(rightCorner.x*s3 + center.x*(1-s3)),(float)(rightCorner.y*s3 + center.y*(1-s3)));
		
		canvas.drawLine(p0.x, p0.y, p1.x, p1.y, valueLine);
		canvas.drawLine(p1.x, p1.y, p2.x, p2.y, valueLine);
		canvas.drawLine(p2.x, p2.y, p3.x, p3.y, valueLine);
		canvas.drawLine(p3.x, p3.y, p0.x, p0.y, valueLine);
		
		Path path = new Path();
		path.moveTo(p0.x, p0.y);
		path.lineTo(p1.x, p1.y);
		path.lineTo(p2.x, p2.y);
		path.lineTo(p3.x, p3.y);
		path.lineTo(p0.x, p0.y);
		
		canvas.drawPath(path, valuePaint);
		
		canvas.drawText(title, limit/10, limit/10, titlePaint);
		canvas.drawText(labelList.get(0),topCorner.x , topCorner.y-labelSize/2, labelPaint);
		canvas.drawText(labelList.get(2),bottomCorner.x , bottomCorner.y+labelSize, labelPaint);
		
		//canvas.drawRect(topCorner.x - labelSize*3, topCorner.y - labelSize*2, topCorner.x + labelSize*3, topCorner.y + labelSize, valuePaint);
		//canvas.drawRect(bottomCorner.x - labelSize*3, bottomCorner.y - labelSize, bottomCorner.x + labelSize*3, bottomCorner.y + labelSize*2, valuePaint);
		
		String label1 = labelList.get(1);
		StaticLayout layout1 = new StaticLayout(label1, labelTextPaint, labelSize, Layout.Alignment.ALIGN_CENTER, 1.f, 0.f, true);
		canvas.save();
		canvas.translate(leftCorner.x - labelSize, leftCorner.y - label1.length()/2*labelSize);
		layout1.draw(canvas);
		canvas.restore();
		
		//canvas.drawRect(leftCorner.x - labelSize*2, leftCorner.y - labelSize*3, leftCorner.x, leftCorner.y + labelSize*3, valuePaint);
		
		String label3 = labelList.get(3);
		StaticLayout layout3 = new StaticLayout(label3, labelTextPaint, labelSize, Layout.Alignment.ALIGN_CENTER, 1.f, 0.f, true);
		canvas.save();
		canvas.translate(rightCorner.x + labelSize, rightCorner.y - label3.length()/2*labelSize);
		layout3.draw(canvas);
		canvas.restore();
		
		//canvas.drawRect(rightCorner.x, rightCorner.y - labelSize*3, rightCorner.x+labelSize*2, rightCorner.y + labelSize*3, valuePaint);
	}

	
	private int temp_type = -1;
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		Rect area = new Rect(getLeft(),getTop(),getRight(),getBottom());
		int limit = Math.min(area.width(), area.height());
		
		topCorner = new PointF(limit/2,limit/5);
		leftCorner = new PointF(limit*3/20,limit*11/20);
		rightCorner = new PointF(limit*17/20,limit*11/20);
		bottomCorner = new PointF(limit/2,limit*9/10);
		Rect r0,r1,r2,r3;
		r0 = new Rect();
		r0.left = (int) (topCorner.x - labelSize*4);
		r0.top = (int) (topCorner.y - labelSize*3);
		r0.right = (int) (topCorner.x + labelSize*4);
		r0.bottom = (int) (topCorner.y + labelSize*2);
		
		r1 = new Rect();
		r1.left = (int) (leftCorner.x - labelSize*3);
		r1.top = (int) (leftCorner.y - labelSize*4);
		r1.right = (int) (leftCorner.x + labelSize);
		r1.bottom = (int) (leftCorner.y + labelSize*4);
		
		r2 = new Rect();
		r2.left = (int) (bottomCorner.x - labelSize*4);
		r2.top = (int) (bottomCorner.y - labelSize*2);
		r2.right = (int) (bottomCorner.x + labelSize*4);
		r2.bottom = (int) (bottomCorner.y + labelSize*2);
		
		r3 = new Rect();
		r3.left = (int) (rightCorner.x - labelSize);
		r3.top = (int) (rightCorner.y - labelSize*4);
		r3.right = (int) (rightCorner.x+labelSize*3);
		r3.bottom = (int) (rightCorner.y + labelSize*4);
		
		int x = (int)event.getX();
		int y = (int)event.getY();
		
		if (event.getAction() == MotionEvent.ACTION_DOWN){
			if (r0.contains(x, y))
				temp_type = 0;
			else if (r1.contains(x, y))
				temp_type = 1;
			else if (r2.contains(x, y))
				temp_type = 2;
			else if (r3.contains(x, y))
				temp_type = 3;
			else
				temp_type = -1;
			type = -1;
		}else if (event.getAction() == MotionEvent.ACTION_UP){
			if (r0.contains(x, y) && temp_type == 0){
				type = 0;
			}else if (r1.contains(x, y) && temp_type == 1){
				type = 1;
			}else if (r2.contains(x, y) && temp_type == 2){
				type = 2;
			}else if (r3.contains(x, y) && temp_type == 3){
				type = 3;
			}else
				type = -1;
			temp_type = -1;
		}
		return super.onTouchEvent(event);
	}
	
	public int getType(){
		return type;
	}
}
