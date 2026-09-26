# Batch Omni — YouTube Create queue prototype

This Android prototype queues prompts and uses an Android AccessibilityService to operate the visible YouTube Create UI. It targets the current YouTube Create package com.google.android.apps.youtube.producer.

## What it does
- Paste multiple prompts using [SHOT 01], [SHOT 02]…
- Validates each prompt <= 900 characters.
- Stores a local queue.
- Accessibility service can find editable fields and buttons by accessibility text/content description.
- Inserts the prompt with ACTION_SET_TEXT, falling back to clipboard paste.
- Clicks Generate and waits for a visible Use Video control before advancing.

## Important prototype limitations
- It does not yet upload the 1–3 reference images automatically.
- It does not yet export/download the MP4; the current safe checkpoint is Use Video.
- UI labels can change between YouTube Create versions. A diagnostic screen should be added after the first device test to dump the accessibility node tree.
- Accessibility services are intended by Android for assisting users with disabilities; use this only on your own device and in accordance with Android/YouTube policies.

## Build without a PC
The included GitHub Actions workflow can build the APK in the cloud. Upload this project to a GitHub repository, then run Actions → Build APK. The APK appears as the workflow artifact.

## Device setup
1. Install YouTube Create.
2. Install Batch Omni.
3. Android Settings → Accessibility → Batch Omni → enable it.
4. Open Batch Omni and paste prompts.
5. Open YouTube Create and leave it signed in.
6. Return to Batch Omni and Start Batch.
7. For the first test, use one short prompt only.

## Safety
This project does not bypass authentication, network controls, or YouTube's backend. It only operates the visible app UI through Android accessibility APIs.