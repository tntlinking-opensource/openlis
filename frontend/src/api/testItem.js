import axios from 'axios'

export function fetchTestItems(params) {
  return axios.get('/api/test-item/list', { params })
}

export function searchTestItems(pym) {
  return axios.get('/api/test-item/search', { params: { pym } })
}

export function saveTestItem(data) {
  return axios.post('/api/test-item/save', data)
}

export function deleteTestItem(xmid) {
  return axios.delete(`/api/test-item/${xmid}`)
}

export function fetchTestItemTypes() {
  return axios.get('/api/test-item/types')
}

export function fetchPrecisions() {
  return axios.get('/api/test-item/precisions')
}
