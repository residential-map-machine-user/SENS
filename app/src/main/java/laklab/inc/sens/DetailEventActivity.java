package laklab.inc.sens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Session;

import java.util.ArrayList;


public class DetailEventActivity extends ActionBarActivity implements View.OnClickListener {
    /**
     * _attendTokenはイベントに参加するかどうか判定するための変数
     */
    private boolean _attendToken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);
        Button attend = (Button)findViewById(R.id.button_attend);
        attend.setOnClickListener(this);
        final TextView eventDay = (TextView) findViewById (R.id.eventDay);
        final TextView eventPlace = (TextView) findViewById(R.id.eventPlace);
        final TextView eventCost = (TextView) findViewById(R.id.eventCost);
        final TextView eventName = (TextView) findViewById(R.id.eventName);
        final TextView eventContent = (TextView) findViewById(R.id.eventContent);
        final Session session = Session.getActiveSession();
        Intent intent = getIntent();
        ArrayList<String> eventInfo = intent.getStringArrayListExtra("eventInfo");
        Log.i("evetnInfo", eventInfo.get(0));
        eventName.setText(eventInfo.get(0));
        eventDay.setText(eventInfo.get(1));
        eventPlace.setText(eventInfo.get(2));
        eventCost.setText(eventInfo.get(3));
        eventContent.setText(eventInfo.get(4));



//        new Request(session,
//                "/" + getString(R.string.pageId) + "/feed",
//                null,
//                HttpMethod.GET,
//                new Request.Callback(){
//                    @Override
//                    public void onCompleted(Response feeds){
//                        GraphObject feedGraph = feeds.getGraphObject();
//                        List<GraphObject> feedList = feedGraph.getPropertyAsList("data", GraphObject.class);
//                        /**
//                         * TODO ここのコードは冗長なのでループさせるwhileがいいかも
//                         *Log.i("feed",feedList.get(1).getProperty("message").toString());
//                         *Log.i("feed",feedList.toString());
//                         */
//                        String [] eventContent1 = feedList.get(1).getProperty("message").toString().split(",");
//                        String [] eventContent2 = feedList.get(2).getProperty("message").toString().split(",");
//                        String [] eventContent3 = feedList.get(3).getProperty("message").toString().split(",");
//                        eventName.setText(eventContent1[0]);
//                        eventDay.setText(eventContent1[1]);
//                        eventPlace.setText(eventContent1[2]);
//                        eventCost.setText(eventContent1[3]);
//                        eventContent.setText(eventContent1[4]);
//                    }
//                }
//        ).executeAsync();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_detail, menu);
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

    @Override
    public void onClick(View v) {
        if (_attendToken != true){
            _attendToken = true;
        }
        SharedPreferences pref = getSharedPreferences("attendance", MODE_PRIVATE);
        boolean saveState = pref.edit().putBoolean("attendance", _attendToken).commit();
        if (saveState){
            finish();
        }

    }
}
