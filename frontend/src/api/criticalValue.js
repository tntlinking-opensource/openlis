import axios from 'axios'

export function fetchCriticalValues(params) {
  return axios.get('/api/critical-value/list', { params })
}

export function addCriticalValue(data) {
  return axios.post('/api/critical-value/add', data)
}

export function deleteCriticalValue(id, data) {
  return axios.delete(`/api/critical-value/${id}`, { data })
}

export function fetchCriticalValueStat(params) {
  return axios.get('/api/critical-value/statistics', { params })
}

export function processCriticalValues(data) {
  return axios.post('/api/critical-value/process', data)
}

export function fetchPatientPreview(params) {
  return axios.get('/api/critical-value/patient-preview', { params })
}
