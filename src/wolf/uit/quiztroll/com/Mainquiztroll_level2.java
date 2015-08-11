package wolf.uit.quiztroll.com;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import com.ckt.android.kingtroll.R;
import wolf.uit.quiztroll.com.database.Question;
import wolf.uit.quiztroll.com.database.UserDBHandler;
import wolf.uit.quiztroll.com.facebook.EziSocialManager;
import wolf.uit.quiztroll.com.facebook.FBConstants;

public class Mainquiztroll_level2 extends Activity
{
  static CountDownTimer timer = null;
  private AdsHelper adsHelper;
  ImageButton bt_facebook;
  private EziSocialManager s_facebook;
  Button btA;
  Button btB;
  Button btC;
  Button btD;
  Question cauhientai;
  ArrayList<Question> ds_cauhoi;
  Typeface face;
  int fap = 3;
  int index = 0;
  View layout;
  MediaPlayer mp;
  int soMiliGiay = 30000;
  int socau = 95;
  TextView text;
  TextView tvFap;
  TextView tvNumber;
  TextView tvQ;
  Toast localToast;
  
  public void Custom_toast(String paramString)
  {
    this.text.setText(paramString);
    this.text.setTextColor(Color.rgb(65, 11, 146));
    localToast = new Toast(getApplicationContext());
    localToast.setGravity(80, 0, 0);
    localToast.setDuration(500);
    localToast.setView(this.layout);
    timer = new CountDownTimer(5000L, 1000L)
    {
      public void onFinish()
      {
        localToast.cancel();
      }

      public void onTick(long paramLong)
      {
        localToast.show();
      }
    }
    .start();
  }

  public void KiemTraCauDung(String paramString)
  {
      if (!paramString.equalsIgnoreCase(cauhientai.getAnswer()))
      {
    	  fap=fap-1;
    	  play(1);
    	  if(fap==0)
    	  {
    		  gameOver();
    		  return;
    	  }
      }
      else
      {
          if (cauhientai.getExplan() != null && cauhientai.getExplan().length() > 0)
          {
              Custom_toast(cauhientai.getExplan());
          }
    	  index = index + 1;  
          hienthi(index);
          play(0);
      }
      this.tvFap.setText("Fap: " + fap);
      switch(index){
	      case 10:
	    	  danhhieu(10);
	    	  break;
	      case 20:
	    	  danhhieu(20);
	    	  break;
	      case 50:
	    	  danhhieu(50);
	    	  break;
		default:
	        if (this.index <= 95)
	            break;
	        gameWin();
      }
  }

  public void addForm()
  {
    this.face = Typeface.createFromAsset(getAssets(), "Arial.ttf");
    this.bt_facebook=((ImageButton)findViewById(R.id.sharefb));
    this.btA = ((Button)findViewById(R.id.btA));
    this.btB = ((Button)findViewById(R.id.btB));
    this.btC = ((Button)findViewById(R.id.btC));
    this.btD = ((Button)findViewById(R.id.btD));
    this.tvQ = ((TextView)findViewById(R.id.txtQuestonContent));
    this.tvNumber = ((TextView)findViewById(R.id.txtQuestionNumber));
    this.tvFap = ((TextView)findViewById(R.id.txtFap));
    this.mp = MediaPlayer.create(this, R.raw.click);
    this.tvQ.setTypeface(this.face, 1);
    this.tvNumber.setTypeface(this.face, 1);
    this.tvFap.setTypeface(this.face, 1);
    this.btA.setTypeface(this.face);
    this.btB.setTypeface(this.face);
    this.btC.setTypeface(this.face);
    this.btD.setTypeface(this.face);
  }

  public void danhhieu(int paramInt)
  {
    Intent localIntent = new Intent(this, Danhhieu_level2.class);
    Bundle localBundle = new Bundle();
    localBundle.putInt("SO", paramInt);
    localIntent.putExtra("DANHHIEU", localBundle);
    startActivity(localIntent);
  }

  public void event()
  {
	this.bt_facebook.setOnClickListener(new View.OnClickListener(){
		public void onClick(View paramView) {
			if(EziSocialManager.isFacebookSessionActive()){
				EziSocialManager.PostToWall(
						getResources().getString(R.string.facebook_message), 
						getResources().getString(R.string.facebook_caption), 
						getResources().getString(R.string.facebook_message), 
						getResources().getString(R.string.facebook_description), 
						getResources().getString(R.string.facebook_badgeIconURL), 
						getResources().getString(R.string.link_googlePlay));
			}else
			{
				EziSocialManager.loginWithFacebook();
				if(EziSocialManager.isFacebookSessionActive())
				{
					EziSocialManager.PostToWall(
							getResources().getString(R.string.facebook_message), 
							getResources().getString(R.string.facebook_caption), 
							getResources().getString(R.string.facebook_message), 
							getResources().getString(R.string.facebook_description), 
							getResources().getString(R.string.facebook_badgeIconURL), 
							getResources().getString(R.string.link_googlePlay));
				}
			}
		}
	});
    this.btA.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        Mainquiztroll_level2.this.play(0);
        Mainquiztroll_level2.this.KiemTraCauDung("a");
      }
    });
    this.btB.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        Mainquiztroll_level2.this.play(0);
        Mainquiztroll_level2.this.KiemTraCauDung("b");
      }
    });
    this.btC.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        Mainquiztroll_level2.this.play(0);
        Mainquiztroll_level2.this.KiemTraCauDung("c");
      }
    });
    this.btD.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        Mainquiztroll_level2.this.play(0);
        Mainquiztroll_level2.this.KiemTraCauDung("d");
      }
    });
  }

  public void gameOver()
  {
    startActivity(new Intent(getApplicationContext(), GameOver_Level2.class));
    finish();
    this.adsHelper = new AdsHelper(this);
    this.adsHelper.loadInterstitial(getResources().getString(R.string.ad_unit_id), true);
  }

  public void gameWin()
  {
    startActivity(new Intent(getApplicationContext(), WinGame_Level2.class));
    finish();
  }

  public void hienthi(int paramInt)
  {
	this.tvNumber.setText(getResources().getString(R.string.num_quest)+ String.valueOf(1 + this.index));
	this.cauhientai = ((Question) this.ds_cauhoi.get(paramInt));
	this.tvFap.setText(getResources().getString(R.string.fap) + this.fap);
    this.tvQ.setText(this.cauhientai.getquestion());
    this.btA.setText(this.cauhientai.getA());
    this.btB.setText(this.cauhientai.getB());
    this.btC.setText(this.cauhientai.getC());
    this.btD.setText(this.cauhientai.getD());
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
	// Turn off the title bar
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	getWindow().setFlags(	WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
							WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    setContentView(R.layout.activity_mainquiztroll);
 // ads view
    AdView mAdView = (AdView) findViewById(R.id.adView);
    AdRequest adRequest = new AdRequest.Builder().build();
    mAdView.loadAd(adRequest);
    
    addForm();
    UserDBHandler localUserDBHandler = new UserDBHandler(this);
    localUserDBHandler.open();
    this.ds_cauhoi = localUserDBHandler.getListQuestion_level2(this.socau);
    localUserDBHandler.close();
    hienthi(this.index);
    event();
    this.layout = getLayoutInflater().inflate(R.layout.toast, (ViewGroup)findViewById(R.id.toast_layout_root));
    this.text = ((TextView)this.layout.findViewById(R.id.tvtoast));
    this.text.setTypeface(this.face);
	s_facebook =new EziSocialManager(this, this);
	s_facebook.initWithActivity("email,user_birthday,user_friends,public_profile");
  }
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
      super.onActivityResult(requestCode, resultCode, data);
		if (s_facebook != null)
		{
			if (requestCode == FBConstants.FACEBOOK_ACTIVITY_LOGIN)
				s_facebook.onActivityResult(requestCode, resultCode, data); 			
		}
  }
  protected void onPause()
  {
    super.onPause();
  }

  public void onStart()
  {
    super.onStart();
	if(!EziSocialManager.isFacebookSessionActive()&&isNetworkAvailable())
	{
		EziSocialManager.loginWithFacebook();
	}
    EasyTracker.getInstance(this).activityStart(this);
  }

  public void onStop()
  {
    super.onStop();
    EasyTracker.getInstance(this).activityStop(this);
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
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}