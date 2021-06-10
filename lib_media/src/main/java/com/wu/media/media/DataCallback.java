package com.wu.media.media;


import com.wu.media.media.entity.Folder;

import java.util.ArrayList;

public interface DataCallback {
    void onData(ArrayList<Folder> list);
}
