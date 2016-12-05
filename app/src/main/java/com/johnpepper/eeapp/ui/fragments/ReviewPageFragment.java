package com.johnpepper.eeapp.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.johnpepper.eeapp.R;
import com.johnpepper.eeapp.asynctask.EEApiManager;
import com.johnpepper.eeapp.listener.CompletedListener;
import com.johnpepper.eeapp.model.EEUser;
import com.johnpepper.eeapp.ui.activities.ReviewActivity;
import com.johnpepper.eeapp.util.EEImageLoader;
import com.johnpepper.eeapp.util.StringUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class ReviewPageFragment extends Fragment {

    private static final int[] imageResourceArray = new int[] {R.mipmap.tour_bg_1, R.mipmap.tour_bg_2,R.mipmap.tour_bg_3, R.mipmap.tour_bg_4};
    private static final String ARG_INDEX = "index";
    private static final String ARG_REVIEW_ATTRIBUTE = "review_attribute";

    private int mIndex;
    private JSONObject mReviewAttribute;


    private Button flagButton;
    private TextView nameTextView;
    private ImageView profileImageView;
    private TextView companyQuestionTextView;
    private TextView questionDescriptionTextView;

    private RelativeLayout companyQuestionContainerLayout;
    private RelativeLayout questionContainerLayout;

    private RatingBar ratingBar;

    private TextView agreeTextView;
    private TextView neutralTextView;
    private TextView disagreeTextView;


    public static ReviewPageFragment newInstance(int index , JSONObject reviewAttribute) {
        ReviewPageFragment fragment = new ReviewPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);
        args.putString(ARG_REVIEW_ATTRIBUTE, reviewAttribute.toString());
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
                mReviewAttribute = new JSONObject(getArguments().getString(ARG_REVIEW_ATTRIBUTE));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle s) {

        View view = inflater.inflate(
                R.layout.fragment_review_page,
                container,
                false
        );

        flagButton = (Button) view.findViewById(R.id.flagButton);
        nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        profileImageView = (ImageView) view.findViewById(R.id.profileImageView);
        companyQuestionTextView = (TextView) view.findViewById(R.id.companyQuestionTextView);
        questionDescriptionTextView = (TextView) view.findViewById(R.id.questionDescriptionTextView);
        companyQuestionContainerLayout = (RelativeLayout) view.findViewById(R.id.companyQuestionContainerLayout);
        questionContainerLayout = (RelativeLayout) view.findViewById(R.id.questionContainerLayout);

        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);

        agreeTextView = (TextView) view.findViewById(R.id.agreeTextView);
        neutralTextView = (TextView) view.findViewById(R.id.neutralTextView);
        disagreeTextView = (TextView) view.findViewById(R.id.disagreeTextView);

        try {
            if (mReviewAttribute.getInt("type") == 0) {

                questionContainerLayout.getLayoutParams().height = 288;

                profileImageView.setVisibility(View.VISIBLE);
                nameTextView.setVisibility(View.VISIBLE);
                companyQuestionTextView.setVisibility(View.INVISIBLE);
                companyQuestionContainerLayout.setVisibility(View.INVISIBLE);

                agreeTextView.setVisibility(View.INVISIBLE);
                neutralTextView.setVisibility(View.INVISIBLE);
                disagreeTextView.setVisibility(View.INVISIBLE);

                nameTextView.setText("COMPANY");
                EEImageLoader.showImage(profileImageView, StringUtil.companyImageURLFromCompanyID(mReviewAttribute.getString("company_id")));
                questionDescriptionTextView.setVisibility(View.VISIBLE);

                questionDescriptionTextView.setText(mReviewAttribute.getString("description").toUpperCase());

                //companyQuestionTextView.setText(mReviewAttribute.getString("category_description"));
            } else {

                questionContainerLayout.getLayoutParams().height = 192;

                profileImageView.setVisibility(View.VISIBLE);
                nameTextView.setVisibility(View.VISIBLE);
                companyQuestionTextView.setVisibility(View.GONE);
                companyQuestionContainerLayout.setVisibility(View.GONE);

                nameTextView.setText(mReviewAttribute.getString("first_name").toUpperCase());
                EEImageLoader.showImage(profileImageView, StringUtil.userImageURLFromUserID(mReviewAttribute.getString("target_user_id")));
                questionDescriptionTextView.setText(mReviewAttribute.getString("description").toUpperCase());
            }
            if (mReviewAttribute.getString("user_name").equalsIgnoreCase("Today")) {
                flagButton.setVisibility(View.GONE);
            } else {
                flagButton.setVisibility(View.VISIBLE);
            }
        }catch (JSONException exception) {
            exception.printStackTrace();
        }

        flagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent=new Intent(Intent.ACTION_SEND);
                    String[] recipients={"alpha@eeapp.co"};
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                    intent.putExtra(Intent.EXTRA_SUBJECT,"EEApp - Flag User");
                    intent.putExtra(Intent.EXTRA_TEXT,"Something wrong? Please let us know and we'll fix it right away! Thank you for helping make EEApp better. You flagged " + mReviewAttribute.getString("user_name"));
                    intent.setType("text/html");
                    startActivity(Intent.createChooser(intent, "Send mail"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(final RatingBar ratingBar, final float rating, boolean fromUser) {

                try {
                    ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("user_id", EEUser.getCurrentUser().userInfo.getString("id")));
                    params.add(new BasicNameValuePair("question_id", mReviewAttribute.getString("question_id")));
                    params.add(new BasicNameValuePair("target_user_id", mReviewAttribute.getString("target_user_id")));
                    params.add(new BasicNameValuePair("rating", String.valueOf(rating)));

                    new EEApiManager("ratings", params, new CompletedListener() {
                        @Override
                        public void onCompleted(JSONObject result) {
                            try {
                                if (result.getString("result").equalsIgnoreCase("success")) {

                                    String ratingID = result.getJSONObject("data").getString("id");
                                    //ratingBar.setEnabled(false);
                                    ReviewActivity activity = (ReviewActivity) getActivity();
                                    if (activity != null) {
                                        if (mReviewAttribute.getInt("target_user_id") == -1) {
                                            activity.didPostRate(mIndex, (int) rating, ratingID, mReviewAttribute.getInt("category_id"), mReviewAttribute.getString("user_name"));
                                        } else {
                                            activity.didPostRate(mIndex, (int) rating, ratingID, mReviewAttribute.getInt("category_id"), mReviewAttribute.getString("first_name"));
                                        }

                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).execute(new String[]{"POST"});
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
        return view;

    }

}
