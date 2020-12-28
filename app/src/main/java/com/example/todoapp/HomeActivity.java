package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    private ImageView holderImage, abc, secondImg;
    private TextView holderName, rName, rEmail, timeVar, count;
    private LinearLayout logout,temp;
    private FirebaseUser user;
    private String photo, name;
    private CardView account;
    RecyclerView.Adapter<TodoVH> adapter;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    RecyclerView list;
    List<QueryDocumentSnapshot> documentSnapshots = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        timeVar = findViewById(R.id.timeVar);

        DateFormat df = new SimpleDateFormat("a");
        String date = df.format(Calendar.getInstance().getTime());

        if (date.equals("PM")){
            timeVar.setText("Good Evening");
        }else {
            timeVar.setText("Good Morning");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        count = findViewById(R.id.count);
        temp = findViewById(R.id.temp);
        list = findViewById(R.id.list);
        account = findViewById(R.id.cardImage);
        holderImage = findViewById(R.id.HolderImage);
        abc = findViewById(R.id.abc);
        holderName = findViewById(R.id.HolderName);
        logout = findViewById(R.id.logOut);
        rEmail = findViewById(R.id.emailId);
        secondImg = findViewById(R.id.rImage);
        rName = findViewById(R.id.rName);
        user = FirebaseAuth.getInstance().getCurrentUser();
        photo = String.valueOf(user.getPhotoUrl());
        name = user.getDisplayName();
        mAuth = FirebaseAuth.getInstance();
        holderName.setText(name);
        Glide.with(holderImage).load(photo).into(holderImage);
        Glide.with(secondImg).load(photo).into(secondImg);
        rName.setText(user.getDisplayName());
        rEmail.setText(user.getEmail());



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });
        mAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    Intent intent = new Intent(HomeActivity.this, AuthActivity.class);
                    startActivity(intent);
                }
            }
        };

        abc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddToDoActivity.class);
                intent.putExtra("postState", "new");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        adapter = new RecyclerView.Adapter<TodoVH>() {
            @NonNull
            @Override
            public TodoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new TodoVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.to_do_layout, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull TodoVH holder, final int position) {
                String a = String.valueOf(documentSnapshots.get(position).getData().get("title"));
                String b = String.valueOf(documentSnapshots.get(position).getData().get("description"));
                String id = String.valueOf(documentSnapshots.get(position).getData().get("id"));

                holder.base.setBackgroundColor(new Random().nextInt());

                holder.title.setText(a.substring(0, Math.min(a.length(), 20)));
                holder.body.setText(b);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeActivity.this, AddToDoActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("postState", "old");
                        startActivity(intent);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return documentSnapshots.size();
            }
        };


        db.collection("Users").document(user.getUid()).collection("Records")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()){
                            int size = task.getResult().size();
                            count.setText(""+size);
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                documentSnapshots.add(doc);
                            }
                            if (size>0){
                                temp.setVisibility(View.GONE);
                                list.setAdapter(adapter);
                                list.setLayoutManager(new GridLayoutManager(HomeActivity.this, 2));
                            }else {
                                temp.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }

    public class TodoVH extends RecyclerView.ViewHolder{
        TextView title, body;
        LinearLayout base;
        public TodoVH(@NonNull View itemView) {
            super(itemView);
            base = itemView.findViewById(R.id.base);
            title = itemView.findViewById(R.id.titleToDo);
            body = itemView.findViewById(R.id.bodyToDo);
        }
    }
}