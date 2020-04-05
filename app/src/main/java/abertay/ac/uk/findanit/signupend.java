package abertay.ac.uk.findanit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class signupend extends AppCompatActivity {


    String firstname,lastname,number,email,psw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupend);


        TextView textView = (TextView) findViewById(R.id.textView);

        Intent intent = getIntent();


        //String arrofstrs[] = intent.getStringArrayExtra(signup.);
        Bundle bundle = getIntent().getExtras();
         firstname = bundle.getString("firstname");
         lastname = bundle.getString("lastname");
         number = bundle.getString("Pnumber");
         email = bundle.getString("email");
         psw = bundle.getString("psw");

        textView.setText("Okay " + firstname + " "+ lastname +". Which one are you? ");

    }

    public void onBackPressed(View view) {
        Intent intent = new Intent(this, signup.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    public void open_confirmsupport(View view) {

        Intent intentsendsupport = new Intent(this, confirmsupport.class);
        intentsendsupport.putExtra("firstname", firstname);
        intentsendsupport.putExtra("lastname", lastname);
        intentsendsupport.putExtra("Pnumber", number);
        intentsendsupport.putExtra("email", email);
        intentsendsupport.putExtra("psw", psw);
        startActivity(intentsendsupport);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    public void open_confirmcustomer(View view) {
        Intent intentsendcustomer = new Intent(this, confirmcustomer.class);
        intentsendcustomer.putExtra("firstname", firstname);
        intentsendcustomer.putExtra("lastname", lastname);
        intentsendcustomer.putExtra("Pnumber", number);
        intentsendcustomer.putExtra("email", email);
        intentsendcustomer.putExtra("psw", psw);
        startActivity(intentsendcustomer);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


    }


}
