package com.johnpepper.eeapp.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import com.johnpepper.eeapp.R;
import com.johnpepper.eeapp.model.EEUser;
import com.johnpepper.eeapp.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CategoryStatPageFragment extends Fragment {

    private static final String ARG_INDEX = "index";
    private static final String ARG_STAT = "stat";

    private int mIndex;
    private JSONObject mStat;

    private TextView categoryDescriptionTextView;
    private TextView participantsNumberTextView;
    private TextView rankingTextView;
    private RelativeLayout rankingGroupLayout;
    private BarChart chart;


    public static CategoryStatPageFragment newInstance(int index , JSONObject stat) {
        CategoryStatPageFragment fragment = new CategoryStatPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);
        args.putString(ARG_STAT, stat.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init with arguments
        if (getArguments() != null) {
            mIndex = getArguments().getInt(ARG_INDEX);
            try {
                mStat = new JSONObject(getArguments().getString(ARG_STAT));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle s) {

        View view = inflater.inflate(
                R.layout.fragment_category_stat,
                container,
                false
        );

        categoryDescriptionTextView = (TextView) view.findViewById(R.id.categoryDescriptionTextView);
        participantsNumberTextView = (TextView) view.findViewById(R.id.participantsNumberTextView);
        rankingTextView = (TextView) view.findViewById(R.id.rankingTextView);

        rankingGroupLayout = (RelativeLayout) view.findViewById(R.id.rankingGroupLayout);
        chart = (BarChart) view.findViewById(R.id.chart);

        try {
            categoryDescriptionTextView.setText(mStat.getString("description"));
            int participantsNumber = mStat.getInt("participants_count");
            if (participantsNumber == 0) {
                participantsNumberTextView.setText("No Reviews");
                rankingGroupLayout.setVisibility(View.INVISIBLE);
            } else {

                ValueFormatter formatter = new StatValueFormatter(1);

                participantsNumberTextView.setText(participantsNumber + " Reviews");
                int rank = mStat.getInt("rank");
                if (rank > 0 && rank < 6 && EEUser.getCurrentUser().isEmployee()) {
                    rankingGroupLayout.setVisibility(View.VISIBLE);
                    rankingTextView.setText(StringUtil.addSuffixToNumber(rank));
                }
                ArrayList<BarEntry> entries = new ArrayList<>();
                entries.add(new BarEntry((float)Math.round(mStat.getDouble("average_rating_user") * 10) / 10, 0));
                entries.add(new BarEntry((float)Math.round(mStat.getDouble("average_rating_all") * 10) / 10, 1));

                entries.add(new BarEntry((float)Math.round(mStat.getDouble("top") * 10) / 10, 2));
                //entries.add(new BarEntry((float)Math.round(mStat.getDouble("low") * 10) / 10, 3));
                //entries.add(new BarEntry((float)Math.round(mStat.getDouble("top25") * 10) / 10, 4));

                BarDataSet dataset = new BarDataSet(entries, "");

                ArrayList<String> labels = new ArrayList<String>();
                labels.add("Yours " + formatter.getFormattedValue((float)Math.round(mStat.getDouble("average_rating_user") * 10) / 10, null, 0, null));
                labels.add("Average " + formatter.getFormattedValue((float)Math.round(mStat.getDouble("average_rating_all") * 10) / 10, null, 1, null));

                labels.add("#Top " + formatter.getFormattedValue((float)Math.round(mStat.getDouble("top") * 10) / 10, null, 2, null));

                //labels.add("low " + formatter.getFormattedValue((float)Math.round(mStat.getDouble("low") * 10) / 10, null, 3, null));
                //labels.add("top 25% " + formatter.getFormattedValue((float)Math.round(mStat.getDouble("top25") * 10) / 10, null, 4, null));

                BarData data = new BarData(labels, dataset);
                data.setValueFormatter(formatter);

                chart.getAxisLeft().setDrawLabels(false);
                chart.getAxisRight().setDrawLabels(false);

                chart.animateXY(2000, 2000);
                chart.setData(data);
                //chart.invalidate();

                dataset.setColors(ColorTemplate.COLORFUL_COLORS);
            }
        }catch (JSONException exception) {
            exception.printStackTrace();
        }

        return view;

    }

    public class StatValueFormatter implements ValueFormatter {

        /** decimalformat for formatting */
        private DecimalFormat mFormat;

        /**
         * Constructor that specifies to how many digits the value should be
         * formatted.
         *
         * @param digits
         */
        public StatValueFormatter(int digits) {

            StringBuffer b = new StringBuffer();
            for (int i = 0; i < digits; i++) {
                if (i == 0)
                    b.append(".");
                b.append("0");
            }

            mFormat = new DecimalFormat("###,###,###,##0" + b.toString());
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

            // put more logic here ...
            // avoid memory allocations here (for performance reasons)

            return mFormat.format(value);
        }
    }

}
