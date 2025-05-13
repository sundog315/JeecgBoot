#!/bin/sh

VERSION="1.0.0"
# 配置参数
SERVER_URL='sundog315.eicp.net:9527'
API_SCRIPT_URL="http://${SERVER_URL}/jeecg-boot/cpe/scripts/cpeScripts"
CRONTAB_FILE="/etc/crontabs/root"
PULLER_SCRIPT="/etc/5g/pullOper.sh"
PULLER_CRON="*/1 * * * * /etc/5g/pullOper.sh"
PULLER_CRON1="*/2 * * * * /etc/5g/pushEvent.sh"
DEVICE_TYPE=$(uci get lede.system.name)
MAX_RETRIES=3
RETRY_DELAY=5

# 日志函数
log_info() {
    logger -t "init_crontab" -p user.info "$1"
    echo "[INFO] $(date '+%Y-%m-%d %H:%M:%S') - $1"
}

log_error() {
    logger -t "init_crontab" -p user.error "$1"
    echo "[ERROR] $(date '+%Y-%m-%d %H:%M:%S') - $1" >&2
}

# 确保目录存在
ensure_dir() {
    local dir_path=$(dirname "$PULLER_SCRIPT")
    if [ ! -d "$dir_path" ]; then
        mkdir -p "$dir_path"
        log_info "创建目录: $dir_path"
    fi
}

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

# 从服务器下载脚本
download_script() {
    log_info "开始从服务器下载 pullOper.sh 脚本"

    # 获取脚本内容
    local retry=0
    local content_response

    while [ $retry -lt $MAX_RETRIES ]; do
        content_response=$(curl -s -X GET \
            "${API_SCRIPT_URL}/getScriptContent?scriptPath=/etc/5g/pullOper.sh")

        if [ $? -eq 0 ]; then
            break
        fi

        retry=$((retry + 1))
        log_error "HTTP请求失败, 尝试次数: $retry of $MAX_RETRIES"
        sleep $RETRY_DELAY
    done

    # 检查响应是否成功
    local success=$(echo "$content_response" | grep -o '"success":true')
    if [ -z "$success" ]; then
        log_error "服务器返回信息错误"
        return 1
    fi

    # 提取脚本内容
    local content=$(echo "$content_response" | grep -o '"result":"[^"]*"' | cut -d'"' -f4)

    if [ -z "$content" ]; then
        log_error "获取脚本内容失败"
        return 1
    fi

    # 确保目录存在
    ensure_dir

    # 解码并写入内容
    echo "$content" | base64 -d > "$PULLER_SCRIPT"

    # 设置执行权限
    chmod +x "$PULLER_SCRIPT"

    if [ -f "$PULLER_SCRIPT" ]; then
        local server_version=$(get_script_version "$PULLER_SCRIPT")
        log_info "脚本下载成功: $PULLER_SCRIPT (版本号: $server_version)"
        return 0
    else
        log_error "脚本下载失败"
        return 1
    fi
}

# 检查并设置crontab
setup_crontab() {
    log_info "检查crontab配置"

    # 确保crontab目录存在
    local crontab_dir=$(dirname "$CRONTAB_FILE")
    if [ ! -d "$crontab_dir" ]; then
        mkdir -p "$crontab_dir"
        log_info "创建crontab目录: $crontab_dir"
    fi

    # 检查文件是否存在
    if [ ! -f "$CRONTAB_FILE" ]; then
        echo "$PULLER_CRON" > "$CRONTAB_FILE"
        echo "$PULLER_CRON1" >> "$CRONTAB_FILE"
        log_info "创建crontab文件并添加定时任务"
    else
        # 检查是否已包含pullOper任务
        if ! grep -q "pullOper.sh" "$CRONTAB_FILE"; then
            echo "$PULLER_CRON" >> "$CRONTAB_FILE"
            log_info "向crontab添加pullOper定时任务"
        elif ! grep -q "pushEvent.sh" "$CRONTAB_FILE"; then
            echo "$PULLER_CRON1" >> "$CRONTAB_FILE"
            log_info "向crontab添加pushEvent定时任务"
        else
            log_info "crontab已包含pullOper定时任务，无需修改"
        fi
    fi

    # 重启cron服务
    /etc/init.d/cron restart
    log_info "重启cron服务"

    return 0
}

# 主函数
main() {
    log_info "初始化crontab服务开始"

    # 设置crontab
    setup_crontab

    # 下载pullOper脚本
    download_script

    log_info "初始化crontab服务完成"
}

# 执行主函数
main