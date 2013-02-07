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
import android.provider.CalendarContract.Calendars;

public abstract class CalendarLoaderFragment extends Fragment {

    public final class ColumnIndex {

        public static final int ID = 0;
        public static final int ACCOUNT_NAME = 1;
        public static final int CALENDAR_DISPLAY_NAME = 2;
        public static final int OWNNER_ACCOUNT = 3;
        public static final int SYNC_EVENTS = 4;

        private ColumnIndex() {}
    }

    public final class Argument {
        public static final String ACCOUNT_NAME = "arg_account_name";
    }

    protected static final String NO_ACCOUNT_NAME = "__no_account_name__";

    protected Context mContext;
    private LoaderManager mLoaderManager;

    private CalendarLoaderCallbacks mLoaderCallbacks;

    private static final int ID_CALENDAR_LOADER = 0x00000001;

    protected String mAccountName = NO_ACCOUNT_NAME;

    protected abstract void onLoadFinished(Loader<Cursor> loader, Cursor cursor);
    protected abstract void onLoaderReset(Loader<Cursor> loader);

    public CalendarLoaderFragment() {}

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
        mLoaderCallbacks = new CalendarLoaderCallbacks(mContext, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        String accountName = args.getString(Argument.ACCOUNT_NAME, NO_ACCOUNT_NAME);
        loadCalendar(accountName);
    }

    protected void loadCalendar(String accountName) {
        Loader<Cursor> loader = mLoaderManager.getLoader(ID_CALENDAR_LOADER);
        mAccountName = accountName;

        Bundle args = new Bundle();
        args.putString(Argument.ACCOUNT_NAME, accountName);

        if (loader != null) {
            mLoaderManager.restartLoader(ID_CALENDAR_LOADER, args, mLoaderCallbacks);
        } else {
            mLoaderManager.initLoader(ID_CALENDAR_LOADER, args, mLoaderCallbacks);
        }
    }

    public String getAccountName() {
        return mAccountName;
    }

    protected void destroyLoader() {
        Loader<Cursor> loader = mLoaderManager.getLoader(ID_CALENDAR_LOADER);
        if (loader != null) {
            mLoaderManager.destroyLoader(ID_CALENDAR_LOADER);
        }
    }

    private static final class CalendarCursorLoader extends CursorLoader {

        private static final String[] PROJECTION = new String[] {
            Calendars._ID,
            Calendars.ACCOUNT_NAME,
            Calendars.CALENDAR_DISPLAY_NAME,
            Calendars.OWNER_ACCOUNT,
            Calendars.SYNC_EVENTS
        };

        public CalendarCursorLoader(Context context, String accountName) {
            super(context, Calendars.CONTENT_URI, PROJECTION,
                    Calendars.ACCOUNT_NAME + "=?",
                    new String[] { accountName }, null);
        }
    }

    private static class CalendarLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        private WeakReference<Context> mContext;
        private WeakReference<CalendarLoaderFragment> mFragment;

        public CalendarLoaderCallbacks(Context context, CalendarLoaderFragment fragment) {
            mContext = new WeakReference<Context>(context);
            mFragment = new WeakReference<CalendarLoaderFragment>(fragment);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String accountName = args.getString(Argument.ACCOUNT_NAME);
            return new CalendarCursorLoader(mContext.get(), accountName);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            CalendarLoaderFragment fragment = mFragment.get();
            if (fragment != null) {
                fragment.onLoadFinished(loader, cursor);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            CalendarLoaderFragment fragment = mFragment.get();
            if (fragment != null) {
                fragment.onLoaderReset(loader);
            }
        }
    }
}
