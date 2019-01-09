package com.example.canteenchecker.canteenmanager.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication;
import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.core.Rating;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;
import com.example.canteenchecker.canteenmanager.service.MyFirebaseMessagingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class RatingsFragment extends Fragment {
    private static final String TAG = ReviewStatsFragment.class.toString();
    private RatingsAdapter ratingsAdapter = new RatingsAdapter();
    private SwipeRefreshLayout srlReviews;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateReviews();
        }
    };

    public static Fragment create(String canteenId) {
        RatingsFragment ratingsFragment = new RatingsFragment();
        return ratingsFragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser)
            updateReviews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        RecyclerView rcvReviews = view.findViewById(R.id.rcvReviews);
        rcvReviews.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvReviews.setAdapter(ratingsAdapter);

        srlReviews = view.findViewById(R.id.srlReviews);
        srlReviews.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateReviews();
            }
        } );

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, MyFirebaseMessagingService.canteenChangedIntentFilter());

        updateReviews();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateReviews();
    }

    private void updateReviews() {
        srlReviews.setRefreshing(false);

        new AsyncTask<String, Void, Collection<Rating>>(){
            @Override
            protected Collection<Rating> doInBackground(String... strings) {
                if(strings[0] != "null"){
                    try {
                        return new ServiceProxy().getAllRatings(strings[0]);
                    } catch (IOException e) {
                        Toast.makeText(getActivity(),getString(R.string.msg_UnableToRetrieveRatings), Toast.LENGTH_SHORT).show();
                        return null;
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Collection<Rating> ratings) {
                ratingsAdapter.displayRatings(ratings);
                srlReviews.setRefreshing(false);

            }
        }.execute(String.valueOf(CanteenManagerApplication.getInstance().getCanteenId()));

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private static class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.ViewHolder> {
        private List<Rating> ratingList = new ArrayList<>();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rating_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RatingsAdapter.ViewHolder holder, int position) {
            final Rating c = ratingList.get(position);

            holder.rtbRatingStars.setRating(c.getRatingPoints());
            holder.txvRatingUserName.setText(c.getUsername());
            holder.txvRemark.setText(c.getRemark());

            holder.btnDeleteRating.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Context context = holder.itemView.getContext();
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            deleteRating(c.getRatingId(), context);

                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Are you sure?")
                                   .setPositiveButton("Yes", dialogClickListener)
                                   .setNegativeButton("No", dialogClickListener)
                                   .show();
                        }
                    }
            );
        }

        private void deleteRating(final int ratingId, final Context context) {
            boolean deleteAccepted = false;
            new AsyncTask<Integer, Void, Integer>(){
                @Override
                protected Integer doInBackground(Integer... integers) {
                    if(integers[0] != null){
                        try {
                            new ServiceProxy().deleteRating("Bearer " + CanteenManagerApplication.getInstance().getAuthenticationToken(),
                                                              String.valueOf(integers[0]));
                            return ratingId;
                        } catch (IOException e) {
                            return null;
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Integer aInteger) {
                    if(aInteger != null){
                        for(Iterator<Rating> iterator = ratingList.iterator(); iterator.hasNext();){
                            Rating r = iterator.next();
                            if(r.getRatingId() == aInteger){
                                iterator.remove();
                            }
                        }
                        notifyDataSetChanged();
                        Toast.makeText(context,context.getString(R.string.msg_deleteSuccessful),Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context,context.getString(R.string.msg_deleteUnSuccessful),Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute(ratingId);
        }

        void displayRatings(Collection<Rating> ratings) {
            ratingList.clear();
            if(ratings != null) {
                ratingList.addAll(ratings);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return ratingList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            private final RatingBar rtbRatingStars = itemView.findViewById(R.id.rtbRatingStars);
            private final TextView txvRatingUserName = itemView.findViewById(R.id.txvRatingUserName);
            private final TextView txvRemark = itemView.findViewById(R.id.txvRemark);
            private final ImageButton btnDeleteRating = itemView.findViewById(R.id.btnDeleteRating);
            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
