export const SAMPLE_STATUS_MAP = {
  0: '登记',
  1: '未审核',
  2: '已审核',
  3: '已打印',
  4: '已检验',
  5: '初审',
  6: '复审',
  [-1]: '已作废'
}

export function getStatusText(ybzt) {
  return SAMPLE_STATUS_MAP[ybzt] ?? '未知'
}

export function getStatusType(ybzt) {
  if (ybzt === -1) return 'danger'
  if (ybzt >= 3) return 'success'
  if (ybzt >= 2) return 'warning'
  if (ybzt >= 1) return 'info'
  return 'info'
}
