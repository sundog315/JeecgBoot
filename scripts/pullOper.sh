#!/bin/sh

VERSION="1.0.0"
# 配置参数
SERVER_URL='sundog315.eicp.net:9527'
API_BASE_URL="http://${SERVER_URL}/jeecg-boot/cpe/device/cpeOperLog"
API_SCRIPT_URL="http://${SERVER_URL}/jeecg-boot/cpe/scripts/cpeScripts" 
DEVICE_TYPE=$(uci get lede.system.name)
MAX_RETRIES=3
RETRY_DELAY=5

# 获取脚本当前版本
get_script_version() {
    local script_path=$1
    if [ -f "$script_path" ]; then
        local version=$(grep "^VERSION=" "$script_path" | cut -d'"' -f2)
        echo "$version"
    else
        echo ""
    fi
}

# 更新或创建脚本
# 更新或创建脚本
handle_script_update() {
    local device_type=$DEVICE_TYPE
    log_info "Checking script updates for device type: $device_type"

    # 获取所有生效的脚本列表
    local response=$(curl -s -X GET \
        "${API_SCRIPT_URL}/listEnabledScripts?deviceModule=${device_type}")

    if [ $? -ne 0 ]; then
        log_error "从服务器获取脚本信息失败"
        return 1
    fi

    # 首先检查响应是否成功
    local success=$(echo "$response" | grep -o '"success":true')
    if [ -z "$success" ]; then
        log_error "服务器返回信息错误"
        return 1
    fi

    # 提取result数组部分
    local result_array=$(echo "$response" | sed 's/.*"result":\[\([^]]*\)\].*/\1/')

    # 使用循环处理每个脚本对象
    echo "$result_array" | grep -o '{[^}]*}' | while read -r script_obj; do
        # 提取脚本信息
        local full_path=$(echo "$script_obj" | grep -o '"fullPath":"[^"]*"' | cut -d'"' -f4)
        local server_version=$(echo "$script_obj" | grep -o '"version":"[^"]*"' | cut -d'"' -f4)

        if [ -z "$full_path" ] || [ -z "$server_version" ]; then
            log_error "错误的脚本信息: path=$full_path, version=$server_version"
            continue
        fi

        # 获取当前版本
        local current_version=$(get_script_version "$full_path")

        # 检查是否需要更新
        if [ ! -f "$full_path" ] || [ "$current_version" != "$server_version" ]; then
            log_info "Script $full_path needs update: current=$current_version, server=$server_version"

            # 获取脚本内容
            local content_response=$(curl -s -X GET \
                "${API_SCRIPT_URL}/getScriptContent?scriptPath=${full_path}")

            local content=$(echo "$content_response" | grep -o '"result":"[^"]*"' | cut -d'"' -f4)

            if [ -z "$content" ]; then
                log_error "获取脚本内容失败: $full_path"
                continue
            fi

            # 确保目录存在
            local dir_path=$(dirname "$full_path")
            [ ! -d "$dir_path" ] && mkdir -p "$dir_path"

            # 解码并写入内容
            echo "$content" | base64 -d > "$full_path"

            # 设置执行权限
            chmod +x "$full_path"

            if [ ! -f "$full_path" ]; then
                log_info "创建脚本: $full_path (版本号: $server_version)"
            else
                log_info "更新脚本: $full_path (版本号: $current_version -> $server_version)"
            fi
        else
            log_info "脚本已是最新: $full_path (版本号: $current_version)"
        fi
    done
}

# 获取MAC地址
get_mac_address() {
    local mac
    mac=$(wtinfo -g mac)
    if [ $? -ne 0 ] || [ -z "$mac" ]; then
        log_error "获取mac地址失败"
        exit 1
    fi
    echo "$mac"
}

# 日志函数
log_info() {
    logger -t "pullOper" -p user.info "$1"
    echo "[INFO] $(date '+%Y-%m-%d %H:%M:%S') - $1"
}

log_error() {
    logger -t "pullOper" -p user.error "$1"
    echo "[ERROR] $(date '+%Y-%m-%d %H:%M:%S') - $1" >&2
}

# HTTP请求函数
make_http_request() {
    local url="$1"
    local data="$2"
    local retry=0
    local response

    while [ $retry -lt $MAX_RETRIES ]; do
        response=$(curl -s -X POST \
            -H "Content-Type: application/x-www-form-urlencoded" \
            --data-urlencode "type=${DEVICE_TYPE}" \
            --data-urlencode "$data" \
            "$url")

        if [ $? -eq 0 ]; then
            echo "$response"
            return 0
        fi

        retry=$((retry + 1))
        log_error "HTTP请求失败, 尝试次数: $retry of $MAX_RETRIES"
        sleep $RETRY_DELAY
    done

    log_error "HTTP请求失败, 尝试次数: $MAX_RETRIES"
    return 1
}

# HTTP推送函数
make_http_push() {
    local url="$1"
    local operid="$2"
    local retCode="$3"
    local output="$4"
    local retry=0
    local response

    while [ $retry -lt $MAX_RETRIES ]; do
        response=$(curl -s -X POST \
            -H "Content-Type: application/x-www-form-urlencoded" \
            --data-urlencode "type=${DEVICE_TYPE}" \
            --data-urlencode "operid=${operid}" \
            --data-urlencode "retCode=${retCode}" \
            --data-urlencode "output=${output}" \
            "$url")

        if [ $? -eq 0 ]; then
            echo "$response"
            return 0
        fi

        retry=$((retry + 1))
        log_error "HTTP请求失败, 尝试次数: $retry of $MAX_RETRIES"
        sleep $RETRY_DELAY
    done

    log_error "HTTP请求失败, 尝试次数: $MAX_RETRIES"
    return 1
}

# 解析操作列表
parse_operation() {
    local response="$1"
    echo "$response" | awk -F ":" '{print $3}' | awk -F '"' '{print $2}'
}

# 处理重启操作
handle_reboot() {
    local id="$1"
    log_info "Executing reboot operation, ID: $id"

    make_http_push "${API_BASE_URL}/push" "${id}" "Rebooted" ""
    if [ $? -eq 0 ]; then
        reboot
    fi
}

# 处理重启操作
handle_restarFrp() {
    local id="$1"
    log_info "Executing restartFrp operation, ID: $id"

    make_http_push "${API_BASE_URL}/push" "${id}" "Restarted" ""

    restart_frp_service
}

# 处理FRP操作
handle_frp() {
    local id="$1"
    local param="$2"
    log_info "Executing FRP operation, ID: $id"

    # 解析FRP参数
    local server_addr=$(echo "$param" | awk -F ',' '{print $1}')
    local server_port=$(echo "$param" | awk -F ',' '{print $2}')
    local token=$(echo "$param" | awk -F ',' '{print $3}')
    local ssh_port=$(echo "$param" | awk -F ',' '{print $4}')
    local http_port=$(echo "$param" | awk -F ',' '{print $5}')

    # 更新FRP配置
    update_frp_config "$server_addr" "$server_port" "$token" "$ssh_port" "$http_port"

    # 重启FRP服务
    restart_frp_service

    # 获取FRP日志
    local frplog=$(logread | grep frpc)

    # 发送操作结果
    make_http_push "${API_BASE_URL}/push" "${id}" "Restarted" "${frplog}"
}

# 更新FRP配置
update_frp_config() {
    local server_addr="$1"
    local server_port="$2"
    local token="$3"
    local ssh_port="$4"
    local http_port="$5"

    # 使用临时文件避免直接修改配置文件
    local temp_file=$(mktemp)
    cp /etc/config/frpc "$temp_file"

    sed -i "/option server_addr/s/'[^']*'/'$server_addr'/" "$temp_file"
    sed -i "/option server_port/s/'[0-9]\+$'/$server_port/" "$temp_file"
    sed -i "/option token/s/'[^']*'/'$token'/" "$temp_file"
    sed -i "/config conf 'CPE_[0-9A-Fa-f]\{6\}_ssh'/,/^config conf/ {/option remote_port/s/[0-9]\+/$ssh_port/}" "$temp_file"
    sed -i "/config conf 'CPE_[0-9A-Fa-f]\{6\}_http'/,/^config conf/ {/option remote_port/s/[0-9]\+/$http_port/}" "$temp_file"

    # 验证配置文件
    if [ $? -eq 0 ]; then
        mv "$temp_file" /etc/config/frpc
    else
        log_error "更新FRP配置失败"
        rm "$temp_file"
        return 1
    fi
}

# 重启FRP服务
restart_frp_service() {
    /etc/init.d/frpc stop
    sleep 1
    /etc/init.d/frpc start

    # 检查服务状态
    if ! pgrep -f frpc > /dev/null; then
        log_error "重启FRP服务失败"
        return 1
    fi
}

handle_autoreboot() {
    local id="$1"
    local param="$2"
    log_info "Executing Schedule Reboot operation, ID: $id"

    local cron_file="/etc/crontabs/root"
    local reboot_line=$(grep "reboot" "$cron_file")
    if [ "$param" = "del" ]; then
        if [ -n "$reboot_line" ]; then
            sed -i "/reboot/d" "$cron_file"
        fi
    else
        if [ -z "$reboot_line" ]; then
            echo "$param" >> "$cron_file"
        else
            sed -i "/reboot/d" "$cron_file"
            echo "$param" >> "$cron_file"
        fi
    fi

    # 发送操作结果
    make_http_push "${API_BASE_URL}/push" "${id}" "Scheduled" ""
}

# 处理网络配置操作
handle_network() {
    local id="$1"
    local param="$2"
    log_info "执行网络配置操作, ID: $id"

    # 解析网络参数
    local lan_ip=$(echo "$param" | awk -F ',' '{print $1}')
    local lan_netmask=$(echo "$param" | awk -F ',' '{print $2}')
    local dhcp_start=$(echo "$param" | awk -F ',' '{print $3}')
    local dhcp_end=$(echo "$param" | awk -F ',' '{print $4}')
    local dhcp_leasetime=$(echo "$param" | awk -F ',' '{print $5}')

    # 更新网络配置
    update_network_config "$lan_ip" "$lan_netmask"
    local network_result=$?

    # 更新DHCP配置
    update_dhcp_config "$dhcp_start" "$dhcp_end" "$dhcp_leasetime"
    local dhcp_result=$?

    # 重启网络服务
    if [ $network_result -eq 0 ] && [ $dhcp_result -eq 0 ]; then
        /etc/init.d/network restart
        /etc/init.d/dnsmasq restart

        # 获取更新后的配置信息
        local network_info=$(cat /etc/config/network | grep -A 5 "config interface 'lan'")
        local dhcp_info=$(cat /etc/config/dhcp | grep -A 5 "config dhcp 'lan'")
        local result_log="Network config:\n${network_info}\n\nDHCP config:\n${dhcp_info}"

        # 发送操作结果
        make_http_push "${API_BASE_URL}/push" "${id}" "Updated" "${result_log}"
    else
        make_http_push "${API_BASE_URL}/push" "${id}" "Failed" "Failed to update network configuration"
    fi
}

# 更新网络配置
update_network_config() {
    local ip="$1"
    local netmask="$2"
    local temp_file=$(mktemp)

    # 备份原配置文件
    cp /etc/config/network "$temp_file"

    # 更新LAN接口配置
    sed -i "/config interface 'lan'/,/^config/ {
        s/option ipaddr '[^']*'/option ipaddr '$ip'/
        s/option netmask '[^']*'/option netmask '$netmask'/
    }" "$temp_file"

    # 验证配置文件
    if [ $? -eq 0 ]; then
        mv "$temp_file" /etc/config/network
        return 0
    else
        log_error "更新网络配置失败"
        rm "$temp_file"
        return 1
    fi
}

# 更新DHCP配置
update_dhcp_config() {
    local start="$1"
    local end="$2"
    local leasetime="$3"
    local temp_file=$(mktemp)

    # 计算limit值（结束地址减去起始地址加1）
    local limit=$((end - start + 1))

    # 备份原配置文件
    cp /etc/config/dhcp "$temp_file"

    # 更新DHCP配置
    sed -i "/config dhcp 'lan'/,/^config/ {
        s/option start '[^']*'/option start '$start'/
        s/option limit '[^']*'/option limit '$limit'/
        s/option leasetime '[^']*'/option leasetime '$leasetime'/
    }" "$temp_file"

    # 验证配置文件
    if [ $? -eq 0 ]; then
        mv "$temp_file" /etc/config/dhcp
        return 0
    else
        log_error "更新DHCP配置失败"
        rm "$temp_file"
        return 1
    fi
}

# 转换速率单位为mbit
convert_to_mbit() {
    local rate=$1
    local value=$(echo $rate | sed 's/[^0-9]//g')
    local unit=$(echo $rate | sed 's/[0-9]//g')

    case $unit in
        "Gbit")
            echo $((value * 1000))
            ;;
        "Mbit"|"mbit")
            echo $value
            ;;
        *)
            echo $value
            ;;
    esac
}

# 获取当前速率限制
get_current_rate() {
    local dev=$1
    local rate=$(tc -s class show dev $dev | grep "class htb 1:10" | grep -o "rate [^ ]*" | cut -d' ' -f2)
    if [ -n "$rate" ]; then
        convert_to_mbit "$rate"
        return 0
    fi
    return 1
}

# 更新速率限制
update_speed_limit() {
    local up_rate=$1
    local down_rate=$2
    local tc_file="/etc/5g/tc"

    # 检查文件是否存在
    if [ ! -f "$tc_file" ]; then
        log_error "TC配置文件不存在"
        return 1
    fi

    # 更新配置文件
    sed -i "s/UP_SPEEDLIMIT=\"[^\"]*\"/UP_SPEEDLIMIT=\"${up_rate}mbit\"/" "$tc_file"
    sed -i "s/DOWN_SPEEDLIMIT=\"[^\"]*\"/DOWN_SPEEDLIMIT=\"${down_rate}mbit\"/" "$tc_file"

    # 重启服务
    /etc/init.d/tc_limiter restart

    log_info "速率限制已更新：上传=${up_rate}mbit，下载=${down_rate}mbit"
    return 0
}

# 处理速率限制操作
handle_speed_limit() {
    local id=$1
    local param=$2
    local up_rate
    local down_rate
    local current_up_rate
    local current_down_rate

    # 解析逗号分隔的参数，分别获取上传和下载速率
    up_rate=$(echo "$param" | cut -d',' -f1 | sed 's/mbit//')
    down_rate=$(echo "$param" | cut -d',' -f2 | sed 's/mbit//')

    if [ -z "$up_rate" ] || [ -z "$down_rate" ]; then
        log_error "无效的速率参数格式"
        make_http_push "${API_BASE_URL}/push" "${id}" "Failed" "无效的速率参数格式"
        return 1
    fi

    # 获取当前上传速率
    current_up_rate=$(get_current_rate "usb0")
    if [ $? -ne 0 ]; then
        log_error "无法获取当前上传速率限制"
        make_http_push "${API_BASE_URL}/push" "${id}" "Failed" "无法获取当前上传速率限制"
        return 1
    fi

    # 获取当前下载速率
    current_down_rate=$(get_current_rate "br-lan")
    if [ $? -ne 0 ]; then
        log_error "无法获取当前下载速率限制"
        make_http_push "${API_BASE_URL}/push" "${id}" "Failed" "无法获取当前下载速率限制"
        return 1
    fi

    # 检查是否需要更新，比较时不带单位
    if [ "$current_up_rate" != "$up_rate" ] || [ "$current_down_rate" != "$down_rate" ]; then
        log_info "当前速率(上传=${current_up_rate}mbit,下载=${current_down_rate}mbit)与目标速率(上传=${up_rate}mbit,下载=${down_rate}mbit)不同，开始更新..."
        update_speed_limit "$up_rate" "$down_rate"
        local result=$?
        if [ $result -eq 0 ]; then
            make_http_push "${API_BASE_URL}/push" "${id}" "Updated" "速率限制已更新：上传=${up_rate}mbit，下载=${down_rate}mbit"
        else
            make_http_push "${API_BASE_URL}/push" "${id}" "Failed" "更新速率限制失败"
        fi
        return $result
    else
        log_info "当前速率已经是：上传=${current_up_rate}mbit，下载=${current_down_rate}mbit，无需更新"
        make_http_push "${API_BASE_URL}/push" "${id}" "Skipped" "当前速率已经是：上传=${current_up_rate}mbit，下载=${current_down_rate}mbit，无需更新"
        return 0
    fi
}

# 处理无线配置操作
handle_wireless() {
    local id="$1"
    local param="$2"
    log_info "执行无线配置操作, ID: $id"

    # 解析无线参数
    local g2_disabled=$(echo "$param" | cut -d',' -f1)
    local g2_channel=$(echo "$param" | cut -d',' -f2)
    local g2_ssid=$(echo "$param" | cut -d',' -f3)
    local g2_encryption=$(echo "$param" | cut -d',' -f4)
    local g2_key=$(echo "$param" | cut -d',' -f5)
    local g2_maxsta=$(echo "$param" | cut -d',' -f6)
    local g2_power=$(echo "$param" | cut -d',' -f7)
    local g2_macfilter=$(echo "$param" | cut -d',' -f8)
    local g2_hidden=$(echo "$param" | cut -d',' -f9)
    local g5_disabled=$(echo "$param" | cut -d',' -f10)
    local g5_channel=$(echo "$param" | cut -d',' -f11)
    local g5_ssid=$(echo "$param" | cut -d',' -f12)
    local g5_encryption=$(echo "$param" | cut -d',' -f13)
    local g5_key=$(echo "$param" | cut -d',' -f14)
    local g5_maxsta=$(echo "$param" | cut -d',' -f15)
    local g5_power=$(echo "$param" | cut -d',' -f16)
    local g5_macfilter=$(echo "$param" | cut -d',' -f17)
    local g5_hidden=$(echo "$param" | cut -d',' -f18)

    # 更新无线配置
    local temp_file=$(mktemp)
    cp /etc/config/wireless "$temp_file"

    # 更新2.4G配置
    sed -i "/config wifi-device 'radio0'/,/config wifi-iface/ {
        s/option channel ['\"]\\{0,1\\}[^'\"]*['\"]\\{0,1\\}/option channel '$g2_channel'/
        s/option power ['\"]\\{0,1\\}[^'\"]*['\"]\\{0,1\\}/option power '$g2_power'/
    }" "$temp_file"

    sed -i "/config wifi-iface 'wlan0'/,/config wifi-iface/ {
        s/option disabled ['\"]\\{0,1\\}[^'\"]*['\"]\\{0,1\\}/option disabled '$g2_disabled'/
        s/option ssid ['\"]\\{0,1\\}[^'\"]*['\"]\\{0,1\\}/option ssid '$g2_ssid'/
        s/option encryption ['\"]\\{0,1\\}[^'\"]*['\"]\\{0,1\\}/option encryption '$g2_encryption'/
        s/option key ['\"]\\{0,1\\}[^'\"]*['\"]\\{0,1\\}/option key '$g2_key'/
        s/option maxsta ['\"]\\{0,1\\}[^'\"]*['\"]\\{0,1\\}/option maxsta '$g2_maxsta'/
        s/option macfilter ['\"]\\{0,1\\}[^'\"]*['\"]\\{0,1\\}/option macfilter '$g2_macfilter'/
        s/option hidden ['\"]\\{0,1\\}[^'\"]*['\"]\\{0,1\\}/option hidden '$g2_hidden'/
    }" "$temp_file"

    # 更新5G配置
    sed -i "/config wifi-device 'radio1'/,/config wifi-iface/ {
        s/option channel ['\"]\\{0,1\\}[^'\"]*['\"]\\{0,1\\}/option channel '$g5_channel'/
        s/option power ['\"]\\{0,1\\}[^'\"]*['\"]\\{0,1\\}/option power '$g5_power'/
    }" "$temp_file"

    sed -i "/config wifi-iface 'wlan1'/,/config wifi-iface/ {
        s/option disabled ['\"]\\{0,1\\}[^'\"]*['\"]\\{0,1\\}/option disabled '$g5_disabled'/
        s/option ssid ['\"]\\{0,1\\}[^'\"]*['\"]\\{0,1\\}/option ssid '$g5_ssid'/
        s/option encryption ['\"]\\{0,1\\}[^'\"]*['\"]\\{0,1\\}/option encryption '$g5_encryption'/
        s/option key ['\"]\\{0,1\\}[^'\"]*['\"]\\{0,1\\}/option key '$g5_key'/
        s/option maxsta ['\"]\\{0,1\\}[^'\"]*['\"]\\{0,1\\}/option maxsta '$g5_maxsta'/
        s/option macfilter ['\"]\\{0,1\\}[^'\"]*['\"]\\{0,1\\}/option macfilter '$g2_macfilter'/
        s/option hidden ['\"]\\{0,1\\}[^'\"]*['\"]\\{0,1\\}/option hidden '$g2_hidden'/
    }" "$temp_file"

    # 验证并应用配置
    if [ $? -eq 0 ]; then
        mv "$temp_file" /etc/config/wireless
        /etc/init.d/network restart

        # 获取更新后的配置信息
        local wireless_info=$(cat /etc/config/wireless)
        make_http_push "${API_BASE_URL}/push" "${id}" "Updated" "${wireless_info}"
    else
        log_error "更新无线配置失败"
        rm "$temp_file"
        make_http_push "${API_BASE_URL}/push" "${id}" "Failed" "更新无线配置失败"
        return 1
    fi
}

# 主函数
main() {
    # 先检查脚本是否需要更新
    handle_script_update

    # 获取MAC地址
    local mac
    mac=$(get_mac_address)

    # 获取操作列表
    local response
    response=$(make_http_request "${API_BASE_URL}/pull" "mac=${mac}")
    if [ $? -ne 0 ]; then
        exit 1
    fi

    # 解析操作
    local operList
    operList=$(parse_operation "$response")

    # 处理操作
    if [[ "$operList" =~ "OK" ]]; then
        log_info "No job to be run"
        exit 0
    fi

    # 解析操作参数
    local id=$(echo "$operList" | awk -F '|' '{print $1}')
    local oper=$(echo "$operList" | awk -F '|' '{print $2}')
    local param=$(echo "$operList" | awk -F '|' '{print $3}')
    # 参数中的特殊字符进行转义
    param=${param//%2C/,}
    param=${param//%7C/\|}

    # 执行操作
    case "$oper" in
        "reboot")
            handle_reboot "$id"
            ;;
        "frp")
            handle_frp "$id" "$param"
            ;;
        "restartFrp")
            handle_restarFrp "$id" "$param"
            ;;
        "autoreboot")
            handle_autoreboot "$id" "$param"
            ;;
        "network")
            handle_network "$id" "$param"
            ;;
        "speed_limit")
            handle_speed_limit "$id" "$param"
            ;;
        "wireless")
            handle_wireless "$id" "$param"
            ;;
        *)
            log_error "未知的操作: $oper"
            exit 1
            ;;
    esac

    log_info "任务执行成功"
}

# 执行主函数
main