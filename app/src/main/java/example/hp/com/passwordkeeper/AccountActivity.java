package example.hp.com.passwordkeeper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.FileNotFoundException;

public class AccountActivity extends AppCompatActivity {

    FirebaseUser user;
    private EditText displayname, email;
    private Button btnchange;
    private ImageView accimage;
    public String Displayname, Email;
    public Uri photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        user = FirebaseAuth.getInstance().getCurrentUser();

        displayname = (EditText) findViewById(R.id.acc_displayname);
        email = (EditText) findViewById(R.id.acc_email);
        accimage = (ImageView) findViewById(R.id.acc_image);

        btnchange = (Button) findViewById(R.id.btn_change);

        if (user != null) {
            Displayname = user.getDisplayName();
            Email = user.getEmail();
            photoUrl = user.getPhotoUrl();
        }

        accimage.setImageURI(photoUrl);
        displayname.setText(Displayname);
        email.setText(Email);

        btnchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changepage();
            }
        });
    }

    public void changepage(){
        startActivity(new Intent(this,ChangeActivity.class));
    }
}
