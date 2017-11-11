package app.cap.foodreet.Adapter;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import app.cap.foodreet.R;

/**
 * Created by clear on 2017-11-11.
 * A placeholder fragment containing a simple view.
 */

public class PlaceHolderFragment extends BaseFragment {

    Handler handler = new Handler();
    ProgressBar progressBar;
    TextView textView;
    InfoEntity info;

    private static final String ARG_INFO_ENTITY = "arg_info_entity";
    private static final int DELAY_TIME = 1000;
    private static final String TAG = PlaceHolderFragment.class.getSimpleName();
    public PlaceHolderFragment() {
    }

    public static PlaceHolderFragment newInstance(InfoEntity infoEntity) {
        PlaceHolderFragment fragment = new PlaceHolderFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_INFO_ENTITY, infoEntity);
        fragment.setArguments(args);
        if (infoEntity != null) {
            fragment.setTitle(infoEntity.getTitle());
        }
        return fragment;
    }

    @Override
    public void initVariables(Bundle bundle) {
        info = bundle.getParcelable(ARG_INFO_ENTITY);
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        textView = (TextView) rootView.findViewById(R.id.section_label);
        return rootView;
    }

    @Override
    protected void initData() {
        //네트워크 요청과 같은 시간이긴 작업
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isPrepared()) {
                    Log.w("initData", "Recycled");
                    return;
                }
                if (!TextUtils.isEmpty(info.getShowNumber())) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(info.getTitle());
                }
                progressBar.setVisibility(View.GONE);
            }
        }, DELAY_TIME);
    }

    public void refreshData(InfoEntity infoEntity) {
        if (infoEntity != null) {
            info = infoEntity;

            Bundle args = getArguments();
            if (args != null) {
                args.putParcelable(ARG_INFO_ENTITY, info);
            }

            if (textView != null) {
                textView.setVisibility(View.GONE);
            }
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }

            if (isFragmentVisible()) {
                initData();
            } else {
                setForceLoad(true);
            }
        }
    }

    @Override
    protected void setDefaultFragmentTitle(String title) {}

    @Override
    public void onStart() {
        super.onStart();
        Log.w(TAG, "onStart");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "onDestroy");
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.w(TAG, "onStop");
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.w(TAG, "onPause");
    }
}
