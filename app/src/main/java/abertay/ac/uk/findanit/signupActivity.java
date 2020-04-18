package abertay.ac.uk.findanit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class signupActivity extends AppCompatActivity {

    EditText firstName,surName,emailtxt,phonenum,psw,confirmpsw;
    boolean passcomp;
    private  Button backbtn,nextbtn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        backbtn = findViewById(R.id.backbtn);
        nextbtn = findViewById(R.id.nextbtn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });



        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), signupRoleActivity.class);

                firstName = (EditText) findViewById(R.id.name);

                surName = (EditText) findViewById(R.id.surname);

                phonenum = (EditText) findViewById(R.id.phonenum);

                emailtxt = (EditText) findViewById(R.id.email);

                psw = (EditText) findViewById(R.id.psw);

                confirmpsw = (EditText) findViewById(R.id.Cpsw);

                if(firstName.length() < 1 || surName.length() <1 || phonenum.length() <1 || emailtxt.length()<1 || psw.length() < 1  || confirmpsw.length() <1 ){
                    Context context = getApplicationContext();
                    CharSequence text = "Please Fill in all the boxes!";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();


                }
                else {

                    String firstname = firstName.getText().toString();
                    intent.putExtra("firstname", firstname);

                    String lastname = surName.getText().toString();
                    intent.putExtra("lastname", lastname);

                    String Pnumber = phonenum.getText().toString();
                    intent.putExtra("Pnumber", Pnumber);

                    String emailadd = emailtxt.getText().toString();
                    intent.putExtra("email", emailadd);

                    String password = psw.getText().toString();
                    intent.putExtra("psw", password);

                    String conpassword = confirmpsw.getText().toString();

                    passcomp = password.equals(conpassword);



                if(!passcomp ){

                    Context context = getApplicationContext();
                    CharSequence text = "Passwords don't match ";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    psw.setText("");
                    confirmpsw.setText("");
                }
                else{

                    if(psw.length()<6){
                        Context context = getApplicationContext();
                        CharSequence text = "Passwords is less than 6 characters long!  ";
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        psw.setText("");
                        confirmpsw.setText("");
                    }

                    else{

                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }
                }

            }
        });
    }

}
