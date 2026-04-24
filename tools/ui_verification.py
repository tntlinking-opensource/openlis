"""
UI还原度智能验证工具
通过分析手册HTML和Vue组件代码，自动检查UI还原度
"""
import re
import json
from pathlib import Path

def extract_ui_elements_from_manual(html_path):
    """从手册HTML中提取UI元素描述"""
    html_content = Path(html_path).read_text(encoding='utf-8')
    
    # 提取6.1-6.5相关段落
    sections = {
        '6.1': {'id': 'p191', 'title': '工程师系统设置'},
        '6.2': {'id': 'p194', 'title': '系统锁定'},
        '6.3': {'id': 'p197', 'title': '刷新权限'},
        '6.4': {'id': 'p200', 'title': '通用编码设置'},
        '6.5': {'id': 'p203', 'title': '特殊报告设置'}
    }
    
    ui_elements = {}
    
    for section_id, section_info in sections.items():
        # 提取该章节的内容
        pattern = rf'id="{section_info["id"]}".*?id="p\d+"'
        match = re.search(pattern, html_content, re.DOTALL)
        if match:
            content = match.group(0)
            
            # 提取文本描述
            text_pattern = r'<div class="p"[^>]*>(.*?)</div>'
            texts = re.findall(text_pattern, content)
            
            # 提取可能的UI元素关键词
            keywords = []
            for text in texts:
                # 提取按钮、字段、操作等关键词
                if '按钮' in text or '输入' in text or '选择' in text or '保存' in text or '删除' in text:
                    keywords.append(text.strip())
            
            ui_elements[section_id] = {
                'title': section_info['title'],
                'descriptions': texts[:5],  # 前5个描述
                'keywords': keywords
            }
    
    return ui_elements

def extract_vue_component_elements(vue_file_path):
    """从Vue组件中提取UI元素"""
    vue_content = Path(vue_file_path).read_text(encoding='utf-8')
    
    elements = {
        'buttons': [],
        'form_fields': [],
        'tables': [],
        'dialogs': []
    }
    
    # 提取按钮
    button_pattern = r'<el-button[^>]*>(.*?)</el-button>'
    buttons = re.findall(button_pattern, vue_content, re.DOTALL)
    elements['buttons'] = [b.strip() for b in buttons if b.strip()]
    
    # 提取表单字段
    form_item_pattern = r'<el-form-item[^>]*label="([^"]*)"[^>]*>'
    form_items = re.findall(form_item_pattern, vue_content)
    elements['form_fields'] = form_items
    
    # 提取表格
    table_pattern = r'<el-table[^>]*>'
    tables = re.findall(table_pattern, vue_content)
    elements['tables'] = len(tables)
    
    # 提取对话框
    dialog_pattern = r'<el-dialog[^>]*title="([^"]*)"[^>]*>'
    dialogs = re.findall(dialog_pattern, vue_content)
    elements['dialogs'] = dialogs
    
    return elements

def compare_ui_elements(manual_elements, vue_elements, section_id):
    """对比手册描述和Vue组件"""
    issues = []
    suggestions = []
    
    manual = manual_elements.get(section_id, {})
    keywords = ' '.join(manual.get('keywords', []))
    
    # 检查关键操作是否在Vue组件中
    if '保存' in keywords and '保存' not in ' '.join(vue_elements['buttons']):
        issues.append(f"缺少'保存'按钮")
    
    if '删除' in keywords and '删除' not in ' '.join(vue_elements['buttons']):
        issues.append(f"缺少'删除'按钮")
    
    if '新增' in keywords or '增加' in keywords:
        add_buttons = [b for b in vue_elements['buttons'] if '新增' in b or '增加' in b or '新增' in b]
        if not add_buttons:
            issues.append(f"缺少'新增/增加'按钮")
    
    # 检查表单字段
    if vue_elements['form_fields']:
        suggestions.append(f"表单字段: {', '.join(vue_elements['form_fields'][:5])}")
    
    return {
        'issues': issues,
        'suggestions': suggestions,
        'buttons_found': vue_elements['buttons'],
        'form_fields_found': vue_elements['form_fields']
    }

def main():
    """主函数"""
    manual_path = Path('D:/LIS02/LIS_SEED_BAG/种子二：_manual_export/manual_export.html')
    vue_files = {
        '6.1': Path('D:/LIS02/LIS_OPEN_JAVA/frontend/src/views/SystemSetting.vue'),
        '6.2': Path('D:/LIS02/LIS_OPEN_JAVA/frontend/src/views/SystemLock.vue'),
        '6.3': Path('D:/LIS02/LIS_OPEN_JAVA/frontend/src/views/RefreshPermission.vue'),
        '6.4': Path('D:/LIS02/LIS_OPEN_JAVA/frontend/src/views/CommonCodeSetting.vue'),
        '6.5': Path('D:/LIS02/LIS_OPEN_JAVA/frontend/src/views/SpecialReportSetting.vue')
    }
    
    print("=" * 60)
    print("UI还原度智能验证报告")
    print("=" * 60)
    
    # 提取手册UI元素
    manual_elements = extract_ui_elements_from_manual(manual_path)
    
    results = {}
    
    for section_id, vue_file in vue_files.items():
        if not vue_file.exists():
            print(f"\n[WARN] {section_id}: Vue文件不存在 - {vue_file}")
            continue
        
        print(f"\n[CHECK] {section_id}: {manual_elements.get(section_id, {}).get('title', 'Unknown')}")
        print("-" * 60)
        
        # 提取Vue组件元素
        vue_elements = extract_vue_component_elements(vue_file)
        
        # 对比
        comparison = compare_ui_elements(manual_elements, vue_elements, section_id)
        
        results[section_id] = comparison
        
        # 输出结果
        if comparison['issues']:
            print("[ISSUES] 发现问题:")
            for issue in comparison['issues']:
                print(f"   - {issue}")
        else:
            print("[OK] 未发现明显问题")
        
        if comparison['suggestions']:
            print("[SUGGESTIONS] 建议:")
            for suggestion in comparison['suggestions']:
                print(f"   - {suggestion}")
        
        print(f"[INFO] 按钮: {len(comparison['buttons_found'])} 个")
        print(f"[INFO] 表单字段: {len(comparison['form_fields_found'])} 个")
    
    # 生成报告
    report_path = Path('D:/LIS02/LIS_OPEN_JAVA/UI_VERIFICATION_REPORT.md')
    with open(report_path, 'w', encoding='utf-8') as f:
        f.write("# UI还原度智能验证报告\n\n")
        f.write("## 检查结果\n\n")
        for section_id, result in results.items():
            f.write(f"### {section_id}\n\n")
            if result['issues']:
                f.write("**问题:**\n")
                for issue in result['issues']:
                    f.write(f"- {issue}\n")
                f.write("\n")
            f.write(f"**按钮数量:** {len(result['buttons_found'])}\n")
            f.write(f"**表单字段数量:** {len(result['form_fields_found'])}\n\n")
    
    print(f"\n[OK] 报告已生成: {report_path}")

if __name__ == '__main__':
    main()

