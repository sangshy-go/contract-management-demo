-- 合同表
CREATE TABLE contracts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contract_no VARCHAR(50) NOT NULL UNIQUE,
    contract_name VARCHAR(200) NOT NULL,
    contract_type VARCHAR(50),
    amount DECIMAL(18,2),
    customer_name VARCHAR(100),
    customer_id VARCHAR(50),
    sign_date DATE,
    start_date DATE,
    end_date DATE,
    status VARCHAR(20) DEFAULT 'DRAFT',
    risk_level VARCHAR(20),
    department VARCHAR(100),
    manager VARCHAR(100),
    remark TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 合同审批记录表
CREATE TABLE approval_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    approver VARCHAR(100),
    approve_level INT,
    approve_result VARCHAR(20),
    approve_comment TEXT,
    approve_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 附件表
CREATE TABLE attachments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    file_name VARCHAR(200),
    file_path VARCHAR(500),
    file_size BIGINT,
    file_type VARCHAR(50),
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 操作日志表
CREATE TABLE operation_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contract_id BIGINT,
    operator VARCHAR(100),
    operation VARCHAR(100),
    detail TEXT,
    ip_address VARCHAR(50),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
