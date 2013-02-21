package com.itcast.db;


import weibo4j.http.AccessToken;
import android.content.Context;
import android.content.SharedPreferences;

public class TokenUtil {
//±£¥ÊAccessToken
public static void saveAccessToken(Context con,AccessToken at){
	SharedPreferences sp=con.getSharedPreferences("token", Context.MODE_WORLD_WRITEABLE);
	sp.edit().putString("tk", at.getAccessToken())
	    .putString("tks", at.getExpireIn())
	    .commit();
}
//∂¡»°AccessToken
public static AccessToken readAccessToken(Context con){
	SharedPreferences sp=con.getSharedPreferences("token", Context.MODE_WORLD_READABLE);
	String tk=sp.getString("tk", null);
	String tks=sp.getString("tks", null);
    
	if(tk!=null)
    {AccessToken at=new AccessToken(tk,tks);
     return at;	
    }
	return null;
 }
}
