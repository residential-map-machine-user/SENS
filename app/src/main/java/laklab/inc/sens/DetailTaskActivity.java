package laklab.inc.sens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


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
    TextView _taskContent;
    TextView _taskLimit;
    private Session session;
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
        _taskContent = (TextView)findViewById(R.id.taskContentTextView);
        _taskLimit = (TextView)findViewById(R.id.taskDueDateTextView);
        //dataの初期化
        Intent intent = getIntent();
        ArrayList<String> eventInfo = intent.getStringArrayListExtra("eventInfo");
        //それぞれのテキストviewにイベント情報をセット
        _eventId = eventInfo.get(0);
        _taskContent.setText(eventInfo.get(1));
        _taskLimit.setText(eventInfo.get(2));
        _commentId = eventInfo.get(3);
        Log.i("fadgs2", _commentId.toString());
        _uiHelper = new UiLifecycleHelper(this, _statusCallback);
        // Facebook ログイン管理sessionがOpenがどうかの確認
        session = Session.getActiveSession();
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
        SharedPreferences pref = getSharedPreferences("MYTASK", MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        if(v.getId() == R.id.button_assigned){
            edit.putString("TASK_CONTENT",_taskContent.getText().toString()).commit();
            edit.putString("TASK_LIMIT", _taskLimit.getText().toString()).commit();
            _assigned.setBackgroundColor(Color.BLUE);
            _assigned.setTextColor(Color.WHITE);
            _unassigned.setBackgroundColor(Color.WHITE);
            _unassigned.setTextColor(Color.BLACK);
            Date date = new Date();
            Date tlimit = null;
            long currentTime = date.getTime();
            long tlimitlong = 0;
            DateFormat df = new SimpleDateFormat("yyyy/mm/dd");
            try {
                System.out.println(_taskContent.getText().toString());
                tlimit = df.parse(_taskContent.getText().toString().trim());
                tlimitlong = tlimit.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println(tlimit +"");
            System.out.println(currentTime + "");
            long tmp = tlimitlong - currentTime;
            new CountDownTimer(tmp,1000){
                TextView countDownText = (TextView)findViewById(R.id.countDown);
                public void onTick(long mill){
                    countDownText.setText("残り時間" + mill/3600000 + "時間です");
                }
                public void onFinish(){
                    countDownText.setText("時間切れです");
                    Bundle params = new Bundle();
                    params.putString("message", "締め切りを守れませんでした。誰か助けて！");
                    params.putBoolean("is_hidden", true);
                    Session session = Session.getActiveSession();
                    new Request(
                            session,
                            getString(R.string.pageId) + "/feed",
                            params,
                            HttpMethod.POST,
                            new Request.Callback() {
                                public void onCompleted(Response response) {
            /* handle the result */
                                    Log.i("publish", response.toString());
                                }
                            }
                    ).executeAsync();
                }
            }.start();
        }else{
            edit.clear().commit();
            _assigned.setBackgroundColor(Color.WHITE);
            _assigned.setTextColor(Color.BLACK);
            _unassigned.setBackgroundColor(Color.BLUE);
            _unassigned.setTextColor(Color.WHITE);

        }
//        if (v.getId() == R.id.button_assigned) {
//            doPost(POSTREQUEST, new Request.Callback() {
//                @Override
//                public void onCompleted(Response response) {
//                    Log.i("チェック１",response.toString());
//                    if ((boolean) response.getGraphObject().getProperty("success")) {
//                        _assigned.setBackgroundColor(Color.BLUE);
//                        _assigned.setTextColor(Color.WHITE);
//                        _unassigned.setBackgroundColor(Color.WHITE);
//                        _unassigned.setTextColor(Color.BLACK);
//                    } else {
//                        Toast.makeText(getApplicationContext(), "正しく実行されませんでした", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        } else {
//            doPost(DELETEREQUEST, new Request.Callback() {
//                @Override
//                public void onCompleted(Response response) {
//                    if((boolean)response.getGraphObject().getProperty("success")) {
//                        _assigned.setBackgroundColor(Color.WHITE);
//                        _assigned.setTextColor(Color.BLACK);
//                        _unassigned.setBackgroundColor(Color.BLUE);
//                        _unassigned.setTextColor(Color.WHITE);
//                    } else {
//                        Toast.makeText(getApplicationContext(), "正しく実行されませんでした", Toast.LENGTH_LONG).show();
//                    }
//                }
//            });
//        }
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
        System.out.println(_commentId);
        new Request(
                session,
                "/" + _commentId + "/likes",
                null,
                method,
                callback
        ).executeAsync();
    }

    public void onSessionStateChange(Session session, SessionState state, Exception exception){
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // メニューの要素を追加して取得
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.getItemId();
        switch(item.getItemId()){
            case R.id.event_list:
                Intent listEventIntent = new Intent(DetailTaskActivity.this, ListEventsActivity.class);
                startActivity(listEventIntent);
                break;
            case R.id.task_list:
                Intent listTaskIntent = new Intent(DetailTaskActivity.this, ListTaskActivity.class);
                startActivity(listTaskIntent);
                break;
            case R.id.myPage:
                Intent myPage = new Intent(DetailTaskActivity.this, MyPageActivity.class);
                startActivity(myPage);
                break;
        }
        return true;
    }
}
