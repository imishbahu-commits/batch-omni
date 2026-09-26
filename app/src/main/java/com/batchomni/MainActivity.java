package com.batchomni;

import android.app.*;import android.os.*;import android.content.*;import android.graphics.Color;import android.graphics.Typeface;import android.provider.Settings;import android.view.*;import android.widget.*;import java.util.*;

public class MainActivity extends Activity {
    public static final String PREFS="batch"; EditText prompts; TextView status; Button start,access;
    final int WHITE=Color.WHITE, DARK=Color.rgb(28,28,30);

    @Override public void onCreate(Bundle b){super.onCreate(b); build();}
    TextView tv(String s,int sp){TextView t=new TextView(this);t.setText(s);t.setTextColor(WHITE);t.setTextSize(sp);t.setPadding(12,10,12,10);return t;}
    Button btn(String label){Button b=new Button(this);b.setText(label);b.setTextColor(Color.BLACK);b.setTextSize(15);b.setAllCaps(false);b.setBackgroundColor(WHITE);b.setMinHeight(64);return b;}

    void build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(24,24,24,18);root.setBackgroundColor(Color.BLACK);
        TextView title=tv("BATCH OMNI",28);title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);root.addView(title);
        root.addView(tv("Paste prompts separated by [SHOT 01], [SHOT 02]…\nEach prompt must be 900 characters or less.",14));
        prompts=new EditText(this);prompts.setTextColor(WHITE);prompts.setHintTextColor(Color.GRAY);prompts.setHint("[SHOT 01]\nYour first video prompt...\n\n[SHOT 02]\nYour second video prompt...");prompts.setGravity(Gravity.TOP|Gravity.LEFT);prompts.setInputType(0x00004001|0x00080000);prompts.setBackgroundColor(DARK);prompts.setPadding(14,14,14,14);
        LinearLayout.LayoutParams ep=new LinearLayout.LayoutParams(-1,0,1);ep.setMargins(0,12,0,12);root.addView(prompts,ep);
        status=tv("Ready • 0 prompts",15);root.addView(status);
        start=btn("▶  START BATCH");LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,62);sp.setMargins(0,6,0,8);root.addView(start,sp);
        access=btn("⚙  ENABLE ACCESSIBILITY");root.addView(access,new LinearLayout.LayoutParams(-1,62));
        start.setOnClickListener(v->startBatch());access.setOnClickListener(v->startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        prompts.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int b,int c){status.setText("Detected • "+parse(s.toString()).size()+" prompts");}public void afterTextChanged(android.text.Editable e){}});
        setContentView(root);
    }

    void startBatch(){
        List<String> p=parse(prompts.getText().toString());
        if(p.isEmpty()){status.setText("No prompts detected — use [SHOT 01] and [SHOT 02]");return;}
        for(String s:p)if(s.length()>900){status.setText("A prompt exceeds 900 characters");return;}
        getSharedPreferences(PREFS,0).edit().putString("queue",String.join("\n---PROMPT---\n",p)).apply();
        if(OmniAccessibilityService.instance==null){status.setText("Accessibility is OFF — enable it, then return and press START BATCH");startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));return;}
        status.setText("Starting • "+p.size()+" prompts queued");OmniAccessibilityService.startQueue();
        new Handler(Looper.getMainLooper()).postDelayed(()->{Intent launch=getPackageManager().getLaunchIntentForPackage("com.google.android.apps.youtube.producer");if(launch!=null)startActivity(launch);else status.setText("YouTube Create is not installed");},900);
    }

    List<String> parse(String s){
        ArrayList<String> out=new ArrayList<>();String normalized=s.replace("\r","");String[] lines=normalized.split("\n",-1);StringBuilder current=new StringBuilder();boolean sawMarker=false;
        for(String line:lines){String trimmed=line.trim();if(trimmed.matches("(?i)\\[SHOT\\s*\\d+\\].*")){sawMarker=true;if(current.length()>0){String v=current.toString().trim();if(!v.isEmpty())out.add(v);current.setLength(0);}int end=trimmed.indexOf("]");String rest=trimmed.substring(end+1).trim();if(!rest.isEmpty())current.append(rest);}else{if(current.length()>0)current.append("\n");current.append(line);}}
        if(current.length()>0){String v=current.toString().trim();if(!v.isEmpty())out.add(v);}if(!sawMarker&&!s.trim().isEmpty()&&out.isEmpty())out.add(s.trim());return out;
    }
}