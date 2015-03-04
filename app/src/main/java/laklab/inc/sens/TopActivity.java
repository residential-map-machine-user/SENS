package laklab.inc.sens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


public class TopActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
        ImageButton annualCalender = (ImageButton) findViewById(R.id.annual_schedule_button);
        annualCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopActivity.this, CalenderActivity.class);
                startActivity(intent);
            }
        });
        ImageButton listingEvents = (ImageButton) findViewById(R.id.show_event_button);
        listingEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopActivity.this, ListEventsActivity.class);
                startActivity(intent);
            }
        });
        ImageButton makeTask = (ImageButton) findViewById(R.id.make_task_button);
        makeTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopActivity.this, ChooseEventActivityForTask.class);
                startActivity(intent);
            }

        });
        ImageButton myPage = (ImageButton) findViewById(R.id.button_mypage);
        myPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopActivity.this, MyPageActivity.class);
                startActivity(intent);
            }
        });
        ImageButton makeEvent = (ImageButton) findViewById(R.id.button_make_event);
        makeEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopActivity.this, MakeEventActivity.class);
                startActivity(intent);
            }
        });
        ImageButton listTask = (ImageButton) findViewById(R.id.button_task_list);
        listTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopActivity.this, ListTaskActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // メニューの要素を追加して取得
        MenuItem actionItem = menu.add("Action Button Help Icon");
        // アイコンを設定
        actionItem.setIcon(android.R.drawable.ic_menu_help);

        // SHOW_AS_ACTION_ALWAYS:常に表示
        actionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, "Selected Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        return true;
    }
}
