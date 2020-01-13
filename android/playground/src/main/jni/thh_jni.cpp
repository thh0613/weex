#ifndef THH_JNI
#define THH_JNI

#include <jni.h>
#include <android/log.h>
#include <JavaScriptCore/API/JSBase.h>
#include <JavaScriptCore/API/JavaScript.h>
#include <JavaScriptCore/API/JSContextRef.h>
#include <JavaScriptCore/API/JSObjectRef.h>
#include <JavaScriptCore/API/JSStringRef.h>
#include <JavaScriptCore/API/JSValueRef.h>
#include <string>


#define TAG    "jni_demo"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__) // 定义LOGD类型

static JSGlobalContextRef  contextRef;

static JSValueRef testObj_test(JSContextRef ctx, JSObjectRef function, JSObjectRef thisObject, size_t argumentCount, const JSValueRef arguments[], JSValueRef* exception)
{
    LOGD("hello weex");
    return JSValueMakeBoolean(ctx,true);
}

static JSStaticFunction testObjFunc[] = {
        {"log",testObj_test,kJSPropertyAttributeNone | kJSPropertyAttributeDontDelete},
        {0,0,0}
};



static jint Jni_thh(JNIEnv *env, jobject jcaller, jobject injectObj)
{

    if (!contextRef) {
        contextRef =
                JSGlobalContextCreateInGroup(JSContextGroupCreate(), nullptr);
    }
    JSObjectRef ref = JSContextGetGlobalObject(contextRef);
    JSStringRef name  = JSStringCreateWithUTF8CString("Console");


    // 创建 example 类的定义
    JSClassDefinition example_class_definition = kJSClassDefinitionEmpty;
    // 设置类的对应函数名和参数名的钩子
    example_class_definition.staticFunctions = testObjFunc;
//    example_class_definition.staticValues = s_console_values_;
    // 设置类的名称
    example_class_definition.className = "Console";

    // 创建 JS 引擎的类
    JSClassRef example_class_ref = JSClassCreate(&example_class_definition);



    JSObjectRef example = JSObjectMake(contextRef, example_class_ref, NULL);
    JSObjectSetProperty(contextRef, ref, name, example, kJSPropertyAttributeNone | kJSPropertyAttributeDontDelete, 0);

    JSObjectSetPrivate(example, injectObj);
    return 0;
}



bool JSValueToStdString(JSContextRef ctx, JSValueRef value, std::string *result) {
    if (nullptr == value || JSValueIsNull(ctx, value)) {
        return false;
    }
    JSStringRef str = JSValueToStringCopy(ctx, value, nullptr);
    size_t max_bytes = JSStringGetMaximumUTF8CStringSize(str);
    result->resize(max_bytes);
    size_t bytes_written = JSStringGetUTF8CString(str, &(*result)[0], max_bytes);
    if (max_bytes == 0) {
        return false;
    }
    result->resize(bytes_written - 1);
    // LOGE("Conversion::JSValueToStdString result: %s", result->c_str());

    JSStringRelease(str);
    return true;
}


static jstring Jni_nativeString(JNIEnv *env, jobject jcaller, jstring jstr)
{
    const char* cstr = env->GetStringUTFChars(jstr, nullptr);
    LOGD("Jni_nativeString, jstr :%s", cstr);
    JSStringRef source = JSStringCreateWithUTF8CString(cstr);
    if (!contextRef) {
        contextRef =
                JSGlobalContextCreateInGroup(JSContextGroupCreate(), nullptr);
    }
    JSValueRef exceptionRef = nullptr;
    std::string strResult;
    JSValueRef  result = JSEvaluateScript(contextRef, source, NULL, NULL, 0, &exceptionRef);
    JSValueToStdString(contextRef,result,&strResult);
    env->ReleaseStringUTFChars(jstr, cstr);
    return env->NewStringUTF(strResult.c_str());
 // return env->NewStringUTF("aaa");
}

static const JNINativeMethod kMethodsWXBridge[] = {
        {     "nativeThh",
                "(Lorg/apache/weex/Console;)I",
                reinterpret_cast<void *>(Jni_thh)
        },
        {     "nativeString",
                "(Ljava/lang/String;)Ljava/lang/String;",
                reinterpret_cast<void *>(Jni_nativeString)
        }
};

static bool RegisterNativesImpl(JNIEnv *env) {

    jclass clz= env->FindClass("org/apache/weex/SplashActivity");

    const int kMethodsWXBridgeSize =
            sizeof(kMethodsWXBridge) / sizeof(kMethodsWXBridge[0]);

    if (env->RegisterNatives(clz, kMethodsWXBridge,
                             kMethodsWXBridgeSize) < 0) {
        //jni_generator::HandleRegistrationError(
        //    env, WXBridge_clazz(env), __FILE__);
        return false;
    }

    return true;
}
// JNI_OnLoad函数实现
jint JNI_OnLoad(JavaVM* vm, void* reserved){
    JNIEnv* env = NULL;
    jint result = -1;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }

    if (!RegisterNativesImpl(env)) {
        return -1;
    }

    result = JNI_VERSION_1_4;
    return result;
}

#endif  // THH_JNI
