package com.example.canteenchecker.canteenmanager.ui;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication;
import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.core.Canteen;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OverviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class OverviewFragment extends Fragment {

    private static final String TAG = OverviewFragment.class.toString();
    private OnFragmentInteractionListener mListener;

    private TextView txvName;
    private EditText edtCurrMeal;
    private EditText edtCurrPrize;
    private Button btnSaveMenuAndPrice;

    private Canteen currentCanteenState;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser)
            updateCanteenData();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCanteenData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        txvName = view.findViewById(R.id.txvName);
        edtCurrMeal = view.findViewById(R.id.edtCurrMeal);
        edtCurrPrize = view.findViewById(R.id.edtCurrPrice);
        edtCurrPrize = view.findViewById(R.id.edtCurrPrice);
        btnSaveMenuAndPrice = view.findViewById(R.id.btnSaveMenuAndPrice);

        //save button action
        btnSaveMenuAndPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCanteenState.setSetMealPrice(Float.parseFloat(edtCurrPrize.getText().toString()));
                currentCanteenState.setSetMeal(edtCurrMeal.getText().toString());
                saveCanteenData(currentCanteenState);
            }
        });

        updateCanteenData();
        return view;

    }

    private void saveCanteenData(Canteen currentCanteenState) {
        new AsyncTask<Canteen, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Canteen... canteens) {
                if (canteens != null && canteens[0] != null) {
                    try {
                        new ServiceProxy().updateCanteen(
                                "Bearer " + CanteenManagerApplication.getInstance().getAuthenticationToken(),
                                canteens[0]);

                    } catch (IOException e) {
                        return false;
                    }
                    return true;
                }
                return false;
            }
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if(aBoolean){
                    Toast.makeText(getActivity(), getString(R.string.canteenSuccessfullySavedMsg), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.saveUnsuccessful), Toast.LENGTH_SHORT);
                }
            }


        }.execute(currentCanteenState);
    }

    private void updateCanteenData() {
        new AsyncTask<Void, Void, Canteen>(){

            @Override
            protected Canteen doInBackground(Void... voids) {
                try {
                    return new ServiceProxy().getCanteen("Bearer " + CanteenManagerApplication.getInstance().getAuthenticationToken());
                } catch (IOException e) {
                    Log.e(TAG, "Error receiving Canteen data");
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Canteen canteen) {
                if(canteen != null){
                    currentCanteenState = canteen;
                    txvName.setText(canteen.getName());
                    edtCurrMeal.setText(canteen.getSetMeal());
                    edtCurrPrize.setText(Float.toString(canteen.getSetMealPrice()));
                    getFragmentManager().beginTransaction().replace(R.id.lnlReviews, ReviewStatsFragment.create(currentCanteenState.getId())).commit();
                    CanteenManagerApplication.getInstance().setCanteenId(Integer.parseInt(canteen.getId()));
                }
            }
        }.execute();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
