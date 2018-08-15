package fit4u.udacity.com.fit4u;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mEmail_text;
    private EditText mPassword_text;
    private EditText mConfirmPass_text;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Button mRegister_btn = findViewById(R.id.btn_register);
        mEmail_text = findViewById(R.id.txt_email);
        mPassword_text = findViewById(R.id.txt_pass);
        TextView mAlreadyMember_text = findViewById(R.id.txt_alreadymember);
        mConfirmPass_text = findViewById(R.id.txt_confirmpass);
        mProgressDialog = new ProgressDialog(this);
        mFirebaseAuth = FirebaseAuth.getInstance();

        mRegister_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });

        mAlreadyMember_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alreadyMemberStart();
            }
        });
    }

    private void alreadyMemberStart() {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
    }

    private void startRegister() {
        String email = mEmail_text.getText().toString().trim();
        String pass = mPassword_text.getText().toString().trim();
        String confirmpass = mConfirmPass_text.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(confirmpass)) {
            Toast.makeText(RegistrationActivity.this, getResources().getString(R.string.empty), Toast.LENGTH_LONG).show();
        } else {
            if (pass.equals(confirmpass)) {
                mProgressDialog.setMessage(getResources().getString(R.string.registering));
                mProgressDialog.show();
                mFirebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mProgressDialog.hide();
                            Toast.makeText(RegistrationActivity.this, getResources().getString(R.string.RegistrationSuccessful), Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RegistrationActivity.this, MainActivity.class).putExtra("Login", getResources().getString(R.string.google)));
                        } else {
                            mProgressDialog.hide();

                            Toast.makeText(RegistrationActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(RegistrationActivity.this, getResources().getString(R.string.incorrectInputs), Toast.LENGTH_LONG).show();
            }
        }


    }
}
