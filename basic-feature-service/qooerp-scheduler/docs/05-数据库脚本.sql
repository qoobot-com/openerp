-- ============================================================================
-- PostgreSQL 数据库初始化脚本
-- ============================================================================
-- 数据库: qooerp_scheduler
-- 版本: v1.0
-- 创建日期: 2026-02-18
-- ============================================================================

-- 创建更新时间触发器函数
CREATE OR REPLACE FUNCTION update_modified_time()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 创建数据库
CREATE DATABASE qooerp_scheduler WITH ENCODING 'UTF8';

\c qooerp_scheduler

-- ============================================================================
-- 表结构定义
-- ============================================================================

-- 定时任务表
CREATE TABLE IF NOT EXISTS schedule_job (
    id BIGSERIAL NOT NULL,
    job_no VARCHAR(50) NOT NULL,
    job_name VARCHAR(200) NOT NULL,
    job_type VARCHAR(20) NOT NULL,
    job_class VARCHAR(500) NOT NULL,
    cron_expression VARCHAR(100),
    interval INTEGER,
    execute_strategy VARCHAR(20) NOT NULL DEFAULT 'SCHEDULE',
    retry_strategy VARCHAR(20) NOT NULL DEFAULT 'NONE',
    retry_count INTEGER NOT NULL DEFAULT 0,
    timeout INTEGER,
    concurrency VARCHAR(20) NOT NULL DEFAULT 'SERIAL',
    dependent_job_id BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'STOPPED',
    next_execute_time TIMESTAMP,
    prev_execute_time TIMESTAMP,
    execute_count BIGINT NOT NULL DEFAULT 0,
    success_count BIGINT NOT NULL DEFAULT 0,
    fail_count BIGINT NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE INDEX uk_job_no (job_no, tenant_id, deleted),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_status (status),
    INDEX idx_next_execute_time (next_execute_time)
);

-- 任务日志表
CREATE TABLE IF NOT EXISTS schedule_log (
    id BIGSERIAL NOT NULL,
    job_id BIGINT NOT NULL,
    execute_time TIMESTAMP NOT NULL,
    execute_result TEXT,
    execute_duration BIGINT,
    status VARCHAR(20) NOT NULL,
    exception_info TEXT,
    tenant_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_job_id (job_id),
    INDEX idx_execute_time (execute_time),
    INDEX idx_status (status)
);

-- 任务配置表
CREATE TABLE IF NOT EXISTS schedule_config (
    id BIGSERIAL NOT NULL,
    job_id BIGINT NOT NULL,
    config_key VARCHAR(100) NOT NULL,
    config_value VARCHAR(500) NOT NULL,
    config_desc VARCHAR(200),
    tenant_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE INDEX uk_job_config (job_id, config_key),
    INDEX idx_job_id (job_id)
);

-- 任务报警表
CREATE TABLE IF NOT EXISTS schedule_notify (
    id BIGSERIAL NOT NULL,
    job_id BIGINT NOT NULL,
    notify_type VARCHAR(20) NOT NULL,
    notify_level VARCHAR(20) NOT NULL,
    notify_content TEXT NOT NULL,
    notify_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    handler VARCHAR(100),
    handle_time TIMESTAMP,
    handle_remark TEXT,
    tenant_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_job_id (job_id),
    INDEX idx_notify_time (notify_time),
    INDEX idx_status (status)
);

-- ============================================================================
-- 创建触发器
-- ============================================================================

CREATE TRIGGER trigger_schedule_job_update_time
BEFORE UPDATE ON schedule_job
FOR EACH ROW
EXECUTE FUNCTION update_modified_time();

-- ============================================================================
-- 创建用户和授权
-- ============================================================================

CREATE USER qooerp_scheduler WITH PASSWORD 'qooerp_scheduler_2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO qooerp_scheduler;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO qooerp_scheduler;

-- ============================================================================
-- 初始化数据
-- ============================================================================

-- 插入示例任务
INSERT INTO schedule_job (
    job_no, job_name, job_type, job_class, cron_expression,
    execute_strategy, retry_strategy, retry_count, timeout,
    concurrency, status, tenant_id
) VALUES
(
    'JOB001', '数据同步任务', 'CRON',
    'com.qoobot.qooerp.scheduler.job.DataSyncJob',
    '0 0 2 * * ?', 'SCHEDULE', 'FIXED', 3, 600,
    'SERIAL', 'STOPPED', 1
),
(
    'JOB002', '报表生成任务', 'CRON',
    'com.qoobot.qooerp.scheduler.job.ReportGenerateJob',
    '0 0 3 * * ?', 'SCHEDULE', 'FIXED', 2, 1800,
    'PARALLEL', 'STOPPED', 1
),
(
    'JOB003', '数据清理任务', 'INTERVAL',
    'com.qoobot.qooerp.scheduler.job.DataCleanupJob',
    NULL, 'SCHEDULE', 'NONE', 0, 3600,
    'SERIAL', 'STOPPED', 1
);

-- 插入任务配置示例
INSERT INTO schedule_config (job_id, config_key, config_value, config_desc, tenant_id)
VALUES
(1, 'sync.source', 'source_db', '数据同步源数据库', 1),
(1, 'sync.target', 'target_db', '数据同步目标数据库', 1),
(2, 'report.type', 'daily', '报表类型', 1),
(3, 'cleanup.days', '30', '清理天数', 1);

-- ============================================================================
-- 创建视图
-- ============================================================================

-- 任务统计视图
CREATE OR REPLACE VIEW v_job_statistics AS
SELECT
    j.id,
    j.job_no,
    j.job_name,
    j.status,
    j.execute_count,
    j.success_count,
    j.fail_count,
    CASE
        WHEN j.execute_count > 0 THEN ROUND((j.success_count::FLOAT / j.execute_count) * 100, 2)
        ELSE 0
    END AS success_rate,
    CASE
        WHEN j.execute_count > 0 THEN ROUND((j.fail_count::FLOAT / j.execute_count) * 100, 2)
        ELSE 0
    END AS fail_rate,
    j.next_execute_time,
    j.prev_execute_time
FROM schedule_job j
WHERE j.deleted = 0;

-- ============================================================================
-- 创建存储过程
-- ============================================================================

-- 清理过期日志
CREATE OR REPLACE FUNCTION cleanup_old_logs()
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    DELETE FROM schedule_log
    WHERE execute_time < CURRENT_TIMESTAMP - INTERVAL '30 days';

    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

-- 清理过期报警
CREATE OR REPLACE FUNCTION cleanup_old_notifies()
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    DELETE FROM schedule_notify
    WHERE notify_time < CURRENT_TIMESTAMP - INTERVAL '90 days';

    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- 创建函数
-- ============================================================================

-- 生成任务编号
CREATE OR REPLACE FUNCTION generate_job_no()
RETURNS VARCHAR AS $$
DECLARE
    job_no VARCHAR(50);
    date_str VARCHAR(8);
    seq_num INTEGER;
BEGIN
    date_str := TO_CHAR(CURRENT_DATE, 'YYYYMMDD');

    SELECT COALESCE(MAX(CAST(SUBSTRING(job_no FROM 9) AS INTEGER)), 0) + 1
    INTO seq_num
    FROM schedule_job
    WHERE job_no LIKE 'JOB' || date_str || '%'
    AND deleted = 0;

    job_no := 'JOB' || date_str || LPAD(seq_num::TEXT, 4, '0');
    RETURN job_no;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- 注释
-- ============================================================================

-- schedule_job 表注释
COMMENT ON TABLE schedule_job IS '定时任务表';
COMMENT ON COLUMN schedule_job.id IS '主键';
COMMENT ON COLUMN schedule_job.job_no IS '任务编号';
COMMENT ON COLUMN schedule_job.job_name IS '任务名称';
COMMENT ON COLUMN schedule_job.job_type IS '任务类型:CRON/INTERVAL/ONCE/DEPENDENT';
COMMENT ON COLUMN schedule_job.job_class IS '任务类全限定名';
COMMENT ON COLUMN schedule_job.cron_expression IS 'Cron表达式';
COMMENT ON COLUMN schedule_job.interval IS '执行间隔(秒)';
COMMENT ON COLUMN schedule_job.execute_strategy IS '执行策略:NOW/SCHEDULE/RETRY/SKIP';
COMMENT ON COLUMN schedule_job.retry_strategy IS '重试策略:NONE/FIXED/EXPONENTIAL/LINEAR';
COMMENT ON COLUMN schedule_job.retry_count IS '重试次数';
COMMENT ON COLUMN schedule_job.timeout IS '超时时间(秒)';
COMMENT ON COLUMN schedule_job.concurrency IS '并发策略:SERIAL/PARALLEL';
COMMENT ON COLUMN schedule_job.dependent_job_id IS '依赖任务ID';
COMMENT ON COLUMN schedule_job.status IS '状态:STOPPED/RUNNING/PAUSED/DELETED';
COMMENT ON COLUMN schedule_job.next_execute_time IS '下次执行时间';
COMMENT ON COLUMN schedule_job.prev_execute_time IS '上次执行时间';
COMMENT ON COLUMN schedule_job.execute_count IS '执行次数';
COMMENT ON COLUMN schedule_job.success_count IS '成功次数';
COMMENT ON COLUMN schedule_job.fail_count IS '失败次数';
COMMENT ON COLUMN schedule_job.tenant_id IS '租户ID';
COMMENT ON COLUMN schedule_job.create_time IS '创建时间';
COMMENT ON COLUMN schedule_job.update_time IS '更新时间';
COMMENT ON COLUMN schedule_job.deleted IS '删除标记:0-正常,1-删除';

-- schedule_log 表注释
COMMENT ON TABLE schedule_log IS '任务日志表';
COMMENT ON COLUMN schedule_log.id IS '主键';
COMMENT ON COLUMN schedule_log.job_id IS '任务ID';
COMMENT ON COLUMN schedule_log.execute_time IS '执行时间';
COMMENT ON COLUMN schedule_log.execute_result IS '执行结果';
COMMENT ON COLUMN schedule_log.execute_duration IS '执行时长(毫秒)';
COMMENT ON COLUMN schedule_log.status IS '状态:SUCCESS/FAILURE/RUNNING/TIMEOUT';
COMMENT ON COLUMN schedule_log.exception_info IS '异常信息';
COMMENT ON COLUMN schedule_log.tenant_id IS '租户ID';
COMMENT ON COLUMN schedule_log.create_time IS '创建时间';

-- schedule_config 表注释
COMMENT ON TABLE schedule_config IS '任务配置表';
COMMENT ON COLUMN schedule_config.id IS '主键';
COMMENT ON COLUMN schedule_config.job_id IS '任务ID';
COMMENT ON COLUMN schedule_config.config_key IS '配置键';
COMMENT ON COLUMN schedule_config.config_value IS '配置值';
COMMENT ON COLUMN schedule_config.config_desc IS '配置描述';
COMMENT ON COLUMN schedule_config.tenant_id IS '租户ID';
COMMENT ON COLUMN schedule_config.create_time IS '创建时间';

-- schedule_notify 表注释
COMMENT ON TABLE schedule_notify IS '任务报警表';
COMMENT ON COLUMN schedule_notify.id IS '主键';
COMMENT ON COLUMN schedule_notify.job_id IS '任务ID';
COMMENT ON COLUMN schedule_notify.notify_type IS '报警类型:FAILURE/TIMEOUT/RETRY';
COMMENT ON COLUMN schedule_notify.notify_level IS '报警级别:LOW/MEDIUM/HIGH/CRITICAL';
COMMENT ON COLUMN schedule_notify.notify_content IS '报警内容';
COMMENT ON COLUMN schedule_notify.notify_time IS '报警时间';
COMMENT ON COLUMN schedule_notify.status IS '状态:PENDING/HANDLED/IGNORED';
COMMENT ON COLUMN schedule_notify.handler IS '处理人';
COMMENT ON COLUMN schedule_notify.handle_time IS '处理时间';
COMMENT ON COLUMN schedule_notify.handle_remark IS '处理备注';
COMMENT ON COLUMN schedule_notify.tenant_id IS '租户ID';
COMMENT ON COLUMN schedule_notify.create_time IS '创建时间';

-- ============================================================================
-- 数据库初始化完成
-- ============================================================================
