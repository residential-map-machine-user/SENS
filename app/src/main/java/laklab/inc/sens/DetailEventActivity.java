package laklab.inc.sens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

import java.util.ArrayList;
import java.util.List;


public class DetailEventActivity extends ActionBarActivity implements View.OnClickListener {
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
    private UiLifecycleHelper _uiHelper;
    private String _eventId;
    private Button _attend;
    private Button _unAttend;
    private Session.StatusCallback _statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    public void onSessionStateChange(Session session, SessionState state, Exception exception){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);
        _attend = (Button)findViewById(R.id.button_attend);
        _unAttend = (Button)findViewById(R.id.button_notAttend);
        _attend.setOnClickListener(this);
        _unAttend.setOnClickListener(this);
        //イベントの基本情報を保持しておくリスト
        final TextView eventDay = (TextView) findViewById (R.id.eventDay);
        final TextView eventPlace = (TextView) findViewById(R.id.eventPlace);
        final TextView eventCost = (TextView) findViewById(R.id.eventCost);
        final TextView eventName = (TextView) findViewById(R.id.eventName);
        final TextView eventContent = (TextView) findViewById(R.id.eventContent);
        //listEventsActivityでIntentにセットしたイベント情報を取得する
        try {
            //インテントで送られてきたデータを取得
            Intent intent = getIntent();
            ArrayList<String> eventInfo = intent.getStringArrayListExtra("eventInfo");
            //それぞれのテキストviewにイベント情報をセット
            _eventId = eventInfo.get(0);
            eventName.setText(eventInfo.get(1));
            eventDay.setText(eventInfo.get(2));
            eventPlace.setText(eventInfo.get(3));
            eventCost.setText(eventInfo.get(4));
            eventContent.setText(eventInfo.get(5));
        }catch(Exception dataNotFoundException){
            finish();
        }
        //セッションの状態が変わるたびに呼ばれる
        _uiHelper = new UiLifecycleHelper(this, _statusCallback);
        Session session = Session.getActiveSession();
        //activeなSessionを取得する
        if (session == null) {
            //saveInstanceStateで前回のSessionの状態を取得
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, _statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                //??ここはアクセス権限を取得している
                session.openForRead(new Session.OpenRequest(this));
            }
        }
        // セッションがオープンならログイン
        if (! session.isOpened()) {
            doLogin();
            userJoinedStatus(session);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        _uiHelper.onResume();
    }

    @Override
    public void onStart(){
        super.onStart();
        Session.getActiveSession().addCallback(_statusCallback);
    }

    @Override
    public void onStop(){
        super.onStop();
        _uiHelper.onStop();
        Session.getActiveSession().removeCallback(_statusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        _uiHelper.onActivityResult(requestCode, resultCode, data, null);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_attend) {
            doPost(POSTREQUEST, new Request.Callback(){
                @Override
                public void onCompleted(Response response) {
                    if((boolean)response.getGraphObject().getProperty("success")) {
                        _attend.setBackgroundColor(Color.BLUE);
                        _attend.setTextColor(Color.WHITE);
                        _unAttend.setBackgroundColor(Color.WHITE);
                        _unAttend.setTextColor(Color.BLACK);
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
                        _attend.setBackgroundColor(Color.WHITE);
                        _attend.setTextColor(Color.BLACK);
                        _unAttend.setBackgroundColor(Color.BLUE);
                        _unAttend.setTextColor(Color.WHITE);
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
        new Request(
                session,
                "/" + _eventId + "/likes",
                null,
                method,
                callback
        ).executeAsync();
    }

    private void doLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened()) {
            if (session.isClosed()) {
                session = new Session(this);
                Session.setActiveSession(session);
            }
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

    public void userJoinedStatus(Session session){
        new Request(session, "/" + _eventId  + "/likes", null, HttpMethod.GET, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                SharedPreferences pref = getSharedPreferences("USER_ID", MODE_PRIVATE);
                String userId = pref.getString("userId", "");
                System.out.println(userId);
                List<GraphObject> likesUserList = response.getGraphObject().getPropertyAsList("data", GraphObject.class);
                for(GraphObject likesUser:likesUserList){
                    String likesUserId  =(String)likesUser.getProperty("id");
                    if(likesUser.equals(userId)){
                        _attend.setBackgroundColor(Color.BLUE);
                        _attend.setTextColor(Color.WHITE);
                        _unAttend.setBackgroundColor(Color.WHITE);
                        _unAttend.setTextColor(Color.BLACK);
                    }
                }
            }
        }
        ).executeAsync();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.getItemId();
        switch(item.getItemId()){
            case R.id.event_list:
                Intent listEventIntent = new Intent(DetailEventActivity.this, ListEventsActivity.class);
                startActivity(listEventIntent);
                break;
            case R.id.task_list:
                Intent listTaskIntent = new Intent(DetailEventActivity.this, ListTaskActivity.class);
                startActivity(listTaskIntent);
                break;
            case R.id.myPage:
                Intent myPage = new Intent(DetailEventActivity.this, MyPageActivity.class);
                startActivity(myPage);
                break;
        }
        return true;
    }
}
