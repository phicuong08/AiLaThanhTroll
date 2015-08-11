package wolf.uit.quiztroll.com.facebook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.AppEventsLogger;
import com.facebook.FacebookException;
import com.facebook.FacebookGraphObjectException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.SessionLoginBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
//import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint({"DefaultLocale"})
public class EziSocialManager
{
  private static final String TAG = "EziSocialManager";
  private static Activity m_activity = null;
  public static EziSocialManager s_instance;
  private static FacebookAndroidGLSocialLibThread m_Thread;
  private static final List<String> PUBLISH_PERMISSION = Arrays.asList("publish_actions");
  //for achievements
  private static String ACHIEVEMENT_url = "";;
  
	//for Post to wall, Post to friend's wall and post photo to wall without dialog
	private static String WALL_uid;
	private static String WALL_msg;
	private static String WALL_link;
	private static String WALL_title; 
	private static String WALL_pictureUrl; 
	private static String WALL_description; 
	private static String WALL_gameName;
	
	
	//for post photo to wall without dialog
	private static byte[] WALL_dataPic;
	
	
	//for OpenGraph
	private static String OPENGRAPH_appNamespace;
	private static String OPENGRAPH_action;
	private static String OPENGRAPH_objectUrl;
	private static String OPENGRAPH_objectType;
	
	//for update score
	private static int SCORE_score;
  
	//----------------------------------------------------------------------------------------------
	/// VARIABLES FOR PUBLISH PERMISSION
	//----------------------------------------------------------------------------------------------
  private enum PendingAction {
      PENDING_NONE,
      PENDING_POST_TO_WALL_WITHOUT_DIALOG,
      PENDING_POST_PHOTO_TO_WALL_WITHOUT_DIALOG,
      PENDING_POST_OPENGRAPH,
		PENDING_UPDATE_SCORE,
		PENDING_DELETE_SCORE,
      PENDING_GOT_ACHIEVEMENT,
      PENDING_RESET_ACHIEVEMENT
  }
  private static PendingAction pendingAction = PendingAction.PENDING_NONE;
  private static boolean s_isLoginRequest = false;
  private static boolean s_isLogoutRequest = false;
  private static boolean s_isRequestPermissionRequest = false;
  private static List<String> s_loginReadPermissions;
  private static String s_appId = null;
  
  private static Context m_context;
	//----------------------------------------------------------------------------------------------
	/// PUBLIC MEMBERS         
	//----------------------------------------------------------------------------------------------	
public EziSocialManager(  Activity activity, Context context)
{
	ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: FacebookAndroidGLSocialLib constructor");
	m_activity = activity;
    m_context = context;
	s_instance = this;
}

//SSO callback
public void onActivityResult(int requestCode, int resultCode, Intent data) {
	ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: onActivityResult");
	try
	{
		if(Session.getActiveSession()!=null)
			Session.getActiveSession().onActivityResult(s_instance.m_activity,requestCode, resultCode, data);
	}
	catch(Exception e)
	{
		ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: exception on onActivityResult: "+e.toString());
	}
}
//----------------------------------------------------------------------------------------------
/// LOGIN CALLBACK
//----------------------------------------------------------------------------------------------
private static Session.StatusCallback LoginCallback = new Session.StatusCallback() {
	@Override
	public void call(Session session, SessionState state, Exception exception) {
		onSessionStateChange(session, state, exception);
	}
};

	//----------------------------------------------------------------------------------------------
	/// PRIVATE MEMBERS
	//----------------------------------------------------------------------------------------------

	//callback function called when the session state is changed
private static void onSessionStateChange(Session session, SessionState state, Exception exception)
{
	if(exception != null)
	{
		ConsoleAndroidGLSocialLib.Log_Minor_Error("FacebookAndroidGLSocialLib: Error while authorizing:"+ exception.toString());
		if(pendingAction != PendingAction.PENDING_NONE || s_isLoginRequest || s_isRequestPermissionRequest)
		{
			pendingAction = PendingAction.PENDING_NONE;
			s_isLoginRequest = false;
			s_isRequestPermissionRequest = false;
			//nativeOnFBFailWithError("Facebook Error while authorizing:"+ exception.toString());
		}
	}
	else if(s_isLoginRequest)
	{
		ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: "+"Authorization call");
		if(state == SessionState.OPENED || state == SessionState.OPENED_TOKEN_UPDATED)
		{
			ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: "+"Authorization successful.");
			s_isLoginRequest = false;
			//nativeOnFBDialogDidComplete();
		}
		else if(state == SessionState.CLOSED || state == SessionState.CLOSED_LOGIN_FAILED)
		{
			ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: User Canceled Authorization");
			s_isLoginRequest = false;
			//nativeOnFBDialogDidNotComplete();
		}
	}
	else if(s_isLogoutRequest)
	{
		ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: "+"Logout called");
		if(state == SessionState.CLOSED)
		{
			s_isLogoutRequest = false;
			//nativeOnFBDialogDidComplete();
		}
	}
	else if(s_isRequestPermissionRequest)
	{
		s_isRequestPermissionRequest = false;
		ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: "+"Request permission call");
		if(state == SessionState.OPENED || state == SessionState.OPENED_TOKEN_UPDATED)
		{
			ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: "+"User allowed permissions");
			s_isRequestPermissionRequest = false;
			//nativeOnFBDialogDidComplete();
		}
		else if(state == SessionState.CLOSED || state == SessionState.CLOSED_LOGIN_FAILED)
		{
			ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: User denied permissions");
			s_isRequestPermissionRequest = false;
			//nativeOnFBDialogDidNotComplete();
		}
	}
	else if (pendingAction != PendingAction.PENDING_NONE)
	{
		if (state == SessionState.OPENED_TOKEN_UPDATED) 
		{
			//I requested some permissions => I must check if user accepted those permissions
			//hasPublishPermission(false);
		}
	}
}
  public void initWithActivity(String loginReadPermissions)
  {
		ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: Init()");
		//set the read permissions that will be requested at login
		s_loginReadPermissions = null;
		if(!loginReadPermissions.isEmpty())
			s_loginReadPermissions = Arrays.asList(loginReadPermissions.split(","));
		
		Session m_session = Session.getActiveSession();
		if(m_session==null)
		{
			ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: m_session==null && s_appId!=null");
			m_session = new Session.Builder(s_instance.m_context).build();
		
			ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: new Session");
			m_session.addCallback(LoginCallback);
			if(m_session.getState() == SessionState.CREATED_TOKEN_LOADED)
			{
				ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: CREATED_TOKEN_LOADED");
				//valid access token
				//m_session.openForRead(null);
				AccessToken at = AccessToken.createFromExistingAccessToken(m_session.getAccessToken(), m_session.getExpirationDate(), null, null, m_session.getPermissions() );
				m_session.open(at,LoginCallback);
			}
			
			Session.setActiveSession(m_session);
		}
		
		s_instance.s_appId = m_session.getApplicationId();
		ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: appID = "+s_instance.s_appId);
		
		m_Thread = new FacebookAndroidGLSocialLibThread();
		m_Thread.start();
  }

  private static Bitmap getBitmapFromAsset(Context context, String strName)
  {
    AssetManager assetManager = context.getAssets();

    Bitmap bitmap = null;
    try
    {
      InputStream istr = assetManager.open(strName);
      bitmap = BitmapFactory.decodeStream(istr);
    }
    catch (IOException e)
    {
      return null;
    }
    return bitmap;
  }

  public static boolean isFacebookSessionActive()
  {
    Session session = Session.getActiveSession();
    if (session != null)
    {
      if (session.isOpened())
      {
        ConsoleAndroidGLSocialLib.Log_Debug("EzisocialManager: Session is Opened ");
        return true;
      }
      ConsoleAndroidGLSocialLib.Log_Debug("EzisocialManager: Session is Closed ");
      return false;
    }
    ConsoleAndroidGLSocialLib.Log_Debug("EziSocialManager: Session is NULL");
    return false;
  }
  
public static void loginWithFacebook()
{
	//s_instance.m_activity.runOnUiThread(new Runnable() 
	FacebookAndroidGLSocialLibThread.m_handler.post(new Runnable()
	{
		@Override
		public void run() 
		{
			ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: Login()");
			Session m_session = Session.getActiveSession();
			if(m_session==null)
			{
				ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: m_session is null");
				m_session = new Session.Builder(s_instance.m_context).build();
				s_instance.s_appId = m_session.getApplicationId();
				Session.setActiveSession(m_session);
			}
			
			if(m_session != null)
			{
				ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: s_instance.m_session.state = "+m_session.getState());
				s_isLoginRequest = true;
				
				if(m_session.isOpened() || m_session.isClosed())
				{
					ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: re-create a Session");
					m_session = new Session.Builder(s_instance.m_context).build();
					m_session.addCallback(LoginCallback);
					Session.setActiveSession(m_session);
				}
				
				if(!m_session.isOpened() && !m_session.isClosed())
				{
					ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: permissions");
					Session.OpenRequest authRequest = new Session.OpenRequest(s_instance.m_activity);
					authRequest.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
					
					ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: statusCallback");
					authRequest.setCallback(LoginCallback);
					
					authRequest.setPermissions(s_instance.s_loginReadPermissions);
					
					m_session.openForRead(authRequest);
					ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: openForRead");
				}
				else
					Session.openActiveSession(s_instance.m_activity, true, LoginCallback);
			}
		}
	});
}

public static void logoutFromFacebook()
{
	ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: Logout_facade()");
	
	FacebookAndroidGLSocialLibThread.m_handler.post(new Runnable() 
	{
		@Override
		public void run() 
		{
			ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: Logout()");
			s_isLogoutRequest = true;
			Session session = Session.getActiveSession();
			if(session != null)
			{
				if (!session.isClosed()) 
					session.closeAndClearTokenInformation();
				ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: Logged out successfully");
			}
			else
			{
				ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: Error on logged out: Active session null");
			}
		}
	});
}

public static void postMessageOnWall(final String heading, final String caption, final String message, final String description, final String badgeIconURL, final String deeplinkURL)
{
	ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: PostToWall_facade()");
	
	//s_instance.m_activity.runOnUiThread(new Runnable() 
	//java.lang.Throwable: Warning: All WebView methods must be called on the UI thread. Future versions of WebView may not support use on other threads.
	FacebookAndroidGLSocialLibThread.m_handler.post(new Runnable() 
	{
		@Override
		public void run() {
			PostToWall(heading, caption, message, description, badgeIconURL,deeplinkURL);
		}
	});	
}
public static void PostToWall(String heading, String caption, String message, String description, String badgeIconURL, String deeplinkURL)
{
  Bundle params = new Bundle();

  params.putString("name", heading);
  params.putString("caption", caption);
  params.putString("message", message);
  params.putString("description", description);
  params.putString("picture", badgeIconURL);
  params.putString("link", deeplinkURL);

  final WebDialog requestsDialog = 
			(new WebDialog.FeedDialogBuilder(s_instance.m_context,Session.getActiveSession(),params))
			.setOnCompleteListener(new WebDialog.OnCompleteListener() 
			{
				@Override
				public void onComplete(Bundle values, FacebookException error)
				{
					ParseServerDialogResponse(values, error, "postMessageOnWall");
			}
		}).build();
			
	requestsDialog.show();
}

public static void ParseServerDialogResponse(Bundle values, FacebookException error, String functionName)
{
	ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: ParseServerDialogResponse");
	if (error != null) 
	{
		if (error instanceof FacebookOperationCanceledException) 
		{
			ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: User Canceled "+functionName);
			//nativeOnFBDialogDidNotComplete();
		} 
		else 
		{
			ConsoleAndroidGLSocialLib.Log_Minor_Error("FacebookAndroidGLSocialLib: Network Error while "+functionName+":"+ error.toString());
			//nativeOnFBFailWithError("Facebook Android SNS ERROR: "+error.toString());
		}
	} 
	else 
	{
		if(values == null || values.isEmpty())
		{
			ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: User Canceled "+functionName);
			//nativeOnFBDialogDidNotComplete();
		}
		else
		{
			String error_code = values.getString("error_code");
			if(error_code == null)
			{
				ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: "+ functionName+" successful. ");
				if(functionName.equals("sendGameRequestToFriends"))
				{
					ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: "+ values.toString());

				}
				//else
					//nativeOnFBDialogDidComplete();
			}
			else
			{
				String error_msg = values.getString("error_msg");
				if(error_msg != null)
				{
					ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: FacebookError while "+functionName+" error_code = "+error_code+" error_msg = "+error_msg);
					//nativeOnFBFailWithError("Facebook Android SNS ERROR: "+error_msg);
				}
				else
				{
					ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: FacebookError while "+functionName+" error_code = "+error_code);
					//nativeOnFBFailWithError("Facebook Android SNS ERROR: error_code = "+error_code);
				}
			}
		}
	}
}

//check if the active session has publish permission
private static void hasPublishPermission(final boolean isFirstTime) 
{
	FacebookAndroidGLSocialLibThread.m_handler.post(new Runnable() 
	{
		@Override
		public void run() 
		{
			Session session = Session.getActiveSession();
            if(session == null)
            {
				//nativeOnFBFailWithError("Active session null");
				ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: Error on logged out: Active session null");
				//nativeOnFBDialogDidNotComplete();
				pendingAction = PendingAction.PENDING_NONE;
				return;
            }
			
			Request.Callback callback = new Request.Callback()
			{
				@Override
				public void onCompleted(Response response) 
				{
					ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: onCompleted");
					if(response != null)
						ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: Received Response = " +response);
						
					//check if publish_actions is in the list of permissions from response
						//yes => call the pending request
						//no =>
								//if isFirstTime = true => request publish permission
								//else => error
					
					boolean foundPublishPermission = searchPermissionFromResponse(response, "publish_actions");
					
					if(foundPublishPermission)
					{
						//yes => call the pending request
						ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: yes => call the pending request");
						handlePendingAction();
					}
					else
					{
						//no =>
						if(isFirstTime)
						{
							ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: if isFirstTime = true => request publish permission");
							//if isFirstTime = true => request publish permission
							Session session = Session.getActiveSession();
							Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(s_instance.m_activity, PUBLISH_PERMISSION);
							session.requestNewPublishPermissions(newPermissionsRequest);
						}
						else
						{
							ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: else => error");
							//else => error
							//nativeOnFBDialogDidNotComplete();
							pendingAction = PendingAction.PENDING_NONE;
						}
					}
				}
			};
			String requestString = "me/permissions";
			String accessToken = getAccessToken();
			Bundle params = new Bundle();
			params.putString("access_token", accessToken);
			Request request = new Request(Session.getActiveSession(),requestString,params,HttpMethod.GET,callback);
			request.executeAsync();
		}
	});
}

public static String getAccessToken() {
	return Session.getActiveSession().getAccessToken();
}

public static boolean searchPermissionFromResponse(Response response, String permission)
{
	ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: searchPermissionFromResponse");
	if(response == null)
		return false;
	if(response.getError()!=null)
		return false;
	try
	{
		if(response.getGraphObject() == null)
			return false;
		JSONObject object = response.getGraphObject().getInnerJSONObject();
		if(object == null)
			return false;
		if(object.has("error"))
			return false;
		if(object.has("data")==false)
			return false;
		JSONArray dataArray = object.getJSONArray("data");
		if(dataArray == null)
			return false;
		if(dataArray.length()<=0)
			return false;
		
		JSONObject listOfPermissions = dataArray.optJSONObject(0);
		if(listOfPermissions == null)
			return false;
		
		if(listOfPermissions.has(permission) && listOfPermissions.getInt(permission)==1)
			return true;
		
		return false;
	}	
	catch(JSONException e)
	{
		return false;
	}	
	catch(Exception e)
	{
		return false;
	}
}


//if pendingAction is not PENDING_NONE, the saved request should be called
private static void handlePendingAction() 
{
    // These actions may re-set pendingAction if they are still pending, but we assume they
    // will succeed.

    switch (pendingAction)
    {
        case PENDING_POST_TO_WALL_WITHOUT_DIALOG:
            //call post to wall without dialog
			ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: "+"handlePendingAction: call PostToWallWithoutDialog");
			PostToWallWithoutDialog(WALL_msg, WALL_link, WALL_title, WALL_pictureUrl, WALL_description, WALL_gameName);
            break;
        case PENDING_POST_PHOTO_TO_WALL_WITHOUT_DIALOG:
            //call post photo to wall without dialog
			ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: "+"handlePendingAction: call PostPhotoToWallWithoutDialog");
			PostPhotoToWallWithoutDialog(WALL_dataPic, WALL_msg);
			break;
        case PENDING_POST_OPENGRAPH:
            //call opengraph
			ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: "+"handlePendingAction: call postOpenGraphAction");
			//postOpenGraphAction(OPENGRAPH_appNamespace, OPENGRAPH_action, OPENGRAPH_objectUrl, OPENGRAPH_objectType);
            break;
		case PENDING_UPDATE_SCORE:
            //call update score
			ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: "+"handlePendingAction: call updateScore");
			//updateScore(SCORE_score);
			break;
		case PENDING_DELETE_SCORE:
            //call delete score
			ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: "+"handlePendingAction: call deleteScore");
			//deleteScore();
			break;
        case PENDING_GOT_ACHIEVEMENT:
        {
            //call got achievement
            ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: "+"handlePendingAction: call gotAchievement");
            //gotAchievement(ACHIEVEMENT_url);
            break;
        }
        case PENDING_RESET_ACHIEVEMENT:
        {
            //call reset achievement
            ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: "+"handlePendingAction: call resetAchievement");
            //resetAchievement(ACHIEVEMENT_url);
            break;
        }
        default:
			break;
    }
}

public static void PostToWallWithoutDialog(final String title,  final String gameName,final String msg,final String description,  final String pictureUrl, final String link)
{
	ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: PostToWallWithoutDialog()");
	
	FacebookAndroidGLSocialLibThread.m_handler.post(new Runnable() 
	{
		@Override
		public void run() {
		
			Session session = Session.getActiveSession();
            if(session == null)
            {
				//nativeOnFBFailWithError("Active session null");
				ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: Error on logged out: Active session null");
				return;
            }
			//if pendingAction is PENDING_NONE -> it's the first time this func is called => check permissions
					//else call directly the request
			if (pendingAction == PendingAction.PENDING_NONE)
			{
				ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: check permissions");
				pendingAction = PendingAction.PENDING_POST_TO_WALL_WITHOUT_DIALOG;
				WALL_msg = msg;
				WALL_link = link;
				WALL_title = title;
				WALL_pictureUrl = pictureUrl;
				WALL_description = description; 
				WALL_gameName = gameName;
				// Check for publish permissions      
				hasPublishPermission(true);
				return;
			}
			
			pendingAction = PendingAction.PENDING_NONE;
	
			ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: PostToWallWithoutDialog() msg: " + msg);
			ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: PostToWallWithoutDialog() link: " + link);
			ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: PostToWallWithoutDialog() title: " + title);
			ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: PostToWallWithoutDialog() pictureUrl: " + pictureUrl);
			ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: PostToWallWithoutDialog() description: " + description);
			ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: PostToWallWithoutDialog() gameName: " + gameName);
			
			Bundle params = new Bundle();
			params.putString("app_id", s_instance.s_appId);
			params.putString("link", link);
			params.putString("name", title);
			params.putString("picture", pictureUrl);
			if(gameName.isEmpty())
				params.putString("caption", "by Gameloft");
			else
				params.putString("caption", gameName);
			params.putString("description", description);
			params.putString("message", msg);
			
			Request.Callback callback = new Request.Callback()
			{
				@Override
				public void onCompleted(Response response) 
				{
					ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: onCompleted");
					if(response != null)
						ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: Received Response = " +response);
					ParseServerResponse(response, "PostToWallWithoutDialog");
				}
			};
					
			Request request = new Request(session,"me/feed",params,HttpMethod.POST,callback);
			
			request.executeAsync();
		}
	});	
}


public static void PostPhotoToWallWithoutDialog(final byte[] dataPic, final String msg)
{
	ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: PostPhotoToWallWithoutDialog()");
	
	FacebookAndroidGLSocialLibThread.m_handler.post(new Runnable() 
	{
		@Override
		public void run() {
		
			Session session = Session.getActiveSession();
            if(session == null)
            {
				//nativeOnFBFailWithError("Active session null");
				ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: Error on logged out: Active session null");
				return;
            }
			//if pendingAction is PENDING_NONE -> it's the first time this func is called => check permissions
					//else call directly the request
			if (pendingAction == PendingAction.PENDING_NONE)
			{
				ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: check permissions");
				pendingAction = PendingAction.PENDING_POST_PHOTO_TO_WALL_WITHOUT_DIALOG;
				WALL_msg = msg;
				WALL_dataPic = dataPic;
				// Check for publish permissions      
				hasPublishPermission(true);
				return;
			}
			pendingAction = PendingAction.PENDING_NONE;
	
			if(msg != null)
				ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLi: PostToWallWithoutDialog() msg: " + msg);
			
            Bitmap image = BitmapFactory.decodeByteArray(dataPic, 0, dataPic.length);
			if(image==null)
				ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: Bitmap is nil");

            Bundle params = new Bundle();
            params.putParcelable("picture", image);
            params.putString("message", msg);

			Request.Callback callback = new Request.Callback()
			{
				@Override
				public void onCompleted(Response response) 
				{
					ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: onCompleted");
					if(response != null)
						ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: Received Response = " +response);
					ParseServerResponse(response, "PostPhotoToWallWithoutDialog");
				}
			};
					
			Request request = new Request(session,"me/photos",params,HttpMethod.POST,callback);
			
			request.executeAsync();
		}
	});	
}

//called whenever you get a response from a request and you want to check if you got an error or you want to parse the response
public static void ParseServerResponse(Response response, String functionName)
{
	ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: ParseServerResponse");
	if(response == null)
	{
		ConsoleAndroidGLSocialLib.Log_Minor_Error("FacebookAndroidGLSocialLib: Error in "+functionName+": response is null");
		//nativeOnFBFailWithError("Facebook Android SNS ERROR: response is null" );
		return;
	}
	if(response.getError()!=null)
	{
		if(response.getError().getErrorMessage()!=null)
		{
			ConsoleAndroidGLSocialLib.Log_Minor_Error("FacebookAndroidGLSocialLib: Error in "+functionName+" Request: "+response.getError().getErrorMessage() );
			//nativeOnFBFailWithError("Facebook Android SNS ERROR: "+response.getError().getErrorMessage() );
		}
		else
		{
			ConsoleAndroidGLSocialLib.Log_Minor_Error("FacebookAndroidGLSocialLib: Error in "+functionName);
			//nativeOnFBFailWithError("Facebook Android SNS ERROR" );
		}	
	}
	else
	{
		try
		{
			if(response.getGraphObject() == null)
			{
				ConsoleAndroidGLSocialLib.Log_Minor_Error("FacebookAndroidGLSocialLib: Error in "+functionName+": response.getGraphObject() is null");
				//nativeOnFBFailWithError("Facebook Android SNS ERROR: response.getGraphObject() is null");
				return;
			}
			JSONObject object = response.getGraphObject().getInnerJSONObject();
			if(object == null)
			{
				ConsoleAndroidGLSocialLib.Log_Minor_Error("FacebookAndroidGLSocialLib: Error in "+functionName+": esponse.getGraphObject().getInnerJSONObject() is null");
				//nativeOnFBFailWithError("Facebook Android SNS ERROR: esponse.getGraphObject().getInnerJSONObject() is null");
				return;
			}
			if(object.has("error") == false)
			{
				String db_Object=object.toString();
				nativeOnFBDataLoad(db_Object);
			}
			else
			{
				String error = object.getString("error");
				ConsoleAndroidGLSocialLib.Log_Minor_Error("FacebookAndroidGLSocialLib: Error in "+functionName+" Request: "  + error);
				//nativeOnFBFailWithError("Facebook Android SNS ERROR: "+error);
			}
		}	
		catch(JSONException e)
		{
			ConsoleAndroidGLSocialLib.Log_Minor_Error("FacebookAndroidGLSocialLib: Error when tokenizing json response: " + e.toString());
			//nativeOnFBFailWithError("Facebook Android SNS ERROR: "+e.toString());
		}	
		catch(Exception e)
		{
			ConsoleAndroidGLSocialLib.Log_Minor_Error("FacebookAndroidGLSocialLib: Error: " + e.toString());
			//nativeOnFBFailWithError("Facebook Android SNS ERROR: "+e.toString());
		}		
	}
}

private static void shareOpenGraphStory(long callback_address, String serverAddress, String namespcae, String objectType, String actionType, String title, String imageAddress, String description)
{
}

private static void getUserDetails(final String ids)
{
	ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: GetUserData()");
	if(ids != null)
		ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: GetUserData() ids: " + ids);
	
	FacebookAndroidGLSocialLibThread.m_handler.post(new Runnable() 
	{
		@Override
		public void run() 
		{
			String accessToken = getAccessToken();
			Bundle params = new Bundle();
			params.putString("access_token", accessToken);
			params.putString("ids", ids);
			params.putString("fields", "id,name,picture,gender");
			if(accessToken != null)
				ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: GetUserData, access_token: " + accessToken);
			
			Request.Callback callback = new Request.Callback()
			{
				@Override
				public void onCompleted(Response response) 
				{
					ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: onCompleted");
					if(response != null)
						ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: Received Response = " +response);
					ParseServerResponse(response, "GetUserData");
				}
			};
			Request request = new Request(Session.getActiveSession(),"",params,HttpMethod.GET,callback);
			
			request.executeAsync();
		}
	});
}

private static void getFriends()
{
	FacebookAndroidGLSocialLibThread.m_handler.post(new Runnable() 
	{
		@Override
		public void run() 
		{
			try
			{
				Request.GraphUserListCallback request = new Request.GraphUserListCallback()
				{
					/**
					 * The method that will be called when the request completes.
					 *
					 * @param users    the list of GraphObjects representing the returned friends, or null
					 * @param response the Response of this request, which may include error information if the request was unsuccessful
					 */
					@Override
					public void onCompleted(List<GraphUser> users, Response response)
					{
						ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: onCompleted");
						if(response!=null)
							ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: Received Response = " +response);
						ParseServerResponse(response, "GetFriends");
					}
				};
				Request.newMyFriendsRequest(Session.getActiveSession(),request).executeAsync();
			}
			catch(FacebookGraphObjectException e)
			{
				ConsoleAndroidGLSocialLib.Log_Minor_Error("FacebookAndroidGLSocialLib: FacebookGraphObjectException in GetFriends"+e.toString());
				//nativeOnFBFailWithError("Facebook Android SNS ERROR: "+e.toString());
			}
			catch(Exception e)
			{
				ConsoleAndroidGLSocialLib.Log_Minor_Error("FacebookAndroidGLSocialLib: Exception in GetFriends"+e.toString());
				//nativeOnFBFailWithError("Facebook Android SNS ERROR: "+e.toString());
			}
		}
	});  
}
public static void GetFriendsNotPlaying()
{
	FacebookAndroidGLSocialLibThread.m_handler.post(new Runnable() 
	{
		@Override
		public void run() 
		{
			String accessToken = getAccessToken();
			Bundle params = new Bundle();
			params.putString("access_token", accessToken);
			//params.putString("q", "SELECT uid FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me()) AND is_app_user = 0");
			if(accessToken != null)
				ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: GetFriendsNotPlaying, access_token: " + accessToken);
			
			Request.Callback callback = new Request.Callback()
			{
				@Override
				public void onCompleted(Response response) 
				{
					ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: onCompleted");
					if(response != null)
						ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: Received Response = " +response);
					ParseServerResponse(response, "GetFriendsNotPlaying");
				}
			};
			//Request request = new Request(Session.getActiveSession(),"/fql",params,HttpMethod.GET,callback);
			Request request = new Request(Session.getActiveSession(),"/me/invitable_friends",params,HttpMethod.GET,callback);
			
			request.executeAsync();
		}
	});
}


public static void GetFriendsInGame()
{
	FacebookAndroidGLSocialLibThread.m_handler.post(new Runnable() 
	{
		@Override
		public void run() 
		{
			String accessToken = getAccessToken();
			Bundle params = new Bundle();
			params.putString("access_token", accessToken);
			params.putString("q", "SELECT uid FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me()) AND is_app_user = 1");
			if(accessToken != null)
				ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: GetFriendsInGame, access_token: " + accessToken);
			
			Request.Callback callback = new Request.Callback()
			{
				@Override
				public void onCompleted(Response response) 
				{
					ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: onCompleted");
					if(response != null)
						ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: Received Response = " +response);
					ParseServerResponse(response, "GetFriendsInGame");
				}
			};
			Request request = new Request(Session.getActiveSession(),"/fql",params,HttpMethod.GET,callback);
			
			request.executeAsync();
		}
	});	
}

public static void GetFriendsData(final boolean getAllFriends, final boolean m_isPlaying, final int start, final int limit)
{
	FacebookAndroidGLSocialLibThread.m_handler.post(new Runnable() 
	{
		@Override
		public void run() 
		{
			String accessToken = getAccessToken();
			Bundle params = new Bundle();
			params.putString("access_token", accessToken);
			if(accessToken != null)
				ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: GetFriendsData, access_token: " + accessToken);
			
			String queryString = "SELECT uid,name,profile_update_time,sex,pic FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me())";
			if(getAllFriends == false)
			{
				queryString += " AND is_app_user = ";
				if(m_isPlaying == true)
					queryString += "1";
				else
					queryString += "0";
			}
			if(limit > 0)
				queryString += " LIMIT "+ limit + " OFFSET " + start;
			ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: GetFriendsData, queryString: " + queryString);
			params.putString("q", queryString);
			
			
			Request.Callback callback = new Request.Callback()
			{
				@Override
				public void onCompleted(Response response) 
				{
					ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: onCompleted");
					if(response != null)
						ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: Received Response = " +response);
					ParseServerResponse(response, "GetFriendsData");
				}
			};
			Request request = new Request(Session.getActiveSession(),"/fql",params,HttpMethod.GET,callback);
			
			request.executeAsync();
		}
	});
}
  private static void postAchievement(long callback_address, String achievementURL)
  {
  }

  private static Bitmap getBitmapFromAsset(String strName)
  {
    AssetManager assetManager = m_activity.getAssets();
    InputStream istr = null;
    try
    {
      istr = assetManager.open(strName);
    }
    catch (IOException localIOException)
    {
    }

    Bitmap bitmap = BitmapFactory.decodeStream(istr);
    return bitmap;
  }

  private static void postPhoto(long callback_address, String imageFilePath, String message)
  {
  }

  private static boolean isAcitvityLive()
  {
    return m_activity != null;
  }

  public static void deleteScore()
  {
		FacebookAndroidGLSocialLibThread.m_handler.post(new Runnable() 
		{
			@Override
			public void run() 
			{
				Session session = Session.getActiveSession();
              if(session == null)
              {
					//nativeOnFBFailWithError("Active session null");
					ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: Error on logged out: Active session null");
					return;
              }
			
				//if pendingAction is PENDING_NONE -> it's the first time this func is called => check permissions
						//else call directly the request
				if (pendingAction == PendingAction.PENDING_NONE)
				{
					ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: check permissions");
					pendingAction = PendingAction.PENDING_DELETE_SCORE;
					// Check for publish permissions    
					hasPublishPermission(true);
					return;
				}
				pendingAction = PendingAction.PENDING_NONE;
				
				Request.Callback callback = new Request.Callback()
				{
					@Override
					public void onCompleted(Response response) 
					{
						ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: onCompleted");
						if(response != null)
							ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: Received Response = " +response);
						ParseServerResponse(response, "deleteScore");
					}
				};
				String requestString = "me/scores";
				
				String accessToken = getAccessToken();
				Bundle params = new Bundle();
				params.putString("access_token", accessToken);
				Request request = new Request(Session.getActiveSession(),requestString,params,HttpMethod.DELETE,callback);
				request.executeAsync();
			}
		});	  
  }

  public static void postScore(final int score)
  {
		FacebookAndroidGLSocialLibThread.m_handler.post(new Runnable() 
		{
			@Override
			public void run() 
			{
				Session session = Session.getActiveSession();
              if(session == null)
              {
					//nativeOnFBFailWithError("Active session null");
					ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: Error on logged out: Active session null");
					return;
              }
				//if pendingAction is PENDING_NONE -> it's the first time this func is called => check permissions
						//else call directly the request
				if (pendingAction == PendingAction.PENDING_NONE)
				{
					ConsoleAndroidGLSocialLib.Log_Debug("FacebookAndroidGLSocialLib: check permissions");;
					pendingAction = PendingAction.PENDING_UPDATE_SCORE;
					SCORE_score = score;
					// Check for publish permissions    
					hasPublishPermission(true);
					return;
				}
				pendingAction = PendingAction.PENDING_NONE;
			
				Request.Callback callback = new Request.Callback()
				{
					@Override
					public void onCompleted(Response response) 
					{
						ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: onCompleted");
						if(response != null)
							ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: Received Response = " +response);
						ParseServerResponse(response, "updateScore");
					}
				};
				String requestString = "me/scores";
				
				String accessToken = getAccessToken();
				Bundle params = new Bundle();
				params.putString("access_token", accessToken);
				params.putInt("score", score);
				Request request = new Request(session,requestString,params,HttpMethod.POST,callback);
				request.executeAsync();
			}
		});
  }

  public static void getAppScores(final String appId)
  {
  	FacebookAndroidGLSocialLibThread.m_handler.post(new Runnable() 
  	{
  		@Override
  		public void run() 
  		{
  			Request.Callback callback = new Request.Callback()
  			{
  				@Override
					public void onCompleted(Response response) 
  				{
  					ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: onCompleted");
  					if(response != null)
  						ConsoleAndroidGLSocialLib.Log_Info("FacebookAndroidGLSocialLib: Received Response = " +response);
  					ParseServerResponse(response, "getAppScores");
  				}
  			};
  			String requestString;
  			if(appId == null || appId.isEmpty())
  				requestString = s_appId+"/scores";
  			else
  				requestString = appId+"/scores";
  			
  			String accessToken = getAccessToken();
  			Bundle params = new Bundle();
  			params.putString("access_token", accessToken);
  			Request request = new Request(Session.getActiveSession(),requestString,params,HttpMethod.GET,callback);
  			request.executeAsync();
  		}
  	});
  };
  
private static void getHighScores() {
	try
	{
		  FacebookAndroidGLSocialLibThread.m_handler.post(new Runnable()
		  {
		    public void run()
		    {
		      Session session = Session.getActiveSession();
		
		      if ((session != null) && (session.isOpened()))
		      {
		        String appID = EziSocialManager.s_appId;
		        String graphPath = "/" + appID + "/scores";
		
		        Request higScoreRequest = Request.newGraphPathRequest(session, graphPath, new Request.Callback()
		        {
		        
		          public void onCompleted(Response response)
		          {
		            FacebookRequestError fbError = response.getError();
		            if (fbError != null)
		            {
		              Log.e("EziSocialManager", "Got error " + fbError.getErrorMessage());
		              int errorCode = 653;
		              String errorString = fbError.toString();
		
		              if (fbError.getRequestStatusCode() == -1)
		              {
		                errorCode = 100;
		                errorString = "getFriends: Internet connection error";
		              }
		
		              int errorCodeFinal = errorCode;
		              String errorStringFinal = errorString;
		
		              /*EziSocialManager.mGLSurfaceView.queueEvent(new Runnable(this.val$callback_address, errorCodeFinal, errorStringFinal)
		              {
		                public void run()
		                {
		                  EziSocialManager.access$42(this.val$callback_address, this.val$errorCodeFinal, 
		                    this.val$errorStringFinal, "");
		                }
		              });*/
		              return;
		            }
		
		            GraphObject object = response.getGraphObject();
		            JSONArray resultArray = (JSONArray)object.getProperty("data");
		
		            int totalResults = resultArray.length();
		            String resultString = "";
		            for (int i = 0; i < totalResults; i++)
		            {
		              try
		              {
		                JSONObject innerGraphObject = resultArray.getJSONObject(i);
		                long score = innerGraphObject.getLong("score");
		                resultString = resultString + "score;" + score + ";";
		
		                JSONObject userGraphObject = innerGraphObject
		                  .getJSONObject("user");
		                String username = userGraphObject.getString("name");
		                resultString = resultString + "name;" + username + ";";
		
		                String userID = userGraphObject.getString("id");
		                resultString = resultString + "id;" + userID + ";";
		              }
		              catch (JSONException e)
		              {
		                e.printStackTrace();
		              }
		            }
		
		            //String highScoreString = resultString;
		            //EziSocialManager.mGLSurfaceView.queueEvent(new Runnable(this.val$callback_address, highScoreString)
		            //{
		              //public void run()
		            //  {
		                //EziSocialManager.access$42(this.val$callback_address, 405, 
		                //  "Got Score List - Success", this.val$highScoreString);
		             // }
		            //});
		          }
		        });
		        Request.executeBatchAsync(new Request[] { 
		          higScoreRequest });
		      }
		    }
		  });
	}
	catch (Exception e)
	{
	      //EziSocialManager.access$42(this.val$callback_address, 404, 
	      //  "Got Error while fetching score list", "");
	}
}
private static void sendRequestToFriends(String message)
{
}
public static native void nativeFBSessionRequestComplete(long paramLong, int paramInt, String paramString1, String paramString2);

public static native void nativeFBMessageRequestComplete(long paramLong, int paramInt, String paramString);

public static native void nativeFBPageLikeRequestComplete(long paramLong, int paramInt, String paramString);

public static native void nativeFBFriendsRequestComplete(long paramLong, int paramInt, String paramString1, String paramString2);

public static native void nativeFBUserDetailsRequestComplete(long paramLong, int paramInt, String paramString1, String paramString2);

public static native void nativeShareDialogComplete(long paramLong, int paramInt, String paramString);

public static native void nativeFBOGRequestComplete(long paramLong, int paramInt, String paramString);

public static native void nativeFBHighScoreRequestComplete(long paramLong, int paramInt, String paramString1, String paramString2);

public static native void nativeSendMailRequestComplete(long paramLong, int paramInt);

public static native void nativeRequestSentCallback(long paramLong, int paramInt, String paramString1, String paramString2);

public static native void nativeFacebookRequestReceived(long paramLong, int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7);

public static native void nativePhotoPostCallback(long paramLong, int paramInt, String paramString);

public static native void nativeIncomingRequestCallback(long paramLong, int paramInt, String paramString1, String paramString2);

public static native void nativeAchievementCallback(long paramLong, int paramInt, String paramString);
public  static native void nativeOnFBDataLoad(String data);

}