package com.example.mena.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.realtime.util.StringListReader;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView ;
    private DatabaseReference databaseReference ;

    private FirebaseAuth auth ;
    private DatabaseReference newDB ;
    private FirebaseAuth.AuthStateListener listener ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null )
                {
                    Intent intent = new Intent(MainActivity.this ,SignIn.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
            }
        };

        databaseReference = FirebaseDatabase.getInstance().getReference().child("posts");
        newDB=FirebaseDatabase.getInstance().getReference().child("users");
        newDB.keepSynced(true);
        recyclerView = (RecyclerView)findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        if (auth.getCurrentUser() != null) {

            final String id = auth.getCurrentUser().getUid();
            newDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(id)) {

                        Intent intent = new Intent(MainActivity.this, SetupAccount.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        auth.addAuthStateListener(listener);

        FirebaseRecyclerAdapter<ChallengeModel , PostsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ChallengeModel, PostsViewHolder>(
                ChallengeModel.class,
                R.layout.recycler_item,
                PostsViewHolder.class,
                databaseReference

        ) {
            @Override
            protected void populateViewHolder(final PostsViewHolder viewHolder, final ChallengeModel model, int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setFoot(model.getFootSteps());
                viewHolder.setmyImage(getApplicationContext(),model.getImage());
                viewHolder.setusername(model.getUsername());
                viewHolder.setUserImage(getApplicationContext(),model.getUserpic());
                viewHolder.MyView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, model.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }







    public static class PostsViewHolder extends RecyclerView.ViewHolder{

        View MyView ;
        public PostsViewHolder(View itemView) {
            super(itemView);
            MyView= itemView ;

        }
        public void setTitle  (String title ) {
            TextView item_title = (TextView)MyView.findViewById(R.id.posted_title);
            item_title.setText(title);
        }
        public void setDesc  (String Desc ) {
            TextView item_des = (TextView)MyView.findViewById(R.id.posted_des);
            item_des.setText(Desc);
        }
        public void setFoot  (int Foot ) {
            TextView item_foot = (TextView)MyView.findViewById(R.id.posted_foot);
            item_foot.setText(String.valueOf(Foot));
        }
        public void setmyImage (Context context , String imageUri) {

            ImageView imageView = (ImageView)MyView.findViewById(R.id.posted_im);
            Picasso.with(context).load(imageUri).into(imageView);

        }
        public void setusername (String User)
        {
            TextView item_username = (TextView)MyView.findViewById(R.id.posted_username);
            item_username.setText(User);
        }
        public void setUserImage (Context context , String imageUri) {

            ImageView im = (ImageView)MyView.findViewById(R.id.posted_pic);
            Picasso.with(context).load(imageUri).into(im);

        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            startActivity(new Intent(MainActivity.this,PostActivity.class));
        }
        if (item.getItemId() == R.id.action_logout)
        {

            auth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }
}
