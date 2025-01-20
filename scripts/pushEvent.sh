#!/bin/sh

VERSION="1.0.0"
# 配置参数
SERVER_URL='sundog315.eicp.net:9527'
API_BASE_URL="http://${SERVER_URL}/jeecg-boot/cpe/device/api/push"
DEVICE_TYPE=$(uci get lede.system.name)
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

    # 组合所有网络配置信息为JSON格式
    echo "{\"lan_ip\":\"$lan_ipaddr\",\"lan_netmask\":\"$lan_netmask\",\"dhcp_start\":\"$dhcp_start\",\"dhcp_end\":\"$dhcp_end\",\"dhcp_lease\":\"$dhcp_leasetime\",\"dhcp_range\":\"$dhcp_range\"}"
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
    # 2.4G配置
    local wlan0_disabled=$(awk '/config wifi-iface '\''wlan0'\''/{p=1;next} p&&/option disabled/{split($0,a,"'\''"); print a[2];exit}' /etc/config/wireless)
    local radio0_channel=$(awk '/config wifi-device '\''radio0'\''/{p=1;next} p&&/option channel/{split($0,a,"'\''"); print a[2];exit}' /etc/config/wireless)
    local wlan0_ssid=$(awk '/config wifi-iface '\''wlan0'\''/{p=1;next} p&&/option ssid/{split($0,a,"'\''"); print a[2];exit}' /etc/config/wireless)
    local wlan0_encryption=$(awk '/config wifi-iface '\''wlan0'\''/{p=1;next} p&&/option encryption/{split($0,a,"'\''"); print a[2];exit}' /etc/config/wireless)
    local wlan0_key=$(awk '/config wifi-iface '\''wlan0'\''/{p=1;next} p&&/option key/{split($0,a,"'\''"); print a[2];exit}' /etc/config/wireless)
    local wlan0_maxsta=$(awk '/config wifi-iface '\''wlan0'\''/{p=1;next} p&&/option maxsta/{split($0,a,"'\''"); print a[2];exit}' /etc/config/wireless)
    local radio0_power=$(awk '/config wifi-device '\''radio0'\''/{p=1;next} p&&/option power/{split($0,a,"'\''"); print a[2];exit}' /etc/config/wireless)

    # 5G配置
    local wlan1_disabled=$(awk '/config wifi-iface '\''wlan1'\''/{p=1;next} p&&/option disabled/{split($0,a,"'\''"); print a[2];exit}' /etc/config/wireless)
    local radio1_channel=$(awk '/config wifi-device '\''radio1'\''/{p=1;next} p&&/option channel/{split($0,a,"'\''"); print a[2];exit}' /etc/config/wireless)
    local wlan1_ssid=$(awk '/config wifi-iface '\''wlan1'\''/{p=1;next} p&&/option ssid/{split($0,a,"'\''"); print a[2];exit}' /etc/config/wireless)
    local wlan1_encryption=$(awk '/config wifi-iface '\''wlan1'\''/{p=1;next} p&&/option encryption/{split($0,a,"'\''"); print a[2];exit}' /etc/config/wireless)
    local wlan1_key=$(awk '/config wifi-iface '\''wlan1'\''/{p=1;next} p&&/option key/{split($0,a,"'\''"); print a[2];exit}' /etc/config/wireless)
    local wlan1_maxsta=$(awk '/config wifi-iface '\''wlan1'\''/{p=1;next} p&&/option maxsta/{split($0,a,"'\''"); print a[2];exit}' /etc/config/wireless)
    local radio1_power=$(awk '/config wifi-device '\''radio1'\''/{p=1;next} p&&/option power/{split($0,a,"'\''"); print a[2];exit}' /etc/config/wireless)

    # 组合所有无线配置信息为JSON格式
    echo "{\"2g_disabled\":\"$wlan0_disabled\",\"2g_channel\":\"$radio0_channel\",\"2g_ssid\":\"$wlan0_ssid\",\"2g_encryption\":\"$wlan0_encryption\",\"2g_key\":\"$wlan0_key\",\"2g_maxsta\":\"$wlan0_maxsta\",\"2g_power\":\"$radio0_power\",\"5g_disabled\":\"$wlan1_disabled\",\"5g_channel\":\"$radio1_channel\",\"5g_ssid\":\"$wlan1_ssid\",\"5g_encryption\":\"$wlan1_encryption\",\"5g_key\":\"$wlan1_key\",\"5g_maxsta\":\"$wlan1_maxsta\",\"5g_power\":\"$radio1_power\"}"
}

# HTTP请求函数
make_http_request() {
    local retry=0
    local success=false

    while [ $retry -lt $MAX_RETRIES ] && [ "$success" = "false" ]; do
        # 准备所有数据
        local mac=$(get_mac_address)
        local ubus_call=$(get_ubus_call)
        local network_info=$(get_network_info "usb0")
        local lte_status=$(get_lte_status)
        local frp_config=$(get_frp_config)
        local auto_reboot_config=$(get_auto_reboot_config)
        local network_config=$(get_network_config)
        local speed_limit_config=$(get_speed_limit)
        local wireless_config=$(get_wireless_config)

        # 发送HTTP请求
        local response
        response=$(curl --connect-timeout $TIMEOUT -s -X POST \
            -H "Content-Type: application/x-www-form-urlencoded" \
            --compressed \
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