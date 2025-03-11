include $(TOPDIR)/rules.mk

PKG_NAME:=tc-limit
PKG_VERSION:=1.0.0
PKG_RELEASE:=1

PKG_BUILD_DIR:=$(BUILD_DIR)/$(PKG_NAME)-$(PKG_VERSION)

include $(INCLUDE_DIR)/package.mk

define Package/tc-limit
  SECTION:=net
  CATEGORY:=Network
  TITLE:=Traffic Control Speed Limit Tool
  DEPENDS:=+tc
endef

define Package/tc-limit/description
  A tool for setting upload and download speed limits using tc
endef

define Build/Prepare
	mkdir -p $(PKG_BUILD_DIR)
	$(CP) ./src/* $(PKG_BUILD_DIR)/
endef

define Build/Compile
	$(TARGET_CC) $(TARGET_CFLAGS) -o $(PKG_BUILD_DIR)/tc-limit $(PKG_BUILD_DIR)/tc-limit.c
endef

define Package/tc-limit/install
	$(INSTALL_DIR) $(1)/usr/bin
	$(INSTALL_BIN) $(PKG_BUILD_DIR)/tc-limit $(1)/usr/bin/
endef

$(eval $(call BuildPackage,tc-limit))