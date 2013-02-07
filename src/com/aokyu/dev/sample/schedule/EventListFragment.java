package com.aokyu.dev.sample.schedule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class EventListFragment extends EventLoaderFragment {

    /* package */ static final String TAG = EventListFragment.class.getSimpleName();

    private ListView mEventView;
    private EventAdapter mEventAdapter;

    public EventListFragment() {
        super();
    }

    public static EventListFragment newInstance() {
        EventListFragment fragment = new EventListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.event_panel, null);
        setupViews(contentView);
        return contentView;
    }

    private void setupViews(View rootView) {
        mEventView = (ListView) rootView.findViewById(R.id.event_view);
        mEventAdapter = new EventAdapter(mContext, null);
        mEventView.setAdapter(mEventAdapter);
    }

    @Override
    protected void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (mEventAdapter != null) {
            Cursor oldCursor = mEventAdapter.swapCursor(cursor);
            if (oldCursor != null) {
                oldCursor.close();
            }
        }
    }

    @Override
    protected void onLoaderReset(Loader<Cursor> loader) {
        if (mEventAdapter != null) {
            mEventAdapter.swapCursor(null);
        }
    }

    private static class EventAdapter extends CursorAdapter {

        private LayoutInflater mInflater;

        private static final String DATE_ONLY_FORMAT = "yyyy/MM/dd";
        private static final String DEFAULT_FORMAT = "yyyy/MM/dd/ HH:mm";
        private final DateFormat mDateFormatter =
                new SimpleDateFormat(DATE_ONLY_FORMAT, Locale.getDefault());
        private final DateFormat mFormatter =
                new SimpleDateFormat(DEFAULT_FORMAT, Locale.getDefault());
        

        private static final class ViewCache {
            public final TextView titleView;
            public final TextView scheduleView;

            public ViewCache(View root) {
                titleView = (TextView) root.findViewById(R.id.title_view);
                scheduleView = (TextView) root.findViewById(R.id.schedule_view);
            }
        }

        public EventAdapter(Context context, Cursor c) {
            super(context, c, false);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.event_list_item, null);
            view.setTag(new ViewCache(view));
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewCache cache = (ViewCache) view.getTag();

            String title = cursor.getString(ColumnIndex.TITLE);
            cache.titleView.setText(title);

            long startMillis = cursor.getLong(ColumnIndex.START);
            int allDay = cursor.getInt(ColumnIndex.ALL_DAY);

            Date startDate = new Date(startMillis);
            String schedule = null;
            if (allDay == 1) {
                schedule = mDateFormatter.format(startDate);
            } else {
                if (!cursor.isNull(ColumnIndex.END)) {
                    long endMillis = cursor.getLong(ColumnIndex.END);
                    Date endData = new Date(endMillis);
                    String start = mFormatter.format(startDate);
                    String end = mFormatter.format(endData);
                    StringBuilder builder = new StringBuilder();
                    builder.append(start);
                    builder.append(" - ");
                    builder.append(end);
                    schedule = builder.toString();
                } else {
                    schedule = mFormatter.format(startDate);
                }
            }

            cache.scheduleView.setText(schedule);
        }

    }
}
