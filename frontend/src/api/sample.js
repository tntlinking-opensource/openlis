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

export function inspectSample(brxxId) {
  return axios.post(`/api/sample/inspect/${brxxId}`)
}

export function auditSample(brxxId) {
  return axios.post(`/api/sample/audit/${brxxId}`)
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

export function batchPrint(brxxIds) {
  return axios.post('/api/sample/batch/print', { brxxIds })
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




