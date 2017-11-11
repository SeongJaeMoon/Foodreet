package app.cap.foodreet.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by clear on 2017-10-30.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = ViewPagerAdapter.class.getSimpleName();
    private List<PlaceHolderFragment> fragmentList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void init(List<InfoEntity> list) {
        fragmentList.clear();
        for (InfoEntity info : list) {
            fragmentList.add(PlaceHolderFragment.newInstance(info));
            Log.w(TAG, String.valueOf(info));
        }
    }

    public void refreshAllFragment(List<InfoEntity> list) {
        for (InfoEntity info : list) {
            for (PlaceHolderFragment fragment : fragmentList) {
                String pageTitle = fragment.getTitle();
                if (pageTitle != null && pageTitle.equals(info.getTitle())) {
                    fragment.refreshData(info);
                }
            }
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        if (fragmentList != null && position < fragmentList.size()) {
            return fragmentList.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (getItem(position) instanceof BaseFragment) {
            return ((BaseFragment) getItem(position)).getTitle();
        }
        return super.getPageTitle(position);
    }
}
