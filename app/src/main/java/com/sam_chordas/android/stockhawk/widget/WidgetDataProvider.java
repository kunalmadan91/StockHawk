package com.sam_chordas.android.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Created by KUNAL on 28-05-2016.
 */
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private Cursor mCursor;
    private Intent mIntent;


    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;

    }

    @Override
    public void onCreate() {
        //initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {


        String symbol = "";
        String bidPrice = "";
        String change = "";
        int isUp = 1;

        if (mCursor.moveToPosition(position)) {
            symbol = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL));

            bidPrice = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE));

            change = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE));

            isUp = mCursor.getInt(mCursor.getColumnIndex(QuoteColumns.ISUP));

        }
        int itemId = R.layout.widget_collection_item;
        //int id = R.id.widget_list;

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_collection_item);

        remoteViews.setTextViewText(R.id.stock_symbol, symbol);
        remoteViews.setTextViewText(R.id.bid_price, bidPrice);
        remoteViews.setTextViewText(R.id.change, change);

        if (isUp == 1) {
            remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        } else {
            remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
        }

        Intent fillInIntent = new Intent();


        fillInIntent.putExtra("SYMBOL",symbol);

        remoteViews.setOnClickFillInIntent(R.id.widget_item, fillInIntent);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public void initData() {

        // Refresh the cursor
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);

    }

}
