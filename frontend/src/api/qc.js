import axios from 'axios'

// 质控品管理
export function fetchQcProducts(params) {
  return axios.get('/api/qc/products', { params })
}

export function getQcProduct(zkpid) {
  return axios.get(`/api/qc/products/${zkpid}`)
}

export function addQcProduct(data) {
  return axios.post('/api/qc/products', data)
}

export function updateQcProduct(zkpid, data) {
  return axios.put(`/api/qc/products/${zkpid}`, data)
}

export function deleteQcProduct(zkpid) {
  return axios.delete(`/api/qc/products/${zkpid}`)
}

// 日常质控结果
export function fetchDailyResults(params) {
  return axios.get('/api/qc/daily-results', { params })
}

export function addDailyResult(data) {
  return axios.post('/api/qc/daily-results', data)
}

export function deleteDailyResult(id) {
  return axios.delete(`/api/qc/daily-results/${id}`)
}

// 质控评价
export function fetchEvaluations(params) {
  return axios.get('/api/qc/evaluations', { params })
}

export function addEvaluation(data) {
  return axios.post('/api/qc/evaluations', data)
}

export function deleteEvaluation(id) {
  return axios.delete(`/api/qc/evaluations/${id}`)
}

// 质控项目管理
export function fetchQcProjects(zkpid) {
  return axios.get('/api/qc/projects', { params: { zkpid } })
}

export function updateQcProject(zkxmid, data) {
  return axios.put(`/api/qc/projects/${zkxmid}`, data)
}

export function addQcProject(data) {
  return axios.post('/api/qc/projects', data)
}

export function deleteQcProject(id) {
  return axios.delete(`/api/qc/projects/${id}`)
}

// 可用的检验项目（未绑定到该质控品的）
export function fetchAvailableProjects(zkpid, sbDjid) {
  return axios.get('/api/qc/available-projects', { params: { zkpid, sbDjid } })
}

// 质控分析（带失控判断）
export function fetchQcAnalysis(params) {
  return axios.get('/api/qc/analysis', { params })
}

// 所有检验项目
export function fetchAllProjects() {
  return axios.get('/api/qc/all-projects')
}
