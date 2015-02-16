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


public class ChooseEventActivityForTask extends ActionBarActivity {
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
        final ArrayList<List> eachEventInfo = new ArrayList<List>();
        eachEventInfo.add(_eventNameList);
        eachEventInfo.add(_eventDayList);
        eachEventInfo.add(_eventPlaceList);
        eachEventInfo.add(_eventCostList);
        eachEventInfo.add(_eventContentList);
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
                                            //GraphObjectにある複数の"data"の値を取得してListで保存
                                            List<GraphObject> feedList = feeds.getGraphObject().getPropertyAsList("data", GraphObject.class);
                                            //"message"単位でGraphObjectを保存するListを作成
                                            List<GraphObject> eventList = new ArrayList<>();
                                            for (GraphObject feed : feedList) {
                                                if (checkGraphObjectContent(feed, "message")) {
                                                    eventList.add(feed);
                                                }
                                            }
                                            Log.i("RESPONSE", "イベント数：" + eventList.size());
                                            // イベントから情報とタスクを取得
                                            for (GraphObject event : eventList) {
                                                String eventId = (String) event.getProperty("id");

                                                String message = (String) event.getProperty("message");
                                                /**
                                                 *eventInfoには一つのイベントの詳細な情報全て保存されている
                                                 * 0 eventId
                                                 * 1 eventName
                                                 * 2 eventDay
                                                 * 3 eventPlace
                                                 * 4 eventCost
                                                 * 5 eventContent
                                                 */
                                                String[] eventInfo = new String[6];
                                                eventInfo[0] = eventId;
                                                eventInfo = message.split(",");
                                                //ここでイベントの情報を情報の種類ごとに保存している
                                                for(int i = 0; i < eventInfo.length; i ++){
                                                    storeDetailEventInfoToList(i, eventInfo, eachEventInfo);
                                                }
                                                // イベント参加者数
                                                Log.i("チェック", "イベント参加者：" + event.getProperty("like_count"));
                                                _eventAttendanceList.add(event.getProperty("likes_count").toString());
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
                                                            Intent intent = new Intent(ChooseEventActivityForTask.this, MakeTaskActivity.class);
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

    /**
     * このメソッドはsplitで分解されたデータをeventの持つ情報の種類群でまとめて保存する
     * ex eventNameList()には登録されていてるevent名が全て保存されている
     * @param evenInfoType 0:イベント名 1:イベント日時　2:イベント場所　3:イベント参加費　4:イベント内容;
     * @param storedEventInf イベントの詳細情報が入っている渡すためのデータ
     * @param eachEventList イベントを保存しておくためのList
     */
    public void storeDetailEventInfoToList(int evenInfoType, String[] storedEventInf, ArrayList<List> eachEventList){
        Log.i("チェック", "------------------------");
        if (storedEventInf.length > evenInfoType && storedEventInf[evenInfoType] != null) {
            Log.i("チェック", storedEventInf[evenInfoType]);
            eachEventList.get(evenInfoType).add(storedEventInf[evenInfoType]);
        }
    }

    /**
     * 取得するGraphObjectの中身があるかチェックするメソッド
     * @param graph 取得したいプロパティが属するgraphObject
     * @param property GraphObjectの中から取得したいproperty
     */
    public boolean checkGraphObjectContent(GraphObject graph,String property){
        if(graph.getProperty(property) != null && graph.getProperty(property).toString().length() > 0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * ここではobjectIdで指定したGraphObjectがもつlikesの中身を取得する。いいねを押したUserのnameが表示される
     */

//    public void getLikesUser(Session session,){
//        new Request(session,)
//    }


}