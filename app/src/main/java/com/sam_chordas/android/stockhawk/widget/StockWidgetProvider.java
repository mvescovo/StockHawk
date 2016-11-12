package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StockIntentService;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;
import com.sam_chordas.android.stockhawk.ui.StockDetailActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Michael Vescovo.
 */

public class StockWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "StockWidgetProvider";
    public static final String ACTION_DATA_UPDATED = "com.sam_chordas.android.stockhawk.DATA_UPDATED";
    public static final String ACTION_ITEM_CLICKED = "com.sam_chordas.android.stockhawk.ITEM_CLICKED";
    public static final String EXTRA_STOCK_SYMBOL = "com.sam_chordas.android.stockhawk.EXTRA_STOCK_SYMBOL";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(ACTION_DATA_UPDATED)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
            showUpdateTime(context, appWidgetManager, appWidgetIds);
        }

        if (intent.getAction().equals(ACTION_ITEM_CLICKED)) {
            String stock_symbol = intent.getStringExtra(EXTRA_STOCK_SYMBOL);
            Intent detailIntent = new Intent(context, StockDetailActivity.class);
            detailIntent.putExtra(StockDetailActivity.EXTRA_STOCK_SYMBOL, stock_symbol);
            context.startActivity(detailIntent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate: ONUPDATE CALLED");
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);

            // Setup refresh button
            Intent updateDataService = new Intent(context, StockIntentService.class);
            updateDataService.putExtra("tag", "periodic");
            PendingIntent updateDataPendingIntent = PendingIntent.getService(context, 0, updateDataService, 0);
            rv.setOnClickPendingIntent(R.id.refresh, updateDataPendingIntent);

            // WidgetListService intent for populating collection views.
            Intent collectionIntent = new Intent(context, WidgetListService.class);
            rv.setRemoteAdapter(R.id.widget_list, collectionIntent);
            rv.setEmptyView(R.id.widget_list, R.id.widget_empty);

            // MyStocksActivity intent for opening the main app from the widget title.
            Intent myStocksIntent = new Intent(context, MyStocksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, myStocksIntent, 0);
            rv.setOnClickPendingIntent(R.id.widget_title, pendingIntent);

            // Collection for StockDetailActivity intent when opening individual list item.
            Intent clickIntentTemplate = new Intent(context, StockWidgetProvider.class);
            clickIntentTemplate.setAction(StockWidgetProvider.ACTION_ITEM_CLICKED);
            PendingIntent clickPendingIntentTemplate = PendingIntent.getBroadcast(context, 0,
                    clickIntentTemplate, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);

            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }

        showUpdateTime(context, appWidgetManager, appWidgetIds);

    }

    /*
    * Show the time data was updated on the widget.
    * */
    private void showUpdateTime(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
            Calendar c = Calendar.getInstance();
            DateFormat timeInstance = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
            String timeUpdated = "Updated " + timeInstance.format(c.getTime());
            rv.setTextViewText(R.id.time_updated, timeUpdated);
            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
            rv.removeAllViews(appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);


    }
}