package laklab.inc.sens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;


public class MakeEventActivity extends ActionBarActivity implements View.OnClickListener {

    private String _eventName;
    private String _eventDay;
    private String _eventPlace;
    private String _eventContent;
    private String _eventCost;
    private String _inputtedEventInfo= "";
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_event);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        Button send = (Button)findViewById(R.id.send);
        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        TextView eventName = (TextView) findViewById(R.id.eventName);
        TextView eventDay = (TextView) findViewById(R.id.eventDay);
        TextView eventPlace = (TextView) findViewById(R.id.eventPlace);
        TextView eventContent = (TextView) findViewById(R.id.eventContent);
        TextView eventCost = (TextView) findViewById(R.id.eventCost);

        _eventName = eventName.getText().toString();
        _eventDay = eventDay.getText().toString();
        _eventPlace = eventPlace.getText().toString();
        _eventContent = eventContent.getText().toString();
        _eventCost = eventCost.getText().toString();

        _inputtedEventInfo += _eventName + ",";
        _inputtedEventInfo += _eventDay + ",";
        _inputtedEventInfo += _eventPlace + ",";
        _inputtedEventInfo += _eventCost + ",";
        _inputtedEventInfo += _eventContent + ",";

        Bundle params = new Bundle();
        params.putString("message", _inputtedEventInfo);
        params.putBoolean("is_hidden", true);
        Session session = Session.getActiveSession();
        new Request(
                session,
                getString(R.string.pageId) + "/feed",
                params,
                HttpMethod.POST,
                new Request.Callback() {
                    public void onCompleted(Response response) {
            /* handle the result */
                        Log.i("publish", response.toString());
                        Log.i("publish", _inputtedEventInfo);
                    }
                }
        ).executeAsync();
        finish();
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
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
                Intent listEventIntent = new Intent(MakeEventActivity.this, ListEventsActivity.class);
                startActivity(listEventIntent);
                break;
            case R.id.task_list:
                Intent listTaskIntent = new Intent(MakeEventActivity.this, ListTaskActivity.class);
                startActivity(listTaskIntent);
                break;
            case R.id.myPage:
                Intent myPage = new Intent(MakeEventActivity.this, MyPageActivity.class);
                startActivity(myPage);
                break;
        }
        return true;
    }
}