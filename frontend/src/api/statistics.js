import axios from 'axios'

export function fetchComprehensiveStat(data) {
  return axios.post('/api/statistics/comprehensive', data)
}

export function fetchStatByDepartment(params) {
  return axios.get('/api/statistics/by-department', { params })
}

export function fetchStatByDoctor(params) {
  return axios.get('/api/statistics/by-doctor', { params })
}

export function fetchStatByItem(params) {
  return axios.get('/api/statistics/by-item', { params })
}

export function fetchStatByStatus(params) {
  return axios.get('/api/statistics/by-status', { params })
}

export function fetchWorkloadByItem(data) {
  return axios.post('/api/statistics/workload-by-item', data)
}

export function fetchWorkloadDetail(params) {
  return axios.get('/api/statistics/workload-detail', { params })
}

export function fetchWorkloadByItemV2(params) {
  return axios.get('/api/statistics/workload-by-item-v2', { params })
}

export function fetchWorkloadItemDetailV2(params) {
  return axios.get('/api/statistics/workload-item-detail-v2', { params })
}

export function fetchWorkloadByDept(params) {
  return axios.get('/api/statistics/workload-by-dept', { params })
}

export function fetchWorkloadDeptDetail(params) {
  return axios.get('/api/statistics/workload-dept-detail', { params })
}

export function fetchWorkloadByDoctor(params) {
  return axios.get('/api/statistics/workload-by-doctor', { params })
}

export function fetchWorkloadDoctorDetail(params) {
  return axios.get('/api/statistics/workload-doctor-detail', { params })
}

export function fetchWorkloadByExaminer(params) {
  return axios.get('/api/statistics/workload-by-examiner', { params })
}

export function fetchWorkloadExaminerDetail(params) {
  return axios.get('/api/statistics/workload-examiner-detail', { params })
}
