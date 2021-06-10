package com.wu.media.model;


public class CircleChoose {
    private CircleData.ReleaseVideoTypeListBean message;
    public CircleChoose(CircleData.ReleaseVideoTypeListBean message){
        this.message=message;
    }
    public CircleData.ReleaseVideoTypeListBean getMessage() {
        return message;
    }

    public void setMessage(CircleData.ReleaseVideoTypeListBean message) {
        this.message = message;
    }

}
