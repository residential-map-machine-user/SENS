package laklab.inc.sens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.widget.Toast;


public class ListEventsActivity extends ActionBarActivity {
    private ListView _eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_events);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        adapter.add("Christmas Discussion");
        adapter.add("桜 Discussion");
        adapter.add("Freshman Speech");
        adapter.add("Freshman Debate");
        adapter.add("K.U.E.L Discussion");
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view, int position, long id) {

                //listView　parentでlistView全体を取得する
                ListView listView = (ListView) parent;
                //getItemAtPositionでクリックされたItemを取得する
                String item = (String) listView.getItemAtPosition(position);
                //Toastでクリックされたitem が取得されているか確認する
                Toast.makeText(ListEventsActivity.this, item, Toast.LENGTH_SHORT).show();
                //つぎのアクティビティを開始する
                Intent intent = new Intent(ListEventsActivity.this, DetailEventActivity.class);
                startActivity(intent);

            }
        });
    }


    @Override
    protected void onResume(){
        super.onResume();
        SharedPreferences pref = getSharedPreferences("attendance", MODE_PRIVATE);
        Boolean attendToken = pref.getBoolean("attendance", false);
        System.out.println(attendToken);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_listing_events, menu);
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
}
