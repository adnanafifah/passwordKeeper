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

public class PasswordSettingActivity extends AppCompatActivity {

    FirebaseUser user;
    private EditText newpassword;
    private Button btnupdatepassword;
    public String newpass;
    private TextInputLayout SettingLayoutPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwordsetting);

        user = FirebaseAuth.getInstance().getCurrentUser();

        SettingLayoutPassword = (TextInputLayout) findViewById(R.id.setting_layout_password);

        newpassword = (EditText) findViewById(R.id.setting_password);

        btnupdatepassword = (Button) findViewById(R.id.btn_settingpassword);

        btnupdatepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitchange();
            }
        });
    }

    public void submitchange(){

        newpass = newpassword.getText().toString().trim();

        if(!checkNewpass(newpass)) {
            return;
        }

        user.updatePassword(newpass)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "User password updated.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean checkNewpass(String newpass) {

        if (newpass.isEmpty() || !isNewpassValid(newpass)) {

            SettingLayoutPassword.setErrorEnabled(true);
            SettingLayoutPassword.setError(getString(R.string.err_msg_password));
            newpassword.setError(getString(R.string.err_msg_required));
            requestFocus(newpassword);
            return false;
        }
        SettingLayoutPassword.setErrorEnabled(false);
        return true;
    }

    private static boolean isNewpassValid(String newpass) {
        return !TextUtils.isEmpty(newpass) && (newpass.length() >= 6);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
