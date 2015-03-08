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


public class ChooseEventActivityForTask extends ActionBarActivity {
    private UiLifecycleHelper _uiHelper;

    //イベントに関する情報を種類別に保持
    List<String> _eventNameList = new ArrayList<>();
    List<String> _eventDayList = new ArrayList<>();
    List<String> _eventPlaceList = new ArrayList<>();
    List<String> _eventCostList = new ArrayList<>();
    List<String> _eventContentList = new ArrayList<>();
    List<String> _eventAttendanceNumList = new ArrayList<>();
    List<GraphObject> _feedObjectIdList = new ArrayList<>();
    //キーはeventName, バリューはeventId
    Map<String, String> _eventIdMap = new HashMap<>();
    List<String> _taskLimitList = new ArrayList<>();
    List<String> _taskContentList = new ArrayList<>();
    //セッションが変更されるたびに呼ばれる
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    /**
     * 0:イベント名
     */
    public static final int EVENTNAME = 0;
    /**
     * 1:イベント日時
     */
    public static final int EVENTDAY = 1;
    /**
     * 2:イベント場所
     */
    public static final int EVENTPLACE = 2;
    /**
     * 3:イベント費用
     */
    public static final int EVENTCOST = 3;
    /**
     * 4:イベント詳細
     */
    public static final int EVENTDETAIL = 4;
    /**
     * 0:タスク期限
     */
    public static final int TASKLIMIT = 0;
    /**
     * 1:タスク内容
     */
    public static final int TASKCONTENT = 1;

    public boolean _taskAssigned = false;
    /**
     * Facebookとのセッションの状態が変化したときに呼び出される処理
     * @param session
     * @param state
     * @param exception
     */
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_events);
        //セッションの状態を最新にするためのインスタンス
        _uiHelper = new UiLifecycleHelper(this, callback);
        _uiHelper.onCreate(savedInstanceState);
        //listViewに関する記述
        ////////////////////////////////////////////////
        //カスタムリストViewはすでにあるItemにたいして置換していくようなものなのでデフォルトの表示を準備しておく
        /////////////////////////////////////////////////
        final ListView listView = (ListView) findViewById(R.id.list_card);
        //TextViewをコードで生成　アクティビティの消滅とともに消滅する
        TextView nothing = new TextView(this);
        nothing.setText(getString(R.string.eventlist_nothing));
        //からの時に表示するテキスト
        listView.setEmptyView(nothing);
        //アイテムがクリックされたときの処理
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView eventLabel = (TextView) view.findViewById(R.id.eventTitle);
                ArrayList<String> eachEventInfo = new ArrayList<>();
                /**
                 * eachEventInfoにはいるデータ一覧
                 * 0:イベントId
                 * 1:イベントネーム
                 * 2:イベント日時
                 * 3:イベンタ開催場所
                 * 4:参加費
                 * 5:イベント内容
                 */
                //マップから値を取得するためのeイベント名
                String eventName = eventLabel.getText().toString();
                String eventId = _eventIdMap.get(eventName);
                eachEventInfo.add(eventId);
                eachEventInfo.add(_eventNameList.get(position));
                eachEventInfo.add(_eventDayList.get(position));
                eachEventInfo.add(_eventPlaceList.get(position));
                eachEventInfo.add(_eventCostList.get(position));
                eachEventInfo.add(_eventContentList.get(position));
                //インテントの生成
                Intent intent = new Intent(ChooseEventActivityForTask.this, MakeTaskActivity.class);
                intent.putExtra("eventInfo", eachEventInfo);
                startActivity(intent);
            }
        });
        final Session session = Session.getActiveSession();
        //イベント情報とタスク情報の取得
        if (session.isOpened()) {
            new Request(session,
                    "/" + getString(R.string.pageId) + "/feed",
                    null,
                    HttpMethod.GET,
                    new Request.Callback() {
                        @Override
                        public void onCompleted(Response feeds) {
                            List<GraphObject> feedList = feeds.getGraphObject().getPropertyAsList("data", GraphObject.class);
                            //イベント情報のstory
                            for (GraphObject feed : feedList) {
                                if (checkGraphObject(feed, "id")) {
                                    _feedObjectIdList.add(feed);
                                    Log.d("チェックobjectId", _feedObjectIdList.toString());
                                }
                            }
                            //投稿欄のデータ
                            List<GraphObject> eventList = new ArrayList<>();
                            for (GraphObject feed : feedList) {
                                if (checkGraphObject(feed, "message")) {
                                    eventList.add(feed);
                                }
                            }
                            // イベントから情報とタスクを取得
                            for (GraphObject event : eventList) {
                                //メッセージプロパティを取得
                                String message = (String) event.getProperty("message");
                                //csvをパースする
                                String[] eventInfo = message.split(",");
                                //イベントの名前をキーにしてイベントのIDを取得する
                                if (eventInfo.length > 0 && eventInfo[0] != null) {
                                    String objectId = (String) event.getProperty("id");
                                    Log.d("チェックId", objectId.toString());
                                    _eventIdMap.put(eventInfo[0], objectId);
                                }
                                // イベントの基本情報
                                storeClassifiedInfo(eventInfo, EVENTNAME, _eventNameList);
                                storeClassifiedInfo(eventInfo, EVENTDAY, _eventDayList);
                                storeClassifiedInfo(eventInfo, EVENTPLACE, _eventPlaceList);
                                storeClassifiedInfo(eventInfo, EVENTCOST, _eventCostList);
                                storeClassifiedInfo(eventInfo, EVENTDETAIL, _eventContentList);
                                //イベントの参加者数
                                if (event.getProperty("like_count") != null) {
                                    //イベント参加者
                                    _eventAttendanceNumList.add(event.getProperty("likes_count").toString());
                                }
                                // タスク抽出
                                if (event.getProperty("comments") != null) {
                                    //dataプロパティはarray形式
                                    List<GraphObject> tasks = GraphObject.Factory.create(
                                            (JSONObject) event.getProperty("comments")
                                    ).getPropertyAsList("data", GraphObject.class);
                                    //コメントの内容だけを保持するリスト
                                    List<GraphObject> taskList = new ArrayList<>();
                                    for (GraphObject task : tasks) {
                                        if (checkGraphObject(task, "comments")) {
                                            taskList.add(task);
                                        }
                                    }
                                    //csvで保存してあるデータをパース
                                    for (GraphObject taskData : taskList) {
                                        String taskMessage = (String) taskData.getProperty("message");
                                        //csvのパース
                                        String[] taskInfo = taskMessage.split(",");
                                        // タスク基本情報
                                        storeClassifiedInfo(taskInfo, TASKLIMIT, _taskLimitList);
                                        storeClassifiedInfo(taskInfo, TASKCONTENT, _taskContentList);
                                        // 割当済みか
                                        if ((int) taskData.getProperty("like_count") > 0) {
                                            Log.i("チェック", ">>>>担当割当： 割当済");
                                        } else {
                                            Log.i("チェック", ">>>>担当割当： 未割当");
                                        }
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
     *グラフオブジェクトから欲しい情報を取得するためのメソッド
     * @param graph 取得したいpropertyが属するgraphObject
     * @param property 取得したいgraphObjectの特定のproperty
     * @return 内容があったらtrueを返す
     */
    public boolean checkGraphObject(GraphObject graph, String property){
        if(graph.getProperty(property) != null && graph.getProperty(property).toString().length() >0){
            return true;
        } else {
            return false;
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
                Intent listEventIntent = new Intent(ChooseEventActivityForTask.this, ListEventsActivity.class);
                startActivity(listEventIntent);
                break;
            case R.id.task_list:
                Intent listTaskIntent = new Intent(ChooseEventActivityForTask.this, ListTaskActivity.class);
                startActivity(listTaskIntent);
                break;
            case R.id.myPage:
                Intent myPage = new Intent(ChooseEventActivityForTask.this, MyPageActivity.class);
                startActivity(myPage);
                break;
        }
        return true;
    }
}