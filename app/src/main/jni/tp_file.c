#include <fcntl.h>
#include <string.h>
#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <dirent.h>
#include <android/log.h>

static const char *TAG = "NDKFileUtil";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)
#define MAXLINE 1024

char* Jstring2CStr(JNIEnv* env, jstring jstr) {
	char* rtn = NULL;
	//找到Java中的String的Class对象
	jclass clsstring = (*env)->FindClass(env, "java/lang/String");
	//创建一个Java中的字符串 "UTF-8"
	jstring strencode = (*env)->NewStringUTF(env, "UTF-8");
	/*
	 * 获取String中定义的方法 getBytes(), 该方法的参数是 String类型的, 返回值是 byte[]数组
	 * "(Ljava/lang/String;)[B" 方法前面解析 :
	 * -- Ljava/lang/String; 表示参数是String字符串
	 * -- [B : 中括号表示这是一个数组, B代表byte类型, 返回值是一个byte数组
	 */
	jmethodID mid = (*env)->GetMethodID(env, clsstring, "getBytes",
			"(Ljava/lang/String;)[B");
	//调用Java中的getBytes方法, 传入参数介绍 参数②表示调用该方法的对象, 参数③表示方法id , 参数④表示方法参数
	jbyteArray barr = (jbyteArray)(*env)->CallObjectMethod(env, jstr, mid,
			strencode); // String .getByte("GB2312");
	//获取数组的长度
	jsize alen = (*env)->GetArrayLength(env, barr);
	//获取数组中的所有的元素 , 存放在 jbyte*数组中
	jbyte* ba = (*env)->GetByteArrayElements(env, barr, JNI_FALSE);
	//将Java数组中所有元素拷贝到C的char*数组中, 注意C语言数组结尾要加一个 '\0'
	if (alen > 0) {
		rtn = (char*) malloc(alen + 1); //new   char[alen+1]; "\0"
		memcpy(rtn, ba, alen);
		rtn[alen] = 0;
	}
	(*env)->ReleaseByteArrayElements(env, barr, ba, 0); //释放内存

	return rtn;
}

//删除文件或者文件夹
JNIEXPORT int Java_jone_helper_util_NDKFileUtil_remove(JNIEnv *env, jobject obj, jstring filePath){
    char *c_filePath = (char*) Jstring2CStr(env, filePath);
    if(is_exist(c_filePath) != 0){
        LOGE("remove: %s is not exist.", c_filePath);
        return -1;
    }
    char c_rm[80];
    strcpy (c_rm,"rm -rf ");
    strcat (c_rm, c_filePath);
    LOGI("remove: %s", c_rm);
    return system(c_rm);
}

//重命名文件或者文件夹
JNIEXPORT int Java_jone_helper_util_NDKFileUtil_rename(JNIEnv *env, jobject obj, jstring filePath, jstring outPath){
    char *c_filePath = (char*) Jstring2CStr(env, filePath);
	char *c_outPath = (char*) Jstring2CStr(env, outPath);
	if(is_exist(c_filePath) != 0){
	    LOGE("rename: %s is not exist.", c_filePath);
        return -1;
    }
    LOGI("rename: %s to %s", c_filePath, c_outPath);
    return rename(c_filePath, c_outPath);
}

//创建文件夹(包含父目录)
JNIEXPORT int Java_jone_helper_util_NDKFileUtil_mkdirs(JNIEnv *env, jobject obj, jstring filePath){
    char *c_filePath = (char*) Jstring2CStr(env, filePath);
    char DirName[256];
    strcpy(DirName, c_filePath);
    int i, len = strlen(DirName);
    if(DirName[len-1] != '/'){
        strcat(DirName, "/");
    }
    len = strlen(DirName);
    for(i = 1; i< len; i++){
        if(DirName[i] == '/'){
            DirName[i] = 0;
            if(is_exist(DirName) != 0){
                if(mkdir(DirName, 0755) == -1){
                    perror("mkdir error");
                    return -1;
                }
            }
            DirName[i] = '/';
        }
    }
    return 0;
}

int is_exist(char *c_filePath){
    if(!access(c_filePath, F_OK)){
        return 0;
    }
    return -1;
}
