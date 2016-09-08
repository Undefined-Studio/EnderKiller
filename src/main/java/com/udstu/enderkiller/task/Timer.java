package com.udstu.enderkiller.task;

import com.udstu.enderkiller.task.implement.TimerCallBack;

/**
 * Created by czp on 16-9-8.
 * 定时器
 */
public class Timer implements Runnable {
    private TimerCallBack timerCallBack = null;

    public Timer(TimerCallBack timerCallBack) {
        this.timerCallBack = timerCallBack;
    }

    @Override
    public void run() {
        timerCallBack.timerCallBack();
    }
}
