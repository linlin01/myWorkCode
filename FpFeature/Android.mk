LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional


###LOCAL_JAVA_LIBRARIES := SettingsLib

LOCAL_STATIC_JAVA_LIBRARIES := android-common

LOCAL_SRC_FILES := $(call all-subdir-java-files)
LOCAL_SRC_FILES += \
	src/com/rgk/fpfeature/IFpService.aidl


LOCAL_PACKAGE_NAME := RGKFpFeature
LOCAL_CERTIFICATE := platform

####include frameworks/base/packages/SettingsLib/common.mk

include $(BUILD_PACKAGE)