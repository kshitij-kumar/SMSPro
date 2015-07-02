package com.kshitij.android.smspro.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kshitij.android.smspro.R;
import com.kshitij.android.smspro.util.TimeFormatter;

/**
 * Created by kshitij.kumar on 30-06-2015.
 */

/**
 * An implementation of CursorAdapter, used to display sms feed
 */

public class ConversationAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    public ConversationAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = getCursor();
        if (cursor != null) {
            cursor.moveToPosition(position);
            if (cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE))
                    == Telephony.Sms.MESSAGE_TYPE_INBOX)
                return 0;
            else
                return 1;
        } else {
            return -1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public void bindView(View convertView, Context context, Cursor cursor) {
        // Use View-Holder pattern to avoid redundant findViewById calls
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.mTxtSms = (TextView) convertView
                    .findViewById(R.id.tvSms);
            convertView.setTag(viewHolder);
        }

        String message = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
        String date = TimeFormatter.getDateFormatMddYYYYhhmma(cursor.getLong(
                cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)));

        viewHolder.mTxtSms.setText(message + "\n" + date);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rowView = null;
        if (cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE))
                == Telephony.Sms.MESSAGE_TYPE_INBOX) {
            rowView = mInflater.inflate(R.layout.list_item_sms_received, parent, false);
        } else {
            rowView = mInflater.inflate(R.layout.list_item_sms_sent, parent, false);
        }

        return rowView;
    }

    public static class ViewHolder {
        TextView mTxtSms;
        TextView mTxtSmsTime;
    }
}