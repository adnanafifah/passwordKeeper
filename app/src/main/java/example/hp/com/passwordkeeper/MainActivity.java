package example.hp.com.passwordkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseUser user;
    Firebase myFirebaseRef;
    private TextView helloUserText;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private LinearLayout layout;
    private CircleImageView userphoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Firebase.setAndroidContext(this);
        setSupportActionBar(toolbar);
        myFirebaseRef = new Firebase("https://passwordkeeper-84e19.firebaseio.com/");

        user = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        helloUserText = (TextView) header.findViewById(R.id.textView);
        userphoto = (CircleImageView) header.findViewById(R.id.imageView);

        layout = (LinearLayout)findViewById(R.id.myLayout);

        ValueEventListener postListener = new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(user != null ) {
                   for (DataSnapshot postSnapshot : snapshot.child(user.getUid()).getChildren()) {
                        ListAcc person = postSnapshot.getValue(ListAcc.class);
                        String string =  person.getAccname();
                        String key = postSnapshot.getKey();
                        listaccounts(string,key);
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        };
        myFirebaseRef.addListenerForSingleValueEvent(postListener);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addnewaccount();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }else{
                    helloUserText.setText(user.getDisplayName());
                    userphoto.setImageURI(user.getPhotoUrl());                }
            }
        };
    }

    public void listaccounts(String string, final String key){
        final TextView accountList = new TextView(this);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        llp.setMargins(0, 10, 0, 0); // llp.setMargins(left, top, right, bottom);

        accountList.setText(string.toUpperCase());
        accountList.setTextSize(20);
        accountList.setBackground(getResources().getDrawable(R.drawable.back));
        accountList.setPadding(10, 10, 10, 10);
        accountList.setLayoutParams(llp);
        accountList.setGravity(Gravity.CENTER);

        accountList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                openapp(key);
            }
        });

        layout.addView(accountList);
    }

    public void openapp(String text){
        Intent intent = new Intent(this, OpenApp.class);
        intent.putExtra("Account", text);
        startActivity(intent);
    }

    public void addnewaccount() {
        startActivity(new Intent(MainActivity.this, NewAccount.class));
    }

    public void accountpage() {
        startActivity(new Intent(MainActivity.this, AccountActivity.class));
    }

    public void aboutpage() {
        startActivity(new Intent(MainActivity.this, AboutActivity.class));
    }

    public void settingpage() {
        startActivity(new Intent(MainActivity.this, SettingActivity.class));
    }

    public void helppage() {
        startActivity(new Intent(MainActivity.this, HelpActivity.class));
    }

    public void signOutButton() {
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_user) {
            Toast.makeText(this,"Home",Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_account) {
            Toast.makeText(this,"Account",Toast.LENGTH_LONG).show();
            accountpage();
        } else if (id == R.id.nav_about) {
            Toast.makeText(this,"About",Toast.LENGTH_LONG).show();
            aboutpage();
        } else if (id == R.id.nav_setting) {
            Toast.makeText(this,"Setting",Toast.LENGTH_LONG).show();
            settingpage();
        } else if (id == R.id.nav_help) {
            Toast.makeText(this,"Help",Toast.LENGTH_LONG).show();
            helppage();
        } else if (id == R.id.nav_logout) {
            Toast.makeText(this,"Logout",Toast.LENGTH_LONG).show();
            signOutButton();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
