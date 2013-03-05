package com.aokyu.dev.sample.schedule;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class EventSchedule implements Parcelable {

    public static final long NO_SCHEDULE = -1;

    private long mStartMillis = NO_SCHEDULE;
    private long mEndMillis = NO_SCHEDULE;
    private boolean mAllDay = false;

    public EventSchedule(boolean allDay, long startMillis, long endMillis) {
        mAllDay = allDay;
        mStartMillis = startMillis;
        mEndMillis = endMillis;

    }

    public Date getStartDate() {
        return new Date(mStartMillis);
    }

    public long getStartDateMillis() {
        return mStartMillis;
    }

    public Date getEndDate() {
        return new Date(mEndMillis);
    }

    public long getEndDateMillis() {
        return mEndMillis;
    }

    public boolean isAllDay() {
        return mAllDay;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mStartMillis);
        dest.writeLong(mEndMillis);
        dest.writeInt(mAllDay ? 1 : 0);
    }

    /* package */ static EventSchedule createFromParcelBody(Parcel in) {
        long start = in.readLong();
        long end = in.readLong();
        boolean allDay = (in.readInt() == 1);
        EventSchedule schedule = new EventSchedule(allDay, start, end);
        return schedule;
    }

    public static final Parcelable.Creator<EventSchedule> CREATOR = 
            new Parcelable.Creator<EventSchedule>() {

        @Override
        public EventSchedule createFromParcel(Parcel source) {
            EventSchedule schedule = EventSchedule.createFromParcelBody(source);
            return schedule;
        }
        
        @Override
        public EventSchedule[] newArray(int size) {
            return new EventSchedule[size];
        }

    };
}
