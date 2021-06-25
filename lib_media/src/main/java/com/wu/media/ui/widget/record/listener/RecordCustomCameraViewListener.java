package com.wu.media.ui.widget.record.listener;

/**
 * @author wkq
 * @date 2021年06月25日 13:13
 * @des
 */
public interface RecordCustomCameraViewListener {
    void takePicture(String filePath);

    void takeVideo(String videoPath);
}
