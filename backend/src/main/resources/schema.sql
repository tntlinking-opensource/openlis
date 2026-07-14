-- ============================================================
-- OpenLIS 完整数据库初始化脚本
-- 基于实体类定义 + 原 schema.sql 整合生成
-- 数据库: MySQL 8.0+, charset utf8mb4
-- 最后更新: 2026-07-14
-- ============================================================

-- ============================================================
-- 1. 仪器设备表 (sys_sbdjb) — 对应 Instrument 实体
-- ============================================================
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
    bgbh VARCHAR(50),
    bgmc VARCHAR(100),
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO sys_sbdjb (sbdm, sbmc, sbbm, ksdm, gzzdm, pym, zxbz, tybz) VALUES
('BC5800', 'BC5800 Blood Cell Analyzer', 'BC5800', '01', '0001', 'BC5800', 1, 0),
('AU-400', 'AU-400 Biochemistry Analyzer', 'AU-400', '01', '0002', 'AU400', 1, 0),
('LX-5000', 'LX-5000 Urine Analyzer', 'LX-5000', '01', '0003', 'LX5000', 1, 0),
('INS001', '生化分析仪', 'SH-001', '01', '01', 'shfxy', 1, 0),
('INS002', '血常规分析仪', 'XC-001', '01', '01', 'xcgfxy', 1, 0),
('INS003', '尿液分析仪', 'NY-001', '02', '02', 'nyfxy', 1, 0);

-- ============================================================
-- 2. 操作员代码表 (sys_czydm) — 对应 SysCzydm 实体
-- ============================================================
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
    ysbz TINYINT DEFAULT 0,
    czybz TINYINT DEFAULT 0,
    glybz TINYINT DEFAULT 0,
    sybz TINYINT DEFAULT 1,
    gzzdm VARCHAR(20),
    dzqm BLOB,
    czysfzhm VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO sys_czydm (czydm, czyxm, czymm, sybz, glybz, czybz) VALUES
('admin', '系统管理员', 'admin123', 1, 1, 1),
('test', '测试用户', 'test', 1, 0, 1);

-- ============================================================
-- 3. 操作员权限表 (sys_czyqx) — 对应 SysCzyqx 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_czyqx (
    czydm VARCHAR(50) NOT NULL,
    qxxldm VARCHAR(50) NOT NULL,
    PRIMARY KEY (czydm, qxxldm)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 4. 系统配置表 (sys_config) — 对应 SysConfig 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_config (
    id INT PRIMARY KEY AUTO_INCREMENT,
    w_yydm VARCHAR(50),
    yymc VARCHAR(200),
    jykksdm VARCHAR(50),
    his_connectbz INT DEFAULT 0,
    his_connectlevel INT DEFAULT 0,
    tj_connectbz INT DEFAULT 0,
    tj_jghcbz INT DEFAULT 0,
    ysz_jghcbz INT DEFAULT 0,
    ysz_connectbz INT DEFAULT 0,
    qtxt_jghcbz INT DEFAULT 0,
    websc INT DEFAULT 0,
    gdsj INT DEFAULT 0,
    his_connect_ybzx INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 5. 系统参数配置表 (sys_xtsz) — 对应 SysXtsz 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_xtsz (
    xtsz_id INT PRIMARY KEY AUTO_INCREMENT,
    xtsz_key VARCHAR(100) NOT NULL,
    xtsz_value VARCHAR(500),
    xtsz_desc VARCHAR(200),
    lwbz TINYINT DEFAULT 0,
    bxt TINYINT DEFAULT 0,
    zyfykz INT DEFAULT 0,
    UNIQUE KEY uk_xtsz_key (xtsz_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO sys_xtsz (xtsz_key, xtsz_value, xtsz_desc) VALUES
('hospital_name', '测试医院', '医院名称'),
('report_title', 'LIS检验报告单', '报告标题');

-- ============================================================
-- 6. 报告合并组配置表 (sys_bghbzb) — 对应 SysBghbzb 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_bghbzb (
    hbid INT PRIMARY KEY AUTO_INCREMENT,
    hbmc VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 7. 报告合并明细表 (sys_bghbmx) — 对应 SysBghbmx 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_bghbmx (
    mxid INT PRIMARY KEY AUTO_INCREMENT,
    hbid INT,
    sb_djid INT,
    sybz TINYINT DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 8. 病人类型字典表 (sys_brlb) — 对应 SysBrlb 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_brlb (
    bm INT PRIMARY KEY AUTO_INCREMENT,
    bmsm VARCHAR(100),
    pym VARCHAR(50),
    qtdm VARCHAR(20),
    sjlyfs INT,
    sjlyfsms VARCHAR(200),
    mrksbz TINYINT DEFAULT 0,
    mrksdm VARCHAR(50),
    mrksmc VARCHAR(100),
    mrysbz TINYINT DEFAULT 0,
    mrysdm VARCHAR(50),
    mrysmc VARCHAR(100),
    xh INT DEFAULT 0,
    tybz TINYINT DEFAULT 0,
    jkbz TINYINT DEFAULT 0,
    jgxx_bz TINYINT DEFAULT 0,
    jgxx VARCHAR(500),
    qxkz TINYINT DEFAULT 0,
    qxmc VARCHAR(100),
    brlby INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO sys_brlb (bm, bmsm, pym) VALUES
(1, '门诊', 'mz'),
(2, '住院', 'zy'),
(3, '体检', 'tj'),
(4, '急诊', 'jz'),
(5, '科研', 'ky');

-- ============================================================
-- 9. 患者/样本信息表 (bgxt_brxx) — 对应 BgxtBrxx 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS bgxt_brxx (
    brxx_id INT PRIMARY KEY AUTO_INCREMENT,
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
    bbzl INT,
    ybzt TINYINT DEFAULT 0,
    jyrq DATETIME,
    shrq DATETIME,
    sfbz TINYINT DEFAULT 0,
    bz VARCHAR(500),
    bz2 VARCHAR(500),
    tjdw VARCHAR(200),
    zjhm VARCHAR(50),
    lxfs VARCHAR(100),
    bbxt VARCHAR(50),
    czy VARCHAR(50),
    czrq DATETIME,
    lczd VARCHAR(500),
    jyys VARCHAR(50),
    sjys VARCHAR(50),
    shys VARCHAR(50),
    sb_djid INT,
    bgbh VARCHAR(50),
    bgmc VARCHAR(100),
    bgbt VARCHAR(200),
    bgyj VARCHAR(500),
    bgjglx INT,
    yczt INT DEFAULT 0,
    dycs INT DEFAULT 0,
    dybz INT DEFAULT 0,
    bgrq DATETIME,
    INDEX idx_tmh (brxx_tmh),
    INDEX idx_jyrq (jyrq),
    INDEX idx_ybzt (ybzt)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO bgxt_brxx (brxx_tmh, brbh, brxm, brxb, brnl, nllx, brlb, syqk, ksdm, brch, syh, bbzl, ybzt, jyrq, sfbz, czy) VALUES
('TM20260310001', 'BR001', '张三', 1, '35', '岁', 1, 1, '01', '101', '202603100001', 1, 1, NOW(), 1, 'admin'),
('TM20260310002', 'BR002', '李四', 2, '28', '岁', 1, 0, '02', '202', '202603100002', 2, 0, NOW(), 1, 'admin'),
('TM20260310003', 'BR003', '王五', 1, '45', '岁', 2, 1, '01', '305', '202603100003', 3, 2, NOW(), 1, 'admin');

-- ============================================================
-- 10. 检验项目/标本种类表 (sys_bbzl) — 双用途表
--     视图 sys_jyxm 基于此表创建
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_bbzl (
    bmid INT PRIMARY KEY AUTO_INCREMENT,
    bmsm VARCHAR(100),
    bm VARCHAR(20),
    pym VARCHAR(50),
    xmdm VARCHAR(50),
    xmzwmc VARCHAR(100),
    xmdw VARCHAR(20),
    xmdw2 VARCHAR(20),
    bjzdx VARCHAR(20),
    bjzgx VARCHAR(20),
    ckzdx VARCHAR(20),
    ckzgx VARCHAR(20),
    ckz VARCHAR(20),
    zxbz TINYINT DEFAULT 1,
    sybz TINYINT DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO sys_bbzl (bmsm, bm, pym, xmdm, xmzwmc, xmdw, bjzdx, bjzgx) VALUES
('葡萄糖', '001', 'pt', 'GLU', '葡萄糖', 'mmol/L', '3.9', '6.1'),
('尿素氮', '002', 'nsd', 'BUN', '尿素氮', 'mmol/L', '2.9', '8.2'),
('肌酐', '003', 'jg', 'CREA', '肌酐', 'umol/L', '44', '133'),
('尿酸', '004', 'ns', 'UA', '尿酸', 'umol/L', '150', '440'),
('总蛋白', '005', 'zb', 'TP', '总蛋白', 'g/L', '60', '80'),
('白蛋白', '006', 'bd', 'ALB', '白蛋白', 'g/L', '35', '55'),
('谷丙转氨酶', '007', 'gbz', 'ALT', '谷丙转氨酶', 'U/L', '0', '40'),
('谷草转氨酶', '008', 'gc', 'AST', '谷草转氨酶', 'U/L', '0', '40'),
('总胆红素', '009', 'zdhs', 'TBIL', '总胆红素', 'umol/L', '3.4', '20.5'),
('直接胆红素', '010', 'jzhs', 'DBIL', '直接胆红素', 'umol/L', '0', '6.8'),
('血清', '1', 'xq', NULL, NULL, NULL, NULL, NULL),
('血浆', '2', 'xj', NULL, NULL, NULL, NULL, NULL),
('尿液', '3', 'ny', NULL, NULL, NULL, NULL, NULL),
('粪便', '4', 'fb', NULL, NULL, NULL, NULL, NULL),
('脑脊液', '5', 'njy', NULL, NULL, NULL, NULL, NULL),
('胸腹水', '6', 'xfs', NULL, NULL, NULL, NULL, NULL),
('关节液', '7', 'gjy', NULL, NULL, NULL, NULL, NULL),
('骨髓', '8', 'gs', NULL, NULL, NULL, NULL, NULL),
('咽拭子', '9', 'ysz', NULL, NULL, NULL, NULL, NULL),
('鼻拭子', '10', 'bsz', NULL, NULL, NULL, NULL, NULL),
('痰液', '11', 'ty', NULL, NULL, NULL, NULL, NULL),
('分泌物', '12', 'fmw', NULL, NULL, NULL, NULL, NULL),
('其他', '99', 'qt', NULL, NULL, NULL, NULL, NULL);

-- 检验项目视图 (兼容旧代码中的 sys_jyxm)
DROP VIEW IF EXISTS sys_jyxm;
CREATE VIEW sys_jyxm AS
SELECT bmid AS xmid, xmzwmc, xmdw, xmdm, pym FROM sys_bbzl;

-- ============================================================
-- 11. 科室设置表 (sys_kssz) — 对应 SysKssz 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_kssz (
    ksid INT PRIMARY KEY AUTO_INCREMENT,
    ksdm VARCHAR(20) NOT NULL,
    ksmc VARCHAR(100),
    pym VARCHAR(50),
    ksxz VARCHAR(20),
    zxbz TINYINT DEFAULT 1,
    sybz TINYINT DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO sys_kssz (ksdm, ksmc, pym) VALUES
('01', '检验科', 'yjk'),
('02', '内科', 'nk'),
('03', '外科', 'wk'),
('04', '妇产科', 'fck'),
('05', '儿科', 'ek');

-- ============================================================
-- 12. 员工设置表 (sys_rysz) — 对应 SysRysz 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_rysz (
    ryid INT PRIMARY KEY AUTO_INCREMENT,
    rydm VARCHAR(20) NOT NULL,
    ryxm VARCHAR(50),
    pym VARCHAR(50),
    ksdm VARCHAR(20),
    zxbz TINYINT DEFAULT 1,
    sybz TINYINT DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO sys_rysz (rydm, ryxm, pym, ksdm) VALUES
('Y001', '张医生', 'zys', '01'),
('Y002', '李护士', 'lhs', '01'),
('Y003', '王检验师', 'wjys', '01');

-- ============================================================
-- 13. 工作组字典表 (sys_gzzd) — 对应 SysGzzd 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_gzzd (
    gzid INT PRIMARY KEY AUTO_INCREMENT,
    gzdm VARCHAR(20) NOT NULL,
    gzmc VARCHAR(100),
    pym VARCHAR(50),
    zxbz TINYINT DEFAULT 1,
    sybz TINYINT DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO sys_gzzd (gzdm, gzmc, pym) VALUES
('01', '生化组', 'shz'),
('02', '免疫组', 'myz'),
('03', '常规组', 'cgz');

-- ============================================================
-- 14. 工作组设置表 (sys_gzzsz) — 对应 SysGzzsz / SysGzz 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_gzzsz (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ssksdm VARCHAR(50),
    gzzdm VARCHAR(50) NOT NULL,
    gzzmc VARCHAR(100),
    pym VARCHAR(50),
    gzzlx INT DEFAULT 0,
    his_ksdm VARCHAR(50),
    xh INT DEFAULT 0,
    sybz TINYINT DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 15. 质控品表 (sys_zkpd) — 对应 SysZkpd 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_zkpd (
    zkpid INT PRIMARY KEY AUTO_INCREMENT,
    zkpmc VARCHAR(100),
    pym VARCHAR(50),
    zkplx VARCHAR(20),
    xmdm VARCHAR(50),
    xmzwmc VARCHAR(100),
    bjzl VARCHAR(20),
    bjzh VARCHAR(20),
    sccj VARCHAR(100),
    sxrq DATE,
    zxbz TINYINT DEFAULT 1,
    sybz TINYINT DEFAULT 1,
    sb_djid INT,
    zwmc VARCHAR(100),
    ywmc VARCHAR(100),
    zkpsm VARCHAR(500),
    ph VARCHAR(50),
    syrq DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO sys_zkpd (zkpmc, pym, zkplx, xmdm, xmzwmc, bjzl, bjzh, sccj, sxrq, zxbz, sybz) VALUES
('Blood Glucose QC', 'tangzk', 'BIOCHEM', 'GLU', 'Glucose', '5.5', '6.5', 'Randox', '2026-12-31', 1, 1),
('Urea Nitrogen QC', 'nsdzk', 'BIOCHEM', 'BUN', 'Urea Nitrogen', '5.0', '6.5', 'Randox', '2026-12-31', 1, 1),
('Creatinine QC', 'jgzkgc', 'BIOCHEM', 'CREA', 'Creatinine', '80', '120', 'Bio-Rad', '2026-12-31', 1, 1),
('Hemoglobin QC', 'xqdbz', 'HEMA', 'HGB', 'Hemoglobin', '120', '180', 'Sysmex', '2026-12-31', 1, 1),
('Platelet QC', 'xxbz', 'HEMA', 'PLT', 'Platelet Count', '200', '400', 'Sysmex', '2026-12-31', 1, 1);

-- ============================================================
-- 16. 质控记录表 (sys_zkjl) — 对应 SysZkjl 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_zkjl (
    id INT PRIMARY KEY AUTO_INCREMENT,
    zkpid INT,
    xmdm VARCHAR(50),
    jyrq DATE,
    jyjg VARCHAR(20),
    pgjg VARCHAR(20),
    czy VARCHAR(50),
    czrq TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO sys_zkjl (zkpid, xmdm, jyrq, jyjg, pgjg, czy, czrq) VALUES
(1, 'GLU', '2026-03-11', '5.8', '1', 'admin', CURRENT_TIMESTAMP),
(1, 'GLU', '2026-03-10', '6.0', '1', 'admin', CURRENT_TIMESTAMP),
(1, 'GLU', '2026-03-09', '5.5', '1', 'admin', CURRENT_TIMESTAMP),
(1, 'GLU', '2026-03-08', '6.2', '1', 'admin', CURRENT_TIMESTAMP),
(1, 'GLU', '2026-03-07', '5.9', '1', 'admin', CURRENT_TIMESTAMP),
(2, 'BUN', '2026-03-11', '5.5', '1', 'admin', CURRENT_TIMESTAMP),
(2, 'BUN', '2026-03-10', '5.8', '1', 'admin', CURRENT_TIMESTAMP),
(2, 'BUN', '2026-03-09', '6.1', '2', 'admin', CURRENT_TIMESTAMP);

-- ============================================================
-- 17. 项目组合主表 (bgxt_xmzh_zb) — 对应 BgxtXmzhZb 实体
--     直接包含所有字段（不再使用ALTER TABLE补字段）
-- ============================================================
CREATE TABLE IF NOT EXISTS bgxt_xmzh_zb (
    zhid INT PRIMARY KEY AUTO_INCREMENT,
    zhmc VARCHAR(100),
    pym VARCHAR(50),
    zhlx VARCHAR(20),
    zxbz TINYINT DEFAULT 1,
    sybz TINYINT DEFAULT 1,
    bbzl INT,
    qtdm VARCHAR(10),
    his_xmdm VARCHAR(50),
    his_zhmc VARCHAR(100),
    sfbz DECIMAL(10,2) DEFAULT 0.00,
    gzl INT DEFAULT 0,
    qybz TINYINT DEFAULT 1,
    lbid INT,
    bqys INT,
    yssm VARCHAR(20),
    ybzxxg INT,
    ReportType INT,
    GroupType VARCHAR(200),
    GetSampleFromHIS TINYINT DEFAULT 0,
    DefaultResult VARCHAR(400),
    ProjectLevel INT,
    ddsj INT,
    zh_zy VARCHAR(500),
    zh_syz VARCHAR(500),
    zh_cjyq VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO bgxt_xmzh_zb (zhmc, pym, zhlx) VALUES
('血常规', 'xcg', '常规'),
('尿常规', 'ncg', '常规'),
('肝功能', 'ggn', '生化'),
('肾功能', 'sgn', '生化'),
('血脂', 'xz', '生化');

-- ============================================================
-- 18. 项目组合明细表 (bgxt_xmzh_mx) — 对应 BgxtXmzhMx 实体
--     直接包含所有字段
-- ============================================================
CREATE TABLE IF NOT EXISTS bgxt_xmzh_mx (
    id INT PRIMARY KEY AUTO_INCREMENT,
    zhid INT,
    xmdm VARCHAR(50),
    xmzwmc VARCHAR(100),
    xmdw VARCHAR(20),
    xh INT DEFAULT 0,
    xmid INT,
    mrjg VARCHAR(10),
    sb_djid INT,
    INDEX idx_zhid (zhid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO bgxt_xmzh_mx (zhid, xmdm, xmzwmc, xmdw, xh) VALUES
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

-- ============================================================
-- 19. 项目组合预告时间设置 (bgxt_xmzh_ygsjsz) — 对应 BgxtXmzhYgsjsz 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS bgxt_xmzh_ygsjsz (
    id INT PRIMARY KEY AUTO_INCREMENT,
    zhid INT NOT NULL,
    szlb INT DEFAULT 1 COMMENT '设置类别: 1=完成所需时间, 2=定点完成时间',
    qssj VARCHAR(20) COMMENT '起始时间',
    jssj VARCHAR(20) COMMENT '结束时间',
    ygrq INT DEFAULT 0 COMMENT '预告日期: 0=当日, 1=次日',
    ygsj VARCHAR(20) COMMENT '完成时间',
    ddsj INT COMMENT '完成所需时间(分钟)',
    tybz TINYINT DEFAULT 0 COMMENT '停用标志: 0=启用, 1=停用',
    INDEX idx_zhid (zhid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 20. 检验结果表 (bgxt_jyjg) — 对应 BgxtJyjg 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS bgxt_jyjg (
    id INT PRIMARY KEY AUTO_INCREMENT,
    brxx_id INT,
    xmdm VARCHAR(50),
    xmzwmc VARCHAR(100),
    jyjg VARCHAR(100),
    jldw VARCHAR(20),
    bjzl VARCHAR(20),
    bjzh VARCHAR(20),
    ckz VARCHAR(20),
    jyri DATE,
    czy VARCHAR(50),
    czri DATETIME,
    INDEX idx_brxx_id (brxx_id),
    INDEX idx_jyri (jyri)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 21. 检验结果变更日志 (bgxt_jyjglog) — 对应 BgxtJyjglog 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS bgxt_jyjglog (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    brxx_id INT,
    xmid INT,
    jyjg VARCHAR(100),
    gdbj VARCHAR(10),
    ckz VARCHAR(50),
    czri DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 22. 质控项目表 (zk_nyzkxm) — 对应 ZkNyzkxm 实体
-- ============================================================
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO zk_nyzkxm (zkpid, bz, bzc, zkdz, zkgz, dx_lx, fhbz) VALUES
(1, '5.5', '0.5', '4.5', '6.5', 0, 0),
(1, '6.0', '0.5', '5.0', '7.0', 0, 0),
(2, '5.5', '0.5', '4.5', '6.5', 0, 0),
(3, '100', '10', '70', '130', 0, 0),
(4, '150', '15', '120', '180', 0, 0);

-- ============================================================
-- 23. 质控评价表 (zk_nykpj) — 无对应实体，保留
-- ============================================================
CREATE TABLE IF NOT EXISTS zk_nykpj (
    id INT PRIMARY KEY AUTO_INCREMENT,
    zkpid INT,
    pjmd VARCHAR(20),
    pjjg VARCHAR(20),
    pjjsyj TEXT,
    pjczy VARCHAR(50),
    pjrq DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO zk_nykpj (zkpid, pjmd, pjjg, pjjsyj, pjczy, pjrq) VALUES
(1, 'Excellent', 'Pass', 'QC results good, keep it up', 'admin', '2026-03-11'),
(2, 'Good', 'Pass', 'QC results normal', 'admin', '2026-03-10');

-- ============================================================
-- 24. 质控处理记录表 (zk_nyskcl) — 对应 ZkNyskcl 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS zk_nyskcl (
    id INT PRIMARY KEY AUTO_INCREMENT,
    zkxmid INT,
    zkcl TEXT,
    czydm_clr VARCHAR(50),
    ksrq DATETIME,
    jsrq DATETIME,
    clrq DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 25. 质控结果表 (zk_nyzkjg) — 对应 ZkNyzkjg 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS zk_nyzkjg (
    id INT PRIMARY KEY AUTO_INCREMENT,
    zkxmid INT,
    yssj VARCHAR(50),
    yhsj VARCHAR(50),
    jssj VARCHAR(50),
    syrq DATE,
    sybz TINYINT DEFAULT 1,
    skbz TINYINT DEFAULT 0,
    jssj_date DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 26. 检验项目完整配置表 (sys_jyxm_full) — 对应 SysJyxmFull 实体
-- ============================================================
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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

-- 检验项目视图 (材料费查询)
DROP VIEW IF EXISTS view_jian_yan_xiang_mu;
CREATE VIEW view_jian_yan_xiang_mu AS
    SELECT xmdm AS code, xmzwmc AS name, pym, sfbz AS dan_jia
    FROM sys_jyxm_full
    WHERE tybz = 0;

-- ============================================================
-- 27. 仪器-组合映射表 (bgxt_yqxmzh) — 对应 BgxtYqxmzh 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS bgxt_yqxmzh (
    sb_djid INT NOT NULL,
    zhid INT NOT NULL,
    zhsx INT DEFAULT 0,
    PRIMARY KEY (sb_djid, zhid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO bgxt_yqxmzh (sb_djid, zhid, zhsx) VALUES
(4, 3, 1), (4, 4, 2), (5, 1, 1), (6, 2, 1);

-- ============================================================
-- 28. 参考范围表 (sys_xmckz) — 对应 SysXmckz 实体
-- ============================================================
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
    sb_djid INT,
    INDEX idx_xmid (xmid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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

-- ============================================================
-- 29. 项目默认值表 (bgxt_xmmrz) — 对应 BgxtXmmrz 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS bgxt_xmmrz (
    xmid INT NOT NULL,
    sb_djid INT NOT NULL,
    mrz VARCHAR(50),
    mr TINYINT DEFAULT 0,
    PRIMARY KEY (xmid, sb_djid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 30. 高低值标志表 (sys_gdz) — 对应 SysGdz 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_gdz (
    bhid INT PRIMARY KEY AUTO_INCREMENT,
    bh VARCHAR(10),
    bs INT DEFAULT 0,
    sybz TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO sys_gdz (bhid, bh, bs, sybz) VALUES
(1, '↑', 1, 1), (2, '↓', 0, 1), (3, '↑↑', 3, 1), (4, '↓↓', 2, 1);

-- ============================================================
-- 31. 标本类型字典表 (sys_bbzl_dict) — 对应 SysBbzlDict 实体
-- ============================================================
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO sys_bbzl_dict (bmsm, pym, xssx) VALUES
('血清', 'xq', 1), ('血浆', 'xj', 2), ('全血', 'qx', 3),
('尿液', 'ny', 4), ('粪便', 'fb', 5), ('脑脊液', 'njy', 6),
('胸腹水', 'xfs', 7), ('关节液', 'gjy', 8), ('骨髓', 'gs', 9),
('咽拭子', 'ysz', 10), ('痰液', 'ty', 11), ('分泌物', 'fmw', 12),
('其他', 'qt', 99);

-- ============================================================
-- 32. 仪器-项目通道映射表 (sys_cjdzb) — 对应 SysCjdzb 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_cjdzb (
    sb_djid INT NOT NULL,
    xmid INT NOT NULL,
    xmdm VARCHAR(10),
    xs DECIMAL(10,2) DEFAULT 1.00,
    dyxh INT DEFAULT 0,
    yqxmdw VARCHAR(20),
    PRIMARY KEY (sb_djid, xmid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 33. 计算公式表 (sys_jsgs) — 对应 SysJsgs 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_jsgs (
    sb_djid INT NOT NULL,
    xmid INT NOT NULL,
    bds VARCHAR(500),
    bdssm VARCHAR(200),
    PRIMARY KEY (sb_djid, xmid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 34. 组合-标本-颜色映射 (bgxt_zhbbys) — 无对应实体，保留
-- ============================================================
CREATE TABLE IF NOT EXISTS bgxt_zhbbys (
    zhid INT NOT NULL,
    bbzl INT,
    bqys INT,
    yssm VARCHAR(20),
    PRIMARY KEY (zhid, bbzl)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 35. 样本状态变更日志 (sys_ybzt) — 对应 SysYbzt 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_ybzt (
    brxx_id INT,
    old_ybzt INT,
    new_ybzt INT,
    czydm VARCHAR(200),
    czrq DATETIME,
    INDEX idx_brxx_id (brxx_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 36. 样本撤销审计日志 (bgxt_brxx_log) — 对应 BgxtBrxxLog 实体
-- ============================================================
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 37. 仪器原始数据主表 (sys_cjysz_zb) — 对应 SysCjyszZb 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_cjysz_zb (
    cjid INT PRIMARY KEY AUTO_INCREMENT,
    hb_sb_djid INT,
    sb_djid INT,
    syh INT,
    cjrq DATETIME,
    zkbz TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 38. 仪器原始数据明细表 (sys_cjysz_mx) — 对应 SysCjyszMx 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_cjysz_mx (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cjid INT,
    xmid INT,
    jyjg VARCHAR(200),
    mgd VARCHAR(10),
    OD VARCHAR(50),
    CutOff VARCHAR(50),
    INDEX idx_cjid (cjid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 39. 仪器原始数据替换设置 (sys_cjysz_settings) — 对应 SysCjyszSettings 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_cjysz_settings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    sb_djid INT,
    xmid INT,
    original_value VARCHAR(200),
    replace_value VARCHAR(200)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 40. 打印日志表 (bgxt_jgdyb) — 对应 BgxtJgdyb 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS bgxt_jgdyb (
    id INT PRIMARY KEY AUTO_INCREMENT,
    brxx_id INT,
    czydm VARCHAR(200),
    dyrq DATETIME,
    zd VARCHAR(20),
    ip VARCHAR(30)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 41. 报告模板表 (bgxt_bgmb) — 对应 BgxtBgmb 实体
-- ============================================================
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 42. 危急值记录表 (bgxt_CriticalValue) — 对应 BgxtCriticalValue 实体
-- ============================================================
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 43. TAT设定表 (bgxt_tsxmtat) — 对应 BgxtTsxmtat 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS bgxt_tsxmtat (
    sb_djid INT NOT NULL,
    brlb INT NOT NULL,
    syqk INT NOT NULL,
    zhid INT NOT NULL,
    zhmc VARCHAR(100),
    TAT INT,
    PRIMARY KEY (sb_djid, brlb, syqk, zhid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 44. HIS检验项目/计费表 (bgxt_his_xm) — 对应 BgxtHisXm 实体
-- ============================================================
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 45. 管座设置主表 (gzsz_zb) — 对应 GzszZb 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS gzsz_zb (
    dlid INT PRIMARY KEY AUTO_INCREMENT,
    dlmc VARCHAR(255) NOT NULL,
    isuse INT DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 46. 管座设置明细表 (gzsz_mx) — 对应 GzszMx 实体
-- ============================================================
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 47. 管座组合项目表 (gzsz_zhxm) — 对应 GzszZhxm 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS gzsz_zhxm (
    id INT PRIMARY KEY AUTO_INCREMENT,
    xlbh INT,
    zhid INT,
    zhxmmc VARCHAR(200),
    yxxh INT DEFAULT 0,
    INDEX idx_xlbh (xlbh)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 48. 子系统名称表 (sys_zxtmc) — 对应 SysZxtmc 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_zxtmc (
    zxtid INT PRIMARY KEY,
    zxtjc VARCHAR(50),
    zxtmc VARCHAR(100),
    zxtbb VARCHAR(20),
    bz VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO sys_zxtmc (zxtid, zxtjc, zxtmc) VALUES
(1, 'LIS', '检验信息系统'),
(2, 'QC', '质控系统'),
(3, 'SYS', '系统管理');

-- ============================================================
-- 49. 日志操作类型说明表 (sys_rzztsm) — 对应 SysRzztsm 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_rzztsm (
    zxtid INT NOT NULL,
    ztid INT NOT NULL,
    ztsm VARCHAR(255) NOT NULL,
    PRIMARY KEY (zxtid, ztid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO sys_rzztsm (zxtid, ztid, ztsm) VALUES
(1, 1, '样本录入'), (1, 2, '样本审核'), (1, 3, '报告打印'), (1, 4, '样本取消'),
(2, 1, '质控录入'), (2, 2, '质控评价'),
(3, 1, '用户登录'), (3, 2, '参数修改'), (3, 3, '系统锁定');

-- ============================================================
-- 50. 系统日志表 (sys_rz) — 对应 SysRz 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_rz (
    id INT PRIMARY KEY AUTO_INCREMENT,
    czrq DATETIME NOT NULL,
    czydm VARCHAR(50),
    sm TEXT,
    czip VARCHAR(50),
    czmk VARCHAR(100),
    ztid INT,
    zxtid INT,
    INDEX idx_czrq (czrq),
    INDEX idx_czydm (czydm),
    INDEX idx_zxtid_ztid (zxtid, ztid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 51. 报告模板配置表 (sys_report_template) — 对应 ReportTemplate 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_report_template (
    template_id INT AUTO_INCREMENT PRIMARY KEY,
    template_name VARCHAR(100) NOT NULL,
    template_type VARCHAR(50) NOT NULL,
    template_code VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    config JSON NOT NULL,
    mr INT DEFAULT 0,
    sycx INT DEFAULT 101,
    czyxm VARCHAR(50),
    gxrq DATETIME,
    status INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_template_code (template_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO sys_report_template (template_name, template_type, template_code, description, config, mr, sycx) VALUES
('标准单列报告', 'single_col', 'STD_SINGLE_COL', '标准单列报告模板，适用于大多数检验项目',
 '{"hospitalName": "医院名称", "reportTitle": "检验报告单", "showLogo": true, "pageSize": "A4"}', 1, 101),
('标准双列报告', 'double_col', 'STD_DOUBLE_COL', '标准双列报告模板，适用于项目较多的检验组合',
 '{"hospitalName": "医院名称", "reportTitle": "检验报告单", "showLogo": true, "pageSize": "A4"}', 0, 101);

-- ============================================================
-- 52. 报告版本表 (sys_bbsz) — 对应 ReportVersion 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_bbsz (
    bb_id INT PRIMARY KEY AUTO_INCREMENT,
    sycx INT,
    bbsm VARCHAR(200),
    czydm VARCHAR(50),
    czyxm VARCHAR(50),
    gxrq DATETIME,
    bb LONGBLOB,
    mr INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 53. 科室仪器分配表 (bgxt_ksyqsz) — 对应 BgxtKsyqsz 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS bgxt_ksyqsz (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ksdm VARCHAR(20) NOT NULL,
    sb_djid INT NOT NULL,
    gxrq DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_ksdm_sbdjid (ksdm, sb_djid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 54. 特殊标本设置表 (bgxt_tsbbsz) — 对应 BgxtTsbbsz/BgxtTybbsz 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS bgxt_tsbbsz (
    id INT PRIMARY KEY AUTO_INCREMENT,
    mkid INT NOT NULL,
    xmid INT,
    mksm VARCHAR(100),
    gxrq DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_mkid_xmid (mkid, xmid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO bgxt_tsbbsz (mkid, mksm) VALUES
(0, '溶血'), (1, '乳糜'), (2, '黄疸'), (3, '其他');

-- ============================================================
-- 55. 项目仪器关联表 (bgxt_xmssyq) — 对应 BgxtXmssyq 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS bgxt_xmssyq (
    id INT PRIMARY KEY AUTO_INCREMENT,
    xmid INT NOT NULL,
    sb_djid INT NOT NULL,
    zhid INT,
    gxrq DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_xmid_sbdjid (xmid, sb_djid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 56. 样本拒收表 (bgxt_sample_reject) — 对应 BgxtSampleReject 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS bgxt_sample_reject (
    id INT PRIMARY KEY AUTO_INCREMENT,
    brxx_id INT,
    test_barcode VARCHAR(100),
    patient_name VARCHAR(50),
    sex VARCHAR(10),
    age VARCHAR(20),
    patient_type INT,
    department VARCHAR(50),
    bed_number VARCHAR(20),
    sample_type VARCHAR(50),
    item_name VARCHAR(200),
    class_group VARCHAR(100),
    error_reason VARCHAR(500),
    group_name VARCHAR(100),
    handling_measures VARCHAR(500),
    handling_measures_other VARCHAR(500),
    recipient VARCHAR(50),
    notes VARCHAR(500),
    operator_code VARCHAR(50),
    operator_name VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 57. 开关控制表 (bgxt_kgkz) — 对应 BgxtKgkz 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS bgxt_kgkz (
    id INT PRIMARY KEY,
    kgmc VARCHAR(100),
    kgsm VARCHAR(200),
    kgz INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 58. 试管架颜色设置 (bgxt_sgyssz) — 对应 TubeColor 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS bgxt_sgyssz (
    sgys VARCHAR(50) PRIMARY KEY,
    pym VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 59. 通用编码主表 (sys_tybmzb) — 对应 SysTybmzb 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_tybmzb (
    id INT PRIMARY KEY AUTO_INCREMENT,
    bmmc VARCHAR(200),
    bmdm INT,
    bmbh VARCHAR(50),
    syccbm INT,
    syccmc VARCHAR(100),
    tybz TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 60. 通用编码明细表 (sys_tybmmx) — 对应 SysTybmmx 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_tybmmx (
    id INT PRIMARY KEY AUTO_INCREMENT,
    bmdm INT,
    bm INT,
    bmsm VARCHAR(200),
    szdm VARCHAR(50),
    pym VARCHAR(50),
    mrzbz TINYINT DEFAULT 0,
    tybz TINYINT DEFAULT 0,
    bz VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 61. 模块表 (sys_mkb) — 对应 SysMkb 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_mkb (
    mkdm INT PRIMARY KEY,
    zxtid INT,
    frm_name VARCHAR(100),
    frm_caption VARCHAR(200),
    mkfl VARCHAR(50),
    action_name VARCHAR(100),
    caption VARCHAR(200),
    bz TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 62. 权限大类表 (sys_qxdl) — 对应 SysQxdl 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_qxdl (
    id INT PRIMARY KEY AUTO_INCREMENT,
    zxtid INT,
    dldm VARCHAR(50),
    dlmc VARCHAR(100),
    kmjs INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 63. 权限小类表 (sys_qxxl) — 对应 SysQxxl 实体
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_qxxl (
    dldm VARCHAR(50) NOT NULL,
    xldm VARCHAR(50) NOT NULL,
    xlmc VARCHAR(100),
    bz TINYINT DEFAULT 0,
    PRIMARY KEY (dldm, xldm)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 64. 检验项目表 (sys_jyxm) — 对应 SysJyxm 实体
--     注意：此表为视图(sys_bbzl)的补充实体映射
--     如需独立表可取消下方注释
-- ============================================================
-- CREATE TABLE IF NOT EXISTS sys_jyxm (
--     xmid INT PRIMARY KEY,
--     xmdm VARCHAR(50),
--     xmzwmc VARCHAR(100),
--     xmywmc VARCHAR(100),
--     xmdw VARCHAR(20),
--     pym VARCHAR(50)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 完成！共 63 张表 + 2 个视图
-- ============================================================
