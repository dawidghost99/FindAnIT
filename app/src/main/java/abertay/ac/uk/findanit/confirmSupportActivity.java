package abertay.ac.uk.findanit;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class confirmSupportActivity extends Activity {
    public static final String TAG = "TAG";
    String firstname,lastname,number,emailtxt,psw, userID;

    Button signupbtn,backbtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    FirebaseDatabase database;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmsupport);
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;

        firstname = bundle.getString("firstname");
        lastname = bundle.getString("lastname");
        number = bundle.getString("Pnumber");
        emailtxt = bundle.getString("email");
        psw = bundle.getString("psw");
        final String fullname = firstname + " " + lastname;

        final TextView name = (TextView) findViewById(R.id.name);
        final TextView pnumber = (TextView) findViewById(R.id.pnumber);
        TextView email = (TextView) findViewById(R.id.email);
        TextView role = (TextView) findViewById(R.id.role);
        name.setText(firstname + " " + lastname);
        pnumber.setText(number);
        email.setText(emailtxt);
        role.setText("I provide I.T. support");


        signupbtn = findViewById(R.id.signupbtn);
        backbtn = findViewById(R.id.backbtn);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                // register the user in firebase
                fAuth.createUserWithEmailAndPassword(emailtxt,psw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            String user_id = fAuth.getCurrentUser().getUid();
                            DatabaseReference myrefname = database.getReference().child("Users").child("Support").child(user_id).child("name").child(fullname);
                            DatabaseReference myrefpnum = database.getReference().child("Users").child("Support").child(user_id).child("pnumber").child(number);
                            myrefname.setValue(true);
                            myrefpnum.setValue(true);


                            userID = fAuth.getCurrentUser().getUid();
                            Toast.makeText(confirmSupportActivity.this, "User Created.", Toast.LENGTH_SHORT).show();

                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for "+ userID);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), supportLoginActivity.class));

                        }else {
                            Toast.makeText(confirmSupportActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }


        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendback = new Intent(getApplicationContext(), signupRoleActivity.class);
                sendback.putExtra("firstname", firstname);
                sendback.putExtra("lastname", lastname);
                sendback.putExtra("Pnumber", number);
                sendback.putExtra("email", emailtxt);
                sendback.putExtra("psw", psw);
                startActivity(sendback);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);

    }


}
