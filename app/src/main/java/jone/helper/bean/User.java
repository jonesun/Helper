package jone.helper.bean;

import android.content.Context;
import android.content.SharedPreferences;

import jone.helper.Constants;

/**
 * Created by jone.sun on 2015/11/20.
 */
public class User {
    private static User mUser;

    private static final String TAG_SP = Constants.TAG_SP_USER;

    private SharedPreferences mSP;

    private String avatar;
    private String nickName;
    private boolean login;
    private String signature;
    private String token;
    private String userId;
    private String phone;

    private User(){
    }

    public void init(Context context){
        mSP = context.getSharedPreferences(TAG_SP, Context.MODE_PRIVATE);
        update();
    }

    public static User getInstance(){
        if (mUser==null) {
            mUser = new User();
        }
        return mUser;
    }
    public void update(){
        login = loadBoolean("login");
        if (login) {
            token = loadString("token");
            avatar = loadString("avatar");
            nickName = loadString("nickName");
            signature = loadString("signature");
            userId = loadString("userId");
            phone = loadString("phone");
        }
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
        save("login",login);
        update();
    }

    public void logout(){
        setLogin(false);
        //clear remaining user data here
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
        save("nickName", nickName);
    }

    private void save(String tag,String data){
        mSP.edit().putString(tag, data).apply();
    }

    private void save(String tag,boolean data){
        mSP.edit().putBoolean(tag, data).apply();
    }

    private String loadString(String tag){
        return loadString(tag,"");
    }

    private String loadString(String tag,String defValue){
        return mSP.getString(tag,defValue);
    }

    private boolean loadBoolean(String tag){
        return loadBoolean(tag, false);
    }

    private boolean loadBoolean(String tag, boolean defValue){
        return mSP.getBoolean(tag, defValue);
    }
}
