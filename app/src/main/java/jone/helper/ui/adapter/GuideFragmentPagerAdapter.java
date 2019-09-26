package jone.helper.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import jone.helper.R;
import jone.helper.ui.fragments.GuideFragment;

/**
 * Created by jone.sun on 2016/1/13.
 */
public class GuideFragmentPagerAdapter extends FragmentPagerAdapter {
    protected static final String[] CONTENT = new String[] {
            "你若想得到这世界最好的东西，先提供这世界最好的你。",
            "如果你没有梦想，那么你只能为别人的梦想打工。",
            "你的父母仍在为你打拼，这就是你今天坚强的理由。",
            "什么是坚持?就是每天告诉自己，再坚持一天。",
    };
    public GuideFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        boolean end = position == CONTENT.length - 1;
        return GuideFragment.newInstance(CONTENT[position % CONTENT.length], end, getBgColor(position));
    }

    @Override
    public int getCount() {
        return CONTENT.length;
    }

    public static int getBgColor(int position){
        if(position == 0){
            return R.color.light_blue_500;
        }else if(position == 1){
            return R.color.light_blue_400;
        }else if(position == 2){
            return R.color.light_blue_300;
        }else if(position == 3){
            return R.color.light_blue_200;
        }
        return R.color.light_blue_300;
    }
}
