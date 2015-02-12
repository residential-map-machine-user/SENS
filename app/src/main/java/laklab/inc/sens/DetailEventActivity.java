package laklab.inc.sens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DetailEventActivity extends ActionBarActivity implements View.OnClickListener {
    /**
     * _attendTokenはイベントに参加するかどうか判定するための変数
     */
    private boolean _attendToken = false;
    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            //updateView();
            Log.d("ステータスチェック", "SessionStatusCallback");
            onSessionStateChange(session, state, exception);
        }
    }
    //callbackとは
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    private UiLifecycleHelper uiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);
        //参加表明のためのボタンをIdより取得してくる
        Button attend = (Button)findViewById(R.id.button_attend);
        //参加するボタンのにリスナーをつける
        attend.setOnClickListener(this);
        final TextView eventDay = (TextView) findViewById (R.id.eventDay);
        final TextView eventPlace = (TextView) findViewById(R.id.eventPlace);
        final TextView eventCost = (TextView) findViewById(R.id.eventCost);
        final TextView eventName = (TextView) findViewById(R.id.eventName);
        final TextView eventContent = (TextView) findViewById(R.id.eventContent);
        //listEventsActivityでIntentにセットしたイベント情報を取得する
        Intent intent = getIntent();
        ArrayList<String> eventInfo = intent.getStringArrayListExtra("eventInfo");
        //それぞれのテキストviewにイベント情報をセット
        eventName.setText(eventInfo.get(0));
        eventDay.setText(eventInfo.get(1));
        eventPlace.setText(eventInfo.get(2));
        eventCost.setText(eventInfo.get(3));
        eventContent.setText(eventInfo.get(4));

//        LikeView likeView = (LikeView)findViewById(R.id.like);
//        likeView.setObjectId("https://www.facebook.com/684530848329994/posts/695663580550054");
        uiHelper = new UiLifecycleHelper(this, statusCallback);

        // Facebook ログイン管理sessionがOpenがどうかの確認
        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                //session.openForPublish(getOpenRequest());
                session.openForRead(new Session.OpenRequest(this));
            }
        }
        // ログイン状態の確認セッションが投稿可能かどうかの確認
        if (! session.isOpened()) {
            doLogin();
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop(){
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("リザルト","onActivityResult");
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, null);
    }
    @Override
    public void onClick(View v) {
        /**
         * TODO　ここでRequestを送るRequestの内容はPostされた投稿に対していいねを送る
         * TODO まずはpage/feed/likesのようなURIをしっかり把握する
         */
        Session session = Session.getActiveSession();
        List <String> permission = Arrays.asList("publish_actions");
        if(checkPermission(permission) == true){
            doPost();
        } else {
            session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, permission));
        }
    }

    /**
     * permissionのチェックをする関数
     * @param permissions チェックするpermissionの種類
     * @return
     */
    public boolean checkPermission(List<String> permissions){
        Session session = Session.getActiveSession();
        if (session != null && session.getPermissions().contains(permissions)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * facebookに投稿するための関数
     * これがよばれるんと特定のidのobjectにたいしていいねが送信される
     *HttpMe
     */
    public void doPost(){
        Session session = Session.getActiveSession();
        new Request(
                session,
                "/684530848329994_687799074669838/likes",
                null,
                HttpMethod.POST,
                new Request.Callback() {
                    public void onCompleted(Response response) {
            /* handle the result */
                        Log.d("チェック", response.toString());
                    }
                }
        ).executeAsync();
    }

    public void onSessionStateChange(Session session, SessionState state, Exception exception){
        Log.i("実行","実行されてるよ");
        Log.i("セッションチェック", session.getPermissions().toString());
        if ((exception instanceof FacebookOperationCanceledException ||
                exception instanceof FacebookAuthorizationException)) {
            Log.w("チェック", "error occured:" + exception.getMessage());
        } else if (state == SessionState.OPENED_TOKEN_UPDATED) {
            doPost();
        }
    }

    private void doLogin() {
        Session session = Session.getActiveSession();
        Log.d("ログイン","doLogin: session state is " + session.getState() + ", isOpend:" + session.isOpened() + ", isClosed:" + session.isClosed());
        if (!session.isOpened()) {
            if (session.isClosed()) {
                session = new Session(this);
                Session.setActiveSession(session);
            }
            //session.openForPublish(getOpenRequest());
            session.openForRead(new Session.OpenRequest(this));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
    }
}
