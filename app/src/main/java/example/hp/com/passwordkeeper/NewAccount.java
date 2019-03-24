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
import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NewAccount extends AppCompatActivity {

    FirebaseUser user;
    Firebase myFirebaseRef;
    private Button addnew;
    private EditText accname, website, emailusername, password;
    private TextInputLayout newaccInputLayoutAccname, newaccInputLayoutWebsite, newaccInputLayoutEmailUsername, newaccInputLayoutPassword;
    public static String seedValue;
    public  String encWebsite, encEmailUsername, encPassword;
    public String Accname, Website, EmailUsername, Password;
    private SecureRandom random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://passwordkeeper-84e19.firebaseio.com/");

        random = new SecureRandom();
        seedValue = new BigInteger(130, random).toString(32);

        newaccInputLayoutAccname = (TextInputLayout) findViewById(R.id.new_acc_layout_accname);
        newaccInputLayoutWebsite = (TextInputLayout) findViewById(R.id.new_acc_layout_website);
        newaccInputLayoutEmailUsername = (TextInputLayout) findViewById(R.id.new_acc_layout_emailusername);
        newaccInputLayoutPassword = (TextInputLayout) findViewById(R.id.new_acc_layout_password);

        accname = (EditText) findViewById(R.id.new_acc_accname);
        website = (EditText) findViewById(R.id.new_acc_website);
        emailusername = (EditText) findViewById(R.id.new_acc_emailusername);
        password = (EditText) findViewById(R.id.new_acc_password);
        addnew = (Button) findViewById(R.id.btn_addnew);

        addnew.setOnClickListener(new View.OnClickListener() {
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

        newaccInputLayoutAccname.setErrorEnabled(false);
        newaccInputLayoutWebsite.setErrorEnabled(false);
        newaccInputLayoutEmailUsername.setErrorEnabled(false);
        newaccInputLayoutPassword.setErrorEnabled(false);

        try {
            encWebsite = AESHelper.encrypt(seedValue, Website);
            encEmailUsername = AESHelper.encrypt(seedValue, EmailUsername);
            encPassword = AESHelper.encrypt(seedValue, Password);

        } catch (Exception e) {
            e.printStackTrace();
        }

        ListAcc listAcc = new ListAcc();

        listAcc.setAccname(Accname);
        listAcc.setWebsite(encWebsite);
        listAcc.setEmailusername(encEmailUsername);
        listAcc.setPassword(encPassword);
        listAcc.setEnckey(seedValue);


        myFirebaseRef.child(user.getUid()).push().setValue(listAcc);

        Toast.makeText(getApplicationContext(), "You are successfully Registered !!", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(NewAccount.this, MainActivity.class));

    }

    private boolean checkAccname(String Accname) {

        if (Accname.isEmpty() || !isAccnameValid(Accname)) {

            newaccInputLayoutAccname.setErrorEnabled(true);
            newaccInputLayoutAccname.setError(getString(R.string.err_msg_accname));
            accname.setError(getString(R.string.err_msg_required));
            requestFocus(accname);
            return false;
        }
        newaccInputLayoutAccname.setErrorEnabled(false);
        return true;
    }

    private boolean checkWebsite(String Website) {

        if (Website.isEmpty() || !isWebsiteValid(Website)) {

            newaccInputLayoutWebsite.setErrorEnabled(true);
            newaccInputLayoutWebsite.setError(getString(R.string.err_msg_website));
            website.setError(getString(R.string.err_msg_required));
            requestFocus(website);
            return false;
        }
        newaccInputLayoutWebsite.setErrorEnabled(false);
        return true;
    }

    private boolean checkEmailUsername(String EmailUsername) {

        if (EmailUsername.isEmpty() || !isEmailValid(EmailUsername)) {

            newaccInputLayoutEmailUsername.setErrorEnabled(true);
            newaccInputLayoutEmailUsername.setError(getString(R.string.err_msg_emailusername));
            emailusername.setError(getString(R.string.err_msg_required));
            requestFocus(emailusername);
            return false;
        }
        newaccInputLayoutEmailUsername.setErrorEnabled(false);
        return true;
    }

    private boolean checkPassword(String Password) {

        if (Password.isEmpty() || !isPasswordValid(Password)) {

            newaccInputLayoutPassword.setErrorEnabled(true);
            newaccInputLayoutPassword.setError(getString(R.string.err_msg_password));
            password.setError(getString(R.string.err_msg_required));
            requestFocus(password);
            return false;
        }
        newaccInputLayoutPassword.setErrorEnabled(false);
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


