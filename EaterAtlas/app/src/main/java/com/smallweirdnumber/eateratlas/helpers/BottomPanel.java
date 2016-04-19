package com.smallweirdnumber.eateratlas.helpers;


import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.google.android.gms.maps.model.Marker;
import com.smallweirdnumber.eateratlas.R;
import com.smallweirdnumber.eateratlas.activities.MapsActivity;
import com.smallweirdnumber.eateratlas.models.Gig;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BottomPanel {

    private static final int PANEL_FADE_DURATION = 250;
    private static MapsActivity mMapsActivity;
    private static SlidingUpPanelLayout mPanel;
    private static TextView mTitle, mSubtitle, mDefault;
    private static View mHeader, mContainer, mGigInfo;
    private static ListView mTruckList, mGigList;
    private static SlidingUpPanelLayout.PanelState mBackPanelState;

    public static void bind(Context context) {
        mMapsActivity = (MapsActivity) context;
        mTitle = (TextView) findViewById(R.id.panel_header_title);
        mSubtitle = (TextView) findViewById(R.id.panel_header_subtitle);
        mDefault = (TextView) findViewById(R.id.panel_header_title_default);
        mHeader = findViewById(R.id.panel_header);
        mContainer = findViewById(R.id.panel_header_container);
        mGigInfo = findViewById(R.id.panel_gig);
        mPanel = (SlidingUpPanelLayout) findViewById(R.id.panel);
        mTruckList = (ListView) findViewById(R.id.panel_list);
        mGigList = (ListView) findViewById(R.id.gig_list);
        mPanel.setPanelSlideListener(new PanelSlideListener());
    }

    public static boolean goBack() {
        if (mBackPanelState != null) {
            mPanel.setPanelState(mBackPanelState);
            return true;
        } else {
            return false;
        }
    }

    public static void collapse() {
        if (mPanel != null) {
            mPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }

    public static void expand() {
        if (mPanel != null) {
            mPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
    }

    public static void enable() {
        if (mPanel != null) {
            mPanel.setTouchEnabled(true);
        }
    }

    public static void disable() {
        if (mPanel != null) {
            mPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            mPanel.setTouchEnabled(false);
        }
    }

    public static void reset() {
        darkenPanelHeader();
        disable();

        // Reset padding
        /*if (mHeader != null) {
            mHeader.setPadding(
                    mHeader.getPaddingLeft(),
                    (int) mMapsActivity.getResources().getDimension(R.dimen.panel_header_padding),
                    mHeader.getPaddingRight(),
                    mHeader.getPaddingBottom());
        }*/

        // Show default header
        if (mDefault != null && mDefault.getVisibility() != View.VISIBLE) {
            if (mContainer != null) {
                Animator anim1 = ViewAnimationUtils.createCircularReveal(mContainer,
                        0, mContainer.getHeight() / 2,
                        mContainer.getWidth(), 0);
                mContainer.setVisibility(View.GONE);
                anim1.start();
            }
            Animator anim2 = ViewAnimationUtils.createCircularReveal(mDefault,
                    mDefault.getWidth(), mDefault.getHeight() / 2,
                    0, mDefault.getWidth());
            mDefault.setVisibility(View.VISIBLE);
            anim2.start();
        }
    }

    public static void fillPanelWithTrucksAtSelectedMarker() {
        Marker marker = MapsHelper.getSelectedMarker();
        if (mMapsActivity != null && mTruckList != null && marker != null) {

            // Show trucks in panel
            List<String> trucks = FireData.getTrucks(marker.getPosition());
            mTruckList.setAdapter(new TruckAdapter(mMapsActivity, trucks));

            // Set item click listener
            mTruckList.setOnItemClickListener(new TruckClickListener(marker));

            // Set panel anchor
            if (mPanel != null) {
                float customAnchor = ((float) trucks.size()) / 4;
                float maxAnchor = Float.parseFloat(
                        mMapsActivity.getResources().getString(R.string.anchor));
                mPanel.setAnchorPoint(Math.min(customAnchor, maxAnchor));
            }
        }
    }

    public static void setHeaderFromSelectedMarker() {
        Marker marker = MapsHelper.getSelectedMarker();
        if (marker != null) {
            setTitle(FireData.getPlace(marker.getPosition()));
            int truckCount = FireData.getTruckCount(marker.getPosition());
            if (truckCount == 1) {
                setSubtitle(Integer.toString(truckCount) + " truck");
            } else {
                setSubtitle(Integer.toString(truckCount) + " trucks");
            }
            headerReveal();
        }
    }

    private static View findViewById(int id) {
        if (mMapsActivity != null) {
            return mMapsActivity.findViewById(id);
        } else {
            return null;
        }
    }

    private static void setTitle(String title) {
        if (mTitle != null) {
            mTitle.setText(title);
        }
    }

    private static void setSubtitle(String subtitle) {
        if (mSubtitle != null) {
            mSubtitle.setText(subtitle);
        }
    }

    private static void headerReveal() {
        if (mContainer != null && mDefault != null
                && mContainer.getVisibility() != View.VISIBLE) {
            Animator anim1 = ViewAnimationUtils.createCircularReveal(mDefault,
                    mDefault.getWidth(), 0, mDefault.getWidth(), 0);
            mDefault.setVisibility(View.GONE);
            anim1.start();
            Animator anim2 = ViewAnimationUtils.createCircularReveal(mContainer,
                    0, mContainer.getHeight() / 2,
                    0, mContainer.getWidth());
            mContainer.setVisibility(View.VISIBLE);
            anim2.start();
        }

    }

    private static void lightenPanelHeader() {
        if (mHeader != null) {
            int colorFrom = ColorHelper.getBackgroundColor(mMapsActivity, mHeader, R.color.green);
            int colorTo = ColorHelper.getColor(mMapsActivity, R.color.lightGreen);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(PANEL_FADE_DURATION);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    mHeader.setBackgroundColor((int) animator.getAnimatedValue());
                }
            });
            colorAnimation.start();
        }
        if (mTitle != null && mSubtitle != null) {
            int colorFrom = mTitle.getCurrentTextColor();
            int colorTo = ColorHelper.getColor(mMapsActivity, R.color.darkGreen);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(PANEL_FADE_DURATION);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    mTitle.setTextColor((int) animator.getAnimatedValue());
                    mSubtitle.setTextColor((int) animator.getAnimatedValue());
                }
            });
            colorAnimation.start();
        }
    }

    private static void darkenPanelHeader() {
        if (mHeader != null) {
            int colorFrom = ColorHelper.getBackgroundColor(
                    mMapsActivity, mHeader, R.color.lightGreen);
            int colorTo = ColorHelper.getColor(mMapsActivity, R.color.green);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(
                    new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(PANEL_FADE_DURATION);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    mHeader.setBackgroundColor((int) animator.getAnimatedValue());
                }
            });
            colorAnimation.start();
        }
        if (mTitle != null && mSubtitle != null) {
            int colorFrom = ColorHelper.getColor(mMapsActivity, R.color.darkGreen);
            int colorTo = ColorHelper.getColor(mMapsActivity, R.color.lightGreen);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(
                    new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(PANEL_FADE_DURATION);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    mTitle.setTextColor((int) animator.getAnimatedValue());
                    mSubtitle.setTextColor((int) animator.getAnimatedValue());
                }
            });
            colorAnimation.start();
        }
    }

    private static class PanelSlideListener implements SlidingUpPanelLayout.PanelSlideListener {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (mPanel != null) {
                    lightenPanelHeader();
                    float anchor = mPanel.getAnchorPoint();
                    int statusBarHeight = ScreenHelper.getStatusBarHeight(mMapsActivity);
                    int diff = mPanel.getHeight() - mPanel.getPanelHeight();
                    int max = ScreenHelper.getPixelsFromPercent(anchor, diff);
                    int cur = ScreenHelper.getPixelsFromPercent(slideOffset, diff);
                    int paddingTop = (int) mMapsActivity.getResources()
                            .getDimension(R.dimen.panel_header_padding) +
                            ScreenHelper.getPixelsFromPercent((slideOffset - anchor) /
                                    (1 - anchor), statusBarHeight);
                    if (cur <= max) {
                        // Push bottom padding up until panel anchor is hit
                        mPanel.setParallaxOffset(cur);
                    } else if (mHeader != null && cur > max) {
                        // Expand panel to fill status bar
                        mHeader.setPadding(
                                mHeader.getPaddingStart(),
                                paddingTop,
                                mHeader.getPaddingEnd(),
                                mHeader.getPaddingBottom());
                    }
                }
            }

            @Override
            public void onPanelCollapsed(View panel) {
                darkenPanelHeader();
                setHeaderFromSelectedMarker();
                resetHeader();
                mBackPanelState = null;
            }

            @Override
            public void onPanelExpanded(View panel) {
                mBackPanelState = SlidingUpPanelLayout.PanelState.ANCHORED;
            }

            @Override
            public void onPanelAnchored(View panel) {
                setHeaderFromSelectedMarker();
                resetHeader();
                mBackPanelState = SlidingUpPanelLayout.PanelState.COLLAPSED;
            }

            @Override
            public void onPanelHidden(View panel) {

            }

            private void resetHeader() {
                // TODO this could be prettier
                if (mHeader != null && mMapsActivity != null) {
                    int padding = (int) mMapsActivity.getResources()
                            .getDimension(R.dimen.panel_header_padding);
                    mHeader.setPadding(padding, padding, padding, padding);
                }
                if (mTruckList != null) {
                    mTruckList.setVisibility(View.VISIBLE);
                }
                if (mGigInfo != null) {
                    mGigInfo.setVisibility(View.GONE);
                }
            }
    }

    private static class TruckAdapter extends ArrayAdapter<String> {
        // View lookup cache
        private static class ViewHolder {
            TextView name;
        }

        public TruckAdapter(Context context, List<String> trucks) {
            super(context, R.layout.truck_list_item, trucks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            String truck = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.truck_list_item, parent, false);
                viewHolder.name = (TextView) convertView.findViewById(R.id.truck_text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Populate the data into the template view using the data object
            viewHolder.name.setText(truck);

            // Return the completed view to render on screen
            return convertView;
        }
    }

    private static class GigAdapter extends ArrayAdapter<Gig> {
        // View lookup cache
        private static class ViewHolder {
            TextView name;
            RangeBar range;
        }

        private Date mMin, mMax;
        private long mDuration;

        public GigAdapter(Context context, List<Gig> gigs) {
            super(context, R.layout.gig_list_item, gigs);
            for (Gig gig : gigs) {
                Date min, max;
                try {
                    min = gig.getStartDate();
                    max = gig.getStopDate();
                } catch (ParseException e) {
                    min = null;
                    max = null;
                }
                if (mMin == null || (min != null && min.before(mMin))) {
                    mMin = min;
                }
                if (mMax == null || (max != null && max.after(mMax))) {
                    mMax = max;
                }
            }
            if (mMin != null && mMax != null) {
                mDuration = mMax.getTime() - mMin.getTime();
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                // Get the data item for this position
                Gig gig = getItem(position);
                ViewHolder viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.gig_list_item, parent, false);
                viewHolder.name = (TextView) convertView.findViewById(R.id.info_text);
                viewHolder.name.setText(gig.getSource());
                viewHolder.name.setOnClickListener(new SourceClickListener(gig.getSource()));
                viewHolder.range = (RangeBar) convertView.findViewById(R.id.gig_range);
                viewHolder.range.setDrawTicks(false);
                viewHolder.range.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                Date startDate, stopDate;
                final long start, stop;
                try {
                    startDate = gig.getStartDate();
                    stopDate = gig.getStopDate();
                } catch (ParseException e) {
                    startDate = null;
                    stopDate = null;
                }
                if (startDate != null && stopDate != null && mMin != null && mMax != null) {
                    start = (long) Math.ceil((double)
                            (startDate.getTime() - mMin.getTime()) / mDuration * 100.0);
                    stop = 100 + (long) Math.floor((double)
                            (stopDate.getTime() - mMax.getTime()) / mDuration * 100.0);
                } else {
                    start = 0;
                    stop = 100;
                }

                viewHolder.range.setRangePinsByIndices((int) start + 10, (int) stop + 10);
                viewHolder.range.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                viewHolder.range.setPinTextFormatter(new RangeBar.PinTextFormatter() {
                    @Override
                    public String getText(String value) {
                        long dur = (long) ((double) mDuration * (Double.parseDouble(value) / 100.0));
                        Date date = new Date(mMin.getTime() + dur);
                        // Round to :30:00
                        if (date.getTime() % 1800000 != 0) {
                            date.setTime(date.getTime() + (1800000 - (date.getTime() % 1800000)));
                        }
                        // Format pin
                        SimpleDateFormat format;
                        if (date.getTime() % 3600000 == 0) {
                            format = new SimpleDateFormat("h a", Locale.US);
                        } else {
                            format = new SimpleDateFormat("h:mm a", Locale.US);
                        }
                        return format.format(date);
                    }
                });

                convertView.setTag(viewHolder);
            }

            return convertView;
        }
    }

    private static class TruckClickListener implements AdapterView.OnItemClickListener {

        private Marker mMarker;

        public TruckClickListener(Marker marker) {
            mMarker = marker;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            expand();
            String truck = (String) mTruckList.getItemAtPosition(position);
            setTitle(truck);
            setSubtitle(FireData.getPlace(mMarker.getPosition()));

            mTruckList.setVisibility(View.GONE);

            List<Gig> gigs = new ArrayList<>();
            Uri site = null;
            for (Gig gig : FireData.getGigs(mMarker.getPosition(), truck)) {
                if (site == null && gig.getSite() != null && !gig.getSite().isEmpty()) {
                    site = gig.getSiteUri();
                }
                gigs.add(gig);
            }
            View siteRipple = findViewById(R.id.site_ripple);
            Button siteText = (Button) findViewById(R.id.site_text);
            if (siteRipple != null && siteText != null && site != null) {
                siteRipple.setVisibility(View.VISIBLE);
                siteText.setText(site.getHost().replaceFirst("^www\\.", ""));
                siteText.setOnClickListener(new SiteClickListener(site));
            } else if (siteRipple != null) {
                siteRipple.setVisibility(View.GONE);
            }
            mGigList.setAdapter(new GigAdapter(mMapsActivity, gigs));

            if (mGigInfo != null) {
                mGigInfo.setVisibility(View.VISIBLE);
            }
        }
    }

    private static class SiteClickListener implements View.OnClickListener {

        private Uri mSite;

        public SiteClickListener(Uri site) {
            mSite = site;
        }

        @Override
        public void onClick(View v) {
            if (mMapsActivity != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(mSite);
                mMapsActivity.startActivity(intent);
            }
        }
    }

    private static class SourceClickListener implements View.OnClickListener {

        private Uri mSite;
        private String mSource;

        public SourceClickListener(String source) {
            mSource = source;
            if (mMapsActivity != null) {
                int id = getResId(source, R.string.class);
                if (id != -1) {
                    String site = mMapsActivity.getResources().getString(id);
                    mSite = Uri.parse(site);
                }

            }
        }

        private static int getResId(String resName, Class<?> c) {

            try {
                Field idField = c.getDeclaredField(resName);
                return idField.getInt(idField);
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        @Override
        public void onClick(View v) {
            if (mMapsActivity != null && mSite != null) {
                new AlertDialog.Builder(mMapsActivity)
                        .setMessage(Html.fromHtml(
                                mMapsActivity.getResources()
                                        .getString(R.string.source_info)
                                        + " <b>" + mSource + "</b>"))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        })
                        .setNegativeButton("OPEN SITE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(mSite);
                                mMapsActivity.startActivity(intent);
                            }
                        })
                        .create().show();
            }
        }
    }
}
