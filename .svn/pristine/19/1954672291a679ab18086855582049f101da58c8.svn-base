package wolf.uit.quiztroll.com;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.ckt.android.kingtroll.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class About extends Activity
{
  String path = "file:///android_asset/about.html";
  WebView wv;

  @SuppressLint({"NewApi"})
  public void onBackPressed()
  {
    super.onBackPressed();
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    requestWindowFeature(1);
    setContentView(R.layout.activity_about);
    this.wv = ((WebView)findViewById(R.id.webView1));
    this.wv.loadUrl(this.path);
  }

  public void onPause()
  {
    super.onPause();
  }
}