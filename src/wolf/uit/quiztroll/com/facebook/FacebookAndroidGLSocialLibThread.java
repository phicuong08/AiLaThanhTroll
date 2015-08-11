package wolf.uit.quiztroll.com.facebook;

//console class
import wolf.uit.quiztroll.com.facebook.ConsoleAndroidGLSocialLib;

// Android imports
import android.util.Log;
import android.os.Handler;
import android.os.Looper;

public class FacebookAndroidGLSocialLibThread extends Thread
{
	public static Handler m_handler;

	@Override
	public void run() {
	  try {
		ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidSocial: FacebookAndroidSocialThread Looper.prepare()");
		Looper.prepare();
		
		ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidSocial: FacebookAndroidSocialThread m_handler = new Handler()");
		m_handler = new Handler();
	     
		ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidSocial: FacebookAndroidSocialThread Looper.loop();");
		Looper.loop();
		ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidSocial: FacebookAndroidSocialThread end loop");
	  } 
	  catch (Throwable t) {
		ConsoleAndroidGLSocialLib.Log_Major_Error("FacebookAndroidSocial: FacebookAndroidSocialThread halted due to an error"+t.toString());
	  }
	}
}
