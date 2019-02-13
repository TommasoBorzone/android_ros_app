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

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

/**
 * @author tom.borzone@google.com (Tommaso Borzone)
 */

public class ChildActivity extends AppCompatActivity {

    public String input;
    public ArrayList<Point> points = new ArrayList<>();
    public int number_of_points;
    public static final String EXTRA_POINTS_REPLY =
            "org.ros.android.android_client.extra.SHAPE_REPLY";
    public static final String EXTRA_WINDOW_SIZE_REPLY =
            "org.ros.android.android_client.extra.WINDOW_SIZE_REPLY";
    DrawCircleView mDrawCircleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_activity_layout);

        Intent intent = getIntent();
        input            = intent.getStringExtra(MainActivity.EXTRA_INPUT);
        number_of_points = intent.getIntExtra(MainActivity.EXTRA_NUMBER_OF_POINTS,3);

        mDrawCircleView = findViewById(R.id.activity_draw_circle);
        mDrawCircleView.setUserInput(input);
    }

    public void AcceptButton(View view) {
        Intent replyIntent = new Intent();

        Point ll = mDrawCircleView.getLowerLeftCorner();
        Point ur = mDrawCircleView.getUpperRightCorner();

        double a = (ur.x - ll.x)/2;
        double b = (ur.y - ll.y)/2;
        double increment = (2 * Math.PI / number_of_points);

        for (int counter=0;counter<number_of_points;counter++) {
            Point p = new Point(0, 0);
            p.x = Math.round((float) (a * Math.cos(increment * counter)));
            p.y = Math.round((float) (b * Math.sin(increment * counter)));
            points.add(p);
        }

        int windowSize = mDrawCircleView.getWidth();
        replyIntent.putParcelableArrayListExtra(EXTRA_POINTS_REPLY, points);
        replyIntent.putExtra(EXTRA_WINDOW_SIZE_REPLY, windowSize);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

}
