package wolf.uit.quiztroll.com;

import com.ckt.android.kingtroll.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Danhhieu_level2 extends Activity
{
  Button btok;
  Typeface face;
  ImageView imTroll;
  int index;
  MediaPlayer mp;
  TextView tvdanhhieu;
  TextView tvthongbao;

  public void getIndex()
  {
    this.index = getIntent().getBundleExtra("DANHHIEU").getInt("SO");
  }

  public void onBackPressed()
  {
    finish();
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    requestWindowFeature(1);
    setContentView(R.layout.activity_danhhieu);
    this.mp = MediaPlayer.create(this, R.raw.click);
    this.tvthongbao = ((TextView)findViewById(R.id.tvthongbao));
    this.tvdanhhieu = ((TextView)findViewById(R.id.tvdanhhieu));
    this.imTroll = ((ImageView)findViewById(R.id.icontroll));
    this.face = Typeface.createFromAsset(getAssets(), "Arial.ttf");
    this.tvdanhhieu.setTypeface(this.face, 1);
    this.tvthongbao.setTypeface(this.face, 1);
    this.imTroll = ((ImageView)findViewById(R.id.icontroll));
    this.btok = ((Button)findViewById(R.id.button1));
    this.btok.setTypeface(this.face, 1);
    getIndex();
    this.btok.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        Danhhieu_level2.this.finish();
      }
    });
    switch (this.index)
    {
    default:
      return;
    case 20:
      this.tvthongbao.setText(getResources().getString(R.string.lv_question20));
      this.tvdanhhieu.setText(getResources().getString(R.string.troll_lv4));
      this.imTroll.setImageResource(R.drawable.trollcapdang);
      playButton(1);
      return;
    case 50:
    }
    this.tvthongbao.setText(getResources().getString(R.string.lv_question50));
    this.tvdanhhieu.setText(getResources().getString(R.string.troll_lv5));
    this.imTroll.setImageResource(R.drawable.trolldaihoc);
    playButton(1);
  }

  protected void onPause()
  {
    super.onPause();
  }

  public void playButton(int paramInt)
  {
    if (this.mp.isPlaying())
    {
      this.mp.stop();
      this.mp.reset();
    }
    if (paramInt == 0)
      this.mp = MediaPlayer.create(this, R.raw.click);
    if (paramInt == 1)
      this.mp = MediaPlayer.create(this, R.raw.win);
    this.mp.start();
  }
}