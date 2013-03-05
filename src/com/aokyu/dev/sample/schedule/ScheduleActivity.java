package com.aokyu.dev.sample.schedule;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.view.Menu;

import com.aokyu.dev.sample.schedule.CalendarListFragment.OnCalendarItemClickListener;
import com.aokyu.dev.sample.schedule.EventListFragment.OnEventItemClickListener;

public class ScheduleActivity extends Activity
    implements OnCalendarItemClickListener, OnEventItemClickListener {

    private boolean mTransactionAllowed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_screen);

        mTransactionAllowed = true;
        showCalendarListFragment("sample@gmail.com");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTransactionAllowed = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTransactionAllowed = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mTransactionAllowed = false;
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.schedule_screen, menu);
        return true;
    }

    private void showCalendarListFragment(String accountName) {
        FragmentManager manager = getFragmentManager();

        CalendarListFragment fragment =
                (CalendarListFragment) manager.findFragmentByTag(CalendarListFragment.TAG);
        if (fragment == null) {
            fragment = CalendarListFragment.newInstance();
        }

        Bundle args = new Bundle();
        args.putString(CalendarLoaderFragment.Argument.ACCOUNT_NAME, accountName);
        fragment.setArguments(args);
        showFragment(fragment);
    }

    private void showEventListFragment(long calendarId) {
        FragmentManager manager = getFragmentManager();

        EventListFragment fragment =
                (EventListFragment) manager.findFragmentByTag(EventListFragment.TAG);
        if (fragment == null) {
            fragment = EventListFragment.newInstance();
        }

        Bundle args = new Bundle();
        args.putLong(EventListFragment.Argument.CALENDAR_ID, calendarId);
        fragment.setArguments(args);
        showFragment(fragment, EventListFragment.TAG);
    }

    private void showFragment(Fragment fragment) {
        if (!isFragmentTransactionAllowed()) {
            return;
        }

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container_view, fragment);
        transaction.commit();
    }

    private void showFragment(Fragment fragment, String tag) {
        if (!isFragmentTransactionAllowed()) {
            return;
        }

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(tag);
        transaction.replace(R.id.container_view, fragment, tag);
        transaction.commit();
    }

    public boolean isFragmentTransactionAllowed() {
        return mTransactionAllowed;
    }

    @Override
    public void onCalendarItemClick(long calendarId) {
        showEventListFragment(calendarId);
    }

    @Override
    public void onEventItemClicked(long eventId, EventSchedule schedule) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
        intent.setData(uri);
        boolean allDay = schedule.isAllDay();
        long start = schedule.getStartDateMillis();
        long end = schedule.getEndDateMillis();
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, allDay);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
        startActivity(intent);
    }
}
