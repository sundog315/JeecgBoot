#!/bin/sh

VERSION="1.0.1"
# 目标IP地址
TARGET_IP1="8.8.8.8"
TARGET_IP2="1.1.1.1"

# Ping的次数
PING_COUNT=3

# GPIO操作路径
GPIO_PATH="/sys/class/gpio/5gpower/value"

# 日志文件路径
LOG_DIR="/etc/5g"
LOG_FILE="$LOG_DIR/ping_monitor.log"

# 确保日志目录存在
ensure_log_dir() {
    if [ ! -d "$LOG_DIR" ]; then
        mkdir -p "$LOG_DIR"
    fi
}

# 日志记录函数
log_info() {
    logger -t "ping_monitor" -p user.info "$1"
    echo "[INFO] $(date '+%Y-%m-%d %H:%M:%S') - $1"
}

log_error() {
    logger -t "ping_monitor" -p user.error "$1"
    echo "[ERROR] $(date '+%Y-%m-%d %H:%M:%S') - $1" >&2
}

# 记录到文件日志
log_to_file() {
    ensure_log_dir
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" >> "$LOG_FILE"
}

# 检查网络连接的函数
check_connection() {
    ALL_FAIL=1  # 假设所有目标都不可达

    for TARGET_IP in $TARGET_IP1 $TARGET_IP2; do
        FAIL_COUNT=0

        # 对目标IP进行ping测试
        for i in $(seq 1 $PING_COUNT); do
            ping -c 1 -w 1 $TARGET_IP > /dev/null 2>&1
            if [ $? -ne 0 ]; then
                FAIL_COUNT=$((FAIL_COUNT+1))
            fi
        done

        # 如果有一次ping成功，则认为网络正常
        if [ $FAIL_COUNT -lt $PING_COUNT ]; then
            ALL_FAIL=0
            break  # 如果某个目标可达，退出循环
        fi
    done

    return $ALL_FAIL  # 如果所有目标都失败，返回 1，否则返回 0
}

# 重启模块的函数
restart_module() {
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    local restart_msg="重启 5G 模块... (时间戳: $timestamp)"

    # 记录到系统日志
    log_info "$restart_msg"

    # 记录到文件日志
    log_to_file "5G模块重启触发 - 网络连接失败"

    # 执行重启操作
    echo 1 > $GPIO_PATH
    sleep 1
    echo 0 > $GPIO_PATH

    # 记录重启完成
    local complete_msg="5G 模块已重启。"
    log_info "$complete_msg"
    log_to_file "5G模块重启完成"
}

# 主流程
perform_check() {
    # 第一次检查
    check_connection
    if [ $? -ne 0 ]; then
        log_info "第一次网络检查失败，等待 5 秒..."
        sleep 5

        # 第二次检查
        check_connection
        if [ $? -ne 0 ]; then
            log_error "第二次网络检查失败，准备重启模块。"
            restart_module
        else
            log_info "第二次网络检查成功，网络正常。"
        fi
    else
        log_info "第一次网络检查成功，网络正常。"
    fi
}

# 添加脚本启动日志
log_info "网络连接监控脚本启动 (版本 $VERSION)"

# 执行检查
perform_check

# 添加脚本结束日志
log_info "网络连接监控脚本执行完毕"