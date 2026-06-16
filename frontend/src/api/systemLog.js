import axios from 'axios'

export function fetchLogSystems() {
  return axios.get('/api/log/systems')
}

export function fetchLogOperationTypes(systemId) {
  return axios.get(`/api/log/operation-types/${systemId}`)
}

export function fetchAllOperationTypes() {
  return axios.get('/api/log/operation-types')
}

export function querySystemLogs(params) {
  return axios.get('/api/log/query', { params })
}

export function searchOperators(params) {
  return axios.get('/api/log/operators', { params })
}
