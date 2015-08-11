package wolf.uit.quiztroll.com.facebook;

// Android imports
import android.util.Log;

public class ConsoleAndroidGLSocialLib 
{	
	public static final int SOCIALLIB_LOG_LEVEL_DEBUG	= 0;
	public static final int SOCIALLIB_LOG_LEVEL_FATAL	= 1;
	public static final int SOCIALLIB_LOG_LEVEL_MAJOR	= 2;
	public static final int SOCIALLIB_LOG_LEVEL_MINOR	= 3;
	public static final int SOCIALLIB_LOG_LEVEL_INFO	= 4;
	public static final int SOCIALLIB_LOG_LEVEL_VERBOSE	= 5;
	
	public ConsoleAndroidGLSocialLib()
	{
	}
	
	public static void Print(int logLevel, String msg)
	{
		switch(logLevel)
		{
		case SOCIALLIB_LOG_LEVEL_DEBUG:
			Log.d("facebook_DEBUG", msg);
			break;
		case SOCIALLIB_LOG_LEVEL_FATAL:
			Log.wtf("facebook_FATAL", msg);
			break;
		case SOCIALLIB_LOG_LEVEL_MAJOR:
			Log.e("facebook_MAJOR", msg);
			break;
		case SOCIALLIB_LOG_LEVEL_MINOR:
			Log.w("facebook_MINOR", msg);
			break;
		case SOCIALLIB_LOG_LEVEL_INFO:
			Log.i("facebook_INFO", msg);
			break;
		case SOCIALLIB_LOG_LEVEL_VERBOSE:
			Log.v("facebook_VERBOSE", msg);
			break;
		default:
			break;
		}
	}
	public static void Log_Debug(String msg)
	{
		Print(SOCIALLIB_LOG_LEVEL_DEBUG, msg);
	}

	public static void Log_Fatal_Error(String msg)
	{
		Print(SOCIALLIB_LOG_LEVEL_FATAL, msg);
	}

	public static void Log_Major_Error(String msg)
	{
		Print(SOCIALLIB_LOG_LEVEL_MAJOR, msg);
	}

	public static void Log_Minor_Error(String msg)
	{
		Print(SOCIALLIB_LOG_LEVEL_MINOR, msg);
	}

	public static void Log_Info(String msg)
	{
		Print(SOCIALLIB_LOG_LEVEL_INFO, msg);
	}

	public static void Log_Verbose(String msg)
	{
		Print(SOCIALLIB_LOG_LEVEL_VERBOSE, msg);
	}
}
