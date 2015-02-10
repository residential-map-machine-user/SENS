package laklab.inc.sens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

import java.util.ArrayList;
import java.util.List;


public class ChooseEventActivity extends ActionBarActivity {
    List<String> _eventNameList = new ArrayList<>();
    List<String> _eventDayList = new ArrayList<>();
    List<String> _eventPlaceList = new ArrayList<>();
    List<String> _eventCostList = new ArrayList<>();
    List<String> _eventContentList = new ArrayList<>();
    List<String> _eventAttendanceList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_event);
        final ListView listView = (ListView) findViewById(R.id.choose_event_list);
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
                                            List<GraphObject> feedList = feeds.getGraphObject().getPropertyAsList("data", GraphObject.class);
                                            List<GraphObject> eventList = new ArrayList<>();
                                            for (GraphObject feed : feedList) {
                                                if (feed.getProperty("message") != null
                                                        && feed.getProperty("message").toString().length() > 0) {
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

                                                EventListAdapter adapter = new EventListAdapter(
                                                        getApplicationContext(),
                                                        0,
                                                        _eventNameList);
                                                if (listView != null) {
                                                    listView.setAdapter(adapter);
                                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                            ArrayList<String> item1 = new ArrayList<String>();
                                                            item1.add(_eventNameList.get(position));
                                                            Intent intent = new Intent(ChooseEventActivity.this, MakeTaskActivity.class);
                                                            Log.i("eventInfo", item1.toString());
                                                            intent.putExtra("eventInfo", item1);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                            ).executeAsync();
                        }
                    }
            ).executeAsync();
        }
    }
}