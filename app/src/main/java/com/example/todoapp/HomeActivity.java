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
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ImageView holderImage, abc;
    private TextView holderName;
    private FirebaseUser user;
    private String photo, name;
    private CardView account;
    RecyclerView.Adapter<TodoVH> adapter;
    RecyclerView list;
    List<QueryDocumentSnapshot> documentSnapshots = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        list = findViewById(R.id.list);
        account = findViewById(R.id.cardImage);
        holderImage = findViewById(R.id.HolderImage);
        abc = findViewById(R.id.abc);
        holderName = findViewById(R.id.HolderName);

        user = FirebaseAuth.getInstance().getCurrentUser();
        photo = String.valueOf(user.getPhotoUrl());
        name = user.getDisplayName();
        holderName.setText(name);
        Glide.with(holderImage).load(photo).into(holderImage);

        abc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddToDoActivity.class);
                intent.putExtra("postState", "new");
                startActivity(intent);
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AccountActivity.class);

                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(account, "share_image");
                pairs[1] = new Pair<View, String>(holderName, "share_name");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this, pairs);
                intent.putExtra("photo", photo);
                intent.putExtra("name", user.getDisplayName());
                startActivity(intent, options.toBundle());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

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
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                documentSnapshots.add(doc);
                            }
                                list.setAdapter(adapter);
                                list.setLayoutManager(new GridLayoutManager(HomeActivity.this, 2));

                        }
                    }
                });

    }

    public class TodoVH extends RecyclerView.ViewHolder{
        TextView title, body;
        public TodoVH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleToDo);
            body = itemView.findViewById(R.id.bodyToDo);
        }
    }
}