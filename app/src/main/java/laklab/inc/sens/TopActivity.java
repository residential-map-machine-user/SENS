package laklab.inc.sens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;


public class TopActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
        Button annualCalender = (Button) findViewById(R.id.annual_schedule_button);
        annualCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopActivity.this, CalenderActivity.class);
                startActivity(intent);
            }
        });
        Button listingEvents = (Button) findViewById(R.id.show_event_button);
        listingEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopActivity.this, ListEventsActivity.class);
                startActivity(intent);
            }
        });
        Button makeTask = (Button) findViewById(R.id.make_task_button);
        makeTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopActivity.this, ChooseEventActivityForTask.class);
                startActivity(intent);
            }

        });
        Button myPage = (Button) findViewById(R.id.button_mypage);
        myPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopActivity.this, MyPageActivity.class);
                startActivity(intent);
            }
        });
        Button makeEvent = (Button) findViewById(R.id.button_make_event);
        makeEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopActivity.this, MakeEventActivity.class);
                startActivity(intent);
            }
        });
        Button listTask = (Button) findViewById(R.id.button_task_list);
        listTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopActivity.this, ListTaskActivity.class);
                startActivity(intent);
            }
        });
    }
}
