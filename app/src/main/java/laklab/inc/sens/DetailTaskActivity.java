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
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    //Qここではimplementとして新しいクラスを定義しているけど機能の拡張がないということはその必要があるのか？

    //callbackとは
    private Session.StatusCallback _statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {

        }
    };
    private UiLifecycleHelper _uiHelper;
    private String _eventId;
    private Button _assigned;
    private Button _unassigned;
    private String _commentId;
    private TextView _taskContent;
    private TextView _taskLimit;
    private Session session;
    public void onSessionStateChange(Session session, SessionState state, Exception exception){
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_task);
        _uiHelper = new UiLifecycleHelper(this, _statusCallback);
        //ボタンをIdで取得
        _assigned = (Button)findViewById(R.id.button_assigned);
        _unassigned = (Button)findViewById(R.id.button_unassigned);
        //リスナーのセット
        _assigned.setOnClickListener(this);
        _unassigned.setOnClickListener(this);
        //テキストビューをIdで取得
        _taskContent = (TextView)findViewById(R.id.taskContentTextView);
        _taskLimit = (TextView)findViewById(R.id.taskDueDateTextView);
        //イベントに関する種類別にリストされた情報を取得
        Intent intent = getIntent();
        ArrayList<String> eventInfo = intent.getStringArrayListExtra("eventInfo");
        //イベント情報の取得
        _eventId = eventInfo.get(0);
        _taskContent.setText(eventInfo.get(1));
        _taskLimit.setText(eventInfo.get(2));
        _commentId = eventInfo.get(3);
        //オープンなセッションを作成
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
        //ログインされたない場合はログイン
        if (! session.isOpened()) {
            doLogin();
        }
        userJoinedStatus(session);
        countDownTask(_taskContent.getText().toString());
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
        _uiHelper.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        _uiHelper.onActivityResult(requestCode, resultCode, data, null);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_assigned) {
            doPost(POSTREQUEST, new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    Log.i("チェック１", response.toString());
                    if ((boolean) response.getGraphObject().getProperty("success")) {
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
                    if ((boolean) response.getGraphObject().getProperty("success")) {
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

    public void userJoinedStatus(Session session){
        new Request(session, "/" + _commentId  + "/likes", null, HttpMethod.GET, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                //ログインしているユーザーのIdの取得
                SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                String userId = pref.getString("userId", "ユーザーIDが取得できませんでした");
                System.out.println(userId);
                //likeしているユーザーすべてを取得
                List<GraphObject> likesUserList = response.getGraphObject().getPropertyAsList("data", GraphObject.class);
                for(GraphObject likesUser:likesUserList){
                    String likesUserId  =(String)likesUser.getProperty("id");
                    if(likesUserId.equals(userId)){
                        _assigned.setBackgroundColor(Color.BLUE);
                        _assigned.setTextColor(Color.WHITE);
                        _unassigned.setBackgroundColor(Color.WHITE);
                        _unassigned.setTextColor(Color.BLACK);
                    }
                }
            }
        }
        ).executeAsync();
    }

    public void countDownTask(String taskContent){
        //タスク期限までの時間を取得

        Date taskLimit = null;
        long taskLimitSecond = 0;
        Date currentDate = new Date(System.currentTimeMillis());
        long currentTime = currentDate.getTime();
        //日時のformatを作る
        DateFormat df = new SimpleDateFormat("yyyy/mm/dd");
        try {
            //文字列かたDate型に変換
            taskLimit = df.parse(taskContent);
            //long型で時間を取得
            taskLimitSecond = taskLimit.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long tmp = taskLimitSecond - currentTime;
        //一秒ごとに更新
        new CountDownTimer(tmp,60000){
            TextView countDownText = (TextView)findViewById(R.id.countDown);
            //viewにセットするためのメソッド
            public void onTick(long mill){
                countDownText.setText("残り時間" + mill/3600000 + "時間です");
            }
            //期限までの時間がゼロになった場合
            public void onFinish(){
                countDownText.setText("時間切れです");
            }
        }.start();
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
