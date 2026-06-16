-- LIS Database Schema for H2
-- Compatible with MySQL mode

-- 仪器设备表 (Instrument)
CREATE TABLE IF NOT EXISTS sys_sbdjb (
    sb_djid INT PRIMARY KEY AUTO_INCREMENT,
    sbdm VARCHAR(50) NOT NULL,
    sbmc VARCHAR(100) NOT NULL,
    sbbm VARCHAR(50),
    ksdm VARCHAR(20),
    gzzdm VARCHAR(20),
    pym VARCHAR(50),
    zxbz TINYINT DEFAULT 1,
    tybz TINYINT DEFAULT 0,
    comsm VARCHAR(20),
    btl INT,
    jyw VARCHAR(10),
    sjw INT,
    tzw INT,
    xmxsfs VARCHAR(20),
    bgbt VARCHAR(200),
    bgyj VARCHAR(500),
    mrzhid INT,
    tx VARCHAR(10),
    dyfs VARCHAR(10),
    shzfs VARCHAR(10),
    sxpl INT DEFAULT 0,
    ycxwc TINYINT DEFAULT 0,
    xsfs VARCHAR(20),
    bblb VARCHAR(10),
    xslb VARCHAR(20),
    zklb VARCHAR(20),
    yqzd VARCHAR(20),
    zjjgts INT DEFAULT 7,
    zkjh VARCHAR(50),
    jzjh VARCHAR(50),
    cjcx VARCHAR(200),
    szdm VARCHAR(20),
    kztsbz TINYINT DEFAULT 0,
    jkxmxz TINYINT DEFAULT 0,
    fsztsbz TINYINT DEFAULT 0,
    zerotsbz TINYINT DEFAULT 0,
    ip VARCHAR(50),
    dk VARCHAR(10),
    sjklj VARCHAR(500),
    wjdz VARCHAR(500),
    bfdz VARCHAR(500),
    wjyhm VARCHAR(100),
    wjmm VARCHAR(100),
    yszcz VARCHAR(20) DEFAULT '#000000',
    yspgz VARCHAR(20) DEFAULT '#FF0000',
    yspdz VARCHAR(20) DEFAULT '#0000FF',
    ysbjgz VARCHAR(20) DEFAULT '#FF00FF',
    ysbjdz VARCHAR(20) DEFAULT '#008000',
    yswsh VARCHAR(20) DEFAULT '#000000',
    ysysh VARCHAR(20) DEFAULT '#000000',
    ysycy VARCHAR(20) DEFAULT '#000000',
    ysydy VARCHAR(20) DEFAULT '#000000',
    ysyjy VARCHAR(20) DEFAULT '#000000',
    ysycz VARCHAR(20) DEFAULT '#FF0000',
    yswjz VARCHAR(20) DEFAULT '#FF0000',
    ysjgwc VARCHAR(20) DEFAULT '#000000',
    UNIQUE KEY uk_sbdm (sbdm)
);

-- 仪器设备测试数据 (添加检验科室代码)
INSERT IGNORE INTO sys_sbdjb (sbdm, sbmc, sbbm, ksdm, gzzdm, pym, zxbz, tybz) VALUES
('BC5800', 'BC5800 Blood Cell Analyzer', 'BC5800', '01', '0001', 'BC5800', 1, 0),
('AU-400', 'AU-400 Biochemistry Analyzer', 'AU-400', '01', '0002', 'AU400', 1, 0),
('LX-5000', 'LX-5000 Urine Analyzer', 'LX-5000', '01', '0003', 'LX5000', 1, 0);

-- 操作员代码表 (Users) - 匹配SysCzydm实体
CREATE TABLE IF NOT EXISTS sys_czydm (
    czydm VARCHAR(50) PRIMARY KEY,
    czyxm VARCHAR(50),
    pym VARCHAR(50),
    ksdm VARCHAR(20),
    zcdm VARCHAR(20),
    kl VARCHAR(100),
    czymm VARCHAR(100),
    his_czydm VARCHAR(50),
    mrsrf VARCHAR(50),
    ysbz TINYINT,
    czybz TINYINT,
    glybz TINYINT,
    sybz TINYINT DEFAULT 1,
    gzzdm VARCHAR(20),
    dzqm BLOB,
    czysfzhm VARCHAR(20)
);

-- 系统参数配置表 - 匹配SysXtsz实体
CREATE TABLE IF NOT EXISTS sys_xtsz (
    xtsz_id INT PRIMARY KEY AUTO_INCREMENT,
    xtsz_key VARCHAR(100) NOT NULL UNIQUE,
    xtsz_value VARCHAR(500),
    xtsz_desc VARCHAR(200)
);

-- 报告合并组配置表
CREATE TABLE IF NOT EXISTS sys_bghbzb (
    hbid INT PRIMARY KEY AUTO_INCREMENT,
    hbmc VARCHAR(100)
);

-- 报告合并明细表
CREATE TABLE IF NOT EXISTS sys_bghbmx (
    mxid INT PRIMARY KEY AUTO_INCREMENT,
    hbid INT,
    sb_djid INT,
    sybz TINYINT DEFAULT 1
);

-- 插入默认操作员 (admin/admin) - 使用czydm作为主键
INSERT IGNORE INTO sys_czydm (czydm, czymm, czyxm, sybz) 
VALUES ('admin', 'admin123', '系统管理员', 1);

INSERT IGNORE INTO sys_czydm (czydm, czymm, czyxm, sybz) 
VALUES ('test', 'test', '测试用户', 1);

-- 插入默认仪器
INSERT IGNORE INTO sys_sbdjb (sbdm, sbmc, sbbm, ksdm, gzzdm, pym, zxbz, tybz)
VALUES ('INS001', '生化分析仪', 'SH-001', '01', '01', 'shfxy', 1, 0);

INSERT IGNORE INTO sys_sbdjb (sbdm, sbmc, sbbm, ksdm, gzzdm, pym, zxbz, tybz)
VALUES ('INS002', '血常规分析仪', 'XC-001', '01', '01', 'xcgfxy', 1, 0);

INSERT IGNORE INTO sys_sbdjb (sbdm, sbmc, sbbm, ksdm, gzzdm, pym, zxbz, tybz)
VALUES ('INS003', '尿液分析仪', 'NY-001', '02', '02', 'nyfxy', 1, 0);

-- 插入默认系统配置
INSERT IGNORE INTO sys_xtsz (xtsz_key, xtsz_value, xtsz_desc)
VALUES ('hospital_name', '测试医院', '医院名称');

INSERT IGNORE INTO sys_xtsz (xtsz_key, xtsz_value, xtsz_desc)
VALUES ('report_title', 'LIS检验报告单', '报告标题');

-- ============================================
-- 新增: 患者/样本信息表
-- ============================================
CREATE TABLE IF NOT EXISTS bgxt_brxx (
    brxx_id INT PRIMARY KEY AUTO_INCREMENT,
    brxx_tmh VARCHAR(50),      -- 条码号
    brbh VARCHAR(50),          -- 病人编号
    brxm VARCHAR(50),          -- 病人姓名
    brxb TINYINT,              -- 性别: 1男 2女
    brnl VARCHAR(20),          -- 年龄
    nllx VARCHAR(20),          -- 年龄类型: 岁、月、天
    brlb TINYINT,              -- 病人类型: 1门诊 2住院 3体检 4其他 5科研
    syqk TINYINT,              -- 送检情况/紧急程度
    ksdm VARCHAR(20),         -- 科室代码
    brch VARCHAR(20),          -- 病床号
    syh VARCHAR(20),           -- 样本号
    bbzl VARCHAR(20),          -- 标本种类
    ybzt TINYINT,              -- 样本状态: 0待录入 1已接收 2已审核 3已打印
    jyrq DATETIME,             -- 检验日期
    shrq DATETIME,             -- 审核日期
    sfbz TINYINT,              -- 收费标志
    bz VARCHAR(500),           -- 备注
    czy VARCHAR(50),           -- 操作员
    czrq DATETIME              -- 操作日期
);

-- 插入测试样本数据
INSERT INTO bgxt_brxx (brxx_tmh, brbh, brxm, brxb, brnl, nllx, brlb, syqk, ksdm, brch, syh, bbzl, ybzt, jyrq, sfbz, czy) VALUES
('TM20260310001', 'BR001', '张三', 1, '35', '岁', 1, 1, '01', '101', '202603100001', 1, 1, NOW(), 1, 'admin'),
('TM20260310002', 'BR002', '李四', 2, '28', '岁', 1, 0, '02', '202', '202603100002', 2, 0, NOW(), 1, 'admin'),
('TM20260310003', 'BR003', '王五', 1, '45', '岁', 2, 1, '01', '305', '202603100003', 3, 2, NOW(), 1, 'admin');

-- ============================================
-- 新增: 检验项目表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_bbzl (
    bmid INT PRIMARY KEY AUTO_INCREMENT,
    bmsm VARCHAR(100),         -- 项目说明
    bm VARCHAR(20),            -- 编码
    pym VARCHAR(50),           -- 拼音码
    xmdm VARCHAR(50),          -- 项目代码
    xmzwmc VARCHAR(100),       -- 项目中文名称
    xmdw VARCHAR(20),          -- 单位
    xmdw2 VARCHAR(20),         -- 备用单位
    bjzdx VARCHAR(20),         -- 参考值低限
    bjzgx VARCHAR(20),         -- 参考值高限
    ckzdx VARCHAR(20),         -- 触发值低限
    ckzgx VARCHAR(20),         -- 触发值高限
    ckz VARCHAR(20),           -- 参考值(字符串)
    zxbz TINYINT DEFAULT 1,    -- 启用标志
    sybz TINYINT DEFAULT 1     -- 使用标志
);

-- 插入检验项目测试数据
INSERT INTO sys_bbzl (bmsm, bm, pym, xmdm, xmzwmc, xmdw, bjzdx, bjzgx) VALUES
('葡萄糖', '001', 'pt', 'GLU', '葡萄糖', 'mmol/L', '3.9', '6.1'),
('尿素氮', '002', 'nsd', 'BUN', '尿素氮', 'mmol/L', '2.9', '8.2'),
('肌酐', '003', 'jg', 'CREA', '肌酐', 'umol/L', '44', '133'),
('尿酸', '004', 'ns', 'UA', '尿酸', 'umol/L', '150', '440'),
('总蛋白', '005', 'zb', 'TP', '总蛋白', 'g/L', '60', '80'),
('白蛋白', '006', 'bd', 'ALB', '白蛋白', 'g/L', '35', '55'),
('谷丙转氨酶', '007', 'gbz', 'ALT', '谷丙转氨酶', 'U/L', '0', '40'),
('谷草转氨酶', '008', 'gc', 'AST', '谷草转氨酶', 'U/L', '0', '40'),
('总胆红素', '009', 'zdhs', 'TBIL', '总胆红素', 'umol/L', '3.4', '20.5'),
('直接胆红素', '010', 'jzhs', 'DBIL', '直接胆红素', 'umol/L', '0', '6.8');

-- 创建检验项目视图 (兼容旧代码中的 sys_jyxm)
DROP VIEW IF EXISTS sys_jyxm;
CREATE VIEW sys_jyxm AS 
SELECT bmid AS xmid, xmzwmc, xmdw, xmdm FROM sys_bbzl;

-- 添加标本种类数据（样本类型）
INSERT INTO sys_bbzl (bmsm, bm, pym) VALUES
('血清', '1', 'xq'),
('血浆', '2', 'xj'),
('尿液', '3', 'ny'),
('粪便', '4', 'fb'),
('脑脊液', '5', 'njy'),
('胸腹水', '6', 'xfs'),
('关节液', '7', 'gjy'),
('骨髓', '8', 'gs'),
('咽拭子', '9', 'ysz'),
('鼻拭子', '10', 'bsz'),
('痰液', '11', 'ty'),
('分泌物', '12', 'fmw'),
('其他', '99', 'qt');

INSERT INTO sys_bbzl (bmsm, bm, pym, xmdm, xmzwmc, xmdw, bjzdx, bjzgx) VALUES
('血糖', 'GLU', 'tang', 'GLU', '葡萄糖', 'mmol/L', '3.9', '6.1'),
('尿素氮', 'BUN', 'nsd', 'BUN', '尿素氮', 'mmol/L', '2.9', '8.2'),
('肌酐', 'Cr', 'jg', 'Cr', '肌酐', 'umol/L', '44', '133'),
('尿酸', 'UA', 'ns', 'UA', '尿酸', 'umol/L', '150', '440'),
('总蛋白', 'TP', 'zdb', 'TP', '总蛋白', 'g/L', '60', '80'),
('白蛋白', 'ALB', 'bd', 'ALB', '白蛋白', 'g/L', '35', '55'),
('谷丙转氨酶', 'ALT', 'gbzms', 'ALT', '谷丙转氨酶', 'U/L', '0', '40'),
('谷草转氨酶', 'AST', 'gszms', 'AST', '谷草转氨酶', 'U/L', '0', '40');

-- ============================================
-- 新增: 科室设置表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_kssz (
    ksid INT PRIMARY KEY AUTO_INCREMENT,
    ksdm VARCHAR(20) NOT NULL,
    ksmc VARCHAR(100),
    pym VARCHAR(50),
    ksxz VARCHAR(20),          -- 科室性质
    zxbz TINYINT DEFAULT 1,
    sybz TINYINT DEFAULT 1
);

INSERT INTO sys_kssz (ksdm, ksmc, pym) VALUES
('01', '检验科', 'yjk'),
('02', '内科', 'nk'),
('03', '外科', 'wk'),
('04', '妇产科', 'fck'),
('05', '儿科', 'ek');

-- ============================================
-- 新增: 员工设置表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_rysz (
    ryid INT PRIMARY KEY AUTO_INCREMENT,
    rydm VARCHAR(20) NOT NULL,
    ryxm VARCHAR(50),
    pym VARCHAR(50),
    ksdm VARCHAR(20),
    zxbz TINYINT DEFAULT 1,
    sybz TINYINT DEFAULT 1
);

INSERT INTO sys_rysz (rydm, ryxm, pym, ksdm) VALUES
('Y001', '张医生', 'zys', '01'),
('Y002', '李护士', 'lhs', '01'),
('Y003', '王检验师', 'wjys', '01');

-- ============================================
-- 新增: 工作组设置表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_gzzd (
    gzid INT PRIMARY KEY AUTO_INCREMENT,
    gzdm VARCHAR(20) NOT NULL,
    gzmc VARCHAR(100),
    pym VARCHAR(50),
    zxbz TINYINT DEFAULT 1,
    sybz TINYINT DEFAULT 1
);

INSERT INTO sys_gzzd (gzdm, gzmc, pym) VALUES
('01', '生化组', 'shz'),
('02', '免疫组', 'myz'),
('03', '常规组', 'cgz');

-- ============================================
-- 新增: 质控品表 (与SQL Server一致)
-- ============================================
CREATE TABLE IF NOT EXISTS sys_zkpd (
    zkpid INT PRIMARY KEY AUTO_INCREMENT,
    zkpmc VARCHAR(100),       -- 质控品名称
    pym VARCHAR(50),          -- 拼音码
    zkplx VARCHAR(20),       -- 质控品类型
    xmdm VARCHAR(50),        -- 项目代码
    xmzwmc VARCHAR(100),     -- 项目中文名
    bjzl VARCHAR(20),        -- 靶值低限
    bjzh VARCHAR(20),        -- 靶值高限
    sccj VARCHAR(100),       -- 生产厂家
    sxrq DATE,               -- 失效日期
    zxbz TINYINT DEFAULT 1,   -- 注销标志
    sybz TINYINT DEFAULT 1,   -- 使用标志
    sb_djid INT,             -- 设备ID (代码使用)
    zwmc VARCHAR(100),       -- 中文名 (代码使用)
    ywmc VARCHAR(100),       -- 英文名 (代码使用)
    zkpsm VARCHAR(500),      -- 质控品说明 (代码使用)
    ph VARCHAR(50),          -- 批号 (代码使用)
    syrq DATETIME            -- 使用日期 (代码使用)
);

-- 质控品测试数据
INSERT INTO sys_zkpd (zkpmc, pym, zkplx, xmdm, xmzwmc, bjzl, bjzh, sccj, sxrq, zxbz, sybz) VALUES
('Blood Glucose QC', 'tangzk', 'BIOCHEM', 'GLU', 'Glucose', '5.5', '6.5', 'Randox', '2026-12-31', 1, 1),
('Urea Nitrogen QC', 'nsdzk', 'BIOCHEM', 'BUN', 'Urea Nitrogen', '5.0', '6.5', 'Randox', '2026-12-31', 1, 1),
('Creatinine QC', 'jgzkgc', 'BIOCHEM', 'CREA', 'Creatinine', '80', '120', 'Bio-Rad', '2026-12-31', 1, 1),
('Hemoglobin QC', 'xqdbz', 'HEMA', 'HGB', 'Hemoglobin', '120', '180', 'Sysmex', '2026-12-31', 1, 1),
('Platelet QC', 'xxbz', 'HEMA', 'PLT', 'Platelet Count', '200', '400', 'Sysmex', '2026-12-31', 1, 1);

-- ============================================
-- 质控记录表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_zkjl (
    id INT PRIMARY KEY AUTO_INCREMENT,
    zkpid INT,
    xmdm VARCHAR(50),
    jyrq DATE,
    jyjg VARCHAR(20),
    pgjg VARCHAR(20),
    czy VARCHAR(50),
    czrq TIMESTAMP
);

-- 质控记录测试数据
INSERT INTO sys_zkjl (zkpid, xmdm, jyrq, jyjg, pgjg, czy, czrq) VALUES
(1, 'GLU', '2026-03-11', '5.8', '1', 'admin', CURRENT_TIMESTAMP),
(1, 'GLU', '2026-03-10', '6.0', '1', 'admin', CURRENT_TIMESTAMP),
(1, 'GLU', '2026-03-09', '5.5', '1', 'admin', CURRENT_TIMESTAMP),
(1, 'GLU', '2026-03-08', '6.2', '1', 'admin', CURRENT_TIMESTAMP),
(1, 'GLU', '2026-03-07', '5.9', '1', 'admin', CURRENT_TIMESTAMP),
(2, 'BUN', '2026-03-11', '5.5', '1', 'admin', CURRENT_TIMESTAMP),
(2, 'BUN', '2026-03-10', '5.8', '1', 'admin', CURRENT_TIMESTAMP),
(2, 'BUN', '2026-03-09', '6.1', '2', 'admin', CURRENT_TIMESTAMP);

-- ============================================
-- 新增: 项目组合主表
-- ============================================
CREATE TABLE IF NOT EXISTS bgxt_xmzh_zb (
    zhid INT PRIMARY KEY AUTO_INCREMENT,
    zhmc VARCHAR(100),         -- 组合名称
    pym VARCHAR(50),          -- 拼音码
    zhlx VARCHAR(20),          -- 组合类型
    zxbz TINYINT DEFAULT 1,    -- 启用标志
    sybz TINYINT DEFAULT 1    -- 使用标志
);

INSERT INTO bgxt_xmzh_zb (zhmc, pym, zhlx) VALUES
('血常规', 'xcg', '常规'),
('尿常规', 'ncg', '常规'),
('肝功能', 'ggn', '生化'),
('肾功能', 'sgn', '生化'),
('血脂', 'xz', '生化');

-- ============================================
-- 新增: 项目组合明细表
-- ============================================
CREATE TABLE IF NOT EXISTS bgxt_xmzh_mx (
    id INT PRIMARY KEY AUTO_INCREMENT,
    zhid INT,                  -- 组合ID
    xmdm VARCHAR(50),         -- 项目代码
    xmzwmc VARCHAR(100),      -- 项目名称
    xmdw VARCHAR(20),          -- 单位
    xh INT DEFAULT 0           -- 序号
);

INSERT INTO bgxt_xmzh_mx (zhid, xmdm, xmzwmc, xmdw, xh) VALUES
(1, 'WBC', '白细胞计数', '10^9/L', 1),
(1, 'RBC', '红细胞计数', '10^12/L', 2),
(1, 'HGB', '血红蛋白', 'g/L', 3),
(1, 'PLT', '血小板计数', '10^9/L', 4),
(2, 'URO', '尿胆原', 'umol/L', 1),
(2, 'BIL', '胆红素', 'umol/L', 2),
(3, 'ALT', '谷丙转氨酶', 'U/L', 1),
(3, 'AST', '谷草转氨酶', 'U/L', 2),
(3, 'TP', '总蛋白', 'g/L', 3),
(3, 'ALB', '白蛋白', 'g/L', 4);

-- ============================================
-- 新增: 检验结果表
-- ============================================
CREATE TABLE IF NOT EXISTS bgxt_jyjg (
    id INT PRIMARY KEY AUTO_INCREMENT,
    brxx_id INT,               -- 病人ID
    xmdm VARCHAR(50),         -- 项目代码
    xmzwmc VARCHAR(100),      -- 项目名称
    jyjg VARCHAR(100),        -- 检验结果
    jldw VARCHAR(20),         -- 结果单位
    bjzl VARCHAR(20),         -- 参考值低限
    bjzh VARCHAR(20),         -- 参考值高限
    ckz VARCHAR(20),          -- 参考值字符串
    jyri DATE,                -- 检验日期
    czy VARCHAR(50),          -- 操作员
    czri DATETIME             -- 操作时间
);

-- ============================================
-- 质控项目表 (zk_nyzkxm)
-- ============================================
CREATE TABLE IF NOT EXISTS zk_nyzkxm (
    zkxmid INT PRIMARY KEY AUTO_INCREMENT,
    zkpid INT,
    xmid INT,
    bz VARCHAR(20),
    bzc VARCHAR(20),
    zkdz VARCHAR(20),
    zkgz VARCHAR(20),
    dx_lx TINYINT DEFAULT 0,
    fhbz TINYINT DEFAULT 0,
    bc VARCHAR(20)
);

-- 质控项目测试数据
INSERT INTO zk_nyzkxm (zkpid, bz, bzc, zkdz, zkgz, dx_lx, fhbz) VALUES
(1, '5.5', '0.5', '4.5', '6.5', 0, 0),
(1, '6.0', '0.5', '5.0', '7.0', 0, 0),
(2, '5.5', '0.5', '4.5', '6.5', 0, 0),
(3, '100', '10', '70', '130', 0, 0),
(4, '150', '15', '120', '180', 0, 0);

-- ============================================
-- 质控评价表 (zk_nykpj)
-- ============================================
CREATE TABLE IF NOT EXISTS zk_nykpj (
    id INT PRIMARY KEY AUTO_INCREMENT,
    zkpid INT,
    pjmd VARCHAR(20),
    pjjg VARCHAR(20),
    pjjsyj TEXT,
    pjczy VARCHAR(50),
    pjrq DATE
);

-- 质控评价测试数据
INSERT INTO zk_nykpj (zkpid, pjmd, pjjg, pjjsyj, pjczy, pjrq) VALUES
(1, 'Excellent', 'Pass', 'QC results good, keep it up', 'admin', '2026-03-11'),
(2, 'Good', 'Pass', 'QC results normal', 'admin', '2026-03-10');

-- ============================================
-- 新增: 质控处理记录表 (zk_nyskcl)
-- ============================================
CREATE TABLE IF NOT EXISTS zk_nyskcl (
    id INT PRIMARY KEY AUTO_INCREMENT,
    zkxmid INT,               -- 质控项目ID
    zkcl TEXT,                 -- 处理措施
    czydm_clr VARCHAR(50),    -- 处理人
    ksrq DATETIME,              -- 开始日期
    jsrq DATETIME,             -- 结束日期
    clrq DATETIME              -- 处理日期
);

-- ============================================
-- 新增: 质控结果表 (zk_nyzkjg)
-- ============================================
CREATE TABLE IF NOT EXISTS zk_nyzkjg (
    id INT PRIMARY KEY AUTO_INCREMENT,
    zkxmid INT,               -- 质控项目ID
    yssj VARCHAR(50),         -- 原始数据
    yhsj VARCHAR(50),         -- 原始数据(合并)
    jssj VARCHAR(50),         -- 最终结果
    syrq DATE,                -- 使用日期
    sybz TINYINT DEFAULT 1,  -- 使用标志
    skbz TINYINT DEFAULT 0,   -- 失控标志
    jssj_date DATE           -- 结果日期
);

-- ============================================
-- Phase 1: 检验项目设置 - 完整sys_jyxm表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_jyxm_full (
    xmid INT PRIMARY KEY AUTO_INCREMENT,
    xmdm VARCHAR(10),
    xmzwmc VARCHAR(100),
    xmywmc VARCHAR(100),
    pym VARCHAR(30),
    qtdm VARCHAR(10),
    xmdw VARCHAR(10),
    xmjd INT DEFAULT 3,
    xmlx INT DEFAULT 0,
    jsbz TINYINT DEFAULT 0,
    xs DECIMAL(12,3) DEFAULT 1.000,
    sjxhl DECIMAL(12,3) DEFAULT 0.000,
    tybz TINYINT DEFAULT 0,
    dybz TINYINT DEFAULT 1,
    zsbz TINYINT DEFAULT 0,
    sfbz DECIMAL(10,2) DEFAULT 0.00,
    gzl DECIMAL(10,2) DEFAULT 0.00,
    his_fydm VARCHAR(50),
    his_jyxmmc VARCHAR(400),
    zsk_xmdm VARCHAR(200),
    zsk_xmmc VARCHAR(200),
    ItemType INT DEFAULT 0,
    lcyy VARCHAR(800)
);

INSERT IGNORE INTO sys_jyxm_full (xmdm, xmzwmc, pym, xmdw, xmjd) VALUES
('GLU', '葡萄糖', 'ptt', 'mmol/L', 3),
('BUN', '尿素氮', 'nsd', 'mmol/L', 3),
('CREA', '肌酐', 'jg', 'umol/L', 3),
('UA', '尿酸', 'ns', 'umol/L', 3),
('TP', '总蛋白', 'zdb', 'g/L', 3),
('ALB', '白蛋白', 'bd', 'g/L', 3),
('ALT', '谷丙转氨酶', 'gbzms', 'U/L', 3),
('AST', '谷草转氨酶', 'gszms', 'U/L', 3),
('TBIL', '总胆红素', 'zdhs', 'umol/L', 3),
('DBIL', '直接胆红素', 'zjhs', 'umol/L', 3),
('WBC', '白细胞计数', 'bxb', '10^9/L', 3),
('RBC', '红细胞计数', 'hxb', '10^12/L', 3),
('HGB', '血红蛋白', 'xgdb', 'g/L', 3),
('PLT', '血小板计数', 'xxb', '10^9/L', 3);

-- ============================================
-- Phase 1: 仪器-组合映射表
-- ============================================
CREATE TABLE IF NOT EXISTS bgxt_yqxmzh (
    sb_djid INT NOT NULL,
    zhid INT NOT NULL,
    zhsx INT DEFAULT 0,
    PRIMARY KEY (sb_djid, zhid)
);

INSERT IGNORE INTO bgxt_yqxmzh (sb_djid, zhid, zhsx) VALUES
(4, 3, 1),
(4, 4, 2),
(5, 1, 1),
(6, 2, 1);

-- ============================================
-- Phase 1: 参考范围表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_xmckz (
    id INT PRIMARY KEY AUTO_INCREMENT,
    xmid INT NOT NULL,
    bbsgbz TINYINT DEFAULT 0,
    bbzl INT,
    xbsgbz TINYINT DEFAULT 0,
    brxb INT,
    nlsgbz TINYINT DEFAULT 0,
    nllx INT,
    nlsx DECIMAL(12,3),
    nlxx DECIMAL(12,3),
    ckz VARCHAR(1000),
    ckzgx DECIMAL(12,3),
    ckzdx DECIMAL(12,3),
    bjzgx DECIMAL(12,3),
    bjzdx DECIMAL(12,3),
    jszgx DECIMAL(12,3),
    jszdx DECIMAL(12,3),
    fczgx DECIMAL(12,3),
    fczdx DECIMAL(12,3),
    zdshbz TINYINT DEFAULT 0,
    zdshgx DECIMAL(12,3),
    zdshdx DECIMAL(12,3),
    zdshcyqj INT,
    jgfctsbz TINYINT DEFAULT 0,
    sb_djid INT
);

INSERT IGNORE INTO sys_xmckz (xmid, ckz, ckzgx, ckzdx, bjzgx, bjzdx) VALUES
(1, '3.9-6.1', 6.1, 3.9, 6.1, 3.9),
(2, '2.9-8.2', 8.2, 2.9, 8.2, 2.9),
(3, '44-133', 133, 44, 133, 44),
(4, '150-440', 440, 150, 440, 150),
(5, '60-80', 80, 60, 80, 60),
(6, '35-55', 55, 35, 55, 35),
(7, '0-40', 40, 0, 40, 0),
(8, '0-40', 40, 0, 40, 0),
(11, '3.5-9.5', 9.5, 3.5, 9.5, 3.5),
(12, '4.0-5.5', 5.5, 4.0, 5.5, 4.0),
(13, '120-160', 160, 120, 160, 120),
(14, '100-300', 300, 100, 300, 100);

-- ============================================
-- Phase 1: 默认值表
-- ============================================
CREATE TABLE IF NOT EXISTS bgxt_xmmrz (
    xmid INT NOT NULL,
    sb_djid INT NOT NULL,
    mrz VARCHAR(50),
    mr TINYINT DEFAULT 0,
    PRIMARY KEY (xmid, sb_djid)
);

-- ============================================
-- Phase 1: 高低值标志表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_gdz (
    bhid INT PRIMARY KEY AUTO_INCREMENT,
    bh VARCHAR(10),
    bs INT DEFAULT 0,
    sybz TINYINT DEFAULT 0
);

INSERT IGNORE INTO sys_gdz (bhid, bh, bs, sybz) VALUES
(1, '↑', 1, 1),
(2, '↓', 0, 1),
(3, '↑↑', 3, 1),
(4, '↓↓', 2, 1);

-- ============================================
-- Phase 1: 标本类型字典(完整版)
-- ============================================
CREATE TABLE IF NOT EXISTS sys_bbzl_dict (
    bm INT PRIMARY KEY AUTO_INCREMENT,
    bmsm VARCHAR(50),
    pym VARCHAR(30),
    qtdm VARCHAR(10),
    xssx INT DEFAULT 0,
    whonet VARCHAR(10),
    his_bmdm VARCHAR(50),
    rqdm VARCHAR(50),
    rqlx VARCHAR(200),
    cjyq VARCHAR(1000)
);

INSERT IGNORE INTO sys_bbzl_dict (bmsm, pym, xssx) VALUES
('血清', 'xq', 1),
('血浆', 'xj', 2),
('全血', 'qx', 3),
('尿液', 'ny', 4),
('粪便', 'fb', 5),
('脑脊液', 'njy', 6),
('胸腹水', 'xfs', 7),
('关节液', 'gjy', 8),
('骨髓', 'gs', 9),
('咽拭子', 'ysz', 10),
('痰液', 'ty', 11),
('分泌物', 'fmw', 12),
('其他', 'qt', 99);

-- ============================================
-- Phase 1: 仪器-项目通道映射表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_cjdzb (
    sb_djid INT NOT NULL,
    xmid INT NOT NULL,
    xmdm VARCHAR(10),
    xs DECIMAL(10,2) DEFAULT 1.00,
    dyxh INT DEFAULT 0,
    yqxmdw VARCHAR(20),
    xmjc VARCHAR(50),
    PRIMARY KEY (sb_djid, xmid)
);

-- ============================================
-- Phase 1: 计算公式表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_jsgs (
    sb_djid INT NOT NULL,
    xmid INT NOT NULL,
    bds VARCHAR(500),
    bdssm VARCHAR(200),
    PRIMARY KEY (sb_djid, xmid)
);

-- ============================================
-- Phase 1: 组合-标本-颜色映射
-- ============================================
CREATE TABLE IF NOT EXISTS bgxt_zhbbys (
    zhid INT NOT NULL,
    bbzl INT,
    bqys INT,
    yssm VARCHAR(20),
    PRIMARY KEY (zhid, bbzl)
);

-- ============================================
-- Phase 1: 补充bgxt_xmzh_zb缺失字段
-- ============================================
-- MySQL不支持ALTER TABLE IF NOT EXISTS COLUMN，使用存储过程安全添加
DROP PROCEDURE IF EXISTS add_column_if_not_exists;
DELIMITER //
CREATE PROCEDURE add_column_if_not_exists()
BEGIN
    DECLARE col_count INT;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_zb' AND column_name = 'bbzl';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_zb ADD COLUMN bbzl INT; END IF;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_zb' AND column_name = 'qtdm';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_zb ADD COLUMN qtdm VARCHAR(10); END IF;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_zb' AND column_name = 'his_xmdm';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_zb ADD COLUMN his_xmdm VARCHAR(50); END IF;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_zb' AND column_name = 'his_zhmc';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_zb ADD COLUMN his_zhmc VARCHAR(100); END IF;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_zb' AND column_name = 'sfbz';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_zb ADD COLUMN sfbz DECIMAL(10,2) DEFAULT 0.00; END IF;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_zb' AND column_name = 'gzl';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_zb ADD COLUMN gzl INT DEFAULT 0; END IF;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_zb' AND column_name = 'qybz';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_zb ADD COLUMN qybz TINYINT DEFAULT 1; END IF;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_zb' AND column_name = 'lbid';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_zb ADD COLUMN lbid INT; END IF;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_zb' AND column_name = 'bqys';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_zb ADD COLUMN bqys INT; END IF;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_zb' AND column_name = 'yssm';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_zb ADD COLUMN yssm VARCHAR(20); END IF;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_zb' AND column_name = 'ybzxxg';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_zb ADD COLUMN ybzxxg INT; END IF;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_zb' AND column_name = 'ReportType';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_zb ADD COLUMN ReportType INT; END IF;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_zb' AND column_name = 'GroupType';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_zb ADD COLUMN GroupType VARCHAR(200); END IF;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_zb' AND column_name = 'GetSampleFromHIS';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_zb ADD COLUMN GetSampleFromHIS TINYINT DEFAULT 0; END IF;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_zb' AND column_name = 'DefaultResult';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_zb ADD COLUMN DefaultResult VARCHAR(400); END IF;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_zb' AND column_name = 'ProjectLevel';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_zb ADD COLUMN ProjectLevel INT; END IF;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_zb' AND column_name = 'ddsj';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_zb ADD COLUMN ddsj INT; END IF;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_zb' AND column_name = 'zh_zy';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_zb ADD COLUMN zh_zy VARCHAR(500); END IF;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_zb' AND column_name = 'zh_syz';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_zb ADD COLUMN zh_syz VARCHAR(500); END IF;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_zb' AND column_name = 'zh_cjyq';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_zb ADD COLUMN zh_cjyq VARCHAR(500); END IF;

    -- 补充bgxt_xmzh_mx的xmid字段
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_mx' AND column_name = 'xmid';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_mx ADD COLUMN xmid INT; END IF;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_mx' AND column_name = 'mrjg';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_mx ADD COLUMN mrjg VARCHAR(10); END IF;
    
    SELECT COUNT(*) INTO col_count FROM information_schema.columns 
    WHERE table_schema = DATABASE() AND table_name = 'bgxt_xmzh_mx' AND column_name = 'sb_djid';
    IF col_count = 0 THEN ALTER TABLE bgxt_xmzh_mx ADD COLUMN sb_djid INT; END IF;
END //
DELIMITER ;
CALL add_column_if_not_exists();
DROP PROCEDURE IF EXISTS add_column_if_not_exists;

-- ============================================
-- Phase 2: 样本状态变更日志
-- ============================================
CREATE TABLE IF NOT EXISTS sys_ybzt (
    brxx_id INT,
    old_ybzt INT,
    new_ybzt INT,
    czydm VARCHAR(200),
    czrq DATETIME
);

-- ============================================
-- Phase 2: 撤销审计日志表
-- ============================================
CREATE TABLE IF NOT EXISTS bgxt_brxx_log (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    brxx_tmh VARCHAR(50),
    brbh VARCHAR(50),
    brxm VARCHAR(50),
    brxb TINYINT,
    brnl VARCHAR(20),
    nllx VARCHAR(20),
    brlb TINYINT,
    syqk TINYINT,
    ksdm VARCHAR(20),
    brch VARCHAR(20),
    syh VARCHAR(20),
    bbzl VARCHAR(20),
    ybzt TINYINT,
    jyrq DATETIME,
    sfbz TINYINT,
    bz VARCHAR(500),
    qxshczy VARCHAR(50),
    qxshrq DATETIME
);

CREATE TABLE IF NOT EXISTS bgxt_jyjglog (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    brxx_id INT,
    xmid INT,
    jyjg VARCHAR(100),
    gdbj VARCHAR(10),
    ckz VARCHAR(50),
    czri DATETIME
);

-- ============================================
-- Phase 2: 仪器原始数据(采集数据)
-- ============================================
CREATE TABLE IF NOT EXISTS sys_cjysz_zb (
    cjid INT PRIMARY KEY AUTO_INCREMENT,
    hb_sb_djid INT,
    sb_djid INT,
    syh INT,
    cjrq DATETIME,
    zkbz TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sys_cjysz_mx (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cjid INT,
    xmid INT,
    jyjg VARCHAR(200),
    mgd VARCHAR(10),
    OD VARCHAR(50),
    CutOff VARCHAR(50)
);

-- ============================================
-- Phase 2: 打印日志
-- ============================================
CREATE TABLE IF NOT EXISTS bgxt_jgdyb (
    id INT PRIMARY KEY AUTO_INCREMENT,
    brxx_id INT,
    czydm VARCHAR(200),
    dyrq DATETIME,
    zd VARCHAR(20),
    ip VARCHAR(30)
);

-- ============================================
-- Phase 3: 报告模板表
-- ============================================
CREATE TABLE IF NOT EXISTS bgxt_bgmb (
    id INT PRIMARY KEY AUTO_INCREMENT,
    bgmbmc VARCHAR(255) NOT NULL,
    bgjglx VARCHAR(100),
    sb_djid INT,
    zhid INT,
    ksdm VARCHAR(20),
    sfbz TINYINT DEFAULT 0,
    bz TEXT,
    bgmbnr LONGTEXT,
    srrq DATETIME
);

-- ============================================
-- Phase 4: 危急值记录表
-- ============================================
CREATE TABLE IF NOT EXISTS bgxt_CriticalValue (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ReportId INT NOT NULL,
    CriticalValue TEXT NOT NULL,
    AddDate DATETIME NOT NULL,
    AddOperCode VARCHAR(50),
    AddOperName VARCHAR(100),
    xmid INT,
    CancelDate DATETIME NULL,
    CancelOperCode VARCHAR(50) NULL
);

-- ============================================
-- Phase 5: TAT设定表
-- ============================================
CREATE TABLE IF NOT EXISTS bgxt_tsxmtat (
    sb_djid INT NOT NULL,
    brlb INT NOT NULL,
    syqk INT NOT NULL,
    zhid INT NOT NULL,
    zhmc VARCHAR(100),
    TAT INT,
    PRIMARY KEY (sb_djid, brlb, syqk, zhid)
);

-- ============================================
-- Phase 5: HIS检验项目/计费表
-- ============================================
CREATE TABLE IF NOT EXISTS bgxt_his_xm (
    id INT PRIMARY KEY AUTO_INCREMENT,
    brxx_id INT NOT NULL,
    zhid INT,
    sf TINYINT DEFAULT 0,
    sfrq DATETIME NULL,
    czydm_sfr VARCHAR(50) NULL,
    zfbz TINYINT DEFAULT 0,
    zfrq DATETIME NULL,
    czydm_zfr VARCHAR(50) NULL,
    INDEX idx_brxx_id (brxx_id)
);

-- ============================================
-- Phase 5: 管座设置主表
-- ============================================
CREATE TABLE IF NOT EXISTS gzsz_zb (
    dlid INT PRIMARY KEY AUTO_INCREMENT,
    dlmc VARCHAR(255) NOT NULL,
    isuse INT DEFAULT 1
);

-- ============================================
-- Phase 5: 管座设置明细表
-- ============================================
CREATE TABLE IF NOT EXISTS gzsz_mx (
    xlbh INT PRIMARY KEY AUTO_INCREMENT,
    dlid INT NOT NULL,
    xlmc VARCHAR(255) NOT NULL,
    isuse INT DEFAULT 1,
    yxxh INT DEFAULT 0,
    tmhgs VARCHAR(100),
    tmfs VARCHAR(50),
    sflbz DECIMAL(10,2) DEFAULT 0.00,
    cjyq VARCHAR(255),
    zysx TEXT,
    sgys VARCHAR(100),
    clfdm VARCHAR(50),
    clfmc VARCHAR(255)
);

-- ============================================
-- Phase 5: 检验项目视图(材料费查询)
-- ============================================
CREATE OR REPLACE VIEW view_jian_yan_xiang_mu AS
    SELECT
        xmdm AS code,
        xmzwmc AS name,
        pym,
        sfbz AS dan_jia
    FROM sys_jyxm_full
    WHERE tybz = 0;

-- ============================================
-- Phase 5: 子系统名称表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_zxtmc (
    zxtid INT PRIMARY KEY,
    zxtjc VARCHAR(50),
    zxtmc VARCHAR(100),
    zxtbb VARCHAR(20),
    bz VARCHAR(500)
);

INSERT IGNORE INTO sys_zxtmc (zxtid, zxtjc, zxtmc) VALUES
(1, 'LIS', '检验信息系统'),
(2, 'QC', '质控系统'),
(3, 'SYS', '系统管理');

-- ============================================
-- Phase 5: 日志操作类型说明表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_rzztsm (
    zxtid INT NOT NULL,
    ztid INT NOT NULL,
    ztsm VARCHAR(255) NOT NULL,
    PRIMARY KEY (zxtid, ztid)
);

INSERT IGNORE INTO sys_rzztsm (zxtid, ztid, ztsm) VALUES
(1, 1, '样本录入'),
(1, 2, '样本审核'),
(1, 3, '报告打印'),
(1, 4, '样本取消'),
(2, 1, '质控录入'),
(2, 2, '质控评价'),
(3, 1, '用户登录'),
(3, 2, '参数修改'),
(3, 3, '系统锁定');

-- ============================================
-- Phase 5: 系统日志表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_rz (
    id INT PRIMARY KEY AUTO_INCREMENT,
    czrq DATETIME NOT NULL,
    czydm VARCHAR(50),
    sm TEXT,
    ztid INT,
    zxtid INT,
    INDEX idx_czrq (czrq),
    INDEX idx_czydm (czydm),
    INDEX idx_zxtid_ztid (zxtid, ztid)
);

-- 项目组合_预告时间设置 (bgxt_xmzh_ygsjsz)
CREATE TABLE IF NOT EXISTS bgxt_xmzh_ygsjsz (
    id INT PRIMARY KEY AUTO_INCREMENT,
    zhid INT NOT NULL,
    szlb INT DEFAULT 1 COMMENT '设置类别: 1=完成所需时间, 2=定点完成时间',
    qssj VARCHAR(20) COMMENT '起始时间',
    jssj VARCHAR(20) COMMENT '结束时间',
    ygrq INT DEFAULT 0 COMMENT '预告日期: 0=当日, 1=次日, 2=第三日',
    ygsj VARCHAR(20) COMMENT '完成时间',
    ddsj INT COMMENT '完成所需时间(分钟)',
    tybz TINYINT DEFAULT 0 COMMENT '停用标志: 0=启用, 1=停用',
    INDEX idx_zhid (zhid),
    INDEX idx_szlb (szlb)
);

-- ============================================
-- 报告模板配置表 (替代RTM模板)
-- ============================================
CREATE TABLE IF NOT EXISTS sys_report_template (
    template_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '模板ID',
    template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    template_type VARCHAR(50) NOT NULL COMMENT '模板类型:single_col/double_col/chart',
    template_code VARCHAR(50) NOT NULL COMMENT '模板代码',
    description VARCHAR(200) COMMENT '模板描述',
    config JSON NOT NULL COMMENT '模板配置JSON',
    mr INT DEFAULT 0 COMMENT '默认模板标志',
    sycx INT DEFAULT 101 COMMENT '使用程序',
    czyxm VARCHAR(50) COMMENT '操作员姓名',
    gxrq DATETIME COMMENT '更新日期',
    status INT DEFAULT 1 COMMENT '状态:0禁用,1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_template_code (template_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报告模板配置表';

-- ============================================
-- 插入默认模板数据
-- ============================================
INSERT INTO sys_report_template (template_name, template_type, template_code, description, config, mr, sycx) VALUES
(
    '标准单列报告',
    'single_col',
    'STD_SINGLE_COL',
    '标准单列报告模板，适用于大多数检验项目',
    '{"hospitalName": "医院名称", "reportTitle": "检验报告单", "showLogo": true, "pageSize": "A4"}',
    1,
    101
),
(
    '标准双列报告',
    'double_col',
    'STD_DOUBLE_COL',
    '标准双列报告模板，适用于项目较多的检验组合',
    '{"hospitalName": "医院名称", "reportTitle": "检验报告单", "showLogo": true, "pageSize": "A4"}',
    0,
    101
);

-- ============================================
-- 科室仪器分配表 (对应旧系统 p_ksyqfpsz)
-- 用于将仪器分配到检验科室
-- 注意：原始系统表名为 bgxt_ksyqsz
-- ============================================
CREATE TABLE IF NOT EXISTS bgxt_ksyqsz (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ksdm VARCHAR(20) NOT NULL COMMENT '科室代码',
    sb_djid INT NOT NULL COMMENT '设备ID',
    gxrq DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_ksdm_sbdjid (ksdm, sb_djid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室仪器分配表';

-- ============================================
-- 特殊标本设置表 (对应旧系统 p_tsbbsz)
-- 用于配置特殊标本（溶血/乳糜/黄疸等）处理规则
-- ============================================
CREATE TABLE IF NOT EXISTS bgxt_tsbbsz (
    id INT PRIMARY KEY AUTO_INCREMENT,
    mkid INT NOT NULL COMMENT '类型ID：0-溶血, 1-乳糜, 2-黄疸, 3-其他',
    xmid INT NOT NULL COMMENT '项目ID',
    mksm VARCHAR(100) COMMENT '类型说明',
    gxrq DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_mkid_xmid (mkid, xmid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='特殊标本设置表';

-- ============================================
-- 项目仪器关联表 (对应旧系统 P_xmsszhyqsz)
-- 用于建立检验项目与仪器之间的对应关系
-- ============================================
CREATE TABLE IF NOT EXISTS bgxt_xmssyq (
    id INT PRIMARY KEY AUTO_INCREMENT,
    xmid INT NOT NULL COMMENT '项目ID',
    sb_djid INT NOT NULL COMMENT '设备ID',
    zhid INT COMMENT '组合ID（可选）',
    gxrq DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_xmid_sbdjid (xmid, sb_djid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目仪器关联表';

-- ============================================
-- 插入特殊标本类型默认数据
-- ============================================
INSERT IGNORE INTO bgxt_tsbbsz (mkid, mksm) VALUES 
    (0, '溶血'),
    (1, '乳糜'),
    (2, '黄疸'),
    (3, '其他');
