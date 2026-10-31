cmake_minimum_required(VERSION 3.5)

project(rime-jni)

cmake_policy(SET CMP0077 NEW)  # 显式设置 覆盖子模块同名配置

set(CMAKE_CXX_STANDARD 17)



list(PREPEND CMAKE_MODULE_PATH "${CMAKE_CURRENT_SOURCE_DIR}/cmake")

option(BUILD_SHARED_LIBS "" OFF)
option(ENABLE_LOGGING "" OFF)        # 如果不需要 glog
option(BUILD_TESTING "" OFF)
option(BUILD_STATIC "" ON)

option(BUILD_TEST "Build and run tests" OFF)

option(USE_SYSTEM_MARISA "Use system version of Marisa" ON)
#leveldb
option(LEVELDB_BUILD_TESTS "Build LevelDB's unit tests" OFF)
option(LEVELDB_BUILD_BENCHMARKS "Build LevelDB's benchmarks" OFF)
option(LEVELDB_INSTALL "Install LevelDB's header and library" OFF)


option(ENABLE_DARTS "Build DartsDict (ocd format)." OFF)

include(Boost)


add_subdirectory(librime/deps/yaml-cpp)
set(YAML-CPP_INCLUDE_PATH
        "${CMAKE_SOURCE_DIR}/librime/deps/yaml-cpp/include"
)

add_subdirectory(librime/deps/leveldb)
set(LEVELDB_INCLUDE_PATH
        "${CMAKE_SOURCE_DIR}/librime/deps/leveldb/include"
)

add_subdirectory(librime/deps/marisa-trie)
set(MARISA-TRIE_INCLUDE_PATH
        "${CMAKE_SOURCE_DIR}/librime/deps/marisa-trie/include"
)
set(LIBMARISA "${CMAKE_BINARY_DIR}" CACHE FILEPATH "marisa library path" FORCE)

add_subdirectory(librime/deps/opencc)
include(OpenCCInstall)


list(APPEND CMAKE_LIBRARY_PATH "${CMAKE_BINARY_DIR}")

list(APPEND CMAKE_LIBRARY_PATH "${CMAKE_SOURCE_DIR}")


add_subdirectory(librime)




include_directories(librime/src)

set(CMAKE_C_FLAGS_RELEASE "${CMAKE_C_FLAGS_RELEASE} -fvisibility=hidden ")

add_link_options("-Wl,--pack-dyn-relocs=android")

set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -std=c++17")


set(SRC
        rime_jni.c
        rime_callback_java.c
        jni_init.c
        test.c

)

add_library(rime-jni SHARED  ${SRC})
target_link_libraries(rime-jni log rime-static libopencc leveldb yaml-cpp::yaml-cpp)

