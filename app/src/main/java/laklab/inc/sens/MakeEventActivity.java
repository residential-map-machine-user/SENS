package laklab.inc.sens;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MakeEventActivity extends ActionBarActivity implements View.OnClickListener {

    private String _eventName;
    private TextView _eventDay;
    private TextView _eventPlace;
    private TextView _eventContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_event);
        Button send = (Button)findViewById(R.id.send);
        send.setOnClickListener(this);
        TextView eventName = (TextView) findViewById(R.id.eventName);
        TextView eventDay = (TextView) findViewById(R.id.eventDay);
        TextView eventPlace = (TextView) findViewById(R.id.eventPlace);
        TextView eventContent = (TextView) findViewById(R.id.eventContent);

        _eventName = eventName.getText().toString();
        _eventDay = eventDay;
        _eventPlace = eventPlace;
        _eventContent = eventContent;


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_making_new_event, menu);
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

        SharedPreferences pref = getSharedPreferences("EVENT_NAME",MODE_PRIVATE);
        Boolean saveState = pref.edit().putString("EVENT_NAME",_eventName).commit();
        if (saveState){
            finish();
        }

    }
}
