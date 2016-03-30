package com.smallweirdnumber.eateratlas.helpers;

import android.content.Context;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.smallweirdnumber.eateratlas.enums.Meal;
import com.smallweirdnumber.eateratlas.enums.Weekday;
import com.smallweirdnumber.eateratlas.R;
import com.smallweirdnumber.eateratlas.activities.MapsActivity;
import com.smallweirdnumber.eateratlas.models.Gig;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FireData {

    private static MapsActivity mMapsActivity;
    private static Map<Weekday,Map<Meal,Map<LatLng,List<Gig>>>> mData;
    private static Weekday mWeekday = Weekday.getCurrent();
    private static Meal mMeal = Meal.getCurrent();

    public static void bind(Context context) {
        mMapsActivity = (MapsActivity) context;
        Firebase.setAndroidContext(mMapsActivity);
        if (!Firebase.getDefaultConfig().isPersistenceEnabled()) {
            Firebase.getDefaultConfig().setPersistenceEnabled(true);
        }
        Firebase ref = new Firebase(mMapsActivity.getResources().getString(R.string.firebase));
        ref.keepSynced(true);
        ref.addValueEventListener(new TruckEventListener());
    }

    public static Weekday getWeekday() {
        return mWeekday;
    }

    public static Meal getMeal() {
        return mMeal;
    }

    public static void setWeekday(Weekday weekday) {
        if (mWeekday != weekday) {
            mWeekday = weekday;
            mMapsActivity.setRedraw(true);
            //TopMenu.update(mWeekday, mMeal);
        }
    }

    public static void setMeal(Meal meal) {
        if (mMeal != meal) {
            mMeal = meal;
            mMapsActivity.setRedraw(true);
            //TopMenu.update(mWeekday, mMeal);
        }
    }

    public static void setSnapshot(DataSnapshot dataSnapshot){
        mData = new HashMap<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            Gig gig = snapshot.getValue(Gig.class);
            Weekday weekday = gig.getWeekdayEnum();
            Meal meal = gig.getMealEnum();
            LatLng latLng = gig.getLatLng();
            if (!mData.containsKey(weekday)) {
                mData.put(weekday, new HashMap<Meal, Map<LatLng, List<Gig>>>());
            }
            if (!mData.get(weekday).containsKey(meal)) {
                mData.get(weekday).put(meal, new HashMap<LatLng,List<Gig>>());
            }
            if (!mData.get(weekday).get(meal).containsKey(latLng)) {
                mData.get(weekday).get(meal).put(latLng, new ArrayList<Gig>());
            }
            mData.get(weekday).get(meal).get(latLng).add(gig);
        }
    }

    public static Set<LatLng> getLatLngs() {
        Set<LatLng> latLngs = new HashSet<>();
        if (mWeekday != null && mMeal != null
                && mData.containsKey(mWeekday)
                && mData.get(mWeekday).containsKey(mMeal)) {
             for (LatLng key : mData.get(mWeekday).get(mMeal).keySet()) {
                 if (!getGigs(key).isEmpty()) {
                     latLngs.add(key);
                 }
             }
        }
        return latLngs;
    }

    public static String getPlace(LatLng latLng) {
        return getGigs(latLng).iterator().next().getPlace();
    }

    public static Set<String> getTrucks(LatLng latLng) {
        Set<String> trucks = new HashSet<>();
        for (Gig gig : getGigs(latLng)) {
            trucks.add(gig.getTruck());
        }
        return trucks;
    }

    public static int getTruckCount(LatLng latLng) {
        return getTrucks(latLng).size();
    }

    public static List<Gig> getGigs(LatLng latLng) {
        List<Gig> gigs = new ArrayList<>();
        if (mData != null
                && mData.containsKey(mWeekday)
                && mData.get(mWeekday).containsKey(mMeal)
                && mData.get(mWeekday).get(mMeal).containsKey(latLng)) {
            long max = System.currentTimeMillis() + (6 * 24 * 60 * 60 * 1000);
            long gigTime;
            for (Gig gig : mData.get(mWeekday).get(mMeal).get(latLng)) {
                try {
                    gigTime = gig.getStartDate().getTime();
                } catch (ParseException e) {
                    gigTime = -1;
                }
                if (gigTime >= 0 && gigTime < max) {
                    gigs.add(gig);
                }
            }
        }

        // Sort gigs by source
        Collections.sort(gigs, new Comparator<Gig>() {
            @Override
            public int compare(Gig gig1, Gig gig2) {
                return gig1.getSource().compareTo(gig2.getSource());
            }
        });

        return gigs;
    }

    public static List<Gig> getGigs(LatLng latLng, String truck) {
        List<Gig> gigs = new ArrayList<>();
        for (Gig gig : getGigs(latLng)) {
            if (gig.getTruck().equals(truck)) {
                gigs.add(gig);
            }
        }
        return gigs;
    }

    private static class TruckEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setSnapshot(dataSnapshot);
            if (mMapsActivity != null) {
                if (mMapsActivity.isStarting()) {
                    mMapsActivity.setRedraw(MapsHelper.drawMap());
                    mMapsActivity.markStarted();
                } else {
                    mMapsActivity.setRedraw(true);
                }
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {}
    }
}
