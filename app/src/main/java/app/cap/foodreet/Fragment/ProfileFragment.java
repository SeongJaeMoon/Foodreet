package app.cap.foodreet.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.w3c.dom.Text;

import app.cap.foodreet.Main2Activity;
import app.cap.foodreet.MainActivity;
import app.cap.foodreet.R;
import app.cap.foodreet.SignIn.SignInActivity;

/**
 * Created by clear on 2017-10-30.
 * 프로필용
 */

public class ProfileFragment extends Fragment{

    private Button btnLogout;
    private TextView textUser;
    private TextView textRole;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    View view;

    public ProfileFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseReference.keepSynced(true);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.profile_fragment, container, false);
        btnLogout = (Button)view.findViewById(R.id.xbtnLogout);
        textUser = (TextView)view.findViewById(R.id.textUser);
        textRole = (TextView)view.findViewById(R.id.textRole);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    UserManagement.requestLogout(new LogoutResponseCallback() {
                        @Override
                        public void onCompleteLogout() {
                            FirebaseAuth.getInstance().signOut();

                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateUI();
                                }
                            });
                        }
                    });
                 }
        });
        try {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            final String user_id =mFirebaseAuth.getCurrentUser().getUid();
            if (firebaseUser != null) {

                if (user_id != null) {
                    textUser.setText(user_id);
                }
               mDatabaseReference.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       if (dataSnapshot.hasChild(user_id)) {
                           String role = dataSnapshot.child("role").getValue(String.class);
                           textRole.setText(role);
                       }
                   }
                   @Override
                   public void onCancelled(DatabaseError databaseError) {
                   }
               });
            } else {
                Toast.makeText(getContext(), getString(R.string.profile_error),Toast.LENGTH_SHORT).show();
            }
        }catch (NullPointerException e){
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return view;
    }
    private void updateUI() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(getContext(), SignInActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            Toast.makeText(getContext(), getString(R.string.default_error), Toast.LENGTH_SHORT).show();
        }
    }

}
