package laklab.inc.sens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListTaskActivity extends ActionBarActivity {

    private UiLifecycleHelper _uiHelper;
    List<String> _eventNameList = new ArrayList<>();
    List<String> _eventDayList = new ArrayList<>();
    List<String> _eventPlaceList = new ArrayList<>();
    List<String> _eventCostList = new ArrayList<>();
    List<String> _eventContentList = new ArrayList<>();
    List<String> _eventAttendanceList = new ArrayList<>();
    List<GraphObject> _feedObjectIdList = new ArrayList<>();
    List<String> _taskNameList = new ArrayList<>();
    List<String> _taskLimitList = new ArrayList<>();
    List<String> _taskContentList = new ArrayList<>();
    Map<String, String> _eventIdMap = new HashMap<>();
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_events);
        _uiHelper = new UiLifecycleHelper(this, callback);
        _uiHelper.onCreate(savedInstanceState);

        //listViewに関する記述
        final ListView listView = (ListView) findViewById(R.id.listview);
        TextView nothing = new TextView(this);
        nothing.setText(getString(R.string.eventlist_nothing));
        listView.setEmptyView(nothing);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView eventLabel = (TextView) view.findViewById(R.id.eventTitle);
                String eventName = eventLabel.getText().toString();
                String eventId = _eventIdMap.get(eventName);
                ArrayList<String> eachEventInfo = new ArrayList<String>();
                eachEventInfo.add(eventId);
                eachEventInfo.add(_eventNameList.get(position));
                eachEventInfo.add(_eventDayList.get(position));
                eachEventInfo.add(_eventPlaceList.get(position));
                eachEventInfo.add(_eventCostList.get(position));
                eachEventInfo.add(_eventContentList.get(position));
                eachEventInfo.add(_taskLimitList.get(position));
                eachEventInfo.add(_taskContentList.get(position));
                Intent intent = new Intent(ListTaskActivity.this, DetailTaskActivity.class);
                Log.i("eventInfo", eachEventInfo.toString());
                intent.putExtra("eventInfo", eachEventInfo);
                startActivity(intent);
            }
        });

        //facebookにrequestを送る処理
        final Session session = Session.getActiveSession();
        if (session.isOpened()) {
            new Request(session,
                    "/" + getString(R.string.pageId),
                    null,
                    HttpMethod.GET,
                    new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            GraphObject graph = response.getGraphObject();
                            int likeCount = (int) graph.getProperty("likes");
                            boolean canPost = (boolean) graph.getProperty("can_post");
                            Log.i("page", "メンバー数：" + likeCount);
                            Log.i("page", "投稿可能：" + canPost);

                            new Request(session,
                                    "/" + getString(R.string.pageId) + "/feed",
                                    null,
                                    HttpMethod.GET,
                                    new Request.Callback() {
                                        @Override
                                        public void onCompleted(Response feeds) {
                                            Log.d("feedsのaslistdata", feeds.getGraphObject().getPropertyAsList("data", GraphObject.class).toString());
                                            List<GraphObject> feedList = feeds.getGraphObject().getPropertyAsList("data", GraphObject.class);
                                            //いいねをポストするためにfeedのobjectIdを取得
                                            for (GraphObject feed : feedList) {
                                                if (checkGraphObject(feed, "id")) {
                                                    _feedObjectIdList.add(feed);
                                                    Log.d("チェックobjectId", _feedObjectIdList.toString());
                                                }
                                            }
                                            Log.d("Response feeds getPropertydata", feeds.toString());
                                            List<GraphObject> eventList = new ArrayList<>();
                                            for (GraphObject feed : feedList) {
                                                if (checkGraphObject(feed, "message")) {
                                                    eventList.add(feed);
                                                }
                                            }
                                            Log.i("RESPONSE", "イベント数：" + eventList.size());
                                            // イベントから情報とタスクを取得
                                            for (GraphObject event : eventList) {
                                                String message = (String) event.getProperty("message");
                                                String[] eventInfo = message.split(",");
                                                // イベント名
                                                Log.i("チェック", "------------------------");
                                                if (eventInfo.length > 0 && eventInfo[0] != null) {
                                                    Log.i("チェック", "イベント名：" + eventInfo[0]);
                                                    _eventNameList.add(eventInfo[0]);
                                                    String objectId = (String) event.getProperty("id");
                                                    Log.d("チェックId", objectId.toString());
                                                    _eventIdMap.put(eventInfo[0], objectId);
                                                }
                                                // イベント日時
                                                if (eventInfo.length > 1 && eventInfo[1] != null) {
                                                    Log.i("チェック", "イベント日時：" + eventInfo[1]);
                                                    _eventDayList.add(eventInfo[1]);
                                                }
                                                // イベント場所
                                                if (eventInfo.length > 1 && eventInfo[2] != null) {
                                                    Log.i("チェック", "イベント場所：" + eventInfo[2]);
                                                    _eventPlaceList.add(eventInfo[2]);
                                                }
                                                // 参加費
                                                if (eventInfo.length > 1 && eventInfo[3] != null) {
                                                    Log.i("チェック", "参加費：" + eventInfo[3]);
                                                    _eventCostList.add(eventInfo[3]);
                                                }
                                                // 内容
                                                if (eventInfo.length > 1 && eventInfo[4] != null) {
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
                                                    List<GraphObject> tasks = GraphObject.Factory.create((JSONObject) event.getProperty("comments")).getPropertyAsList("data", GraphObject.class);
                                                    List<GraphObject> taskList = new ArrayList<>();
                                                    for (GraphObject task : tasks) {
                                                            taskList.add(task);
                                                    }
                                                    Log.i("チェック", "タスク数：" + taskList.size());
                                                    for (GraphObject taskData : taskList) {
                                                        String taskMessage = (String) taskData.getProperty("message");
                                                        String[] taskInfo = taskMessage.split(",");
                                                        // タスク期限
                                                        if (taskInfo.length > 1 && taskInfo[0] != null) {
                                                            Log.i("チェック", ">>>>タスク期限：" + taskInfo[0]);
                                                            _taskLimitList.add(taskInfo[0]);
                                                        }
                                                        // タスク名
                                                        if (taskInfo.length > 0 && taskInfo[1] != null) {
                                                            Log.i("チェック", ">>>>タスク内容：" + taskInfo[1]);
                                                            _taskContentList.add(taskInfo[1]);
                                                        }
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
                                                    0,
                                                    _eventNameList,
                                                    _eventIdMap
                                            );
                                            if (listView != null) {
                                                listView.setAdapter(adapter);
                                            }
                                        }
                                    }
                            ).executeAsync();
                            /**
                             * TODO それぞれのobjectIdにたいしてrequestを送る
                             *
                             */

                        }
                    }
            ).executeAsync();
        }
    }

    public boolean checkGraphObject(GraphObject graph, String property){
        if(graph.getProperty(property) != null && graph.getProperty(property).toString().length() >0){
            return true;
        } else {
            return false;
        }
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
