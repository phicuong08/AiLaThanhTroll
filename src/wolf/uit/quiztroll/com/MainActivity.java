package wolf.uit.quiztroll.com;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import basegameutils.GameHelper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.Leaderboards;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdView;

import java.io.IOException;

import com.ckt.android.kingtroll.R;
import wolf.uit.quiztroll.com.database.DBHelper;
import wolf.uit.quiztroll.com.database.UserDBHandler;
import wolf.uit.quiztroll.com.facebook.FBConstants;

public class MainActivity extends Activity implements
		basegameutils.GameHelper.GameHelperListener {
	private AdsHelper adsHelper;
	Button btLeaderBoard;
	Button btRating;
	Button btNew;
	public static DBHelper dbhelper;
	Typeface face;
	MediaPlayer mp;
	private UserDBHandler userDBHandler;
	protected GameHelper mHelper;
	private static final String LEADER_BOARD_ID = "CgkI9ufzjKkCEAIQAQ";
	protected int mRequestedClients;

	public MainActivity() {
		mRequestedClients = 1;
	}

	public void About() {
		startActivity(new Intent(this, About.class));
	}

	public void AddEvent() {
		this.btNew.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {

				MainActivity.this.play(0);
				MainActivity.this.OpenGame();
			}
		});
		this.btLeaderBoard.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				MainActivity.this.play(0);
				showLeaderBoard();
			}
		});
		this.btRating.setOnClickListener(new View.OnClickListener() {
			@SuppressLint({ "NewApi" })
			public void onClick(View paramView) {
				Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
				Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
				try {
					startActivity(goToMarket);
				} catch (ActivityNotFoundException e) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
				}
			}
		});
	}

	public void OpenGame() {
		startActivity(new Intent(this, LevelGame.class));
	}

	public void getForm() {
		this.face = Typeface.createFromAsset(getAssets(), "Arial.ttf");
		this.btNew = ((Button) findViewById(R.id.new_button));
		this.btLeaderBoard = ((Button) findViewById(R.id.btleaderboard));
		this.btRating = ((Button) findViewById(R.id.btRating));
		this.mp = MediaPlayer.create(this, R.raw.click);
		this.btNew.setTypeface(this.face, 1);
		this.btLeaderBoard.setTypeface(this.face, 1);
		this.btRating.setTypeface(this.face, 1);
	}

	@SuppressLint({ "NewApi" })
	public void onBackPressed() {
		super.onBackPressed();
	}

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		setContentView(R.layout.activity_main);
		getForm();

		this.dbhelper = new DBHelper(this);
		this.userDBHandler = new UserDBHandler(this);

		try {
			if (this.dbhelper.createDataBase()) {
				this.userDBHandler.open();
				this.userDBHandler.close();
			}
			AddEvent();
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
		getGameHelper();
        mHelper.setMaxAutoSignInAttempts(2);
        mHelper.setup(this);
	}

	public void onPause() {
		super.onPause();
	}

	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		mHelper.onStart(this);
	}

	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
		mHelper.onStop();
	}
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        mHelper.onActivityResult(requestCode, resultCode, data);
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
			mHelper = new GameHelper(this, mRequestedClients);
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