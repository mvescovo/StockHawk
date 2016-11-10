package com.sam_chordas.android.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

public class WidgetListService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }

    class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private Context mContext;
        private static final String TAG = "ListRemoteViewsFactory";
        private Cursor mData = null;
        private int mCount = 0;

        public ListRemoteViewsFactory(Context context) {
            mContext = context;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            Log.d(TAG, "onDataSetChanged: CALLED");

            final long identityToken = Binder.clearCallingIdentity();

            if (mData != null) {
                mCount = mData.getCount();
                Log.d(TAG, "onDataSetChanged: COUNT at start of method: " + mCount);
                Log.d(TAG, "onDataSetChanged: data is not null");
                mData.close();
                mData = null;
            } else {
                Log.d(TAG, "onDataSetChanged: data is null");
            }

            String[] projection = {
                    QuoteColumns._ID,
                    QuoteColumns.SYMBOL,
                    QuoteColumns.BIDPRICE,
                    QuoteColumns.PERCENT_CHANGE,
                    QuoteColumns.CHANGE,
                    QuoteColumns.ISUP
            };

            mData = getContentResolver().query(
                    QuoteProvider.Quotes.CONTENT_URI,
                    projection,
                    QuoteColumns.ISCURRENT + " = ?",
                    new String[]{"1"},
                    null
            );

            Binder.restoreCallingIdentity(identityToken);

            mCount = mData.getCount();
            Log.d(TAG, "onDataSetChanged: COUNT at end of method: " + mCount);
        }

        @Override
        public void onDestroy() {
            Log.d(TAG, "onDestroy: called");
            if (mData != null) {
                Log.d(TAG, "onDestroy: data is not null");
                mData.close();
                mData = null;
            } else {
                Log.d(TAG, "onDestroy: data is null");
            }
        }

        @Override
        public int getCount() {
            if (mData != null) {
                return mData.getCount();
            } else {
                return 0;
            }
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (position == AdapterView.INVALID_POSITION ||
                    mData == null || !mData.moveToPosition(position)) {
                return null;
            }

            RemoteViews rv = new RemoteViews(getPackageName(), R.layout.widget_item);

            String stock_symbol = mData.getString(1);
            String bid_price = mData.getString(2);
            String percent_change = mData.getString(3);
            String change = mData.getString(4);
            String is_up = mData.getString(5);

            rv.setTextViewText(R.id.stock_symbol, stock_symbol);
            rv.setTextViewText(R.id.bid_price, bid_price);
            if (is_up.contentEquals("1")) {
                rv.setInt(R.id.change, "setBackgroundColor", ContextCompat.getColor(mContext, R.color.material_green_700));
            } else {
                rv.setInt(R.id.change, "setBackgroundColor", ContextCompat.getColor(mContext, R.color.material_red_700));
            }
            if (Utils.showPercent) {
                rv.setTextViewText(R.id.change, percent_change);
            } else {
                rv.setTextViewText(R.id.change, change);
            }

            // Setup the fillIn click intent for each list item.
            Intent fillInIntent = new Intent();
            fillInIntent.putExtra(StockWidgetProvider.EXTRA_STOCK_SYMBOL, stock_symbol);
            rv.setOnClickFillInIntent(R.id.stock_widget_item, fillInIntent);

            return rv;
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
            if (mData.moveToPosition(position)) {
                return mData.getLong(0);
            } else {
                return -1;
            }
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}


