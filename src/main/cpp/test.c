//
// Created by mao on 2026/1/3.
//
#include <rime_api.h>
#include <jni.h>
int add(int a,int b){
    RimeApi *pApi = rime_get_api();
    const char *string = pApi->get_staging_dir();

    return a+b;
}

