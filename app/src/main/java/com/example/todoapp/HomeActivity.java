package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private ImageView holderImage, abc;
    private TextView holderName;
    private FirebaseUser user;
    private String photo, name;
    private CardView addTodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        addTodo = findViewById(R.id.addToDo);
        holderImage = findViewById(R.id.HolderImage);
        abc = findViewById(R.id.abc);
        holderName = findViewById(R.id.HolderName);

        user = FirebaseAuth.getInstance().getCurrentUser();
        photo = String.valueOf(user.getPhotoUrl());
        name = user.getDisplayName();
        holderName.setText(name);
        Glide.with(holderImage).load(photo).into(holderImage);

        addTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddToDoActivity.class);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(HomeActivity.this, addTodo, ViewCompat.getTransitionName(abc));
                startActivity(intent, optionsCompat.toBundle());
            }
        });
    }
}