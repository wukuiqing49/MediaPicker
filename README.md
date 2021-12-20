## 介绍
  由于三方库更新慢,以及出现各种各样的机型兼容问题(大图,文件格式,华为加载慢,图片旋转,高版本URI兼容问题),项目
中被喷无数决定自力更生做一个相册库.本项目基于公司项目需求做的定制,如有使用可以再自行定制页面以及逻辑.

注意
  由于公司项目相机模块运用的是七牛视频库做的视频拍摄代码不方便放出,本项目自定义了一个项目拍摄逻辑(注意部分手机兼容问题)

* Android 10 版本兼容
* 单选/多选
* 支持GIf格式图片
* 视频录制
* 图片拍摄
* 裁剪(页面过于简略,可重新定制)
* 异常格式处理(bm,btff)
* 放大缩小预览
* 二维码识别(效率问题待定)
 
#### 引用

  implementation project(path: ':lib_media')// 本地引用

#### 效果
<p>
<img src="imgs/device_1.gif" width="40%">
</p>


#### 方法介绍

##### 1.打开相册
```
new ImagePicker
.Builder()							
.maxNum(9)
.setSelectGif(true)
.maxImageSize(25 * 1024 * 1024)
.maxVideoSize(100 * 1024 * 1024)
.isReturnUri(AndroidQUtil.isAndroidQ())
.selectMode(PickerConfig.PICKER_IMAGE_VIDEO)
.defaultSelectList(new ArrayList<Media>())
.needCamera(true)
.builder()
.start(this, PickerConfig.PICKER_IMAGE, PickerConfig.DEFAULT_RESULT_CODE);  
```
##### 2.打开相机
```
new ImagePicker
.Builder()
.setJumpCameraMode(3)
.cachePath(fileDir)// 1 图片 3 视频
.doCrop(0,0,300,300)
.builder()
.startCamera(this, select, PickerConfig.PICKER_IMAGE, PickerConfig.DEFAULT_RESULT_CODE);  
```





 
 

 








