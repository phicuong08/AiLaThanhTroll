package wolf.uit.quiztroll.com.facebook;

import com.facebook.model.GraphObject;

abstract interface EziOpenGraphObject extends GraphObject
{
  public abstract String getUrl();

  public abstract void setUrl(String paramString);

  public abstract String getId();

  public abstract void setId(String paramString);
}
