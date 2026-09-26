package com.batchomni;

import android.accessibilityservice.AccessibilityService;import android.view.accessibility.*;import android.os.*;import android.content.*;import java.util.*;

public class OmniAccessibilityService extends AccessibilityService {
    static OmniAccessibilityService instance; Handler h=new Handler(Looper.getMainLooper()); ArrayList<String> q=new ArrayList<>(); int index=0; boolean running=false; long lastAction=0;
    static void startQueue(){if(instance!=null)instance.begin();}
    @Override public void onServiceConnected(){instance=this;}
    @Override public void onAccessibilityEvent(AccessibilityEvent e){if(!running)return; if(System.currentTimeMillis()-lastAction<700)return; if(!"com.google.android.apps.youtube.producer".equals(e.getPackageName()))return; h.postDelayed(this::drive,500);}
    @Override public void onInterrupt(){running=false;}
    void begin(){String raw=getSharedPreferences(MainActivity.PREFS,0).getString("queue","");q.clear();q.addAll(Arrays.asList(raw.split("\n---PROMPT---\n",-1)));index=0;running=!q.isEmpty(); if(running)drive();}
    void drive(){if(!running||index>=q.size()){running=false;return;} AccessibilityNodeInfo root=getRootInActiveWindow();if(root==null)return;
        AccessibilityNodeInfo edit=findEditable(root); if(edit!=null){setText(edit,q.get(index)); lastAction=System.currentTimeMillis(); h.postDelayed(this::clickGenerate,800); return;}
        AccessibilityNodeInfo nav=findText(root,"Generate video"); if(nav!=null){click(nav);lastAction=System.currentTimeMillis();return;}
        AccessibilityNodeInfo gen=findText(root,"Generate"); if(gen!=null){click(gen);lastAction=System.currentTimeMillis();return;}
    }
    void clickGenerate(){AccessibilityNodeInfo root=getRootInActiveWindow();if(root==null)return;AccessibilityNodeInfo gen=findText(root,"Generate");if(gen!=null){click(gen);lastAction=System.currentTimeMillis();h.postDelayed(this::watchResult,2500);}}
    void watchResult(){if(!running)return;AccessibilityNodeInfo root=getRootInActiveWindow();if(root==null){h.postDelayed(this::watchResult,2500);return;}
        AccessibilityNodeInfo use=findText(root,"Use Video");if(use!=null){click(use);index++;lastAction=System.currentTimeMillis();h.postDelayed(this::drive,1500);return;}
        h.postDelayed(this::watchResult,2500);
    }
    AccessibilityNodeInfo findEditable(AccessibilityNodeInfo n){if(n==null)return null; if(n.isEditable() && n.isEnabled())return n;for(int i=0;i<n.getChildCount();i++){AccessibilityNodeInfo r=findEditable(n.getChild(i));if(r!=null)return r;}return null;}
    AccessibilityNodeInfo findText(AccessibilityNodeInfo n,String s){if(n==null)return null;CharSequence t=n.getText(),d=n.getContentDescription();if((t!=null&&t.toString().equalsIgnoreCase(s))||(d!=null&&d.toString().equalsIgnoreCase(s)))return n;for(int i=0;i<n.getChildCount();i++){AccessibilityNodeInfo r=findText(n.getChild(i),s);if(r!=null)return r;}return null;}
    void setText(AccessibilityNodeInfo n,String s){android.os.Bundle b=new android.os.Bundle();b.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,s);if(!n.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT,b)){n.performAction(AccessibilityNodeInfo.ACTION_FOCUS);android.content.ClipboardManager cm=(android.content.ClipboardManager)getSystemService(CLIPBOARD_SERVICE);cm.setPrimaryClip(android.content.ClipData.newPlainText("prompt",s));n.performAction(AccessibilityNodeInfo.ACTION_PASTE);}}
    void click(AccessibilityNodeInfo n){if(n.isClickable()){n.performAction(AccessibilityNodeInfo.ACTION_CLICK);return;}AccessibilityNodeInfo p=n.getParent();if(p!=null)click(p);}
}