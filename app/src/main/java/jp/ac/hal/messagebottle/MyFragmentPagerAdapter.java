package jp.ac.hal.messagebottle;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muto.masakazu on 2017/09/02.
 */

public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments = new ArrayList<>();

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount(){
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //positionごとのタブ名をreturn
        switch (position){
            case 0:
                return "main";
            case 1:
                return "camara";
            case 2:
                return "setting";
            default:
                return null;
        }

    }

    public void addFragment(Fragment ft) {
        mFragments.add(ft);
    }

}
