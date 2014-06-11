package at.juggle.speedometer.Speedo;
/*
    Copyright (C) 2014  Mathias Lux, mathias@juggle.at

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    Note that the code for the LocationManager has been adopted from
    GPSSpeedo https://code.google.com/p/gpspeedo/ (GPL v3)
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mlux on 11.06.14.
 */
public class SpeedView extends View {

    private final Paint textPaint = new Paint();
    private final Paint satPaint = new Paint();
    private final Paint greenPaint = new Paint();
    private final Paint yellowPaint = new Paint();
    private final Paint redPaint = new Paint();
    private int speed = -1;
    private int satellites = 0;

    public SpeedView(Context context) {
        super(context);
        init();
    }

    public SpeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        textPaint.setColor(Color.GRAY);
        textPaint.setTextSize(172f);
        satPaint.setColor(Color.GRAY);
        satPaint.setTextSize(24f);
        greenPaint.setColor(Color.GREEN);
        greenPaint.setStrokeWidth(12f);
        yellowPaint.setColor(Color.YELLOW);
        yellowPaint.setStrokeWidth(12f);
        redPaint.setColor(Color.RED);
        redPaint.setStrokeWidth(12f);
        setKeepScreenOn(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        float w = canvas.getWidth();
        float offset = w / 10f;
        float speedFactor = 5*offset * 8f / 200f;
        Paint p = greenPaint;
        if (speed > -1) {
            canvas.drawText(speed + " km/h", offset, (getHeight() - textPaint.getTextSize()) / 2, textPaint);
            for (int i = 0; i <= Math.floor(speed / 5); i++) {
                if (i*5>50) p=yellowPaint;
                else if (i*5>100) p=redPaint;
                canvas.drawRect(offset + i*speedFactor, getHeight() - offset - 128,
                        offset + (i+1)*speedFactor-2,
                        getHeight() - offset, p);
            }
        } else {
            canvas.drawText("no GPS fix yet.", offset, (getHeight() - textPaint.getTextSize()) / 2, textPaint);
            double v = Math.random() * 150;
            for (int i = 0; i <= Math.floor(v / 5); i++) {
                if (i*5>50) p=yellowPaint;
                else if (i*5>100) p=redPaint;
                canvas.drawRect(offset + i*speedFactor, getHeight() - offset - 128,
                        offset + (i+1)*speedFactor-2,
                        getHeight() - offset, p);
            }
        }
        canvas.drawText(satellites + " satellites", offset, 100, satPaint);
    }

    public void setSpeed(int speed) {
        this.speed = speed;
        invalidate();
    }

    public void setSatellites(int satellites) {
        this.satellites = satellites;
    }
}
