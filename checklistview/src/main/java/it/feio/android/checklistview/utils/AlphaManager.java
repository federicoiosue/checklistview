package it.feio.android.checklistview.utils;

import android.annotation.SuppressLint;
import android.view.View;

public class AlphaManager {

  @SuppressLint("NewApi")
  public static void setAlpha (View v, float alpha) {
    v.setAlpha(alpha);
  }
}
