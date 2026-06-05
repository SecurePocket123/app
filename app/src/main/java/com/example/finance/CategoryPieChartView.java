package com.example.finance;

import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CategoryPieChartView extends View {

    private List<Float> values = new ArrayList<>();
    private int[] colors = new int[] {
            Color.parseColor("#6FA8FF"),
            Color.parseColor("#4F8DF7"),
            Color.parseColor("#2F6EDB"),
            Color.parseColor("#1F4FAE"),
            Color.parseColor("#7C6AA6")
    };

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint holePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public CategoryPieChartView(Context context) {
        super(context);
        init();
    }

    public CategoryPieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        holePaint.setColor(Color.parseColor("#155FA0"));
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(42);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setValues(List<Float> values) {
        this.values = values;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float total = 0;
        for (Float value : values) {
            total += value;
        }

        int size = Math.min(getWidth(), getHeight());
        float padding = size * 0.08f;
        RectF rect = new RectF(
                (getWidth() - size) / 2f + padding,
                (getHeight() - size) / 2f + padding,
                (getWidth() + size) / 2f - padding,
                (getHeight() + size) / 2f - padding
        );

        if (total <= 0) {
            paint.setColor(Color.parseColor("#2F6EDB"));
            canvas.drawArc(rect, 0, 360, true, paint);
        } else {
            float startAngle = -90;
            for (int i = 0; i < values.size(); i++) {
                float sweep = values.get(i) / total * 360f;
                paint.setColor(colors[i % colors.length]);
                canvas.drawArc(rect, startAngle, sweep, true, paint);
                startAngle += sweep;
            }
        }

        float radius = rect.width() * 0.30f;
        canvas.drawCircle(rect.centerX(), rect.centerY(), radius, holePaint);
        canvas.drawText("Ausgaben", rect.centerX(), rect.centerY() + 14, textPaint);
    }
}
