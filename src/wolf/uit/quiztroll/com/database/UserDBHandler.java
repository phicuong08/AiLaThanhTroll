package wolf.uit.quiztroll.com.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class UserDBHandler
{
  private static final String DATABASE_TABLE = "tablecauhoi";
  private static final String DATABASE_TABLE2 = "tablecauhoilevel2";
  private SQLiteDatabase database;
  private DBHelper dbhelper;
  private byte[] filesBytes;
  private byte[] kq;

  public UserDBHandler(Context paramContext)
  {
    this.dbhelper = new DBHelper(paramContext);
  }

  @SuppressLint({"NewApi"})
  private Question cursorToQuestion(Cursor paramCursor)
    throws SQLException
  {
    Question localQuestion = new Question();
    localQuestion.setId(Integer.parseInt(paramCursor.getString(0)));
    String str1;
    while (true)
      try
      {
        byte[] arrayOfByte3 = Base64.decode(paramCursor.getString(1), 0);
        this.kq = MyCipher.decodeFile(MyCipher.generateKey("oldwolfuitquizztrollkinh091888710201664605405"), arrayOfByte3);
        new String(this.kq, "UTF-8");
        continue;
      }
      catch (Exception localException1)
      {
        localException1.printStackTrace();
        String str4="";
		try {
			str4 = new String(this.kq, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        str1 = str4;
        try
        {
          byte[] arrayOfByte1 = Base64.decode(paramCursor.getString(7), 0);
          this.kq = MyCipher.decodeFile(MyCipher.generateKey("oldwolfuitquizztrollkinh091888710201664605405"), arrayOfByte1);
          String str2 = new String(this.kq, "UTF-8");
          String str3 = str1;
          localQuestion.setquestion(null);
          localQuestion.setA(paramCursor.getString(2));
          localQuestion.setB(paramCursor.getString(3));
          localQuestion.setC(paramCursor.getString(4));
          localQuestion.setD(paramCursor.getString(5));
          localQuestion.setAnswer(str3);
          localQuestion.setExplan(str2);
          return localQuestion;
        }
        catch (Exception localException3)
        {
        }
      }
  }

  public void close()
    throws SQLException
  {
    if (this.database != null)
      this.database.close();
    this.dbhelper.close();
  }

  public long count()
    throws SQLException
  {
    open();
    long l = DatabaseUtils.queryNumEntries(this.database, "tablecauhoi");
    close();
    return l;
  }

  public Question cursorToQuestion2(Cursor paramCursor)
    throws SQLException
  {
    Question localQuestion = new Question();
    localQuestion.setId(Integer.parseInt(paramCursor.getString(0)));
    localQuestion.setquestion(paramCursor.getString(1));
    localQuestion.setA(paramCursor.getString(2));
    localQuestion.setB(paramCursor.getString(3));
    localQuestion.setC(paramCursor.getString(4));
    localQuestion.setD(paramCursor.getString(5));
    localQuestion.setAnswer(paramCursor.getString(6));
    localQuestion.setExplan(paramCursor.getString(7));
    return localQuestion;
  }

  public long emptyRecord()
  {
    open();
    long l = this.database.delete("tablecauhoi", null, null);
    close();
    return l;
  }

  public ArrayList<Question> getListQuestion(int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    String str = "0, " + paramInt;
    Cursor localCursor = this.database.query("tablecauhoi", null, null, null, null, null, "random()", str);
    if (localCursor.moveToFirst())
      do
        localArrayList.add(cursorToQuestion2(localCursor));
      while (localCursor.moveToNext());
    localCursor.close();
    return localArrayList;
  }

  public ArrayList<Question> getListQuestion_level2(int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    String str = "0, " + paramInt;
    Cursor localCursor = this.database.query("tablecauhoilevel2", null, null, null, null, null, "random()", str);
    if (localCursor.moveToFirst())
      do
        localArrayList.add(cursorToQuestion2(localCursor));
      while (localCursor.moveToNext());
    localCursor.close();
    return localArrayList;
  }

  public void insert(Question paramQuestion)
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("cauhoi", paramQuestion.getquestion());
    localContentValues.put("cau_a", paramQuestion.getA());
    localContentValues.put("cau_b", paramQuestion.getB());
    localContentValues.put("cau_c", paramQuestion.getC());
    localContentValues.put("cau_d", paramQuestion.getD());
    localContentValues.put("dapan", paramQuestion.getAnswer());
    localContentValues.put("giaithich", paramQuestion.getExplan());
    this.database.insert("table1", null, localContentValues);
  }

  public void mahoa()
  {
  }

  public void open()
    throws SQLException
  {
    this.database = this.dbhelper.getWritableDatabase();
  }
}