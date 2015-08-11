package wolf.uit.quiztroll.com;

import com.ckt.android.kingtroll.R;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GameOver_Level1 extends Activity
{
  Button btReplay;
  MediaPlayer mp;

  public void OpenGame()
  {
    startActivity(new Intent(this, Mainquiztroll.class));
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
    setContentView(R.layout.activity_game_over);
    this.mp = MediaPlayer.create(this, R.raw.click);
    play(1);
    this.btReplay = ((Button)findViewById(R.id.button1));
    this.btReplay.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        GameOver_Level1.this.play(0);
        GameOver_Level1.this.OpenGame();
      }
    });
  }

  protected void onPause()
  {
    super.onPause();
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
      this.mp = MediaPlayer.create(this, R.raw.gameover);
    this.mp.start();
  }
}