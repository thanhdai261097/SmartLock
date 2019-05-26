package com.example.administrator.qrcode;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    public static ViewPager mViewPager;
    TabLayout tabLayout;
    private String name = null;
    private String phone = null;
    private String dateExp = null;
    private String email = null;
    private Boolean isLock = false;

    public UserInterface userInterface;
    //region get-set method
    public Boolean getIsLock() {
        return isLock;
    }
    public void setIsLock(Boolean bool) {
        this.isLock = bool;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDateExp() {
        return dateExp;
    }

    public void setDateExp(String dateExp) {
        this.dateExp = dateExp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //endregion

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // position + 1 vì position bắt đầu từ số 0.
            //return PlaceholderFragment.newInstance(position + 1);
            Fragment fragment = null;
            Log.d("d","d");
            switch (position){
                case 0:
                    fragment = new Fragment_CreateQR();
                    break;
                case 1:
                    fragment =  new Fragment_ScanQR();

            }

            return  fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Login";
                case 1:
                    return "Scan";
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);

        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = new TabLayout(this);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (userInterface != null)
                userInterface.update();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        getDataFromSharePreference();

        setViewListener();
    }

    private void getDataFromSharePreference(){
        SharedPreferences sp = getSharedPreferences(Fragment_CreateQR.SHARE_PREF_ID, Context.MODE_PRIVATE);
        String cmnd = sp.getString(Fragment_CreateQR.KEY_CMND,null);
        String email = sp.getString(Fragment_CreateQR.KEY_EMAIL,null);
        isLock = sp.getBoolean(Fragment_CreateQR.KEY_ISLOCK,true);
        if ( cmnd == null )
            mViewPager.setCurrentItem(0);
        else
            mViewPager.setCurrentItem(1);
    }
    public void setViewListener(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void setUserInterface(UserInterface m) {
        this.userInterface = m;
    }
}

interface UserInterface {
    void update();
}
