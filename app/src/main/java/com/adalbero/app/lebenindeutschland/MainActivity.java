package com.adalbero.app.lebenindeutschland;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.data.Exam;
import com.adalbero.app.lebenindeutschland.data.ExamDynamic;
import com.adalbero.app.lebenindeutschland.data.ExamHeader;
import com.adalbero.app.lebenindeutschland.data.ExamLand;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Exam> data;
    private ExamItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateData();

        mAdapter = new ExamItemAdapter(this, data);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                Exam exam = data.get(position);
                final String name = exam.getName();

                if (exam instanceof ExamHeader) return;
                if (exam instanceof ExamDynamic) {
                    ((ExamDynamic) exam).build(MainActivity.this, new ResultCallback() {
                        @Override
                        public void onResult(Object parent, Object param) {
                            goList(name);
                        }
                    });
                    return;
                }

                if (exam instanceof ExamLand && name.charAt(0) == '*') {
                    goSettings();
                    return;
                }
                goList(name);
            }
        });

        AppController.initAdView(this);
    }

    private void updateData() {
        data = new ArrayList();
        for (Exam exam : AppController.getExamList()) {
            exam.init();
            data.add(exam);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                goSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppController.getExamLand();
        Store.resetExam();
    }

    private void goSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void goList(String examName) {
        Store.setExamName(examName);
        Store.setQuestionIdx(0);

        Intent intent = new Intent(this, ExamActivity.class);
        this.startActivity(intent);
    }
}
