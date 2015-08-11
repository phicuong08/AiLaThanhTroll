package wolf.uit.quiztroll.com;

import com.ckt.android.kingtroll.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LevelGame extends Activity
{
  static CountDownTimer timer = null;
  Button btlv1;
  Button btlv2;
  Typeface face;
  View layout;
  boolean level2;
  MediaPlayer mp;
  private SharedPreferences sharedPreferences;
  TextView text;
  Toast toast;

  public void Custom_toast(String paramString)
  {
    if (this.toast != null)
      this.toast.cancel();
    this.text.setText(paramString);
    this.text.setTextColor(Color.rgb(65, 11, 146));
    this.toast = new Toast(getApplicationContext());
    this.toast.setGravity(80, 0, 0);
    this.toast.setDuration(500);
    this.toast.setView(this.layout);
    timer = new CountDownTimer(5000L, 1000L)
    {
      public void onFinish()
      {
        LevelGame.this.toast.cancel();
      }

      public void onTick(long paramLong)
      {
        LevelGame.this.toast.show();
      }
    }
    .start();
  }

  public void OpenGamelevel1()
  {
    startActivity(new Intent(this, Mainquiztroll.class));
  }

  public void OpenGamelevel2()
  {
    startActivity(new Intent(this, Mainquiztroll_level2.class));
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    requestWindowFeature(1);
    setContentView(R.layout.activity_level_game);
    this.mp = MediaPlayer.create(this, R.raw.click);
    this.sharedPreferences = getSharedPreferences("my_data", 0);
    this.btlv1 = ((Button)findViewById(R.id.bt_lv1));
    this.btlv2 = ((Button)findViewById(R.id.bt_lv2));
    this.face = Typeface.createFromAsset(getAssets(), "Arial.ttf");
    this.btlv1.setTypeface(this.face, 1);
    this.btlv2.setTypeface(this.face, 1);
    this.btlv1.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        LevelGame.this.play(0);
        LevelGame.this.OpenGamelevel1();
      }
    });
    this.btlv2.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        LevelGame.this.play(0);
        LevelGame.this.level2 = LevelGame.this.sharedPreferences.getBoolean("checked", false);
        if (LevelGame.this.level2)
          LevelGame.this.OpenGamelevel2();
        else
        	LevelGame.this.Custom_toast(getResources().getString(R.string.cant_Next_lv));
      }
    });
    this.layout = getLayoutInflater().inflate(R.layout.toast, (ViewGroup)findViewById(R.id.toast_layout_root));
    this.text = ((TextView)this.layout.findViewById(R.id.tvtoast));
    this.text.setTypeface(this.face, 1);
  }

  public void play(int paramInt)
  {
    if (this.mp.isPlaying())
    {
      this.mp.stop();
      this.mp.reset();
    }
    if (paramInt == 0)
      this.mp = MediaPlayer.create(this, R.raw.click);
    if (paramInt == 1)
      this.mp = MediaPlayer.create(this, R.raw.buzz);
    this.mp.start();
  }
}