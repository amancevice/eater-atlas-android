package com.smallweirdnumber.eateratlas.models;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.smallweirdnumber.eateratlas.enums.Meal;
import com.smallweirdnumber.eateratlas.enums.Weekday;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@SuppressWarnings("unused")
public class Gig {
    private Float latitude;
    private Float longitude;
    private String city;
    private String endpoint;
    private String meal;
    private String neighborhood;
    private String place;
    private String site;
    private String source;
    private String start;
    private String stop;
    private String timezone;
    private String truck;
    private String type;
    private String weekday;
    public Float getLatitude()      { return latitude;     }
    public Float getLongitude()     { return longitude;    }
    public String getCity()         { return city;         }
    public String getEndpoint()     { return endpoint;     }
    public String getMeal()         { return meal;         }
    public String getNeighborhood() { return neighborhood; }
    public String getPlace()        { return place;        }
    public String getSource()       { return source;       }
    public String getStart()        { return start;        }
    public String getStop()         { return stop;         }
    public String getTimezone()     { return timezone;     }
    public String getTruck()        { return truck;        }
    public String getType()         { return type;         }
    public String getWeekday()      { return weekday;      }

    public Weekday getWeekdayEnum() {
        return Weekday.fromString(weekday);
    }

    public Meal getMealEnum() {
        return Meal.fromString(meal);
    }

    public LatLng getLatLng() {
        return new LatLng(getLatitude(), getLongitude());
    }

    public Date getStartDate() throws ParseException {
        return toDate(start);
    }

    public Date getStopDate() throws ParseException {
        return toDate(stop);
    }

    public String toString() {
        try {
            SimpleDateFormat formatStart = new SimpleDateFormat("M/d h:mm a", Locale.US);
            SimpleDateFormat formatEnd = new SimpleDateFormat("h:mm a z", Locale.US);
            return  source + "\n" +
                    formatStart.format(getStartDate()) + " :: " +
                    formatEnd.format(getStopDate());
        } catch (ParseException e) {
            return "";
        }
    }

    private static Date toDate(String dateStr) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.parse(dateStr);
    }

    public String getSite() {
        if (site != null && (site.startsWith("http://") || site.startsWith("https://"))) {
            return site;
        } else if (site != null && !site.isEmpty()) {
            return "http://" + site;
        } else {
            return null;
        }
    }

    public Uri getSiteUri() {
        if (getSite() != null) {
            return Uri.parse(getSite());
        } else {
            return null;
        }
    }
}
