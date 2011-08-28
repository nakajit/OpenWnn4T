LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_PACKAGE_NAME := OpenWnn4T

LOCAL_JNI_SHARED_LIBRARIES := \
	 libWnnEngDic4T libWnnJpnDic4T libwnndict4T

LOCAL_AAPT_FLAGS += -c hdpi

LOCAL_CERTIFICATE := private

include $(BUILD_PACKAGE)

include $(call all-makefiles-under,$(LOCAL_PATH))
