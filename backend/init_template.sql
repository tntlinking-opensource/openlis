-- 报告模板配置表
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

-- 插入默认模板
INSERT INTO sys_report_template (template_name, template_type, template_code, description, config, mr, sycx, czyxm) VALUES
('标准单列报告', 'single_col', 'STD_SINGLE_COL', '标准单列报告模板', '{"hospitalName":"医院名称","reportTitle":"检验报告单","showLogo":true,"pageSize":"A4"}', 1, 101, '系统'),
('标准双列报告', 'double_col', 'STD_DOUBLE_COL', '标准双列报告模板', '{"hospitalName":"医院名称","reportTitle":"检验报告单","showLogo":true,"pageSize":"A4"}', 0, 101, '系统'),
('标准图表报告', 'chart', 'STD_CHART', '图表报告模板', '{"hospitalName":"医院名称","reportTitle":"检验报告单","showLogo":true,"pageSize":"A4","showCharts":true}', 0, 101, '系统');