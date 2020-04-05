package abertay.ac.uk.findanit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class signup extends AppCompatActivity {

    EditText Fname,Sname,emailtxt,phonenum,psw,Cpsw;
    boolean passcomp;
    Button Register;
    FirebaseAuth fAuth;


    public String[] arrofstr={};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    public void onBackPressed(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void onNextPressed(View view) {

        Intent intent = new Intent(this, signupend.class);



        Fname = (EditText) findViewById(R.id.name);


        Sname = (EditText) findViewById(R.id.Sname);


        phonenum = (EditText) findViewById(R.id.phonenum);


        emailtxt = (EditText) findViewById(R.id.email);


        psw = (EditText) findViewById(R.id.psw);


        Cpsw = (EditText) findViewById(R.id.Cpsw);

        if(Fname.length() < 1 || Sname.length() <1 || phonenum.length() <1 || emailtxt.length()<1 || psw.length() < 1  || Cpsw.length() <1 ){


            Context context = getApplicationContext();
            CharSequence text = "Please Fill in all the boxes!";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }
        else {

            String firstname = Fname.getText().toString();
            intent.putExtra("firstname", firstname);



            String lastname = Sname.getText().toString();
            intent.putExtra("lastname", lastname);



            String Pnumber = phonenum.getText().toString();
            intent.putExtra("Pnumber", Pnumber);




            String emailadd = emailtxt.getText().toString();
            intent.putExtra("email", emailadd);




            String password = psw.getText().toString();
            intent.putExtra("psw", password);


            String conpassword = Cpsw.getText().toString();

            passcomp = password.equals(conpassword);

        }

        if(!passcomp ){

            Context context = getApplicationContext();
            CharSequence text = "Passwords don't match ";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            psw.setText("");
            Cpsw.setText("");




        }

        else{

            startActivity(intent);

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }
    }
}
