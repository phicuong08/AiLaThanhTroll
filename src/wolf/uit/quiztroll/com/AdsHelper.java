package wolf.uit.quiztroll.com;

import android.app.Activity;
import android.util.Log;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.InterstitialAd;

public class AdsHelper
{
  private static final String LOG_TAG = "Ads";
  private Activity _activity;
  private AdRequest adRequest;
  private InterstitialAd interstitialAd;

  public AdsHelper(Activity paramActivity)
  {
    this._activity = paramActivity;
  }

  private String getErrorReason(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return "";
    case 0:
      return "Internal error";
    case 1:
      return "Invalid request";
    case 2:
      return "Network Error";
    case 3:
    }
    return "No fill";
  }

  public void loadInterstitial(String paramString, boolean paramBoolean)
  {
    if ((this.interstitialAd != null) && (this.interstitialAd.isLoaded()))
      return;
    this.interstitialAd = new InterstitialAd(this._activity);
    this.interstitialAd.setAdUnitId(paramString);
    
    this.interstitialAd.setAdListener(new AdListener()
    {
      public void onAdFailedToLoad(int paramInt)
      {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = getErrorReason(paramInt);
        Log.d("Ads", String.format("onAdFailedToLoad (%s)", arrayOfObject));
      }

      public void onAdLoaded()
      {
    	  showInterstitial();
      }
    });
	this.adRequest = new AdRequest.Builder().build();
    this.interstitialAd.loadAd(this.adRequest);
  }

  public void showInterstitial()
  {
    if (this.interstitialAd.isLoaded())
    {
      this.interstitialAd.show();
      return;
    }
    Log.d("Ads", "Interstitial ad was not ready to be shown.");
  }
}