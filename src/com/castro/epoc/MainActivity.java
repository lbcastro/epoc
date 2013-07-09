
package com.castro.epoc;

import static com.castro.epoc.Global.CALC_VIEW;
import static com.castro.epoc.Global.CONC_VIEW;
import static com.castro.epoc.Global.CONFIG_VIEW;
import static com.castro.epoc.Global.CONT_VIEW;
import static com.castro.epoc.Global.KEY_VIEW;

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
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity implements
        PropertyChangeListener {

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
                case CONFIG_VIEW:
                    fragment = new Configuration();
                    break;
                case CONC_VIEW:
                    fragment = new Oscillation();
                    break;
                case KEY_VIEW:
                    fragment = new Keyboard();
                    break;
                case CONT_VIEW:
                    fragment = new Contacts();
                    break;
                case CALC_VIEW:
                    fragment = new Calculation();
                    break;
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale locale = Locale.getDefault();
            switch (position) {
                case KEY_VIEW:
                    return getString(R.string.title_activity_keyboard).toUpperCase(
                            locale);
                case CONT_VIEW:
                    return getString(R.string.title_activity_contacts).toUpperCase(
                            locale);
                case CALC_VIEW:
                    return getString(R.string.title_activity_calculation)
                            .toUpperCase(locale);
                case CONC_VIEW:
                    return getString(R.string.title_activity_oscillation)
                            .toUpperCase(locale);
                case CONFIG_VIEW:
                    return getString(R.string.title_activity_configuration)
                            .toUpperCase(locale);
            }
            return null;
        }
    }

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private UsbManager mManager;
    private int mPages = 5;
    private Menu mMenu;

    private void checkProfiles() {
        if (Profiles.getActiveUser() == null) {
            Profiles profiles = new Profiles();
            profiles.userSelection(this).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBarManager.setActionBar(getActionBar());
        ActionBarManager.setTitle("EPOC Interface");
        ActionBarManager.setState("Offline");
        mManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        Connection.getInstance().setMainListener(this);
        Training.updateLda();
        toggleMenuBarIcons(Connection.getInstance().getConnection());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        toggleMenuBarIcons(Connection.getInstance().getConnection());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.action_profiles:
                Profiles profiles = new Profiles();
                profiles.userSelection(this).show();
                return true;
            case R.id.action_recon:
                Connection.getInstance().isConnected(mManager, this);
                return true;
            case R.id.action_help:
                Help help = new Help();
                help.setCurrent(mViewPager.getCurrentItem());
                help.show(getSupportFragmentManager(), null);
                return true;
            case R.id.action_jump:
                navigationDialog();
                return true;
                // TODO: EYES NAVIGATION TOGGLE
                // TODO: GLOBAL RECORDING
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkProfiles();
        Connection.getInstance().isConnected(mManager, this);
        Connection.getInstance().setMainListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Connection.getInstance().closeConnection();
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName() == "connection") {
            if (!(Boolean) event.getNewValue()) {
                Connection.getInstance().isConnected(mManager, this);
            }
            toggleMenuBarIcons((Boolean) event.getNewValue());
        } else if (event.getPropertyName() == "battery") {
            Battery.changeDrawable(mMenu);
        }
    }

    private void toggleMenuBarIcons(boolean b) {
        if (mMenu == null) {
            return;
        }
        mMenu.findItem(R.id.action_battery).setVisible(b ? true : false);
        mMenu.findItem(R.id.action_rec).setVisible(b ? true : false);
        mMenu.findItem(R.id.action_eyes).setVisible(b ? true : false);
        mMenu.findItem(R.id.action_recon).setVisible(b ? false : true);
    }

    private void navigationDialog() {
        final CharSequence[] sections = {
                "Keyboard", "Contacts",
                "Calculation", "Oscillation", "Configuration"
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
}
