package com.aokyu.dev.sample.schedule;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;

public abstract class EventLoaderFragment extends Fragment {

    public final class ColumnIndex {

        public static final int ID = 0;
        public static final int CALENDAR_ID = 1;
        public static final int TITLE = 2;
        public static final int START = 3;
        public static final int END = 4;
        public static final int ALL_DAY = 5;

        private ColumnIndex() {}
    }

    public final class Argument {
        public static final String CALENDAR_ID = "arg_calendar_id";
    }

    protected static final long NO_CALENDAR_ID = -2;

    protected Context mContext;
    private LoaderManager mLoaderManager;

    private EventLoaderCallbacks mLoaderCallbacks;

    private static final int ID_EVENT_LOADER = 0x00000002;

    protected long mCalendarId = NO_CALENDAR_ID;

    protected abstract void onLoadFinished(Loader<Cursor> loader, Cursor cursor);
    protected abstract void onLoaderReset(Loader<Cursor> loader);

    public EventLoaderFragment() {}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity != null) {
            mContext = activity.getApplicationContext();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoaderManager = getLoaderManager();
        mLoaderCallbacks = new EventLoaderCallbacks(mContext, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        long calendarId = args.getLong(Argument.CALENDAR_ID, NO_CALENDAR_ID);
        loadEvents(calendarId);
    }

    protected void loadEvents(long calendarId) {
        Loader<Cursor> loader = mLoaderManager.getLoader(ID_EVENT_LOADER);
        mCalendarId = calendarId;

        Bundle args = new Bundle();
        args.putLong(Argument.CALENDAR_ID, calendarId);

        if (loader != null) {
            mLoaderManager.restartLoader(ID_EVENT_LOADER, args, mLoaderCallbacks);
        } else {
            mLoaderManager.initLoader(ID_EVENT_LOADER, args, mLoaderCallbacks);
        }
    }

    public long getCalendarId() {
        return mCalendarId;
    }

    protected void destroyLoader() {
        Loader<Cursor> loader = mLoaderManager.getLoader(ID_EVENT_LOADER);
        if (loader != null) {
            mLoaderManager.destroyLoader(ID_EVENT_LOADER);
        }
    }

    private static final class EventCursorLoader extends CursorLoader {

        private static final String[] PROJECTION = new String[] {
            CalendarContract.Events._ID,
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.ALL_DAY
        };

        public EventCursorLoader(Context context, long calendarId) {
            super(context, CalendarContract.Events.CONTENT_URI, PROJECTION,
                    CalendarContract.Events.CALENDAR_ID + "=?",
                    new String[] { String.valueOf(calendarId) }, null);
        }
    }

    private static class EventLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        private WeakReference<Context> mContext;
        private WeakReference<EventLoaderFragment> mFragment;

        public EventLoaderCallbacks(Context context, EventLoaderFragment fragment) {
            mContext = new WeakReference<Context>(context);
            mFragment = new WeakReference<EventLoaderFragment>(fragment);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            long calendarId = args.getLong(Argument.CALENDAR_ID);
            return new EventCursorLoader(mContext.get(), calendarId);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            EventLoaderFragment fragment = mFragment.get();
            if (fragment != null) {
                fragment.onLoadFinished(loader, cursor);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            EventLoaderFragment fragment = mFragment.get();
            if (fragment != null) {
                fragment.onLoaderReset(loader);
            }
        }
    }
}
