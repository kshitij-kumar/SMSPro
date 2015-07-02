package com.kshitij.android.smspro.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.kshitij.android.smspro.R;
import com.kshitij.android.smspro.adapter.ConversationAdapter;
import com.kshitij.android.smspro.receiver.SmsReceiver;
import com.kshitij.android.smspro.util.ContentManager;
import com.kshitij.android.smspro.util.SmsUtils;

/**
 * Created by kshitij.kumar on 30-06-2015.
 */

/**
 * Displays a SMS, either when an item in list is clicked or a notification is
 * clicked
 */
public class SmsViewActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    public static final String EXTRA_PHONE_NUMBER = "phone_number";
    public static final String EXTRA_MESSAGE = "message";
    private static final String TAG = SmsViewActivity.class.getSimpleName();
    private Context mContext;
    private ListView mConversationListView;
    private ConversationAdapter mConversationAdapter;
    private String mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (getIntent() != null && getIntent().getExtras() != null) {

            int notificationId = getIntent().getExtras().getInt(
                    SmsReceiver.EXTRA_NOTIFICATION_ID, -1);
            if (notificationId != -1) {
                NotificationManager manager = (NotificationManager) getSystemService(
                        NOTIFICATION_SERVICE);
                manager.cancel(notificationId);
            }
            mPhoneNumber = getIntent().getExtras().getString(EXTRA_PHONE_NUMBER);

            setUpActionBar();
            String message = getIntent().getExtras().getString(EXTRA_MESSAGE);
            SmsUtils.markSmsAsRead(this, mPhoneNumber, message);

            mConversationListView = (ListView) findViewById(R.id.conversationList);
            mConversationAdapter = new ConversationAdapter(this, null, false);
            mConversationListView.setAdapter(mConversationAdapter);
            getSupportLoaderManager().restartLoader(0, null, this);
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Log.d(TAG, "onCreateLoader()");
        String selection = Telephony.Sms.ADDRESS + " = ?";
        String[] selectionArgs = {mPhoneNumber};
        CursorLoader loader = new CursorLoader(this, Telephony.Sms.CONTENT_URI, new String[]{},
                selection, selectionArgs, Telephony.Sms.DATE + " ASC");
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        mConversationAdapter.swapCursor(cursor);
        mConversationListView.setSelection(mConversationAdapter.getCount() - 1);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mConversationAdapter.swapCursor(null);
    }

    private void setUpActionBar() {
        new Thread() {
            @Override
            public void run() {
                String title = SmsUtils.getContactName(SmsViewActivity.this, mPhoneNumber);
                if (!SmsUtils.isNullOrEmpty(title)) {
                    getSupportActionBar().setTitle(title);
                    getSupportActionBar().setSubtitle(mPhoneNumber);
                }
            }
        }.start();

        if (SmsUtils.isKnownNumber(mPhoneNumber)) {
            getSupportActionBar().setTitle(ContentManager.getInstance().getContactsMap()
                    .get(mPhoneNumber));
            getSupportActionBar().setSubtitle(mPhoneNumber);
        } else {
            getSupportActionBar().setTitle(mPhoneNumber);
        }
    }
}
