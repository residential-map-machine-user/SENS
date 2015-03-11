package laklab.inc.sens;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import java.util.Arrays;

public class LoginActivity extends Activity {

    private final static String TAG = "LoginActivity";
    //facebookログインのダイアログの管理を自動で行う
    /////////////////////////////////////////////////////
    //どの状態から呼ばれても大丈夫なように全てのライフサイクルメソッドに実装する
    //??
    ////////////////////////////////////////////////////
    private UiLifecycleHelper uiHelper;
    //セッションが変更されたことを通知する
    ////////////////////////////////////////////////////
    //StatusCallbackクラスはSessionの状態を保存する
    /////////////////////////////////////////////////
    private Session.StatusCallback callback = new Session.StatusCallback() {
        //セッションの状態が変更されたときに必ず呼ばれる
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            //sessionの状態が変わったときに毎回呼びたいメソッド
            onSessionStateChange(session, state, exception); 
        }
    };

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //レイアウトで作成したfacebookボタンを取得
        LoginButton authButton = (LoginButton) findViewById(R.id.authButton);
        //登校許可のパーミッションを与える
        authButton.setPublishPermissions(Arrays.asList("publish_actions"));
        //インスタンスを生成
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
    }

    /**
     * 画面がforGroundに来るときにかならず呼ばれるメソッド
     */
    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        Session session = Session.getActiveSession();
        //セッションの状態によって遷移させてやる
        if(session.isOpened()){
            new Request(session,
                    "/me",
                    null,
                    HttpMethod.GET,
                    new Request.Callback(){
                        @Override
                        public void onCompleted(Response userInfo){
                            String userId = (String)userInfo.getGraphObject().getProperty("id");
                            String userName = (String)userInfo.getGraphObject().getProperty("name");
                            SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("userId", userId);
                            editor.putString("userName", userName);
                            editor.commit();
                            System.out.println(userId);
                            System.out.println(userName);
                    }
                }).executeAsync();
            Intent showTop = new Intent(LoginActivity.this, TopActivity.class);
            startActivity(showTop);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
}

