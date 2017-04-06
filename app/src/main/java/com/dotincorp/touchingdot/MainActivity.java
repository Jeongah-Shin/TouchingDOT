package com.dotincorp.touchingdot;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wjddk on 2017-03-31.
 */

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);

        initViewPager();

    }

    public void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        //프래그먼트 리스트에 추가
        List<Fragment> listFragments = new ArrayList<>();
        listFragments.add(new BrailleFragment());
        listFragments.add(new AlphabetFragment());
        listFragments.add(new SpecialFragment());
        //TabPagerAdapter에 리스트를 넘겨준 후 ViewPager와 연결함.
        TabPagerAdapter fragmentPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), listFragments);
        viewPager.setAdapter(fragmentPagerAdapter);
        //각 탭의 이름 지정
        tabLayout = (TabLayout) findViewById(R.id.admin_tab);
        tabLayout.addTab(tabLayout.newTab().setText("점자"));
        tabLayout.addTab(tabLayout.newTab().setText("알파벳"));
        tabLayout.addTab(tabLayout.newTab().setText("특수문자"));
        tabLayout.setTabTextColors(R.color.colorBackground, R.color.colorAccent);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                switch(tab.getPosition()){
                    case 0:
                        viewPager.setCurrentItem(tab.getPosition());
                        break;
                    case 1:
                        viewPager.setCurrentItem(tab.getPosition());
                        break;
                    case 2:
                        viewPager.setCurrentItem(tab.getPosition());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    //@OnClick({R.id.tutorial,R.id.br_learning,R.id.br_selected,R.id.br_search})
    public void brIntentMoving(View v){
        switch (v.getId()){
            case R.id.tutorial:
                Intent tutorialIntent = new Intent(getApplicationContext(), DwTutorialActivity.class);
                startActivity(tutorialIntent);
                break;
            case R.id.br_learning:
                Intent brLearningIntent = new Intent(getApplicationContext(), BrailleEducationActivity.class);
                startActivity(brLearningIntent);
                break;
            case R.id.br_selected:
                Intent brSelectedIntent = new Intent(getApplicationContext(), BrailleUserActionActivity.class);
                startActivity(brSelectedIntent);
                break;
            case R.id.br_search:
                Intent brSearchIntent = new Intent(getApplicationContext(),BrailleSearchActivity.class);
                startActivity(brSearchIntent);
                break;
        }
    }

    //@OnClick({R.id.ap_learning,R.id.ap_song,R.id.ap_test})
    public void apIntentMoving(View v){
        switch (v.getId()){
            case R.id.ap_learning:
                Intent apLearningIntent = new Intent(getApplicationContext(), AlphabetSwitchingActivity.class);
                startActivity(apLearningIntent);
                break;
            case R.id.ap_song:
                Intent songIntent = new Intent(getApplicationContext(), AlphabetSongActivity.class);
                startActivity(songIntent);
                break;
            case R.id.ap_test:
                Intent testIntent = new Intent(getApplicationContext(), AlphabetTestMultipleActivity.class);
                startActivity(testIntent);
                break;
        }
    }

    //@OnClick({R.id.sc_number,R.id.sc_punc,R.id.sc_sign})
    public void scIntentMoving(View v){
        // punctuation,number,special_sign
        Intent specialLetterIntent = new Intent(getApplicationContext(), SpecialLetterSwitchingActivity.class);
        switch (v.getId()){
            case R.id.sc_number:
                specialLetterIntent.putExtra("type","number");
                break;
            case R.id.sc_punc:
                specialLetterIntent.putExtra("type","punctuation");
                break;
            case R.id.sc_sign:
                specialLetterIntent.putExtra("type","special_sign");
                break;
        }
        startActivity(specialLetterIntent);
    }

}

