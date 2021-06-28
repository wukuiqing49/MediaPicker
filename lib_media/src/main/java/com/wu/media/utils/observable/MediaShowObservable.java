package com.wu.media.utils.observable;

import com.wu.media.media.entity.Media;

import java.util.Observable;

/**
 * @author wkq
 * @date 2021年06月28日 14:47
 * @des
 */

public class MediaShowObservable extends Observable {
    static MediaShowObservable instance;

    public static MediaShowObservable getInstance() {
        synchronized (MediaSelectStateObservable.class) {
            if (instance == null) {
                instance = new MediaShowObservable();
            }
        }
        return instance;
    }

    public void setMediaShowInfo(MediaShowInfo mediaShowInfo) {

        setChanged();
        notifyObservers(mediaShowInfo);
    }

   public static class MediaShowInfo {

       private String filePath;
       private int type;

       public String getFilePath() {
           return filePath;
       }

       public void setFilePath(String filePath) {
           this.filePath = filePath;
       }

       public int getType() {
           return type;
       }

       public void setType(int type) {
           this.type = type;
       }
   }
}
