package com.example.idotalmor.todofirebase.Classes;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.idotalmor.todofirebase.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Intro extends AppIntro {

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("myshared", MODE_PRIVATE);
        FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
        sharedPreferences.edit().putString(String.valueOf(currentuser.getUid()),"logedin").apply();


        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle("Welcome!");
        sliderPage1.setDescription("Let's explore this ToDo App!");
        sliderPage1.setImageDrawable(R.drawable.waving_hand);
        sliderPage1.setBgColor(Color.parseColor("#1280ED"));
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("Add Yor Tasks!");
        sliderPage2.setDescription("Add your tasks to keep track of your chores");
        sliderPage2.setImageDrawable(R.drawable.todo_intro);
        sliderPage2.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle("Let's get started!");
        sliderPage3.setDescription("Let's start adding fun chores!");
        sliderPage3.setImageDrawable(R.drawable.get_started);
        sliderPage3.setBgColor(Color.parseColor("#2D415C"));
        addSlide(AppIntroFragment.newInstance(sliderPage3));

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }
}
