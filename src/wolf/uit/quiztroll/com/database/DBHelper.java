package wolf.uit.quiztroll.com.database;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import com.ckt.android.kingtroll.R;

public class DBHelper extends SQLiteOpenHelper
{
  private static final String DATABASE_NAME = "databasecauhoi.db";
  private static final int DATABASE_VERSION = 4;
  private Context context;
  private String db_path = "";

  public DBHelper(Context paramContext)
  {
    super(paramContext, "databasecauhoi.db", null, 4);
    this.context = paramContext;
    this.db_path = paramContext.getDatabasePath(paramContext.getResources().getString(R.string.database_name)).getPath();
  }

  private void copyDataBase()
    throws IOException
  {
    InputStream localInputStream = this.context.getAssets().open("databasecauhoi.db");
    FileOutputStream localFileOutputStream = new FileOutputStream(this.db_path);
    byte[] arrayOfByte = new byte[1024];
    while (true)
    {
      int i = localInputStream.read(arrayOfByte);
      if (i <= 0)
      {
        localFileOutputStream.flush();
        localFileOutputStream.close();
        localInputStream.close();
        return;
      }
      localFileOutputStream.write(arrayOfByte, 0, i);
    }
  }

  private boolean databaseExist()
  {
    return new File(this.db_path).exists();
  }

  public boolean createDataBase()
    throws IOException
  {
    Log.d("Database is existing", String.valueOf(databaseExist()));
    boolean bool = databaseExist();
    if (!databaseExist())
      getReadableDatabase();
    try
    {
      copyDataBase();
      return true;
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      localNotFoundException.printStackTrace();
      return bool;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return bool;
  }

  public void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
  }

  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    if (paramInt2 > paramInt1)
    {
      File localFile = new File(this.db_path);
      if (localFile.exists())
        localFile.delete();
    }
    try
    {
      copyDataBase();
      return;
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      localNotFoundException.printStackTrace();
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
}