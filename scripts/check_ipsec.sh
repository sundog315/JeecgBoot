#!/bin/sh

trap 'release_lock; exit' INT TERM EXIT

VERSION="1.0.0"
# 目标IP地址
TARGET_IP1="192.168.0.166"
TARGET_IP2="192.168.0.254"

# Ping的次数
PING_COUNT=3

# 日志文件路径
LOG_DIR="/etc/5g"
LOG_FILE="$LOG_DIR/check_ipsec.log"
LOCK_FILE="$LOG_DIR/check_ipsec.lock"

NETWORK_STATUS=0

# 确保日志目录存在
ensure_log_dir() {
    if [ ! -d "$LOG_DIR" ]; then
        mkdir -p "$LOG_DIR"
    fi
}

# 锁定机制，防止同时执行多个实例
acquire_lock() {
    if [ -e "$LOCK_FILE" ]; then
        PID=$(cat "$LOCK_FILE")
        if kill -0 "$PID" 2>/dev/null; then
            log_error "另一个脚本实例正在运行 (PID: $PID)，退出。"
            exit 1
        else
            log_info "发现过期锁文件，清除。"
        fi
    fi
    echo $$ > "$LOCK_FILE"
}

release_lock() {
    rm -f "$LOCK_FILE"
}

# 日志记录函数
log_info() {
    logger -t "check_ipsec" -p user.info "$1"
    echo "[INFO] $(date '+%Y-%m-%d %H:%M:%S') - $1"
}

log_error() {
    logger -t "check_ipsec" -p user.error "$1"
    echo "[ERROR] $(date '+%Y-%m-%d %H:%M:%S') - $1" >&2
}

# 记录到文件日志
log_to_file() {
    ensure_log_dir
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" >> "$LOG_FILE"
}

# 获取当前时间戳（秒）
get_current_time() {
    date +%s
}

# 检查网络连接的函数
check_connection() {
    ALL_FAIL=1  # 假设所有目标都不可达

    for TARGET_IP in $TARGET_IP1 $TARGET_IP2; do
        FAIL_COUNT=0

        # 对目标IP进行ping测试
        for i in $(seq 1 $PING_COUNT); do
            ping -c 1 -w 2 -W 2 $TARGET_IP > /dev/null 2>&1
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

# 重启IPSEC的函数
restart_ipsec() {
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    local restart_msg="重启 IPSEC... (时间戳: $timestamp)"

    # 记录到系统日志
    log_info "$restart_msg"

    # 记录到文件日志
    log_to_file "IPSEC重启触发 - 网络连接失败"

    # 执行重启操作
    /etc/init.d/ipsec restart

    # 记录重启完成
    local complete_msg="IPSEC已重启。"
    log_info "$complete_msg"
    log_to_file "IPSEC重启完成"
}

# 主流程
perform_check() {

    # 第一次检查
    check_connection
    if [ $? -ne 0 ]; then
        log_info "第一次IPSEC网络检查失败，等待 5 秒..."
        sleep 5

        # 第二次检查
        check_connection
        if [ $? -ne 0 ]; then
            log_error "第二次IPSEC网络检查失败，准备评估是否IPSEC。"

            if [ "$NETWORK_STATUS" -eq 0 ]; then
                # 如果网络之前是正常的，立即重启
                log_info "IPSEC网络新故障或初始状态，立即重启模块。"
                restart_ipsec
            fi
        else
            log_info "第二次IPSEC网络检查成功，网络正常。"
        fi
    else
        log_info "第一次IPSEC网络检查成功，网络正常。"
    fi
}

# 添加脚本启动日志
log_info "IPSEC网络连接监控脚本启动 (版本 $VERSION)"

# 获取锁
acquire_lock

# 执行检查
perform_check

# 释放锁
release_lock

# 添加脚本结束日志
log_info "IPSEC连接监控脚本执行完毕"