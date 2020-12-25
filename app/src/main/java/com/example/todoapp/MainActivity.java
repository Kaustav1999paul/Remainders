package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView NEXT;
    private ViewPager viewPager;
    private LinearLayout layoutDots;
    private IntroPref introPref;
    private int[] layouts;
    private TextView[] dots;
    private MyViewPageAdapter viewPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        introPref = new IntroPref(this);
        if (!introPref.isFirstTimeLaunch()){
            LoadRegisterActivity();
            finish();
        }

        NEXT = findViewById(R.id.next);
        viewPager = findViewById(R.id.viewPager);
        layoutDots = findViewById(R.id.layoutDots);

        layouts = new int[]{
                R.layout.layout_one,
                R.layout.layout_two,
                R.layout.layout_three,
                R.layout.layout_four
        };

        NEXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = getItem(+1);
                if (current< layouts.length){
                    viewPager.setCurrentItem(current);
                }else {
                    LoadRegisterActivity();
                }
            }
        });

        viewPageAdapter = new MyViewPageAdapter();
        viewPager.setAdapter(viewPageAdapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);
        addBottomDots(0);

    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            if (position == layouts.length -1){
                NEXT.setText("GET STARTED");
            }else {
                NEXT.setText("NEXT");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    private void addBottomDots(int currentPage){
        dots = new TextView[layouts.length];
        int [] activeColor = getResources().getIntArray(R.array.active);
        int [] inactiveColor = getResources().getIntArray(R.array.inactive);
        layoutDots.removeAllViews();

        for (int i=0; i<dots.length;i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(50);
            dots[i].setTextColor(inactiveColor[currentPage]);
            layoutDots.addView(dots[i]);
        }
        if (dots.length > 0){
            dots[currentPage].setTextColor(activeColor[currentPage]);
        }
    }

    public class MyViewPageAdapter extends PagerAdapter{

        LayoutInflater layoutInflater;
        public MyViewPageAdapter(){
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    private int getItem(int i){
        return viewPager.getCurrentItem() + 1;
    }

    private void LoadRegisterActivity() {
        introPref.setIsFirstTimeLaunch(false);
        startActivity(new Intent(MainActivity.this, AuthActivity.class));
        finish();
    }
}