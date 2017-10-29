package app.cap.foodreet.SignUp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import app.cap.foodreet.Foodreet;
import app.cap.foodreet.MainActivity;
import app.cap.foodreet.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText etSignUpEmail, etSignUpPassword, etPasswordConfirm;
    private Button btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private static final String mOwner = "owner";
    private static final String mUser = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users");

        etSignUpEmail = (EditText) findViewById(R.id.xetSignUpEmail);
        etSignUpPassword = (EditText) findViewById(R.id.xetSignUpPassword);
        etPasswordConfirm = (EditText)findViewById(R.id.xetPasswordConfirm);
        btnSignUp = (Button) findViewById(R.id.xbnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSignUp();
            }
        });
    }
    private void doSignUp() {

        final String email = etSignUpEmail.getText().toString().trim();
        String pass = etSignUpPassword.getText().toString().trim();
        String passConfirm = etPasswordConfirm.getText().toString().trim();
        if (!email.isEmpty() && !pass.isEmpty() && !passConfirm.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            if (pass.compareTo(passConfirm)!=0){
                Toast.makeText(getApplicationContext(),getString(R.string.incorrect_confirm), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }else {
                mFirebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Foodreet foodreet = (Foodreet)getApplicationContext();
                        String user_id = mFirebaseAuth.getCurrentUser().getUid();
                        DatabaseReference userDbRef = mDatabaseRef.child(user_id);
                        if (userDbRef.child("email").equals(email)) {
                            Toast.makeText(getApplicationContext(), getString(R.string.registerd), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        } else {
                            if(foodreet.getRoleType().equals(mUser)&&foodreet!=null) {
                                userDbRef.child("email").setValue(email);
                                userDbRef.child("u_id").setValue(user_id);
                                userDbRef.child("role").setValue(mUser);
                            }else if(foodreet.getRoleType().equals(mOwner)&&foodreet!=null){
                                userDbRef.child("email").setValue(email);
                                userDbRef.child("u_id").setValue(user_id);
                                userDbRef.child("role").setValue(mOwner);
                            }
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        }
    }
}
