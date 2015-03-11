package laklab.inc.sens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    List<GraphObject> _feedObjectIdList = new ArrayList<>();
    List<String> _taskLimitList = new ArrayList<>();
    List<String> _taskContentList = new ArrayList<>();
    List<String> _commentIdList = new ArrayList<>();
    List<String> _eventIdList = new ArrayList<>();
    Map<String, String> _commentIdMap = new HashMap<>();
    Map<String, String> _eventIdMap = new HashMap<>();
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    /**
     * 0:タスク期限
     */
    public static final int TASKLIMIT = 0;
    /**
     * 1:タスク内容
     */
    public static final int TASKCONTENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task);
        _uiHelper = new UiLifecycleHelper(this, callback);
        _uiHelper.onCreate(savedInstanceState);
        //listViewに関する記述
        final ListView listView = (ListView) findViewById(R.id.list_card);
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
                eachEventInfo.add(_taskLimitList.get(position));
                eachEventInfo.add(_taskContentList.get(position));
                eachEventInfo.add(_commentIdList.get(position));
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
                    "/" + getString(R.string.pageId) + "/feed",
                    null,
                    HttpMethod.GET,
                    new Request.Callback() {
                        @Override
                        public void onCompleted(Response feeds) {
                            List<GraphObject> feedList = feeds.getGraphObject().getPropertyAsList("data", GraphObject.class);
                            for (GraphObject feed : feedList) {
                                if (checkGraphObject(feed, "id")) {
                                    _feedObjectIdList.add(feed);
                                }
                            }
                            List<GraphObject> eventList = new ArrayList<>();
                            for (GraphObject feed : feedList) {
                                if (checkGraphObject(feed, "message")) {
                                    eventList.add(feed);
                                }
                            }
                            // イベントから情報とタスクを取得
                            for (GraphObject event : eventList) {
                                String message = (String) event.getProperty("message");
                                String[] eventInfo = message.split(",");
                                // イベント名
                                if (eventInfo.length > 0 && eventInfo[0] != null) {
                                    _eventNameList.add(eventInfo[0]);
                                    String objectId = (String) event.getProperty("id");
                                    _eventIdMap.put(eventInfo[0], objectId);
                                    _eventIdList.add(objectId);
                                }
                                // タスク抽出
                                if (event.getProperty("comments") != null) {
                                    List<GraphObject> tasks = GraphObject.Factory.create((JSONObject) event.getProperty("comments")).getPropertyAsList("data", GraphObject.class);
                                    List<GraphObject> taskList = new ArrayList<>();
                                    for (GraphObject task : tasks) {
                                            taskList.add(task);
                                    }
                                    for (GraphObject taskData : taskList) {
                                        String taskMessage = (String) taskData.getProperty("message");
                                        String[] taskInfo = taskMessage.split(",");
                                        // タスク基本情報
                                        storeClassifiedInfo(taskInfo, TASKLIMIT, _taskLimitList);
                                        storeClassifiedInfo(taskInfo, TASKCONTENT, _taskContentList);
                                        // タスクの状態
                                        if ((int) taskData.getProperty("like_count") > 0)
                                            Log.i("チェック", ">>>>担当割当： 割当済");
                                        else
                                            Log.i("チェック", ">>>>担当割当： 未割当");
                                    }
                                }
                            }
                            getCommentId(_eventIdList, session);
                            for(String commentId :_commentIdList){
                                for(String taskContent : _taskContentList) {
                                    _commentIdMap.put(commentId, taskContent);
                                }
                            }
                            EventListAdapter adapter = new EventListAdapter(
                                    getApplicationContext(),
                                    0,
                                    _taskContentList,
                                    _eventIdMap
                            );
                            if (listView != null) {
                                listView.setAdapter(adapter);
                            }
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

    public void getCommentId(List<String> eventIdList, Session session){
        //全てのイベントに対して処理をする
        //TODO ここで順番がめちゃくちゃになっているので対処する
        for(String eachEventId : eventIdList){
            new Request(session,
                    "/" + eachEventId + "/comments",
                    null,
                    HttpMethod.GET,
                    new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            Log.i("チェックコメントID", response.getGraphObject().getPropertyAsList("data", GraphObject.class).toString());
                                List<GraphObject> comments = response.getGraphObject().getPropertyAsList("data", GraphObject.class);
                                for(GraphObject comment :comments) {
                                    _commentIdList.add((String) comment.getProperty("id"));
                                }
                        }
                    }
            ).executeAsync();
        }
    }

    /**
     * Facebookとのセッションの状態が変化したときに呼び出される処理
     * @param state
     * @param session
     * @param exception
     */
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i("SessionOpen", "セッションはオープン");
        }else if (state.isClosed()) {
            Log.i("SessionClose", "セッションはクローズ");
        }
    }

    /**
     *ひとつながりのイベントの情報を情報の種類ごとに分けて保存するためのメソッド
     * @param sequentialEventInfo 一つのイベントに関する全ての情報か入っている
     * @param eventInfoType　イベントの情報の種類　例　イベント名　イベントコスト
     * @param storeEventInfo　イベントの種類ごとに保存しておくためにリスト
     * @return　
     */
    public void storeClassifiedInfo(String[] sequentialEventInfo, int eventInfoType, List<String> storeEventInfo){
        if (sequentialEventInfo.length > 1 && sequentialEventInfo[eventInfoType] != null){
            storeEventInfo.add(sequentialEventInfo[eventInfoType]);
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
                Intent listEventIntent = new Intent(ListTaskActivity.this, ListEventsActivity.class);
                startActivity(listEventIntent);
                break;
            case R.id.task_list:
                Intent listTaskIntent = new Intent(ListTaskActivity.this, ListTaskActivity.class);
                startActivity(listTaskIntent);
                break;
            case R.id.myPage:
                Intent myPage = new Intent(ListTaskActivity.this, MyPageActivity.class);
                startActivity(myPage);
                break;
        }
        return true;
    }
}
