package com.example.administrator.qrcode;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

       /* button = (Button)findViewById(R.id.scanButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,QrCodeActivity.class);
                startActivityForResult( i,REQUEST_CODE_QR_SCAN);
            }
        });*/


    }
    /*public static class PlaceholderFragment extends Fragment {

        private static final String KEY_COLOR = "key_color";

        public PlaceholderFragment() {
        }

        // Method static dạng singleton, cho phép tạo fragment mới, lấy tham số đầu vào để cài đặt màu sắc.
        public static PlaceholderFragment newInstance(int color) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();//truyen du lieu
            args.putInt(KEY_COLOR, color);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            View rootViewFragmentScan = inflater.inflate(R.layout.fragment_scan, container, false);
            RelativeLayout relativeLayout = (RelativeLayout) rootView.findViewById(R.id.rl_fragment);
            RelativeLayout relativeLayout2 = (RelativeLayout) rootViewFragmentScan.findViewById(R.id.r2_fragment);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);*/

          /*  switch (getArguments().getInt(KEY_COLOR)) {
                case 1:
                    relativeLayout2.setBackgroundColor(Color.YELLOW);
                   // textView.setText("Green");
                    break;
                case 2:
                    relativeLayout.setBackgroundColor(Color.RED);
                    textView.setText("Red");
                    break;
                default:
                    relativeLayout.setBackgroundColor(Color.GREEN);
                    textView.setText("Green");
                    break;
            }



            return rootView;
        }


    }*/
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // position + 1 vì position bắt đầu từ số 0.
            //return PlaceholderFragment.newInstance(position + 1);
            Fragment fragment = null;
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
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
            }
            return null;
        }
    }
}
