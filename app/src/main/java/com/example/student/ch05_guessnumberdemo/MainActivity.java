package com.example.student.ch05_guessnumberdemo;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button[] btns = new Button[12];
    private int[] btnId = {R.id.btn01, R.id.btn02, R.id.btn03, R.id.btn04, R.id.btn05, R.id.btn06, R.id.btn07, R.id.btn08, R.id.btn09,
            R.id.btnCls, R.id.btnOk, R.id.btnNewGame};
    private TextView tvMsg, tvScore;
    private ListView listView1;

    private int[] userGuess = {0, 0, 0, 0};
    private int[] answer = {0, 0, 0, 0};
    private int[] pool = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private String msg;
    private int inputCount;
    private int guessCount;

    private ArrayList<String> list = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;

    private MenuItem level_setting;
    private int index = 2;
    private int[] palyMax = {10000, 25, 15};

    private static final String FILE_NAME = "setting";
    private String hard_name, normal_name, easy_name;
    private int hard_score = 100000, normal_score = 100000, easy_score = 100000;
    private boolean sound_play = true;

    private MediaPlayer player;
    private Uri mediaPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewId();
        newGame();
        //Toast.makeText(MainActivity.this,"onCreate", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        hard_name = sharedPreferences.getString("hard_name", hard_name);
        normal_name = sharedPreferences.getString("normal_name", normal_name);
        easy_name = sharedPreferences.getString("easy_name", easy_name);
        hard_score = sharedPreferences.getInt("hard_score", hard_score);
        normal_score = sharedPreferences.getInt("normal_score", normal_score);
        easy_score = sharedPreferences.getInt("easy_score", easy_score);
        index = sharedPreferences.getInt("index", index);
        sound_play = sharedPreferences.getBoolean("sound_play", sound_play);
        //Toast.makeText(MainActivity.this,"onResume", Toast.LENGTH_SHORT).show();
        StringBuilder sb = new StringBuilder("紀錄：\n");
        if (easy_name != null) {
            sb.append("1. 易 [").append(String.format("%02d] ", easy_score)).append(easy_name).append("\n");
        }
        if (normal_name != null) {
            sb.append("2. 中 [").append(String.format("%02d] ", normal_score)).append(normal_name).append("\n");
        }
        if (hard_name != null) {
            sb.append("3. 難 [").append(String.format("%02d] ", hard_score)).append(hard_name).append("\n");
        }
        tvScore.setText(sb.toString());

        suondPlay();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(player!=null){
            player.release();
        }
    }

    private void suondPlay() {

        try {
            if(player==null){
                player = new MediaPlayer();
                mediaPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.backsound);
                player.setDataSource(getApplicationContext(), mediaPath);
            }
            if(sound_play) {
                player.prepare();
                player.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void soundStop() {
        player.stop();
    }

    private void newGame() {
        msg = "";
        userGuess = new int[]{0, 0, 0, 0};
        answer = new int[]{0, 0, 0, 0};
        pool = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
        inputCount = 0;
        guessCount = 0;

        int temp;
        for (int i = 0; i < answer.length; i++) {
            while (pool[(temp = (int) (Math.random() * 9 + 1)) - 1] == 1) {
            }
            answer[i] = temp;
            pool[temp - 1] = 1;
        }
//        tvMsg.setText("");
//        tvMsg.append(answer[0] + "");
//        tvMsg.append(answer[1] + "");
//        tvMsg.append(answer[2] + "");
//        tvMsg.append(answer[3] + "");
        btns[10].setEnabled(false);
        btns[10].setAlpha(0.5F);
        btns[9].setEnabled(true);
        btns[9].setAlpha(1.0F);
        setNumberButtonEnable(true);
        list.clear();
        arrayAdapter.notifyDataSetChanged();

    }

    private void setNumberButtonEnable(boolean boo) {
        for (int i = 0; i < 9; i++) {
            btns[i].setEnabled(boo);
            if (boo) {
                btns[i].setAlpha(1.0F);
            } else {
                btns[i].setAlpha(0.5F);
            }
        }
    }

    private void findViewId() {
        for (int i = 0; i < btns.length; i++) {
            btns[i] = (Button) findViewById(btnId[i]);
            btns[i].setOnClickListener(btnClickListener);
        }

        tvMsg = (TextView) findViewById(R.id.tvMsg);
        tvScore = (TextView) findViewById(R.id.tvScore);
        btns[10].setEnabled(false);
        btns[10].setAlpha(0.5F);

        listView1 = (ListView) findViewById(R.id.listView1);
        arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, list);
        listView1.setAdapter(arrayAdapter);

    }

    private View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int num = 0;
            switch (v.getId()) {
                case R.id.btnNewGame:
                    newGame();
                    break;
                case R.id.btnCls:
                    msg = "輸入4個號碼";
                    btns[10].setEnabled(false);
                    btns[10].setAlpha(0.5F);
                    setNumberButtonEnable(true);
                    tvMsg.setText(msg);
                    inputCount = 0;
                    break;
                case R.id.btnOk:
                    result();
                    break;
                default:
                    switch (v.getId()) {
                        case R.id.btn01:
                            num = 1;
                            break;
                        case R.id.btn02:
                            num = 2;
                            break;
                        case R.id.btn03:
                            num = 3;
                            break;
                        case R.id.btn04:
                            num = 4;
                            break;
                        case R.id.btn05:
                            num = 5;
                            break;
                        case R.id.btn06:
                            num = 6;
                            break;
                        case R.id.btn07:
                            num = 7;
                            break;
                        case R.id.btn08:
                            num = 8;
                            break;
                        case R.id.btn09:
                            num = 9;
                            break;
                    }
                    userInput(v, num);
            }
        }
    };

    private void userInput(View v, int num) {
        ((Button) v).setEnabled(false);
        ((Button) v).setAlpha(0.5F);
        userGuess[inputCount] = num;
        inputCount++;
        if (tvMsg.getText().toString().equals("輸入4個號碼")) {
            msg = "";
        }
        msg = msg + num;
        tvMsg.setText(msg);

        if (inputCount == 4) {
            setNumberButtonEnable(false);
            btns[10].setEnabled(true);
            btns[10].setAlpha(1.0F);
        }
    }

    private void result() {
        int a = 0, b = 0;
        msg = "輸入4個號碼";
        guessCount++;
        for (int i = 0; i < 4; i++) {
            if (userGuess[i] == answer[i]) {
                a++;
            } else if (pool[userGuess[i] - 1] == 1) {
                b++;
            }
        }
        //Toast.makeText(MainActivity.this, a+"A"+b+"B", Toast.LENGTH_SHORT).show();
        list.add(0, String.format("%02d. %d%d%d%d %dA%dB", guessCount, userGuess[0], userGuess[1], userGuess[2], userGuess[3], a, b));
        arrayAdapter.notifyDataSetChanged();
        if (a == 4) {
            msg = "猜對了!";
            btns[9].setEnabled(false);
            btns[9].setAlpha(0.5F);
            btns[10].setEnabled(false);
            btns[10].setAlpha(0.5F);
            setNumberButtonEnable(false);
            if ((index == 0 && guessCount < easy_score) || (index == 1 && guessCount < normal_score) || (index == 2 && guessCount < hard_score)) {
                setScore();
            }
        } else if (guessCount == palyMax[index]) {
            msg = "遊戲結束!";
            btns[9].setEnabled(false);
            btns[9].setAlpha(0.5F);
            btns[10].setEnabled(false);
            btns[10].setAlpha(0.5F);
            setNumberButtonEnable(false);
        } else {
            setNumberButtonEnable(true);
            btns[10].setEnabled(false);
            btns[10].setAlpha(0.5F);
        }
        inputCount = 0;
        tvMsg.setText(msg);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        level_setting = menu.findItem(R.id.level_setting);
        level_setting.setTitle(getResources().getStringArray(R.array.level)[index]);
        //Toast.makeText(MainActivity.this,"onCreateOptionsMenu", Toast.LENGTH_SHORT).show();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.level_setting:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("難易度設定")
                        .setIcon(R.mipmap.ic_launcher)
                        .setSingleChoiceItems(getResources().getStringArray(R.array.level), index, levelSettingListener)
                        .setNeutralButton("取消", null)
                        .setPositiveButton("確定", levelSettingListener)
                        .show();
                break;
            case R.id.music:
                sound_play = !sound_play;
                if(sound_play){
                    suondPlay();
                }else{
                    soundStop();
                }
                SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
                sharedPreferences.edit().putBoolean("sound_play", sound_play).commit();
        }

        return super.onOptionsItemSelected(item);
    }

    private DialogInterface.OnClickListener levelSettingListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which >= 0) {
                index = which;
            } else {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    level_setting.setTitle(getResources().getStringArray(R.array.level)[index]);
                    SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
                    sharedPreferences.edit().putInt("index", index).commit();
                }
            }
        }
    };

    private void setScore() {
        //自訂
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View scoreDialog = inflater.inflate(R.layout.score, null);
//        TextView tvScoreLevel = (TextView) scoreDialog.findViewById(R.id.tvScoreLevel);
//        tvScoreLevel.setText(getResources().getStringArray(R.array.level)[index]);
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("最佳成績：" + getResources().getStringArray(R.array.level)[index])
                .setView(scoreDialog)
                .setIcon(R.mipmap.ic_launcher)
                .setNegativeButton("取消", null)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText etScoreName = (EditText) scoreDialog.findViewById(R.id.etScoreName);
                        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
                        if (etScoreName.length() != 0) {
                            String name = etScoreName.getText().toString();
                            if (index == 0) {
                                sharedPreferences.edit().putString("easy_name", name).putInt("easy_score", guessCount).commit();
                            } else if (index == 1) {
                                sharedPreferences.edit().putString("normal_name", name).putInt("normal_score", guessCount).commit();
                            } else if (index == 2) {
                                sharedPreferences.edit().putString("hard_name", name).putInt("hard_score", guessCount).commit();
                            }
                        }
                        onResume();

                    }
                })
                .create();
        dialog.show();
    }
}
