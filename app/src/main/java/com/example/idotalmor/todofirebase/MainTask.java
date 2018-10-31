package com.example.idotalmor.todofirebase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableArrayMap;
import androidx.databinding.ObservableList;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.idotalmor.todofirebase.Classes.Intro;
import com.example.idotalmor.todofirebase.Models.Note;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;


public class MainTask extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    Toolbar toolbar;

    ViewPager mViewPager;
    TabLayout tabLayout;
    SectionsPagerAdapter mSectionsPagerAdapter;
    SharedPreferences sharedPreferences;

    public static ObservableArrayMap<String,Note> todolist,donelist;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskfrag);

        firebaseAuth = FirebaseAuth.getInstance();

        currentUser = firebaseAuth.getCurrentUser();

        sharedPreferences = getSharedPreferences("myshared", MODE_PRIVATE);
        String firstTime = sharedPreferences.getString(currentUser.getUid().toString(),null);
        if (firstTime == null){
            //this is first login
            Intent intent = new Intent(MainTask.this,Intro.class);
            startActivity(intent);
        }

        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("To Do");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        makeDrawer();

        mDatabase = FirebaseDatabase.getInstance().getReference().child(currentUser.getUid());

        mViewPager = findViewById(R.id.container);
        tabLayout = findViewById(R.id.main_tab_layout);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);
        getnotes();



    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        //clear stack and create new one with maintask activity as root
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

    }

    public void addNoteIntent(View view){

        Intent intent = new Intent(MainTask.this,AddActivity.class);
        startActivity(intent);

    }

    public void getnotes(){

        todolist = new ObservableArrayMap<String,Note>();
        donelist = new ObservableArrayMap<String,Note>();

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Note child = dataSnapshot.getValue(Note.class);

                if(child.done){
                    donelist.put(child.note_uid,child);

                }else{
                    todolist.put(child.note_uid,child);
                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Note child = dataSnapshot.getValue(Note.class);

                todolist.remove(child.note_uid);
                donelist.put(child.note_uid,child);


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    ToDoTab toDoTab = new ToDoTab();
                    return toDoTab;
                case 1:
                    DoneTab doneTab = new DoneTab();
                    return doneTab;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public void makeDrawer(){

        //init the drawerimageloasder to load images from internet url
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.get().load(uri).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.get().cancelRequest(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.primary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.colorAccent).sizeDp(56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }

        });


        final IProfile profile = new ProfileDrawerItem().withName(currentUser.getDisplayName()).withEmail(currentUser.getEmail()).withIcon(currentUser.getPhotoUrl()).withIdentifier(100);

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true).withHeaderBackground(R.drawable.header_bg)
                .addProfiles(
                        profile,
                        new ProfileSettingDrawerItem().withName("Sign Out").withDescription("Sign out this account").withIcon(R.drawable.signout).withIdentifier(12)


                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1 add a new profile ;)
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == 12) {
                            logout(view);
                        }

                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }
                })
                .build();

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Add a new note").withIcon(R.drawable.plus);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("About the developer").withIcon(R.drawable.android_phone);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName("Show Intro").withIcon(R.drawable.show);

        new DrawerBuilder().withActivity(this).withToolbar(toolbar).withAccountHeader(headerResult)
                .addDrawerItems(item1,item2,item3)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        switch((int) drawerItem.getIdentifier()){
                            case 1:{

                                addNoteIntent(view);

                                break;
                            }
                            case 2:{
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainTask.this);
                                LayoutInflater inflater = MainTask.this.getLayoutInflater();
                                View dialogView = inflater.inflate(R.layout.alert_about, null);
                                dialogBuilder.setView(dialogView);

                                AlertDialog alertDialog = dialogBuilder.create();
                                alertDialog.show();
                            break;}
                            case 3:{
                                Intent intent = new Intent(MainTask.this,Intro.class);
                                startActivity(intent);
                                break;
                            }

                        }
                        view.setSelected(false);
                        return false;
                    }
                })
                .build();


    }

}
