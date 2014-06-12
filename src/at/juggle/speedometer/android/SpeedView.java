package at.juggle.speedometer.android;
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
import android.graphics.Typeface;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mlux on 11.06.14.
 * Colors from https://kuler.adobe.com/Hot-Girls-And-Traffic-Lights-color-theme-373931/
 */
public class SpeedView extends View {

    private final Paint textPaint = new Paint();
    private final Paint satPaint = new Paint();
    private final Paint colPaint1 = new Paint();
    private final Paint colPaint2 = new Paint();
    private final Paint colPaint3 = new Paint();
    private final Paint colPaint4 = new Paint();
    private final Paint colPaint5 = new Paint();
    private int speed = -1;
    private int satellites = 0;
    private Paint gradPaint = null;
    private float maxSpeedInVisualization = 150f;

    private float maxSpeed = 0;

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
        Typeface elektraFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/ELEKTRA_.ttf");
        textPaint.setTypeface(elektraFont);

        satPaint.setColor(Color.GRAY);
        satPaint.setTextSize(32f);
        satPaint.setTypeface(elektraFont);

        colPaint1.setColor(Color.argb(255, 5, 89, 2));
        colPaint1.setStrokeWidth(12f);

        colPaint2.setColor(Color.argb(255, 242, 183, 5));
        colPaint2.setStrokeWidth(12f);

        colPaint3.setColor(Color.argb(255, 242, 116, 5));
        colPaint3.setStrokeWidth(12f);

        colPaint4.setColor(Color.argb(255, 242, 5, 5));
        colPaint4.setStrokeWidth(12f);

        colPaint5.setColor(Color.argb(255, 115, 2, 2));
        colPaint5.setStrokeWidth(12f);

        setKeepScreenOn(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        maxSpeed = Math.max(maxSpeed, speed);
        float w = canvas.getWidth();
        float offsetX = w / 10f;
        float offsetY = canvas.getHeight() / 10f;
        float speedFactor = 5 * offsetX * 8f / maxSpeedInVisualization;
        Paint p = colPaint1;
        textPaint.setTextSize(offsetY * 7f);
        // setting text to red if speed > 135 km/h
        if (speed > 135)
            textPaint.setColor(Color.argb(255, 242, 5, 5));
        else
            textPaint.setColor(Color.GRAY);
        satPaint.setTextSize(offsetY / 3f);

        if (gradPaint == null) {
            gradPaint = new Paint();
            gradPaint.setShader(new LinearGradient(0, 0, 0, getHeight(), Color.BLACK, Color.argb(255, 32, 32, 128), Shader.TileMode.CLAMP));
        }

        canvas.drawPaint(gradPaint);

        if (speed > -1) {
            String s = getStringFromSpeed(speed);
            canvas.drawText(s, offsetX, getHeight() - offsetY * 5, textPaint);
            for (int i = 0; i * 5 <= Math.min(maxSpeedInVisualization, speed); i++) {
                if (i * 5 > 30) p = colPaint2;
                if (i * 5 > 50) p = colPaint3;
                if (i * 5 > 100) p = colPaint4;
                if (i * 5 > 130) p = colPaint5;
                canvas.drawRect(offsetX + i * speedFactor, getHeight() - offsetY * 4,
                        offsetX + (i + 1) * speedFactor - 4,
                        getHeight() - offsetY, p);
            }
        } else {
            canvas.drawText("NFX", offsetX, getHeight() - offsetY * 5, textPaint);
            double v = Math.random()*maxSpeedInVisualization;
            for (int i = 0; i * 5 <= v; i++) {
                if (i * 5 >= 30) p = colPaint2;
                if (i * 5 >= 50) p = colPaint3;
                if (i * 5 >= 100) p = colPaint4;
                if (i * 5 >= 130) p = colPaint5;
                canvas.drawRect(offsetX + i * speedFactor, getHeight() - offsetY * 4,
                        offsetX + (i + 1) * speedFactor - 4,
                        getHeight() - offsetY, p);
            }
        }
//        canvas.drawText(satellites + " satellites", offsetX, offsetY, satPaint);
        String s = "max:  " + getStringFromSpeed((int) maxSpeed) + " km/h";
        Rect rect = new Rect();
        satPaint.getTextBounds(s, 0, s.length(), rect);
        canvas.drawText(s, getWidth() - offsetX - rect.width(), offsetY, satPaint);
    }

    private String getStringFromSpeed(int speed) {
        String s = speed + "";
        if (9 < speed && speed < 100) s = "0" + speed;
        else if (speed <= 9) s = "00" + speed;
        return s;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
        invalidate();
    }

    public void setSatellites(int satellites) {
        this.satellites = satellites;
    }
}
