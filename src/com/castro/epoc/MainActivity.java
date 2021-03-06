
package com.castro.epoc;

import static com.castro.epoc.Global.CALC_PAGE;
import static com.castro.epoc.Global.CONC_PAGE;
import static com.castro.epoc.Global.CONFIG_PAGE;
import static com.castro.epoc.Global.CONT_PAGE;
import static com.castro.epoc.Global.KEY_PAGE;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends FragmentActivity implements PropertyChangeListener {
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mPages;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new Fragment();
            switch (position) {
                case CONFIG_PAGE:
                    fragment = new Configuration();
                    break;
                case CONC_PAGE:
                    fragment = new Oscillation();
                    break;
                case KEY_PAGE:
                    fragment = new Keyboard();
                    break;
                case CONT_PAGE:
                    fragment = new Contacts();
                    break;
                case CALC_PAGE:
                    fragment = new Calculation();
                    break;
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale locale = Locale.getDefault();
            switch (position) {
                case KEY_PAGE:
                    return getString(R.string.title_activity_keyboard).toUpperCase(locale);
                case CONT_PAGE:
                    return getString(R.string.title_activity_contacts).toUpperCase(locale);
                case CALC_PAGE:
                    return getString(R.string.title_activity_calculation).toUpperCase(locale);
                case CONC_PAGE:
                    return getString(R.string.title_activity_oscillation).toUpperCase(locale);
                case CONFIG_PAGE:
                    return getString(R.string.title_activity_configuration).toUpperCase(locale);
            }
            return null;
        }
    }

    // ViewPager transformer, to customize the transition animation.
    public class ZoomOut implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.98f;

        private static final float MIN_ALPHA = 1f;

        @Override
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();
            if (position < -1) {
                view.setAlpha(0);
            } else if (position <= 1) {
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
                view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)
                        * (1 - MIN_ALPHA));
            } else {
                view.setAlpha(0);
            }
        }
    }

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private UsbManager mManager;

    private Recording mRecording;

    private EyesNavigation mEyesNavigation;

    private AlertDialog mProfilesDialog;

    private int mPages = 5;

    private Menu mMenu;

    private void actionEyes() {
        if (!EyesNavigation.getActive()) {
            mEyesNavigation = new EyesNavigation(mMenu, mViewPager);
            mEyesNavigation.start();
            ToastManager.create("Eyes navigation is now active", this);
        } else {
            mEyesNavigation.stop();
            ToastManager.create("Eyes navigation deactivated", this);
        }
    }

    private void actionHelp() {
        Help help = new Help();
        help.setCurrent(mViewPager.getCurrentItem());
        help.show(getSupportFragmentManager(), null);
    }

    private void actionProfiles() {
        mProfilesDialog = Profiles.getInstance().userSelection(this);
        mProfilesDialog.show();
    }

    private void actionRec() {
        if (mRecording == null) {
            mRecording = new Recording();
            mRecording.start();
            ToastManager.create("Recording global data", this);
            mMenu.findItem(R.id.action_rec).setIcon(R.drawable.rec_stop);
        } else {
            mRecording.stop();
            ToastManager.create("Finished recording global data", this);
            mRecording = null;
            mMenu.findItem(R.id.action_rec).setIcon(R.drawable.rec_start);
        }
    }

    private void actionRecon() {
        Connection.getInstance().closeConnection();
        Connection.getInstance().isConnected(mManager, this);
    }

    // Opens the profiles selection dialog if no profile is currently selected.
    private void checkProfiles() {
        if (Profiles.getInstance().getActiveUser() == null) {
            mProfilesDialog = Profiles.getInstance().userSelection(this);
            mProfilesDialog.show();
        }
    }

    // Initiates all ViewPager related methods.
    private void initViewPager() {
        mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setPageTransformer(true, new ZoomOut());
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageSelected(int page) {
                ActiveFragment.set(page);
            }
        });
    }

    // Dialog to quickly switch the ViewPager fragment.
    private void navigationDialog() {
        final CharSequence[] sections = {
                "Keyboard", "Contacts", "Calculation", "Oscillation", "Configuration"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(sections, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mViewPager.setCurrentItem(which);
            }
        });
        builder.create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBarManager.setActionBar(getActionBar());
        ActionBarManager.setTitle("EPOC Interface");
        ActionBarManager.setState("Offline");
        mManager = (UsbManager)getSystemService(Context.USB_SERVICE);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        initViewPager();
        toggleMenuBarIcons(Connection.getInstance().getConnection());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        Battery.setMenu(mMenu);
        toggleMenuBarIcons(Connection.getInstance().getConnection());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.action_profiles:
                actionProfiles();
                return true;
            case R.id.action_recon:
                actionRecon();
                return true;
            case R.id.action_help:
                actionHelp();
                return true;
            case R.id.action_jump:
                navigationDialog();
                return true;
            case R.id.action_eyes:
                actionEyes();
                return true;
            case R.id.action_rec:
                actionRec();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onPause() {
        super.onPause();
        Connection.getInstance().closeConnection();
        Connection.getInstance().setConnectionListener(this, false);
        toggleMenuBarIcons(Connection.getInstance().getConnection());
        if (mProfilesDialog != null && mProfilesDialog.isShowing()) {
            mProfilesDialog.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkProfiles();
        Connection.getInstance().isConnected(mManager, this);
        Connection.getInstance().setConnectionListener(this, true);
        toggleMenuBarIcons(Connection.getInstance().getConnection());
    }

    @Override
    public void onStop() {
        super.onStop();
        Connection.getInstance().closeConnection();
        Connection.getInstance().setConnectionListener(this, false);
        toggleMenuBarIcons(Connection.getInstance().getConnection());
        if (mProfilesDialog != null && mProfilesDialog.isShowing()) {
            mProfilesDialog.cancel();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName() == "connection") {
            boolean connectionStatus = (Boolean)event.getNewValue();
            if (!connectionStatus) {
                Connection.getInstance().isConnected(mManager, this);
                ActionBarManager.setState("Offline");
            }
            toggleMenuBarIcons(connectionStatus);
        }
    }

    // Changes MenuBar icons based on a specified boolean.
    private void toggleMenuBarIcons(boolean b) {
        if (mMenu == null) {
            return;
        }
        mMenu.findItem(R.id.action_battery).setVisible(b ? true : false);
        mMenu.findItem(R.id.action_rec).setVisible(b ? true : false);
        mMenu.findItem(R.id.action_eyes).setVisible(b ? true : false);
        mMenu.findItem(R.id.action_recon).setVisible(b ? false : true);
    }
}
