-- 初始合同数据
INSERT INTO contracts (contract_no, contract_name, contract_type, amount, customer_name, customer_id, sign_date, start_date, end_date, status, risk_level, department, manager) VALUES
('CT202601001', '流动资金贷款合同', 'LOAN', 5000000.00, '深圳市科技有限公司', '91440300MA5DQWXJ1A', '2026-01-15', '2026-01-20', '2027-01-20', 'APPROVED', 'LOW', '信贷管理部', '张三'),
('CT202601002', '固定资产贷款合同', 'LOAN', 15000000.00, '广州制造业集团', '91440101MA59BXYZ12', '2026-02-01', '2026-02-10', '2029-02-10', 'PENDING', 'MEDIUM', '风险管理部', '李四'),
('CT202601003', '授信额度协议', 'CREDIT', 8000000.00, '东莞市贸易公司', '91441900MA4UABC123', '2026-03-10', '2026-03-15', '2027-03-15', 'DRAFT', 'LOW', '信贷管理部', '王五'),
('CT202602001', '供应链融资合同', 'SUPPLY_CHAIN', 3000000.00, '佛山市供应链企业', '91440600MA52MXYZ34', '2026-03-20', '2026-04-01', '2026-12-31', 'APPROVED', 'HIGH', '国际业务部', '赵六'),
('CT202602002', '项目贷款合同', 'PROJECT', 25000000.00, '惠州市建设项目公司', '91441300MA51PQR567', '2026-04-05', '2026-04-15', '2031-04-15', 'PENDING', 'HIGH', '项目融资部', '孙七');

-- 审批记录
INSERT INTO approval_records (contract_id, approver, approve_level, approve_result, approve_comment) VALUES
(1, '审批员A', 1, 'PASS', '资料完整，同意'),
(1, '审批员B', 2, 'PASS', '风险可控，同意'),
(4, '审批员A', 1, 'PASS', '供应链真实，同意');
