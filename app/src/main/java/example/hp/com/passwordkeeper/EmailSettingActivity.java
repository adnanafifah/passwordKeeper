package example.hp.com.passwordkeeper;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EmailSettingActivity extends AppCompatActivity {

    FirebaseUser user;
    private EditText email;
    private Button btnupdateemail;
    public String Email;
    private TextInputLayout SettingLayoutEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailsetting);

        user = FirebaseAuth.getInstance().getCurrentUser();

        SettingLayoutEmail = (TextInputLayout) findViewById(R.id.setting_layout_email);

        email = (EditText) findViewById(R.id.setting_email);

        btnupdateemail = (Button) findViewById(R.id.btn_settingemail);

        if (user != null) {
            Email = user.getEmail();
        }

        email.setText(Email);

        btnupdateemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitchange();
            }
        });
    }

    public void submitchange(){

        Email = email.getText().toString().trim();

        if(!checkEmail(Email)) {
            return;
        }

        user.updateEmail(Email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "You have successfully updated!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean checkEmail(String Email) {

        if (Email.isEmpty() || !isEmailValid(Email)) {

            SettingLayoutEmail.setErrorEnabled(true);
            SettingLayoutEmail.setError(getString(R.string.err_msg_emailusername));
            email.setError(getString(R.string.err_msg_required));
            requestFocus(email);
            return false;
        }
        SettingLayoutEmail.setErrorEnabled(false);
        return true;
    }

    private static boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
