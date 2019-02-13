/*
 * Copyright (C) 2019 Tommaso Borzone.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ros.android.android_ros_app_component;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * @author tom.borzone@google.com (Tommaso Borzone)
 */

public class DrawCircleView extends View {

    Point[] points = new Point[4];
    String mUserInput;

    /**
     * point1 and point 3 are of same group and same as point 2 and point4
     */
    int groupId = -1;
    private ArrayList<ColorBall> colorballs = new ArrayList<ColorBall>();
    // array that holds the balls
    private int balID = 0;
    // variable to know what ball is being dragged
    Paint paint;
    Canvas canvas;

    public DrawCircleView(Context context) {
        super(context);
        paint = new Paint();
        setFocusable(true); // necessary for getting the touch events
        canvas = new Canvas();
    }

    public DrawCircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DrawCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        setFocusable(true); // necessary for getting the touch events
        canvas = new Canvas();

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DrawCircleView,
                0, 0);

        try {
            mUserInput = a.getString(R.styleable.DrawCircleView_userInput);
        } finally {
            a.recycle();
        }

    }

    public String getUserInput() {
        return mUserInput;
    }

    public void setUserInput(String input) {
        mUserInput = input;
        invalidate();
        requestLayout();
    }

    // the method that draws the balls
    @Override
    protected void onDraw(Canvas canvas) {
        if(points[3]==null) //point4 null when user did not touch and move on screen.
            return;
        int left, top, right, bottom;
        left = points[0].x;
        top = points[0].y;
        right = points[0].x;
        bottom = points[0].y;

        for (int i = 1; i < points.length; i++) {
            left = left > points[i].x ? points[i].x:left;
            top = top > points[i].y ? points[i].y:top;
            right = right < points[i].x ? points[i].x:right;
            bottom = bottom < points[i].y ? points[i].y:bottom;
        }
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(5);

        //draw stroke
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#AADB1255"));
        paint.setStrokeWidth(10);
        int l, t, b, r;
        l = left + colorballs.get(0).getWidthOfBall() / 2;
        r = right + colorballs.get(2).getWidthOfBall() / 2;
        b = bottom + colorballs.get(2).getWidthOfBall() / 2;
        t = top + colorballs.get(0).getWidthOfBall() / 2;
        float cx = (l + r) / 2;
        float cy = (t + b) / 2;
        double radius = Math.hypot(t - b, r - l) / 2;
        canvas.drawOval(l,t,r,b,paint);
        //canvas.drawCircle(cx, cy, (float) radius, paint);

        //draw the corners
        BitmapDrawable bitmap = new BitmapDrawable();
        // draw the balls on the canvas
        paint.setColor(Color.BLUE);
        paint.setTextSize(18);
        paint.setStrokeWidth(0);
        for (int i =0; i < colorballs.size(); i ++) {
            ColorBall ball = colorballs.get(i);
            canvas.drawBitmap(ball.getBitmap(), ball.getX(), ball.getY(),
                    paint);

            canvas.drawText("" + (i+1), ball.getX(), ball.getY(), paint);
        }
    }

    // events when touching the screen
    public boolean onTouchEvent(MotionEvent event) {
        int eventaction = event.getAction();

        int X = (int) event.getX();
        int Y = (int) event.getY();

        switch (eventaction) {

            case MotionEvent.ACTION_DOWN: // touch down so check if the finger is on
                // a ball
                if (points[0] == null) {
                    //initialize rectangle.
                    points[0] = new Point();
                    points[0].x = X;
                    points[0].y = Y;

                    points[1] = new Point();
                    points[1].x = X;
                    points[1].y = Y + 100;

                    points[2] = new Point();
                    points[2].x = X + 100;
                    points[2].y = Y + 100;

                    points[3] = new Point();
                    points[3].x = X +100;
                    points[3].y = Y;

                    balID = 2;
                    groupId = 1;
                    // declare each ball with the ColorBall class
                    for (Point pt : points) {
                        colorballs.add(new ColorBall(getContext(), R.drawable.center_widget, pt));
                    }
                } else {
                    //resize rectangle
                    balID = -1;
                    groupId = -1;
                    for (int i = colorballs.size()-1; i>=0; i--) {
                        ColorBall ball = colorballs.get(i);
                        // check if inside the bounds of the ball (circle)
                        // get the center for the ball
                        int centerX = ball.getX() + ball.getWidthOfBall();
                        int centerY = ball.getY() + ball.getHeightOfBall();
                        paint.setColor(Color.CYAN);
                        // calculate the radius from the touch to the center of the
                        // ball
                        double radCircle = Math
                                .sqrt((double) (((centerX - X) * (centerX - X)) + (centerY - Y)
                                        * (centerY - Y)));

                        if (radCircle < ball.getWidthOfBall()) {

                            balID = ball.getID();
                            if (balID == 1 || balID == 3) {
                                groupId = 2;
                            } else {
                                groupId = 1;
                            }
                            invalidate();
                            break;
                        }
                        invalidate();
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE: // touch drag with the ball


                if (balID > -1) {
                    // move the balls the same as the finger
                    colorballs.get(balID).setX(X);
                    colorballs.get(balID).setY(Y);

                    paint.setColor(Color.CYAN);
                    if (groupId == 1) {
                        colorballs.get(1).setX(colorballs.get(0).getX());
                        colorballs.get(1).setY(colorballs.get(2).getY());
                        colorballs.get(3).setX(colorballs.get(2).getX());
                        colorballs.get(3).setY(colorballs.get(0).getY());
                    } else {
                        colorballs.get(0).setX(colorballs.get(1).getX());
                        colorballs.get(0).setY(colorballs.get(3).getY());
                        colorballs.get(2).setX(colorballs.get(3).getX());
                        colorballs.get(2).setY(colorballs.get(1).getY());
                    }

                    invalidate();
                }

                break;

            case MotionEvent.ACTION_UP:
                // touch drop - just do things here after dropping

                break;
        }
        // redraw the canvas
        invalidate();
        return true;

    }

    public Point getLowerLeftCorner() {
        int llx = Math.min(colorballs.get(0).getX(), colorballs.get(1).getX());
        int LLx = Math.min(llx, colorballs.get(2).getX());
        int lly = Math.min(colorballs.get(0).getY(), colorballs.get(1).getY());
        int LLy = Math.min(lly, colorballs.get(2).getY());

        Point llCorner = new Point(LLx, LLy);
        return llCorner;
    }

    public Point getUpperLeftCorner() {
        int ulx = Math.min(colorballs.get(0).getX(), colorballs.get(1).getX());
        int ULx = Math.min(ulx, colorballs.get(2).getX());
        int uly = Math.max(colorballs.get(0).getY(), colorballs.get(1).getY());
        int ULy = Math.max(uly, colorballs.get(2).getY());

        Point ulCorner = new Point(ULx, ULy);
        return ulCorner;
    }

    public Point getUpperRightCorner() {
        int urx = Math.max(colorballs.get(0).getX(), colorballs.get(1).getX());
        int URx = Math.max(urx, colorballs.get(2).getX());
        int ury = Math.max(colorballs.get(0).getY(), colorballs.get(1).getY());
        int URy = Math.max(ury, colorballs.get(2).getY());

        Point urCorner = new Point(URx, URy);
        return urCorner;
    }

    public Point getLowerRightCorner() {
        int lrx = Math.max(colorballs.get(0).getX(), colorballs.get(1).getX());
        int LRx = Math.max(lrx, colorballs.get(2).getX());
        int lry = Math.min(colorballs.get(0).getY(), colorballs.get(1).getY());
        int LRy = Math.min(lry, colorballs.get(2).getY());

        Point lrCorner = new Point(LRx, LRy);
        return lrCorner;
    }

    public static class ColorBall {

        Bitmap bitmap;
        Context mContext;
        Point point;
        int id;
        static int count = 0;

        public ColorBall(Context context, int resourceId, Point point) {
            this.id = count++;
            bitmap = BitmapFactory.decodeResource(context.getResources(),
                    resourceId);
            mContext = context;
            this.point = point;
        }

        public int getWidthOfBall() {
            return bitmap.getWidth();
        }

        public int getHeightOfBall() {
            return bitmap.getHeight();
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public int getX() {
            return point.x;
        }

        public int getY() {
            return point.y;
        }

        public int getID() {
            return id;
        }

        public void setX(int x) {
            point.x = x;
        }

        public void setY(int y) {
            point.y = y;
        }
    }
}
