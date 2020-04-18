package abertay.ac.uk.findanit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class signupRoleActivity extends AppCompatActivity {

    private String firstname,lastname,number,email,psw;

    Button backbtn, supportBtn, customerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signuprole);

        TextView textView = (TextView) findViewById(R.id.textView);

        backbtn = findViewById(R.id.backbtn);
        supportBtn = findViewById(R.id.supportbtn);
        customerBtn = findViewById(R.id.customerbtn);

        Bundle bundle = getIntent().getExtras();
        firstname = bundle.getString("firstname");
        lastname = bundle.getString("lastname");
        number = bundle.getString("Pnumber");
        email = bundle.getString("email");
        psw = bundle.getString("psw");

        textView.setText("Okay " + firstname + " "+ lastname +". Which one are you? ");

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), signupActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });


        supportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentsendsupport = new Intent(getApplicationContext(), confirmSupportActivity.class);
                intentsendsupport.putExtra("firstname", firstname);
                intentsendsupport.putExtra("lastname", lastname);
                intentsendsupport.putExtra("Pnumber", number);
                intentsendsupport.putExtra("email", email);
                intentsendsupport.putExtra("psw", psw);
                startActivity(intentsendsupport);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        customerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentsendcustomer = new Intent(getApplicationContext(), confirmCustomerActivity.class);
                intentsendcustomer.putExtra("firstname", firstname);
                intentsendcustomer.putExtra("lastname", lastname);
                intentsendcustomer.putExtra("Pnumber", number);
                intentsendcustomer.putExtra("email", email);
                intentsendcustomer.putExtra("psw", psw);
                startActivity(intentsendcustomer);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });


    }

}
