package laklab.inc.sens;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


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
