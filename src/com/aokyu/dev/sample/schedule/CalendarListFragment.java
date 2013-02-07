package com.aokyu.dev.sample.schedule;

import android.app.Activity;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CalendarListFragment extends CalendarLoaderFragment {

    /* package */ static final String TAG = CalendarListFragment.class.getSimpleName();

    private ListView mCalendarView;
    private CalendarAdapter mCalendarAdapter;

    private OnCalendarItemClickListener mListener;

    public CalendarListFragment() {
        super();
    }

    public static CalendarListFragment newInstance() {
        CalendarListFragment fragment = new CalendarListFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnCalendarItemClickListener) {
            mListener = (OnCalendarItemClickListener) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.calendar_panel, null);
        setupViews(contentView);
        return contentView;
    }

    private void setupViews(View rootView) {
        mCalendarView = (ListView) rootView.findViewById(R.id.calendar_view);
        mCalendarAdapter = new CalendarAdapter(mContext, null);
        mCalendarView.setAdapter(mCalendarAdapter);
        mCalendarView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (mListener != null) {
                    mListener.onCalendarItemClick(id);
                }
            }
        });
    }

    @Override
    protected void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (mCalendarAdapter != null) {
            Cursor oldCursor = mCalendarAdapter.swapCursor(cursor);
            if (oldCursor != null) {
                oldCursor.close();
            }
        }
    }

    @Override
    protected void onLoaderReset(Loader<Cursor> loader) {
        if (mCalendarAdapter != null) {
            mCalendarAdapter.swapCursor(null);
        }
    }

    public void setOnItemClickListener() {
        
    }


    private static class CalendarAdapter extends CursorAdapter {

        private LayoutInflater mInflater;

        private static final class ViewCache {
            public final TextView nameView;
            public final TextView accountView;

            public ViewCache(View root) {
                nameView = (TextView) root.findViewById(R.id.name_view);
                accountView = (TextView) root.findViewById(R.id.account_view);
            }
        }

        public CalendarAdapter(Context context, Cursor c) {
            super(context, c, false);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.calendar_list_item, null);
            view.setTag(new ViewCache(view));
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewCache cache = (ViewCache) view.getTag();

            String calendarName = cursor.getString(ColumnIndex.CALENDAR_DISPLAY_NAME);
            cache.nameView.setText(calendarName);

            String accountName = cursor.getString(ColumnIndex.OWNNER_ACCOUNT);
            cache.accountView.setText(accountName);
        }

    }

    public interface OnCalendarItemClickListener {
        public void onCalendarItemClick(long calendarId);
    }
}
