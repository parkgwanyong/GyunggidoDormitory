package org.androidtown.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class BusActivity extends AppCompatActivity {
    private TextView gFirstTime;
    private TextView sFirstTime;
    private TextView gSecondTime;
    private TextView sSecondTime;
    private TextView gFirstTime2;
    private TextView sFirstTime2;
    private TextView gSecondTime2;
    private TextView sSecondTime2;
    private TextView gFirstTime3;
    private TextView sFirstTime3;
    private TextView gSecondTime3;
    private TextView sSecondTime3;

    String dobong01_1[];
    String dobong01_2[];
    String dobong02_1[];
    String dobong02_2[];
    String dobong03_1[];
    String dobong03_2[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);
        gFirstTime = (TextView) findViewById(R.id.gFirstTime);
        gSecondTime = (TextView) findViewById(R.id.gSecondTime);
        sFirstTime = (TextView) findViewById(R.id.sFirstTime);
        sSecondTime = (TextView) findViewById(R.id.sSecondTime);

        gFirstTime2 = (TextView) findViewById(R.id.gFirstTime2);
        gSecondTime2 = (TextView) findViewById(R.id.gSecondTime2);
        sFirstTime2 = (TextView) findViewById(R.id.sFirstTime2);
        sSecondTime2 = (TextView) findViewById(R.id.sSecondTime2);

        gFirstTime3 = (TextView) findViewById(R.id.gFirstTime3);
        gSecondTime3 = (TextView) findViewById(R.id.gSecondTime3);
        sFirstTime3 = (TextView) findViewById(R.id.sFirstTime3);
        sSecondTime3 = (TextView) findViewById(R.id.sSecondTime3);

        getXmlsite();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.bus_actionbar,null);

        actionBar.setCustomView(actionbar);


        //getMenuInflater().inflate(R.layout.bus_actionbar, null);

        Toolbar parent = (Toolbar)actionbar.getParent();

        parent.setContentInsetsAbsolute(0,0);

        ImageButton refresh = (ImageButton) actionbar.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"새로고침 되었습니다.",Toast.LENGTH_SHORT).show();

            }
        });

        return true;


    }

    private void getXmlsite(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder1_1 = new StringBuilder();
                final StringBuilder builder1_2 = new StringBuilder();

                final StringBuilder builder2_1 = new StringBuilder();
                final StringBuilder builder2_2 = new StringBuilder();

                final StringBuilder builder3_1 = new StringBuilder();
                final StringBuilder builder3_2 = new StringBuilder();

                try {
                    Document doc = Jsoup.connect("http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRouteAll?ServiceKey=1%2BBzaBAcSlgVGh4IdbZiXNcXgTGtiE6tzQ8lEh1ATXxz%2B6iWZGb2N4VVNdbaKUlnuQT8ltI3McIEClf0TQwayA%3D%3D&busRouteId=109900001").get();
                    Document doc2 = Jsoup.connect("http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRouteAll?ServiceKey=1%2BBzaBAcSlgVGh4IdbZiXNcXgTGtiE6tzQ8lEh1ATXxz%2B6iWZGb2N4VVNdbaKUlnuQT8ltI3McIEClf0TQwayA%3D%3D&busRouteId=108900010").get();
                    Document doc3 = Jsoup.connect("http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRouteAll?ServiceKey=1%2BBzaBAcSlgVGh4IdbZiXNcXgTGtiE6tzQ8lEh1ATXxz%2B6iWZGb2N4VVNdbaKUlnuQT8ltI3McIEClf0TQwayA%3D%3D&busRouteId=109900007").get();

                    Elements links1_1 = doc.select("ServiceResult").select("msgBody").select("itemList").select("arrmsg1");
                    Elements links1_2 = doc.select("ServiceResult").select("msgBody").select("itemList").select("arrmsg2");

                    Elements links2_1 = doc2.select("ServiceResult").select("msgBody").select("itemList").select("arrmsg1");
                    Elements links2_2 = doc2.select("ServiceResult").select("msgBody").select("itemList").select("arrmsg2");

                    Elements links3_1 = doc3.select("ServiceResult").select("msgBody").select("itemList").select("arrmsg1");
                    Elements links3_2 = doc3.select("ServiceResult").select("msgBody").select("itemList").select("arrmsg2");

                    int arridx1_1 = 0;
                    int arridx1_2 = 0;

                    int arridx2_1 = 0;
                    int arridx2_2 = 0;

                    int arridx3_1 = 0;
                    int arridx3_2 = 0;

                    dobong01_1 = new String [1000];
                    dobong01_2 = new String [1000];

                    dobong02_1 = new String [1000];
                    dobong02_2 = new String [1000];

                    dobong03_1 = new String [1000];
                    dobong03_2 = new String [1000];




                    for (Element link : links1_1) {

                        builder1_1.append("\n\n").append(link.text());
                        dobong01_1[arridx1_1] = link.text().toString();
                        arridx1_1 = arridx1_1+1;

                    }
                    for (Element link2 : links1_2) {

                        builder1_2.append("\n\n").append(link2.text());
                        dobong01_2[arridx1_2] = link2.text().toString();
                        arridx1_2 = arridx1_2+1;

                    }



                    for (Element link : links2_1) {

                        builder2_1.append("\n\n").append(link.text());
                        dobong02_1[arridx2_1] = link.text().toString();
                        arridx2_1 = arridx2_1+1;

                    }
                    for (Element link2 : links2_2) {

                        builder2_2.append("\n\n").append(link2.text());
                        dobong02_2[arridx2_2] = link2.text().toString();
                        arridx2_2 = arridx2_2+1;

                    }



                    for (Element link : links3_1) {

                        builder3_1.append("\n\n").append(link.text());
                        dobong03_1[arridx3_1] = link.text().toString();
                        arridx3_1 = arridx3_1+1;

                    }
                    for (Element link2 : links3_2) {

                        builder3_2.append("\n\n").append(link2.text());
                        dobong03_2[arridx3_2] = link2.text().toString();
                        arridx3_2 = arridx3_2+1;

                    }
                } catch (IOException e) {
                    builder1_1.append("Error : ").append(e.getMessage()).append("\n");
                    builder1_2.append("Error : ").append(e.getMessage()).append("\n");
                    builder2_1.append("Error : ").append(e.getMessage()).append("\n");
                    builder2_2.append("Error : ").append(e.getMessage()).append("\n");
                    builder3_1.append("Error : ").append(e.getMessage()).append("\n");
                    builder3_2.append("Error : ").append(e.getMessage()).append("\n");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            gFirstTime.setText(dobong01_1[6]);
                            gSecondTime.setText(dobong01_2[6]);
                            sFirstTime.setText(dobong01_1[19]);
                            sSecondTime.setText(dobong01_2[19]);

                            gFirstTime2.setText(dobong02_1[7]);
                            gSecondTime2.setText(dobong02_2[7]);
                            sFirstTime2.setText(dobong02_1[14]);
                            sSecondTime2.setText(dobong02_2[14]);

                            gFirstTime3.setText(dobong03_1[4]);
                            gSecondTime3.setText(dobong03_2[4]);
                            sFirstTime3.setText(dobong03_1[10]);
                            sSecondTime3.setText(dobong03_2[10]);
                        }catch(Exception e){

                        }



                    }
                });
            }
        }).start();

    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(BusActivity.this, MainActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

    }

}
