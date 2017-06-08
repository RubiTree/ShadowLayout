# ShadowLayout
[![](https://img.shields.io/badge/Version-v0.9.2-green.svg)](https://github.com/SaltedfishCaptain/ShadowLayout/releases/tag/v0.9.2)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/SaltedfishCaptain/ShadowLayout/blob/master/LICENSE)

A custom view group which wrap view and display shadow, help you control shadow as UI design.

[中文版]()

![](file/screenshots/samples.png)

## Feature
1. Support SDK VERSION below to 9.
2. Support draw shadow in any size at any side by `shadow offset` and `shadow padding`.
3. Support draw colorful shadow by `shadow color`.
4. Support preview shadow size and color in xml.
5. Support draw square, rounded corner and circle shadow.

## Usage
### Add Dependency
```groovy
compile 'com.github.SaltedfishCaptain:ShadowLayout:v0.9.2'
```
If you have not use library from JitPack before, you should add this in your project build.gradle.
```groovy
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

### Use In XML
```XML
<com.saltedfishcaptain.library.ShadowLayout
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginTop="20dp"
    
    app:shadow_corner_radius="2dp"
    app:shadow_blur_radius="4dp"
    app:shadow_offset_y="4dp"
    app:shadow_padding_left="3dp"
    app:shadow_padding_right="3dp"
    app:shadow_color="@color/orange">
    
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:padding="10dp"
        android:textColor="@color/white"
        android:background="@drawable/shape_round_rect_2dp"
        android:text="Offset And Padding" />
        
</com.saltedfishcaptain.library.ShadowLayout>
```
[Here]() have some samples with screenshots more.


## Attributes

|name|format|description|
|:---:|:---:|:---:|
| shadow_blur_radius | dimension | Shadow blur radius can control blur degree and blur area, control blur degree like elevation, control blur area will make layout larger at four sides, default 4(dp)
| shadow_corner_radius | dimension | Shadow corner radius, set this as child circle view's radius will make circle shadow, default 4(dp)
| shadow_color | color | Shadow color, default #88757575
| shadow_offset_x | dimension | Move shadow by X axis, default 0(dp)
| shadow_offset_y | dimension | Move shadow by Y axis, default 0(dp)
| shadow_padding_left | dimension | Shrink shadow at left side, default 0(dp)
| shadow_padding_right | dimension | Shrink shadow at right side, default 0(dp)
| shadow_padding_top | dimension | Shrink shadow at top side, default 0(dp)
| shadow_padding_bottom | dimension | Shrink shadow at bottom side, default 0(dp)
| invalidate_shadow_on_size_changed | boolean | Control if invalidate shadow on size changed, default true


## APIs
1. `invalidateShadow()` 
Direct invalidate shadow whenever you need, especially when you have set `invalidate_shadow_on_size_changed` false.

## WIKI
1. 引子
2. 解析
3. [Samples]()

## License
Apache License 2.0, here is the [LICENSE](https://github.com/SaltedfishCaptain/ShadowLayout/blob/master/LICENSE).