package app.cap.foodreet.SignIn;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.usermgmt.LoginButton;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.auth.Session;
import com.kakao.auth.ISessionCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.auth.AuthType;
import com.kakao.util.helper.log.Logger;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import app.cap.foodreet.Foodreet;
import app.cap.foodreet.Main2Activity;
import app.cap.foodreet.MainActivity;
import app.cap.foodreet.R;
import app.cap.foodreet.SignUp.ForgotAccoutActivity;
import app.cap.foodreet.SignUp.SignUpActivity;

public class SignInActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private static final String TAG = SignInActivity.class.getSimpleName();
    private TextView btnNewAccount, btnForgotAccount;
    private EditText etEmail, etPassword;
    private Button btnLogIn;
    private LoginButton btnKakao;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mDatabaseRef;
    private static final String mOwner = "owner";
    private static final String mUser = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //getAppKeyHash();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseRef.keepSynced(true);
        etEmail = (EditText)findViewById(R.id.xetEmail);
        etPassword = (EditText)findViewById(R.id.xetPassword);
        btnLogIn = (Button)findViewById(R.id.xbtnLogIn);
        btnKakao = (LoginButton)findViewById(R.id.xbtnKakaoSignIn);
        btnNewAccount = (TextView)findViewById(R.id.xbtnNewAccount);
        btnForgotAccount = (TextView)findViewById(R.id.xbtnForgotAccount);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);

        Session.getCurrentSession().addCallback(new KakaoSessionCallback());

        btnLogIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                doLogIn();
            }
        });
        btnNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startActivity(new Intent(SignInActivity.this, SignUpActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
            }
        });
        btnForgotAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, ForgotAccoutActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        });
 }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //로그인 activity를 이용하여
        //sdk에서 필요로 하는 activity를 띄우기 때문에 해당 결과를 세션에도 넘겨줘서 처리할 수 있도록 Session#handleActivityResult를 호출
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateUI() throws NullPointerException{
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        try {
            if (firebaseUser != null) {
                final String user_id = mFirebaseAuth.getCurrentUser().getUid();

                mDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id)) {
                            String role = dataSnapshot.child("role").getValue(String.class);
                            if (role != null && role.equals(mUser)) {
                                startActivity(new Intent(SignInActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                            } else if (role != null && role.equals(mOwner)) {
                                startActivity(new Intent(SignInActivity.this, Main2Activity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.default_error), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }else {
                Foodreet foodreet = (Foodreet)getApplicationContext();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                String pushId = databaseReference.child("users").push().getKey();
                DatabaseReference userDbRef = mDatabaseRef.child(pushId);
                if(foodreet.getRoleType().equals(mUser)&&foodreet.getRoleType()!=null) {
                    userDbRef.child("u_id").setValue(pushId);
                    userDbRef.child("role").setValue(mUser);
                    startActivity(new Intent(SignInActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }else if(foodreet.getRoleType().equals(mOwner)&&foodreet.getRoleType()!=null){
                    userDbRef.child("u_id").setValue(pushId);
                    userDbRef.child("role").setValue(mOwner);
                    startActivity(new Intent(SignInActivity.this, Main2Activity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.default_error), Toast.LENGTH_SHORT).show();
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //--kako Login--
    /**
     *
     * @param kakaoAccessToken 카카오 로그인 성공시 토큰 생성
     * @return Task 객체 파이어베이스 토큰 생성, 카카오 로그인 (정상적으로 이루어질 때)
     */
    private Task<String> getFirebaseJwt(final String kakaoAccessToken) {
        final TaskCompletionSource<String> source = new TaskCompletionSource<>();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.validation_server_domain) + "/verifyToken";
        HashMap<String, String> validationObject = new HashMap<>();
        validationObject.put("token", kakaoAccessToken);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(validationObject), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String firebaseToken = response.getString("firebase_token");
                    source.setResult(firebaseToken);
                } catch (Exception e) {
                    source.setException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                source.setException(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", kakaoAccessToken);
                return params;
            }
        };

        queue.add(request);
        return source.getTask();
    }

    /**
     * 카카오 로그인. OnSessionOpened() -> 로그인 성공 후에 콜백
     */
    private class KakaoSessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            Toast.makeText(getApplicationContext(), getString(R.string.kakao_login), Toast.LENGTH_LONG).show();
            String accessToken = Session.getCurrentSession().getAccessToken();
            getFirebaseJwt(accessToken).continueWithTask(new Continuation<String, Task<AuthResult>>() {
                @Override
                public Task<AuthResult> then(@NonNull Task<String> task) throws Exception {
                    String firebaseToken = task.getResult();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    return auth.signInWithCustomToken(firebaseToken);
                }
            }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        updateUI();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.login_error), Toast.LENGTH_LONG).show();
                        if (task.getException() != null) {
                            Log.e(TAG, task.getException().toString());
                        }
                    }
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Log.e(TAG, exception.toString());
            }
        }
    }

    //--Email Login--
    //로그인 버튼 클릭
    private void doLogIn() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!email.isEmpty() && !password.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        checkUserExists();
                    }
                    else{
                        Toast.makeText(SignInActivity.this, getString(R.string.incorrect_id), Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
    //이메일로 로그인시 로그인 버튼 클릭하면, 유저가 존재하는지 확인
    private void checkUserExists() throws NullPointerException{
        try {
            final String user_id = mFirebaseAuth.getCurrentUser().getUid();

            mDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user_id)) {
                        progressBar.setVisibility(View.GONE);
                        String role = dataSnapshot.child("role").getValue(String.class);
                        if (role != null && role.equals(mUser)) {
                            startActivity(new Intent(SignInActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else if (role != null && role.equals(mOwner)) {
                            startActivity(new Intent(SignInActivity.this, Main2Activity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.default_error), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignInActivity.this, getString(R.string.incorrect_et), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("Hash key:", "*****"+something+"*****");
            }
        } catch (Exception e){
            Log.e("name not found", e.toString());
        }
    }
}
