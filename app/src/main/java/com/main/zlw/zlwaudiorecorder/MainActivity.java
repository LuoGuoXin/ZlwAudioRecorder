package com.main.zlw.zlwaudiorecorder;

import android.content.Intent;
import android.media.AudioFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zlw.main.recorderlib.RecordManager;
import com.zlw.main.recorderlib.recorder.RecordConfig;
import com.zlw.main.recorderlib.recorder.RecordHelper;
import com.zlw.main.recorderlib.recorder.listener.RecordFftDataListener;
import com.zlw.main.recorderlib.recorder.listener.RecordIngListener;
import com.zlw.main.recorderlib.recorder.listener.RecordResultListener;
import com.zlw.main.recorderlib.recorder.listener.RecordSoundSizeListener;
import com.zlw.main.recorderlib.recorder.listener.RecordStateListener;
import com.zlw.main.recorderlib.utils.Logger;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    Button btRecord;
    Button btStop;
    TextView tvState;
    TextView tvDuration;
    TextView tvSoundSize;
    RadioGroup rgAudioFormat;
    RadioGroup rgSimpleRate;
    RadioGroup tbEncoding;
    AudioView audioView;
    Spinner spUpStyle;
    Spinner spDownStyle;
    private String voicePath = "";

    private boolean isStart = false;
    private boolean isPause = false;
    final RecordManager recordManager = RecordManager.getInstance();
    private static final String[] STYLE_DATA = new String[]{"STYLE_ALL", "STYLE_NOTHING", "STYLE_WAVE", "STYLE_HOLLOW_LUMP"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.IsDebug = true;
        findView();
        initAudioView();
        initEvent();
        initRecord();
//        AndPermission.with(this)
//                .runtime()
//                .permission(new String[]{Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE,
//                        Permission.RECORD_AUDIO})
//                .start();
    }


    private void findView() {
        btRecord = findViewById(R.id.btRecord);
        btStop = findViewById(R.id.btStop);
        tvState = findViewById(R.id.tvState);
        tvSoundSize = findViewById(R.id.tvSoundSize);
        tvDuration = findViewById(R.id.tvDuration);
        rgAudioFormat = findViewById(R.id.rgAudioFormat);
        rgSimpleRate = findViewById(R.id.rgSimpleRate);
        tbEncoding = findViewById(R.id.tbEncoding);
        audioView = findViewById(R.id.audioView);
        spUpStyle = findViewById(R.id.spUpStyle);
        spDownStyle = findViewById(R.id.spDownStyle);
        btRecord.setOnClickListener(v -> doPlay());
        btStop.setOnClickListener(v -> doStop());
        findViewById(R.id.jumpTestActivity).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TestHzActivity.class)));
        findViewById(R.id.btPlay).setOnClickListener(v -> {
            if (!TextUtils.isEmpty(voicePath)) {
                MediaPlayerUtil.INSTANCE.play(voicePath, new MediaPlayerUtil.OnCallback() {

                    @Override
                    public void onStop() {
                    }

                    @Override
                    public void onStar() {
                    }

                    @Override
                    public void onPrepare() {
                    }

                    @Override
                    public void onCompletion() {

                    }

                    @Override
                    public void onError(@NotNull String msg) {

                    }
                });
            }
        });
    }

    private void initAudioView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, STYLE_DATA);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUpStyle.setAdapter(adapter);
        spDownStyle.setAdapter(adapter);
        spUpStyle.setOnItemSelectedListener(this);
        spDownStyle.setOnItemSelectedListener(this);
    }

    private void initEvent() {
        rgAudioFormat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbPcm:
                        recordManager.getRecordConfig().setFormat(RecordConfig.RecordFormat.PCM);
                        break;
                    case R.id.rbMp3:
                        recordManager.getRecordConfig().setFormat(RecordConfig.RecordFormat.MP3);
                        break;
                    case R.id.rbWav:
                        recordManager.getRecordConfig().setFormat(RecordConfig.RecordFormat.WAV);
                        break;
                    default:
                        break;
                }
            }
        });

        rgSimpleRate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb8K:
                        recordManager.getRecordConfig().setSampleRate(8000);
                        break;
                    case R.id.rb16K:
                        recordManager.getRecordConfig().setSampleRate(16000);
                        break;
                    case R.id.rb44K:
                        recordManager.getRecordConfig().setSampleRate(44100);
                        break;
                    default:
                        break;
                }
            }
        });

        tbEncoding.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb8Bit:
                        recordManager.getRecordConfig().setEncodingConfig(AudioFormat.ENCODING_PCM_8BIT);
                        break;
                    case R.id.rb16Bit:
                        recordManager.getRecordConfig().setEncodingConfig(AudioFormat.ENCODING_PCM_16BIT);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initRecord() {
        RecordManager.getInstance().init(this);
        initRecordEvent();
    }

    private void initRecordEvent() {
        recordManager.setRecordStateListener(new RecordStateListener() {
            @Override
            public void onStateChange(RecordHelper.RecordState state) {
                switch (state) {
                    case PAUSE:
                        tvState.setText("暂停中");
                        break;
                    case IDLE:
                        tvState.setText("空闲中");
                        break;
                    case RECORDING:
                        tvState.setText("录音中");
                        break;
                    case STOP:
                        tvState.setText("停止");
                        break;
                    case FINISH:
                        tvState.setText("录音结束");
                        tvSoundSize.setText("---");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onError(String error) {
                Logger.i(TAG, "onError %s", error);
            }
        });
        recordManager.setRecordIngListener(new RecordIngListener() {
            @Override
            public void onDuration(int duration) {
                tvDuration.setText("时长：" + duration);
            }
        });
        recordManager.setRecordSoundSizeListener(new RecordSoundSizeListener() {
            @Override
            public void onSoundSize(int soundSize) {
                tvSoundSize.setText(String.format(Locale.getDefault(), "声音大小：%s db", soundSize));
            }
        });
        recordManager.setRecordResultListener(new RecordResultListener() {
            @Override
            public void onResult(File result) {
                voicePath = result.getAbsolutePath();
                Toast.makeText(MainActivity.this, "录音文件： " + result.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        });
        recordManager.setRecordFftDataListener(new RecordFftDataListener() {
            @Override
            public void onFftData(byte[] data) {
                audioView.setWaveData(data);
            }
        });
    }


    private void doStop() {
        recordManager.stop();
        btRecord.setText("开始");
        isPause = false;
        isStart = false;
    }

    private void doPlay() {
        if (isStart) {
            recordManager.pause();
            btRecord.setText("开始");
            isPause = true;
            isStart = false;
        } else {
            if (isPause) {
                recordManager.resume();
            } else {
                recordManager.start();
            }
            btRecord.setText("暂停");
            isStart = true;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spUpStyle:
                audioView.setStyle(AudioView.ShowStyle.getStyle(STYLE_DATA[position]), audioView.getDownStyle());
                break;
            case R.id.spDownStyle:
                audioView.setStyle(audioView.getUpStyle(), AudioView.ShowStyle.getStyle(STYLE_DATA[position]));
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //nothing
    }
}
