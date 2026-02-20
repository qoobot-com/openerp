-- ============================================================================
-- PostgreSQL 数据库初始化脚本
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
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_job_no ON schedule_job (job_no, tenant_id, deleted);
CREATE INDEX idx_tenant_id ON schedule_job (tenant_id);
CREATE INDEX idx_status ON schedule_job (status);
CREATE INDEX idx_next_execute_time ON schedule_job (next_execute_time);

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
    PRIMARY KEY (id)
);

CREATE INDEX idx_job_id ON schedule_log (job_id);
CREATE INDEX idx_execute_time ON schedule_log (execute_time);
CREATE INDEX idx_status ON schedule_log (status);

-- 任务配置表
CREATE TABLE IF NOT EXISTS schedule_config (
    id BIGSERIAL NOT NULL,
    job_id BIGINT NOT NULL,
    config_key VARCHAR(100) NOT NULL,
    config_value VARCHAR(500) NOT NULL,
    config_desc VARCHAR(200),
    tenant_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_job_config ON schedule_config (job_id, config_key);
CREATE INDEX idx_job_id_config ON schedule_config (job_id);

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
    PRIMARY KEY (id)
);

CREATE INDEX idx_job_id_notify ON schedule_notify (job_id);
CREATE INDEX idx_notify_time ON schedule_notify (notify_time);
CREATE INDEX idx_status_notify ON schedule_notify (status);
