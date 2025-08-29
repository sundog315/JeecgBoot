#!/bin/sh

VERSION="1.0.0"
# 配置参数
SERVER_URL='sundog315.eicp.net:9527'
API_BASE_URL="http://${SERVER_URL}/jeecg-boot/cpe/device/api/push"
DEVICE_TYPE=$(cat /tmp/sysinfo/board_name)
MAX_RETRIES=3
RETRY_DELAY=5
TIMEOUT=10

# 日志函数
log_info() {
    logger -t "pushEvent" -p user.info "$1"
    echo "[INFO] $(date '+%Y-%m-%d %H:%M:%S') - $1"
}

log_error() {
    logger -t "pushEvent" -p user.error "$1"
    echo "[ERROR] $(date '+%Y-%m-%d %H:%M:%S') - $1" >&2
}

get_version() {
    cat /etc/openwrt_build
}

# 获取MAC地址
get_mac_address() {
    local mac
    mac=$(wtinfo -g mac)
    if [ $? -ne 0 ] || [ -z "$mac" ]; then
        log_error "获取MAC地址失败"
        return 1
    fi
    echo "$mac"
}

# 获取UBUS调用结果
get_ubus_call() {
    local result
    result=$(ubus call lteat get)
    if [ $? -ne 0 ]; then
        log_error "获取UBUS信息失败"
        return 1
    fi
    echo "$result"
}

# 获取UCPU温度
get_cpu_temp() {
    local result
    result=$(echo $(($(cat /sys/class/thermal/thermal_zone0/temp)/1000)))
    if [ $? -ne 0 ]; then
        log_error "获取CPU温度失败"
        return 1
    fi
    echo "$result"
}

# 获取网络信息
get_network_info() {
    local interface="$1"
    local info

    # 获取公网IPv4地址
    local ipv4
    ipv4=$(curl -s --connect-timeout 5 4.ipw.cn)

    # 获取公网IPv6地址
    local ipv6
    ipv6=$(curl -s --connect-timeout 5 6.ipw.cn)

    # 获取流量统计
    local stats
    stats=$(ifconfig "$interface")
    if [ $? -ne 0 ]; then
        log_error "获取接口$interface流量统计失败"
        return 1
    fi

    # 解析上行流量
    local up_bytes
    up_bytes=$(echo "$stats" | grep "RX bytes" | awk '{print $6}' | awk -F ':' '{print $2}')

    # 解析下行流量
    local down_bytes
    down_bytes=$(echo "$stats" | grep "RX bytes" | awk '{print $2}' | awk -F ':' '{print $2}')

    # 组合网络信息
    echo "${ipv4},${ipv6},${up_bytes},${down_bytes}"
}

# 获取LTE状态
get_lte_status() {
    local status
    status=$(ubus call network.interface.lte0 status)
    if [ $? -ne 0 ]; then
        log_error "获取LTE状态失败"
        return 1
    fi
    echo "$status"
}

# 获取FRP配置
get_frp_config() {
    if [ ! -f "/etc/config/frpc" ]; then
        log_error "FRP配置文件不存在"
        return 1
    fi
    cat "/etc/config/frpc"
}

# 获取自动重启配置
get_auto_reboot_config() {
    if [ ! -f "/etc/crontabs/root" ]; then
        log_error "计划任务配置文件不存在"
        return 1
    fi
    grep "reboot" "/etc/crontabs/root"
}

# 分析network配置
get_network_config() {
    # 分析LAN配置
    local lan_ipaddr=$(awk '/config interface '\''lan'\''/{p=1;next} p&&/option ipaddr/{split($0,a,"'\''"); print a[2];exit}' /etc/config/network)
    local lan_netmask=$(awk '/config interface '\''lan'\''/{p=1;next} p&&/option netmask/{split($0,a,"'\''"); print a[2];exit}' /etc/config/network)

    # 分析DHCP配置
    local dhcp_start=$(awk '/config dhcp '\''lan'\''/{p=1;next} p&&/option start/{split($0,a,"'\''"); print a[2];exit}' /etc/config/dhcp)
    local dhcp_limit=$(awk '/config dhcp '\''lan'\''/{p=1;next} p&&/option limit/{split($0,a,"'\''"); print a[2];exit}' /etc/config/dhcp)
    local dhcp_leasetime=$(awk '/config dhcp '\''lan'\''/{p=1;next} p&&/option leasetime/{split($0,a,"'\''"); print a[2];exit}' /etc/config/dhcp)

    # 计算DHCP地址池范围
    local dhcp_range=""
    local dhcp_end=""
    if [ ! -z "$lan_ipaddr" ] && [ ! -z "$dhcp_start" ] && [ ! -z "$dhcp_limit" ]; then
        local ip_prefix=$(echo $lan_ipaddr | cut -d. -f1-3)
        local start_ip="$ip_prefix.$dhcp_start"
        local end_num=$((dhcp_start + dhcp_limit - 1))
        local end_ip="$ip_prefix.$end_num"
        dhcp_range="$start_ip-$end_ip"
        dhcp_end="$end_num"
    fi

    # Get formatted modification times of both files
    dhcp_time=$(date -r /etc/config/dhcp "+%Y-%m-%d %H:%M:%S" 2>/dev/null || echo "1970-01-01 00:00:00")
    network_time=$(date -r /etc/config/network "+%Y-%m-%d %H:%M:%S" 2>/dev/null || echo "1970-01-01 00:00:00")

    # Compare the date strings to find the most recent one
    if [ "$dhcp_time" \> "$network_time" ]; then
        last_modified_str="$dhcp_time"
    else
        last_modified_str="$network_time"
    fi

    # 组合所有网络配置信息为JSON格式
    echo "{\"lan_ip\":\"$lan_ipaddr\",\"lan_netmask\":\"$lan_netmask\",\"dhcp_start\":\"$dhcp_start\",\"dhcp_end\":\"$dhcp_end\",\"dhcp_lease\":\"$dhcp_leasetime\",\"dhcp_range\":\"$dhcp_range\",\"last_modified\":\"$last_modified_str\"}"
}

# 获取速率限制配置
get_speed_limit() {
    local tc_file="/etc/5g/tc"
    if [ ! -f "$tc_file" ]; then
        return 1
    fi

    # 读取速率限制值
    local up_limit=$(grep "UP_SPEEDLIMIT=" "$tc_file" | cut -d'"' -f2)
    local down_limit=$(grep "DOWN_SPEEDLIMIT=" "$tc_file" | cut -d'"' -f2)

    if [ -n "$up_limit" ] && [ -n "$down_limit" ]; then
        echo "{\"up\":\"$up_limit\",\"down\":\"$down_limit\"}"
        return 0
    fi
    return 1
}

# 获取无线配置信息
get_wireless_config() {
    # 检查section是否存在
    is_wifi_section_exist() {
        local section="$1"
        awk -v section="$section" '$0 ~ "config[[:space:]]+wifi-iface[[:space:]]+"section {found=1} END{exit !found}' /etc/config/wireless
    }

    # 判断wlan0/wlan1是否存在，不存在则用default_radio0/1
    local wlan0_section="wlan0"
    local wlan1_section="wlan1"
    if ! is_wifi_section_exist "wlan0"; then
        wlan0_section="default_radio0"
    fi
    if ! is_wifi_section_exist "wlan1"; then
        wlan1_section="default_radio1"
    fi

    # 通用awk函数，支持带引号和不带引号的格式
    get_wifi_option() {
        local section="$1"
        local option="$2"
        awk -v section="$section" -v option="$option" '
            $0 ~ "config.*"section{p=1;next}
            p&&$0~/^config/{p=0}
            p&&$0~"option.*"option{
                # 移除开头的option和字段名
                sub(/^[[:space:]]*option[[:space:]]+[^[:space:]]+[[:space:]]+/, "")
                # 移除可能存在的引号
                gsub(/'\''/, "")
                print
                exit
            }
        ' /etc/config/wireless
    }

    # 2.4G配置
    local wlan0_disabled
    if [ "$wlan0_section" = "wlan0" ]; then
        wlan0_disabled=$(get_wifi_option "$wlan0_section" "disabled")
    else
        wlan0_disabled=$(get_wifi_option "radio0" "disabled")
    fi
    local radio0_channel=$(get_wifi_option "radio0" "channel")
    local wlan0_ssid=$(get_wifi_option "$wlan0_section" "ssid")
    local wlan0_encryption=$(get_wifi_option "$wlan0_section" "encryption")
    local wlan0_key=$(get_wifi_option "$wlan0_section" "key")
    local wlan0_maxsta=$(get_wifi_option "$wlan0_section" "maxsta")
    local radio0_power
    if [ "$wlan0_section" = "wlan0" ]; then
        radio0_power=$(get_wifi_option "radio0" "power")
    else
        radio0_power=$(get_wifi_option "radio0" "txpower")
    fi
    local wlan0_macfilter=$(get_wifi_option "$wlan0_section" "macfilter")
    local wlan0_hidden=$(get_wifi_option "$wlan0_section" "hidden")

    # 5G配置
    local wlan1_disabled
    if [ "$wlan1_section" = "wlan1" ]; then
        wlan1_disabled=$(get_wifi_option "$wlan1_section" "disabled")
    else
        wlan1_disabled=$(get_wifi_option "radio1" "disabled")
    fi
    local radio1_channel=$(get_wifi_option "radio1" "channel")
    local wlan1_ssid=$(get_wifi_option "$wlan1_section" "ssid")
    local wlan1_encryption=$(get_wifi_option "$wlan1_section" "encryption")
    local wlan1_key=$(get_wifi_option "$wlan1_section" "key")
    local wlan1_maxsta=$(get_wifi_option "$wlan1_section" "maxsta")
    local radio1_power
    if [ "$wlan1_section" = "wlan1" ]; then
        radio1_power=$(get_wifi_option "radio1" "power")
    else
        radio1_power=$(get_wifi_option "radio1" "txpower")
    fi
    local wlan1_macfilter=$(get_wifi_option "$wlan1_section" "macfilter")
    local wlan1_hidden=$(get_wifi_option "$wlan1_section" "hidden")

    # 获取配置文件最后修改时间
    local last_modified
    last_modified=$(date -r /etc/config/wireless "+%Y-%m-%d %H:%M:%S")

    # 组合所有无线配置信息为JSON格式
    echo "{\"last_modified\":\"$last_modified\",\"2g_disabled\":\"$wlan0_disabled\",\"2g_channel\":\"$radio0_channel\",\"2g_ssid\":\"$wlan0_ssid\",\"2g_encryption\":\"$wlan0_encryption\",\"2g_key\":\"$wlan0_key\",\"2g_maxsta\":\"$wlan0_maxsta\",\"2g_power\":\"$radio0_power\",\"2g_macfilter\":\"$wlan0_macfilter\",\"2g_hidden\":\"$wlan0_hidden\",\"5g_disabled\":\"$wlan1_disabled\",\"5g_channel\":\"$radio1_channel\",\"5g_ssid\":\"$wlan1_ssid\",\"5g_encryption\":\"$wlan1_encryption\",\"5g_key\":\"$wlan1_key\",\"5g_maxsta\":\"$wlan1_maxsta\",\"5g_power\":\"$radio1_power\",\"5g_macfilter\":\"$wlan1_macfilter\",\"5g_hidden\":\"$wlan1_hidden\"}"
}

# 获取系统运行时长
get_system_uptime() {
    local uptime_output
    uptime_output=$(uptime)
    if [ $? -ne 0 ]; then
        log_error "获取系统运行时长失败"
        return 1
    fi
    
    # 从uptime输出提取运行时长，兼容BusyBox/ash
    # 1) 截取" up "之后的部分
    local uptime_info
    uptime_info=${uptime_output#* up }

    # 2) 去掉" load average"及其后内容（如果存在）
    uptime_info=${uptime_info%% load average*}

    # 3) 去掉" user"/" users"及其后内容（如果存在）
    uptime_info=${uptime_info%% user*}

    # 4) 规范化空格与逗号，去除首尾逗号与空白
    uptime_info=$(echo "$uptime_info" | sed 's/^[[:space:],]*//;s/[[:space:],]*$//' | sed 's/, \{1,\}/, /g')

    if [ -z "$uptime_info" ]; then
        log_error "解析uptime信息失败"
        return 1
    fi
    
    echo "$uptime_info"
}

# 获取客户端连接信息
get_client_connections() {
    local stat_file="/proc/net/wtnet/stat"
    if [ -f "$stat_file" ]; then
        cat "$stat_file"
        return 0
    fi

    local tmpfile="/tmp/wifi_clients.txt"
    > $tmpfile
    for iface in $(iwinfo 2>/dev/null | grep -oE '^[^ ]+' | grep -E 'ap0$|wlan[0-9]$'); do
        iwinfo $iface assoclist 2>/dev/null | \
        awk '
            /^[0-9A-Fa-f:]{17}/ {
                mac=tolower($1);
                rx=0; tx=0;
                while(getline>0 && $0!~/^[0-9A-Fa-f:]{17}/){
                    if($1=="RX:") rx=$2;
                    if($1=="TX:") tx=$2;
                }
                print mac "|" rx "|" tx
            }
        ' >> $tmpfile
    done

    local arp_cmd=""
    if command -v arp >/dev/null 2>&1; then
        arp_cmd="arp -a"
    elif command -v ip >/dev/null 2>&1; then
        arp_cmd="ip neigh"
    else
        echo "No arp or ip command found" >&2
        rm -f $tmpfile
        return 1
    fi

    $arp_cmd | {
        if [ "$arp_cmd" = "arp -a" ]; then
            awk '/br-lan/ && $1 ~ /^[0-9]+(\.[0-9]+){3}$/ {print $1, tolower($4)}'
        else
            awk '/br-lan/ && $1 ~ /^[0-9]+(\.[0-9]+){3}$/ {print $1, tolower($5)}'
        fi
    } | while read ip mac; do
        line=$(grep "^$mac|" $tmpfile)
        if [ -n "$line" ]; then
            rx=$(echo $line | awk -F'|' '{print $2}')
            tx=$(echo $line | awk -F'|' '{print $3}')
            echo "$ip $mac 0 0 0 0 0 0 0"
        fi
    done
    rm -f $tmpfile
}

# HTTP请求函数
make_http_request() {
    local retry=0
    local success=false

    while [ $retry -lt $MAX_RETRIES ] && [ "$success" = "false" ]; do
        # 准备所有数据
        local mac=$(get_mac_address)
        local ubus_call=$(get_ubus_call)
        
        # 检查ubus_call中是否包含MT5700M，如果包含则使用eth1接口而不是usb0
        local network_interface="usb0"
        if echo "$ubus_call" | grep -q "MT5700M"; then
            network_interface="eth1"
            log_info "检测到MT5700M设备，使用eth1接口"
        fi
        
        local network_info=$(get_network_info "$network_interface")
        local lte_status=$(get_lte_status)
        local frp_config=$(get_frp_config)
        local auto_reboot_config=$(get_auto_reboot_config)
        local network_config=$(get_network_config)
        local speed_limit_config=$(get_speed_limit)
        local wireless_config=$(get_wireless_config)
        local version=$(get_version)
        local system_uptime=$(get_system_uptime)
        local client_connections=$(get_client_connections)
        local cpu_temp=$(get_cpu_temp)

        # 发送HTTP请求
        local response
        response=$(curl --connect-timeout $TIMEOUT -s -X POST \
            -H "Content-Type: application/x-www-form-urlencoded" \
            --data-urlencode "type=${DEVICE_TYPE}" \
            --data-urlencode "mac=${mac}" \
            --data-urlencode "ubus_call=${ubus_call}" \
            --data-urlencode "ip_addr=${network_info}" \
            --data-urlencode "lte_status=${lte_status}" \
            --data-urlencode "frp=${frp_config}" \
            --data-urlencode "auto_reboot=${auto_reboot_config}" \
            --data-urlencode "network=${network_config}" \
            --data-urlencode "speed_limit=${speed_limit_config}" \
            --data-urlencode "wireless=${wireless_config}" \
            --data-urlencode "version=${version}" \
            --data-urlencode "uptime=${system_uptime}" \
            --data-urlencode "clients=${client_connections}" \
            --data-urlencode "cpu_temp=${cpu_temp}" \
            "$API_BASE_URL")

        if [ $? -eq 0 ]; then
            log_info "成功推送设备状态"
            success=true
        else
            log_error "推送设备状态失败, 尝试次数: $((retry + 1)) of $MAX_RETRIES"
            retry=$((retry + 1))
            sleep $RETRY_DELAY
        fi
    done

    if [ "$success" = "false" ]; then
        log_error "推送设备状态失败, 尝试次数: $MAX_RETRIES"
        return 1
    fi

    return 0
}

# 主函数
main() {
    # 检查网络连接
    #if ! ping -c 1 -W 5 8.8.8.8 > /dev/null 2>&1; then
    #    log_error "网络不可用"
    #    exit 1
    #fi

    # 发送设备状态
    if ! make_http_request; then
        exit 1
    fi

    log_info "设备状态推送完成"
}

# 错误处理
#set -e
#trap 'log_error "Script failed on line $LINENO"' ERR

# 执行主函数
main