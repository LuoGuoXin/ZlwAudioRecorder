package com.zlw.main.recorderlib;


import android.annotation.SuppressLint;
import android.content.Context;

import com.zlw.main.recorderlib.recorder.RecordConfig;
import com.zlw.main.recorderlib.recorder.RecordHelper;
import com.zlw.main.recorderlib.recorder.listener.RecordDataListener;
import com.zlw.main.recorderlib.recorder.listener.RecordFftDataListener;
import com.zlw.main.recorderlib.recorder.listener.RecordIngListener;
import com.zlw.main.recorderlib.recorder.listener.RecordResultListener;
import com.zlw.main.recorderlib.recorder.listener.RecordSoundSizeListener;
import com.zlw.main.recorderlib.recorder.listener.RecordStateListener;
import com.zlw.main.recorderlib.utils.Logger;

/**
 * @author zhaolewei on 2018/7/10.
 */
public class RecordManager {
    private static final String TAG = RecordManager.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private volatile static RecordManager instance;
    private RecordConfig recordConfig;

    private RecordManager() {
    }

    public static RecordManager getInstance() {
        if (instance == null) {
            synchronized (RecordManager.class) {
                if (instance == null) {
                    instance = new RecordManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        getRecordConfig().setRecordDir(context.getCacheDir().getAbsolutePath() + "/");
    }


    public void start() {
        Logger.i(TAG, "start...");
        RecordHelper.getInstance().start();
    }

    public void stop() {
        RecordHelper.getInstance().stop();
    }

    public void resume() {
        RecordHelper.getInstance().resume();
    }

    public void pause() {
        RecordHelper.getInstance().pause();
    }

    /**
     * 录音状态监听回调
     */
    public void setRecordStateListener(RecordStateListener listener) {
        RecordHelper.getInstance().setRecordStateListener(listener);
    }

    /**
     * 录音数据监听回调
     */
    public void setRecordDataListener(RecordDataListener listener) {
        RecordHelper.getInstance().setRecordDataListener(listener);
    }

    /**
     * 录音可视化数据回调，傅里叶转换后的频域数据
     */
    public void setRecordFftDataListener(RecordFftDataListener recordFftDataListener) {
        RecordHelper.getInstance().setRecordFftDataListener(recordFftDataListener);
    }

    /**
     * 录音文件转换结束回调
     */
    public void setRecordResultListener(RecordResultListener listener) {
        RecordHelper.getInstance().setRecordResultListener(listener);
    }

    /**
     * 录音音量监听回调
     */
    public void setRecordSoundSizeListener(RecordSoundSizeListener listener) {
        RecordHelper.getInstance().setRecordSoundSizeListener(listener);
    }

    /**
     * 录音时长监听回调
     */
    public void setRecordIngListener(RecordIngListener listener) {
        RecordHelper.getInstance().setRecordIngListener(listener);
    }


    public RecordConfig getRecordConfig() {
        if (recordConfig == null) {
            recordConfig = new RecordConfig();
        }
        return recordConfig;
    }

    public void setRecordConfig(RecordConfig recordConfig) {
        this.recordConfig = recordConfig;
    }

    /**
     * 获取当前的录音状态
     *
     * @return 状态
     */
    public RecordHelper.RecordState getState() {
        return RecordHelper.getInstance().getState();
    }

}
