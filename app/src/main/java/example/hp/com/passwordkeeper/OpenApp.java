package example.hp.com.passwordkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;

public class OpenApp extends AppCompatActivity {

    FirebaseUser user;
    Firebase myFirebaseRef;
    private EditText accname, website, emailusername, password;
    private Button editbtn, deletebtn;
    String lain;
    public static String seedValue;
    public String decWebsite, decEmailUsername, decPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openapp);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://passwordkeeper-84e19.firebaseio.com/");

        Bundle bundle = getIntent().getExtras();
        lain = bundle.getString("Account");

        accname = (EditText) findViewById(R.id.openapp_accname);
        website = (EditText) findViewById(R.id.openapp_website);
        emailusername = (EditText) findViewById(R.id.openapp_email);
        password = (EditText) findViewById(R.id.openapp_password);

        editbtn = (Button) findViewById(R.id.btn_edit);
        deletebtn = (Button) findViewById(R.id.btn_delete);

        ValueEventListener postListener = new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(user != null ) {
                    ListAcc person = snapshot.child(user.getUid()).child(lain).getValue(ListAcc.class);
                    seedValue = person.getEnckey();
                    try{
                        decWebsite = AESHelper.decrypt(seedValue, person.getWebsite());
                        decEmailUsername = AESHelper.decrypt(seedValue, person.getEmailusername());
                        decPassword = AESHelper.decrypt(seedValue, person.getPassword());

                        accname.setText(person.getAccname());
                        website.setText(decWebsite);
                        emailusername.setText(decEmailUsername);
                        password.setText(decPassword);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        };
        myFirebaseRef.addListenerForSingleValueEvent(postListener);

        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editbtnprocess(lain);
            }
        });

        deletebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                deletebtnprocess();
            }
        });

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWeb(lain);
            }
        });
    }

    public void openWeb(String key){
        Intent intent = new Intent(this, OpenBrowser.class);
        intent.putExtra("key", key);
        startActivity(intent);
    }

    public void editbtnprocess(String text){
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("Account", text);
        startActivity(intent);
    }

    public void deletebtnprocess(){
        myFirebaseRef.child(user.getUid()).child(lain).removeValue();
        Toast.makeText(getApplicationContext(), "Your account has been successfully deleted!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
    }
}