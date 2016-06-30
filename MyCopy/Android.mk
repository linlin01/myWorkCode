LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := android-common

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_PRIVILEGED_MODULE := true

LOCAL_PACKAGE_NAME := RgkCopy

LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)
