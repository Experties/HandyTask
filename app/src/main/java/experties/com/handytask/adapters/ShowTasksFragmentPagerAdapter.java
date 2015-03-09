package experties.com.handytask.adapters;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;

import experties.com.handytask.fragments.ShowOnListFragment;
import experties.com.handytask.fragments.ShowOnMapFragment;

/**
 * Created by vincetulit on 3/7/15.
 */
public class ShowTasksFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = {"MAP VIEW", "LIST VIEW"};

    public ShowTasksFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    // The order and creation of fragments within the pager
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ShowOnMapFragment();
        } else if (position == 1) {
            return new ShowOnListFragment();
        }
        else {
            return null;
        }
    }

    // Return the tab title
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    // How many fragments are to swipe between?
    @Override
    public int getCount() {
        return tabTitles.length;
    }

}
