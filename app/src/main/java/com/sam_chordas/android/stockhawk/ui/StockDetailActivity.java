package com.sam_chordas.android.stockhawk.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import static yahoofinance.histquotes.Interval.MONTHLY;

public class
StockDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_STOCK_SYMBOL = "STOCK_SYMBOL";
    private static final String TAG = "StockDetailActivity";
    private final String DATE_RANGE_5DAYS = "5DAYS";
    private final String DATE_RANGE_3MONTHS = "3MONTHS";
    private final String DATE_RANGE_6MONTHS = "6MONTHS";
    private final String DATE_RANGE_1YEAR = "1YEAR";
    private final String DATE_RANGE_5YEARS = "5YEARS";
    private final String DATE_RANGE_MAX = "MAX";
    private LineChartView mLineChartView;
    private String mSymbol;
    private String mDateRange;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.stock_symbol)
    TextView mStockSymbol;

    @BindView(R.id.days5)
    Button mDays5Button;

    @BindView(R.id.months3)
    Button mMonths3Button;

    @BindView(R.id.months6)
    Button mMonths6Button;

    @BindView(R.id.years1)
    Button mYears1Button;

    @BindView(R.id.years5)
    Button mYears5Button;

    @BindView(R.id.max)
    Button mMaxButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        ButterKnife.bind(this);

        mLineChartView = (LineChartView) findViewById(R.id.linechart);
        mDays5Button.setOnClickListener(this);
        mMonths3Button.setOnClickListener(this);
        mMonths6Button.setOnClickListener(this);
        mYears1Button.setOnClickListener(this);
        mYears5Button.setOnClickListener(this);
        mMaxButton.setOnClickListener(this);

        if (getIntent().getStringExtra(EXTRA_STOCK_SYMBOL) != null) {
            mSymbol = getIntent().getStringExtra(EXTRA_STOCK_SYMBOL);
            mStockSymbol.setText(mSymbol);
            mDateRange = DATE_RANGE_5DAYS;
            mDays5Button.setBackgroundColor(ContextCompat.getColor(this, R.color.material_red_700));
            new GetStockData().execute();
        }
    }

    private void loadChart(Calendar[] dates, float[] values) {
        String[] labels;
        LineSet lineset;
        switch (mDateRange) {
            case DATE_RANGE_5DAYS:
                labels = new String[values.length];
                for (int i = 0; i < dates.length; i++) {
                    String day = String.valueOf(dates[i].get(Calendar.DAY_OF_MONTH));
                    labels[i] = day;
                }
                mLineChartView.setXLabels(AxisRenderer.LabelPosition.OUTSIDE);
                lineset = new LineSet(labels, values);
                break;
            case DATE_RANGE_3MONTHS:
                labels = new String[3];
                for (int i = 0; i < dates.length; i++) {
                    String month = String.valueOf(dates[i].get(Calendar.MONTH));
                    labels[i] = month;
                }
                mLineChartView.setXLabels(AxisRenderer.LabelPosition.OUTSIDE);
                lineset = new LineSet(labels, values);
                break;
            case DATE_RANGE_6MONTHS:
                labels = new String[6];
                for (int i = 0; i < dates.length; i++) {
                    String month = String.valueOf(dates[i].get(Calendar.MONTH));
                    labels[i] = month;
                }
                mLineChartView.setXLabels(AxisRenderer.LabelPosition.OUTSIDE);
                lineset = new LineSet(labels, values);
                break;
            case DATE_RANGE_1YEAR:
                labels = new String[12];
                for (int i = 0; i < dates.length; i++) {
                    String month = String.valueOf(dates[i].get(Calendar.MONTH));
                    labels[i] = month;
                }
                mLineChartView.setXLabels(AxisRenderer.LabelPosition.OUTSIDE);
                lineset = new LineSet(labels, values);
                break;
            case DATE_RANGE_5YEARS:
                labels = new String[5];
                float[] yearValues = new float[5];
                for (int i = 0, j = 0; i < dates.length; i += 12, j++) {
                    String year = String.valueOf(dates[i].get(Calendar.YEAR));
                    labels[j] = year;
                    yearValues[j] = values[i];
                }
                values = yearValues;
                mLineChartView.setXLabels(AxisRenderer.LabelPosition.OUTSIDE);
                lineset = new LineSet(labels, values);
                break;
            case DATE_RANGE_MAX:
                labels = new String[dates.length];
                for (int i = 0; i < dates.length; i++) {
                    String month = String.valueOf(dates[i].get(Calendar.MONTH));
                    labels[i] = month;
                }
                mLineChartView.setXLabels(AxisRenderer.LabelPosition.NONE);
                lineset = new LineSet(labels, values);
                lineset.setDotsRadius(0f);
                break;
            default:
                labels = new String[dates.length];
                for (int i = 0; i < dates.length; i++) {
                    String month = String.valueOf(dates[i].get(Calendar.MONTH));
                    labels[i] = month;
                }
                mLineChartView.setXLabels(AxisRenderer.LabelPosition.OUTSIDE);
                lineset = new LineSet(labels, values);
        }
        lineset.setColor(ContextCompat.getColor(this, R.color.material_red_700));
        lineset.setDotsColor(ContextCompat.getColor(this, R.color.black));
        lineset.setFill(ContextCompat.getColor(this, R.color.material_red_200));
        mLineChartView.dismiss();
        mLineChartView.addData(lineset);
        int min = (int) Math.floor(getMin(values));
        while (min % 2 != 0) {
            min--;
        }
        int max = (int) Math.ceil(getMax(values));
        while (max % 2 != 0) {
            max++;
        }
        int step = Tools.largestDivisor(max - min);
        mLineChartView.setAxisBorderValues(min, max, step);
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

    protected void setProgressIndicator(boolean active) {
        if (active) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    void showNoDataMessage() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.days5:
                mDateRange = DATE_RANGE_5DAYS;
                unSelectChartButtonColours();
                mDays5Button.setBackgroundColor(ContextCompat.getColor(this, R.color.material_red_700));
                break;
            case R.id.months3:
                mDateRange = DATE_RANGE_3MONTHS;
                unSelectChartButtonColours();
                mMonths3Button.setBackgroundColor(ContextCompat.getColor(this, R.color.material_red_700));
                break;
            case R.id.months6:
                mDateRange = DATE_RANGE_6MONTHS;
                unSelectChartButtonColours();
                mMonths6Button.setBackgroundColor(ContextCompat.getColor(this, R.color.material_red_700));
                break;
            case R.id.years1:
                mDateRange = DATE_RANGE_1YEAR;
                unSelectChartButtonColours();
                mYears1Button.setBackgroundColor(ContextCompat.getColor(this, R.color.material_red_700));
                break;
            case R.id.years5:
                mDateRange = DATE_RANGE_5YEARS;
                unSelectChartButtonColours();
                mYears5Button.setBackgroundColor(ContextCompat.getColor(this, R.color.material_red_700));
                break;
            case R.id.max:
                mDateRange = DATE_RANGE_MAX;
                unSelectChartButtonColours();
                mMaxButton.setBackgroundColor(ContextCompat.getColor(this, R.color.material_red_700));
                break;
        }
        new GetStockData().execute();
    }

    private void unSelectChartButtonColours() {
        mDays5Button.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
        mMonths3Button.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
        mMonths6Button.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
        mYears1Button.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
        mYears5Button.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
        mMaxButton.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
    }

    private class GetStockData extends AsyncTask<Void, Void, Stock> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressIndicator(true);
        }

        @Override
        protected Stock doInBackground(Void... params) {
            Stock stock = null;
            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            try {
                switch (mDateRange) {
                    case DATE_RANGE_5DAYS:
                        from.add(Calendar.DAY_OF_WEEK, -7);
                        stock = YahooFinance.get(mSymbol, from, to, Interval.DAILY);
                        break;
                    case DATE_RANGE_3MONTHS:
                        from.add(Calendar.MONTH, -2);
                        stock = YahooFinance.get(mSymbol, from, to, MONTHLY);
                        break;
                    case DATE_RANGE_6MONTHS:
                        from.add(Calendar.MONTH, -5);
                        stock = YahooFinance.get(mSymbol, from, to, MONTHLY);
                        break;
                    case DATE_RANGE_1YEAR:
                        from.add(Calendar.MONTH, -11);
                        stock = YahooFinance.get(mSymbol, from, to, MONTHLY);
                        break;
                    case DATE_RANGE_5YEARS:
                        from.add(Calendar.MONTH, -59);
                        stock = YahooFinance.get(mSymbol, from, to, Interval.MONTHLY);
                        break;
                    case DATE_RANGE_MAX:
                        from.set(0, 0, 0);
                        stock = YahooFinance.get(mSymbol, from, to, Interval.MONTHLY);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return stock;
        }

        @Override
        protected void onPostExecute(@Nullable Stock stock) {
            if (stock == null) {
                showNoDataMessage();
            } else {
                try {
                    List<HistoricalQuote> historicalQuotes = stock.getHistory();
                    Calendar[] dates = new Calendar[historicalQuotes.size()];
                    float[] values = new float[historicalQuotes.size()];
                    Log.d(TAG, "onPostExecute: history size: " + historicalQuotes.size());

                    for (int i = 0; i < historicalQuotes.size(); i++) {
                        Calendar calendar = historicalQuotes.get(i).getDate();
                        BigDecimal high = historicalQuotes.get(i).getHigh();
                        dates[historicalQuotes.size() - 1 - i] = calendar;
                        values[historicalQuotes.size() - 1 - i] = high.floatValue();
                        Log.d(TAG, "onPostExecute: DATE: " + dates[i] + ", HIGH: " + values[i]);
                    }
                    loadChart(dates, values);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            setProgressIndicator(false);
        }
    }
}
