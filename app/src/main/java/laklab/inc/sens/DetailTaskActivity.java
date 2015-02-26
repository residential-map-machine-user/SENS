package laklab.inc.sens;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;

import java.util.ArrayList;
import java.util.List;


public class DetailTaskActivity extends ActionBarActivity implements View.OnClickListener {
    /**
     * いいねユーザー取得
     */
    public static final int GETREUEST = 0;
    /**
     * いいね送信
     */
    public static final int POSTREQUEST = 1;
    /**
     * いいね取り消し
     */
    public static final int DELETEREQUEST = 2;
    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            //updateView();
            Log.d("ステータスチェック", "SessionStatusCallback");
            onSessionStateChange(session, state, exception);
        }
    }
    //callbackとは
    private Session.StatusCallback _statusCallback = new SessionStatusCallback();
    private UiLifecycleHelper _uiHelper;
    private String _eventId;
    Button _assigned;
    Button _unassigned;
    String _commentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_task);
        //参加表明のためのボタンをIdより取得してくる
        _assigned = (Button)findViewById(R.id.button_assigned);
        _unassigned = (Button)findViewById(R.id.button_unassigned);
        //参加するボタンのにリスナーをつける
        _assigned.setOnClickListener(this);
        _unassigned.setOnClickListener(this);
        _assigned.setBackgroundColor(Color.WHITE);
        _unassigned.setBackgroundColor(Color.WHITE);
//        int[] layoutInt = {R.id.button_assign, R.id.button_notAttend, R.id.eventDay,
//                R.id.eventPlace, R.id.eventCost, R.id.eventName, R.id.eventContent};
//        final TextView[] textViews = new TextView[layoutInt.length];
//        for(int i = 0; i < layoutInt.length; i ++){
//            textViews[i] = (TextView)findViewById(i);
//        }
        final TextView taskContent = (TextView)findViewById(R.id.taskContentTextView);
        final TextView taskLimit = (TextView)findViewById(R.id.taskDueDateTextView);
//        final TextView eventDay = (TextView) findViewById (R.id.eventDay);
//        final TextView eventPlace = (TextView) findViewById(R.id.eventPlace);
//        final TextView eventCost = (TextView) findViewById(R.id.eventCost);
//        final TextView eventName = (TextView) findViewById(R.id.eventName);
//        final TextView eventContent = (TextView) findViewById(R.id.eventContent);
        //listEventsActivityでIntentにセットしたイベント情報を取得する
        try {
            //dataの初期化
            Intent intent = getIntent();
            ArrayList<String> eventInfo = intent.getStringArrayListExtra("eventInfo");
            //それぞれのテキストviewにイベント情報をセット
            _eventId = eventInfo.get(0);
            taskContent.setText(eventInfo.get(6));
            taskLimit.setText(eventInfo.get(7));
            _commentId = eventInfo.get(8);
//            eventName.setText(eventInfo.get(1));
//            eventDay.setText(eventInfo.get(2));
//            eventPlace.setText(eventInfo.get(3));
//            eventCost.setText(eventInfo.get(4));
//            eventContent.setText(eventInfo.get(5));
        }catch(Exception dataNotFoundException){
            finish();
            Toast.makeText(getApplicationContext(), "イベント情報が取得できませんでした", Toast.LENGTH_SHORT).show();
        }
        _uiHelper = new UiLifecycleHelper(this, _statusCallback);

        // Facebook ログイン管理sessionがOpenがどうかの確認
        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, _statusCallback, savedInstanceState);
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
        Session.getActiveSession().addCallback(_statusCallback);
    }

    @Override
    public void onStop(){
        super.onStop();
        Session.getActiveSession().removeCallback(_statusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("リザルト","onActivityResult");
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        _uiHelper.onActivityResult(requestCode, resultCode, data, null);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_assigned) {
            doPost(POSTREQUEST, new Request.Callback(){
                @Override
                public void onCompleted(Response response) {
                    if((boolean)response.getGraphObject().getProperty("success")) {
                        _assigned.setBackgroundColor(Color.BLUE);
                        _assigned.setTextColor(Color.WHITE);
                        _unassigned.setBackgroundColor(Color.WHITE);
                        _unassigned.setTextColor(Color.BLACK);
                    } else {
                        Toast.makeText(getApplicationContext(), "正しく実行されませんでした", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            doPost(DELETEREQUEST, new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    if((boolean)response.getGraphObject().getProperty("success")) {
                        _assigned.setBackgroundColor(Color.WHITE);
                        _assigned.setTextColor(Color.BLACK);
                        _unassigned.setBackgroundColor(Color.BLUE);
                        _unassigned.setTextColor(Color.WHITE);
                    } else {
                        Toast.makeText(getApplicationContext(), "正しく実行されませんでした", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void showUserCount(){
        doPost(GETREUEST, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                GraphObject likes = response.getGraphObject();
                Log.i("チェックGraph", response.toString());
            }
        });
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
     * facebookに投稿するためのメソッド
     * これがよばれると特定のidのobjectにたいしていいねが送信される
     *HttpMe
     * @param requestType リクエストの種類 0:GET 1:いいね　2:取り消し
     * @param callback callback処理
     */
    public void doPost(int requestType, Request.Callback callback){
        HttpMethod method = null;
        if (callback == null){
            return;
        }
        switch(requestType){
            case 0:
                method = HttpMethod.GET;
                break;
            case 1:
                method = HttpMethod.POST;
                break;
            case 2:
                method = HttpMethod.DELETE;
                break;
            default:
                return;
        }
        Session session = Session.getActiveSession();
        //TODO イベン  IDではなくcommentIdが存在する。データで取得してきたグラフオブジェクトに対してidをさらに取得する
        new Request(
                session,
                "/" + _commentId + "/likes",
                null,
                method,
                callback
        ).executeAsync();
    }


    public void onSessionStateChange(Session session, SessionState state, Exception exception){
        Log.i("実行","実行されてるよ");
        Log.i("セッションチェック", session.getPermissions().toString());
//        if ((exception instanceof FacebookOperationCanceledException ||
//                exception instan   ceof FacebookAuthorizationException)) {
//            Log.w("チェック", "error occured:" + exception.getMessage());
//        } else if (state == SessionState.OPENED_TOKEN_UPDATED) {
//            doPost();
//        }
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
            Session.openActiveSession(this, true, _statusCallback);
        }
    }
}
