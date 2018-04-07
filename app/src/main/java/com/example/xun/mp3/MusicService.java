package com.example.xun.mp3;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

import java.io.IOException;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    private MediaPlayer player;
    private Timer timer;
    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
       return new MusicControl();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //创建音乐播放器对象
        player= new MediaPlayer();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //停止播放
        player.stop();
        //释放空间
        player.release();
        //设置为空
        player=null;
    }
    public void play(){
        try {
            if (player==null){
                player=new MediaPlayer();
            }
            //重置
            player.reset();
            //获取音乐文件
            player.setDataSource("sdcard/music.mp3");
            //准备播放
            player.prepare();
            //开始播放
            player.start();
            //开始计时
            addTime();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    //暂停播放音乐
    public void pausePlay() {

        player.pause();
    }

    //继续播放音乐
    public void continuePlay() {

        player.start();
    }
    class MusicControl extends Binder implements MusicInterface{
        @Override
        public void play() {
            MusicService.this.play();//内部类调用外部类的用法；
        }

        @Override
        public void pausePlay() {
            MusicService.this.pausePlay();

        }

        @Override
        public void continuePlay() {
            MusicService.this.continuePlay();
        }

        @Override
        public void seekTo(int progress) {
            MusicService.this.seekTo(progress);
        }
    }
    //设置音乐的播放位置
    public void seekTo(int progress) {

        player.seekTo(progress);
    }
    public void addTime(){
        if(timer==null){
            timer=new Timer();
            timer.schedule(new TimerTask() {
                //执行计时任务
                @Override
                public void run() {
                        //获取歌曲总时间
                        int duration=player.getDuration();
                        //获取歌曲的当前播放进度
                        int currentPosition=player.getCurrentPosition();
                    //创建消息对象
                    Message msg=MainActivity.handler.obtainMessage();
                    //将音乐的播放进度封装至消息对象中
                    Bundle bundle = new Bundle();
                    bundle.putInt("duration", duration);
                    bundle.putInt("currentPosition", currentPosition);
                    msg.setData(bundle);
                    //将消息发送到主线程的消息队列
                    MainActivity.handler.sendMessage(msg);
                }
            },
                    //开始计时任务后的5毫秒，第一次执行run方法，以后每500毫秒执行一次
                    5, 500);

        }
    }

}
