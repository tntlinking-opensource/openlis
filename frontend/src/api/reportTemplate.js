import axios from 'axios'

export function fetchReportTemplates(keyword) {
  return axios.get('/api/report-template/list', { params: { keyword } })
}

export function getReportTemplate(id) {
  return axios.get(`/api/report-template/${id}`)
}

export function getReportTemplateHtml(id) {
  return axios.get(`/api/report-template/${id}/html`)
}

export function saveReportTemplate(data) {
  return axios.post('/api/report-template/save', data)
}

export function saveReportTemplateHtml(id, html) {
  return axios.post(`/api/report-template/${id}/html`, { html })
}

export function setDefaultReportTemplate(id) {
  return axios.post(`/api/report-template/set-default/${id}`)
}

export function deleteReportTemplate(id) {
  return axios.delete(`/api/report-template/${id}`)
}

export function renderReport(data) {
  return axios.post('/api/report/render', data)
}
