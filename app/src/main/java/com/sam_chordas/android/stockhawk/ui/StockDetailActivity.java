package com.sam_chordas.android.stockhawk.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class
StockDetailActivity extends AppCompatActivity {

    public static final String EXTRA_STOCK_SYMBOL = "STOCK_SYMBOL";
    private static final String TAG = "StockDetailActivity";
    private LineChartView mLineChartView;
    private ProgressBar mProgressBar;
    private String mSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        mLineChartView = (LineChartView) findViewById(R.id.linechart);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        if (getIntent().getStringExtra(EXTRA_STOCK_SYMBOL) != null) {
            mSymbol = getIntent().getStringExtra(EXTRA_STOCK_SYMBOL);
            new GetStockData().execute(mSymbol);
        }
    }

    private void loadChart(String[] labels, float[] values) {
        LineSet lineset = new LineSet(labels, values);
        lineset.setColor(ContextCompat.getColor(this, R.color.material_red_700));
        lineset.setDotsColor(ContextCompat.getColor(this, R.color.black));
        lineset.setFill(ContextCompat.getColor(this, R.color.material_red_200));
        mLineChartView.setYAxis(false);
        mLineChartView.addData(lineset);
        mLineChartView.setAxisBorderValues((int)Math.floor(getMin(values)),
                (int)Math.ceil(getMax(values)));
        mLineChartView.setPadding(16, 16, 16, 16);
        mLineChartView.setBackgroundColor(ContextCompat.getColor(this, R.color.material_grey_200));
        mLineChartView.show();
    }

    private float getMax(float[] values) {
        float max = values[0];
        for (float value : values) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private float getMin(float[] values) {
        float min = values[0];
        for (float value : values) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    private class GetStockData extends AsyncTask<String, Void, ArrayList<Stock>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressIndicator(true);
        }

        @Override
        protected ArrayList<Stock> doInBackground(String... strings) {
            ArrayList<Stock> stocks = new ArrayList<>();
            for (String symbol : strings) {
                Calendar from = Calendar.getInstance();
                Calendar to = Calendar.getInstance();
                from.add(Calendar.DAY_OF_WEEK, -7);

                Stock stock = null;
                try {
                    stock = YahooFinance.get(symbol, from, to, Interval.DAILY);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (stock != null) {
                    stocks.add(stock);
                }
            }
            return stocks;
        }

        @Override
        protected void onPostExecute(ArrayList<Stock> stocks) {
            setProgressIndicator(false);
            for (Stock stock : stocks) {
                try {
                    List<HistoricalQuote> historicalQuotes = stock.getHistory();
                    String[] labels = new String[historicalQuotes.size()];
                    float[] values = new float[historicalQuotes.size()];
                    Log.d(TAG, "onPostExecute: history size: " + historicalQuotes.size());

                    for (int i = 0; i < historicalQuotes.size(); i++) {
                        Calendar calendar = historicalQuotes.get(i).getDate();
                        BigDecimal high = historicalQuotes.get(i).getHigh();
                        labels[i] = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                        values[i] = high.floatValue();
                        Log.d(TAG, "onPostExecute: DAY OF WEEK: " + labels[i] + ", HIGH: " + values[i]);
                    }

                    loadChart(labels, values);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setProgressIndicator(boolean active) {
        if (active) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
