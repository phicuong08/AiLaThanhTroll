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

public class WinGame_level1 extends Activity
{
  Button btNext;
  Typeface face;
  ImageView imTroll;
  MediaPlayer mp;
  TextView tvdanhhieu;
  TextView tvthongbao;

  public void OpenGame()
  {
    startActivity(new Intent(this, Mainquiztroll_level2.class));
    finish();
  }

  public void onBackPressed()
  {
    finish();
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    requestWindowFeature(1);
    setContentView(R.layout.activity_wingame);
    this.btNext = ((Button)findViewById(R.id.button1));
    this.tvthongbao = ((TextView)findViewById(R.id.tvthongbao));
    this.tvdanhhieu = ((TextView)findViewById(R.id.textView2));
    this.face = Typeface.createFromAsset(getAssets(), "Arial.ttf");
    this.tvthongbao.setTypeface(this.face, 1);
    this.tvdanhhieu.setTypeface(this.face, 1);
    this.btNext.setTypeface(this.face, 1);
    this.mp = MediaPlayer.create(this, R.raw.win);
    play();
    this.btNext.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        WinGame_level1.this.playButton(0);
        WinGame_level1.this.OpenGame();
      }
    });
  }

  protected void onPause()
  {
    super.onPause();
  }

  public void play()
  {
    if (this.mp.isPlaying())
    {
      this.mp.stop();
      this.mp.reset();
    }
    this.mp = MediaPlayer.create(this, R.raw.win);
    this.mp.start();
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
      this.mp = MediaPlayer.create(this, R.raw.gameover);
    this.mp.start();
  }
}