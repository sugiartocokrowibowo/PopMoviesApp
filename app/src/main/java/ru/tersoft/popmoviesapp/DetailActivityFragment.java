package ru.tersoft.popmoviesapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailActivityFragment extends Fragment {
    MovieInfo mMovieInfo;
    private int mMovieId;
    private RecyclerView mRecyclerView;
    private MovieInfoAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Integer> mDataSetTypes = new ArrayList<>();
    View v;

    public DetailActivityFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This makes AsyncTask not to stuck after changing configuration
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_detail, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.mainView);
        Bundle args = getArguments();
        if (args != null) {
            int pos = getArguments().getInt("position");
            mMovieInfo = Data.Movies.get(pos);
            mMovieId = pos;
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new MovieInfoAdapter(mMovieId, mDataSetTypes, getActivity());
            mRecyclerView.setAdapter(mAdapter);
            // Parameters: 0 - api key (string), 1 - movie position (int)
            Object[] params = {getResources().getString(R.string.api_key), pos};
            loadMovieInfo(params);
        }
        return v;
    }

    private void loadMovieInfo(Object... params) {
        MovieInfoLoader testAsyncTask = new MovieInfoLoader(new DetailActivityFragment.FragmentCallback() {
            @Override
            public void onTaskDone() {
                // Callback from MovieInfoLoader task
                if(getActivity() != null) {
                    mDataSetTypes.clear();
                    if (mMovieInfo.mDesc != null) {
                        mDataSetTypes.add(0);
                    }
                    mDataSetTypes.add(1);
                    mDataSetTypes.add(2);
                    String mBackdropPath = mMovieInfo.mBackdropPath;
                    String mName = mMovieInfo.mName;
                    CollapsingToolbarLayout collapsingToolbar =
                            (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar);
                    collapsingToolbar.setTitle(mName);
                    // Load backdrop image to toolbar
                    ImageView backdropView = (ImageView) getActivity().findViewById(R.id.backdropView);
                    Picasso.with(getActivity())
                            .load(mBackdropPath)
                            .config(Bitmap.Config.RGB_565)
                            .tag(getActivity())
                            .into(backdropView);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        testAsyncTask.execute(params);
    }

    public interface FragmentCallback {
        void onTaskDone();
    }
}
