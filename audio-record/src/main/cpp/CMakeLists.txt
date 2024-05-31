cmake_minimum_required(VERSION 3.22.1)

project("audio-record")

include_directories(${CMAKE_SOURCE_DIR}/)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}  -DANDROID_STL=c++_static -DANDROID_STL=c++_shared -lc++")

add_library(${CMAKE_PROJECT_NAME} SHARED
        audio_record.cpp
        mixer.c
        pcm.c
)

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log)