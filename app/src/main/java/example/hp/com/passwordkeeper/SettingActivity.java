package example.hp.com.passwordkeeper;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingActivity extends AppCompatActivity {

    private Button btnsettingpassword, btnsettingemail, btndeleteacc;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        user = FirebaseAuth.getInstance().getCurrentUser();

        btnsettingpassword = (Button) findViewById(R.id.btnresetpassword);
        btnsettingemail = (Button) findViewById(R.id.btnresetemail);
        btndeleteacc = (Button) findViewById(R.id.btndeleteacc);

        btnsettingpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, PasswordSettingActivity.class));
            }
        });

        btnsettingemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, EmailSettingActivity.class));
            }
        });

        btndeleteacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteacc();
            }
        });
    }

    public void deleteacc(){
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            Toast.makeText(getApplicationContext(), "Account is deleted!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
