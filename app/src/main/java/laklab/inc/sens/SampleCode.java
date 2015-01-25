package laklab.inc.sens;

/**
* Created by yukimatsuyama on 2015/01/22.
*/


        import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
* これはなになにする画面です。<br/>
*
* @author Kawamura
* @version 0.0.1
* @created 2015/01/08
* @updated 2015/01/10 Kawamura なになに処理を修正しました。
*/
public class SampleCode extends ActionBarActivity implements View.OnClickListener {

    private TextView _test;
    private EditText _nameInput;
    private Button _send;

    private String _pageId = "769246619808467";

    /**
     * リスト一覧ビュー
     */
    private ListView _eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _test = (TextView) findViewById(R.id.eventName);
        _test.setBackgroundColor(Color.BLUE);
        _nameInput = (EditText) findViewById(R.id.eventName);
        String name = _nameInput.getText().toString();

        _send = (Button) findViewById(R.id.send);
        _eventList = (ListView) findViewById(R.id.eventName);
        // クリックイベントリスナーをセット
        _send.setOnClickListener(this);
        // start Facebook Login
//        Session.openActiveSession(this, true, new Session.StatusCallback() {
//
//            // callback when session changes state
//            @Override
//            public void call(final Session session, SessionState state, Exception exception) {
//                if (session.isOpened()) {
//
//                    // make request to the /me API
//                    Request.newMeRequest(session, new Request.GraphUserCallback() {
//
//                        // callback after Graph API response with user object
//                        @Override
//                        public void onCompleted(GraphUser user, Response response) {
//                            if (user != null) {
//                                TextView welcome = (TextView) findViewById(R.id.eventName);
//                                welcome.setText("Hello " + user.getName() + "!");
//                                // ページ読み込み
//                                Request.newGraphPathRequest(session, "/page/" + _pageId, new Request.Callback() {
//                                    @Override
//                                    public void onCompleted(Response response) {
//                                        Log.i("RESPONSE", response.toString());
//                                    }
//                                }).executeAsync();
//                            }
//                        }
//                    }).executeAsync();
//                }
//            }
//        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 送信ボタンのクリックイベント処理をします。
     * タスク名とタスクの内容をユーザーが入力する。
     * 文字列を空白がないことをチェックして、
     * Facebookに保存する。
     *
     * [TODO] FacebookSDK経由でFacebookに保存するように修正する
     *
     * @param v イベント発火したビュー
     * @author Kawamura
     *
     */
    @Override
    public void onClick(View v) {
        if (((Button) v).getText().toString().equals("Send")) {
            //Session.openActiveSession()
            final Session currentSession = Session.getActiveSession();
            if (currentSession == null) {
                Log.i("TODO", "No Active Session!");
                return;
            }
            Request getPageRequest = new Request(
                    currentSession,
                    "/" + _pageId,
                    null,
                    HttpMethod.GET,
                    new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            Log.i("RESPONSE", response.toString());
                            if (response.getError() != null) {
                                Log.i("RESPONSE", response.getError().getErrorMessage());
                                return;
                            }
                            GraphObject graph = response.getGraphObject();
                            // ページ内参加者
                            int likeCount = (int) graph.getProperty("likes");
                            boolean canPost = (boolean) graph.getProperty("can_post");
                            Log.i("チェック", "メンバー数：" + likeCount);
                            Log.i("チェック", "投稿可能か：" + canPost);
                            // 続いてフィードを取得
                            Request getPageFeedRequest = new Request(
                                    currentSession,
                                    "/" + _pageId + "/feed",
                                    null,
                                    HttpMethod.GET,
                                    new Request.Callback() {
                                        @Override
                                        public void onCompleted(Response feeds) {
                                            Log.i("RESPONSE", feeds.toString());
                                            if (feeds.getError() != null) {
                                                Log.i("RESPONSE", feeds.getError().getErrorMessage());
                                                return;
                                            }
                                            List<GraphObject> feedList = feeds.getGraphObject().getPropertyAsList("data", GraphObject.class);
                                            List<GraphObject> eventList = new ArrayList<>();
                                            for (GraphObject feed : feedList) {
                                                if (feed.getProperty("message") != null
                                                        && feed.getProperty("message").toString().length() > 0) {
                                                    eventList.add(feed);
                                                }
                                            }
                                            Log.i("RESPONSE", "イベント数：" + eventList.size());
                                            // イベント名リスト
                                            List<String> eventNameList = new ArrayList<>();
                                            List<String> eventDayList = new ArrayList<>();
                                            List<String> eventPlaceList = new ArrayList<>();
                                            List<String> eventCostList = new ArrayList<>();
                                            List<String> eventContentList = new ArrayList<>();
                                            List<String> eventAttendanceList = new ArrayList<>();
                                            // イベントから情報とタスクを取得
                                            for (GraphObject event : eventList) {
                                                String message = (String) event.getProperty("message");
                                                String[] eventInfo = message.split(" ");
                                                // イベント名
                                                Log.i("チェック", "------------------------");
                                                if (eventInfo.length > 0 && eventInfo[0] != null) {
                                                    Log.i("チェック", "イベント名：" + eventInfo[0]);
                                                    eventNameList.add(eventInfo[0]);
                                                }
                                                // イベント日時
                                                if (eventInfo.length > 1 && eventInfo[1] != null){
                                                    Log.i("チェック", "イベント日時：" + eventInfo[1]);
                                                    eventDayList.add(eventInfo[1]);
                                                }
                                                // イベント場所
                                                if (eventInfo.length > 1 && eventInfo[2] != null){
                                                    Log.i("チェック", "イベント場所：" + eventInfo[2]);
                                                    eventPlaceList.add(eventInfo[2]);
                                                }
                                                // 参加費
                                                if (eventInfo.length > 1 && eventInfo[3] != null){
                                                    Log.i("チェック", "参加費：" + eventInfo[3]);
                                                    eventCostList.add(eventInfo[3]);
                                                }
                                                // 内容
                                                if (eventInfo.length > 1 && eventInfo[4] != null){
                                                    Log.i("チェック", "イベント内容：" + eventInfo[4]);
                                                    eventContentList.add(eventInfo[4]);
                                                }
                                                // イベント参加者数
                                                if (event.getProperty("like_count") != null) {
                                                    Log.i("チェック", "イベント参加者：" + event.getProperty("like_count"));
                                                    eventAttendanceList.add(event.getProperty("likes_count").toString());
                                                }
                                                // タスク抽出
                                                if (event.getProperty("comments") != null) {
                                                    List<GraphObject> tasks = GraphObject.Factory.create(
                                                            (JSONObject) event.getProperty("comments")
                                                    ).getPropertyAsList("data", GraphObject.class);
                                                    List<GraphObject> taskList = new ArrayList<>();
                                                    for (GraphObject task : tasks) {
                                                        if (task.getProperty("message") != null
                                                                && task.getProperty("message").toString().length() > 0) {
                                                            taskList.add(task);
                                                        }
                                                    }
                                                    Log.i("チェック", "タスク数：" + taskList.size());
                                                    for (GraphObject taskData : taskList) {
                                                        String taskMessage = (String) taskData.getProperty("message");
                                                        String[] taskInfo = taskMessage.split(" ");
                                                        // タスク期限
                                                        if (taskInfo.length > 1 && taskInfo[0] != null) {
                                                            Log.i("チェック", ">>>>タスク期限：" + taskInfo[0]);
                                                        }
                                                        // タスク名
                                                        if (taskInfo.length > 0 && taskInfo[1] != null)
                                                            Log.i("チェック", ">>>>タスク内容：" + taskInfo[1]);
                                                        // タスクの状態
                                                        // 割当済みか
                                                        if ((int) taskData.getProperty("like_count") > 0)
                                                            Log.i("チェック", ">>>>担当割当： 割当済");
                                                        else
                                                            Log.i("チェック", ">>>>担当割当： 未割当");
                                                    }
                                                }
                                            }
                                            EventListAdapter adapter = new EventListAdapter(
                                                    getApplicationContext(),
                                                    android.R.layout.simple_list_item_1,
                                                    eventNameList);
                                            if (_eventList != null) {
                                                _eventList.setAdapter(adapter);
                                            }
                                        }
                                    }
                            );
                            getPageFeedRequest.executeAsync();
                        }
                    });
            getPageRequest.executeAsync();
            return;
        }
        System.out.println("クリックされたよ。");
        String inputed = _nameInput.getText().toString();
        String inputed2 = _nameInput.getText().toString();
        _test.setText(inputed);
        // ユーザ入力文字列の空白を消す
        inputed = inputed.replace(" ", "");
        inputed = inputed.substring(0, 19);
        // プリファレンスに一時保存する
        // [TODO] Facebookに保存するように修正してー！
        SharedPreferences pref = getSharedPreferences("tasks", MODE_PRIVATE);
        //pref.edit().putString("TASK_NAME", inputed).putString("TASK_CONTENT", inputed2).commit();
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("TASK_NAME", inputed);
        editor.putString("TASK_CONTENT", inputed2);
        boolean savedStatus = editor.commit();
        if (savedStatus) {
            finish();
        }
    }


    /**************************************************
     * ネットワークアクセス制御関係
     */

    ///////////////////////////////////////////////////
    // ネットワークアクセス制御関係
    ///////////////////////////////////////////////////



}
