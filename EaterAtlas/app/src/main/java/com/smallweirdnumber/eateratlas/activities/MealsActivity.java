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

import com.smallweirdnumber.eateratlas.enums.Meal;
import com.smallweirdnumber.eateratlas.helpers.FireData;
import com.smallweirdnumber.eateratlas.helpers.ScreenHelper;
import com.smallweirdnumber.eateratlas.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MealsActivity extends AppCompatActivity {

    private View mShade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals);

        // Set up shade
        mShade = findViewById(R.id.weekday_shade);
        if (mShade != null) {
            mShade.animate().translationY(
                    -ScreenHelper.getScreenMetrics(this).heightPixels);
        }

        // Set up header
        View mealHeader = findViewById(R.id.meal_header);
        if (mealHeader != null) {
            mealHeader.setPadding(
                    mealHeader.getPaddingLeft(),
                    ScreenHelper.getStatusBarHeight(this) + mealHeader.getPaddingTop(),
                    mealHeader.getPaddingRight(),
                    mealHeader.getPaddingBottom());
        }

        // Set up list
        ListView mealList = (ListView) findViewById(R.id.meal_list);
        ArrayList<Meal> meals = new ArrayList<>();
        Collections.addAll(meals, Meal.values());
        if (mealList != null) {
            mealList.setAdapter(new MealAdapter(this, meals));
            mealList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mShade != null) {
                        FireData.setMeal(Meal.values()[position]);
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
                            MealsActivity.super.onBackPressed();
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

    public static class MealAdapter extends ArrayAdapter<Meal> {
        // View lookup cache
        private static class ViewHolder {
            Button name;
        }

        public MealAdapter(Context context, List<Meal> meals) {
            super(context, R.layout.selector_list_item, meals);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Meal meal = getItem(position);
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
            viewHolder.name.setText(meal.toString());

            // Return the completed view to render on screen
            return convertView;
        }
    }
}
