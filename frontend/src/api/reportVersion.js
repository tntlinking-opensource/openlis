import axios from 'axios'

export function fetchReportVersions(keyword) {
  return axios.get('/api/report-version/list', { params: { keyword } })
}

export function getReportVersion(id) {
  return axios.get(`/api/report-version/${id}`)
}

export function saveReportVersion(data) {
  return axios.post('/api/report-version/save', data)
}

export function setDefaultReport(id) {
  return axios.post(`/api/report-version/set-default/${id}`)
}

export function deleteReportVersion(id) {
  return axios.delete(`/api/report-version/${id}`)
}

export function getMrtTemplate(id) {
  return axios.get(`/api/report-version/${id}/mrt`)
}

export function saveMrtTemplate(id, mrt) {
  return axios.post(`/api/report-version/${id}/mrt`, { mrt })
}