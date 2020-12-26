package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddToDoActivity extends AppCompatActivity {

    private ImageView close;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText title, description;
    private FloatingActionButton ok;
    String tit, desc, id, postState;
    private FirebaseUser user;
    Map<String, Object> doc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_do);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        ok = findViewById(R.id.ok);
        id = getIntent().getStringExtra("id");
        postState = getIntent().getStringExtra("postState");
        title = findViewById(R.id.title);
        description = findViewById(R.id.desc);
        close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(AddToDoActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        if (postState.equals("new")){
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tit = title.getText().toString().trim();
                    desc = description.getText().toString();

                    if (TextUtils.isEmpty(tit)){
                        Toast.makeText(AddToDoActivity.this, "Empty", Toast.LENGTH_SHORT).show();
                    }else {
                        uploadPost(tit, desc);
                    }
                }
            });
        }
        else {
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tit = title.getText().toString().trim();
                    desc = description.getText().toString();

                    if (TextUtils.isEmpty(tit)){
                        Toast.makeText(AddToDoActivity.this, "Empty", Toast.LENGTH_SHORT).show();
                    }else {
                        updatePost(tit, desc);
                    }
                }
            });
            getData();
        }
    }

    private void updatePost(String tit, String desc) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM, YYYY");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String saveCurrentTime = currentTime.format(calendar.getTime());

        String idd = saveCurrentDate + saveCurrentTime + user.getUid();

        Map<String, Object> userdataMap = new HashMap<>();
        userdataMap.put("id", idd);
        userdataMap.put("title", tit);
        userdataMap.put("description", desc);
        userdataMap.put("state", "completed");
        userdataMap.put("date", saveCurrentDate);
        userdataMap.put("time", saveCurrentTime);

        db.collection("Users").document(user.getUid()).collection("Records")
                .document(id).update(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(AddToDoActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Toast.makeText(AddToDoActivity.this, "Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData() {
        db.collection("Users").document(user.getUid()).collection("Records").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    doc = (Map<String, Object>) task.getResult().getData();
                    title.setText(doc.get("title").toString());
                    description.setText(doc.get("description").toString());
                }
            }
        });
    }

    private void uploadPost(String tit, String desc) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM, YYYY");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String saveCurrentTime = currentTime.format(calendar.getTime());

        String idd = saveCurrentDate + saveCurrentTime + user.getUid();

        Map<String, Object> userdataMap = new HashMap<>();
        userdataMap.put("id", idd);
        userdataMap.put("title", tit);
        userdataMap.put("description", desc);
        userdataMap.put("state", "completed");
        userdataMap.put("date", saveCurrentDate);
        userdataMap.put("time", saveCurrentTime);

        db.collection("Users").document(user.getUid()).collection("Records")
                .document(idd).set(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(AddToDoActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Toast.makeText(AddToDoActivity.this, "Posted", Toast.LENGTH_SHORT).show();
            }
        });

    }
}