package com.smallweirdnumber.eateratlas.activities;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.smallweirdnumber.eateratlas.enums.Weekday;
import com.smallweirdnumber.eateratlas.R;
import com.smallweirdnumber.eateratlas.helpers.FireData;
import com.smallweirdnumber.eateratlas.helpers.ScreenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeekdaysActivity extends AppCompatActivity {

    private View mShade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekdays);

        // Set up shade
        mShade = findViewById(R.id.weekday_shade);
        if (mShade != null) {
            mShade.animate().translationY(
                    -ScreenHelper.getScreenMetrics(this).heightPixels);
        }

        // Set up header
        View weekdayHeader = findViewById(R.id.weekday_header);
        if (weekdayHeader != null) {
            weekdayHeader.setPadding(
                    weekdayHeader.getPaddingLeft(),
                    ScreenHelper.getStatusBarHeight(this) + weekdayHeader.getPaddingTop(),
                    weekdayHeader.getPaddingRight(),
                    weekdayHeader.getPaddingBottom());
        }

        // Set up list
        ListView weekdayList = (ListView) findViewById(R.id.weekday_list);
        ArrayList<Weekday> weekdays = new ArrayList<>();
        Collections.addAll(weekdays, Weekday.values());
        weekdayList.setAdapter(new WeekdayAdapter(this, weekdays));
        weekdayList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mShade != null) {
                    FireData.setWeekday(Weekday.values()[position]);
                    mShade.animate()
                            .translationY(0)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    finish();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
                            });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mShade != null) {
            mShade.animate()
                    .translationY(0)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            WeekdaysActivity.super.onBackPressed();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
        }
    }

    public static class WeekdayAdapter extends ArrayAdapter<Weekday> {
        // View lookup cache
        private static class ViewHolder {
            Button name;
        }

        public WeekdayAdapter(Context context, List<Weekday> weekdays) {
            super(context, R.layout.selector_list_item, weekdays);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Weekday weekday = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.selector_list_item, parent, false);
                viewHolder.name = (Button) convertView.findViewById(R.id.selector_text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Populate the data into the template view using the data object
            viewHolder.name.setText(weekday.toString());

            // Return the completed view to render on screen
            return convertView;
        }
    }
}
