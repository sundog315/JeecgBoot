<!--
 * @Author: Janelle.Liu sundog315@foxmail.com
 * @Date: 2025-01-15 13:44:36
 * @LastEditors: Janelle.Liu sundog315@foxmail.com
 * @LastEditTime: 2025-01-20 10:02:23
 * @FilePath: /JeecgBoot/jeecg-boot/jeecg-module-cpe/src/main/java/org/jeecg/modules/cpe/device/wireless.md
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
首先编写shell脚本：
使用shell脚本，分析openwrt设备无线网络配置文件/etc/config/wireless，文件内容为：
```
config wifi-device radio0
        option type mt7981
        option vendor ralink
        option phyname mt7981
        option channel auto
        option country CN
        option htmode auto
        option hwmode 11axg
        option def_hwmode 11axg
        option wpa3 1
        option power 100

config wifi-iface wlan0
        option device radio0
        option network lan
        option mode ap
        option ifname ra0
        option ssid 5G_CPE-A060
        option encryption mixed-psk
        option key '123456789'
        option macaddr A4:D8:CA:8F:A0:60
        option disabled 0
        option web_merge 0
        option macfilter deny
        option maxsta 128

config wifi-iface guest0
        option device radio0
        option network guest
        option mode ap
        option ifname ra1
        option ssid 5G_CPE-guest-A060
        option encryption none
        option disabled 1
        option maxsta 64

config wifi-iface wisp0
        option device radio0
        option network wisp0
        option mode sta
        option ifname apcli0
        option ssid 5G_CPE-wisp-A060
        option encryption none
        option disabled 1

config wifi-device radio1
        option type mt7981
        option vendor ralink
        option phyname mt7981
        option channel auto
        option country CN
        option htmode auto
        option hwmode 11axa
        option def_hwmode 11axa
        option wpa3 1
        option power 100

config wifi-iface wlan1
        option device radio1
        option network lan
        option mode ap
        option ifname rax0
        option ssid 5G_CPE_5G-A060
        option encryption mixed-psk
        option key '123456789'
        option macaddr A4:D8:CA:8F:A0:61
        option disabled 0
        option macfilter deny
        option maxsta 128

config wifi-iface guest1
        option device radio1
        option network guest
        option mode ap
        option ifname rax1
        option ssid 5G_CPE-guest-A060
        option encryption none
        option disabled 1
        option maxsta 64

config wifi-iface wisp1
        option device radio1
        option network wisp1
        option mode sta
        option ifname apclix0
        option ssid 5G_CPE-wisp-A060
        option encryption none
        option disabled 1
```
提取wlan0条目下的disable作为2.4G无线的启用标志，提取wlan1条目下的disable作为5G无线的启用标志，提取radio0条目下的channel作为2.4G无线的信道，提取radio1条目下的channel作为5G无线的信道，提取wlan0条目下的ssid作为2.4G无线的ssid，提取wlan1条目下的ssid作为5G无线的ssid，提取wlan0条目下的encryption作为2.4G无线的加密方式，提取wlan1条目下的encryption作为5G无线的加密方式，提取wlan0条目下的key作为2.4G无线的密码，提取wlan1条目下的key作为5G无线的密码，提取wlan0条目下的maxsta作为2.4G无线的终端数量，提取wlan1条目下的maxsta作为5G无线的终端数量，提取radio0条目下的power作为2.4G无线功率，提取radio1条目下的power作为5G无线功率。

将上面的信息组合为wireless参数，修改pushEvent.sh脚本，将wireless参数传递至服务器。

修改pullOper.sh脚本，当oper参数为wireless时：
将收到的值与/etc/config/wireless配置文件进行比对，如果不同，则使用服务器中获取的值替换配置文件/etc/config/wireless的值，并调用/etc/init.d/network restart命令，从而使新限速值生效。