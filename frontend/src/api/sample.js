import axios from 'axios'

// 样本管理相关 API 封装（对应 SampleEntryController）

export function fetchPatients(params) {
  return axios.get('/api/sample/patients', { params })
}

export function saveSample(data) {
  return axios.post('/api/sample/save', data)
}

export function refreshPatients(params) {
  // 目前等同于 fetchPatients，预留给将来区分刷新行为
  return axios.get('/api/sample/patients', { params })
}

export function inspectSample(brxxId, czydm) {
  return axios.post(`/api/sample/inspect/${brxxId}`, null, { params: { czydm } })
}

export function auditSample(brxxId) {
  return axios.post(`/api/sample/audit/${brxxId}`)
}

export function unauditSample(brxxId) {
  return axios.post(`/api/sample/unaudit/${brxxId}`)
}

export function printSample(brxxId) {
  return axios.post(`/api/sample/print/${brxxId}`)
}

export function searchSamples(params) {
  return axios.get('/api/sample/search', { params })
}

export function getNextSampleNo(params) {
  return axios.get('/api/sample/nextSampleNo', { params })
}

export function fetchCombos(params) {
  return axios.get('/api/sample/combos', { params })
}

export function fetchComboItems(zhid, params) {
  return axios.get(`/api/sample/combos/${zhid}/items`, { params })
}

export function fetchResults(brxxId) {
  return axios.get(`/api/sample/results/${brxxId}`)
}

export function fetchReportHtml(brxxId) {
  return axios.get(`/api/sample/report/${brxxId}`, { responseType: 'text' })
}

export function batchAudit(brxxIds) {
  return axios.post('/api/sample/batch/audit', { brxxIds })
}

export function batchPrint(payload) {
  return axios.post('/api/sample/batch/print', payload)
}

export function batchInvalidate(brxxIds, reason) {
  return axios.post('/api/sample/batch/invalidate', { brxxIds, reason })
}

export function batchUnaudit(brxxIds) {
  return axios.post('/api/sample/batch/unaudit', { brxxIds })
}

export function getProgressStats(date) {
  return axios.get('/api/sample/stats/progress', { params: { date } })
}

export function getSampleIssues(date) {
  return axios.get('/api/sample/sample/issues', { params: { date } })
}

export function handleSampleIssue(data) {
  return axios.post('/api/sample/sample/handle', data)
}

export function getHistoryRecords(date) {
  return axios.get('/api/sample/history', { params: { date } })
}

export function getApplicationInfo(date) {
  return axios.get('/api/sample/application', { params: { date } })
}

export function getWarningInfo(date) {
  return axios.get('/api/sample/warning', { params: { date } })
}

export function clearWarnings() {
  return axios.post('/api/sample/warning/clear')
}

export function getErrorTypes() {
  return axios.get('/api/specimen/error-types')
}

export function getHandlingMeasures() {
  return axios.get('/api/specimen/handling-measures')
}

export function handleSpecimenError(brxxId, data) {
  return axios.post(`/api/specimen/${brxxId}/error-handle`, data)
}

export function reportIncorrect(brxxId, reason) {
  return axios.post(`/api/specimen/${brxxId}/report-incorrect`, { reason })
}

export function getRejectRecords(params) {
  return axios.get('/api/specimen/reject-records', { params })
}

export function getRejectInfo(brxxId) {
  return axios.get(`/api/specimen/${brxxId}/reject-info`)
}

// 仪器提取相关API
export function extractFromInstrument(data) {
  return axios.post('/api/instrument/extract', data)
}

export function getExtractStatus(sbDjid, extractDate, patientName) {
  return axios.get('/api/instrument/extract-status', { params: { sbDjid, extractDate, patientName } })
}

export function getExtractPreview(sbDjid, beginDate, patientName) {
  return axios.get('/api/instrument/extract-preview', { params: { sbDjid, beginDate, patientName } })
}

export function acceptSample(brxxId) {
  return axios.post(`/api/sample/accept/${brxxId}`)
}

export function rejectSample(brxxId, data) {
  return axios.post(`/api/sample/reject/${brxxId}`, data)
}

export function acceptSampleWithSign(brxxId, data) {
  return axios.post(`/api/sample/acceptWithSign/${brxxId}`, data)
}

export function updateSampleTime(brxxId, data) {
  return axios.put(`/api/sample/updateTime/${brxxId}`, data)
}

export function invalidateSample(brxxId, data) {
  return axios.post(`/api/sample/invalid/${brxxId}`, data)
}

export function transferSample(data) {
  return axios.post('/api/sample/transfer', data)
}

export function quickExtract(data) {
  return axios.post('/api/sample/quickExtract', data)
}

export function convertToQC(data) {
  return axios.post('/api/sample/convertToQC', data)
}

export function fetchRawData(brxxId) {
  return axios.get(`/api/sample/rawData/${brxxId}`)
}

export function fetchDropdownOptions() {
  return axios.get('/api/sample/dropdown-options')
}

export function mergePrint(data) {
  return axios.post('/api/report/mergePrint', data)
}


