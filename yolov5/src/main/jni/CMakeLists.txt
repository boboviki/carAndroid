project(yolov5ncnn)  #工程名

cmake_minimum_required(VERSION 3.4.1) #需要的最低版本

set(ncnn_DIR ${CMAKE_SOURCE_DIR}/${ANDROID_ABI}/lib/cmake/ncnn)
find_package(ncnn REQUIRED)

add_library(yolov5ncnn SHARED yolov5ncnn_jni.cpp)#生成动态库或共享库

target_link_libraries(yolov5ncnn
    ncnn

    jnigraphics
)

#target_link_libraries( # 目标库
#        hello
#
#        # 目标库需要链接的库
#        # log-lib 是上面 find_library 指定的变量名
#        ${log-lib} )
