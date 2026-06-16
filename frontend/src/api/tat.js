import axios from 'axios'

export function fetchTatSettings() {
  return axios.get('/api/tat/settings')
}

export function saveTatSetting(data) {
  return axios.post('/api/tat/settings/save', data)
}

export function deleteTatSetting(sbDjid, brlb, syqk, zhid) {
  return axios.delete(`/api/tat/settings/${sbDjid}/${brlb}/${syqk}/${zhid}`)
}

export function autoCalculateTat() {
  return axios.get('/api/tat/settings/auto-calculate')
}

export function fetchTatStatistics(params) {
  return axios.get('/api/tat/statistics', { params })
}

export function fetchTatOvertime(params) {
  return axios.get('/api/tat/overtime', { params })
}

export function fetchTatTrend(params) {
  return axios.get('/api/tat/trend', { params })
}
