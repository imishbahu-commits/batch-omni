package com.batchomni;

import android.app.*;import android.os.*;import android.content.*;import android.graphics.Color;import android.provider.Settings;import android.view.*;import android.widget.*;import java.util.*;

public class MainActivity extends Activity {
    public static final String PREFS="batch"; EditText prompts; TextView status; Button start;
    @Override public void onCreate(Bundle b){super.onCreate(b); build();}
    TextView tv(String s,int sp){TextView t=new TextView(this);t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(sp);t.setPadding(20,12,20,12);return t;}
    void build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(24,30,24,20);root.setBackgroundColor(Color.BLACK);
        TextView title=tv("BATCH OMNI",28);title.setTypeface(null,1);root.addView(title);
        TextView sub=tv("Paste prompts separated by [SHOT 01], [SHOT 02]…\nYouTube Create Omni: max 900 characters per prompt.",14);root.addView(sub);
        prompts=new EditText(this);prompts.setTextColor(Color.WHITE);prompts.setHintTextColor(Color.GRAY);prompts.setHint("[SHOT 01]\nYour first video prompt...\n\n[SHOT 02]\nYour second video prompt...");prompts.setGravity(Gravity.TOP);prompts.setInputType(0x00004001|0x00080000);prompts.setBackgroundColor(Color.rgb(28,28,30));LinearLayout.LayoutParams ep=new LinearLayout.LayoutParams(-1,0,1);ep.setMargins(0,12,0,12);root.addView(prompts,ep);
        status=tv("Ready • 0 prompts",14);root.addView(status);
        start=new Button(this);start.setText("START BATCH");root.addView(start,new LinearLayout.LayoutParams(-1,60));
        Button access=new Button(this);access.setText("ENABLE AUTOMATION ACCESSIBILITY");root.addView(access,new LinearLayout.LayoutParams(-1,60));
        start.setOnClickListener(v->{List<String> p=parse(prompts.getText().toString()); if(p.isEmpty()){status.setText("No prompts found");return;} for(String s:p)if(s.length()>900){status.setText("A prompt exceeds 900 characters");return;} getSharedPreferences(PREFS,0).edit().putString("queue",String.join("\n---PROMPT---\n",p)).apply(); status.setText("Queued "+p.size()+" prompts • Open YouTube Create → Generate Video"); OmniAccessibilityService.startQueue();});
        access.setOnClickListener(v->{startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));});
        setContentView(root);
    }
    List<String> parse(String s){ArrayList<String> out=new ArrayList<>();String[] a=s.split("(?i)(?=\[SHOT\s*\d+\])");for(String x:a){x=x.trim();if(x.isEmpty())continue;x=x.replaceFirst("(?i)^\[SHOT\s*\d+\]\s*","").trim();if(!x.isEmpty())out.add(x);}if(out.isEmpty()&&!s.trim().isEmpty())out.add(s.trim());return out;}
}