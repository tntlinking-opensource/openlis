import axios from 'axios'

export function queryCustomReport(data) {
  return axios.post('/api/custom-report/query', data)
}

export function fetchCustomReportDetail(params) {
  return axios.get('/api/custom-report/detail', { params })
}

export function fetchPatientTypeWithFee(params) {
  return axios.get('/api/custom-report/patient-type', { params })
}
