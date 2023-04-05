package ml.sky233.suiteki;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import github.com.st235.lib_expandablebottombar.ExpandableBottomBar;
import ml.sky233.suiteki.databinding.ActivityMainTestBinding;
import ml.sky233.suiteki.ui.device.DeviceFragment;
import ml.sky233.suiteki.ui.home.HomeFragment;

public class MainTestActivity extends AppCompatActivity {

    private ActivityMainTestBinding binding;

    public List<Fragment> fragmentList;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getApplicationContext().getResources().getConfiguration().uiMode != 0x21)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = ActivityMainTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= 31)
            ActivityCompat.requestPermissions(MainTestActivity.this, new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}, 1);
        ExpandableBottomBar bar = findViewById(R.id.expandable_bottom_bar);
        fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new DeviceFragment());
        ViewPager viewPager = findViewById(R.id.view_page);
        viewPager.setAdapter(new AppFragmentPageAdapter(getSupportFragmentManager(), fragmentList));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Do Nothing
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bar.getMenu().select(R.id.navigation_home);
                        break;
                    case 1:
                        bar.getMenu().select(R.id.navigation_dashboard);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Do Nothing
            }
        });
        bar.setOnItemSelectedListener((view, menuItem, aBoolean) -> {
            if (menuItem.getId() == R.id.navigation_home) {
                viewPager.setCurrentItem(0);
            } else if (menuItem.getId() == R.id.navigation_dashboard) {
                viewPager.setCurrentItem(1);
            }
            return null;
        });
    }

    public class AppFragmentPageAdapter extends FragmentPagerAdapter {
        public List<Fragment> mFragmentList;

        public AppFragmentPageAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            mFragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {

            return mFragmentList == null ? null : mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList == null ? 0 : mFragmentList.size();
        }
    }

}