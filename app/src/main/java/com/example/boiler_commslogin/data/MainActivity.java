package com.example.boiler_commslogin.data;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boiler_commslogin.R;
import com.example.boiler_commslogin.createpost.CreatePostActivity;
import com.example.boiler_commslogin.ui.login.EditUserProfile;
import com.example.boiler_commslogin.ui.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<String> username = new ArrayList<>();
    ArrayList<String> topic = new ArrayList<>();
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> body = new ArrayList<>();
    ArrayList<String> image = new ArrayList<>();
    ArrayList<String> time = new ArrayList<>();

    /*
    String[] username = {"Chirag", "Eli", "Nick", "Mitch"};
    String[] title = {"CS", "Math", "Purdue", "Covid"};
    String[] topic = {"classes", "classes", "College", "College"};
    String[] body = {"skjadfngj sdkofnsa", " khjbdafjkabskvajdnv", "dksajfbjkadbngv", "dkfjnajgnoiasdng"};
    String[] time = {"12:345" , "324234", "234234" , "12:33 am"};*/

    public class LoadUserCredentialsPost extends AsyncTask {
        //private TextView statusField,roleField;
        private Context context;
        String userCredentials;
        //flag 0 means get and 1 means post.(By default it is get.)
        public LoadUserCredentialsPost(Context context) {
            this.context = context;
        }
        public String getUserCredentials(){
            return userCredentials;
        }
        @Override
        protected Object doInBackground(Object[] objects) {
            URL url = null;
            String userID = (String)objects[0];
            try {
                url = new URL("http://10.0.2.2:63343/PHP_TEST2BOYS/getPost.php?q=" + objects[0].toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            //Log.d("url", url.toString());
            HttpURLConnection con = null;
            try {
                con = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                con.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            int status = 0;
            BufferedReader in = null;
            StringBuilder content = new StringBuilder();
            try {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                while (true) {
                    inputLine = in.readLine();
                    if(inputLine == null){
                        break;
                    }
                    content.append(inputLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Log.d("initial",content.toString());
            try {
                if(in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            userCredentials = content.toString();
            return content.toString();
        }
        protected void onPostExecute(String result){
            userCredentials = result;
        }
    }
    private void loadIntoRecyclerView(String json) throws JSONException {

        JSONArray jsonArray = new JSONArray(json);
        //String[] heroes = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            username.add(getIntent().getStringExtra("USERNAME"));
            topic.add(obj.getString("topicName"));
            title.add(obj.getString("postName"));
            time.add(obj.getString("postDate"));
            image.add(obj.getString("postImage"));
            //Log.d("image", image.get(i).toString());
            body.add(obj.getString("postText"));
            //if (i == jsonArray.length() - 1) {
            //    if (time.get(i).charAt(time.get(i).length() - 1) != ']') {
            //        time.set(i, time.get(i) + "00:12}]");
            //    }
            //}
        }
        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, heroes);
        //listView.setAdapter(arrayAdapter);


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setContentView(R.layout.activity_login);

        recyclerView = findViewById(R.id.recyclerView);
        String str_result = null;
        try {
            String userID = getIntent().getStringExtra("USERID");
            str_result = (String) new LoadUserCredentialsPost(this).execute(userID).get(2000, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        /*try {
            JSONObject reader = new JSONObject(str_result);
            email  = reader.getString("email");
            password = reader.getString("PASSWORD");
            firstName = reader.getString("firstName");
            lastName = reader.getString("lastName");
            img = reader.getString("profilePic");
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        //Log.d("POSTING", str_result);
        /*username.add("Chirag");
        topic.add("Purdue");
        title.add("asdf");
        time.add("asdasd");
        image.add("adkjgnvjksangjklasdnfblovasflkjb");
        body.add("adfnaodnvioav");*/
        try {
            loadIntoRecyclerView(str_result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyAdapter myAdapter = new MyAdapter(this, username, topic, title, body, image, time);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final Button settings = findViewById(R.id.settings);
        final Button logout = findViewById(R.id.logout);
        final Button createPost = findViewById(R.id.createPost);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_editprofile);
                Intent intent = new Intent(getApplicationContext(), EditUserProfile.class);
                intent.putExtra("USERID", getIntent().getStringExtra("USERID"));
                intent.putExtra("PASSWORD", getIntent().getStringExtra("PASSWORD"));
                startActivity(intent);
            }
        });

        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_createpost);
                Intent intent = new Intent(getApplicationContext(), CreatePostActivity.class);
                intent.putExtra("USERID", getIntent().getStringExtra("USERID"));
                intent.putExtra("USERNAME", getIntent().getStringExtra("USERNAME"));
                intent.putExtra("PASSWORD", getIntent().getStringExtra("PASSWORD"));
                startActivity(intent);
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("Logout?");
        builder.setMessage("This will bring you back to the login page.");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setContentView(R.layout.activity_login);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Canceled Logout", Toast.LENGTH_LONG).show();
            }
        });
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(-1).setVisibility(View.VISIBLE);
                dialog.getButton(-2).setVisibility(View.VISIBLE);
            }
        });
    }
}