package example.hp.com.passwordkeeper;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    FirebaseUser user;
    Firebase myFirebaseRef;
    private EditText accname, website, emailusername, password;
    private Button btn_update;
    String lain;
    public static String seedValue;
    public String decWebsite, decEmailUsername, decPassword;
    public  String encWebsite, encEmailUsername, encPassword;
    public String Accname, Website, EmailUsername, Password;
    private TextInputLayout EditInputLayoutAccname, EditInputLayoutWebsite, EditInputLayoutEmailUsername, EditInputLayoutPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        user = FirebaseAuth.getInstance().getCurrentUser();
        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://passwordkeeper-84e19.firebaseio.com/");

        Bundle bundle = getIntent().getExtras();
        lain = bundle.getString("Account");

        EditInputLayoutAccname = (TextInputLayout) findViewById(R.id.edit_layout_accname);
        EditInputLayoutWebsite = (TextInputLayout) findViewById(R.id.edit_layout_website);
        EditInputLayoutEmailUsername = (TextInputLayout) findViewById(R.id.edit_layout_email);
        EditInputLayoutPassword = (TextInputLayout) findViewById(R.id.edit_layout_password);

        accname = (EditText) findViewById(R.id.edit_accname);
        website = (EditText) findViewById(R.id.edit_website);
        emailusername = (EditText) findViewById(R.id.edit_email);
        password = (EditText) findViewById(R.id.edit_password);

        btn_update = (Button) findViewById(R.id.btn_update);

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

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    private void submitForm() {

        Accname = accname.getText().toString().trim();
        Website = website.getText().toString().trim();
        EmailUsername = emailusername.getText().toString().trim();
        Password = password.getText().toString().trim();

        if(!checkAccname(Accname)) {
            return;
        }
        if(!checkWebsite(Website)) {
            return;
        }
        if(!checkEmailUsername(EmailUsername)) {
            return;
        }
        if(!checkPassword(Password)) {
            return;
        }

        updateAccount(Accname, Website, EmailUsername, Password);

        Toast.makeText(getApplicationContext(), "You have successfully updated!!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Account", lain);
        startActivity(intent);
    }

    private void updateAccount(String accname, String website, String emailusername, String password) {

        try {
            encWebsite = AESHelper.encrypt(seedValue, website);
            encEmailUsername = AESHelper.encrypt(seedValue, emailusername);
            encPassword = AESHelper.encrypt(seedValue, password);

        } catch (Exception e) {
            e.printStackTrace();
        }

        ListAcc acc = new ListAcc(accname, encWebsite, encEmailUsername, encPassword, seedValue);

        Map<String, Object> accValues = acc.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/"+ user.getUid() +"/" + lain, accValues);

        myFirebaseRef.updateChildren(childUpdates);
    }

    private boolean checkAccname(String Accname) {

        if (Accname.isEmpty() || !isAccnameValid(Accname)) {

            EditInputLayoutAccname.setErrorEnabled(true);
            EditInputLayoutAccname.setError(getString(R.string.err_msg_accname));
            accname.setError(getString(R.string.err_msg_required));
            requestFocus(accname);
            return false;
        }
        EditInputLayoutAccname.setErrorEnabled(false);
        return true;
    }

    private boolean checkWebsite(String Website) {

        if (Website.isEmpty() || !isWebsiteValid(Website)) {

            EditInputLayoutWebsite.setErrorEnabled(true);
            EditInputLayoutWebsite.setError(getString(R.string.err_msg_website));
            website.setError(getString(R.string.err_msg_required));
            requestFocus(website);
            return false;
        }
        EditInputLayoutWebsite.setErrorEnabled(false);
        return true;
    }

    private boolean checkEmailUsername(String EmailUsername) {

        if (EmailUsername.isEmpty() || !isEmailValid(EmailUsername)) {

            EditInputLayoutEmailUsername.setErrorEnabled(true);
            EditInputLayoutEmailUsername.setError(getString(R.string.err_msg_emailusername));
            emailusername.setError(getString(R.string.err_msg_required));
            requestFocus(emailusername);
            return false;
        }
        EditInputLayoutEmailUsername.setErrorEnabled(false);
        return true;
    }

    private boolean checkPassword(String Password) {

        if (Password.isEmpty() || !isPasswordValid(Password)) {

            EditInputLayoutPassword.setErrorEnabled(true);
            EditInputLayoutPassword.setError(getString(R.string.err_msg_password));
            password.setError(getString(R.string.err_msg_required));
            requestFocus(password);
            return false;
        }
        EditInputLayoutPassword.setErrorEnabled(false);
        return true;
    }

    private static boolean isAccnameValid(String Accname) {
        return !TextUtils.isEmpty(Accname);
    }

    private static boolean isWebsiteValid(String Website){
        return !TextUtils.isEmpty(Website) && Patterns.WEB_URL.matcher(Website).matches();
    }

    private static boolean isEmailValid(String EmailUsername) {
        return !TextUtils.isEmpty(EmailUsername);
    }

    private static boolean isPasswordValid(String Password){
        return !TextUtils.isEmpty(Password) && (Password.length() >= 6);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
