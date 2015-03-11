package experties.com.handytask.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.parse.ParseUser;

import experties.com.handytask.R;
import experties.com.handytask.fragments.LoginFragment;
import experties.com.handytask.fragments.RegisterFragment;


public class LoginActivity extends ActionBarActivity {
    private ViewPager pager;
    private LoginPagerAdaptor pagerAdaptor;
    private TextView mTitle;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Intent taskActivity = new Intent(LoginActivity.this, TaskCreatedActivity.class);
            startActivity(taskActivity);
        } else {
            // show the signup or login screen
            setContentView(R.layout.activity_login);

            TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            phoneNumber = tMgr.getLine1Number();
            try {
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                Phonenumber.PhoneNumber usPhoneNumber = phoneUtil.parse(phoneNumber, "US");
                phoneNumber = phoneUtil.format(usPhoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
            } catch (NumberParseException e) {
            }

            Typeface fontJamesFajardo = Typeface.createFromAsset(this.getAssets(), "fonts/JamesFajardo.ttf");

            Toolbar toolbar = (Toolbar) findViewById(R.id.tolBrLogin);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            mTitle = (TextView) toolbar.findViewById(R.id.login_toolbar_title);

            //toolbar.setLogo(R.drawable.ic_tweets);
            mTitle.setTypeface(fontJamesFajardo);
            mTitle.setText(getResources().getString(R.string.title));

            pager = (ViewPager) findViewById(R.id.vwPgrLogin);
            pagerAdaptor = new LoginPagerAdaptor(getSupportFragmentManager());
            pager.setAdapter(pagerAdaptor);
            PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.LoginTabs);
            tabStrip.setViewPager(pager);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    public class LoginPagerAdaptor extends FragmentPagerAdapter /*implements PagerSlidingTabStrip.IconTabProvider*/ {
        private String[] tabTitle = {"LOGIN", "SIGN UP"};
        //private int tabIcons[] = {R.drawable.ic_launcher, R.drawable.ic_compose};
        public LoginPagerAdaptor(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return LoginFragment.newInstance(phoneNumber);
                case 1:
                    return RegisterFragment.newInstance(phoneNumber);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return tabTitle.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitle[position];
        }

        /*@Override
        public int getPageIconResId(int position) {
            return tabIcons[position];
        }*/
    }
}
