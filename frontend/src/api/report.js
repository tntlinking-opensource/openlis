import axios from 'axios'

export function batchPrintQuery(data) {
  return axios.post('/api/batch-print/query', data)
}

export function batchPrintExecute(data) {
  return axios.post('/api/batch-print/execute', data)
}

export function fetchPrintDepartments() {
  return axios.get('/api/batch-print/departments')
}

export function queryReports(data) {
  return axios.post('/api/report/query', data)
}

export function fetchReportResults(id) {
  return axios.get(`/api/report/${id}/results`)
}

export function printReport(id, data) {
  return axios.post(`/api/report/${id}/print`, data)
}

export function batchPrintReport(data) {
  return axios.post('/api/report/batch/print', data)
}

export function fetchReportFilterOptions() {
  return axios.get('/api/report/filter-options')
}

export function listTemplates(params) {
  return axios.get('/api/report-template/list', { params })
}
