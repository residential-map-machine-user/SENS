package laklab.inc.sens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ListEventsActivity extends ActionBarActivity {
    private ListView _eventList;
    private UiLifecycleHelper _uiHelper;
    List<String> _eventNameList = new ArrayList<>();
    List<String> _eventDayList = new ArrayList<>();
    List<String> _eventPlaceList = new ArrayList<>();
    List<String> _eventCostList = new ArrayList<>();
    List<String> _eventContentList = new ArrayList<>();
    List<String> _eventAttendanceList = new ArrayList<>();
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //////////////デフォルトで表示したいViewの作成////////////////
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_events);
        _uiHelper = new UiLifecycleHelper(this, callback);
        _uiHelper.onCreate(savedInstanceState);
        //listViewのdefaultの設定
        final ListView listView = (ListView) findViewById(R.id.listview);
        TextView nothing = new TextView(this);
        nothing.setText(getString(R.string.eventlist_nothing));
        listView.setEmptyView(nothing);
        /////////////////////////////////////////////////////////
        //この時点でlistにAdapterを設定すると同時にAdapterに中身を設定する
        final Session session = Session.getActiveSession();
        if (session.isOpened()) {
            new Request(session,
                "/" + getString(R.string.pageId),
                null,
                HttpMethod.GET,
                new Request.Callback() {
                    @Override
                    public void onCompleted(Response response){
                        GraphObject graph = response.getGraphObject();
                        int likeCount = (int) graph.getProperty("likes");
                        boolean canPost = (boolean) graph.getProperty("can_post");
                        Log.i("page", "メンバー数：" + likeCount);
                        Log.i("page", "投稿可能：" + canPost);
                        new Request(session,
                                "/" + getString(R.string.pageId) + "/feed",
                                null,
                                HttpMethod.GET,
                                new Request.Callback(){
                                    @Override
                                    public void onCompleted(Response feeds){
//                                        GraphObject feedGraph = feeds.getGraphObject();
//                                        List<GraphObject> feedList = feedGraph.getPropertyAsList("data", GraphObject.class);
//                                        /**
//                                         *ここのコードは冗長なのでループさせるwhileがいいかも
//                                         *Log.i("feed",feedList.get(1).getProperty("message").toString());
//                                         *Log.i("feed",feedList.toString());
// */
//                                        部分的にとってこようとするとget(position)でとってintent.putExtraに渡したいが失敗していまう。rootは一つで少しずつ分岐させて取得していくかたちがよい.
//                                        List<String> eventInfo = new ArrayList<>();
//                                        String [] eventContent1 = feedList.get(1).getProperty("message").toString().split(",");
//                                        String [] eventContent2 = feedList.get(2).getProperty("message").toString().split(",");
//                                        String [] eventContent3 = feedList.get(3).getProperty("message").toString().split(",");
//                                        eventInfo.add(eventContent1[0]);
//                                        eventInfo.add(eventContent2[0]);
//                                        eventInfo.add(eventContent3[0]);
//                                        ArrayAdapter<String> eventNameAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_activated_1, eventInfo);
//                                        listView.setAdapter(eventNameAdapter);
//                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                                                            public void onItemClick(AdapterView parent, View view, int position, long id) {
//                                                                                ListView listView = (ListView) parent;
//                                                                                String item = (String) listView.getItemAtPosition(position);
//                                                                                Toast.makeText(ListEventsActivity.this, item, Toast.LENGTH_SHORT).show();
//                                                                                Intent intent = new Intent(ListEventsActivity.this, DetailEventActivity.class);
//                                                                                String selectedContent = eventContent1.get(position);
//                                                                                intent.putExtra("", eventContent1);
//                                                                                startActivity(intent);
//                                                                            }
//                                        });
//                                        if (feeds.getError() != null) {
//                                            Log.i("RESPONSE", feeds.getError().getErrorMessage());
//                                            return;
//                                        }
//                                        List<GraphObject> feedList = feeds.getGraphObject().getPropertyAsList("data", GraphObject.class);
//                                        List<GraphObject> eventList = new ArrayList<>();
//                                        Log.i("feed", feedList.get(1).toString());
//                                        for (GraphObject feed : feedList){
//                                            if (feed.getProperty("member") != null && feed.getProperty("member").toString().length() > 0){
//                                                eventList.add(feed);
//                                            }
//                                        }
//                                        Log.i("RESPONSE", "イベント数：" + eventList.size());
//
//                                        List<String> eventNameList = new ArrayList<>();
//                                        for ( GraphObject event :eventList){
//                                            if (event.getProperty("message") != null){
//                                                String [] eventInfo = event.getProperty("message").toString().split(",");
//                                            }
//                                        } if (feeds.getError() != null) {
//                                            Log.i("RESPONSE", feeds.getError().getErrorMessage());
//                                            return;
//                                        }
//                                        List<GraphObject> feedList = feeds.getGraphObject().getPropertyAsList("data", GraphObject.class);
//                                        List<GraphObject> eventList = new ArrayList<>();
//                                        Log.i("feed", feedList.get(1).toString());
//                                        for (GraphObject feed : feedList){
//                                            if (feed.getProperty("member") != null && feed.getProperty("member").toString().length() > 0){
//                                                eventList.add(feed);
//                                            }
//                                        }
//                                        Log.i("RESPONSE", "イベント数：" + eventList.size());
//
//                                        List<String> eventNameList = new ArrayList<>();
//                                        for ( GraphObject event :eventList){
//                                            if (event.getProperty("message") != null){
//                                                String [] eventInfo = event.getProperty("message").toString().split(",");
//                                            }
//                                        }

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

                                        // イベントから情報とタスクを取得
                                        for (GraphObject event : eventList) {
                                            String message = (String) event.getProperty("message");
                                            String[] eventInfo = message.split(",");
                                            // イベント名
                                            Log.i("チェック", "------------------------");
                                            if (eventInfo.length > 0 && eventInfo[0] != null) {
                                                Log.i("チェック", "イベント名：" + eventInfo[0]);
                                                _eventNameList.add(eventInfo[0]);
                                            }
                                            // イベント日時
                                            if (eventInfo.length > 1 && eventInfo[1] != null){
                                                Log.i("チェック", "イベント日時：" + eventInfo[1]);
                                                _eventDayList.add(eventInfo[1]);
                                            }
                                            // イベント場所
                                            if (eventInfo.length > 1 && eventInfo[2] != null){
                                                Log.i("チェック", "イベント場所：" + eventInfo[2]);
                                                _eventPlaceList.add(eventInfo[2]);
                                            }
                                            // 参加費
                                            if (eventInfo.length > 1 && eventInfo[3] != null){
                                                Log.i("チェック", "参加費：" + eventInfo[3]);
                                                _eventCostList.add(eventInfo[3]);
                                            }
                                            // 内容
                                            if (eventInfo.length > 1 && eventInfo[4] != null){
                                                Log.i("チェック", "イベント内容：" + eventInfo[4]);
                                                _eventContentList.add(eventInfo[4]);
                                            }
                                            // イベント参加者数
                                            if (event.getProperty("like_count") != null) {
                                                Log.i("チェック", "イベント参加者：" + event.getProperty("like_count"));
                                                _eventAttendanceList.add(event.getProperty("likes_count").toString());
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
                                                    String[] taskInfo = taskMessage.split(",");
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
                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                                getApplicationContext(),
                                                android.R.layout.simple_list_item_1,
                                                _eventNameList);
                                        ArrayList<String> eventLists = new ArrayList<>();



                                        if (listView != null) {
                                            listView.setAdapter(adapter);
                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    ArrayList <String> item1 = new ArrayList<String>();
                                                    item1.add(_eventNameList.get(position));
                                                    item1.add(_eventDayList.get(position));
                                                    item1.add(_eventPlaceList.get(position));
                                                    item1.add(_eventCostList.get(position));
                                                    item1.add(_eventContentList.get(position));
                                                    Intent intent = new Intent(ListEventsActivity.this, DetailEventActivity.class);
                                                    Log.i("eventInfo", item1.toString());
                                                    intent.putExtra("eventInfo", item1);
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    }
                                }
                        ).executeAsync();
                    }
                }
            ).executeAsync();

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        _uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        _uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        _uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        _uiHelper.onSaveInstanceState(outState);
    }

    /**
     * Facebookとのセッションの状態が変化したときに呼び出される処理
     * @param session
     * @param state
     * @param exception
     */
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i("SessionOpen", "セッションはオープン");
        }else if (state.isClosed()) {
            Log.i("SessionClose", "セッションはクローズ");
        }
    }

}
