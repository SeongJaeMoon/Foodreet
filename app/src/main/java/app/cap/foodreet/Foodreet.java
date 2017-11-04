package app.cap.foodreet;

import android.app.Application;
import android.content.Context;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

import java.io.FileInputStream;

/**
 * Created by clear on 2017-10-28.
 */

public class Foodreet extends Application {
    private String roleType;
    private static Foodreet self;
    @Override
    public void onCreate() {
        super.onCreate();
        self = this;
        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            FirebaseApp.initializeApp(this);
        }
        KakaoSDK.init(new KakaoAdapter() {
            @Override
            public IApplicationConfig getApplicationConfig() {
                return new IApplicationConfig() {
                    @Override
                    public Context getApplicationContext() {
                        return self;
                    }
                };
            }
        });
    }
    public void setRoleType(String roleType){
        this.roleType = roleType;
    }
    public String getRoleType(){
        return roleType;
    }
}
