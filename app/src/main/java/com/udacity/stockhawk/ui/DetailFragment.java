/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.Utility;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private List<QuoteEntry> rawEntries;

    private static final int DETAIL_LOADER = 0;

    LineChart chart;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        chart = (LineChart) rootView.findViewById(R.id.chart);
        chart.setTouchEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setEnabled(true);
        chart.setContentDescription("");
        chart.setNoDataTextColor(Color.WHITE);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisRight().setTextColor(Color.WHITE);
        chart.getLegend().setEnabled(false);

        chart.setDrawGridBackground(false);
        chart.setEnabled(true);

        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        XAxis xaxis = chart.getXAxis();
        xaxis.setTextColor(Color.WHITE);
        xaxis.setLabelCount(5);
        xaxis.setPosition(XAxis.XAxisPosition.TOP);
        xaxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return Utility.getFormattedYearMonth(Long.valueOf(rawEntries.get((int) value).getDatetime()));
            }
        });
        xaxis.setDrawGridLines(false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        getLoaderManager().initLoader(DETAIL_LOADER, arguments, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            return new CursorLoader(
                    getActivity(),
                    Contract.Quote.makeUriForStock(args.getString("THE_QUOTE_KEY")),
                    Contract.Quote.QUOTE_HISTORY_COLUMNS.toArray(new String[]{}),
                    null,
                    null,
                    null
            );
    }


    class QuoteEntry implements Comparable<QuoteEntry> {

        private long datetime;
        private float value;
        public QuoteEntry(long datetime, float stockValue) {
            this.datetime = datetime;
            this.value = stockValue;
        }

        public long getDatetime() {
            return datetime;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }

        @Override
        public int compareTo(QuoteEntry e) {

            if (datetime > e.datetime) {
                return 1;
            }
            else if (datetime < e.datetime) {
                return -1;
            }
            else {
                return 0;
            }

        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Entry> entries = new ArrayList<Entry>();
        rawEntries = new ArrayList<QuoteEntry>();


        if (data != null && data.moveToFirst()) {
            int count = data.getCount();
            for ( int i = 0; i < count; i++ ) {
                data.moveToPosition(i);
                String theString = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_HISTORY));
                String valuesPairs[] = TextUtils.split(theString, "\n");
                int countPairs = valuesPairs.length;
                for( int j = 0; j < countPairs; j++ ) {
                    String pair = valuesPairs[j];
                    if (!TextUtils.isEmpty(pair)) {
                        String keyValue[] = TextUtils.split(pair, ",");
                        rawEntries.add(new QuoteEntry( Long.valueOf(keyValue[0].trim()),
                                Float.valueOf(keyValue[1].trim())));
                    }
                }
                break;
            }
            Collections.sort(rawEntries);
            int countPairs = rawEntries.size();
            for( int j = 0; j < countPairs; j++ ) {
                entries.add(new Entry(j, rawEntries.get(j).getValue()));
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, ""); // add entries to dataset
        dataSet.setLineWidth(2f);
        dataSet.setHighLightColor(Color.RED);
        dataSet.setColor(Color.WHITE);
        dataSet.setValueTextColor(Color.GREEN);


        dataSet.setHighlightEnabled(true); // allow highlighting for DataSet

        dataSet.setDrawHighlightIndicators(false);

        LineData lineData = new LineData(dataSet);
        String l[] = lineData.getDataSetLabels();

        chart.setData(lineData);
        chart.invalidate();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}