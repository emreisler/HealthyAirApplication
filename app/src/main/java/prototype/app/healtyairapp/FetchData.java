package prototype.app.healtyairapp;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class FetchData extends AsyncTask<Void,Void,Void> {
    String data = "";

    HashMap<String,Integer> allDataMap = new HashMap<>();

    @Override
    protected Void doInBackground(Void... voids) {
        try{

            URL url = new URL(MapActivity.urlString);
            HttpURLConnection httpUrlConnection = (HttpURLConnection)url.openConnection();
            InputStream inputStream = httpUrlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            while(line != null){
                line = bufferedReader.readLine();
                data = data + line;
            }
        }
        catch(MalformedURLException e ){
            e.printStackTrace();
        }catch(IOException e ){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        String result = this.data.substring(13,14);
        //MapActivity.infoLabel.setText(this.data);
        if(result.equals("1")){
            MapActivity.notificationText = MapActivity.nameSurname + ". This place is harmful";
        }else if (result.equals("0")){
            MapActivity.notificationText = MapActivity.nameSurname + ". This place is safe";
        }
        else{
            MapActivity.notificationText = "Data can' t be retrieved. EXPLORE and GET AIR QUALITY INFO again";
        }

    }
}