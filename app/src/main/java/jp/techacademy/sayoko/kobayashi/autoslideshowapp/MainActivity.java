package jp.techacademy.sayoko.kobayashi.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Cursor cursor = null;
    boolean csp = false;
    Timer mTimer;

    Button mNextToButton;
    Button mPreviousButton;
    Button mStartPauseButton;

    double mTimerSec = 0.0;


    private static final int PERMISSIONS_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //許可されている
                getContentsInfo();
            } else {
                //許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);

            }
            //Android 5系以下の場合
        } else {
            getContentsInfo();
        }
        final Handler mHandler = new Handler();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            mNextToButton = (Button) findViewById(R.id.nextTo_button);
            mNextToButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (csp == false) {
                        toastMake("画像へのアクセスが拒否されているためアプリを終了します");
                        finish();
                    } else {
                        if (cursor.moveToNext()) {
                            setImageView();
                        } else { cursor.moveToFirst();
                            setImageView();
                        }


                    }
                }
            });}
        else{mNextToButton = (Button) findViewById(R.id.nextTo_button);
            mNextToButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (csp == false) {
                        toastMake("画像へのアクセスが拒否されているためアプリを終了します");
                        finish();
                    } else {
                        if (cursor.moveToNext()) {
                            setImageView();
                        } else { cursor.moveToFirst();
                            setImageView();
                        }


                    }
                }
            });

        }

        mPreviousButton = (Button) findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (csp == false) {
                    toastMake("画像へのアクセスが拒否されているためアプリを終了します");
                    finish();
                } else {
                    if (cursor.moveToPrevious()) {
                        setImageView();

                    } else {

                        cursor.moveToLast();
                        setImageView();
                    }

                }
            }

        });


        mStartPauseButton = (Button) findViewById(R.id.start_pause_button);
        mStartPauseButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (cursor == null) {
                    toastMake("画像が読み込めません。");
                    finish();
                } else {
                    mStartPauseButton.setText("停止");
                    mNextToButton.setEnabled(false);
                    mPreviousButton.setEnabled(false);
                    if (mTimer == null) {
                        mTimer = new Timer();
                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                mTimerSec += 0.1;


                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (cursor.moveToNext()) {

                                        } else {

                                            cursor.moveToFirst();
                                        }
                                        setImageView();


                                    }
                                });

                            }
                        }, 100, 2000);


                    } else {
                        mTimer.cancel();
                        mTimer = null;
                        mStartPauseButton.setText("再生");
                        mNextToButton.setEnabled(true);
                        mPreviousButton.setEnabled(true);
                    }
                }
            }


        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo() {
        //画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, //データの種類
                null,//項目
                null,//フィルタ条件
                null,//フィルタ用パラメータ
                null//ソート
        );
    csp = true;


    }


    private void setImageView() {
        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageURI(imageUri);
    }


    private void toastMake(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }
}
