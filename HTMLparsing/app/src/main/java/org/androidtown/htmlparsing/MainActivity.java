package org.androidtown.htmlparsing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private TextView textView2;
    String arr[];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.textView) ;
        textView2 = (TextView)findViewById(R.id.textView2);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWebsite();
            }
        });
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
                    arr = new String [100];




                    for (Element link : links) {

                        builder.append("\n\n").append(link.text());
                        arr[arridx] = link.text().toString();
                        arridx = arridx+1;



                    }
                } catch (IOException e) {
                    builder.append("Error : ").append(e.getMessage()).append("\n");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(builder.toString());
                        textView2.setText( getMenu(arr[2]));


                    }
                });
            }
        }).start();

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

}
