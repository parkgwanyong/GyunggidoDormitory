package org.androidtown.prototype;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Calendar;

public class MenuActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextView mon1;
    private TextView mon2;
    private TextView mon3;
    private TextView mon4;
    private TextView mon5;
    private TextView tue1;
    private TextView tue2;
    private TextView tue3;
    private TextView tue4;
    private TextView tue5;
    private TextView wed1;
    private TextView wed2;
    private TextView wed3;
    private TextView wed4;
    private TextView wed5;
    private TextView thu1;
    private TextView thu2;
    private TextView thu3;
    private TextView thu4;
    private TextView thu5;
    private TextView fri1;
    private TextView fri2;
    private TextView fri3;
    private TextView fri4;
    private TextView fri5;
    private TextView sat1;
    private TextView sat2;
    private TextView sat3;
    private TextView sat4;
    private TextView sat5;
    private TextView sun1;
    private TextView sun2;
    private TextView sun3;
    private TextView sun4;
    private TextView sun5;

    private ProgressDialog mMenuDialog;

    private TableRow linMon;
    private TableRow linTue;
    private TableRow linWed;
    private TableRow linThr;
    private TableRow linFri;
    private TableRow linSat;
    private TableRow linSun;
    String menu[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mMenuDialog = new ProgressDialog(this);

        mMenuDialog.setTitle("불러오는 중");
        mMenuDialog.setMessage("잠시만 기다려주세요.");
        mMenuDialog.show();

        mToolbar = (Toolbar) findViewById(R.id.menu_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("식단표");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.BLACK);
        }

        linMon = (TableRow)findViewById(R.id.linMon);
        linTue = (TableRow)findViewById(R.id.linTue);
        linWed = (TableRow)findViewById(R.id.linWed);
        linThr = (TableRow)findViewById(R.id.linThr);
        linFri = (TableRow)findViewById(R.id.linFri);
        linSat = (TableRow)findViewById(R.id.linSat);
        linSun = (TableRow)findViewById(R.id.linSun);

        mon1 = (TextView)findViewById(R.id.mon1);
        mon2 = (TextView)findViewById(R.id.mon2);
        mon3 = (TextView)findViewById(R.id.mon3);
        mon4 = (TextView)findViewById(R.id.mon4);
        mon5 = (TextView)findViewById(R.id.mon5);
        tue1 = (TextView)findViewById(R.id.tue1);
        tue2 = (TextView)findViewById(R.id.tue2);
        tue3 = (TextView)findViewById(R.id.tue3);
        tue4 = (TextView)findViewById(R.id.tue4);
        tue5 = (TextView)findViewById(R.id.tue5);
        wed1 = (TextView)findViewById(R.id.wed1);
        wed2 = (TextView)findViewById(R.id.wed2);
        wed3 = (TextView)findViewById(R.id.wed3);
        wed4 = (TextView)findViewById(R.id.wed4);
        wed5 = (TextView)findViewById(R.id.wed5);
        thu1 = (TextView)findViewById(R.id.thu1);
        thu2 = (TextView)findViewById(R.id.thu2);
        thu3 = (TextView)findViewById(R.id.thu3);
        thu4 = (TextView)findViewById(R.id.thu4);
        thu5 = (TextView)findViewById(R.id.thu5);
        fri1 = (TextView)findViewById(R.id.fri1);
        fri2 = (TextView)findViewById(R.id.fri2);
        fri3 = (TextView)findViewById(R.id.fri3);
        fri4 = (TextView)findViewById(R.id.fri4);
        fri5 = (TextView)findViewById(R.id.fri5);
        sat1 = (TextView)findViewById(R.id.sat1);
        sat2 = (TextView)findViewById(R.id.sat2);
        sat3 = (TextView)findViewById(R.id.sat3);
        sat4 = (TextView)findViewById(R.id.sat4);
        sat5 = (TextView)findViewById(R.id.sat5);
        sun1 = (TextView)findViewById(R.id.sun1);
        sun2 = (TextView)findViewById(R.id.sun2);
        sun3 = (TextView)findViewById(R.id.sun3);
        sun4 = (TextView)findViewById(R.id.sun4);
        sun5 = (TextView)findViewById(R.id.sun5);

        getWebsite();


    }
    private void getWebsite(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();

                try {
                    Document doc = Jsoup.connect("http://www.ggjh.co.kr/now/menu.php").get();
                    Elements links = doc.select("tr[height] td[bgcolor]");
                    int arridx = 0;
                    menu = new String [100];
                    for (Element link : links) {

                        builder.append("\n\n").append(link.text());
                        menu[arridx] = link.text().toString();
                        arridx = arridx+1;



                    }
                } catch (IOException e) {
                    builder.append("오류 : ").append(e.getMessage()).append("\n");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            sun1.setText( getMenu(menu[0]));
                            sun2.setText( getMenu(menu[1]));
                            sun3.setText( getMenu(menu[2]));
                            sun4.setText( getMenu(menu[3]));
                            sun5.setText( getMenu(menu[4]));
                            mon1.setText( getMenu(menu[5]));
                            mon2.setText( getMenu(menu[6]));
                            mon3.setText( getMenu(menu[7]));
                            mon4.setText( getMenu(menu[8]));
                            mon5.setText( getMenu(menu[9]));
                            tue1.setText( getMenu(menu[10]));
                            tue2.setText( getMenu(menu[11]));
                            tue3.setText( getMenu(menu[12]));
                            tue4.setText( getMenu(menu[13]));
                            tue5.setText( getMenu(menu[14]));
                            wed1.setText( getMenu(menu[15]));
                            wed2.setText( getMenu(menu[16]));
                            wed3.setText( getMenu(menu[17]));
                            wed4.setText( getMenu(menu[18]));
                            wed5.setText( getMenu(menu[19]));
                            thu1.setText( getMenu(menu[20]));
                            thu2.setText( getMenu(menu[21]));
                            thu3.setText( getMenu(menu[22]));
                            thu4.setText( getMenu(menu[23]));
                            thu5.setText( getMenu(menu[24]));
                            fri1.setText( getMenu(menu[25]));
                            fri2.setText( getMenu(menu[26]));
                            fri3.setText( getMenu(menu[27]));
                            fri4.setText( getMenu(menu[28]));
                            fri5.setText( getMenu(menu[29]));
                            sat1.setText( getMenu(menu[30]));
                            sat2.setText( getMenu(menu[31]));
                            sat3.setText( getMenu(menu[32]));
                            sat4.setText( getMenu(menu[33]));
                            sat5.setText( getMenu(menu[34]));

                            setBackColor();
                            mMenuDialog.dismiss();

                        } catch(Exception e){
                            Toast.makeText(MenuActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            mMenuDialog.dismiss();
                        }

                    }
                });
            }
        }).start();

    }

    private void setBackColor() {
        Calendar cal = Calendar.getInstance();
        int nWeek = cal.get(Calendar.DAY_OF_WEEK);

        try{
            switch(nWeek){
                case 1:
                    linSun.setBackgroundColor(Color.parseColor("#B2DFDB"));
                    break;
                case 2:
                    linMon.setBackgroundColor(Color.parseColor("#B2DFDB"));
                    break;
                case 3:
                    linTue.setBackgroundColor(Color.parseColor("#B2DFDB"));
                    break;
                case 4:
                    linWed.setBackgroundColor(Color.parseColor("#B2DFDB"));
                    break;
                case 5:
                    linThr.setBackgroundColor(Color.parseColor("#B2DFDB"));
                    break;
                case 6:
                    linFri.setBackgroundColor(Color.parseColor("#B2DFDB"));
                    break;
                case 7:
                    linSat.setBackgroundColor(Color.parseColor("#B2DFDB"));
                    break;
                default:
                    break;
            }
        } catch(Exception e){

        }

    }


    private String getMenu(String menu){
        try{
            String[] splitmeu = menu.split("\\s");
            String printmenu = "";
            for (String me : splitmeu) {
                printmenu = printmenu + me + "\n";
            }
            return printmenu;
        } catch(Exception e){
            return "불러올 정보가 없습니다.";

        }

    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(homeIntent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.menu_actionbar,null);
        actionBar.setCustomView(actionbar);

        Toolbar parent = (Toolbar)actionbar.getParent();
        parent.setContentInsetsAbsolute(0,0);
        return true;


    }
    */

    @Override
    public void onBackPressed(){
        Intent i = new Intent(MenuActivity.this, MainActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);

    }

}
