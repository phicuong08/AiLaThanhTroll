package wolf.uit.quiztroll.com;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import basegameutils.GameHelper;

import com.google.analytics.tracking.android.EasyTracker;
import java.util.ArrayList;

import com.ckt.android.kingtroll.R;
import wolf.uit.quiztroll.com.facebook.EziSocialManager;
import wolf.uit.quiztroll.com.facebook.FBConstants;
import wolf.uit.quiztroll.com.database.Question;
import wolf.uit.quiztroll.com.database.UserDBHandler;
import wolf.uit.quiztroll.com.MainActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public class Mainquiztroll extends Activity implements
	basegameutils.GameHelper.GameHelperListener,View.OnClickListener{
	static CountDownTimer timer = null;
	private AdsHelper adsHelper;
	ImageButton bt_facebook;
	ImageButton bt_fbinvite;
	Button btA;
	Button btB;
	Button btC;
	Button btD;
	int caudung = 0;
	Question cauhientai;
	ArrayList<Question> ds_cauhoi;
	Typeface face;
	int fap = 3;
	int index = 0;
	View layout;
	MediaPlayer mp;
	private SharedPreferences sharedPreferences;
	private EziSocialManager s_facebook;
	int socau = 100;
	TextView text;
	Toast toast;
	TextView tvFap;
	TextView tvNumber;
	TextView tvQ;
	Uri uri_icon;
	private int score=0;
	private GameHelper mHelper;
	private static final String LEADER_BOARD_ID = "CgkI9ufzjKkCEAIQAQ";

	public void Custom_toast(String paramString) {
		if (this.toast != null)
			this.toast.cancel();
		this.text.setText(paramString);
		this.text.setTextColor(Color.rgb(65, 11, 146));
		this.toast = new Toast(getApplicationContext());
		this.toast.setGravity(80, 0, 0);
		this.toast.setDuration(500);
		this.toast.setView(this.layout);
		timer = new CountDownTimer(5000L, 1000L) {
			public void onFinish() {
				Mainquiztroll.this.toast.cancel();
			}

			public void onTick(long paramLong) {
				Mainquiztroll.this.toast.show();
			}
		}.start();
	}

	public void KiemTraCauDung(String paramString) {
		if (!paramString.equalsIgnoreCase(cauhientai.getAnswer())) {
			fap = fap - 1;
			tvFap.setText(getResources().getString(R.string.fap) + this.fap);
			play(1);
			if (fap == 0) {
				gameOver();
				return;
			}else return;
		} else {
			if (cauhientai.getExplan() != null
					&& cauhientai.getExplan().length() > 0) {
				Custom_toast(cauhientai.getExplan());
			}
			index = index + 1;

	//need remote
	// save high score and submit score
			if(index>score)
			{
				score=index;
		        SharedPreferences.Editor localEditor = this.sharedPreferences.edit();
		        localEditor.putInt("score", index);
		        localEditor.commit();	
		        if(isSignedIn())
		        {
		        	Games.Leaderboards.submitScore(getApiClient(), getResources().getString(R.string.leaderboard_king_troll), score);	
		        }
			}
			
			hienthi(index);
			play(0);
		}
		this.tvFap.setText("Fap: " + fap);
		switch (index) {
		case 10:
//facebook
			EziSocialManager.postMessageOnWall(
					getResources().getString(R.string.facebook_message), 
					getResources().getString(R.string.facebook_caption), 
					getResources().getString(R.string.facebook_message), 
					getResources().getString(R.string.facebook_description), 
					getResources().getString(R.string.facebook_badgeIconURL), 
					getResources().getString(R.string.link_googlePlay));
			danhhieu(10);
			break;
		case 20:
			EziSocialManager.postMessageOnWall(
					getResources().getString(R.string.facebook_message), 
					getResources().getString(R.string.facebook_caption), 
					getResources().getString(R.string.facebook_message), 
					getResources().getString(R.string.facebook_description), 
					getResources().getString(R.string.facebook_badgeIconURL), 
					getResources().getString(R.string.link_googlePlay));
			danhhieu(20);
			break;
		case 50:
			EziSocialManager.postMessageOnWall(
					getResources().getString(R.string.facebook_message), 
					getResources().getString(R.string.facebook_caption), 
					getResources().getString(R.string.facebook_message), 
					getResources().getString(R.string.facebook_description), 
					getResources().getString(R.string.facebook_badgeIconURL), 
					getResources().getString(R.string.link_googlePlay));
			danhhieu(50);
			break;
		default:
	        if (this.index <= 95)
	            break;
	        SharedPreferences.Editor localEditor = this.sharedPreferences.edit();
	        localEditor.putBoolean("checked", true);
	        localEditor.commit();
	        gameWin();
		}
	}

	public void addForm() {
		this.face = Typeface.createFromAsset(getAssets(), "Arial.ttf");
		this.bt_facebook=((ImageButton)findViewById(R.id.sharefb));
		this.bt_facebook.setOnClickListener(this);
		this.bt_fbinvite=((ImageButton)findViewById(R.id.invitefb));
		this.btA = ((Button) findViewById(R.id.btA));
		this.btB = ((Button) findViewById(R.id.btB));
		this.btC = ((Button) findViewById(R.id.btC));
		this.btD = ((Button) findViewById(R.id.btD));
		this.tvQ = ((TextView) findViewById(R.id.txtQuestonContent));
		this.tvNumber = ((TextView) findViewById(R.id.txtQuestionNumber));
		this.tvFap = ((TextView) findViewById(R.id.txtFap));
		this.mp = MediaPlayer.create(this, R.raw.click);
		this.tvQ.setTypeface(this.face, 1);
		this.tvNumber.setTypeface(this.face, 1);
		this.tvFap.setTypeface(this.face, 1);
		this.btA.setTypeface(this.face);
		this.btB.setTypeface(this.face);
		this.btC.setTypeface(this.face);
		this.btD.setTypeface(this.face);
	}

	public void danhhieu(int paramInt) {
		Intent localIntent = new Intent(this, Danhhieu_level1.class);
		Bundle localBundle = new Bundle();
		localBundle.putInt("SO", paramInt);
		localIntent.putExtra("DANHHIEU", localBundle);
		startActivity(localIntent);
	}

	public void onClick(View arg0) {
		switch (arg0.getId())
		{
		case R.id.invitefb:
			
			break;
		}
	}
	public void event() {
		this.bt_facebook.setOnClickListener(new View.OnClickListener(){
			public void onClick(View paramView) {
				if(EziSocialManager.isFacebookSessionActive()){
					EziSocialManager.postMessageOnWall(
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
						EziSocialManager.postMessageOnWall(
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
		this.btA.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				Mainquiztroll.this.play(0);
				Mainquiztroll.this.KiemTraCauDung("a");
			}
		});
		this.btB.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				Mainquiztroll.this.play(0);
				Mainquiztroll.this.KiemTraCauDung("b");
			}
		});
		this.btC.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				if (Mainquiztroll.this.mp.isPlaying()) {
					Mainquiztroll.this.mp.stop();
					Mainquiztroll.this.mp.reset();
				}
				Mainquiztroll.this.play(0);
				Mainquiztroll.this.KiemTraCauDung("c");
			}
		});
		this.btD.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				Mainquiztroll.this.play(0);
				Mainquiztroll.this.KiemTraCauDung("d");
			}
		});
	}

	public void gameOver() {
		startActivity(new Intent(getApplicationContext(), GameOver_Level1.class));
		finish();
		this.adsHelper = new AdsHelper(this);
		this.adsHelper.loadInterstitial(
				getResources().getString(R.string.ad_unit_id), true);
	}

	public void gameWin() {
		startActivity(new Intent(getApplicationContext(), WinGame_level1.class));
		finish();
	}

	public void hienthi(int paramInt) {
		this.tvNumber.setText(getResources().getString(R.string.num_quest)+ String.valueOf(1 + this.index));
		this.cauhientai = ((Question) this.ds_cauhoi.get(paramInt));
		this.tvFap.setText(getResources().getString(R.string.fap) + this.fap);
		this.tvQ.setText(this.cauhientai.getquestion());
		this.btA.setText(this.cauhientai.getA());
		this.btB.setText(this.cauhientai.getB());
		this.btC.setText(this.cauhientai.getC());
		this.btD.setText(this.cauhientai.getD());
	}

	protected void onCreate(Bundle paramBundle) {
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
        
		this.sharedPreferences = getSharedPreferences("my_data", 0);
		score=sharedPreferences.getInt("score", score);
		addForm();
		UserDBHandler localUserDBHandler = new UserDBHandler(this);
		localUserDBHandler.open();
		this.ds_cauhoi = localUserDBHandler.getListQuestion(this.socau);
		localUserDBHandler.close();
		hienthi(this.index);
		event();
		this.layout = getLayoutInflater().inflate(R.layout.toast,
				(ViewGroup) findViewById(R.id.toast_layout_root));
		this.text = ((TextView) this.layout.findViewById(R.id.tvtoast));
		this.text.setTypeface(this.face, 1);
		s_facebook =new EziSocialManager(this, this);
		s_facebook.initWithActivity("email,user_birthday,user_friends,public_profile");
		//need remove 		
		getGameHelper();
        mHelper.setMaxAutoSignInAttempts(2);
        mHelper.setup(this);
	}

	protected void onPause() {
		super.onPause();
	}
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
		if (s_facebook != null)
		{
			if (requestCode == FBConstants.FACEBOOK_ACTIVITY_LOGIN)
				s_facebook.onActivityResult(requestCode, resultCode, data); 			
		}
        mHelper.onActivityResult(requestCode, resultCode, data);
    }
	public void onStart() {
		super.onStart();
		if(!EziSocialManager.isFacebookSessionActive()&&isNetworkAvailable())
		{
			EziSocialManager.loginWithFacebook();
		}
		EasyTracker.getInstance(this).activityStart(this);
		mHelper.onStart(this);
	}

	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
		mHelper.onStop();
	}

	public void play(int paramInt) {
		if (this.mp.isPlaying()) {
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
	

	@Override
	public void onSignInFailed() {
		Log.d("King Troll", "onSignInFailed");
	}

	@Override
	public void onSignInSucceeded() {
		Log.d("King Troll", "onSignInSucceeded");
	}
	
    protected void reconnectClient()
    {
        mHelper.reconnectClient();
    }
	protected boolean isSignedIn() {
		return mHelper.isSignedIn();
	}

	protected void signOut() {
		mHelper.signOut();
	}

	protected void beginUserInitiatedSignIn() {
		mHelper.beginUserInitiatedSignIn();
	}

	protected void enableDebugLog(boolean flag) {
		if (mHelper != null) {
			mHelper.enableDebugLog(flag);
		}
	}

	public GameHelper getGameHelper() {
		if (mHelper == null) {
			mHelper = new GameHelper(this, 1);
		}
		return mHelper;
	}

	protected GoogleApiClient getApiClient() {
		return mHelper.getApiClient();
	}

	public void submitScore(long l) {
		if (isSignedIn()) {
			Games.Leaderboards.submitScore(getApiClient(), getResources().getString(R.string.leaderboard_king_troll), l);
		}
	}

	public void showLeaderBoard() {
		if (isSignedIn()) {
			startActivityForResult(
					Games.Leaderboards.getLeaderboardIntent(
							getApiClient(), LEADER_BOARD_ID), 1);
			return;
		} else {
			beginUserInitiatedSignIn();
			return;
		}
	}
}