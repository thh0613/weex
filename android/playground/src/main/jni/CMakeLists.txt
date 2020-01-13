cmake_minimum_required(VERSION 3.4.1)

set(THH_JNI_LIB thh_jni)

#add_compile_options(-fexceptions)
#add_compile_options(-fstack-protector)
#add_compile_options(-funwind-tables)
#add_compile_options(-fno-omit-frame-pointer)

set(THH_JNI_SRCS
        thh_jni.cpp
        )

include_directories(
        ../../../../sdk/weex_core/Source
        ../../../../sdk/weex_core/Source/include
        ../../../../sdk/weex_core/Source/include/JSCHeaderNew
)

link_directories(../../../../sdk/src/main/jniLibs/armeabi)
link_directories(../jniLibs/armeabi)
add_library(${THH_JNI_LIB}  SHARED ${THH_JNI_SRCS})

find_library(
        log-lib
        log )

target_link_libraries(${THH_JNI_LIB}  ${log-lib})
target_link_libraries(${THH_JNI_LIB}  jsc)